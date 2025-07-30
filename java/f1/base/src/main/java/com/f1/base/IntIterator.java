package com.f1.base;

import java.util.Iterator;

/**
 * Simple iterator for getting ints, but bypassed the auto boxing that would be necessary for the generic iterator
 */
public interface IntIterator extends Iterator<Integer> {
	/**
	 * @return next int as a primitive, see next() except that null will throw an exception
	 */
	public int nextInt();
}