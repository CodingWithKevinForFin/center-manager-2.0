package com.f1.ami.center.table;

/**
 * A prepared row is used for inserting and updating records on a particular table. Similar to an {@link AmiPreparedQuery}, it's recommended that the prepared row is created at
 * startup, and then the various cells are populated at runtime before inserts and updates. You can reuse a prepared row after an insert/update. When doing multiple updates, it is
 * only necessary to clear/set those cells that are changing. Alternatively, you can use {@link #reset()} to clear out all fields and "begin" a new insert/update. Here are the
 * steps:
 * 
 * <PRE>
 * <B>A) Create the row (this should be done once at startup):</B><BR>
 * 1) Create the prepared row by calling {@link AmiTable#createAmiPreparedRow()} and store the returned row for future use 
 * <B>B) To execute the query (at runtime):</B>
 * 1)Optionally call {@link #reset()} to clear out all fields
 * 2)Set necessary values using the various set methods
 * 3.i)Insert using {@link AmiTable#insertAmiRow(AmiPreparedRow)} 
 * -- or --
 * 3.ii)Update using {@link AmiTable#updateAmiRow(long, AmiPreparedRow)} where the id (first param) is the id of the row to update, see
 * {@link AmiRow#getAmiId()}
 * 
 * </PRE>
 * <P>
 * NOTE: When updating, those fields not set (or cleared using {@link #unsetField(AmiColumn)}) are not affected in the insert. For clarity an "unset" field is different from a null
 * field, in that a null field will update the record to null and unset will leave the row's cell unchanged.
 * <P>
 * ANOTHER NOTE ON UPDATES: For Updating, you can alternatively just grab the {@link AmiRow} from the table and update values directly. This is faster, but lacks the
 * transactionality of using a {@link AmiPreparedRow}.
 * 
 * 
 */
public interface AmiPreparedRow extends AmiRow {

	/**
	 * Clear out all cells on this prepared row, after which {@link #isSet(AmiColumn)} will return false for all cells.
	 */
	public void reset();

	/**
	 * Test if a cell is unset (meaning it will not affect the target cell on update)
	 * 
	 * @param field
	 *            column of the cell to inspect
	 * @return true if set, false if not. Notice that if the cell has a value of null this WILL return true.
	 */
	public boolean isSet(AmiColumn cell);

	public boolean setString(int colpos, String value);
	public boolean setString(String col, String value);
	public boolean setString(AmiColumn col, String value);
	public boolean setLong(int colpos, long value);
	public boolean setLong(String col, long value);
	public boolean setLong(AmiColumn col, long value);
	public boolean setDouble(int colpos, double value);
	public boolean setDouble(String col, double value);
	public boolean setDouble(AmiColumn col, double value);
	public boolean setNull(int colpos);
	public boolean setNull(String col);
	public boolean setNull(AmiColumn col);
	public Object putAt(int i, Object value);
	public boolean setComparable(int colpos, Comparable value);
	public boolean setComparable(String col, Comparable value);
	public boolean setComparable(AmiColumn col, Comparable value);
}
