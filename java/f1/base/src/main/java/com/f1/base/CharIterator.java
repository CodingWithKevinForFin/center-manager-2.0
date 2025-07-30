package com.f1.base;

import java.util.Iterator;

/**
 * Simple iterator for getting chars, but bypassed the auto boxing that would be necessary for the generic iterator
 */
public interface CharIterator extends Iterator<Character> {

	/**
	 * @return next char as a primitive, see next() except that null will throw an exception
	 */
	public char nextChar();
}
