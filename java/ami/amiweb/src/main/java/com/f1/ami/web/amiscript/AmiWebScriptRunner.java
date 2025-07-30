package com.f1.ami.web.amiscript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiFlowControlPause;
import com.f1.ami.amicommon.AmiFlowControlPauseSql;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterPassToRelayRequest;
import com.f1.ami.amicommon.msg.AmiCenterPassToRelayResponse;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse;
import com.f1.ami.amicommon.msg.AmiCenterRequest;
import com.f1.ami.amicommon.msg.AmiCenterResponse;
import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandResponse;
import com.f1.ami.amicommon.msg.AmiRelaySendEmailResponse;
import com.f1.ami.amiscript.AmiCalcFrameStack;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.datafilter.AmiWebDataFilter;
import com.f1.ami.web.datafilter.AmiWebDataFilterQuery;
import com.f1.ami.web.datafilter.AmiWebDataFilterQueryImpl;
import com.f1.base.Action;
import com.f1.base.CalcFrame;
import com.f1.base.Column;
import com.f1.base.Table;
import com.f1.container.ResultMessage;
import com.f1.suite.web.portal.BackendResponseListener;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.ToDoException;
import com.f1.utils.sql.Tableset;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.DebugPause;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.derived.FlowControlConcurrentPause;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.FlowControlThrow;

public class AmiWebScriptRunner extends FlowControlPause implements BackendResponseListener, AmiWebScriptRunnerListener {
	final static private Logger log = LH.get();

	public static final byte STATE_PREINIT = -1;
	public static final byte STATE_INIT = 0;
	public static final byte STATE_REQUEST_SENT = 1;
	public static final byte STATE_RESPONSE_READY = 2;
	public static final byte STATE_DONE = 3;
	public static final byte STATE_ERROR = 4;
	public static final byte STATE_DEBUG = 5;

	final private DerivedCellCalculator calc;
	final private List<AmiWebScriptRunnerListener> listeners = new ArrayList<AmiWebScriptRunnerListener>();
	final private String script;

	final private AmiWebService service;
	final private AmiWebTopCalcFrameStack fullVars;
	final private int warnSlowAmiScriptMs;
	private byte state;
	private Object returnValue;
	private FlowControlPause step;
	private AmiCenterRequest request;
	private Exception error;
	private Object amiError;
	private boolean isHalted;
	private FlowControlThrow flowControlThrow;
	private long endNanos;

	private AmiCenterRequest[] concurrentRequests;
	private ResultMessage[] concurrentResponses;
	private FlowControlPause[] concurrentSteps;
	private int concurrentRequestsRemaining;

	public AmiWebScriptRunner(String script, DerivedCellCalculator calc, AmiWebTopCalcFrameStack fullVars) {
		super(calc);
		OH.assertNotNull(calc);
		this.calc = calc;
		this.script = script;
		this.state = STATE_PREINIT;
		this.service = fullVars.getService();
		this.fullVars = fullVars;
		this.warnSlowAmiScriptMs = this.service.getScriptManager().getWarnSlowAmiScriptMs();
	}

	public long getStartNanos() {
		return this.fullVars.getTimeoutController().getStartTimeNanos();
	}
	public long getEndNanos() {
		return this.endNanos;
	}

	public AmiWebDomObject getThis() {
		return this.fullVars.getThis();
	}

	public void addListener(AmiWebScriptRunnerListener listener) {
		CH.addIdentityOrThrow(listeners, listener);
	}
	public void removeListener(AmiWebScriptRunnerListener listener) {
		CH.removeOrThrow(listeners, listener);
	}

	public void runStep() {
		if (isHalted)
			return;
		long start = System.currentTimeMillis();
		Object r;
		switch (state) {
			case STATE_PREINIT:
				setState(STATE_INIT);
			case STATE_INIT:
				try {
					r = this.calc.get(this.fullVars);
				} catch (Exception e) {
					this.setError(this.calc, e);
					return;
				}
				break;
			case STATE_RESPONSE_READY:
				try {
					r = this.step.resume();
				} catch (Exception e) {
					this.setError(this.step.getPosition(), e);
					return;
				}
				break;
			default:
				throw new IllegalStateException(SH.toString(state));
		}
		final long dur = System.currentTimeMillis() - start;
		if (dur > this.warnSlowAmiScriptMs)
			LH.warning(log, describeSource(), " Slow Amiscript took ", dur, " ms");
		if (r instanceof FlowControlPause) {
			if (r instanceof DebugPause) {
				DebugPause debug = (DebugPause) r;
				this.clearError();
				this.request = null;
				this.concurrentRequests = null;
				this.step = debug;
				setState(STATE_DEBUG);
				return;
			}
			FlowControlPause fc = (FlowControlPause) r;
			//			if (fc instanceof FlowControlConcurrentPause) {
			//				for (FlowControlPause i : ((FlowControlConcurrentPause) fc).getPauses()) {
			//					System.out.println(i);
			//				}
			//				this.step = fc;
			//				setState(STATE_REQUEST_SENT);
			//			} else
			try {
				if (fc instanceof FlowControlConcurrentPause) {
					FlowControlConcurrentPause cp = (FlowControlConcurrentPause) fc;
					List<FlowControlPause> pauses = cp.getPauses();
					int size = pauses.size();
					this.concurrentRequests = new AmiCenterRequest[size];
					this.concurrentSteps = new FlowControlPause[size];
					for (int i = 0; i < size; i++) {
						this.concurrentRequests[i] = toRequest(pauses.get(i));
						this.concurrentSteps[i] = pauses.get(i);
					}
					this.concurrentResponses = new ResultMessage[size];
					this.concurrentRequestsRemaining = size;
					this.step = fc;
					setState(STATE_REQUEST_SENT);
				} else {
					this.request = toRequest(fc);
					this.step = fc;
					setState(STATE_REQUEST_SENT);
				}
			} catch (Exception e) {
				this.setError(step.getPosition(), e);

			}

		} else if (r instanceof FlowControl) {
			switch (((FlowControl) r).getType()) {
				case FlowControlPause.STATEMENT_SQL:
				case FlowControlPause.STATEMENT_BREAK:
				case FlowControlPause.STATEMENT_CONTINUE: {
					this.returnValue = null;
					setState(STATE_DONE);
					break;
				}
				default:
					LH.warning(log, describeSource(), " Should not have gotten here: " + r);
			}
		} else {
			this.returnValue = r;
			setState(STATE_DONE);
			step = null;
		}
	}

	private AmiCenterRequest toRequest(FlowControlPause r) {
		this.clearError();
		AmiCenterRequest request = null;
		if (r instanceof AmiCallCommandFlowControlPause) {
			AmiCallCommandFlowControlPause cc = (AmiCallCommandFlowControlPause) r;
			AmiCenterPassToRelayRequest req;
			request = req = this.service.sendCommandToBackEnd(cc.getCommand(), cc.getTable(), cc.getTimeout(), this);
			cc.setRequest(req);
		} else if (r instanceof AmiFlowControlPause) {
			AmiFlowControlPause efc = (AmiFlowControlPause) r;
			request = toRequests(this.service, efc);
			service.sendRequestToBackend(this, request);
		} else if (r instanceof AmiCallRestFlowControlPause) {
			AmiCallRestFlowControlPause cc = (AmiCallRestFlowControlPause) r;
			cc.run(this, service.getPortletManager());
		} else if (r instanceof AmiSendEmailFlowControlPause) {
			AmiSendEmailFlowControlPause cc = (AmiSendEmailFlowControlPause) r;
			AmiCenterPassToRelayRequest req;
			request = req = this.service.sendEmailToBackEnd(cc.getBody(), cc.getSubject(), cc.getToList(), cc.getFrom(), cc.getIsHtml(), cc.getAttachmentNames(),
					cc.getAttachmentData(), cc.getUsername(), cc.getPassword(), cc.getTimeout(), this, cc.getRelayIds());
			cc.setRequest(req);
		} else if (r instanceof AmiWebScriptRunner) {
			AmiWebScriptRunner cc = (AmiWebScriptRunner) r;
			switch (cc.getState()) {
				case STATE_DONE:
				case STATE_ERROR:
					throw new ToDoException(); //was: setState(STATE_RESPONSE_READY); break;
				default:
					cc.addListener(this);
			}
		} else if (r instanceof AmiAlertPromptControlPause) {
			AmiAlertPromptControlPause cc = (AmiAlertPromptControlPause) r;
			cc.setScriptRunner(this);
		}
		return request;

	}

	private String describeSource() {
		//		if (SH.isnt(this.callback))
		//			return "User '" + service.getUserName() + "' for amiscript '" + this.sourceAri + "' ";
		//		else
		return "User '" + service.getUserName() + "' for amiscript '" + this.getCallbackAri() + "' ";
	}

	public void setState(byte state) {
		if (this.state == state)
			return;
		byte old = this.state;
		this.state = state;
		//		LH.info(log, "Starting " + this.describeSource() + " state=" + this.state);
		this.service.getBreakpointManager().onStatus(this, state);
		switch (state) {
			case AmiWebScriptRunner.STATE_RESPONSE_READY:
				this.runStep();
				break;
			case AmiWebScriptRunner.STATE_ERROR:
				if (fullVars.getDebugManager().shouldDebug(AmiDebugMessage.SEVERITY_WARNING)) {
					Throwable error = this.getException();
					Map<Object, Object> m = CH.m();
					String message = OH.toString(this.getThrown());
					if (error instanceof ExpressionParserException)
						message = ((ExpressionParserException) error).toLegibleString();
					fullVars.getDebugManager().addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, this.fullVars.getSourceDebugType(), this.fullVars.getSourceAri(),
							fullVars.getCallbackName(), "Thrown: " + message, m, error));
				}
				this.endNanos = System.nanoTime();
				break;
			case AmiWebScriptRunner.STATE_DONE:
				this.endNanos = System.nanoTime();
				break;
			case AmiWebScriptRunner.STATE_DEBUG:
			case AmiWebScriptRunner.STATE_REQUEST_SENT:
			case AmiWebScriptRunner.STATE_INIT:
				break;
		}
		for (int i = 0; i < this.listeners.size(); i++)
			this.listeners.get(i).onScriptRunStateChanged(this, old, state);
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> reslt) {
		if (isHalted)
			return;
		if (this.concurrentRequests != null) {
			int i = AH.indexOfByIdentity(reslt.getRequestMessage().getAction(), this.concurrentRequests, 0);
			if (i == -1) {
				LH.info(log, describeSource(), " Dropping old Response");
				return;
			}
			if (concurrentResponses[i] != null)
				throw new IllegalStateException();
			concurrentResponses[i] = reslt;
			if (--this.concurrentRequestsRemaining > 0)
				return;
			try {
				for (int n = 0; n < concurrentSteps.length; n++) {
					try {
						onResponse(this.concurrentResponses[n], this.concurrentSteps[n]);
						this.concurrentSteps[n].resume();
					} catch (Exception e) {
						this.setError(this.concurrentSteps[n].getPosition(), e);
						return;
					}
				}
				this.concurrentRequests = null;
				this.concurrentResponses = null;
				this.concurrentSteps = null;
				setState(STATE_RESPONSE_READY);
			} catch (Exception e) {
				this.setError(this.step.getPosition(), e);
				return;
			}
		} else {
			if (reslt.getRequestMessage().getAction() != this.request) {
				LH.info(log, describeSource(), " Dropping old Response");
				return;
			}
			try {
				onResponse(reslt, this.step);
				setState(STATE_RESPONSE_READY);
			} catch (Exception e) {
				this.setError(this.step.getPosition(), e);
				return;
			}
		}
	}

	private void onResponse(ResultMessage<Action> result, FlowControlPause step) {
		Tableset ts = AmiUtils.getExecuteInstance(step).getTableset();
		if (step instanceof AmiFlowControlPause) {
			AmiFlowControlPause efcp = (AmiFlowControlPause) step;
			AmiCenterResponse res = (AmiCenterResponse) result.getActionNoThrowable();
			//				AmixecuteDerivedCellCalculator ec = efcp.getExecCalcs()[i];
			if (result.getError() != null || res == null || !res.getOk()) {
				String msg;
				if (res != null && res.getMessage() != null)
					msg = "From center: " + res.getMessage();
				else if (result.getError() != null)
					msg = "From center: " + result.getError().getMessage();
				else if (efcp instanceof AmiFlowControlPauseSql)
					msg = "Remote call to '" + ((AmiFlowControlPauseSql) efcp).getDatasourceName() + "' failed: ";
				else
					msg = "Unknown Error";
				efcp.processErrorResponse(msg);
			} else {
				AmiCenterQueryDsRequest request = (AmiCenterQueryDsRequest) result.getRequestMessage().getAction();
				AmiCenterQueryDsResponse response = (AmiCenterQueryDsResponse) result.getAction();
				applyDataFilters(request, response);
				debugResponse(request, response);
				efcp.processResponse(response);
			}
		} else if (step instanceof AmiCallCommandFlowControlPause) {
			AmiCallCommandFlowControlPause efcp = (AmiCallCommandFlowControlPause) step;
			AmiCenterResponse res = (AmiCenterResponse) result.getActionNoThrowable();
			if (result.getError() != null || res == null || !res.getOk()) {
				AmiCenterPassToRelayResponse response = (AmiCenterPassToRelayResponse) result.getAction();
				AmiRelayRunAmiCommandResponse cmdResponse = response == null ? null : (AmiRelayRunAmiCommandResponse) response.getAgentResponse();
				if (cmdResponse == null) {
					response = service.nw(AmiCenterPassToRelayResponse.class);
					AmiRelayRunAmiCommandResponse cr = service.nw(AmiRelayRunAmiCommandResponse.class);
					response.setAgentResponse(cr);
					cr.setStatusCode(AmiRelayRunAmiCommandResponse.STATUS_GENERAL_ERROR);
				}
				efcp.processResponse(response, ts);
			} else {
				AmiCenterPassToRelayResponse response = (AmiCenterPassToRelayResponse) result.getAction();
				efcp.processResponse(response, ts);
			}
		} else if (step instanceof AmiSendEmailFlowControlPause) {
			AmiSendEmailFlowControlPause efcp = (AmiSendEmailFlowControlPause) step;
			AmiCenterResponse res = (AmiCenterResponse) result.getActionNoThrowable();
			if (result.getError() != null || res == null || !res.getOk()) {
				AmiCenterPassToRelayResponse response = (AmiCenterPassToRelayResponse) result.getAction();
				AmiRelaySendEmailResponse cmdResponse = response == null ? null : (AmiRelaySendEmailResponse) response.getAgentResponse();
				if (cmdResponse == null) {
					response = service.nw(AmiCenterPassToRelayResponse.class);
					AmiRelaySendEmailResponse cr = service.nw(AmiRelaySendEmailResponse.class);
					response.setAgentResponse(cr);
					cr.setOk(false);
					cr.setMessage("UNKNOWN_ERROR");
				}
				efcp.processResponse(response, ts);
			} else {
				AmiCenterPassToRelayResponse response = (AmiCenterPassToRelayResponse) result.getAction();
				efcp.processResponse(response, ts);
			}
		} else
			throw new IllegalStateException();
	}

	private void debugResponse(AmiCenterQueryDsRequest request, AmiCenterQueryDsResponse response) {
		if (fullVars.getDebugManager().shouldDebug(AmiDebugMessage.SEVERITY_INFO)) {
			Map<Object, Object> details = new HashMap<Object, Object>();
			details.put("Original Query", request.getQuery());
			if (response.getTables() != null) {
				for (Table c : response.getTables()) {
					StringBuilder s = new StringBuilder();
					s.append("Table Name: " + c.getTitle()).append('\n');
					s.append("Rows Count: " + c.getSize()).append('\n');
					s.append("Columns Count: " + c.getColumnsCount()).append('\n');
					s.append("Schema: ");
					for (int n = 0; n < c.getColumnsCount(); n++) {
						if (n != 0)
							s.append(", ");
						Column col = c.getColumnAt(n);
						s.append(fullVars.getFactory().forType(col.getType()));
						s.append(' ').append(col.getId());
					}
					s.append('\n');
					if (c.getSize() * c.getColumnsCount() < 10000) {
						s.append("Data: \n");
						c.toString(s);
					} else
						s.append("Data: <Suppressing, too much data>\n");
					details.put("Table: " + c.getTitle(), s.toString());
				}
			}
			fullVars.getDebugManager().addMessage(
					new AmiDebugMessage(AmiDebugMessage.SEVERITY_INFO, AmiDebugMessage.TYPE_DATASOURCE_RESPONSE, this.fullVars.getSourceAri(), fullVars.getCallbackName(),
							"Response from " + request.getDatasourceName() + " (" + (System.currentTimeMillis() - request.getRequestTime()) + " millis)", details, null));
		}
	}

	private boolean applyDataFilters(AmiCenterQueryDsRequest r) {
		final AmiWebDataFilter filter = this.service.getDataFilter();
		if (filter == null)
			return true;
		AmiWebDataFilterQuery awq1 = toFilterQuery(r);
		AmiWebDataFilterQuery awq2;
		try {
			awq2 = filter.evaluateQueryRequest(awq1);
		} catch (Throwable e) {
			LH.warning(log, describeSource(), " Filter plugin '", filter, "', threw exception: ", e);
			return false;
		}
		if (awq2 == null) {
			LH.info(log, describeSource(), " DataFilter Plugin rejected query: ", awq1);
			return false;
		}
		if (awq1 != awq2) {
			if (OH.ne(awq1.getDatasourceName(), awq2.getDatasourceName()) || //
					OH.ne(awq1.getQuery(), awq2.getQuery()) || //
					OH.ne(awq1.getUseDirectives(), awq2.getUseDirectives()) || //
					OH.ne(awq1.getDatasourceOverrideAdapter(), awq2.getDatasourceOverrideAdapter()) || //
					OH.ne(awq1.getDatasourceOverrideOptions(), awq2.getDatasourceOverrideOptions()) || //
					OH.ne(awq1.getDatasourceOverridePassword(), awq2.getDatasourceOverridePassword()) || //
					OH.ne(awq1.getDatasourceOverridePasswordEnc(), awq2.getDatasourceOverridePasswordEnc()) || //
					OH.ne(awq1.getDatasourceOverrideRelay(), awq2.getDatasourceOverrideRelay()) || //
					OH.ne(awq1.getDatasourceOverrideUrl(), awq2.getDatasourceOverrideUrl()) || //
					OH.ne(awq1.getDatasourceOverrideUsername(), awq2.getDatasourceOverrideUsername())) {
				LH.info(log, describeSource(), " DataFilter Plugin adjusted query: ", awq1, " ==> ", awq2);
				r.setQuery(awq2.getQuery());
				r.setDirectives(awq2.getUseDirectives());
				r.setDatasourceName(awq2.getDatasourceName());
				r.setDatasourceOverrideAdapter(awq2.getDatasourceOverrideAdapter());
				r.setDatasourceOverrideOptions(awq2.getDatasourceOverrideOptions());
				r.setDatasourceOverridePassword(awq2.getDatasourceOverridePassword());
				r.setDatasourceOverridePasswordEnc(awq2.getDatasourceOverridePasswordEnc());
				r.setDatasourceOverrideRelay(awq2.getDatasourceOverrideRelay());
				r.setDatasourceOverrideUrl(awq2.getDatasourceOverrideUrl());
				r.setDatasourceOverrideUsername(awq2.getDatasourceOverrideUsername());
			}
		}
		return true;
	}
	private void applyDataFilters(AmiCenterQueryDsRequest request, AmiCenterQueryDsResponse response) {
		final AmiWebDataFilter filter = this.service.getDataFilter();
		if (filter != null) {
			List<Table> tables = response.getTables();
			if (tables != null) {
				AmiWebDataFilterQuery awq = toFilterQuery(request);
				for (Table col : tables) {
					if (col != null) {
						if (col instanceof ColumnarTable) {
							try {
								filter.evaluateQueryResponse(awq, (ColumnarTable) col);
							} catch (Throwable e) {
								LH.warning(log, describeSource(), " Filter plugin '", filter, "', threw exception for table '", col.getTitle(), "': ", e);
							}
						} else {
							LH.warning(log, describeSource(), " Result set not a  columnar Table so can't apply filter: ", col.getTitle(), " is type ", col.getClass().getName());
						}
					}
				}
			}
		}
	}

	static private AmiWebDataFilterQuery toFilterQuery(AmiCenterQueryDsRequest request) {
		AmiWebDataFilterQuery awq = new AmiWebDataFilterQueryImpl(request);
		return awq;
	}
	//	private String resolveDefaultDatasource() {
	//		return this.defaultDatasource;
	//	}
	//	public String getDefaultDatasource() {
	//		return this.defaultDatasource;
	//	}
	public AmiCalcFrameStack getVars() {
		return this.fullVars;
	}
	private AmiCenterQueryDsRequest toRequests(AmiWebService s, AmiFlowControlPause p) {
		AmiCalcFrameStack lcvs = AmiUtils.getExecuteInstance(p);
		//		AmiExecuteDerivedCellCalculator[] execs = p.getExecCalcs();
		//		AmiCenterQueryDsRequest[] r = new AmiCenterQueryDsRequest[execs.length];
		//		for (int i = 0; i < execs.length; i++) {
		//			AmiCenterQueryDsRequest req = toRequest(s, execs[i], lcvs, p.getAttachment());
		//			if (req == null)
		//				throw new RuntimeException("DataFilter plugin denied query");
		//			r[i] = req;
		//		}
		AmiCenterQueryDsRequest request2 = (AmiCenterQueryDsRequest) p.toRequest(s.getPortletManager().getTools());
		request2.setOriginType(AmiCenterQueryDsRequest.ORIGIN_FRONTEND);
		request2.setIsTest(false);
		if (!applyDataFilters(request2))
			throw new RuntimeException("DataFilter plugin denied query");
		return request2;
	}
	//	private AmiCenterQueryDsRequest toRequest(AmiWebService s, AmiExecuteDerivedCellCalculator execCalc, AmiCalcFrameStack lcvs, Object attachment) {
	//		AmiCenterQueryDsRequest r = execCalc.toRequest(lcvs, AmiCenterQueryDsRequest.PERMISSIONS_FULL, attachment);
	//		r.setOriginType(AmiCenterQueryDsRequest.ORIGIN_FRONTEND);
	//		r.setIsTest(false);
	//		AmiDebugManager debugManager = this.fullVars.getDebugManager();
	//		if (debugManager.shouldDebug(AmiDebugMessage.SEVERITY_INFO)) {
	//			Map<Object, Object> m = new LinkedHashMap();
	//			if (SH.is(r.getQuery()))
	//				m.put("Query", r.getQuery());
	//			if (CH.isntEmpty(r.getDirectives()))
	//				for (Entry<String, String> t : r.getDirectives().entrySet())
	//					m.put("Directive: " + t.getKey(), t.getValue());
	//			m.put("Limit", r.getLimit() == SqlProcessor.NO_LIMIT ? "NO_LIMIT" : r.getLimit());
	//			m.put("Timeout", r.getTimeoutMs() == AmiCenterQueryDsRequest.NO_TIMEOUT ? "NO_TIMEOUT" : r.getTimeoutMs());
	//			byte type = -1;
	//			switch (r.getType()) {
	//				case AmiCenterQueryDsRequest.TYPE_UPLOAD: {
	//					List<AmiCenterUploadTable> uploads = r.getUploadValues();
	//					if (CH.isntEmpty(uploads)) {
	//						int n = 1;
	//						for (AmiCenterUploadTable i : uploads) {
	//							StringBuilder str = new StringBuilder();
	//							str.append("INSERT INTO ").append(i.getTargetTable());
	//							if (CH.isntEmpty(i.getTargetColumns())) {
	//								str.append(" (");
	//								SH.join(',', i.getTargetColumns(), str);
	//								str.append(") ");
	//							}
	//							Columns table = i.getData();
	//							str.append(" VALUES (").append(table.getColumnsCount()).append(" cols x ").append(table.getSize()).append(" rows)");
	//							if (n++ == 1)
	//								m.put("Query", str.toString());
	//							else
	//								m.put("Query" + n, str.toString());
	//						}
	//					}
	//					type = AmiDebugMessage.TYPE_DATASOURCE_UPLOAD;
	//					break;
	//				}
	//				case AmiCenterQueryDsRequest.TYPE_SHOW_TABLES:
	//					m.put("Query", "SHOW TABLES");
	//					type = AmiDebugMessage.TYPE_DATASOURCE_SHOW_TABLES;
	//					break;
	//				case AmiCenterQueryDsRequest.TYPE_PREVIEW:
	//					type = AmiDebugMessage.TYPE_DATASOURCE_PREVIEW;
	//					break;
	//				case AmiCenterQueryDsRequest.TYPE_QUERY:
	//				default:
	//					type = AmiDebugMessage.TYPE_DATASOURCE_QUERY;
	//			}
	//			debugManager.addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_INFO, type, this.fullVars.getSourceAri(), this.fullVars.getCallbackName(),
	//					"Sent Query To " + r.getDatasourceName(), m, null));
	//		}
	//		if (!applyDataFilters(r)) {
	//			return null;
	//		}
	//		return r;
	//	}

	public Object getThrown() {
		return this.amiError;
	}

	public byte getState() {
		return this.state;
	}
	public Object getReturnValue() {
		return this.returnValue;
	}

	public void onRestResponse(AmiCallRestFlowControlPause amiCallRestFlowControlPause) {
		setState(STATE_RESPONSE_READY);
	}

	private void clearError() {
		this.flowControlThrow = null;
		this.amiError = null;
		this.error = null;
	}
	private void setError(DerivedCellCalculator position, Exception e) {
		if (e instanceof FlowControlThrow) {
			this.flowControlThrow = (FlowControlThrow) e;
			this.flowControlThrow.getTailFrame().setOriginalSourceCode(this.getCallbackAri(), this.script);

			this.amiError = this.flowControlThrow.getThrownValue();
			this.error = flowControlThrow;
		} else {
			this.flowControlThrow = new FlowControlThrow(position, null, e);
			this.flowControlThrow.getHeadFrame().setOriginalSourceCode(this.getCallbackAri(), this.script);
			this.amiError = e;
			this.error = e;
		}
		this.setState(STATE_ERROR);
		return;
	}

	public Exception getException() {
		return this.error;
	}

	@Override
	public void onScriptRunStateChanged(AmiWebScriptRunner amiWebScriptRunner, byte oldState, byte state) {
		if (this.step == amiWebScriptRunner) {
			switch (state) {
				case STATE_DONE:
				case STATE_ERROR:
					amiWebScriptRunner.removeListener(this);
					setState(STATE_RESPONSE_READY);
			}
		}
	}

	public void halt() {
		if (isHalted)
			return;
		this.isHalted = true;
		setState(STATE_DONE);
	}
	public boolean isHalted() {
		return this.isHalted;
	}

	//	public byte getSourceDebugType() {
	//		return sourceDebugType;
	//	}
	//	public String getSourceAri() {
	//		return this.thiz.getAri();
	//	}
	public String getCallbackAri() {
		String callbackName = this.fullVars.getCallbackName();
		switch (this.fullVars.getSourceDebugType()) {
			case AmiDebugMessage.TYPE_CALLBACK:
				return this.fullVars.getThis().getAmiScriptCallbacks().getCallback(callbackName).getAri();
			case AmiDebugMessage.TYPE_FORMULA:
				return this.fullVars.getThis().getFormulas().getFormula(callbackName).getAri();
			default:
				return AmiDebugMessage.getTypeAsString(this.fullVars.getSourceDebugType()) + " ==> " + callbackName;
		}
	}
	//	public String getCallbackName() {
	//		return callback;
	//	}
	//	public DerivedCellTimeoutController getTimeoutController() {
	//		return this.timeoutController;
	//	}

	public FlowControlPause getStep() {
		return this.step;
	}

	public AmiWebService getService() {
		return this.service;
	}

	public FlowControlThrow getFlowControlThrow() {
		return flowControlThrow;
	}

	//	public AmiWebScriptManagerForLayout getLayout() {
	//		return this.scriptManager;
	//	}

	public CalcFrame getWriteableVars() {
		return this.fullVars.getGlobal();
	}

}
