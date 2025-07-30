/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.Iterator;

import com.f1.base.IterableAndSize;

public class EmptyIterable<I> implements IterableAndSize<I> {

	public static final EmptyIterable INSTANCE = new EmptyIterable();

	@Override
	public Iterator<I> iterator() {
		return EmptyIterator.INSTANCE;
	}

	@Override
	public int size() {
		return 0;
	}

}
