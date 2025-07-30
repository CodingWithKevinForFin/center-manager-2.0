package com.f1.utils.mirror.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import com.f1.utils.mirror.ClassMirror;
import com.f1.utils.mirror.MethodMirror;
import com.f1.utils.OH;

public class ReflectedMethodMirror implements MethodMirror {

	final private Method inner;
	private ClassMirror returnType;
	private ReflectedClassMirror declaringClass;
	private ClassMirror[] parameterTypes;
	private ClassMirror[] exceptionTypes;

	public ReflectedMethodMirror(Method inner) {
		this.inner = inner;
	}

	@Override
	public boolean isAccessible() {
		return inner.isAccessible();
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

	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass_) {
		return inner.isAnnotationPresent(annotationClass_);
	}

	@Override
	public TypeVariable<Method>[] getTypeParameters() {
		return inner.getTypeParameters();
	}

	@Override
	public Annotation[] getAnnotations() {
		return inner.getAnnotations();
	}

	@Override
	public ClassMirror getReturnType() {
		if (returnType == null)
			returnType = ReflectedClassMirror.valueOf(inner.getReturnType());
		return returnType;
	}

	@Override
	public Type getGenericReturnType() {
		return inner.getGenericReturnType();
	}

	@Override
	public ClassMirror[] getParameterTypes() {
		if (parameterTypes == null)
			parameterTypes = ReflectedClassMirror.valuesOf(inner.getParameterTypes());
		return parameterTypes;
	}

	@Override
	public Type[] getGenericParameterTypes() {
		return inner.getGenericParameterTypes();
	}

	@Override
	public ClassMirror[] getExceptionTypes() {
		if (exceptionTypes == null)
			exceptionTypes = ReflectedClassMirror.valuesOf(inner.getParameterTypes());
		return exceptionTypes;
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
	public boolean isBridge() {
		return inner.isBridge();
	}

	@Override
	public boolean isVarArgs() {
		return inner.isVarArgs();
	}

	@Override
	public boolean isSynthetic() {
		return inner.isSynthetic();
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return inner.getDeclaredAnnotations();
	}

	@Override
	public Object getDefaultValue() {
		return inner.getDefaultValue();
	}

	@Override
	public Annotation[][] getParameterAnnotations() {
		return inner.getParameterAnnotations();
	}

	public static MethodMirror valueOf(Method f) {
		return new ReflectedMethodMirror(f);
	}

	public static MethodMirror[] valuesOf(Method[] f) {
		MethodMirror[] r = new MethodMirror[f.length];
		for (int i = 0; i < f.length; i++)
			r[i] = valueOf(f[i]);
		return r;
	}

	@Override
	public int hashCode() {
		return inner.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReflectedMethodMirror other = (ReflectedMethodMirror) obj;
		return OH.eq(inner, other.inner);
	}

}
