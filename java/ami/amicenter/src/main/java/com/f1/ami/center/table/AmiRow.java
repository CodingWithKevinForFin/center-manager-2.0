package com.f1.ami.center.table;

import java.util.Map;

import com.f1.base.CalcFrame;
import com.f1.utils.structs.table.stack.CalcFrameStack;

/**
 * Represents a logical row within a table. Note, the special {@link AmiPreparedRow}, which is used for inserting and updating into {@link AmiTable}s. When getting/setting values
 * on cells, you should consider the type of value the column represents. See {@link AmiColumn#getAmiType()}.
 * <P>
 * Special Note on AmiID: Each row within the IMDB (In Memory Database) will have a unique AmiID. All rows within an IMBD are unique. Note, this is wider than even a table. In
 * other words: two rows, even within DIFFERENT {@link AmiTable}s, are guaranteed to have distinct AmiIDs.
 * <P>
 * It is generally recommended that you do not hold onto references of {@link AmiRow}s after a callback is complete in case the row were deleted at some later time. (This is not
 * true of {@link AmiPreparedRow}s)
 * <P>
 * "Visibility" concept: Rows, by default, are visible. This means that as values change, the contents will be (eventually) published to external subscribers, including the
 * frontend. <BR>
 * (A) If you change the visibility from false to true, subscribers will receive a "new" for the record<BR>
 * (B) If you change the visibility from true to false, subscribers will receive a "delete" for the record<BR>
 * (C) If you change cells for a non-visible row, updates will not be published<BR>
 * (D) If you change cells for a visible row, updates will be published<BR>
 * (E) See {@link #setVisible(boolean)} and {@link #getVisible()} for details
 */
public interface AmiRow extends Map<String, Object>, CalcFrame {

	/**
	 * @return The table that this row belongs to.
	 */
	public AmiTable getAmiTable();

	/**
	 * Get a cell's value from this row as a string.
	 * 
	 * @param colpos
	 *            the zero-indexed position of the cell to return
	 * @return return a string representation at the specified column
	 */
	public String getString(int colpos);
	/**
	 * Get a cell's value from this row as a string. <B>THIS IS SLOW: Get a reference to the {@link AmiColumn} at startup and use the version of this getter that takes the
	 * {@link AmiColumn} instead to avoid an unnecessary column lookup.</B>
	 * 
	 * @param col
	 *            name of the column of the cell to return
	 * @return return a string representation at the specified column
	 */
	public String getString(String col);
	/**
	 * Get a cell's value from this row as a string.
	 * 
	 * @param colpos
	 *            the column of the cell to return
	 * @return return a string representation at the specified column
	 */
	public String getString(AmiColumn col);
	/**
	 * Set the value to the cell for this row at specified column to supplied value.
	 * 
	 * @param colpos
	 *            the zero-indexed position of the cell to set the value for
	 * @param value
	 *            the value to set the cell to
	 */
	public boolean setString(int colpos, String value, CalcFrameStack fs);
	/**
	 * Set the value to the cell for this row at specified column to supplied value. <B>THIS IS SLOW: Get a reference to the {@link AmiColumn} at startup and use the version of
	 * this setter that takes the {@link AmiColumn} instead to avoid an unnecessary column lookup.</B>
	 * 
	 * @param colpos
	 *            the name of the column of the cell to set the value for
	 * @param value
	 *            the value to set the cell to
	 */
	public boolean setString(String col, String value, CalcFrameStack fs);
	/**
	 * Set the value to the cell for this row at specified column to supplied value.
	 * 
	 * @param colpos
	 *            the column of the cell to set the value for
	 * @param value
	 *            the value to set the cell to
	 */
	public boolean setString(AmiColumn col, String value, CalcFrameStack fs);

	/**
	 * Get a cell's value from this row as a long. Note, test for null first using {@link IsNullG
	 * 
	 * @param colpos
	 *            the zero-indexed position of the cell to return
	 * @return return a long representation at the specified column
	 */
	public long getLong(int colpos);
	/**
	 * Get a cell's value from this row as a long. Note, test for null first using IsNull. <B>THIS IS SLOW: Get a reference to the {@link AmiColumn} at startup and use the version
	 * of this getter that takes the {@link AmiColumn} instead to avoid an unnecessary column lookup.</B>
	 * 
	 * @param col
	 *            name of the column of the cell to return
	 * @return return a long representation at the specified column
	 */
	public long getLong(String col);
	/**
	 * Get a cell's value from this row as a long. Note, test for null first using IsNull.
	 * 
	 * @param colpos
	 *            the column of the cell to return
	 * @return return a long representation at the specified column
	 */
	public long getLong(AmiColumn col);

	/**
	 * Set the value to the cell for this row at specified column to supplied value.
	 * 
	 * @param colpos
	 *            the zero-indexed position of the cell to set the value for
	 * @param value
	 *            the value to set the cell to
	 */
	public boolean setLong(int colpos, long value, CalcFrameStack fs);
	/**
	 * Set the value to the cell for this row at specified column to supplied value. <B>THIS IS SLOW: Get a reference to the {@link AmiColumn} at startup and use the version of
	 * this setter that takes the {@link AmiColumn} instead to avoid an unnecessary column lookup.</B>
	 * 
	 * @param colpos
	 *            the name of the column of the cell to set the value for
	 * @param value
	 *            the value to set the cell to
	 */
	public boolean setLong(String col, long value, CalcFrameStack fs);
	/**
	 * Set the value to the cell for this row at specified column to supplied value.
	 * 
	 * @param colpos
	 *            the column of the cell to set the value for
	 * @param value
	 *            the value to set the cell to
	 */
	public boolean setLong(AmiColumn col, long value, CalcFrameStack fs);

	/**
	 * Get a cell's value from this row as a double. Note, test for null first using IsNull.
	 * 
	 * @param colpos
	 *            the zero-indexed position of the cell to return
	 * @return return a double representation at the specified column
	 */
	public double getDouble(int colpos);

	/**
	 * Get a cell's value from this row as a double. Note, test for null first using IsNull. <B>THIS IS SLOW: Get a reference to the {@link AmiColumn} at startup and use the
	 * version of this getter that takes the {@link AmiColumn} instead to avoid an unnecessary column lookup.</B>
	 * 
	 * @param col
	 *            name of the column of the cell to return
	 * @return return a double representation at the specified column
	 */
	public double getDouble(String col);

	/**
	 * Get a cell's value from this row as a double. Note, test for null first using {IsNull}.
	 * 
	 * @param colpos
	 *            the column of the cell to return
	 * @return return a double representation at the specified column
	 */
	public double getDouble(AmiColumn col);

	/**
	 * Set the value to the cell for this row at specified column to supplied value.
	 * 
	 * @param colpos
	 *            the zero-indexed position of the cell to set the value for
	 * @param value
	 *            the value to set the cell to
	 */
	public boolean setDouble(int colpos, double value, CalcFrameStack fs);
	/**
	 * Set the value to the cell for this row at specified column to supplied value. <B>THIS IS SLOW: Get a reference to the {@link AmiColumn} at startup and use the version of
	 * this setter that takes the {@link AmiColumn} instead to avoid an unnecessary column lookup.</B>
	 * 
	 * @param colpos
	 *            the name of the column of the cell to set the value for
	 * @param value
	 *            the value to set the cell to
	 */
	public boolean setDouble(String col, double value, CalcFrameStack fs);
	/**
	 * Set the value to the cell for this row at specified column to supplied value.
	 * 
	 * @param colpos
	 *            the column of the cell to set the value for
	 * @param value
	 *            the value to set the cell to
	 */
	public boolean setDouble(AmiColumn col, double value, CalcFrameStack fs);

	/**
	 * Test if a cell's value from this row is null.
	 * 
	 * @param colpos
	 *            the zero-indexed position of the cell to test
	 * @return return true if null, false if not null
	 */
	public boolean getIsNull(int colpos);

	/**
	 * Test if a cell's value from this row is null. Note, test for null first using IsNull. <B>THIS IS SLOW: Get a reference to the {@link AmiColumn} at startup and use the
	 * version of this getter that takes the {@link AmiColumn} instead to avoid an unnecessary column lookup.</B>
	 * 
	 * @param colpos
	 *            the zero-indexed position of the cell to test
	 * @return return true if null, false if not null
	 */
	public boolean getIsNull(String col);

	/**
	 * Test if a cell's value from this row is null.
	 * 
	 * @param col
	 *            the zero-indexed position of the cell to test
	 * @return return true if null, false if not null
	 */
	public boolean getIsNull(AmiColumn col);

	/**
	 * Set the value to null for this row at specified column.
	 * 
	 * @param colpos
	 *            the zero-indexed position of the cell to set to null
	 */
	public boolean setNull(int colpos, CalcFrameStack fs);
	/**
	 * Set the value to null for this row at specified column.
	 * 
	 * @param colpos
	 *            the name of the column of the cell to set to null
	 */
	public boolean setNull(String col, CalcFrameStack fs);
	/**
	 * Set the value to null for this row at specified column. <B>THIS IS SLOW: Get a reference to the {@link AmiColumn} at startup and use the version of this getter that takes
	 * the {@link AmiColumn} instead to avoid an unnecessary column lookup.</B>
	 * 
	 * @param colpos
	 *            the column of the cell to set to null
	 */
	public boolean setNull(AmiColumn col, CalcFrameStack fs);

	/**
	 * Return if a cell's value from this row is set.
	 * 
	 * @param colpos
	 *            the zero-indexed position of the cell to test
	 * @return return true if set, false if not set
	 */
	public boolean isSet(int colpos);

	/**
	 * @return the zero-indexed position of this row, ex: 0 is the "top row" and 1 is the second to top row, etc.
	 */
	public int getRowNum();

	/**
	 * @return the id representing this row. {@link AmiTable}s will have the same AmiId. See note at top for details about AmiIds
	 */
	long getAmiId();

	/**
	 * Make the row visible or hidden.
	 * 
	 * @param b
	 *            true=visible, false = hidden
	 */
	void setVisible(boolean b);

	/**
	 * Test if the row is visible or hidden.
	 * 
	 * @return true=visible, false = hidden
	 */
	boolean getVisible();

	public Object putAt(int i, Object object, CalcFrameStack fs);

	public Comparable getComparable(AmiColumn amiColumn);
	public Comparable getComparable(int col);
	public Comparable getComparable(String col);

	public boolean setComparable(AmiColumn columnAt, Comparable value, CalcFrameStack fs);
	public boolean setComparable(int col, Comparable value, CalcFrameStack fs);
	public boolean setComparable(String col, Comparable value, CalcFrameStack fs);

}
