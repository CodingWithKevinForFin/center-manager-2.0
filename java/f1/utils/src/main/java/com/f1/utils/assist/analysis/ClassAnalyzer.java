package com.f1.utils.assist.analysis;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassAnalyzer {
	final public static byte TYPE_ARRAY_BOOL = 9;
	final public static byte TYPE_ARRAY_BYTE = 10;
	final public static byte TYPE_ARRAY_CHAR = 11;
	final public static byte TYPE_ARRAY_SHORT = 12;
	final public static byte TYPE_ARRAY_INT = 13;
	final public static byte TYPE_ARRAY_FLOAT = 14;
	final public static byte TYPE_ARRAY_LONG = 15;
	final public static byte TYPE_ARRAY_DOUBLE = 16;
	final public static byte TYPE_OBJECT = 17;
	final public static byte TYPE_ARRAY_OBJECT = 19;

	private final Class<?> clazz;

	private final List<ClassAnalyzerField> fields = new ArrayList<ClassAnalyzerField>();

	final private byte type;
	final private ClassAnalyzer componentAnalyzer;
	final private AnalyzerManager manager;
	private boolean isArray;

	private static final Map<Class, Byte> TYPES = new HashMap<Class, Byte>();
	static {
		TYPES.put(boolean[].class, TYPE_ARRAY_BOOL);
		TYPES.put(byte[].class, TYPE_ARRAY_BYTE);
		TYPES.put(char[].class, TYPE_ARRAY_CHAR);
		TYPES.put(short[].class, TYPE_ARRAY_SHORT);
		TYPES.put(int[].class, TYPE_ARRAY_INT);
		TYPES.put(float[].class, TYPE_ARRAY_FLOAT);
		TYPES.put(long[].class, TYPE_ARRAY_LONG);
		TYPES.put(double[].class, TYPE_ARRAY_DOUBLE);
	}

	public ClassAnalyzer(Class<?> clazz, AnalyzerManager analyzerManager) {
		this.clazz = clazz;
		this.manager = analyzerManager;
		if (this.isArray = clazz.isArray()) {
			if (clazz.getComponentType().isPrimitive()) {
				this.type = TYPES.get(clazz);
				this.componentAnalyzer = null;
			} else if (Modifier.isFinal(clazz.getComponentType().getModifiers())) {
				this.componentAnalyzer = analyzerManager.getClassAnalyzer(clazz.getComponentType());
				this.type = TYPE_ARRAY_OBJECT;
			} else {
				this.componentAnalyzer = null;
				this.type = TYPE_ARRAY_OBJECT;
			}
		} else {
			this.type = TYPE_OBJECT;
			this.componentAnalyzer = null;
		}
	}
	public void initFields() {
		if (!isArray) {
			for (Class<?> cl = clazz; cl != Object.class; cl = cl.getSuperclass()) {
				for (Field field : cl.getDeclaredFields()) {
					int mod = field.getModifiers();
					if (Modifier.isStatic(mod))
						continue;
					if (Modifier.isPrivate(mod))
						field.setAccessible(true);
					fields.add(new ClassAnalyzerField(field, this.manager));
				}
			}
		}
	}
	public Class<?> getClassType() {
		return clazz;
	}

	public byte getType() {
		return type;
	}

	public List<ClassAnalyzerField> getFields() {
		return fields;
	}
	public boolean isArray() {
		return isArray;
	}
	public int getLength(Object obj) {
		if (!isArray)
			throw new UnsupportedOperationException("not an array: " + obj);
		return Array.getLength(obj);
	}
	public ClassAnalyzer getComponentType(Object o) {
		if (o == null)
			return null;
		
		return manager.getClassAnalyzer(o.getClass());
	}

}
