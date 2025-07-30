/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

/**
 * Represents the access point to the 'root suite' (aka the top level suite
 * which has not parent itself).
 * 
 * @see Suite
 * @see Container#getSuiteController()
 */
public interface SuiteController extends ContainerScope {
	/**
	 * @return the root level suite associated with this container. never null
	 */
	public Suite getRootSuite();
}
