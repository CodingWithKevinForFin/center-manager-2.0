/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import com.f1.base.Generated;
import com.f1.base.ObjectGenerator;
import com.f1.base.ObjectGeneratorForClass;

public class BasicObjectGeneratorForClass<T> implements ObjectGeneratorForClass<T> {

	final private Class clazz;
	final private ObjectGenerator generator;
	final private ObjectGenerator actualGenerator;
	private boolean implementsGenerated;

	public BasicObjectGeneratorForClass(ObjectGenerator actualGenerator, ObjectGenerator generator, Class clazz) {
		this.clazz = clazz;
		this.generator = generator;
		if (actualGenerator == null)
			this.actualGenerator = BasicObjectGenerator.INSTANCE;
		else
			this.actualGenerator = actualGenerator;
		this.implementsGenerated = Generated.class.isAssignableFrom(clazz);
	}

	@Override
	public Class<T> askType() {
		return clazz;
	}

	@Override
	public T nw() {
		T r = (T) actualGenerator.nw(clazz);
		if (implementsGenerated)
			((Generated) r).construct(generator);
		return r;
	}

	@Override
	public T nw(Object[] args) {
		T r = (T) actualGenerator.nw(clazz, args);
		if (implementsGenerated)
			((Generated) r).construct(generator);
		return r;
	}

	@Override
	public T nwCast(Class[] types, Object[] args) {
		T r = (T) actualGenerator.nwCast(clazz, types, args);
		if (implementsGenerated)
			((Generated) r).construct(generator);
		return r;
	}

	public static <T> ObjectGeneratorForClass<T> create(ObjectGenerator actualGenerator, ObjectGenerator generator, Class<T> type) {
		if (type == ArrayList.class)
			return (ObjectGeneratorForClass) new ArrayListGenerator();
		if (type == HashSet.class)
			return (ObjectGeneratorForClass) new ArrayListGenerator();
		return new BasicObjectGeneratorForClass<T>(actualGenerator, generator, type);
	}

	private static class ArrayListGenerator implements ObjectGeneratorForClass<ArrayList> {
		static final private ArrayListGenerator INSTANCE = new ArrayListGenerator();
		@Override
		public Class<ArrayList> askType() {
			return ArrayList.class;
		}

		@Override
		public ArrayList nw() {
			return new ArrayList();
		}

		@Override
		public ArrayList nw(Object[] args) {
			switch (args.length) {
				case 0 :
					return nw();
				case 1 :
					if (args[0] instanceof Integer)
						return new ArrayList((Integer) args[0]);
					if (args[0] instanceof Collection)
						return new ArrayList((Collection) args[0]);
					break;
			}
			throw new IllegalArgumentException(Arrays.toString(args));
		}

		@Override
		public ArrayList nwCast(Class[] types, Object[] args) {
			return nw(args);
		}

	}

	private static class HashSetGenerator implements ObjectGeneratorForClass<HashSet> {
		static final private HashSetGenerator INSTANCE = new HashSetGenerator();
		@Override
		public Class<HashSet> askType() {
			return HashSet.class;
		}

		@Override
		public HashSet nw() {
			return new HashSet();
		}

		@Override
		public HashSet nw(Object[] args) {
			switch (args.length) {
				case 0 :
					return nw();
				case 1 :
					if (args[0] instanceof Integer)
						return new HashSet((Integer) args[0]);
					if (args[0] instanceof Collection)
						return new HashSet((Collection) args[0]);
					break;
			}
			throw new IllegalArgumentException(Arrays.toString(args));
		}

		@Override
		public HashSet nwCast(Class[] types, Object[] args) {
			return nw(args);
		}

	}
}
