package com.f1.ami.center;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiFlowControlPause;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.JdbcAdapter;
import com.f1.ami.amicommon.ds.AmiDatasourceRunner;
import com.f1.ami.amicommon.ds.AmiDatasourceTrackerImpl;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse;
import com.f1.ami.amicommon.msg.AmiCenterResponse;
import com.f1.ami.amicommon.msg.AmiCenterUploadTable;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.ami.center.hdb.events.AmiHdbRequest;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiImdbScriptManager;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.base.Action;
import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.container.RequestMessage;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.sql.DerivedCellCalculatorSql;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.TableReturn;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.sqlnode.SqlNode;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.ColumnPositionMapping;
import com.f1.utils.structs.table.ColumnPositionMappingSourceStraight;
import com.f1.utils.structs.table.ColumnPositionMappingStraight;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorExpression;
import com.f1.utils.structs.table.derived.DerivedCellTimeoutController;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;
import com.f1.utils.structs.table.stack.SqlResultset;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public class AmiDatasourceAdapterForAmi {

	public static final AmiServiceLocator AMI_SERVICE_LOCATOR = new AmiServiceLocator(AmiServiceLocator.TARGET_TYPE_DATASOURCE, AmiConsts.DATASOURCE_ADAPTER_NAME_AMI,
			AmiConsts.DATASOURCE_NAME_AMI, null, null, null, null, null);
	private static final Logger log = LH.get();

	public static Map<String, String> buildOptions() {
		return new HashMap<String, String>();
	}

	final private AmiCenterState state;
	final private ContainerTools tools;
	final private long startTimeNano;
	final private AmiImdbSession session;
	final private AmiCenterItineraryWorker worker;
	final private AmiCenterQueryAmiItinerary itinerary;
	final private RequestMessage<AmiCenterQueryDsRequest> initialRequest;
	final private AmiCenterQueryDsResponse resultSink;
	final private AmiDatasourceTrackerImpl debugSink;

	private DerivedCellCalculatorExpression origCalc;
	private FlowControlPause paused;

	public AmiDatasourceAdapterForAmi(AmiCenterQueryAmiItinerary amiCenterQueryAmiItinerary, AmiCenterItineraryWorker worker, AmiImdbSession session,
			AmiCenterQueryDsResponse resultSink, AmiDatasourceTrackerImpl debugSink) {
		this.itinerary = amiCenterQueryAmiItinerary;
		this.debugSink = debugSink;
		this.state = this.itinerary.getState();
		this.tools = this.itinerary.getTools();
		this.initialRequest = this.itinerary.getInitialRequest();
		this.session = session;
		this.worker = worker;
		this.startTimeNano = this.itinerary.getStartTimeNano();
		this.resultSink = resultSink;
	}

	public List<AmiDatasourceTable> getTables() throws AmiDatasourceException {
		StringBuilder sb = new StringBuilder();
		AmiCenterQueryDsRequest query = tools.nw(AmiCenterQueryDsRequest.class);
		query.setQuery("select * from  __COLUMN");
		query.setLimit(SqlProcessor.NO_LIMIT);

		processQuery(query, new DerivedCellTimeoutController(60000));
		Table t = this.resultSink.getTables().get(0);
		List<AmiDatasourceTable> r = new ArrayList<AmiDatasourceTable>();
		Map<String, AmiDatasourceTable> m = new HashMap<String, AmiDatasourceTable>();
		for (Row row : t.getRows()) {
			String tableName = row.get("TableName", Caster_String.INSTANCE);
			String columnName = row.get("ColumnName", Caster_String.INSTANCE);
			String definedBy = row.get("DefinedBy", Caster_String.INSTANCE);
			String dataType = row.get("DataType", Caster_String.INSTANCE);
			AmiDatasourceTable dt = m.get(tableName);
			if (dt == null) {
				dt = tools.nw(AmiDatasourceTable.class);
				dt.setCollectionName(definedBy);
				dt.setName(tableName);

				createSelectQuery(sb, tableName);

				dt.setCustomQuery(SH.toStringAndClear(sb));

				m.put(tableName, dt);
				r.add(dt);
				dt.setColumns(new ArrayList<AmiDatasourceColumn>());
			}
			AmiDatasourceColumn col = tools.nw(AmiDatasourceColumn.class);
			col.setName(columnName);
			col.setType(AmiUtils.parseTypeName(dataType));
			dt.getColumns().add(col);
		}
		return r;
	}
	protected StringBuilder createSelectQuery(StringBuilder sb, String fullname) {
		sb.append(JdbcAdapter.SELECT_ALL_FROM_CLAUSE);
		sb.append(fullname);
		sb.append(JdbcAdapter.WHERE_CLAUSE);
		return sb;
	}

	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, TimeoutController timeout) throws AmiDatasourceException {
		this.session.lock(this.itinerary, null);
		try {
			for (int i = 0; i < tables.size(); i++) {
				AmiDatasourceTable table = tables.get(i);
				table.setDatasourceName(AmiConsts.DATASOURCE_NAME_AMI);
				String name = table.getName();
				//			String query = SH.replaceAll(table.getCustomQuery(), "${WHERE}", "true");
				try {
					if (state.getHdb().getTablesSorted().contains(name)) {
						Table previewData = state.getHdb().getPreview(name, previewCount);
						tables.get(i).setPreviewData(previewData);
						tables.get(i).setPreviewTableSize((long) state.getHdb().getRowsCount(name));
					} else {
						AmiImdbImpl db = this.state.getAmiImdb();
						TableReturn tr = (TableReturn) db.getScriptManager().executeSql("select * from " + name, EmptyCalcFrame.INSTANCE, null, previewCount, this.debugSink,
								session.getReusableTopStackFrame());
						tables.get(i).setPreviewData(tr.getTables().get(0));
						Table tbl = this.session.getTableNoThrow(table.getName());
						if (tbl != null)
							tables.get(i).setPreviewTableSize((long) tbl.getSize());
					}
				} catch (RuntimeException e) {
					LH.warning(log, "Error getting preview for " + name, e);
					throw e;
				}
			}
			return tables;
		} finally {
			this.session.unlock();
		}
	}

	public void processQuery(AmiCenterQueryDsRequest query, TimeoutController timeout) {
		this.session.lock(this.itinerary, null);
		try {
			final AmiImdbScriptManager sm = this.state.getAmiImdb().getScriptManager();
			final String queryStr = query.getQuery();
			final int limit = query.getLimit();
			final boolean allowInjection = query.getAllowSqlInjection();
			final TopCalcFrameStack sf = this.session.getReusableTopStackFrame();
			if (query.getParsedNode() != null)
				this.origCalc = sm.prepareSql(queryStr, query.getParsedNode(), EmptyCalcTypes.INSTANCE, true, allowInjection, sf);
			else
				this.origCalc = sm.prepareSql(queryStr, EmptyCalcTypes.INSTANCE, true, allowInjection, sf);
			final Object t = sm.executeSql(this.origCalc, EmptyCalcFrame.INSTANCE, AmiImdbScriptManager.ON_EXECUTE_RETURN_PAUSE, timeout, limit, this.debugSink, sf);
			processResult(t);
		} catch (Exception e) {
			processError(e);
		} finally {
			session.unlock();
		}
	}

	private void processResult(Object t) {
		if (t instanceof FlowControl) {
			if (t instanceof FlowControlPause) {
				setPause((FlowControlPause) t);
				return;
			} else if (t instanceof TableReturn) {
				session.getReusableTopStackFrame().getSqlResultset().appendTable((TableReturn) t);
				t = null;
			}
		}

		final SqlResultset tr = session.getReusableTopStackFrame().getSqlResultset();
		final String warnings = this.session.drainWarnings();
		if (SH.is(warnings)) {
			BasicTable warningsTable = new BasicTable(String.class, "Warning");
			warningsTable.setTitle("__TEMP_WARNING");
			for (String s : SH.split('\n', warnings))
				if (SH.is(s))
					warningsTable.getRows().addRow(s);
			tr.appendTable(new TableReturn(warningsTable));
		}
		AmiUtils.setReturnValues(tools, resultSink, this.origCalc == null ? Object.class : this.origCalc.getReturnType(), t, tr.getTables());
		resultSink.setRowsEffected(tr.getRowsEffected());
		resultSink.setGeneratedKeys(tr.getGenerateKeys());
		resultSink.setDurrationNanos(System.nanoTime() - this.startTimeNano);
		AmiDatasourceRunner.logSuccess(this.startTimeNano, resultSink.getTables(), state.getLogQueryMaxChars(), this.itinerary.isReadonly(), AMI_SERVICE_LOCATOR,
				this.initialRequest.getAction());
		if (this.debugSink != null)
			resultSink.setTrackedEvents(debugSink.createTrackedEvents());
		tr.reset();
	}
	private void processError(Throwable t) {
		this.paused = null;
		if (t instanceof FlowControlThrow && t.getCause() instanceof ExpressionParserException)
			t = t.getCause();
		if (t instanceof ExpressionParserException) {
			ExpressionParserException t2 = (ExpressionParserException) t;
			if (t2.getExpression() == null)
				t2.setExpression(this.initialRequest.getAction().getQuery());
		}
		AmiDatasourceRunner.logThrowable(t, state.getLogQueryMaxChars(), this.itinerary.isReadonly(), AMI_SERVICE_LOCATOR, this.initialRequest.getAction());
		resultSink.setMessage(AmiUtils.toMessage(t));
		resultSink.setOk(false);
		resultSink.setException(OH.toRuntime(t));
		final SqlResultset tr = session.getReusableTopStackFrame().getSqlResultset();
		AmiUtils.setReturnValues(tools, resultSink, null, null, tr.getTables());
		resultSink.setRowsEffected(tr.getRowsEffected());
		resultSink.setGeneratedKeys(tr.getGenerateKeys());
		resultSink.setDurrationNanos(System.nanoTime() - this.startTimeNano);
		if (this.debugSink != null)
			resultSink.setTrackedEvents(debugSink.createTrackedEvents());
		tr.reset();
	}
	public void resume(Action action) {
		if (this.paused instanceof AmiFlowControlPause) {
			AmiCenterResponse embeddedResponse = (AmiCenterResponse) action;
			AmiFlowControlPause p = (AmiFlowControlPause) this.paused;
			this.session.lock(this.itinerary, this.debugSink);
			try {
				if (!embeddedResponse.getOk()) {
					if (embeddedResponse.getException() != null)
						p.processErrorResponse(embeddedResponse.getException());
					else
						p.processErrorResponse(embeddedResponse.getMessage());
				} else
					p.processResponse(embeddedResponse);
				this.paused = null;
				Object t = p.resume();
				processResult(t);
			} catch (Throwable e) {
				processError(e);
			} finally {
				this.session.unlock();
			}
		} else
			throw new IllegalStateException();
	}

	private void setPause(FlowControlPause p) {
		if (p instanceof AmiFlowControlPause) {
			AmiFlowControlPause t = (AmiFlowControlPause) p;
			AmiCenterQueryDsRequest origReq = this.initialRequest.getAction();
			AmiImdbImpl db = this.state.getAmiImdb();
			Action req = t.toRequest(this.tools);
			if (req instanceof AmiCenterQueryDsRequest) {
				AmiCenterQueryDsRequest req2 = (AmiCenterQueryDsRequest) req;
				if (req2.getDatasourceName() == null && req2.getDatasourceOverrideAdapter() == null)
					throw new ExpressionParserException(req2.getQuery(), t.getPosition().getPosition(), "missing datasouce, ex: USE ds=mydatasource");
				req2.setInvokedBy(origReq.getInvokedBy());
				req2.setIsTest(origReq.getIsTest());
				req2.setRequestTime(System.currentTimeMillis());
				req2.setQuerySessionId(0);
				req2.setQuerySessionKeepAlive(false);
				req2.setParentProcessId(this.itinerary.getProcessId());
				req2.setUseConcurrency(origReq.getUseConcurrency());
				AmiCenterItinerary<AmiCenterQueryDsRequest> t2 = AmiCenterQueryDsToItineraryProcessor.createQueryItinerary(req2);
				worker.startItinerary(itinerary, t2, req2);
			} else if (req instanceof AmiHdbRequest) {
				AmiHdbRequest req2 = (AmiHdbRequest) req;
				worker.sendHdbRequest(itinerary, req2);
			}
			//		} else if (p instanceof AmiHdbPause) {
			//			AmiHdbPause t = (AmiHdbPause) p;
			//			AmiHdbRequest rm = tools.nw(AmiHdbRequest.class);
			//			rm.setSqlFlowControl(t.getHdbFlowControl());
		}
		this.paused = p;
	}
	public boolean cancelQuery() {
		return false;
	}

	public AmiImdbSession getSession() {
		return session;
	}

	public FlowControlPause getPaused() {
		return paused;
	}

	public void processUpload(AmiCenterQueryDsRequest action) throws AmiDatasourceException {
		List<AmiCenterUploadTable> tables = action.getUploadValues();
		this.session.lock(this.itinerary, debugSink);
		try {
			if (tables.size() != 1)
				throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Upload to AMI currently only supports single table");
			AmiCenterUploadTable upload = tables.get(0);
			Table data = (Table) upload.getData();
			String targetTableName = upload.getTargetTable();
			Table targetTable = session.getTableNoThrow(targetTableName);//
			boolean isTemp = session.isTemporaryTable(targetTableName);
			if (targetTable == null)
				throw new AmiDatasourceException(AmiDatasourceException.SCHEMA_ERROR, "Table not found for Upload: " + targetTableName);
			List<String> columns = upload.getTargetColumns();
			int columnsCount = data.getColumnsCount();
			ColumnPositionMapping posMapping;
			if (columns != null) {
				String dup = CH.firstDup(columns);
				if (dup != null)
					throw new AmiDatasourceException(AmiDatasourceException.SCHEMA_ERROR, "Duplicate target column: " + dup);
				final int[] targetColumns = new int[columnsCount];
				for (int i = 0; i < columns.size(); i++) {
					String colName = columns.get(i);
					Column col = targetTable.getColumnsMap().get(colName);
					if (col == null)
						throw new AmiDatasourceException(AmiDatasourceException.SCHEMA_ERROR, "Column not found in table '" + targetTableName + "': " + colName);
					targetColumns[i] = col.getLocation();
				}
				posMapping = ColumnPositionMappingSourceStraight.GET(targetColumns);
			} else {
				if (columnsCount != targetTable.getColumnsCount())
					throw new AmiDatasourceException(AmiDatasourceException.SCHEMA_ERROR,
							"Table has " + targetTable.getColumnsCount() + " column(s) but upload data has " + columnsCount + " column(s)");
				posMapping = ColumnPositionMappingStraight.GET(columnsCount);
			}

			if (isTemp) {
				final Caster<?>[] casters = new Caster[columnsCount];
				for (int i = 0; i < columnsCount; i++) {
					casters[i] = targetTable.getColumnAt(posMapping.getTargetPosAt(i)).getTypeCaster();
				}
				for (Row row : data.getRows()) {
					Row targetRow = targetTable.newEmptyRow();
					for (int n = 0; n < columnsCount; n++)
						targetRow.putAt(posMapping.getTargetPosAt(n), casters[n].cast(row.getAt(n)));
					targetTable.getRows().add(targetRow);
				}
			} else {
				AmiImdbImpl db = this.state.getAmiImdb();
				DerivedCellCalculatorSql query = new DerivedCellCalculatorSql(new SqlNode(0, null, SqlExpressionParser.ID_INSERT),
						db.getScriptManager().getSqlProcessor().getParser());
				TopCalcFrameStack sf = this.session.getReusableTopStackFrame();
				FlowControl t = db.getScriptManager().getSqlProcessor().getInsertProcessor().doInsert(sf, query, targetTable, 0, posMapping, 0, data.getSize(), data, false);
				if (t instanceof FlowControlPause) {
					setPause((FlowControlPause) t);
				} else
					processResult(t);
			}
		} finally {
			this.session.unlock();
		}
	}
	public boolean getIsPaused() {
		return this.paused != null;
	}

}
