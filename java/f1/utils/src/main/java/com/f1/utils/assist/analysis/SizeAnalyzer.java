package com.f1.utils.assist.analysis;

import java.util.Set;

import javax.activation.UnsupportedDataTypeException;

import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.HasherMap.Entry;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.mutable.Mutable;
import com.f1.utils.mutable.Mutable.Int;
import com.f1.utils.structs.LongKeyMap;

public class SizeAnalyzer {

	public long size;
	final private Set<Object> visited = new IdentityHashSet<Object>();
	final private HasherMap<Class<?>, Mutable.Int> counts = new HasherMap<Class<?>, Mutable.Int>();

	public void process(Object obj, ClassAnalyzer ca) throws Exception {

		if (obj == null)
			return;
		if (!visited.add(obj))
			return;
		if (ca == null)
			return;
		Entry<Class<?>, Int> e = counts.getOrCreateEntry(obj.getClass());
		Int val = e.getValue();
		if (val == null)
			e.setValue(new Int(1));
		else
			val.value++;
		switch (ca.getType()) {
			case ClassAnalyzer.TYPE_ARRAY_BOOL:
				size += ca.getLength(obj) * 1;
				break;
			case ClassAnalyzer.TYPE_ARRAY_BYTE:
				size += ca.getLength(obj) * 1;
				break;
			case ClassAnalyzer.TYPE_ARRAY_CHAR:
				size += ca.getLength(obj) * 2;
				break;
			case ClassAnalyzer.TYPE_ARRAY_SHORT:
				size += ca.getLength(obj) * 2;
				break;
			case ClassAnalyzer.TYPE_ARRAY_INT:
				size += ca.getLength(obj) * 4;
				break;
			case ClassAnalyzer.TYPE_ARRAY_FLOAT:
				size += ca.getLength(obj) * 4;
				break;
			case ClassAnalyzer.TYPE_ARRAY_LONG:
				size += ca.getLength(obj) * 8;
				break;
			case ClassAnalyzer.TYPE_ARRAY_DOUBLE:
				size += ca.getLength(obj) * 8;
				break;
			case ClassAnalyzer.TYPE_ARRAY_OBJECT:
				for (Object o : (Object[]) obj) {
					size += 4;
					if (o != null)
						process(o, ca.getComponentType(o));
				}
				break;
			case ClassAnalyzer.TYPE_OBJECT:
				for (ClassAnalyzerField f : ca.getFields()) {
					process(obj, f);
				}
		}
	}
	public void process(Object obj, ClassAnalyzerField f) throws Exception {
		switch (f.getType()) {
			case ClassAnalyzerField.TYPE_OBJECT:
				size += 4;
				Object val = f.getValue(obj);
				if (val != null)
					process(val, f.getComponentType(val));
				break;
			case ClassAnalyzerField.TYPE_PRIMITIVE_BOOL:
				size += 1;
				break;
			case ClassAnalyzerField.TYPE_PRIMITIVE_BYTE:
				size += 1;
				break;
			case ClassAnalyzerField.TYPE_PRIMITIVE_CHAR:
				size += 2;
				break;
			case ClassAnalyzerField.TYPE_PRIMITIVE_SHORT:
				size += 2;
				break;
			case ClassAnalyzerField.TYPE_PRIMITIVE_INT:
				size += 4;
				break;
			case ClassAnalyzerField.TYPE_PRIMITIVE_FLOAT:
				size += 4;
				break;
			case ClassAnalyzerField.TYPE_PRIMITIVE_LONG:
				size += 8;
				break;
			case ClassAnalyzerField.TYPE_PRIMITIVE_DOUBLE:
				size += 8;
				break;
			default:
				throw new UnsupportedDataTypeException("Unsupported type " + f.getType() + " for object " + obj);
		}
	}

	public static void main(String a[]) throws Exception {
		SizeAnalyzer sa = new SizeAnalyzer();
		AnalyzerManager manager = new AnalyzerManager();
		LongKeyMap<String> obj = new LongKeyMap<String>();
		for (int i = 0; i < 1000 * 1000; i++) {
			obj.put(i, SH.toString(i));
		}
		System.out.println("start");
		long start = System.currentTimeMillis();
		sa.process(obj, manager.getClassAnalyzer(obj.getClass()));
		System.out.println(sa.size);
		for (java.util.Map.Entry<Class<?>, Int> e : sa.counts.entrySet())
			System.out.println(RH.toLegibleString(e.getKey()) + ": " + e.getValue());
		long end = System.currentTimeMillis();
		System.out.println(end - start);
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
