package com.f1.base;

import java.util.Iterator;

/**
 * Simple iterator for getting doubles, but bypassed the auto boxing that would be necessary for the generic iterator
 */

public interface DoubleIterator extends Iterator<Double> {

	/**
	 * @return next double as a primitive, see next() except that null will throw an exception
	 */
	public double nextDouble();
}
