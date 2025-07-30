package com.f1.ami.web.datafilter;

import com.f1.ami.web.AmiWebObject;
import com.f1.utils.structs.table.columnar.ColumnarTable;

public interface AmiWebDataFilter {
	public static final byte HIDE_ALWAYS = 1;
	public static final byte SHOW_ALWAYS = 2;
	public static final byte HIDE = 3;
	public static final byte SHOW = 4;

	/**
	 * Called the user logs
	 * 
	 */
	public void onLogin();

	/**
	 * Called when the user logs out
	 * 
	 */
	public void onLogout();

	/**
	 * Called when a new row is received from the Center. Note that during login, all rows for display will pass through this method
	 * 
	 * @param realtimeRow
	 *            the row to filter
	 * @return {@link #HIDE_ALWAYS} - The row will be hidden from the user, regardless of any subsequent updates to the row's data (less overhead)<BR>
	 *         {@link #HIDE} - The row will be hidden from the user, but future updates to the row's data will be re-evaluated via
	 *         {@link #evaluateUpdatedRow(AmiWebObject) }(greater overhead)<BR>
	 *         {@link #SHOW_ALWAYS} - The row will be visible to the user, regardless of any subsequent updates to the row's data (less overhead)<BR>
	 *         {@link #SHOW} - The row will be visible the user, but future updates to the row's data will be re-evaluated via {@link #evaluateUpdatedRow(AmiWebObject)} (greater
	 *         overhead)<BR>
	 * 
	 */
	public byte evaluateNewRow(AmiWebObject realtimeRow);

	/**
	 * Called when a new row is updated from the Center. Note that this is only called for realtimeRows whose prior evaluation either returned {@link #HIDE} or {@link #SHOW}
	 * 
	 * @param realtimeRow
	 *            the row to filter
	 * @param currentStatus
	 *            Either {@link #HIDE} or {@link #SHOW}
	 * @return {@link #HIDE_ALWAYS} - The row will be hidden from the user, regardless of any subsequent updates to the row's data (less overhead)<BR>
	 *         {@link #HIDE} - The row will be hidden from the user, but future updates to the row's data will be re-evaluated via {@link #evaluateUpdatedRow(AmiWebObject)}(greater
	 *         overhead)<BR>
	 *         {@link #SHOW_ALWAYS} - The row will be visible to the user, regardless of any subsequent updates to the row's data (less overhead)<BR>
	 *         {@link #SHOW} - The row will be visible the user, but future updates to the row's data will be re-evaluated via {@link #evaluateUpdatedRow(AmiWebObject)} (greater
	 *         overhead)<BR>
	 * 
	 */
	public byte evaluateUpdatedRow(AmiWebObject realtimeRow, byte currentStatus);

	/**
	 * This method is called after each EXECUTE completes. In the general case, the Rows of the Table of the will be evaluated and certain rows may be deleted.
	 * 
	 * @param query
	 *            - The query passed to the backend.
	 * @param table
	 *            - the resulting table from the query
	 */
	public void evaluateQueryResponse(AmiWebDataFilterQuery query, ColumnarTable table);

	/**
	 * Called before the request is sent to the backend. If the query is permitted as is, simply return the query param. To reject the query return null. Or create a new
	 * {@link AmiWebDataFilterQueryImpl} object and set the various parameters that should actually be executed
	 * 
	 * @param query
	 *            the query that the user would like to run
	 * @return the actually query to run, or null if the query should not be executed.
	 */
	AmiWebDataFilterQuery evaluateQueryRequest(AmiWebDataFilterQuery query);

}
