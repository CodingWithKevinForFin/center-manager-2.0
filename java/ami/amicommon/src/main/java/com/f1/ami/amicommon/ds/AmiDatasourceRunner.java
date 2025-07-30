package com.f1.ami.amicommon.ds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourcePausableAdapter;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiCenterUploadImpl;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.ami.amicommon.msg.AmiRelayRunDbRequest;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.Cancellable;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.DerivedCellTimeoutController;
import com.f1.utils.structs.table.derived.FlowControlThrow;

public class AmiDatasourceRunner implements Runnable, Cancellable {
	private static final Logger log = LH.get();
	final private AmiDatasourceAdapter adapter;
	final private AmiServiceLocator locator;
	final private ContainerTools tools;
	final private long dsId;
	final private AmiCenterQueryDsRequest clientReq;
	private Throwable throwable;
	private long durationNannos;
	private boolean cancelSubmitted;
	final private AmiDatasourceTracker tracker;
	final private AmiCenterQueryResult results;
	private long startTimeNano;
	private List<AmiDatasourceTable> schema;
	final private boolean isReadonly;
	final private int maxLogSize;
	final private int defaultTimeout;
	private DerivedCellTimeoutController timeout;

	public AmiDatasourceRunner(ContainerTools tools, AmiDatasourceAdapter adapter, AmiRelayRunDbRequest request, AmiCenterQueryDsRequest clientReq, boolean isReadonly,
			int maxLogSize, int defaultTimeout) {
		this.adapter = adapter;
		this.clientReq = clientReq;
		this.dsId = request.getDsAmiId();
		this.locator = AmiUtils.newServiceLocator(request);
		this.tools = tools;
		if (clientReq.getIsTest()) {
			this.tracker = new AmiDatasourceTrackerImpl(tools);
		} else
			this.tracker = null;
		results = getTools().nw(AmiCenterQueryResult.class);
		this.isReadonly = isReadonly;
		this.maxLogSize = maxLogSize;
		this.defaultTimeout = defaultTimeout;
	}

	@Override
	final public void run() {
		long start = System.nanoTime();
		try {
			adapter.init(tools, locator);
			innerRun(adapter);
		} catch (Exception t) {
			this.throwable = t;
		} catch (Throwable t) {
			LH.severe(log, "Error for adapter ", OH.getClassName(adapter), t);
			this.throwable = t;
		}
		this.durationNannos += System.nanoTime() - start;
	}

	final public void fillResponse(AmiCenterQueryDsResponse r) {
		r.setDurrationNanos(System.nanoTime() - this.startTimeNano);
		if (this.throwable != null) {
			Throwable t = throwable;
			if (t instanceof FlowControlThrow && t.getCause() != null)
				t = t.getCause();
			r.setMessage(AmiUtils.toMessage(t));
			r.setOk(false);
			if (this.throwable instanceof Exception)
				r.setException(OH.toRuntime((Throwable) t));
		} else if (this.results != null) {
			AmiUtils.setReturnValues(tools, r, this.results.getReturnType(), this.results.getReturnValue(), (List) this.results.getTables());
			r.setGeneratedKeys(this.results.getGeneratedKeys());
			r.setRowsEffected((int) this.results.getRowsEffected());
		}
		r.setDisableLogging(this.getClientReq().getDisableLogging());
		r.setPreviewTables(this.schema);
		if (tracker != null)
			r.setTrackedEvents(tracker.createTrackedEvents());
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	public AmiDatasourceAdapter getAdapter() {
		return adapter;
	}

	public AmiServiceLocator getLocator() {
		return locator;
	}

	public ContainerTools getTools() {
		return tools;
	}

	public long getDsId() {
		return dsId;
	}

	public AmiCenterQueryDsRequest getClientReq() {
		return clientReq;
	}
	public long getDurrationNanos() {
		return this.durationNannos;
	}

	@Override
	public boolean cancel() {
		this.cancelSubmitted = true;
		boolean r = this.adapter.cancelQuery();
		LH.info(log, "User '", this.adapter.getServiceLocator().getInvokedBy(), "': Query cancellation on '", this.adapter.getServiceLocator().getTargetName(),
				r ? "' sent" : "' rejected", " (may be caused by timeout)");
		return r;
	}

	public boolean getWasCancelSubmitted() {
		return this.cancelSubmitted;
	}
	private void innerRun(AmiDatasourceAdapter adapter) throws AmiDatasourceException {
		AmiCenterQueryDsRequest req = this.getClientReq();
		this.startTimeNano = System.nanoTime();
		int timeout = AmiUtils.toTimeout(req.getTimeoutMs(), this.defaultTimeout);
		this.timeout = new DerivedCellTimeoutController(timeout);
		if (!req.getDisableLogging())
			logInit();
		switch (req.getType()) {
			case AmiCenterQueryDsRequest.TYPE_QUERY: {
				if (req.getQuery() != null) {
					AmiCenterQuery query = this.getTools().nw(AmiCenterQuery.class);
					query.setDirectives(req.getDirectives() == null ? Collections.EMPTY_MAP : req.getDirectives());
					query.setLimit(req.getLimit());
					query.setQuery(req.getQuery());
					query.setParsedNode(req.getParsedNode());
					query.setAllowSqlInjection(req.getAllowSqlInjection());
					try {
						adapter.processQuery(query, this.results, tracker, this.timeout);
						if (adapter instanceof AmiDatasourcePausableAdapter) {
							if (((AmiDatasourcePausableAdapter) adapter).getIsPaused())
								return;
						}
						if (!req.getDisableLogging())
							logSuccess(this.results.getTables());
					} catch (AmiDatasourceException e) {
						logThrowable(e);
						throw e;
					} catch (RuntimeException e) {
						logThrowable(e);
						throw e;
					}
				}
				break;
			}
			case AmiCenterQueryDsRequest.TYPE_SHOW_TABLES: {
				try {
					List<AmiDatasourceTable> tables = adapter.getTables(tracker, this.timeout);
					this.schema = tables;
					this.results.setTables(CH.l(toTable(tables)));
					logSuccess(this.results.getTables());
				} catch (AmiDatasourceException e) {
					logThrowable(e);
					throw e;
				} catch (RuntimeException e) {
					logThrowable(e);
					throw e;
				}
				break;
			}
			case AmiCenterQueryDsRequest.TYPE_UPLOAD: {
				AmiCenterUploadImpl upload = new AmiCenterUploadImpl(req.getDirectives() == null ? Collections.EMPTY_MAP : req.getDirectives(), req.getUploadValues(),
						this.timeout);
				try {
					adapter.processUpload(upload, this.results, tracker);
					if (adapter instanceof AmiDatasourcePausableAdapter) {
						if (((AmiDatasourcePausableAdapter) adapter).getIsPaused())
							return;
					}
					logSuccess(this.results.getTables());
				} catch (AmiDatasourceException e) {
					logThrowable(e);
					throw e;
				} catch (RuntimeException e) {
					logThrowable(e);
					throw e;
				}
				break;
			}
			case AmiCenterQueryDsRequest.TYPE_PREVIEW: {
				List<AmiDatasourceTable> tablesForPreview = req.getTablesForPreview();
				try {
					this.schema = adapter.getPreviewData(tablesForPreview, req.getPreviewCount(), null, this.timeout);
					List<Table> t = new ArrayList<Table>();
					for (AmiDatasourceTable i : this.schema) {
						i.setDatasourceName(adapter.getServiceLocator().getTargetName());
						t.add(i.getPreviewData());
					}
					logSuccess(t);
				} catch (AmiDatasourceException e) {
					logThrowable(e);
					throw e;
				} catch (RuntimeException e) {
					throw e;
				}

				break;
			}
			default:
				throw new IllegalStateException(SH.toString(req.getType()));
		}
	}
	private void logThrowable(Throwable e) {
		if (!getWasCancelSubmitted())
			logThrowable(e, this.maxLogSize, isReadonly, this.adapter.getServiceLocator(), this.getClientReq());
	}
	private void logSuccess(List<? extends Table> tables) {
		logSuccess(startTimeNano, tables, this.maxLogSize, isReadonly, this.adapter.getServiceLocator(), this.getClientReq());
	}

	private void logInit() {
		//		if (maxLogSize >= 0 && LH.isFine(log)) {
		AmiCenterQueryDsRequest query = this.getClientReq();
		AmiDatasourceAdapter adapter = getAdapter();
		String queryForLogging = SH.suppress(query.getQuery(), this.maxLogSize);
		if (SH.is(queryForLogging))
			queryForLogging = ", Query=" + queryForLogging;
		else
			queryForLogging = "";
		AmiServiceLocator sl = adapter.getServiceLocator();
		String type = getFormattedType(query.getType(), this.isReadonly);
		String user = getClientReq().getInvokedBy();
		LH.info(log, type, "_INIT, ", user, ", ", sl.getTargetPluginId(), ":", sl.getTargetName(), ", Timeout=", this.timeout.getTimeoutMillis(), " millis", queryForLogging);
		//		}
	}

	static public void logSuccess(long startTimeNano, List<? extends Table> tables, int maxLogSize, boolean isReadonly, AmiServiceLocator sl, AmiCenterQueryDsRequest query) {
		if (maxLogSize >= 0) {
			long end = System.nanoTime();
			long cellCount = 0;
			StringBuilder sb = new StringBuilder();
			if (CH.isntEmpty(tables)) {
				for (int i = 0; i < tables.size(); i++) {
					if (i > 0)
						sb.append('+');
					Table table = tables.get(i);
					if (table == null)
						sb.append("empty");
					else {
						sb.append(table.getSize()).append("x").append(table.getColumnsCount());
						cellCount += ((long) table.getSize()) * table.getColumnsCount();
					}
				}
			}
			String queryForLogging = SH.suppress(query.getQuery(), maxLogSize);
			if (SH.is(queryForLogging))
				queryForLogging = ", Query=" + queryForLogging;
			else
				queryForLogging = "";
			String type = getFormattedType(query.getType(), isReadonly);
			String user = query.getInvokedBy();
			LH.info(log, type, "_SUCCESS, ", user, ", ", sl.getTargetPluginId(), ":", sl.getTargetName(), ", ", cellCount, " cells (", sb, "), ", (end - startTimeNano) / 1000,
					" micros", queryForLogging);
		}
	}

	static public void logThrowable(Throwable e, int maxLogSize, boolean isReadOnly, AmiServiceLocator sl, AmiCenterQueryDsRequest query) {
		if (maxLogSize >= 0) {
			String queryForLogging = SH.suppress(query.getQuery(), maxLogSize);
			if (SH.is(queryForLogging))
				queryForLogging = ", Query=" + queryForLogging;
			else
				queryForLogging = "";
			String type = getFormattedType(query.getType(), isReadOnly);
			String user = query.getInvokedBy();
			if (e instanceof ExpressionParserException && ((ExpressionParserException) e).getisRuntime())
				LH.warning(log, type, "_RUNTIME_ERROR, ", user, ", ", sl.getTargetPluginId(), ":", sl.getTargetName(), ", Error=", SH.ddd(e.getMessage(), 100), queryForLogging, e);
			else
				LH.warning(log, type, "_ERROR, ", user, ", ", sl.getTargetPluginId(), ":", sl.getTargetName(), ", Error=", SH.ddd(e.getMessage(), 100), queryForLogging, e);
		}

	}

	public static String getFormattedType(byte type, boolean isReadonly) {
		switch (type) {
			case AmiCenterQueryDsRequest.TYPE_PREVIEW:
				return "PREVIEW";
			case AmiCenterQueryDsRequest.TYPE_QUERY:
				return isReadonly ? "CONCURRENT_EXECUTE" : "EXECUTE";
			case AmiCenterQueryDsRequest.TYPE_SHOW_TABLES:
				return "SHOW_TABLES";
			case AmiCenterQueryDsRequest.TYPE_UPLOAD:
				return "UPLOAD";
			default:
				return "UNKNOWN:" + type + isReadonly;
		}
	}

	public static Table toTable(List<AmiDatasourceTable> tables) {
		Table r = new BasicTable(String.class, "TableName", String.class, "Schema");
		r.setTitle("TABLES");
		for (AmiDatasourceTable table : tables)
			r.getRows().addRow(table.getName(), table.getCollectionName());
		return r;
	}

}
