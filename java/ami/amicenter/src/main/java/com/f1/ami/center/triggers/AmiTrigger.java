package com.f1.ami.center.triggers;

import java.util.List;
import java.util.Set;

import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.utils.structs.table.stack.CalcFrameStack;

/**
 * 
 * Triggers are bound to an {@link AmiTable} (or multiple tables). The binding can be for inserts ({@link #onInserted(AmiTable, AmiRow)} and {@link #onInserted(AmiTable, AmiRow)}),
 * updates ({@link #onUpdating(AmiTable, AmiRow)} and {@link #onUpdated(AmiTable, AmiRow)}) and deletes ({@link #onDeleting(AmiTable, AmiRow)}). Note that in all three cases
 * (update/insert/delete) the trigger has the opportunity to reject the event (by returning true). With all methods, it is legal to mutate the record.
 * <P>
 * NOTE ON PRIORITY: When multiple triggers are bound to the same table they are fired in the order that the triggers are declared in. Let's assume 2 Triggers are added to a table
 * (Trigger1,Trigger2). Here is the order of operations:
 * <P>
 * 
 * <PRE>
 * Trigger1.onInserting -> Trigger2.onInserting -> < record is inserted > -> Trigger1.onInsert -> Trigger2.onInsert
 * Trigger1.onUpdating -> Trigger2.onUpdate -> < record is updated > -> Trigger1.onUpdated -> Trigger2.onUpdated
 * Trigger1.onDelete -> Trigger2.onDelete -> < record is deleted >
 *  
 * (if either of the onInserting calls were to return false the chain would stop and subsequent events would not take place)
 * </PRE>
 * 
 * NOTE, for convenience (and a bit of legacy reasons) the trigger is also an {@link AmiTimedRunnable} meaning that it can accept timed events
 * 
 */
public interface AmiTrigger {

	/**
	 * Called once at startup, this is a good spot to grab needed tables, columns, indexes, etc that you will use at runtime.
	 * 
	 * @param imdb
	 */
	public void startup(AmiImdb imdb, AmiTriggerBinding binding, CalcFrameStack sf);
	/**
	 * @param table
	 *            the table that the row will be inserted into
	 * @param row
	 *            the row to insert
	 * @return false to reject insert, true to continue down the insert chain
	 */
	public boolean onInserting(AmiTable table, AmiRow row, CalcFrameStack sf);

	/**
	 * @param table
	 *            the table that the row was inserted into
	 * @param row
	 *            the row inserted
	 */
	public void onInserted(AmiTable table, AmiRow row, CalcFrameStack sf);

	/**
	 * @param table
	 *            the table that the row was inserted into
	 * @param row
	 *            the row inserted
	 */
	public void onInitialized(CalcFrameStack sf);

	/**
	 * @param table
	 *            the row that will be updated (contains old values)
	 * @param row
	 *            the row after being updated
	 */
	public boolean onUpdating(AmiTable table, AmiRow row, CalcFrameStack sf);

	public boolean onUpdating(AmiTable table, AmiRow row, AmiPreparedRow updatingTo, CalcFrameStack sf);

	/**
	 * @param table
	 *            the table that the row was updated on
	 * @param row
	 *            the row after being updated
	 */
	public void onUpdated(AmiTable table, AmiRow row, CalcFrameStack sf);

	/**
	 * @param table
	 *            the table that the row will be deleted from
	 * @param row
	 *            the row to be deleted
	 * @return false to reject delete, true to continue down the delete chain
	 */
	public boolean onDeleting(AmiTable table, AmiRow row, CalcFrameStack sf);

	/**
	 * called when the database schema has changed, ex: tables,columns,index,triggers or stored procedures are added/deleted/updated to the database.
	 * 
	 * @param imdb
	 */
	public void onSchemaChanged(AmiImdb imdb, CalcFrameStack sf);

	public void onEnabled(boolean enable, CalcFrameStack sf);

	public byte INSERTING = 1;
	public byte INSERTED = 2;
	public byte UPDATING = 3;
	public byte UPDATED = 4;
	public byte DELETING = 5;
	public byte TIMER = 6;
	public byte CALL = 7;

	public boolean isSupported(byte type);

	Set<String> getLockedTables();
	public List<String> getBindingTables();

	public AmiTriggerBinding getBinding();
	public void onClosed();

	//If there is a lower priority trigger listing for updates on the same table as me and it's onUpdating(...) returns false then this is called to let me know an onUpdated(...) will NOT be coming
	public void onUpdatingRejected(AmiTable table, AmiRow row);

}
