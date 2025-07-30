package com.f1.ami.center;

import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiChainedNamingServiceResolver;
import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.ds.AmiDatasourceAdapterManager;
import com.f1.ami.amicommon.ds.AmiDatasourceRunner;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse;
import com.f1.ami.amicommon.msg.AmiRelayRunDbRequest;
import com.f1.ami.amicommon.msg.AmiRelayRunDbResponse;
import com.f1.ami.center.table.AmiCenterProcess;
import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.povo.standard.RunnableResponseMessage;
import com.f1.utils.LH;
import com.f1.utils.SH;

public class AmiCenterQueryDsItinerary extends AbstractAmiCenterItinerary<AmiCenterQueryDsRequest> implements AmiCenterProcess {

	private static final Logger log = LH.get();

	private AmiCenterQueryDsResponse r;

	private AmiDatasourceRunner runner;

	private static final byte STEP_NAME_RESOLVING = 1;
	private static final byte STEP_SENT_TO_RELAY = 2;
	private static final byte STEP_SENT_TO_DB = 3;
	private static final byte STEP_QUERY = 5;

	private AmiRelayRunDbRequest agentRequest;

	private byte step;
	private long processId = -1L;
	private long startTime;
	private long sessionId;

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
			case STEP_NAME_RESOLVING:
				return PROCESS_WAIT_NAME_SERVICE;
			case STEP_SENT_TO_RELAY:
				return PROCESS_WAIT_RELAY;
			case STEP_SENT_TO_DB:
				return PROCESS_WAIT_DB;
			case STEP_QUERY:
				return PROCESS_RUN;
			default:
				return "STEP " + SH.toString(step);
		}
	}

	@Override
	public byte startJourney(AmiCenterItineraryWorker worker) {
		this.sessionId = getInitialRequest().getAction().getQuerySessionId();
		this.processId = getState().createNextProcessId();
		this.startTime = System.currentTimeMillis();
		if (isReadonly())
			getState().addProcessThreadSafe(this);
		else
			getState().addProcess(this);
		AmiCenterState state = getState();
		r = state.nw(AmiCenterQueryDsResponse.class);
		r.setOk(true);
		state.incrementAmiMessageStats(AmiCenterState.STATUS_TYPE_QUERY_DATASOURCE);
		AmiCenterQueryDsRequest request = getInitialRequest().getAction();
		if (SH.isnt(request.getInvokedBy())) {
			r.setMessage("Username required");
			r.setOk(false);
			return STATUS_COMPLETE;
		}
		if (AmiConsts.DATASOURCE_NAME_AMI.equals(request.getDatasourceName()))
			throw new IllegalStateException();
		if ((request.getPermissions() & AmiCenterQueryDsRequest.PERMISSIONS_EXECUTE) == 0) {
			r.setMessage("Permission denied: EXECUTE required");
			r.setOk(false);
			return STATUS_COMPLETE;
		}
		try {
			this.agentRequest = AmiCenterUtils.toRunDbRequest_ThreadSafe(state, request, 0, request.getDatasourceName(), r, getTimeoutMs());
		} catch (Exception e) {
			r.setMessage("Error processing request: " + e.getMessage());
			r.setOk(false);
			return STATUS_COMPLETE;
		}
		if (!r.getOk())
			return STATUS_COMPLETE;

		AmiServiceLocator locator = AmiUtils.newServiceLocator(agentRequest);
		AmiChainedNamingServiceResolver nsr = state.getNamingServiceResolver();
		if (nsr.canResolve(locator)) {
			worker.sendRunnable(this, state.getDatasourceManager().newLocatorRunnable(locator, agentRequest));
			this.step = STEP_NAME_RESOLVING;
			return STATUS_ACTIVE;
		} else {
			return doQuery(worker);
		}

	}

	private byte doQuery(AmiCenterItineraryWorker worker) {
		this.step = STEP_QUERY;
		AmiCenterState state = getState();
		AmiCenterQueryDsRequest action = this.getInitialRequest().getAction();
		try {
			String agentProcessUid = AmiCenterUtils.getRelayStateProcessUid(state, agentRequest, r);
			if (!r.getOk())
				return STATUS_COMPLETE;
			if (agentProcessUid != null) {
				worker.sendRequestToAgent(this, agentRequest, agentProcessUid);
				this.step = STEP_SENT_TO_RELAY;
				return STATUS_ACTIVE;
			} else {
				AmiDatasourceAdapterManager dsm = state.getDatasourceManager();
				AmiDatasourceAdapter adapter = dsm.createDatasourceAdapter(agentRequest.getDsAdapter(), r);
				if (!r.getOk())
					return STATUS_COMPLETE;
				this.runner = new AmiDatasourceRunner(getTools(), adapter, agentRequest, action, this.isReadonly(), state.getLogQueryMaxChars(),
						this.getState().getDefaultDatasourceTimeout());
				worker.sendRunnable(this, dsm.createRunnableMessage(agentRequest, this.runner));
				this.step = STEP_SENT_TO_DB;
				return STATUS_ACTIVE;
			}
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
			case STEP_NAME_RESOLVING: {
				RunnableResponseMessage msg = (RunnableResponseMessage) result.getAction();
				AmiUtils.fillResponse(msg, r);
				if (!r.getOk())
					return STATUS_COMPLETE;
				return doQuery(worker);
			}
			case STEP_SENT_TO_DB: {
				RunnableResponseMessage msg = (RunnableResponseMessage) result.getAction();
				AmiUtils.fillResponse(msg, r);
				if (!r.getOk())
					return STATUS_COMPLETE;
				this.runner.fillResponse(r);
				return STATUS_COMPLETE;
			}
			case STEP_SENT_TO_RELAY: {
				AmiRelayRunDbResponse res = (AmiRelayRunDbResponse) result.getAction();
				AmiUtils.fillResponse(res, r);
				if (!r.getOk())
					return STATUS_COMPLETE;
				r = (AmiCenterQueryDsResponse) res.getClientResponse();
				return STATUS_COMPLETE;
			}
			default:
				throw new RuntimeException("Bad step: " + this.step);
		}

	}

	@Override
	public Message endJourney(AmiCenterItineraryWorker worker) {
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

	public AmiRelayRunDbRequest getAgentRequest() {
		return this.agentRequest;
	}

	public long getParentProcessId() {
		return this.getInitialRequest().getAction().getParentProcessId();
	}
	@Override
	public String getQuery() {
		return this.agentRequest.getClientRequest().getQuery();
	}
	@Override
	public String getDsName() {
		return this.agentRequest.getDsName();
	}
	@Override
	public String getDsRelayId() {
		return this.getAgentRequest().getDsRelayId();
	}

}
