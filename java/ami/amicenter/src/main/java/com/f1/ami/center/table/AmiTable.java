package com.f1.ami.center.table;

import java.util.List;
import java.util.Set;

import com.f1.ami.amicommon.msg.AmiDataEntity;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.center.table.persist.AmiTablePersisterBinding;
import com.f1.ami.center.triggers.AmiTrigger;
import com.f1.ami.center.triggers.AmiTriggerBindingImpl;
import com.f1.utils.DetailedException;
import com.f1.utils.structs.table.stack.CalcFrameStack;

/**
 * A 2-dimensional data structure for storing, searching, and updating data. Similar to database table. Columns are represented by {@link AmiColumn}s and rows are represented by
 * {@link AmiRow}s. Columns are generally fixed at startup, and rows are inserted, updated, and deleted at runtime. Note that columns have a well-defined ordering/positioning and
 * each column is typed. All columns have a unique name within the table. Each table has a unique name within the {@link AmiImdb}. A table must have at least one column, but may
 * have no rows.<BR>
 * Tables contain {@link AmiIndex}es that are used for fast queries.<BR>
 * Tables can have {@link AmiTrigger}s registered, which will be notified of insert/update/deletes.<BR>
 * <P>
 * <I>Implementation Note: Tables are columnar, but use specialized structures to enable fast deletes. For tables that are configured for broadcasting (this is the default),
 * specialized conflation techniques are used to publish deltas.</I>
 * 
 * <P>
 * Specialized columns:
 * 
 * <PRE>
 * There are 7 pre-defined column names that have specialized usage. These are for inter-operability with the classic AMI dynamic tables.
 * I - object id a unique id for the rows in this table (in combination with the P value)
 * P - application id (I value on login instruction, this can be thought of as a namespace)
 * E - expire time - when the record should automatically be deleted on some timed event
 * V - revision - auto incremented with updates
 * M - last modified on (in milliseconds)
 * C - created on (in milliseconds)
 * D - AMI id (this value will be the same as {@link AmiRow#getAmiId()}
 * </PRE>
 * 
 * Note on Primary Key. For backwards compatibility with "dynamic AMI tables" all AmiTables support the specialized "I" column and "P" indexing. In other words, if you supply an I
 * and P column in the table definition then a primary-composite key will be created on these two columns. At which point, upserts are supported.
 */
public interface AmiTable {

	/**
	 * 64-bit signed
	 */
	byte TYPE_LONG = AmiDatasourceColumn.TYPE_LONG;

	/**
	 * 32-bit signed
	 */
	byte TYPE_INT = AmiDatasourceColumn.TYPE_INT;

	/**
	 * 16-bit signed
	 */
	byte TYPE_SHORT = AmiDatasourceColumn.TYPE_SHORT;

	/**
	 * 8-bit signed
	 */
	byte TYPE_BYTE = AmiDatasourceColumn.TYPE_BYTE;

	/**
	 * 64-bit floating point
	 */
	byte TYPE_DOUBLE = AmiDatasourceColumn.TYPE_DOUBLE;

	/**
	 * 32-bit floating point
	 */
	byte TYPE_FLOAT = AmiDatasourceColumn.TYPE_FLOAT;

	/**
	 * Var char
	 */
	byte TYPE_STRING = AmiDatasourceColumn.TYPE_STRING;

	/**
	 * Auto-creating enumeration
	 */
	byte TYPE_ENUM = AmiDataEntity.PARAM_TYPE_ENUM3;

	/**
	 * bit
	 */
	byte TYPE_BOOLEAN = AmiDatasourceColumn.TYPE_BOOLEAN;

	/**
	 * Date in UTC milliseconds
	 */
	byte TYPE_UTC = AmiDatasourceColumn.TYPE_UTC;

	/**
	 * Date in UTC nanoseconds
	 */
	byte TYPE_UTCN = AmiDatasourceColumn.TYPE_UTCN;

	/**
	 * 2 byte char
	 */
	byte TYPE_CHAR = AmiDatasourceColumn.TYPE_CHAR;

	/**
	 * not a valid column
	 */
	byte TYPE_NONE = AmiDatasourceColumn.TYPE_NONE;

	byte TYPE_BINARY = AmiDatasourceColumn.TYPE_BINARY;

	byte TYPE_UUID = AmiDatasourceColumn.TYPE_UUID;
	byte TYPE_COMPLEX = AmiDatasourceColumn.TYPE_COMPLEX;

	byte TYPE_BIGINT = AmiDatasourceColumn.TYPE_BIGINT;
	byte TYPE_BIGDEC = AmiDatasourceColumn.TYPE_BIGDEC;

	long NULL_NUMBER = Long.MIN_VALUE;
	double NULL_DECIMAL = Double.NaN;

	////////////////////////////////////////////////////
	/////////////////// COLOUMNS ///////////////////////
	////////////////////////////////////////////////////
	/**
	 * get a column name from this table
	 * 
	 * @param position
	 *            the zero-indexed position, zero is left most, 1 is 2nd to left, so on
	 * @return the column name at supplied position
	 * @throws IndexOutOfBoundsException
	 *             if position is not valid
	 */
	String getColunNameAt(int position);

	/**
	 * get the type of column at a position from this table
	 * 
	 * @param position
	 *            the zero-indexed position, zero is left most, 1 is 2nd to left, so on
	 * @return the column type at supplied position (see TYPE_...)
	 * @throws IndexOutOfBoundsException
	 *             if position is not valid
	 */
	byte getColumnTypeAt(int position);

	/**
	 * get the type of column for a given name from this table
	 * 
	 * @param name
	 *            the name of the column
	 * @return the column type or TYPE_NONE if column name does not exist
	 */
	byte getColumnType(String name);

	/**
	 * @param name
	 *            name of the column
	 * @return zero-indexed column location or -1 if invalid column name
	 */
	int getColumnLocation(String name);

	/**
	 * @return number of columns in this table
	 */
	int getColumnsCount();

	/**
	 * get a column from this table by position
	 * 
	 * @param position
	 *            the zero-indexed position, zero is left most, 1 is 2nd to left, so on
	 * @return the column at that position
	 * @throws IndexOutOfBoundsException
	 *             if position is not valid
	 */
	AmiColumn getColumnAt(int position);

	/**
	 * get a column from this table by name
	 * 
	 * @param name
	 *            the name of the column
	 * @return the column at that position
	 * @throws DetailedException
	 *             if the name is not a valid column name
	 */
	AmiColumn getColumn(String name);

	/**
	 * get a column from this table by position
	 * 
	 * @param position
	 *            the zero-indexed position, zero is left most, 1 is 2nd to left, so on
	 * @return the column at that position or null if invalid
	 */
	AmiColumn getColumnAtNoThrow(int position);
	/**
	 * get a column from this table by name
	 * 
	 * @param name
	 *            the name of the column
	 * @return the column at that position or null if not a valid column name
	 */
	AmiColumn getColumnNoThrow(String name);

	////////////////////////////////////////////////////
	///////////////////   ROWS   ///////////////////////
	////////////////////////////////////////////////////
	/**
	 * get number of rows in this table
	 * 
	 * @return number of rows, ex: zero if table is empty
	 */
	int getRowsCount();

	/**
	 * get a row by position
	 * 
	 * @param rowPosition
	 *            offset into table(zero=top, etc)
	 * @return the row at that position
	 * @throws RuntimeException
	 *             if the position is negative or beyond size of table
	 */
	AmiRow getAmiRowAt(int rowPosition);

	/**
	 * Get a known row from this table by id
	 * 
	 * @param amiId
	 *            the id of the row (see {@link AmiRow#getAmiId()}
	 * @return the row with matching amiId, or null if not found in this table
	 */
	AmiRow getAmiRowByAmiId(long amiId);

	////////////////////////////////////////////////////
	//////////  INSERT / UPDATES / DELETES  ////////////
	////////////////////////////////////////////////////
	/**
	 * Remove a row from this table. After remove, be sure to remove all local references to the row. (Rows may be pooled and reused so you could be very surprised to inspect it
	 * later and see it has new values!)
	 * 
	 * @param row
	 *            must be a row from this table. In other words, row. {@link AmiRow#getAmiTable()} should == this
	 */
	boolean removeAmiRow(AmiRow row, CalcFrameStack sf);

	/**
	 * Add row to the table. Note the values are copied out of the {@link AmiPreparedRow} into a new run, so you may reuse the {@link AmiPreparedRow} again for further inserts.
	 * 
	 * @param row
	 *            to insert
	 * @return the location of this row within the table
	 */
	AmiRow insertAmiRow(AmiPreparedRow row, CalcFrameStack sf);

	/**
	 * Update using the (I and P) or the primary key columns as an index
	 */
	AmiRow updateAmiRow(AmiPreparedRow row, CalcFrameStack sf);

	/**
	 * Update the row identified by amiId parameter.
	 * 
	 * @param amiId
	 *            the id of the row to update
	 * @param row
	 *            the values to update record with
	 * @return the updated record
	 */
	AmiRow updateAmiRow(long amiId, AmiPreparedRow row, CalcFrameStack sf);

	////////////////////////////////////////////////////
	//////////////////   TRIGGERS   ////////////////////
	////////////////////////////////////////////////////
	/**
	 * fire {@link AmiTrigger#onUpdated(AmiTable, AmiRow)} on all of this table's registered for-update triggers
	 * 
	 * @param row
	 *            row to pass into call
	 */
	void fireTriggerUpdated(AmiRow row, CalcFrameStack sf);

	/**
	 * fire {@link AmiTrigger#onUpdating(AmiTable, AmiRow)} on all of this table's registered for-update triggers
	 * 
	 * @param row
	 *            row to pass into call
	 * @return true if all triggers returned true, false otherwise
	 */
	//	boolean fireTriggerUpdating(AmiRow row, AmiImdbSession session);

	void addTrigger(AmiTriggerBindingImpl trigger, CalcFrameStack sf);
	void removeTrigger(String triggerName, CalcFrameStack sf);
	int getTriggersCount();
	AmiTriggerBindingImpl getTriggerAt(int position);

	////////////////////////////////////////////////////
	/////////////////    INDEXES    ////////////////////
	////////////////////////////////////////////////////

	/**
	 * get an index registered on this table
	 * 
	 * @param name
	 *            of the index
	 * @return index with supplied name
	 * @throws DetailedException
	 *             if the index name does not exist on this table
	 */
	AmiIndex getAmiIndex(String string);
	Set<String> getAmiIndexNames();

	/**
	 * get an index registered on this table
	 * 
	 * @param name
	 *            of the index
	 * @return index with supplied name or null if not found
	 */
	AmiIndex getAmiIndexNoThrow(String string);

	/**
	 * create a new prepared query, used for queries on this table only.
	 * 
	 * @return a new {@link AmiPreparedQuery}
	 */
	AmiPreparedQuery createAmiPreparedQuery();

	/**
	 * execute a prepared query on this table's rows. Note the query must have been created by this table's {@link #createAmiPreparedQuery()}
	 * 
	 * @param query
	 *            the query to run
	 * @param limit
	 *            max number of records to add to sink before returning
	 * @param sink
	 *            the sink to add records to that match supplied query
	 */
	void query(AmiPreparedQuery query, int limit, List<AmiRow> sink);

	/**
	 * 
	 * @param query
	 * @return
	 */
	AmiRow query(AmiPreparedQuery query);

	boolean addIndex(byte defType, String name, List<String> columns, List<Byte> sorted, byte indexType, StringBuilder errorSink, CalcFrameStack sf);
	AmiIndexImpl removeIndex(String name, CalcFrameStack sf);

	////////////////////////////////////////////////////
	//////////////////    OTHERS    ////////////////////
	////////////////////////////////////////////////////
	/**
	 * Get the unique name of this table, as registered with the database
	 * 
	 * @return this tables name
	 */
	String getName();

	void rename(String name, CalcFrameStack sf);

	/**
	 * create a new prepared row, used for updates and inserts on this table only.
	 * 
	 * @return a new {@link AmiPreparedRow}
	 */
	AmiPreparedRow createAmiPreparedRow();
	AmiPreparedRow createAmiPreparedRowForRecovery();

	/**
	 * @return the in memory database that this table is a member of
	 */
	AmiImdb getImdb();
	public void startup(AmiImdbImpl db, CalcFrameStack sf);
	boolean getIsBroadCast();
	public byte getDefType();
	AmiTablePersisterBinding getPersister();

	void clearRows(CalcFrameStack sf);

	AmiTriggerBindingImpl getTriggerNoThrow(String triggerName);

	boolean updateAmiRow(AmiRowImpl existing, AmiPreparedRow row, CalcFrameStack sf);
}
