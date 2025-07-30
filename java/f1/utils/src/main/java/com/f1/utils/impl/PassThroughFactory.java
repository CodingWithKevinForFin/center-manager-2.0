package com.f1.utils.impl;

import com.f1.base.Factory;

public class PassThroughFactory<T> implements Factory<T, T> {

	static final public PassThroughFactory INSTANCE = new PassThroughFactory();

	@Override
	public T get(T key) {
		return key;
	}

}
