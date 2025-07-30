package com.f1.ami.center;

import java.util.List;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.ds.AmiDatasourceTrackerImpl;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.ami.center.table.AmiCenterProcess;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiImdbSessionManagerService;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.base.Action;
import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellTimeoutController;
import com.f1.utils.structs.table.derived.DerivedHelper;

public class AmiCenterQueryAmiItinerary extends AbstractAmiCenterItinerary<AmiCenterQueryDsRequest> implements AmiCenterProcess {

	private static final Logger log = LH.get();

	private AmiCenterQueryDsResponse r;

	private static final byte STEP_PROCESSING_EMBEDDED_EXECUTE = 4;
	private static final byte STEP_QUERY = 5;

	private byte step;
	private long processId = -1L;
	private long startTime;
	private long sessionId;

	private AmiDatasourceAdapterForAmi adapter;

	private long startTimeNano;

	private DerivedCellTimeoutController timeout;

	@Override
	public long getProcessId() {
		return this.processId;
	}
	@Override
	public long getStartTime() {
		return startTime;
	}

	@Override
	public long getSessionId() {
		return sessionId;
	}

	public byte getStep() {
		return this.step;
	}
	@Override
	public String getProcessStatus() {
		switch (step) {
			case STEP_PROCESSING_EMBEDDED_EXECUTE:
				return PROCESS_WAIT_SUBPROCESS;
			case STEP_QUERY:
				return PROCESS_RUN;
			default:
				return "STEP " + SH.toString(step);
		}
	}

	@Override
	public byte startJourney(AmiCenterItineraryWorker worker) {
		AmiCenterQueryDsRequest action = this.getInitialRequest().getAction();
		if (!AmiConsts.DATASOURCE_NAME_AMI.equals(action.getDatasourceName()))
			throw new IllegalStateException();
		AmiCenterState state = getState();
		this.sessionId = action.getQuerySessionId();
		this.processId = state.createNextProcessId();
		this.startTime = System.currentTimeMillis();
		this.startTimeNano = System.nanoTime();
		if (isReadonly())
			state.addProcessThreadSafe(this);
		else
			state.addProcess(this);
		r = state.nw(AmiCenterQueryDsResponse.class);
		r.setOk(true);
		state.incrementAmiMessageStats(AmiCenterState.STATUS_TYPE_QUERY_DATASOURCE);
		if (SH.isnt(action.getInvokedBy())) {
			r.setMessage("Username required");
			r.setOk(false);
			return STATUS_COMPLETE;
		}
		this.timeout = new DerivedCellTimeoutController(AmiUtils.toTimeout(action.getTimeoutMs(), state.getDefaultDatasourceTimeout()));
		this.step = STEP_QUERY;
		try {
			final byte permissions = action.getPermissions();
			AmiImdbSession session = null;
			AmiImdbSessionManagerService sessionManager = state.getAmiImdb().getSessionManager();
			if (getSessionId() > 0)
				session = sessionManager.getSession(getSessionId());
			if (session == null) {
				int limit = getInitialRequest().getAction().getLimit();
				if (action.getQuerySessionKeepAlive())
					session = sessionManager.newSession(AmiTableUtils.DEFTYPE_USER, action.getOriginType(), action.getInvokedBy(),
							AmiTableUtils.toStringForOriginType(action.getOriginType()), permissions, this.getTimeoutMs(), limit,
							DerivedHelper.toFrame(action.getSessionVariables(), action.getSessionVariableTypes()));
				else {
					session = sessionManager.newTempSession(AmiTableUtils.DEFTYPE_USER, action.getOriginType(), action.getInvokedBy(),
							AmiTableUtils.toStringForOriginType(action.getOriginType()), permissions, this.getTimeoutMs(), limit,
							DerivedHelper.toFrame(action.getSessionVariables(), action.getSessionVariableTypes()));
				}
				sessionId = session.getSessionId();
			} else if ((session.getPermissions() | permissions) != session.getPermissions())
				throw new ExpressionParserException(0, "Illegal widening of permissions");
			AmiDatasourceTrackerImpl tracker = action.getIsTest() ? new AmiDatasourceTrackerImpl(getTools()) : null;
			this.adapter = new AmiDatasourceAdapterForAmi(this, worker, session, r, tracker);
			switch (action.getType()) {
				case AmiCenterQueryDsRequest.TYPE_SHOW_TABLES: {
					List<AmiDatasourceTable> tables = adapter.getTables();
					r.setPreviewTables(tables);
					r.setDurrationNanos(System.nanoTime() - this.startTimeNano);
					return STATUS_COMPLETE;
				}
				case AmiCenterQueryDsRequest.TYPE_PREVIEW: {
					List<AmiDatasourceTable> tables = adapter.getPreviewData(action.getTablesForPreview(), action.getPreviewCount(), this.timeout);
					r.setPreviewTables(tables);
					r.setDurrationNanos(System.nanoTime() - this.startTimeNano);
					return STATUS_COMPLETE;
				}
				case AmiCenterQueryDsRequest.TYPE_QUERY: {
					adapter.processQuery(action, this.timeout);
					break;
				}
				case AmiCenterQueryDsRequest.TYPE_UPLOAD: {
					adapter.processUpload(action);
					break;
				}
			}
			if (adapter.getPaused() != null) {
				this.step = STEP_PROCESSING_EMBEDDED_EXECUTE;
				return STATUS_ACTIVE;
			}
			if (!isReadonly())
				state.onProcessedEventsComplete();
			return STATUS_COMPLETE;
		} catch (Exception e) {
			String ticket = getTools().generateErrorTicket();
			r.setTicket(ticket);
			LH.warning(log, "Error processing request:", getInitialRequest(), e);
			r.setMessage(e.getMessage());
			r.setOk(false);
			r.setException(e);
			return STATUS_COMPLETE;
		}
	}
	@Override
	public byte onResponse(ResultMessage<?> result, AmiCenterItineraryWorker worker) {
		switch (this.step) {
			case STEP_PROCESSING_EMBEDDED_EXECUTE: {
				if (result.getError() != null) {
					r.setException((Exception) result.getError());
					r.setDurrationNanos(System.nanoTime() - startTimeNano);
					return STATUS_COMPLETE;
				}
				Action action = result.getAction();
				// Commenting this out because an inner execute failure doesn't mean this call fails (ex: there could be a catch clause)
				//				if (action instanceof AmiResponse)
				//					AmiUtils.fillResponse((AmiResponse) action, r);
				//				else if (action instanceof RunnableResponseMessage)
				//					AmiUtils.fillResponse((RunnableResponseMessage) action, r);
				//				else {
				//					r.setOk(false);
				//					r.setMessage("Unknown response type: " + action.getClass());
				//				}
				//				if (!r.getOk()) {
				//					String query = getInitialRequest().getAction().getQuery();
				//					r.setException(new ExpressionParserException(query, adapter.getPaused().getPosition().getPosition(), r.getMessage(), r.getException()));
				//				}
				adapter.resume(action);
				if (!isReadonly())
					getState().onProcessedEventsComplete();
				if (adapter.getPaused() != null) {
					this.step = STEP_PROCESSING_EMBEDDED_EXECUTE;
					return STATUS_ACTIVE;
				} else
					return STATUS_COMPLETE;
			}
			default:
				throw new RuntimeException("Bad step: " + this.step);
		}

	}

	@Override
	public Message endJourney(AmiCenterItineraryWorker worker) {
		AmiImdbSession session = adapter.getSession();
		if (session != null) {
			long sessionId = session.getSessionId();
			if (session.getIsTemporary())
				session.close();
			r.setQuerySessionId(session.getIsTemporary() ? 0 : sessionId);
		}
		if (this.getProcessId() != -1) {
			if (isReadonly())
				this.getState().removeProcessThreadSafe(this);
			else
				this.getState().removeProcess(this);
		}
		return r;
	}
	@Override
	public int getTimeoutMs() {
		return AmiUtils.toTimeout(this.getInitialRequest().getAction().getTimeoutMs(), super.getTimeoutMs());
	}

	public long getParentProcessId() {
		return this.getInitialRequest().getAction().getParentProcessId();
	}

	@Override
	public String getQuery() {
		AmiCenterQueryDsRequest request = getInitialRequest().getAction();
		return request.getQuery();
	}
	@Override
	public String getDsName() {
		return AmiConsts.DATASOURCE_NAME_AMI;
	}
	@Override
	public String getDsRelayId() {
		return null;
	}
	public long getStartTimeNano() {
		return this.startTimeNano;
	}

	//	final public void fillResponse() {
	//		r.setDurrationNanos(System.nanoTime() - this.startTimeNano);
	//		if (this.throwable != null) {
	//			Throwable t = throwable;
	//			if (t instanceof FlowControlThrow && t.getCause() != null)
	//				t = t.getCause();
	//			r.setMessage(AmiUtils.toMessage(t));
	//			r.setOk(false);
	//			if (this.throwable instanceof Exception)
	//				r.setException(OH.toRuntime((Throwable) t));
	//		} else if (this.results != null) {
	//			AmiUtils.setReturnValues(r, this.results.getReturnType(), this.results.getReturnValue(), (List) this.results.getTables());
	//			r.setGeneratedKeys(this.results.getGeneratedKeys());
	//			r.setRowsEffected((int) this.results.getRowsEffected());
	//		}
	//		r.setDisableLogging(this.getClientReq().getDisableLogging());
	//		r.setPreviewTables(this.schema);
	//		if (tracker != null)
	//			r.setTrackedEvents(tracker.createTrackedEvents());
	//	}
}
