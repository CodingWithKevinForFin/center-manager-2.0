/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.Iterator;

public class EmptyIterator<T> implements Iterator<T> {

	public static final EmptyIterator INSTANCE = new EmptyIterator();

	private EmptyIterator() {
	}
	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public T next() {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
