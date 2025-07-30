package com.f1.base;

/**
 * Implementing this interface allows an object to be the target of the "foreach" statement and also get the size (something that practically all iterables know)
 * 
 */
public interface IterableAndSize<T> extends Iterable<T> {

	/**
	 * @return number of elements to be iterated over, aka the size of the collection this represents
	 */
	public int size();
}
