/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

import java.util.List;

/**
 * 
 * An object that can have attached listeners that will be fired on as parameters on itself change value.
 */
public interface ValuedListenable {

	/**
	 * @param listener
	 *            listener to add. The listener should NOT be nested to other ValuedListenables. If the listener is added, {@link ValuedListener#onValuedAdded(Valued)} should be
	 *            called on the supplied listener.
	 * @return true if the listener was added (false would indicate it already existed)
	 */
	boolean addListener(ValuedListener listener);

	/**
	 * @param listener
	 *            listener to remove. The listener should also NOT be also removed from all nested ValuedListenables. If the listener is added,
	 *            {@link ValuedListener#onValuedAdded(Valued)} should be called on the supplied listener.
	 * @return true if the listener was added (false would indicate it already existed)
	 */
	boolean removeListener(ValuedListener listener);

	/**
	 * add a listener for changes to a particular value (specified by {@link PID})
	 * 
	 * @param field
	 * @param listener
	 */
	void addListener(byte field, ValuedListener listener);

	/**
	 * remove a listener for changes to a particular value(specified by {@link PID})
	 * 
	 * @param field
	 * @param listener
	 */
	void removeListener(byte field, ValuedListener listener);

	/**
	 * add a listener for changes to a particular value
	 * 
	 * @param field
	 * @param listener
	 */
	void addListener(String field, ValuedListener listener);

	/**
	 * remove a listener for changes to a particular value
	 * 
	 * @param field
	 * @param listener
	 */
	void removeListener(String field, ValuedListener listener);

	/**
	 * @return all attached listeners
	 */
	Iterable<ValuedListener> getValuedListeners();

	/**
	 * @return all attached listeners are dumped into sink
	 */
	void askChildValuedListenables(List<ValuedListenable> sink);

}
