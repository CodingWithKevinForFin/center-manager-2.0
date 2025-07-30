package com.f1.ami.amicommon;

import java.util.List;

import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiCenterUpload;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.container.ContainerTools;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.structs.table.derived.TimeoutController;

/**
 * 
 * represents an instance of a datasource. See {@link AmiDatasourcePlugin}, which is used by AMI to instantiate these datasources. The important functions are init(...) and
 * processQuery(...)
 * 
 */
public interface AmiDatasourceAdapter {
	int NO_LIMIT = SqlProcessor.NO_LIMIT;

	/**
	 * 
	 * called prior to any other methods. If this method throws an exception the datasource will not be available (and no other methods will be called). Only called once per this
	 * objects lifetime
	 * 
	 * @param tools
	 *            has various tools that can be used for accessing system-wide items such as properties, clock, thread pool, etc
	 * @param serviceLocator
	 *            the url, username,password, etc that is used to identify the resource that this instance of the adapter should connect to
	 * @throws AmiDatasourceException
	 */
	void init(ContainerTools tools, AmiServiceLocator serviceLocator) throws AmiDatasourceException;

	/**
	 * @param debugSink
	 *            If not null, can be optionally used to pass debug information back to the AMI user
	 * @param tc
	 *            if not null, a timeout controller that should be used for determining how much time is allowd to complete this call.
	 * @return the schema that this datasource represents. A schema is defined as a list of named tables. this is used only when defining datamodels on this datasource, so
	 *         performance is not as critical... Aka, it's only used in the wizard when in development mode.
	 * @throws AmiDatasourceException
	 */
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException;

	/**
	 * @param tables
	 *            the tables to get preview data for (generally this is the list that you would return)
	 * @param previewCount
	 *            the number of records to get for each table in the preview
	 * @param debugSink
	 *            used to debug information, may be null
	 * @param tc
	 *            if not null, a timeout controller that should be used for determining how much time is allowd to complete this call.
	 * @return a list of tables with preview data. Note, that an AmiDatasouceTable is constructed using tools.nw(AmiDatasourceTable.class);
	 * @throws AmiDatasourceException
	 */
	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException;

	/**
	 * @return the serviceLocator passed into {@link #init(ContainerTools, AmiServiceLocator)}, or null if init(..) has not be called.
	 */
	public AmiServiceLocator getServiceLocator();

	/**
	 * This is invoked as a result of the <i>USE ... EXECUTE</i> command
	 * 
	 * @param query
	 *            the query to run on this datasource, which includes the query string and directives
	 * @param resultSink
	 *            populated with the results from this query. Use AmiCenterQueryResult::setTables, where the list should be entries of type
	 *            com.f1.utils.structs.table.columnar.ColumnarTable
	 * @param debugSink
	 *            If not null then the user is running the query in debug mode hence anything put in this map will be available for viewing by the user in the AMI debug window.
	 *            This paraemter may be null indicating that debug mode is off, all attempts to deref this param must first check for null
	 * 
	 * @throws AmiDatasourceException
	 */
	public void processQuery(AmiCenterQuery query, AmiCenterQueryResult resultSink, AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException;

	/**
	 * Cancel the currently running query
	 * 
	 * @return true if the query was cancelled, false if the query already completed.
	 */
	public boolean cancelQuery();

	/**
	 * 
	 * This is invoked as a result of the <i>USE ... INSERT .... FROM ....</i> command
	 * 
	 * @param upload
	 *            the data to updload into this datasource
	 * @param resultsSink
	 *            Tables returned as a result of this upload (usually, none)
	 * @param debugSink
	 *            If not null then the user is running the query in debug mode hence anything put in this map will be available for viewing by the user in the AMI debug window.
	 *            This paraemter may be null indicating that debug mode is off, all attempts to deref this param must first check for null
	 * @throws AmiDatasourceException
	 */
	void processUpload(AmiCenterUpload upload, AmiCenterQueryResult resultsSink, AmiDatasourceTracker tracker) throws AmiDatasourceException;

}
