/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Iterator2Iterable<V> implements Iterable<V> {

	private Iterator<V> iterator;

	public Iterator2Iterable(Enumeration<V> enumeration) {
		this(new EnumerationToIterator<V>(enumeration));
	}

	public Iterator2Iterable(Iterator<V> iterator) {
		this.iterator = iterator;
	}

	@Override
	public Iterator<V> iterator() {
		if (iterator == null)
			throw new NoSuchElementException();
		Iterator<V> r = iterator;
		iterator = null;
		return r;
	}

	public static <T> Iterable<T> toIterable(Enumeration<T> enumeration) {
		return new Iterator2Iterable<T>(enumeration);
	}
	public static <T> Iterable<T> toIterable(Iterator<T> it) {
		return new Iterator2Iterable<T>(it);
	}

}
