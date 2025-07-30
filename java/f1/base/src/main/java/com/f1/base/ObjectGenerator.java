/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * Used for creating new objects based on a supplied class and potentially arguments. Using an Object Generator instead of just simply using the reserved word "new" allows for
 * flexibility in terms of substitution, code generation, testing and object pooling. <BR>
 * For example, if you use nw(ArrayList.class) through out your code then down the road decided that you have a better implementation of Arraylist, one could easily configura an
 * object generator to substitute the new and improved version.
 * <P>
 * Please note, that when the word "new" is used in the javadoc below that it may not be truly new if the object generator is backed by an object pool. Regardless, it should be a
 * new instance in that noone else is using it
 */
public interface ObjectGenerator extends Factory<Class<?>, Object> {

	/**
	 * Basic implementation. return a new instanceof the supplied class type. The class must have a default (empty) constructor which will be invoked.
	 * 
	 * @param <C>
	 *            type of class to return
	 * @param classs
	 *            type of class
	 * @return new isntance of the supplied class.
	 */
	public <C> C nw(Class<C> classs);

	/**
	 * similar to the {@link #nw(Class)} except operates on a list of classes (for performance)
	 * 
	 * @param the
	 *            list of classes to create.
	 * @return an array of instances (in the same order as the supplied classes)
	 */
	public Object[] nw(final Class<?>... classs);

	/**
	 * Create a new instance of the supplied class using a constructor with a param definition compatible with the supplied constructor Parameters.
	 * 
	 * @param <C>
	 *            type of class to return
	 * @param classs
	 *            type of class
	 * @param constructorParameters
	 *            the arguments to identify which constructor to call, and will be passed into the constructor.
	 * @return new instance of the supplied class.
	 */
	public <C> C nw(Class<C> classs, Object... constructorParameters);

	/**
	 * Create a new instance of the supplied class using a constructor with a param definition compatible with the supplied argument types.
	 * 
	 * @param <C>
	 *            type of class to return
	 * @param classs
	 *            type of class
	 * @param argumentTypes
	 *            used to identify which constructor to call.
	 * @param constructorParameters
	 *            the arguments to be passed into the constructor (must be compatible w/ the argumentTypes)
	 * @return new instance of the supplied class.
	 */
	public <C> C nwCast(Class<C> classs, Class[] argumentTypes, Object[] constructorParameters);

	/**
	 * return an object generator specifically configured to generate classes of the supplied type. For creating many objects this will have better performance in that it avoids a
	 * look up.
	 * 
	 * @param <C>
	 *            type of class to create
	 * @param clazz
	 *            the type of class the returning generator should created
	 * @return the generator for the supplied class
	 */
	public <C> ObjectGeneratorForClass<C> getGeneratorForClass(Class<C> clazz);

}
