/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

import java.util.List;

/**
 * Data structure representing a list or rows.
 * 
 * @see Table
 * @see Row
 * 
 */

public interface TableList extends List<Row> {

	/**
	 * Adds a row to the bottom of the table. The values array size must match the columns count and column types. This does not clone the array. Don't modify elements after entry
	 */
	public Row addRow(Object... values);

	/**
	 * Adds a row to the supplied position(0 is the top). The values array size must match the columns count and column types. This does not clone the array. Don't modify elements
	 * after entry
	 */
	public Row insertRow(int rowPos, Object... values);

	/**
	 * Adds all of the rows to the end of the table
	 */
	public void addAll(Iterable<? extends Row> values);

	public Row[] toRowsArray();

	public long getLongSize();//This is same as size() but supports > billion

}
