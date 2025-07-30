/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * represents a general interface for 'getting' a particular value based on a particular key. Implementations could vary, perhaps acting as a 'factory' (meaning a new object is
 * created everytime {@link #get(Object)} is called) or as a 'look up' meaning that a translation is done.
 * 
 * @param <K>
 *            the key used to get a value
 * @param <R>
 *            the value to return
 */
public interface Getter<K, R> {

	public R get(K key);

}
