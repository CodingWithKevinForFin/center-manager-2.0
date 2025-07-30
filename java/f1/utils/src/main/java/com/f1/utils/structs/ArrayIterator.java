/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * creates an iterator of an array
 * 
 * @param <T>
 */
public class ArrayIterator<T> implements Iterator<T>, Iterable<T> {

	private T[] values;
	private int i = 0;
	private int valuesLength;

	public ArrayIterator(T[] values, int startInclusive, int endExclusive) {
		this.values = values;
		i = startInclusive;
		valuesLength = endExclusive;
	}
	public ArrayIterator(T[] values) {
		this(values, 0, values.length);
	}

	@Override
	public boolean hasNext() {
		return i < valuesLength;
	}

	@Override
	public T next() {
		try {
			return values[i++];
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
