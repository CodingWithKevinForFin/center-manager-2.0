package com.f1.utils.impl;

import java.lang.reflect.Constructor;
import com.f1.base.ObjectGeneratorForClass;
import com.f1.utils.OH;
import com.f1.utils.RH;

public abstract class SimpleObjectGeneratorForClass<T> implements ObjectGeneratorForClass<T> {

	public SimpleObjectGeneratorForClass(Class<T> clazz) {
		this.type = clazz;
	}

	private Class<T> type;

	@Override
	public Class<T> askType() {
		return type;
	}

	@Override
	public T nw() {
		return nwCast(OH.EMPTY_CLASS_ARRAY, OH.EMPTY_OBJECT_ARRAY);
	}

	@Override
	public T nw(Object[] args) {
		return nwCast(RH.getClasses(args), args);
	}

	@Override
	public T nwCast(Class[] types, Object[] args) {
		Constructor<T> cons = RH.findConstructor(type, types);
		try {
			return cons.newInstance(args);
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}

}
