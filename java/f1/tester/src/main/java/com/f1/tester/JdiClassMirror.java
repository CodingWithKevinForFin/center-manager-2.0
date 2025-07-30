package com.f1.tester;

import com.f1.utils.MH;
import com.f1.utils.mirror.ClassMirror;
import com.f1.utils.mirror.simple.SimpleClassMirror;

public class JdiClassMirror extends SimpleClassMirror {
	public static final byte TYPE_INTERFACE = 1;
	public static final byte TYPE_ARRAY = 2;
	public static final byte TYPE_PRIMITIVE = 4;
	public static final byte TYPE_ANNOTATION = 8;
	public static final byte TYPE_SYNTHETIC = 16;
	public static final byte TYPE_ENUM = 32;
	public static final byte TYPE_CLASS = 64;
	private final JdiClassMirror superClass;
	private final JdiClassMirror[] interfaces;
	private final byte type;

	public JdiClassMirror(String className, JdiClassMirror superClass, JdiClassMirror[] interfaces, byte type) {
		super(className);
		this.superClass = superClass;
		this.interfaces = interfaces;
		this.type = type;
	}

	@Override
	public boolean isEnum() {
		return MH.areAllBitsSet(type, TYPE_ENUM);
	}

	@Override
	public boolean isInterface() {
		return MH.areAllBitsSet(type, TYPE_INTERFACE);
	}

	@Override
	public boolean isArray() {
		return MH.areAllBitsSet(type, TYPE_ARRAY);
	}

	@Override
	public boolean isPrimitive() {
		return MH.areAllBitsSet(type, TYPE_PRIMITIVE);
	}

	@Override
	public boolean isAnnotation() {
		return MH.areAllBitsSet(type, TYPE_ANNOTATION);
	}

	@Override
	public boolean isSynthetic() {
		return MH.areAllBitsSet(type, TYPE_SYNTHETIC);
	}

	@Override
	public ClassMirror getSuperclass() {
		return superClass;
	}

	@Override
	public ClassMirror[] getInterfaces() {
		return interfaces;
	}

	@Override
	public String toString() {
		return "JdiClassMirror: " + getName();
	}
}
