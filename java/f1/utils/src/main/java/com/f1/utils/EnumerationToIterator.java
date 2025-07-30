/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.Enumeration;
import java.util.Iterator;

public class EnumerationToIterator<V> implements Iterator<V> {

	private final Enumeration<V> enumeration;

	public EnumerationToIterator(Enumeration<V> enumeration) {
		if (enumeration == null)
			throw new NullPointerException();
		this.enumeration = enumeration;
	}

	@Override
	public boolean hasNext() {
		return enumeration.hasMoreElements();
	}

	@Override
	public V next() {
		return enumeration.nextElement();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
