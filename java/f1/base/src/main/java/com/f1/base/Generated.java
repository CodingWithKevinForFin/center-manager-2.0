/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * Works in conjunction with {@link ObjectGenerator} and {@link ObjectGeneratorForClass}. When the generator creates a new instance of a class that implements this interface it
 * will call the {@link #construct(ObjectGenerator)} method
 * 
 */
public interface Generated {

	/**
	 * 
	 * Gives the Object a chance to construct inner(aka member) Generated instances.
	 * 
	 * @param generator
	 *            the generated the created this instance
	 */
	public void construct(ObjectGenerator generator);
}
