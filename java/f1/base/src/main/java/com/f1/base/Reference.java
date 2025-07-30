/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * used to reference another object instance, or null.
 * 
 * @param <V>
 *            the type of the object to reference.
 * @see Pointer
 */
public interface Reference<V> {

	/**
	 * @return the referenced object. may be null
	 */
	public V get();
}
