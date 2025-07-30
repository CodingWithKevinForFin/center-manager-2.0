package com.f1.utils.mirror;

/*
 * Allows one to represent a java constructor w/o necessarily loading it into the jvm
 */

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public interface ConstructorMirror {
	boolean isAccessible();

	ClassMirror getDeclaringClass();

	String getName();

	int getModifiers();

	TypeVariable[] getTypeParameters();

	Annotation[] getAnnotations();

	ClassMirror[] getParameterTypes();

	Type[] getGenericParameterTypes();

	ClassMirror[] getExceptionTypes();

	Type[] getGenericExceptionTypes();

	String toGenericString();

	boolean isVarArgs();

	boolean isSynthetic();

}
