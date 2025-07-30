/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * enhances the {@link Reference} interface by allowing for the object reference to be mutated.
 * 
 * @param <V>
 *            the type of object being referenced
 */
public interface Pointer<V> extends Reference<V> {

	/**
	 * update the object referenced
	 * 
	 * @param value
	 *            the object to reference, may be null
	 * @return the old value being referenced. (see {@link Reference#get()} )
	 */
	public V put(V value);

}
