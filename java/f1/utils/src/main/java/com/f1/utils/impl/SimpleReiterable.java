package com.f1.utils.impl;

import java.util.Iterator;

import com.f1.utils.Reiterable;

public class SimpleReiterable<V> implements Reiterable<V> {

	private final Iterable<V> inner;

	public SimpleReiterable(Iterable<V> inner) {
		if (inner == null)
			throw new NullPointerException("inner");
		this.inner = inner;
	}

	@Override
	public Iterator<V> iterator() {
		return inner.iterator();
	}

	@Override
	public Iterator<V> iterator(Iterator<V> iterator) {
		return inner.iterator();
	}

}
