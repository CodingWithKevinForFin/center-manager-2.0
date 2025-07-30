/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * 
 * A listener that will be fired on when a ValuedListenable being tracked is changed/added removed.
 * 
 */
public interface ValuedListener {

	/**
	 * A valued object has been added to another valued object being listened to by this listener.
	 */
	public void onValuedAdded(ValuedListenable target);

	/**
	 * A valued object has been removed from another valued object being listened to by this listener.
	 */
	public void onValuedRemoved(ValuedListenable target);

	/**
	 * A param's value has changed for a {@link ValuedListenable} being listened to by this listener.
	 */
	public void onValued(ValuedListenable target, String name, byte pid, Object old, Object value);

	/**
	 * A param's primitive value has changed for a {@link ValuedListenable} being listened to by this listener.
	 */
	public void onValuedBoolean(ValuedListenable target, String name, byte pid, boolean old, boolean value);

	/**
	 * A param's primitive value has changed for a {@link ValuedListenable} being listened to by this listener.
	 */
	public void onValuedByte(ValuedListenable target, String name, byte pid, byte old, byte value);

	/**
	 * A param's primitive value has changed for a {@link ValuedListenable} being listened to by this listener.
	 */
	public void onValuedChar(ValuedListenable target, String name, byte pid, char old, char value);

	/**
	 * A param's primitive value has changed for a {@link ValuedListenable} being listened to by this listener.
	 */
	public void onValuedShort(ValuedListenable target, String name, byte pid, short old, short value);

	/**
	 * A param's primitive value has changed for a {@link ValuedListenable} being listened to by this listener.
	 */
	public void onValuedInt(ValuedListenable target, String name, byte pid, int old, int value);

	/**
	 * A param's primitive value has changed for a {@link ValuedListenable} being listened to by this listener.
	 */
	public void onValuedLong(ValuedListenable target, String name, byte pid, long old, long value);

	/**
	 * A param's primitive value has changed for a {@link ValuedListenable} being listened to by this listener.
	 */
	public void onValuedFloat(ValuedListenable target, String name, byte pid, float old, float value);

	/**
	 * A param's primitive value has changed for a {@link ValuedListenable} being listened to by this listener.
	 */
	public void onValuedDouble(ValuedListenable target, String name, byte pid, double old, double value);
}
