package com.f1.utils.assist.analysis;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class ClassAnalyzerField {

	final public static byte TYPE_PRIMITIVE_BOOL = 1;
	final public static byte TYPE_PRIMITIVE_BYTE = 2;
	final public static byte TYPE_PRIMITIVE_CHAR = 3;
	final public static byte TYPE_PRIMITIVE_SHORT = 4;
	final public static byte TYPE_PRIMITIVE_INT = 5;
	final public static byte TYPE_PRIMITIVE_FLOAT = 6;
	final public static byte TYPE_PRIMITIVE_LONG = 7;
	final public static byte TYPE_PRIMITIVE_DOUBLE = 8;
	final public static byte TYPE_OBJECT = 17;

	private static final Map<Class, Byte> TYPES = new HashMap<Class, Byte>();
	static {
		TYPES.put(boolean.class, TYPE_PRIMITIVE_BOOL);
		TYPES.put(byte.class, TYPE_PRIMITIVE_BYTE);
		TYPES.put(char.class, TYPE_PRIMITIVE_CHAR);
		TYPES.put(short.class, TYPE_PRIMITIVE_SHORT);
		TYPES.put(int.class, TYPE_PRIMITIVE_INT);
		TYPES.put(float.class, TYPE_PRIMITIVE_FLOAT);
		TYPES.put(long.class, TYPE_PRIMITIVE_LONG);
		TYPES.put(double.class, TYPE_PRIMITIVE_DOUBLE);
	}

	final private Field field;
	final private ClassAnalyzer componentAnalyzer;
	final private byte type;
	final private AnalyzerManager manager;

	public byte getType() {
		return type;
	}

	public ClassAnalyzerField(Field field, AnalyzerManager manager) {
		field.setAccessible(true);
		this.manager = manager;
		this.field = field;
		Class<?> ctype = field.getType();
		if (ctype.isPrimitive()) {
			this.type = TYPES.get(ctype).byteValue();
			this.componentAnalyzer = null;
		} else if (Modifier.isFinal(ctype.getModifiers())) {
			type = TYPE_OBJECT;
			componentAnalyzer = manager.getClassAnalyzer(ctype);
		} else {
			type = TYPE_OBJECT;
			this.componentAnalyzer = null;
		}
	}
	public Field getField() {
		return field;
	}

	public Object getValue(Object obj) throws Exception {
		return field.get(obj);
	}

	public ClassAnalyzer getComponentType(Object obj) {
		return componentAnalyzer != null ? componentAnalyzer : manager.getClassAnalyzer(obj.getClass());
	}

}
