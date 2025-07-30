package com.f1.utils.mirror;

/*
 * Allows one to represent a java class w/o necessarily loading it into the jvm
 */

public interface ClassMirror {

	boolean isInterface();

	boolean isAssignableFrom(ClassMirror c);

	boolean isArray();

	boolean isPrimitive();

	boolean isAnnotation();

	boolean isSynthetic();

	String getName();

	ClassMirror getSuperclass();

	String getPackage();

	ClassMirror[] getInterfaces();

	ClassMirror getComponentType();

	int getModifiers();

	MethodMirror getEnclosingMethod();

	ConstructorMirror getEnclosingConstructor();

	ClassMirror getDeclaringClass();

	ClassMirror getEnclosingClass();

	String getSimpleName();

	String getCanonicalName();

	boolean isAnonymousClass();

	boolean isLocalClass();

	boolean isMemberClass();

	ClassMirror[] getClasses();

	FieldMirror[] getFields();

	MethodMirror[] getMethods();

	ConstructorMirror[] getConstructors();

	FieldMirror getField(String name_);

	MethodMirror getMethod(String name_, ClassMirror... parameterTypes_);

	ConstructorMirror getConstructor(ClassMirror... parameterTypes_);

	ClassMirror[] getDeclaredClasses();

	FieldMirror[] getDeclaredFields();

	MethodMirror[] getDeclaredMethods();

	ConstructorMirror[] getDeclaredConstructors();

	FieldMirror getDeclaredField(String name_);

	MethodMirror getDeclaredMethod(String name_, ClassMirror... parameterTypes_);

	ConstructorMirror getDeclaredConstructor(ClassMirror... parameterTypes_);

	boolean isEnum();

}
