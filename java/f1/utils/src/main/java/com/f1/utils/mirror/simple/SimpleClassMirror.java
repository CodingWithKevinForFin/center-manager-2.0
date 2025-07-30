package com.f1.utils.mirror.simple;

import com.f1.utils.mirror.ClassMirror;
import com.f1.utils.mirror.ConstructorMirror;
import com.f1.utils.mirror.FieldMirror;
import com.f1.utils.mirror.MethodMirror;
import com.f1.utils.SH;

public class SimpleClassMirror implements ClassMirror {

	final private String name;

	public SimpleClassMirror(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPackage() {
		return SH.beforeLast(name, '.', "");
	}

	@Override
	public String getCanonicalName() {
		return SH.replaceAll(name, '$', '.');
	}

	@Override
	public boolean isInterface() {
		throw unsupportedOperationException();
	}

	private UnsupportedOperationException unsupportedOperationException() {
		return new UnsupportedOperationException("use the ReflectedClassMirror instead");
	}

	@Override
	public boolean isArray() {
		throw unsupportedOperationException();
	}

	@Override
	public boolean isPrimitive() {
		throw unsupportedOperationException();
	}

	@Override
	public boolean isAnnotation() {
		throw unsupportedOperationException();
	}

	@Override
	public boolean isSynthetic() {
		throw unsupportedOperationException();
	}

	@Override
	public ClassMirror getSuperclass() {
		throw unsupportedOperationException();
	}

	@Override
	public ClassMirror[] getInterfaces() {
		throw unsupportedOperationException();
	}

	@Override
	public ClassMirror getComponentType() {
		throw unsupportedOperationException();
	}

	@Override
	public int getModifiers() {
		throw unsupportedOperationException();
	}

	@Override
	public MethodMirror getEnclosingMethod() {
		throw unsupportedOperationException();
	}

	@Override
	public ConstructorMirror getEnclosingConstructor() {
		throw unsupportedOperationException();
	}

	@Override
	public ClassMirror getDeclaringClass() {
		throw unsupportedOperationException();
	}

	@Override
	public ClassMirror getEnclosingClass() {
		throw unsupportedOperationException();
	}

	@Override
	public String getSimpleName() {
		return SH.afterLast(SH.afterLast(name, '.'), '$');
	}

	@Override
	public boolean isAnonymousClass() {
		throw unsupportedOperationException();
	}

	@Override
	public boolean isLocalClass() {
		throw unsupportedOperationException();
	}

	@Override
	public boolean isMemberClass() {
		throw unsupportedOperationException();
	}

	@Override
	public ClassMirror[] getClasses() {
		throw unsupportedOperationException();
	}

	@Override
	public FieldMirror[] getFields() {
		throw unsupportedOperationException();
	}

	@Override
	public MethodMirror[] getMethods() {
		throw unsupportedOperationException();
	}

	@Override
	public ConstructorMirror[] getConstructors() {
		throw unsupportedOperationException();
	}

	@Override
	public FieldMirror getField(String name_) {
		throw unsupportedOperationException();
	}

	@Override
	public MethodMirror getMethod(String name_, ClassMirror... parameterTypes_) {
		throw unsupportedOperationException();
	}

	@Override
	public ConstructorMirror getConstructor(ClassMirror... parameterTypes_) {
		throw unsupportedOperationException();
	}

	@Override
	public ClassMirror[] getDeclaredClasses() {
		throw unsupportedOperationException();
	}

	@Override
	public FieldMirror[] getDeclaredFields() {
		throw unsupportedOperationException();
	}

	@Override
	public MethodMirror[] getDeclaredMethods() {
		throw unsupportedOperationException();
	}

	@Override
	public ConstructorMirror[] getDeclaredConstructors() {
		throw unsupportedOperationException();
	}

	@Override
	public FieldMirror getDeclaredField(String name_) {
		throw unsupportedOperationException();
	}

	@Override
	public MethodMirror getDeclaredMethod(String name_, ClassMirror... parameterTypes_) {
		throw unsupportedOperationException();
	}

	@Override
	public ConstructorMirror getDeclaredConstructor(ClassMirror... parameterTypes_) {
		throw unsupportedOperationException();
	}

	@Override
	public boolean isEnum() {
		throw unsupportedOperationException();
	}

	@Override
	public String toString() {
		return "SimpleClassMirror: " + name;
	}

	@Override
	public boolean isAssignableFrom(ClassMirror c) {
		while (c != null) {
			if (c.getName().equals(getName()))
				return true;
			for (ClassMirror cm : c.getInterfaces())
				if (isAssignableFrom(cm))
					return true;
			c = c.getSuperclass();
		}
		return false;
	}

}
