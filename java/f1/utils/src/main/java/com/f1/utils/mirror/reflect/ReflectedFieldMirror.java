package com.f1.utils.mirror.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import com.f1.utils.mirror.ClassMirror;
import com.f1.utils.mirror.FieldMirror;
import com.f1.utils.OH;

public class ReflectedFieldMirror implements FieldMirror {

	final private Field inner;
	private ReflectedClassMirror declaringClass;

	public ReflectedFieldMirror(Field inner) {
		this.inner = inner;
	}

	@Override
	public ClassMirror getDeclaringClass() {
		if (declaringClass == null)
			declaringClass = ReflectedClassMirror.valueOf(inner.getDeclaringClass());
		return declaringClass;
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
	public boolean isAccessible() {
		return inner.isAccessible();
	}

	@Override
	public boolean isEnumConstant() {
		return inner.isEnumConstant();
	}

	@Override
	public boolean isSynthetic() {
		return inner.isSynthetic();
	}

	@Override
	public Class<?> getType() {
		return inner.getType();
	}

	@Override
	public Annotation[] getAnnotations() {
		return inner.getAnnotations();
	}

	public static FieldMirror valueOf(Field f) {
		return new ReflectedFieldMirror(f);
	}

	public static FieldMirror[] valuesOf(Field[] f) {
		FieldMirror[] r = new FieldMirror[f.length];
		for (int i = 0; i < f.length; i++)
			r[i] = valueOf(f[i]);
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
		ReflectedFieldMirror other = (ReflectedFieldMirror) obj;
		return OH.eq(inner, other.inner);
	}

}
