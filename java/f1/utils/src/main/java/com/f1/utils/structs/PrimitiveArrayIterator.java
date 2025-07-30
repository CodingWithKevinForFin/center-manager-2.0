/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * creates an iterator of an array
 * 
 * @param <T>
 */
public class PrimitiveArrayIterator<T> implements Iterator<T>, Iterable<T> {

	private Object values;
	private int i = 0;
	private int length;

	public PrimitiveArrayIterator(Object values) {
		this.values = values;
		this.length = Array.getLength(values);
	}

	@Override
	public boolean hasNext() {
		return i < length;
	}

	@Override
	public T next() {
		try {
			return (T) Array.get(values, i++);
		} catch (IndexOutOfBoundsException e) {
			NoSuchElementException r = new NoSuchElementException();
			r.initCause(e);
			throw r;
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<T> iterator() {
		return this;
	}

}
