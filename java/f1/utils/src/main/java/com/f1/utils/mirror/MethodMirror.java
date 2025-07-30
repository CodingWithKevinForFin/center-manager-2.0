package com.f1.utils.mirror;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/*
 * Allows one to represent a java method w/o necessarily loading it into the jvm
 */

public interface MethodMirror {

	boolean isAccessible();

	ClassMirror getDeclaringClass();

	String getName();

	int getModifiers();

	TypeVariable<Method>[] getTypeParameters();

	Annotation[] getAnnotations();

	ClassMirror getReturnType();

	Type getGenericReturnType();

	ClassMirror[] getParameterTypes();

	Type[] getGenericParameterTypes();

	ClassMirror[] getExceptionTypes();

	Type[] getGenericExceptionTypes();

	boolean isBridge();

	boolean isVarArgs();

	boolean isSynthetic();

	Annotation[] getDeclaredAnnotations();

	Object getDefaultValue();

	Annotation[][] getParameterAnnotations();
}
