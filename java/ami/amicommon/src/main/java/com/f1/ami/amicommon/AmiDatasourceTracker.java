package com.f1.ami.amicommon;

import java.util.List;

import com.f1.ami.amicommon.msg.AmiCenterQueryDsTrackerEvent;
import com.f1.base.Table;
import com.f1.utils.sql.SqlPlanListener;

/**
 * for Remote debugging of what is going on inside an adapter
 */
public interface AmiDatasourceTracker extends SqlPlanListener {

	/**
	 * @param query
	 *            call before starting a query
	 */
	public void onQuery(String query);

	/**
	 * @param string
	 *            error message
	 */
	public void onError(String string);

	/**
	 * @param query
	 *            The query that was run and produced the resulting table
	 * @param table
	 *            The resulting data
	 */
	public void onQueryResult(String query, Table table);

	/**
	 * internal use
	 */
	public List<AmiCenterQueryDsTrackerEvent> createTrackedEvents();

	/**
	 * internal use
	 */
	public String getMessage();
}
