/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * 
 * This defines the schema for a Valued object. When the f1 autocoder creates a class it also creates a schema definition (much like the JVM creates a class instance for each
 * class). Note: only one {@link ValuedSchema} is created per autocoded class instance (not object instance). <BR>
 * Unlike the java class and reflections, this is designed to be extremely fast for iterating through the parameters of an autocoded object. Each param (backed by a get/set) is
 * represented by a {@link ValuedParam}. DO NOT MODIFY THE CONTENTS OF THE RETURNED ARRAY
 * <P>
 * All parameters can be identified by their name, or location. For classes supporting {@link PID}s, they can also be identified by pid.
 * <P>
 * 
 * Note: the ask prefix is the same as get but functionally but avoids the bean-ing.
 * 
 * @param <V>
 *            Type of class this schema represents
 */
public interface ValuedSchema<V extends Valued> {

	/**
	 * @return the original interface that this schema represents
	 */
	Class<? extends V> askOriginalType();

	/**
	 * @return number of parameters (getter/setters) on the class
	 */
	int askParamsCount();

	/**
	 * @return Does this class supported {@link PID}s, meaning do the parameters have PID notations.
	 */
	boolean askSupportsPids();

	/**
	 * @return Ordered Name of all the getter params... Note the name is bean compatible
	 */
	String[] askParams();

	/**
	 * @return Ordered params...
	 */
	ValuedParam<V>[] askValuedParams();

	/**
	 * @return Orders list of {@link PID}. See {@link #askSupportsPids()}
	 */
	byte[] askPids();

	/**
	 * @return the type returned by the corresponding getter and supplied for corresponding setter
	 */
	Class<?> askClass(String name);

	/**
	 * @return Similar to {@link #askClass(String)} but return an enum from the {@link BasicTypes}
	 * 
	 */
	byte askBasicType(String name);

	/**
	 * @return the ValuedParam
	 */
	ValuedParam<V> askValuedParam(String name);

	/**
	 * @return Ordered position of param
	 */
	int askPosition(String name);

	/**
	 * @return is the name a valid param
	 */
	boolean askParamValid(String param);

	/**
	 * @return same as {@link #askClass(String)} but for pids
	 */
	Class<?> askClass(byte pid);

	/**
	 * @return same as {@link #askBasicType(String)} but for pids
	 */
	byte askBasicType(byte pid);

	/**
	 * @return same as {@link #askValuedParam(String)} but for pids
	 */
	ValuedParam<V> askValuedParam(byte pid);

	/**
	 * @return same as {@link #askPosition(String)} but for pids
	 */
	int askPosition(byte pid);

	/**
	 * @return same as {@link #askPidValid(String)} but for pids
	 */
	boolean askPidValid(byte pid);

	/**
	 * @return get the pid for a given param name
	 */
	byte askPid(String param);

	/**
	 * @return get the param name for a given PID
	 */
	String askParam(byte pid);

}
