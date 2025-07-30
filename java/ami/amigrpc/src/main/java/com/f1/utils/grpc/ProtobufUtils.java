package com.f1.utils.grpc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.relay.fh.AmiFHBase;
import com.f1.ami.relay.fh.AmiRelayMapToBytesConverter;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.json.JsonBuilder;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.MapEntry;
import com.google.protobuf.Message.Builder;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

public class ProtobufUtils {

	private static final Logger log = LH.get();

	public static enum StubType {
									BLOCKING,
									NORMAL,
									FUTURE
	}

	private static Set<String> PROTOBUF_DEFAULT_FUNCTIONS = CH.s("build", "newStub", "getChannel", "withExecutor", "getCallOptions", "withCallCredentials",
			"withMaxInboundMessageSize", "withMaxOutboundMessageSize", "withWaitForReady", "withDeadline", "withInterceptors", "withCompression", "withChannel",
			"withOnReadyThreshold", "withDeadlineAfter", "withOption", "finalize", "wait", "equals", "toString", "hashCode", "getClass", "clone", "registerNatives", "notify",
			"notifyAll");

	static class GenericInterceptor implements ClientInterceptor {

		private final Metadata metadata;

		public GenericInterceptor(Metadata metadata) {
			this.metadata = metadata;
		}

		@Override
		public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
			return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
				@Override
				public void start(Listener<RespT> responseListener, Metadata headers) {
					headers.merge(metadata);
					super.start(responseListener, headers);
				}
			};
		}
	}

	//The stub class defines the runtime loaded service layer to invoke protobuf methods as a client
	public static class ProtobufStub {
		private final StubType type;
		private Object stub;

		public ProtobufStub(final String className, final ManagedChannel channel, StubType stubType, Map<String, String> metadata) {
			Class<?> c = RH.getClass(className);

			String methodName = "";
			switch (stubType) {
				case NORMAL:
					methodName = "newStub";
					break;
				case FUTURE:
					methodName = "newFutureStub";
					break;
				case BLOCKING:
					methodName = "newBlockingStub";
					break;

			}
			;
			this.type = stubType;
			Method m = RH.findMethod(c, methodName, new Class[] { ManagedChannel.class });

			try {
				this.stub = m.invoke(null, channel);
			} catch (Exception e) {
				throw new RuntimeException("Failed to create stub: " + e);
			}

			if (stub == null)
				throw new RuntimeException("Failed to retrieve protobuf stub, method name: " + methodName + ", class: " + className);

			//Attach interceptor to insert metadata info
			if (metadata != null) {
				Metadata meta = new Metadata();
				for (final Entry<String, String> es : metadata.entrySet()) {
					meta.put(Metadata.Key.of(es.getKey(), Metadata.ASCII_STRING_MARSHALLER), es.getValue());
				}
				GenericInterceptor interceptor = new GenericInterceptor(meta);
				Method withInterceptors = RH.findMethod(this.stub.getClass(), "withInterceptors", new Class[] { ClientInterceptor.class });
				try {
					this.stub = withInterceptors.invoke(this.stub, (Object) (new ClientInterceptor[] { interceptor }));
				} catch (Exception e) {
					throw new RuntimeException("Failed to add interceptor: " + e);
				}
			}

		}

		public ProtobufStub(final String className, final ManagedChannel channel, StubType stubType) {
			this(className, channel, stubType, null);
		}

		public StubType getStubType() {
			return this.type;
		}

		public List<Method> getMethods() {
			List<Method> methods = CH.l();
			for (final Method m : RH.getMethods(this.stub.getClass())) {
				if (!PROTOBUF_DEFAULT_FUNCTIONS.contains(m.getName()))
					methods.add(m);
			}
			return methods;
		}

		public Object invokeMethod(String methodName, Object... params) {
			return RH.invokeMethod(this.stub, methodName, params);
		}
	}

	//Used to recursively store and fetch primary keys while iterating through protobuf descriptors
	public static class ProtobufPrimaryKey {

		private String originalKey;
		private String primaryKey;
		private String parsedKey = null;
		private String current = "";
		private boolean completed = false;
		private boolean skip = false;
		private int listCount = 0;

		public void setShouldSkip(boolean skip) {
			this.skip = skip;
		}
		public boolean shouldParse() {
			return !skip && !completed;
		}
		public boolean atRoot() {
			return !primaryKey.contains(".") && this.listCount == 0;
		}
		public ProtobufPrimaryKey(String primaryKey) {
			if (SH.isnt(primaryKey))
				this.completed = true;
			else {
				this.originalKey = primaryKey;
				reset();
			}
		}

		public void reset() {
			this.primaryKey = this.originalKey;
			this.current = SH.beforeFirst(primaryKey, '.');
			this.parsedKey = null;
			this.completed = false;
			this.listCount = 0;
		}

		public String getParsedKey() {
			return this.parsedKey;
		}

		private void getNext() {
			if (SH.indexOf(primaryKey, '.', 0) == -1) {
				this.primaryKey = "";
				this.current = "";
				this.completed = true;
			} else {
				this.primaryKey = SH.afterFirst(this.primaryKey, '.');
				String key = SH.beforeFirst(this.primaryKey, '.');
				this.current = key;
				try {
					this.listCount = SH.parseInt(key);
				} catch (Exception e) {
					this.listCount = 0;
				}
			}
		}

		public boolean checkKeyMatches(final String key, final Object o) {
			if (SH.equals(key, this.current)) {
				this.listCount = 0;
				if (atRoot()) {
					this.parsedKey = SH.toString(o);
					this.completed = true;
				} else {
					getNext();
				}
				return true;
			}
			return false;
		}

		public boolean parseList() {
			this.listCount--;
			if (this.listCount <= 0) {
				if (!atRoot()) {
					getNext();
				}
				return true;
			}
			return false;
		}

		public void setKey(Object o) {
			this.parsedKey = SH.toString(o);
		}
	}

	public static final ProtobufPrimaryKey NO_PRIMARY_KEY = new ProtobufPrimaryKey("");

	//Contains reflection data for a protobuf object and also methods to reconstruct or read from an object
	public static class ProtobufObjectTemplate {
		public final Class<?> objectClass;
		public final Descriptor descriptor;
		private final Method newBuilderMethod;
		private final Method getField;

		public ProtobufObjectTemplate(final String className) {
			this.objectClass = RH.getClass(className);
			try {
				final Method m = RH.findMethod(this.objectClass, "getDescriptor", null);
				this.descriptor = (Descriptor) m.invoke(null);
			} catch (Exception e) {
				throw new RuntimeException("Failed to get descriptor for class : " + this.objectClass.getName());
			}
			this.newBuilderMethod = RH.findMethod(this.objectClass, "newBuilder", null);
			this.getField = RH.findMethod(this.objectClass, "getField", new Class[] { FieldDescriptor.class });
		}

		public ProtobufObjectTemplate(final Object object) {
			this(object.getClass().getName());
		}

		private static String getFullyQualifiedClassName(final FieldDescriptor fd, final Class<?> parentClass) {
			String parentClassName = parentClass.getName();
			String typeName = SH.afterFirst(SH.strip(fd.toProto().getTypeName(), ".", "", false), ".");
			int dollarIndex = SH.indexOfLast(parentClassName, parentClassName.length(), '$');
			if (dollarIndex != -1) {
				String innerClass = SH.substring(parentClassName, 0, dollarIndex) + '$' + typeName;
				if (RH.getClassNoThrow(innerClass) != null)
					return innerClass;
				innerClass = SH.substring(parentClassName, 0, dollarIndex) + '.' + typeName;
				if (RH.getClassNoThrow(innerClass) != null)
					return innerClass;
			} else {
				int dotIndex = SH.indexOfLast(parentClassName, parentClassName.length(), '.');
				String innerClass = SH.substring(parentClassName, 0, dotIndex) + '$' + typeName;
				if (RH.getClassNoThrow(innerClass) != null)
					return innerClass;
				innerClass = SH.substring(parentClassName, 0, dotIndex) + '.' + typeName;
				if (RH.getClassNoThrow(innerClass) != null)
					return innerClass;
			}
			return getFullyQualifiedClassName(fd);
		}

		private static String getFullyQualifiedClassName(final FieldDescriptor fd) {
			final String javaPackage = fd.getFile().getOptions().getJavaPackage();
			if (SH.isnt(javaPackage)) {
				String fullName = fd.getFullName();
				if (RH.getClassNoThrow(fullName) != null) {
					return fullName;
				}
				int index = SH.indexOfLast(fullName, fullName.length(), '.');
				if (index != -1) {
					String innerClass = SH.substring(fullName, 0, index) + '$' + SH.substring(fullName, index + 1, fullName.length());
					if (RH.getClassNoThrow(innerClass) != null)
						return innerClass;
					String typeName = SH.afterLast(fd.toProto().getTypeName(), ".");
					String newClass = SH.substring(fullName, 0, index) + "." + typeName;
					if (RH.getClassNoThrow(newClass) != null) {
						return newClass;
					}
					newClass = SH.substring(fullName, 0, index) + "$" + typeName;
					if (RH.getClassNoThrow(newClass) != null) {
						return newClass;
					}

				}
				return fd.getFullName();
			} else {
				final String className = javaPackage + "." + fd.getMessageType().getName();
				if (RH.getClassNoThrow(className) == null)
					return javaPackage + "$" + fd.getMessageType().getName();
				return className;
			}
		}

		public Object construct(Map<String, Object> fields) {
			try {
				Builder builder = (com.google.protobuf.Message.Builder) newBuilderMethod.invoke(null);
				for (final Entry<String, Object> e : fields.entrySet()) {
					final FieldDescriptor fd = this.descriptor.findFieldByName(e.getKey());
					builder.setField(fd, e.getValue());
				}
				return builder.build();
			} catch (Exception e) {
				log.warning("Failed to construct object: " + this.objectClass.getName() + " with params: " + fields.toString() + ", exception: " + e.toString());
			}
			return null;
		}

		public Object construct(List<Object> values) {
			try {
				Builder builder = (Builder) newBuilderMethod.invoke(null);

				for (int i = 0; i < values.size(); ++i) {
					final FieldDescriptor fd = this.descriptor.getFields().get(i);
					if (fd.getType() == FieldDescriptor.Type.ENUM) {
						final Object o = values.get(i);
						EnumValueDescriptor enumValue = null;
						if (o instanceof String)
							enumValue = fd.getEnumType().findValueByName(SH.toString(o));
						else
							enumValue = fd.getEnumType().findValueByNumber((int) o);
						builder.setField(fd, enumValue);
					} else {
						builder.setField(fd, values.get(i));
					}
				}
				return builder.build();
			} catch (Exception e) {
				final String err = "Failed to construct object: " + this.objectClass.getName() + " with params: " + values.toString() + ", exception: " + e.toString();
				log.warning(err);
				throw new RuntimeException(err);
			}
		}

		private void handleJsonObject(final Object o, final ProtobufObjectTemplate innerTemplate, final JsonBuilder builder, final Map<String, ProtobufObjectTemplate> templates,
				final ProtobufPrimaryKey primaryKey) {
			if (o instanceof MapEntry) {
				MapEntry<?, ?> entry = (MapEntry<?, ?>) o;
				builder.startMap();
				if (primaryKey.shouldParse())
					primaryKey.setShouldSkip(primaryKey.checkKeyMatches(SH.toString(entry.getKey()), entry.getValue()));
				builder.addKeyValueAutotype(entry.getKey(), entry.getValue(), false);
				builder.endMap();
			} else if (innerTemplate != null) {
				innerTemplate.toJson(o, builder, templates, primaryKey);
			} else {
				if (primaryKey.shouldParse() && primaryKey.atRoot()) {
					primaryKey.setKey(o);
				}
				builder.addEntryAutotype(o, false);
			}
		}

		@SuppressWarnings("unchecked")
		private void toInnerList(final List<Object> l, final JsonBuilder builder, final Map<String, ProtobufObjectTemplate> templates, final ProtobufPrimaryKey primaryKey) {
			boolean shouldParse = primaryKey.shouldParse();
			builder.startList();
			ProtobufObjectTemplate innerTemplate = null;
			Boolean isTemplated = true;
			for (final Object o : l) {
				if (shouldParse)
					primaryKey.setShouldSkip(!primaryKey.parseList());
				if (o instanceof List) {
					toInnerList((List<Object>) o, builder, templates, primaryKey);
				} else if (o instanceof Map) {
					toInnerMap((Map<String, Object>) o, builder, templates, primaryKey);
				} else {
					if (isTemplated && innerTemplate == null) {
						try {
							innerTemplate = new ProtobufObjectTemplate(o);
						} catch (Exception e) {
						}
						if (innerTemplate == null)
							isTemplated = false;
					}

					handleJsonObject(o, innerTemplate, builder, templates, primaryKey);
				}

				primaryKey.setShouldSkip(!shouldParse);
			}
			builder.endList();
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private void toInnerMap(final List<MapEntry<String, Object>> m, final JsonBuilder builder, final Map<String, ProtobufObjectTemplate> templates,
				final ProtobufPrimaryKey primaryKey) {
			final boolean shouldParse = primaryKey.shouldParse();
			builder.startMap();
			for (final MapEntry<String, Object> es : m) {
				final Object o = es.getValue();
				if (shouldParse)
					primaryKey.setShouldSkip(!primaryKey.checkKeyMatches(es.getKey(), o));
				if (o instanceof List) {
					builder.addKey(es.getKey());
					toInnerList((List) o, builder, templates, primaryKey);
				} else if (o instanceof Map) {
					builder.addKey(es.getKey());
					toInnerMap((Map<String, Object>) o, builder, templates, primaryKey);
				} else {
					ProtobufObjectTemplate innerTemplate = null;
					try {
						innerTemplate = new ProtobufObjectTemplate(o);
					} catch (Exception e) {
					}
					if (innerTemplate == null)
						builder.addKeyValueAutotype(es.getKey(), o, false);
					else
						handleJsonObject(o, innerTemplate, builder, templates, primaryKey);

				}
			}
			primaryKey.setShouldSkip(!shouldParse);
			builder.endMap();
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private void toInnerMap(final Map<String, Object> m, final JsonBuilder builder, final Map<String, ProtobufObjectTemplate> templates, final ProtobufPrimaryKey primaryKey) {
			builder.startMap();
			final boolean shouldParse = primaryKey.shouldParse();
			for (final Entry<String, Object> es : m.entrySet()) {
				final Object o = es.getValue();
				if (shouldParse)
					primaryKey.setShouldSkip(!primaryKey.checkKeyMatches(es.getKey(), o));
				if (o instanceof List) {
					builder.addKey(es.getKey());
					toInnerList((List) o, builder, templates, primaryKey);
				} else if (o instanceof Map) {
					builder.addKey(es.getKey());
					toInnerMap((Map<String, Object>) o, builder, templates, primaryKey);
				} else {
					ProtobufObjectTemplate innerTemplate = null;
					try {
						innerTemplate = new ProtobufObjectTemplate(o);
					} catch (Exception e) {
					}
					handleJsonObject(o, innerTemplate, builder, templates, primaryKey);

				}
				primaryKey.setShouldSkip(!shouldParse);
			}
			builder.endMap();
		}

		@SuppressWarnings("unchecked")
		private void toJson(final Object o, final JsonBuilder builder, final Map<String, ProtobufObjectTemplate> templates, final ProtobufPrimaryKey primaryKey) {
			builder.startMap();
			try {
				final boolean shouldParse = primaryKey.shouldParse();
				for (final FieldDescriptor f : this.descriptor.getFields()) {
					Object val = this.getField.invoke(o, f);
					if (primaryKey.shouldParse())
						primaryKey.setShouldSkip(!primaryKey.checkKeyMatches(f.getName(), val));
					if (val instanceof List) {
						builder.addKey(f.getName());
						toInnerList((List<Object>) val, builder, templates, primaryKey);
					} else if (val instanceof Map) {
						builder.addKey(f.getName());
						toInnerMap((Map<String, Object>) val, builder, templates, primaryKey);
					} else {
						ProtobufObjectTemplate innerTemplate = null;
						try {
							innerTemplate = getOrSetTemplate(val, templates);
						} catch (Exception e) {
						}

						if (innerTemplate != null)
							innerTemplate.toJson(val, builder, templates, primaryKey);
						else
							builder.addKeyValueAutotype(f.getName(), val, false);
					}
				}
				primaryKey.setShouldSkip(!shouldParse);

			} catch (Exception e) {
				log.warning("Failed to parse object: " + e);
			}
			builder.endMap();
		}

		@SuppressWarnings("unchecked")
		private void fillConverterInner(final AmiRelayMapToBytesConverter converter, Object o, final Map<String, ProtobufObjectTemplate> templates,
				final ProtobufPrimaryKey primaryKey) {
			try {
				final boolean shouldParse = primaryKey.shouldParse();
				for (final FieldDescriptor f : this.descriptor.getFields()) {
					boolean isMapField = f.isMapField();
					boolean repeats = f.isRepeated();

					Object val = getField.invoke(o, f);
					if (shouldParse)
						primaryKey.setShouldSkip(!primaryKey.checkKeyMatches(f.getName(), val));

					if (isMapField) {
						final List<MapEntry<String, Object>> m = (List<MapEntry<String, Object>>) val;
						JsonBuilder builder = new JsonBuilder();
						this.toInnerMap(m, builder, templates, primaryKey);
						converter.append(f.getName(), builder.toString());
					} else if (repeats) {
						List<Object> l = (List<Object>) val;
						JsonBuilder builder = new JsonBuilder();
						this.toInnerList(l, builder, templates, primaryKey);
						converter.append(f.getName(), builder.toString());

					} else if (JavaType.MESSAGE.equals(f.getJavaType())) {
						final String fullClassName = getFullyQualifiedClassName(f, this.objectClass);
						if (RH.getClassNoThrow(fullClassName) == null) {
							throw new RuntimeException("Failed to get protobuf template for field descriptor: " + f.getFullName());
						} else {
							ProtobufObjectTemplate inner = getOrSetTemplate(fullClassName, templates);
							inner.fillConverterInner(converter, o, templates, primaryKey);
						}
					} else {
						converter.append(f.getName(), val);
					}
					primaryKey.setShouldSkip(!shouldParse);
				}

			} catch (

			Exception e) {
				log.severe("Failed to fill row with exception: " + e.toString());
			}
		}

		@SuppressWarnings("unchecked")
		private void fillRowInner(final Table t, Row r, Object o, final Map<String, ProtobufObjectTemplate> templates, final ProtobufPrimaryKey primaryKey) {
			try {
				if (r == null)
					r = t.newEmptyRow();

				final boolean shouldParse = primaryKey.shouldParse();
				for (final FieldDescriptor f : this.descriptor.getFields()) {
					boolean isMapField = f.isMapField();
					boolean repeats = f.isRepeated();

					Object val = getField.invoke(o, f);
					if (shouldParse)
						primaryKey.setShouldSkip(!primaryKey.checkKeyMatches(f.getName(), val));

					if (isMapField) {
						final List<MapEntry<String, Object>> m = (List<MapEntry<String, Object>>) val;
						JsonBuilder builder = new JsonBuilder();
						toInnerMap(m, builder, templates, primaryKey);
						r.put(f.getName(), builder.toString());
					} else if (repeats) {
						List<Object> l = (List<Object>) val;
						JsonBuilder builder = new JsonBuilder();
						this.toInnerList(l, builder, templates, primaryKey);
						r.put(f.getName(), builder.toString());

					} else if (JavaType.MESSAGE.equals(f.getJavaType())) {

						final String fullClassName = getFullyQualifiedClassName(f, this.objectClass);
						if (RH.getClassNoThrow(fullClassName) == null) {
							throw new RuntimeException("Failed to get protobuf template for field descriptor: " + f.getFullName());
						} else {

							ProtobufObjectTemplate inner = getOrSetTemplate(fullClassName, templates);
							inner.fillRowInner(t, r, o, templates, primaryKey);
						}
					} else {
						r.put(f.getName(), val);
					}
				}
				primaryKey.setShouldSkip(!shouldParse);
				final String primary = primaryKey.getParsedKey();
				if (SH.is(primary))
					r.put("I", primary);
				t.getRows().add(r);
			} catch (Exception e) {
				log.severe("Failed to fill row with exception: " + e.toString());
			}
		}

		@SuppressWarnings("unchecked")
		public void fillRow(final Table t, Row r, Object o, final Map<String, ProtobufObjectTemplate> templates, final ProtobufPrimaryKey primaryKey) {
			try {
				//Special handling for 
				if (this.descriptor.getFields().size() == 1) {
					final FieldDescriptor fd = this.descriptor.getFields().get(0);
					final String fullClassName = getFullyQualifiedClassName(fd, this.objectClass);
					if (RH.getClassNoThrow(fullClassName) != null) {
						ProtobufObjectTemplate t2 = getOrSetTemplate(fullClassName, templates);
						boolean repeats = fd.isRepeated();
						boolean isMapField = fd.isMapField();
						Object val = getField.invoke(o, fd);
						if (repeats) {
							List<Object> l = (List<Object>) val;
							for (final Object o2 : l) {
								primaryKey.reset();
								t2.fillRowInner(t, null, o2, templates, primaryKey);
							}
							return;
						} else if (isMapField) {
							t2.fillRowInner(t, null, val, templates, primaryKey);
							return;
						}
					}
				}

				this.fillRowInner(t, r, o, templates, primaryKey);

			} catch (

			Exception e) {
				log.severe("Failed to fill row with exception: " + e.toString());
			}

		}

		@SuppressWarnings("unchecked")
		public void fillConverter(final AmiRelayMapToBytesConverter converter, Object o, final Map<String, ProtobufObjectTemplate> templates, final AmiFHBase fh,
				final String targetTableName, final ProtobufPrimaryKey primaryKey) {
			try {
				//Special handling for results that return a singular list/map with an underlying type
				if (this.descriptor.getFields().size() == 1) {
					final FieldDescriptor fd = this.descriptor.getFields().get(0);
					final String fullClassName = getFullyQualifiedClassName(fd, this.objectClass);
					if (RH.getClassNoThrow(fullClassName) != null) {
						ProtobufObjectTemplate t2 = getOrSetTemplate(fullClassName, templates);
						boolean repeats = fd.isRepeated();
						boolean isMapField = fd.isMapField();
						Object val = getField.invoke(o, fd);
						if (repeats) {
							List<Object> l = (List<Object>) val;
							for (final Object o2 : l) {
								primaryKey.reset();
								t2.fillConverterInner(converter, o2, templates, primaryKey);
								fh.publishObjectToAmi(-1, primaryKey.getParsedKey(), targetTableName, 0, converter.toBytes());
							}
							return;
						} else if (isMapField) {
							t2.fillConverterInner(converter, val, templates, primaryKey);
							fh.publishObjectToAmi(-1, primaryKey.getParsedKey(), targetTableName, 0, converter.toBytes());
							return;
						}
					}
				}

				this.fillConverterInner(converter, o, templates, primaryKey);
				fh.publishObjectToAmi(-1, primaryKey.getParsedKey(), targetTableName, 0, converter.toBytes());
			} catch (

			Exception e) {
				log.severe("Failed to fill row with exception: " + e.toString());
			}
		}

		private void recursivelyGetClass(final List<Class<?>> classes, final List<String> names, final Map<String, ProtobufObjectTemplate> templates, boolean firstCall) {
			//Special handling for results that return a singular list/map with an underlying type
			if (firstCall && this.descriptor.getFields().size() == 1) {
				final FieldDescriptor fd = this.descriptor.getFields().get(0);
				final String fullClassName = getFullyQualifiedClassName(fd, this.objectClass);
				if (RH.getClassNoThrow(fullClassName) != null) {
					ProtobufObjectTemplate t2 = getOrSetTemplate(fullClassName, templates);
					t2.recursivelyGetClass(classes, names, templates, false);
					return;
				}
				log.warning("Failed to get protobuf template for field descriptor: " + fd.getFullName());
			}

			//Otherwise iterate through the first level of descriptors and deduce types
			for (final FieldDescriptor fd : this.descriptor.getFields()) {
				final Class<?> c = protobufToJavaClass(fd.getJavaType());

				if (fd.isMapField() || fd.isRepeated()) { //Handle nested types as json
					classes.add(String.class);
					names.add(fd.getName());

				} else if (c == Object.class) { //Handle protobuf objects
					final String fullClassName = getFullyQualifiedClassName(fd, this.objectClass);
					if (RH.getClassNoThrow(fullClassName) == null)
						throw new RuntimeException("Failed to get protobuf template for field descriptor: " + fd.getFullName());

					ProtobufObjectTemplate t2 = getOrSetTemplate(fullClassName, templates);
					t2.recursivelyGetClass(classes, names, templates, false);

				} else { //Native types
					classes.add(c);
					names.add(fd.getName());
				}
			}
		}

		public Table toEmptyTable(final Map<String, ProtobufObjectTemplate> templates) {
			ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
			ArrayList<String> names = new ArrayList<String>();
			recursivelyGetClass(classes, names, templates, true);
			return new ColumnarTable(classes.toArray(new Class<?>[classes.size()]), names.toArray(new String[names.size()]));
		}

	}

	public static Class<?> protobufToJavaClass(final JavaType type) {
		if (JavaType.STRING.equals(type))
			return String.class;
		else if (JavaType.INT.equals(type))
			return Integer.class;
		else if (JavaType.BOOLEAN.equals(type))
			return Boolean.class;
		else if (JavaType.DOUBLE.equals(type))
			return Double.class;
		else if (JavaType.FLOAT.equals(type))
			return Float.class;
		else if (JavaType.LONG.equals(type))
			return Long.class;
		else if (JavaType.MESSAGE.equals(type))
			return Object.class;
		throw new UnsupportedOperationException("Unsupported protobuf class: " + type.toString());
	}

	//Helper method to get all variable names and types + enum values
	public static List<String> describeClass(String className, final Map<String, ProtobufObjectTemplate> templates) {
		final List<String> output = CH.l();
		final ProtobufObjectTemplate template = getOrSetTemplate(className, templates);
		for (final FieldDescriptor f : template.descriptor.getFields()) {
			final Object defaultVal = f.getDefaultValue();
			String classStr = f.getJavaType().name();
			String append = "";
			if (defaultVal instanceof List)
				classStr = "LIST<" + classStr + ">";
			else if ("ENUM".equals(classStr)) {
				append += "[";
				for (final EnumValueDescriptor enumDescriptors : f.getEnumType().getValues())
					append += "(" + enumDescriptors.getIndex() + ")" + enumDescriptors.getName();
				append += "]";
			}
			output.add(classStr + " " + f.getName() + append);
		}
		return output;
	}

	//To avoid reconstructing template object
	public static ProtobufObjectTemplate getOrSetTemplate(final String className, final Map<String, ProtobufObjectTemplate> templates) {
		if (templates != null && templates.containsKey(className))
			return templates.get(className);
		final ProtobufObjectTemplate t = new ProtobufObjectTemplate(className);
		if (templates != null)
			templates.put(className, t);
		return t;
	}

	public static ProtobufObjectTemplate getOrSetTemplate(final Object o, final Map<String, ProtobufObjectTemplate> templates) {
		final String className = o.getClass().getName();
		return getOrSetTemplate(className, templates);
	}
}
