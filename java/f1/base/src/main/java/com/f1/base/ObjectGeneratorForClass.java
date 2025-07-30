/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * 
 * can generate (new) objects for a particular class type
 * 
 * @param <T>
 *            type of object to created
 */
public interface ObjectGeneratorForClass<T> extends Generator<T> {

	/**
	 * @return the type of object that can be created
	 */
	public Class<T> askType();

	/**
	 * @return return a new instance using the default constructor
	 */
	public T nw();

	/**
	 * return a new instance using a constructor based on the supplied fields' types
	 * 
	 * @param the
	 *            parameters to supply into the constructor
	 * @return new instance
	 */
	public T nw(Object[] args);

	/**
	 * return a new instance using a constructor based on the supplied types. Note length of types and args must match
	 * 
	 * @param types
	 *            hint for constructor to use
	 * @param args
	 *            the arguments to pass into the constructor
	 * @return the new instance
	 */
	public T nwCast(Class<?>[] types, Object[] args);

}
