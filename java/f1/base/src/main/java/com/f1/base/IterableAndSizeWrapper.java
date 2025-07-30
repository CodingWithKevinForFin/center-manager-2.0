package com.f1.base;

import java.util.Collection;
import java.util.Iterator;

public class IterableAndSizeWrapper<T> implements IterableAndSize<T> {

	final private Collection<T> inner;

	public IterableAndSizeWrapper(Collection<T> inner) {
		this.inner = inner;
	}

	@Override
	public Iterator<T> iterator() {
		return inner.iterator();
	}

	@Override
	public int size() {
		return inner.size();
	}

}
