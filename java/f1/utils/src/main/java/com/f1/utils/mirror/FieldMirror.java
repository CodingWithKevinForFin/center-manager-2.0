package com.f1.utils.mirror;

/*
 * Allows one to represent a java field w/o necessarily loading it into the jvm
 */

import java.lang.annotation.Annotation;

public interface FieldMirror {

	ClassMirror getDeclaringClass();

	String getName();

	int getModifiers();

	boolean isAccessible();

	boolean isEnumConstant();

	boolean isSynthetic();

	Class<?> getType();

	Annotation[] getAnnotations();
}
