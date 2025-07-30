/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * 
 * Something that can be numerically prioritized, usually messages, values range from 0 (highest) to 200(lowest) as a suggestion.
 * 
 */
public interface Prioritized {

	/**
	 * suggested "average" priority
	 */
	public static final int NORMAL_PRIORITY = 100;
	/**
	 * suggested "highets" priority
	 */
	public static final int HIGHEST_PRIORITY = 0;
	/**
	 * suggested "lowest" priority
	 */
	public static final int LOWEST_PRIORITY = 200;

	/**
	 * @return the priority as set by {@link #setPriority(int)}
	 */
	public int getPriority();

	/**
	 * @param priority
	 *            the priority of this instance.
	 */
	public void setPriority(int priority);

}
