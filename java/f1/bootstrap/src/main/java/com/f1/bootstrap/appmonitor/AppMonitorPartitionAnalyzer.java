package com.f1.bootstrap.appmonitor;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.IdeableGenerator;
import com.f1.povo.f1app.inspect.F1AppInspectionArray;
import com.f1.povo.f1app.inspect.F1AppInspectionEntity;
import com.f1.povo.f1app.inspect.F1AppInspectionObject;
import com.f1.povo.f1app.inspect.F1AppInspectionString;
import com.f1.utils.ByteHelper;
import com.f1.utils.assist.analysis.ClassAnalyzer;
import com.f1.utils.assist.analysis.ClassAnalyzerField;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.HasherMap.Entry;
import com.f1.utils.mutable.Mutable;
import com.f1.utils.mutable.Mutable.Int;
import com.f1.utils.structs.IntKeyMap;

public class AppMonitorPartitionAnalyzer {

	public static final int MAX_ARRAY_LENGTH = 100;
	public long size;
	final private Map<Object, F1AppInspectionEntity> visited = new IdentityHashMap<Object, F1AppInspectionEntity>();
	final private Map<String, F1AppInspectionString> strings = new HashMap<String, F1AppInspectionString>();
	final private HasherMap<Class<?>, Mutable.Int> counts = new HasherMap<Class<?>, Mutable.Int>();
	final private IdeableGenerator generator;

	public Iterable<F1AppInspectionEntity> getEntities() {
		return entities.values();
	}
	public int getEntitiesCount() {
		return entities.size();
	}

	final private IntKeyMap<F1AppInspectionEntity> entities = new IntKeyMap<F1AppInspectionEntity>();

	private <T extends F1AppInspectionEntity> T createEnity(Object src, Class<T> type) {
		T r = generator.nw(type);
		r.setId(++nextId);
		r.setIdentityHashCode(System.identityHashCode(src));
		entities.put(r.getId(), r);
		visited.put(src, r);
		return r;
	}

	private int nextId = 0;

	public AppMonitorPartitionAnalyzer(IdeableGenerator generator) {
		this.generator = generator;
	}
	private F1AppInspectionEntity getStringId(String name) {
		F1AppInspectionString r = strings.get(name);
		if (r == null) {
			r = createEnity(name, F1AppInspectionString.class);
			r.setString(name);
		}
		return r;
	}

	public F1AppInspectionEntity process(Object obj, ClassAnalyzer ca) throws Exception {
		if (obj == null)
			return null;
		if (visited.containsKey(obj))
			return null;
		if (ca == null)
			return null;
		final F1AppInspectionEntity r;
		Entry<Class<?>, Int> e = counts.getOrCreateEntry(obj.getClass());
		Int val = e.getValue();
		if (val == null)
			e.setValue(new Int(1));
		else
			val.value++;
		switch (ca.getType()) {
			case ClassAnalyzer.TYPE_ARRAY_BOOL: {
				final F1AppInspectionArray rArray = createEnity(obj, F1AppInspectionArray.class);
				rArray.setArrayType(F1AppInspectionEntity.TYPE_BOOL);
				boolean[] a = (boolean[]) obj;
				rArray.setLength(a.length);
				int inspectSize = Math.min(a.length, MAX_ARRAY_LENGTH);
				byte[] values = new byte[inspectSize];
				for (int i = 0; i < inspectSize; i++)
					values[i] = a[i] ? (byte) 0 : (byte) 1;
				r = rArray;
				rArray.setValues(values);
				size += a.length * 1;
				break;
			}
			case ClassAnalyzer.TYPE_ARRAY_BYTE: {
				final F1AppInspectionArray rArray = createEnity(obj, F1AppInspectionArray.class);
				rArray.setArrayType(F1AppInspectionEntity.TYPE_BYTE);
				byte[] a = (byte[]) obj;
				rArray.setLength(a.length);
				int inspectSize = Math.min(a.length, MAX_ARRAY_LENGTH);
				byte[] values = new byte[inspectSize];
				for (int i = 0; i < inspectSize; i++)
					values[i] = a[i];
				r = rArray;
				rArray.setValues(values);
				size += a.length * 1;
				break;
			}
			case ClassAnalyzer.TYPE_ARRAY_CHAR: {
				final F1AppInspectionArray rArray = createEnity(obj, F1AppInspectionArray.class);
				rArray.setArrayType(F1AppInspectionEntity.TYPE_CHAR);
				char[] a = (char[]) obj;
				rArray.setLength(a.length);
				int inspectSize = Math.min(a.length, MAX_ARRAY_LENGTH);
				byte[] values = new byte[inspectSize * 2];
				for (int i = 0; i < inspectSize; i++)
					ByteHelper.writeChar(a[i], values, i * 2);
				r = rArray;
				rArray.setValues(values);
				size += a.length * 2;
				break;
			}
			case ClassAnalyzer.TYPE_ARRAY_SHORT: {
				final F1AppInspectionArray rArray = createEnity(obj, F1AppInspectionArray.class);
				rArray.setArrayType(F1AppInspectionEntity.TYPE_SHORT);
				short[] a = (short[]) obj;
				rArray.setLength(a.length);
				int inspectSize = Math.min(a.length, MAX_ARRAY_LENGTH);
				byte[] values = new byte[inspectSize * 2];
				for (int i = 0; i < inspectSize; i++)
					ByteHelper.writeShort(a[i], values, i * 2);
				r = rArray;
				rArray.setValues(values);
				size += a.length * 2;
				break;
			}
			case ClassAnalyzer.TYPE_ARRAY_INT: {
				final F1AppInspectionArray rArray = createEnity(obj, F1AppInspectionArray.class);
				rArray.setArrayType(F1AppInspectionEntity.TYPE_INT);
				int[] a = (int[]) obj;
				rArray.setLength(a.length);
				int inspectSize = Math.min(a.length, MAX_ARRAY_LENGTH);
				byte[] values = new byte[inspectSize * 4];
				for (int i = 0; i < inspectSize; i++)
					ByteHelper.writeInt(a[i], values, i * 4);
				r = rArray;
				rArray.setValues(values);
				size += a.length * 4;
				break;
			}
			case ClassAnalyzer.TYPE_ARRAY_FLOAT: {
				final F1AppInspectionArray rArray = createEnity(obj, F1AppInspectionArray.class);
				rArray.setArrayType(F1AppInspectionEntity.TYPE_FLOAT);
				float[] a = (float[]) obj;
				rArray.setLength(a.length);
				int inspectSize = Math.min(a.length, MAX_ARRAY_LENGTH);
				byte[] values = new byte[inspectSize * 4];
				for (int i = 0; i < inspectSize; i++)
					ByteHelper.writeFloat(a[i], values, i * 4);
				r = rArray;
				rArray.setValues(values);
				size += a.length * 4;
				break;
			}
			case ClassAnalyzer.TYPE_ARRAY_LONG: {
				final F1AppInspectionArray rArray = createEnity(obj, F1AppInspectionArray.class);
				rArray.setArrayType(F1AppInspectionEntity.TYPE_LONG);
				long[] a = (long[]) obj;
				rArray.setLength(a.length);
				int inspectSize = Math.min(a.length, MAX_ARRAY_LENGTH);
				byte[] values = new byte[inspectSize * 8];
				for (int i = 0; i < inspectSize; i++)
					ByteHelper.writeLong(a[i], values, i * 8);
				r = rArray;
				rArray.setValues(values);
				size += a.length * 8;
				break;
			}
			case ClassAnalyzer.TYPE_ARRAY_DOUBLE: {
				final F1AppInspectionArray rArray = createEnity(obj, F1AppInspectionArray.class);
				rArray.setArrayType(F1AppInspectionEntity.TYPE_DOUBLE);
				double[] a = (double[]) obj;
				rArray.setLength(a.length);
				int inspectSize = Math.min(a.length, MAX_ARRAY_LENGTH);
				byte[] values = new byte[inspectSize * 8];
				for (int i = 0; i < inspectSize; i++)
					ByteHelper.writeDouble(a[i], values, i * 8);
				r = rArray;
				rArray.setValues(values);
				size += a.length * 8;
				break;
			}
			case ClassAnalyzer.TYPE_ARRAY_OBJECT: {
				final F1AppInspectionArray rArray = createEnity(obj, F1AppInspectionArray.class);
				Class<?> ct = ca.getClassType().getComponentType();
				rArray.setComponentType(getStringId(ct == null ? Object.class.getName() : ct.getName()).getId());
				rArray.setArrayType(F1AppInspectionEntity.TYPE_OBJECT);
				r = rArray;
				final Object[] array = (Object[]) obj;
				rArray.setLength(array.length);
				final int size = Math.min(array.length, MAX_ARRAY_LENGTH);
				byte[] buf = new byte[4 * size];
				rArray.setValues(buf);
				for (int i = 0; i < size; i++) {
					Object o = array[i];
					this.size += 4;
					if (o != null) {
						F1AppInspectionEntity t = process(o, ca.getComponentType(o));
						if (t != null)
							ByteHelper.writeInt(t.getId(), buf, i * 4);
					}
				}
				break;
			}
			case ClassAnalyzer.TYPE_OBJECT: {
				if (obj instanceof String) {
					return getStringId((String) obj);
				} else {
					final F1AppInspectionObject rObj = createEnity(obj, F1AppInspectionObject.class);
					rObj.setClassName(getStringId(ca.getClassType().getName()).getId());
					r = rObj;
					List<ClassAnalyzerField> fields = ca.getFields();
					int fCount = fields.size();
					int[] names = new int[fCount];
					rObj.setFieldNames(names);
					byte[] types = new byte[fCount];
					rObj.setFieldTypes(types);
					long[] values = new long[fCount];
					rObj.setFieldValues(values);
					for (int i = 0; i < fields.size(); i++) {
						final ClassAnalyzerField f = fields.get(i);
						process(obj, f, rObj, i);
					}
					break;
				}
			}
			default:
				throw new RuntimeException("unknown type: " + ca.getType());
		}
		return r;
	}
	public void process(Object obj, ClassAnalyzerField f, F1AppInspectionObject rObj, int fieldOffset) throws Exception {
		final byte type;
		final long val;
		switch (f.getType()) {
			case ClassAnalyzerField.TYPE_OBJECT: {
				type = F1AppInspectionEntity.TYPE_OBJECT;
				size += 4;
				Object oval = f.getValue(obj);
				if (oval != null) {
					F1AppInspectionEntity t = process(oval, f.getComponentType(oval));
					if (t != null)
						val = t.getId();
					else
						val = 0;
				} else
					val = 0;
				break;
			}
			case ClassAnalyzerField.TYPE_PRIMITIVE_BOOL:
				type = F1AppInspectionEntity.TYPE_BOOL;
				val = f.getField().getBoolean(obj) ? 1 : 0;
				size += 1;
				break;
			case ClassAnalyzerField.TYPE_PRIMITIVE_BYTE:
				type = F1AppInspectionEntity.TYPE_BYTE;
				val = f.getField().getByte(obj);
				size += 1;
				break;
			case ClassAnalyzerField.TYPE_PRIMITIVE_CHAR:
				type = F1AppInspectionEntity.TYPE_CHAR;
				val = f.getField().getChar(obj);
				size += 2;
				break;
			case ClassAnalyzerField.TYPE_PRIMITIVE_SHORT:
				type = F1AppInspectionEntity.TYPE_SHORT;
				val = f.getField().getShort(obj);
				size += 2;
				break;
			case ClassAnalyzerField.TYPE_PRIMITIVE_INT:
				type = F1AppInspectionEntity.TYPE_INT;
				val = f.getField().getInt(obj);
				size += 4;
				break;
			case ClassAnalyzerField.TYPE_PRIMITIVE_FLOAT:
				type = F1AppInspectionEntity.TYPE_FLOAT;
				val = Float.floatToRawIntBits(f.getField().getFloat(obj));
				size += 4;
				break;
			case ClassAnalyzerField.TYPE_PRIMITIVE_LONG:
				type = F1AppInspectionEntity.TYPE_LONG;
				val = f.getField().getLong(obj);
				size += 8;
				break;
			case ClassAnalyzerField.TYPE_PRIMITIVE_DOUBLE:
				type = F1AppInspectionEntity.TYPE_DOUBLE;
				val = Double.doubleToRawLongBits(f.getField().getDouble(obj));
				size += 8;
				break;
			default:
				throw new RuntimeException("unknown field type: " + f.getType());
		}
		rObj.getFieldNames()[fieldOffset] = getStringId(f.getField().getName()).getId();
		rObj.getFieldTypes()[fieldOffset] = type;
		rObj.getFieldValues()[fieldOffset] = val;
	}

	public long getSize() {
		return size;
	}

	public Set<Class<?>> getClasses() {
		return counts.keySet();
	}
	public long getCount(Class<?> clazz) {
		return counts.get(clazz).value;
	}
}
