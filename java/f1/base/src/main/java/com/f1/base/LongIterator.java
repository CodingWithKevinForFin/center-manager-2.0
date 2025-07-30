package com.f1.base;

import java.util.Iterator;

/**
 * Simple iterator for getting longs, but bypassed the auto boxing that would be necessary for the generic iterator
 */
public interface LongIterator extends Iterator<Long> {

	/**
	 * @return next long as a primitive, see next() except that null will throw an exception
	 */
	public long nextLong();
}
