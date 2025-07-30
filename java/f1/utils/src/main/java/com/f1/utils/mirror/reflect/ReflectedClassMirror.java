package com.f1.utils.mirror.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import com.f1.utils.mirror.ClassMirror;
import com.f1.utils.mirror.ConstructorMirror;
import com.f1.utils.mirror.FieldMirror;
import com.f1.utils.mirror.MethodMirror;
import com.f1.utils.OH;

public class ReflectedClassMirror implements ClassMirror {

	private final Class inner;

	public ReflectedClassMirror(Class inner) {
		OH.assertNotNull(inner);
		this.inner = inner;
	}

	@Override
	public String getName() {
		return inner.getName();
	}

	@Override
	public String getPackage() {
		return inner.getPackage().getName();
	}

	@Override
	public String getCanonicalName() {
		return inner.getCanonicalName();
	}

	@Override
	public boolean isInterface() {
		return inner.isInterface();
	}

	@Override
	public boolean isArray() {
		return inner.isArray();
	}

	@Override
	public boolean isPrimitive() {
		return inner.isPrimitive();
	}

	@Override
	public boolean isAnnotation() {
		return inner.isAnnotation();
	}

	@Override
	public boolean isSynthetic() {
		return inner.isSynthetic();
	}

	public TypeVariable[] getTypeParameters() {
		return inner.getTypeParameters();
	}

	@Override
	public ClassMirror getSuperclass() {
		return valueOf(inner.getSuperclass());
	}

	public Type getGenericSuperclass() {
		return inner.getGenericSuperclass();
	}

	@Override
	public ClassMirror[] getInterfaces() {
		return valuesOf(inner.getInterfaces());
	}

	public Type[] getGenericInterfaces() {
		return inner.getGenericInterfaces();
	}

	@Override
	public ClassMirror getComponentType() {
		return valueOf(inner.getComponentType());
	}

	@Override
	public int getModifiers() {
		return inner.getModifiers();
	}

	@Override
	public MethodMirror getEnclosingMethod() {
		return ReflectedMethodMirror.valueOf(inner.getEnclosingMethod());
	}

	@Override
	public ConstructorMirror getEnclosingConstructor() {
		return ReflectedConstructorMirror.valueOf(inner.getEnclosingConstructor());
	}

	@Override
	public ClassMirror getDeclaringClass() {
		return valueOf(inner.getDeclaringClass());
	}

	@Override
	public ClassMirror getEnclosingClass() {
		return valueOf(inner.getEnclosingClass());
	}

	@Override
	public String getSimpleName() {
		return inner.getSimpleName();
	}

	@Override
	public boolean isAnonymousClass() {
		return inner.isAnonymousClass();
	}

	@Override
	public boolean isLocalClass() {
		return inner.isLocalClass();
	}

	@Override
	public boolean isMemberClass() {
		return inner.isMemberClass();
	}

	@Override
	public ClassMirror[] getClasses() {
		return valuesOf(inner.getClasses());
	}

	@Override
	public FieldMirror[] getFields() {
		return ReflectedFieldMirror.valuesOf(inner.getFields());
	}

	@Override
	public MethodMirror[] getMethods() {
		return ReflectedMethodMirror.valuesOf(inner.getMethods());
	}

	@Override
	public ConstructorMirror[] getConstructors() {
		return ReflectedConstructorMirror.valuesOf(inner.getConstructors());
	}

	@Override
	public FieldMirror getField(String name_) {
		try {
			return ReflectedFieldMirror.valueOf(inner.getField(name_));
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}

	public Method getMethod(String name_, Class... parameterTypes_) {
		try {
			return inner.getMethod(name_, parameterTypes_);
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}

	public Constructor getConstructor(Class... parameterTypes_) {
		try {
			return inner.getConstructor(parameterTypes_);
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}

	@Override
	public ClassMirror[] getDeclaredClasses() {
		return valuesOf(inner.getDeclaredClasses());
	}

	@Override
	public FieldMirror[] getDeclaredFields() {
		return ReflectedFieldMirror.valuesOf(inner.getDeclaredFields());
	}

	@Override
	public MethodMirror[] getDeclaredMethods() {
		return ReflectedMethodMirror.valuesOf(inner.getDeclaredMethods());
	}

	@Override
	public ConstructorMirror[] getDeclaredConstructors() {
		return ReflectedConstructorMirror.valuesOf(inner.getDeclaredConstructors());
	}

	@Override
	public FieldMirror getDeclaredField(String name_) {
		try {
			return ReflectedFieldMirror.valueOf(inner.getDeclaredField(name_));
		} catch (Exception e_) {
			throw OH.toRuntime(e_);
		}
	}

	public Method getDeclaredMethod(String name_, Class... parameterTypes_) throws NoSuchMethodException, SecurityException {
		return inner.getDeclaredMethod(name_, parameterTypes_);
	}

	public Constructor getDeclaredConstructor(Class... parameterTypes_) throws NoSuchMethodException, SecurityException {
		return inner.getDeclaredConstructor(parameterTypes_);
	}

	@Override
	public boolean isEnum() {
		return inner.isEnum();
	}

	public static ReflectedClassMirror valueOf(Class<?> clazz) {
		if (clazz == null)
			return null;
		return new ReflectedClassMirror(clazz);
	}

	public static ReflectedClassMirror valueOf(ClassMirror clazz) throws ClassNotFoundException {
		if (clazz == null)
			return null;
		if (clazz instanceof ReflectedClassMirror)
			return (ReflectedClassMirror) clazz;
		return valueOf(Class.forName(clazz.getName()));
	}

	public static ClassMirror[] valuesOf(Class<?>[] clazz) {
		ClassMirror[] r = new ClassMirror[clazz.length];
		for (int i = 0; i < clazz.length; i++)
			r[i] = valueOf(clazz[i]);
		return r;
	}

	@Override
	public MethodMirror getMethod(String name, ClassMirror... parameterTypes) {
		try {
			Class[] t = extractTypes(parameterTypes);
			return ReflectedMethodMirror.valueOf(inner.getMethod(name, t));
		} catch (Exception e_) {
			throw OH.toRuntime(e_);
		}
	}

	@Override
	public ConstructorMirror getConstructor(ClassMirror... parameterTypes) {
		try {
			final Class[] t = extractTypes(parameterTypes);
			return ReflectedConstructorMirror.valueOf(inner.getConstructor(t));
		} catch (Exception e_) {
			throw OH.toRuntime(e_);
		}
	}

	@Override
	public MethodMirror getDeclaredMethod(String name, ClassMirror... parameterTypes) {
		try {
			final Class[] t = extractTypes(parameterTypes);
			return ReflectedMethodMirror.valueOf(inner.getDeclaredMethod(name, t));
		} catch (Exception e_) {
			throw OH.toRuntime(e_);
		}
	}

	@Override
	public ConstructorMirror getDeclaredConstructor(ClassMirror... parameterTypes) {
		try {
			final Class[] t = extractTypes(parameterTypes);
			return ReflectedConstructorMirror.valueOf(inner.getConstructor(t));
		} catch (Exception e_) {
			throw OH.toRuntime(e_);
		}
	}

	private Class[] extractTypes(ClassMirror[] t) {
		Class[] r = new Class[t.length];
		for (int i = 0; i < t.length; i++)
			r[i] = ((ReflectedClassMirror) t[i]).getReflectedClass();
		return r;
	}

	// Maybe null if not reflected yet!
	public Class getReflectedClass() {
		return inner;
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
		ReflectedClassMirror other = (ReflectedClassMirror) obj;
		return OH.eq(inner, other.inner);
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

	@Override
	public String toString() {
		return "ReflectedClassMirror: " + getName();
	}

}
