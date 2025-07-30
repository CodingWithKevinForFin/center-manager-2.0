/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * 
 * Listen for changes to a {@link Table}
 */
public interface TableListener {

	/**
	 * fired when a cell changes.
	 * 
	 * @param row
	 *            row containing the cell
	 * @param cell
	 *            column location of the cell within the supplied row
	 * @param oldValue
	 *            old value of the cell
	 * @param newValue
	 *            new value of the cell
	 */
	public void onCell(Row row, int cell, Object oldValue, Object newValue);

	/**
	 * fired when a column is added
	 */
	public void onColumnAdded(Column nuw);

	/**
	 * fired when a column is removed
	 */
	public void onColumnRemoved(Column old);

	/**
	 * fired when a column is changed
	 */
	public void onColumnChanged(Column old, Column nuw);

	/**
	 * fired when a row is added
	 */
	public void onRowAdded(Row add);

	/**
	 * fired when a row is removed
	 */
	public void onRowRemoved(Row removed, int index);

}
