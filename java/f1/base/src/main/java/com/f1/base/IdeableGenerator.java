/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * extends the functionality of the object generator for supporting the ability to identify classes based on there "class ID" as outlined in the {@link Ideable} interface.
 */
public interface IdeableGenerator extends ObjectGenerator {

	/**
	 * create a class based on the class's integer id(see {@link Ideable}
	 * 
	 * @param <C>
	 *            type of class
	 * @param ideableId
	 *            the id of the class to create
	 * @return the newly created class
	 */
	public <C> C nw(long ideableId);

	/**
	 * create a class based on the class's string id(see {@link Ideable}
	 * 
	 * @param <C>
	 *            type of class
	 * @param ideableId
	 *            the id of the class to create
	 * @return the newly created class
	 */
	public <C> C nw(String ideableName);

	/**
	 * Register classes so that they can be resolved by there id
	 * 
	 * @param classes
	 *            classes (presumably extending {@link Ideable}) to register
	 */
	public void register(final Class... classes);

	/**
	 * @return Unordered list of all classes known to this generator
	 */
	public Iterable<Class> getRegistered();

}
