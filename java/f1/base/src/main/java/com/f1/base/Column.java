/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * 
 * A Column of a table, which has a position type and identifier. Columns are owned by a {@link Table} instance
 */
public interface Column extends IterableAndSize {

	/**
	 * @return the base type of the values stored in this column
	 */
	public Class<?> getType();
	public Caster<?> getTypeCaster();

	/**
	 * @return the parent table of this column
	 */
	public Table getTable();

	/**
	 * @return zero based position(zero being left most)
	 */
	public int getLocation();

	/**
	 * @return the identifier of the column
	 */
	public String getId();

	/**
	 * @param location
	 *            zero based row in the table (zero being top)
	 * @return the value in the table at this column and given row
	 */
	public Object getValue(int location);

	public void setValue(int location, Object value);

}
