/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * 
 * An object that can be conflated (latest one overwrites the existing value). Only items with the same conflating key should be
 * 
 */
public interface Conflatable {

	/**
	 * @return The key that will be used for conflation
	 */
	public Object getConflatingKey();

	/**
	 * 
	 * @param conflatingKeyThe
	 *            key that will be used for conflation
	 */
	public void setConflatingKey(Object conflatingKey);
}
