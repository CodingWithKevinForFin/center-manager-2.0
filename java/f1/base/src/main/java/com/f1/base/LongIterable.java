package com.f1.base;

/**
 * 
 * Collection containing doubles for iteration
 */
public interface LongIterable extends IterableAndSize<Long> {

	@Override
	public LongIterator iterator();
}
