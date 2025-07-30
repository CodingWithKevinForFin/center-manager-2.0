/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * A message (meaning it can be marshaled and go across the wire) with the additional property that not all fields need to be supplied aka exists (akin to undefined in javascript,
 * etc). Note the asking for a value that doesnt exist will return null, but internally there is a difference between not-existing and null. Likewise, setting a value to null is
 * different than not setting it at all. Not setting it means it doesn't exist and setting it to null means it exists but is null. Initially, a partial message has no existing
 * fields, and as fielods are set, they are marked as existing.
 * <P>
 * This is extremely useful for sending deltas where only a handful of values change.
 * 
 */
public interface PartialMessage extends Message, Clearable {

	/**
	 * @param name
	 *            the name of the fields
	 * @return true if the value has been set, aka exists
	 */
	public boolean askExists(String name);

	/**
	 * @param name
	 *            the pid of the fields
	 * @return true if the value has been set, aka exists
	 */
	public boolean askExists(byte pid);

	/**
	 * @return a list of pids that have been set.
	 */
	public ByteIterator askExistingPids();

	/**
	 * @return a list of valued params for all fields that have been set
	 */
	public Iterable<ValuedParam> askExistingValuedParams();

	/**
	 * @return unset a value (it no longer exists)
	 */
	public void removeValue(String name);

	/**
	 * @return unset a value (it no longer exists)
	 */
	public void removeValue(byte pid);

}
