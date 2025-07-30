package com.f1.utils.mirror.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import com.f1.utils.mirror.ClassMirror;
import com.f1.utils.mirror.ConstructorMirror;
import com.f1.utils.OH;

public class ReflectedConstructorMirror implements ConstructorMirror {

	private Constructor inner;

	public ReflectedConstructorMirror(Constructor inner) {
		this.inner = inner;
	}

	@Override
	public boolean isAccessible() {
		return inner.isAccessible();
	}

	@Override
	public ClassMirror getDeclaringClass() {
		return ReflectedClassMirror.valueOf(inner.getDeclaringClass());
	}

	@Override
	public String getName() {
		return inner.getName();
	}

	@Override
	public int getModifiers() {
		return inner.getModifiers();
	}

	@Override
	public TypeVariable[] getTypeParameters() {
		return inner.getTypeParameters();
	}

	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass_) {
		return inner.isAnnotationPresent(annotationClass_);
	}

	@Override
	public Annotation[] getAnnotations() {
		return inner.getAnnotations();
	}

	@Override
	public ClassMirror[] getParameterTypes() {
		return ReflectedClassMirror.valuesOf(inner.getParameterTypes());
	}

	@Override
	public Type[] getGenericParameterTypes() {
		return inner.getGenericParameterTypes();
	}

	@Override
	public ClassMirror[] getExceptionTypes() {
		return ReflectedClassMirror.valuesOf(inner.getExceptionTypes());
	}

	@Override
	public Type[] getGenericExceptionTypes() {
		return inner.getGenericExceptionTypes();
	}

	@Override
	public String toString() {
		return inner.toString();
	}

	@Override
	public String toGenericString() {
		return inner.toGenericString();
	}

	@Override
	public boolean isVarArgs() {
		return inner.isVarArgs();
	}

	@Override
	public boolean isSynthetic() {
		return inner.isSynthetic();
	}

	public static ReflectedConstructorMirror valueOf(Constructor c) {
		return new ReflectedConstructorMirror(c);
	}

	public static ConstructorMirror[] valuesOf(Constructor[] c) {

		ConstructorMirror[] r = new ConstructorMirror[c.length];
		for (int i = 0; i < r.length; i++)
			r[i] = valueOf(c[i]);
		return r;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((inner == null) ? 0 : inner.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReflectedConstructorMirror other = (ReflectedConstructorMirror) obj;
		return OH.eq(inner, other.inner);
	}

}
