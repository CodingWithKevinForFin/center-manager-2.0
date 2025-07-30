package com.f1.utils.ids;

import com.f1.base.Factory;
import com.f1.base.Getter;
import com.f1.utils.impl.BasicFactoryCache;

public class BasicNamespaceIdGenerator<T> implements NamespaceIdGenerator<T> {

	private Getter<String, IdGenerator<T>> getter;

	public BasicNamespaceIdGenerator(Factory<String, ? extends IdGenerator<T>> factory) {
		this.getter = new BasicFactoryCache<String, IdGenerator<T>>(factory);
	}

	@Override
	public IdGenerator<T> getIdGenerator(String nameSpace) {
		return getter.get(nameSpace);
	}

	@Override
	public T createNextId(String nameSpace) {
		return getIdGenerator(nameSpace).createNextId();
	}

}
