/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * 
 * A {@link Table} that can be listened to by a {@link TableListener}. As changes take place this instance should fire updates on all attahced listeners.
 */
public interface TableListenable extends Table {

	/**
	 * Add a listener to the table.
	 */
	public void addTableListener(TableListener listener);

	/**
	 * Remove a listener from the table.
	 */
	public void removeTableListener(TableListener listener);
}
