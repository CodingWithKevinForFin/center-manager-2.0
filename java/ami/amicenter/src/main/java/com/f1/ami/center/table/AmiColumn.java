package com.f1.ami.center.table;

import com.f1.utils.structs.table.stack.CalcFrameStack;

/**
 * Represent a column within a table (this is actually where the data is stored, but that's not important). Columns are typed, and the type can be discovered using
 * {@link #getAmiType()}. A column belongs to an {@link AmiTable}. When getting/setting values it's important to use the appropriate get/set method depending on the ype the column
 * represents.
 * 
 * <BR>
 * Depending on the column type, this is the get/set values you should use
 * 
 * <pre>
 * AmiTable.TYPE_LONG: getLong, setLong, getIsNull, setNull AmiTable.TYPE_INT: getLong, setLong, getIsNull, setNull AmiTable.TYPE_DOUBLE: getDouble, setDouble, getIsNull, setNull
 * AmiTable.TYPE_FLOAT: getDouble, setDouble, getIsNull, setNull AmiTable.TYPE_STRING: getString, setString AmiTable.PARAM_TYPE_ENUM3: getString, setString AmiTable.TYPE_BOOLEAN:
 * getLong, setLong, getIsNull, setNull AmiTable.TYPE_UTC: getLong, setLong, getIsNull, setNull AmiTable.TYPE_NONE: N/A
 */
public interface AmiColumn {

	/**
	 * @return the table this column belongs to
	 */
	AmiTable getAmiTable();

	/**
	 * @return Name of this column
	 */
	String getName();

	/**
	 * 
	 * @return type, set AmiTable.TYPE*
	 */
	byte getAmiType();

	/**
	 * @return 0 indexed position of this column within the table (0=left most, 1=2nd to left, etc)
	 */
	int getLocation();

	/**
	 * gets the String value at the cell represented by the supplied row at this column
	 * 
	 * @param row
	 *            the row to get value for
	 * @return the string representation of the value at this column in the supplied row, or null
	 */
	public String getString(AmiRow row);

	/**
	 * gets the long value at the cell represented by the supplied row at this column. IMPORTANT check if null first using getIsNull
	 * 
	 * @param row
	 *            the row to get value for
	 * @return the long representation of the value at this column in the supplied row
	 * @throws NullPointerException
	 *             if the value at this cell is null
	 */
	public long getLong(AmiRow row);

	/**
	 * gets the double value at the cell represented by the supplied row at this column. IMPORTANT check if null first using getIsNull
	 * 
	 * @param row
	 *            the row to get value for
	 * @return the double representation of the value at this column in the supplied row
	 * @throws NullPointerException
	 *             if the value at this cell is null
	 */
	public double getDouble(AmiRow row);

	/**
	 * see if the value at the cell represented by the supplied row at this column is null.
	 * 
	 * @param row
	 * @return true if null, otherwise false
	 */
	public boolean getIsNull(AmiRow row);

	/**
	 * set the value at the cell represented by this column at supplied row to supplied value
	 * 
	 * @param row
	 *            the row to set value to null for
	 * @param value
	 *            the value to store in cell
	 * @throws ClassCastException
	 *             if the supplied string is not compatible with this columns type
	 */
	public boolean setString(AmiRow row, String value, CalcFrameStack fs);
	/**
	 * set the value at the cell represented by this column at supplied row to supplied value
	 * 
	 * @param row
	 *            the row to set value to null for
	 * @param value
	 *            the value to store in cell
	 */
	public boolean setLong(AmiRow row, long value, CalcFrameStack fs);

	/**
	 * set the value at the cell represented by this column at supplied row to supplied value
	 * 
	 * @param row
	 *            the row to set value to null for
	 * @param value
	 *            the value to store in cell
	 */
	public boolean setDouble(AmiRow row, double value, CalcFrameStack fs);

	/**
	 * set the value at the cell represented by this column at supplied row to null
	 * 
	 * @param row
	 *            the row to set value to null for
	 */
	public boolean setNull(AmiRow row, CalcFrameStack fs);

	/**
	 * Fast function for copying the value from a cell (potentially in another table) into this table. Source cell is identified by fromCol+fromRow, target cell is identified by
	 * this column+toRow
	 * 
	 * @param toRow
	 *            row to copy to
	 * @param fromCol
	 *            col to copy from
	 * @param fromRow
	 *            row to copy from
	 */
	public boolean copyToFrom(AmiRow toRow, AmiColumn fromCol, AmiRow fromRow, CalcFrameStack fs);

	/**
	 * Use with caution, this is expensive. Returns an Object version of the value at the cells in the supplied row at this column.
	 * 
	 * @param row
	 *            the row to get value for
	 * @return may be null if cell is null
	 */
	Comparable getComparable(AmiRow row);

	boolean areEqual(AmiRow row, AmiColumn col2, AmiRow row2);

	boolean setComparable(AmiRow amiRowImpl, Comparable value, CalcFrameStack fs);

	boolean getIsOnDisk();

	boolean getAllowNull();

	long getColumnPositionMask0();

	long getColumnPositionMask64();

	byte getReservedType();

}
