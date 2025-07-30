/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * Useful for reuse. Calling clear should put the implementing object back to the original state (after construction).
 * <P>
 * This is used for objects that change put when there done
 * 
 */
public interface Clearable {

	/**
	 * reset object
	 */
	public void clear();

}
