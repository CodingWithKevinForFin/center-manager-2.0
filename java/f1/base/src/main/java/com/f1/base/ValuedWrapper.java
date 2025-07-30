/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * 
 * A value object that "wraps" a single valued object.
 * 
 * @param <V>
 */
public interface ValuedWrapper<V extends Valued> extends Valued {

	/**
	 * sets the value to be wrapped
	 * 
	 * @param valued
	 */
	public void init(V valued);

	/**
	 * 
	 * @return the inner value
	 */
	public V getInner();

	/**
	 * @return the class type of the inner value.
	 */
	public Class<? extends Valued> getInnerType();

}
