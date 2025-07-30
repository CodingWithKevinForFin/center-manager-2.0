package com.f1.base;

/**
 * Collection containing doubles for iteration
 */
public interface IntIterable extends IterableAndSize<Integer> {

	@Override
	public IntIterator iterator();
}
