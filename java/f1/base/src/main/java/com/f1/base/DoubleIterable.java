package com.f1.base;

/**
 * 
 * Collection containing doubles for iteration
 */
public interface DoubleIterable extends IterableAndSize<Double> {

	@Override
	public DoubleIterator iterator();
}
