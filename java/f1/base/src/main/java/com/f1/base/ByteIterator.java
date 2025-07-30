package com.f1.base;

import java.util.Iterator;

/**
 * Simple iterator for getting bytes, but bypassed the auto boxing that would be necessary for the generic iterator
 */
public interface ByteIterator extends Iterator<Byte> {

	/**
	 * @return next byte as a primitive, see next() except that null will throw an exception
	 */
	public byte nextByte();
}
