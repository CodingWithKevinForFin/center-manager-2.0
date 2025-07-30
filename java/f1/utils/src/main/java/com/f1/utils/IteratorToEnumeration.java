/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.Enumeration;
import java.util.Iterator;

public class IteratorToEnumeration<V> implements Enumeration<V> {

	private final Iterator<V> iterator;

	public IteratorToEnumeration(Iterator<V> iterator) {
		this.iterator = iterator;
	}

	@Override
	public boolean hasMoreElements() {
		return iterator.hasNext();
	}

	@Override
	public V nextElement() {
		return iterator.next();
	}

}
