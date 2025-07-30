package com.f1.utils;

import com.f1.base.Caster;
import com.f1.base.Getter;

public class CastGetter<F, T> implements Getter<F, T> {

	final private Caster<T> caster;

	public CastGetter(Class<T> toType) {
		this.caster = OH.getCaster(toType);
	}
	@Override
	public T get(Object key) {
		return this.caster.cast(key, false, false);
	}

}
