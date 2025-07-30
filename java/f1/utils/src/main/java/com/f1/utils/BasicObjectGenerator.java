/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.concurrent.ConcurrentMap;
import com.f1.base.ObjectGenerator;
import com.f1.base.ObjectGeneratorForClass;

public class BasicObjectGenerator implements ObjectGenerator {

	public static final BasicObjectGenerator INSTANCE = new BasicObjectGenerator();
	private ConcurrentMap<Class, ObjectGeneratorForClass> generators = new CopyOnWriteHashMap<Class, ObjectGeneratorForClass>();

	@Override
	public Object[] nw(Class<?>... classes) {
		Object r[] = new Object[classes.length];
		for (int i = 0; i < classes.length; i++)
			r[i] = nw(classes[i]);
		return r;
	}

	@Override
	public <C> C nw(Class<C> classs) {
		return nwCast(classs, OH.EMPTY_CLASS_ARRAY, OH.EMPTY_OBJECT_ARRAY);
	}

	@Override
	public <C> C nw(Class<C> classs, Object[] constructorParameters) {
		return nwCast(classs, null, constructorParameters);
	}

	@Override
	public <C> C nwCast(Class<C> classs, Class[] argumentTypes, Object[] constructorParameters) {
		try {
			return OH.nw(classs, argumentTypes, constructorParameters);
		} catch (Exception e) {
			throw new RuntimeException("Error creating " + classs, e);
		}
	}

	@Override
	public <C> ObjectGeneratorForClass<C> getGeneratorForClass(Class<C> clazz) {
		ObjectGeneratorForClass<C> r = generators.get(clazz);
		if (r != null)
			return r;
		if (generators.containsKey(clazz))
			return generators.get(clazz);
		ObjectGeneratorForClass<C> exists = generators.putIfAbsent(clazz, r = createGenerator(clazz));
		return exists == null ? r : exists;
	}

	protected <C> ObjectGeneratorForClass<C> createGenerator(Class<C> clazz) {
		return new BasicObjectGeneratorForClass<C>(this, this, clazz);
	}

	@Override
	public Object get(Class<?> key) {
		return nw(key);
	}

}
