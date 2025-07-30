package com.f1.bootstrap.appmonitor;

import java.lang.Thread.State;
import java.lang.management.MemoryPoolMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.base.BasicTypes;
import com.f1.base.IdeableGenerator;
import com.f1.base.Valued;
import com.f1.bootstrap.Bootstrap;
import com.f1.container.Container;
import com.f1.container.ContainerScope;
import com.f1.container.ContainerServices;
import com.f1.container.ContainerTools;
import com.f1.container.DispatchController;
import com.f1.container.InputPort;
import com.f1.container.OutputPort;
import com.f1.container.PartitionController;
import com.f1.container.PartitionGenerator;
import com.f1.container.PartitionResolver;
import com.f1.container.PersistenceController;
import com.f1.container.Port;
import com.f1.container.Processor;
import com.f1.container.RequestInputPort;
import com.f1.container.RequestOutputPort;
import com.f1.container.StateGenerator;
import com.f1.container.Suite;
import com.f1.container.SuiteController;
import com.f1.container.ThreadPoolController;
import com.f1.container.ThreadScopeController;
import com.f1.container.ThrowableHandler;
import com.f1.povo.f1app.F1AppClass;
import com.f1.povo.f1app.F1AppContainerScope;
import com.f1.povo.f1app.F1AppDatabase;
import com.f1.povo.f1app.F1AppDispatcher;
import com.f1.povo.f1app.F1AppEntity;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.povo.f1app.F1AppLogger;
import com.f1.povo.f1app.F1AppLoggerSink;
import com.f1.povo.f1app.F1AppMsgTopic;
import com.f1.povo.f1app.F1AppPartition;
import com.f1.povo.f1app.F1AppPort;
import com.f1.povo.f1app.F1AppProcessor;
import com.f1.povo.f1app.F1AppProperty;
import com.f1.povo.f1app.F1AppState;
import com.f1.povo.f1app.F1AppThreadScope;
import com.f1.povo.f1app.audit.F1AppAuditTrailEvent;
import com.f1.utils.ByteHelper;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.EnvironmentDump;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.Property;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.Tuple2;

public class AppMonitorUtils {
	public static final Logger log = Logger.getLogger(AppMonitorUtils.class.getName());

	public static <T extends AppMonitorObjectListener> T getExistingListener(AppMonitorState state, Object[] listeners, Class<T> returnType) {
		for (Object o : listeners) {
			if (o instanceof AppMonitorObjectListener) {
				if (state == ((AppMonitorObjectListener) o).getState())
					return (T) o;
			}

		}
		return null;
	}
	final static private Map<Class, Byte> classesToTypes = new HashMap<Class, Byte>();
	static {
		classesToTypes.put(F1AppDispatcher.class, F1AppEntity.TYPE_CONTAINER_SCOPE);
		classesToTypes.put(F1AppClass.class, F1AppEntity.TYPE_CONTAINER_STATE);
		classesToTypes.put(F1AppDatabase.class, F1AppEntity.TYPE_DATABASE);
		classesToTypes.put(F1AppMsgTopic.class, F1AppEntity.TYPE_MSG_TOPIC);
		classesToTypes.put(F1AppContainerScope.class, F1AppEntity.TYPE_CONTAINER_SCOPE);
		classesToTypes.put(F1AppLogger.class, F1AppEntity.TYPE_LOGGER);
		classesToTypes.put(F1AppLoggerSink.class, F1AppEntity.TYPE_LOGGER_SINK);
		classesToTypes.put(F1AppPartition.class, F1AppEntity.TYPE_CONTAINER_PARTITION);
		classesToTypes.put(F1AppPort.class, F1AppEntity.TYPE_CONTAINER_SCOPE);
		classesToTypes.put(F1AppProcessor.class, F1AppEntity.TYPE_CONTAINER_SCOPE);
		classesToTypes.put(F1AppState.class, F1AppEntity.TYPE_CONTAINER_STATE);
		classesToTypes.put(F1AppThreadScope.class, F1AppEntity.TYPE_CONTAINER_THREADSCOPE);
		classesToTypes.put(F1AppMsgTopic.class, F1AppEntity.TYPE_MSG_TOPIC);
		classesToTypes.put(F1AppMsgTopic.class, F1AppEntity.TYPE_MSG_TOPIC);
		classesToTypes.put(F1AppProperty.class, F1AppEntity.TYPE_PROPERTY);
		classesToTypes.put(F1AppInstance.class, F1AppEntity.TYPE_APP_INSTANCE);
	}

	public static Map<String, List<F1AppProperty>> toF1AppProperties(PropertyController props, IdeableGenerator gen) {
		final Map<String, List<F1AppProperty>> propertiesMap = new HashMap<String, List<F1AppProperty>>();
		for (String key : props.getKeys()) {
			final List<Property> properties = props.getPropertySources(key);
			boolean isPassword = EnvironmentDump.isPasswordKey(key);
			final List<F1AppProperty> aprops = new ArrayList<F1AppProperty>(properties.size());
			int position = properties.size();
			for (Property property : properties) {
				F1AppProperty aprop = gen.nw(F1AppProperty.class);
				populateProperty(property, aprop, --position);
				if (position == 0)//for the final property always use the materialized value
					aprop.setValue(OH.noNull(props.getOptional(key), aprop.getValue()));

				if (isPassword) {
					aprop.setIsSecure(true);
				}
				aprop.setObjectType(F1AppEntity.TYPE_PROPERTY);
				aprops.add(aprop);
			}
			propertiesMap.put(key, aprops);
		}
		return propertiesMap;
	}
	public static void populateSnapshotStatics(Bootstrap bs, F1AppInstance sink) {
		final Map<String, Long> memoryPools = new HashMap<String, Long>();
		for (MemoryPoolMXBean pool : java.lang.management.ManagementFactory.getMemoryPoolMXBeans())
			memoryPools.put(pool.getName(), pool.getUsage().getMax());
		sink.setMainClassName(OH.getName(bs.getMainClass()));
		sink.setAppName(SH.is(bs.getAppNameProperty()) ? bs.getAppNameProperty() : OH.getSimpleName(bs.getMainClass()));
		sink.setHostName(EH.getLocalHost());
		sink.setStartTimeMs(EH.getStartTime());
		sink.setProcessUid(EH.getProcessUid());
		sink.setUserName(EH.getUserName());
		sink.setPid(EH.getPid());
		sink.setPwd(EH.getPwd());
		sink.setClasspath(CH.l(EH.getJavaClassPath()));
		sink.setBootClasspath(CH.l(EH.getBootClassPath()));
		sink.setJavaExternalDirs(CH.l(EH.getJavaExtDirs()));
		sink.setJavaHome(EH.getJavaHome());
		sink.setJavaVendor(EH.getJavaVendor());
		sink.setJavaVersion(EH.getJavaVersion());
		sink.setMainClassArguments(CH.l(bs.getArgs()));
		sink.setJvmArguments(CH.l(EH.getJvmArguments()));
		sink.setIsDebug(EH.getIsDebug());
		sink.setMemoryPools(memoryPools);
		sink.setAvailableProcessorsCount((short) EH.getAvailableProcessors());
	}
	private static void populateProperty(Property p, F1AppProperty prop, int position) {
		prop.setValue(p.getValue());
		prop.setKey(p.getKey());
		prop.setSource(p.getSource());
		prop.setSourceLineNumber(p.getSourceLineNumber());
		prop.setPosition((short) position);
		prop.setSourceType(p.getSourceType());//TODO: these just happen to line up... should use a translation!

	}
	public static void populateSnapshotDynamics(F1AppInstance sink) {

		sink.setFreeMemory(EH.getFreeMemory());
		sink.setMaxMemory(EH.getMaxMemory());
		sink.setTotalMemory(EH.getTotalMemory());
		Thread[] threads = EH.getAllThreads();
		short threadsBlocked = 0, threadsNew = 0, threadsRunnable = 0, threadsTerminated = 0, threadsTimedWaiting = 0, threadsWaiting = 0;
		for (Thread thread : threads) {
			State threadState = thread.getState();
			switch (thread.getState()) {
				case BLOCKED:
					threadsBlocked++;
					break;
				case NEW:
					threadsNew++;
					break;
				case RUNNABLE:
					threadsRunnable++;
					break;
				case TERMINATED:
					threadsTerminated++;
					break;
				case TIMED_WAITING:
					threadsTimedWaiting++;
					break;
				case WAITING:
					threadsWaiting++;
					break;
				default:
					LH.info(log, "unknown thread state: ", threadState);
					continue;
			}
		}
		sink.setThreadsBlockedCount(threadsBlocked);
		sink.setThreadsNewCount(threadsNew);
		sink.setThreadsRunnableCount(threadsRunnable);
		sink.setThreadsTerminatedCount(threadsTerminated);
		sink.setThreadsTimedWaitingCount(threadsTimedWaiting);
		sink.setThreadsWaitingCount(threadsWaiting);
	}

	public static int toContainerScopeType(ContainerScope cu) {
		int type = 0;
		if (cu instanceof Suite)
			type |= F1AppContainerScope.TYPE_SUITE;
		if (cu instanceof DispatchController)
			type |= F1AppContainerScope.TYPE_DISPATCHER;
		if (cu instanceof ContainerServices)
			type |= F1AppContainerScope.TYPE_SERVICES;
		if (cu instanceof Processor)
			type |= F1AppContainerScope.TYPE_PROCESSOR;
		if (cu instanceof Container)
			type |= F1AppContainerScope.TYPE_CONTAINER;
		if (cu instanceof ContainerTools)
			type |= F1AppContainerScope.TYPE_TOOLS;
		if (cu instanceof PartitionController)
			type |= F1AppContainerScope.TYPE_PARTITION_CONTROLLER;
		if (cu instanceof PartitionGenerator)
			type |= F1AppContainerScope.TYPE_PARTITION_GENERATOR;
		if (cu instanceof PartitionResolver)
			type |= F1AppContainerScope.TYPE_PARTITION_RESOLVER;
		if (cu instanceof PersistenceController)
			type |= F1AppContainerScope.TYPE_PERSISTENCE_CONTROLLER;
		if (cu instanceof StateGenerator)
			type |= F1AppContainerScope.TYPE_STATE_GENERATOR;
		if (cu instanceof SuiteController)
			type |= F1AppContainerScope.TYPE_SUITE_CONTROLLER;
		if (cu instanceof ThreadPoolController)
			type |= F1AppContainerScope.TYPE_THREADPOOL_CONTROLLER;
		if (cu instanceof ThreadScopeController)
			type |= F1AppContainerScope.TYPE_THREADSCOPE_CONTROLLER;
		if (cu instanceof ThrowableHandler)
			type |= F1AppContainerScope.TYPE_THROWABLE_HANDLER;
		if (cu instanceof Port) {
			type |= F1AppContainerScope.TYPE_PORT;
			if (cu instanceof InputPort)
				type |= F1AppContainerScope.TYPE_PORT_INPUT;
			if (cu instanceof OutputPort)
				type |= F1AppContainerScope.TYPE_PORT_OUTPUT;
			if (cu instanceof RequestOutputPort || cu instanceof RequestInputPort)
				type |= F1AppContainerScope.TYPE_PORT_REQUEST;
		}
		return type;
	}

	static public void processStats(byte[] stats, LongKeyMap<F1AppEntity> objects) {
		processStats(stats, objects, null);
	}

	//See AppMonitorF1ObjectValuedListener for details on expected stats format
	static public void processStats(byte[] stats, LongKeyMap<F1AppEntity> objects, Set<Long> changedObjectsSink) {
		for (int i = 0; i < stats.length;) {
			long objectId = ByteHelper.readLong(stats, i);
			i += 8;
			F1AppEntity object = objects.get(objectId);
			if (object == null) {
				LH.warning(log, "received update for unknown obj id: ", objectId);
			} else if (changedObjectsSink != null)
				changedObjectsSink.add(objectId);
			for (;;) {
				byte pid = ByteHelper.readByte(stats, i++);
				if (pid == Valued.NO_PID)
					break;
				byte basicType = ByteHelper.readByte(stats, i++);
				switch (basicType) {
					case BasicTypes.PRIMITIVE_BYTE: {
						byte value = ByteHelper.readByte(stats, i);
						i++;
						if (object != null)
							object.putByte(pid, value);
						break;
					}
					case BasicTypes.PRIMITIVE_SHORT: {
						short value = ByteHelper.readShort(stats, i);
						i += 2;
						if (object != null)
							object.putShort(pid, value);
						break;
					}
					case BasicTypes.PRIMITIVE_INT: {
						int value = ByteHelper.readInt(stats, i);
						i += 4;
						if (object != null)
							object.putInt(pid, value);
						break;
					}
					case BasicTypes.PRIMITIVE_LONG: {
						long value = ByteHelper.readLong(stats, i);
						i += 8;
						if (object != null)
							object.putLong(pid, value);
						break;
					}
					case BasicTypes.PRIMITIVE_FLOAT: {
						float value = ByteHelper.readFloat(stats, i);
						i += 4;
						if (object != null)
							object.putFloat(pid, value);
						break;
					}
					case BasicTypes.PRIMITIVE_DOUBLE: {
						double value = ByteHelper.readDouble(stats, i);
						i += 8;
						if (object != null)
							object.putDouble(pid, value);
						break;
					}
					case BasicTypes.PRIMITIVE_CHAR: {
						char value = ByteHelper.readChar(stats, i);
						i += 2;
						if (object != null)
							object.putChar(pid, value);
						break;
					}
					case BasicTypes.PRIMITIVE_BOOLEAN: {
						boolean value = ByteHelper.readBoolean(stats, i);
						i += 1;
						if (object != null)
							object.putBoolean(pid, value);
						break;
					}
					default:
						throw new RuntimeException("unsupported basictype: " + basicType);
				}
			}
		}
	}
	public static String describe(F1AppInstance javaApp) {
		return javaApp == null ? null : javaApp.getProcessUid() + " (" + javaApp.getMainClassName() + " owned by " + javaApp.getUserName() + "@" + javaApp.getHostName() + ")";
	}
	public static void convertPayloadToJson(F1AppAuditTrailEvent event, ObjectToByteArrayConverter converter, ObjectToJsonConverter jsonConverter, Tuple2<String, Object> sink) {
		String json;
		Object object;
		switch (event.getPayloadFormat()) {
			case F1AppAuditTrailEvent.FORMAT_BYTES_F1:
				object = converter.bytes2Object(event.getPayloadAsBytes());
				json = jsonConverter.objectToString(object);
				break;
			case F1AppAuditTrailEvent.FORMAT_STRING_FIX:
				json = jsonConverter.objectToString(object = fixToJson(event.getPayloadAsString()));
				break;
			case F1AppAuditTrailEvent.FORMAT_BYTES_FIX:
				json = jsonConverter.objectToString(object = fixToJson(new String(event.getPayloadAsBytes())));
				break;
			case F1AppAuditTrailEvent.FORMAT_BYTES_TEXT:
				object = new String(event.getPayloadAsBytes());
				json = SH.quote((String) object);
				break;
			case F1AppAuditTrailEvent.FORMAT_STRING_JSON:
				json = event.getPayloadAsString();
				object = jsonConverter.stringToObject(json);
				break;
			case F1AppAuditTrailEvent.FORMAT_STRING_TEXT:
				object = event.getPayloadAsString();
				json = SH.quote(event.getPayloadAsString());
				break;
			default:
				throw new RuntimeException("unknown event type: " + event);
		}
		sink.setAB(json, object);
	}

	private static Map<String, String> fixToJson(String fix) {
		fix = SH.trim(SH.CHAR_SOH, fix);
		return SH.splitToMap(new LinkedHashMap<String, String>(), SH.CHAR_SOH, '=', fix);
	}

	static public byte getType(Class clazz) {
		return classesToTypes.get(clazz);
	}

}
