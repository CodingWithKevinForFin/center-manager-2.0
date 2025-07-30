/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SingletonIterator<I> implements Iterator<I> {
	private static final Object CALLED = new Object();
	private Object value;

	public SingletonIterator(I value) {
		super();
		this.value = value;
	}

	@Override
	public boolean hasNext() {
		return value != CALLED;
	}

	@Override
	public I next() {
		Object r = value;
		if (r == CALLED)
			throw new NoSuchElementException();
		value = CALLED;
		return (I) r;
	}

	public Iterator<I> reset(I value) {
		this.value = value;
		return this;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
