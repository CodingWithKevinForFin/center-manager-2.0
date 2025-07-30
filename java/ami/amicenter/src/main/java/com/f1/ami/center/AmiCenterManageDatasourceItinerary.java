package com.f1.ami.center;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiChainedNamingServiceResolver;
import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.ds.AmiDatasourceAdapterManager;
import com.f1.ami.amicommon.ds.AmiDatasourceRunner;
import com.f1.ami.amicommon.msg.AmiCenterManageDatasourceRequest;
import com.f1.ami.amicommon.msg.AmiCenterManageDatasourceResponse;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.ami.amicommon.msg.AmiRelayRunDbRequest;
import com.f1.ami.amicommon.msg.AmiRelayRunDbResponse;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.povo.standard.RunnableResponseMessage;
import com.f1.utils.DBH;
import com.f1.utils.LH;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiCenterManageDatasourceItinerary extends AbstractAmiCenterItinerary<AmiCenterManageDatasourceRequest> {

	private static final Logger log = LH.get();

	private AmiCenterManageDatasourceResponse r;
	private AmiCenterManageDatasourceRequest req;
	private boolean isDelete = false;
	private boolean isEdit = false;
	private boolean addSuccessful = false;
	private long id;
	private String name;
	private String adapter;
	private String url;
	private String username;
	private String password;
	private String options;
	private AmiDatasourceRunner runner;
	private String selectedName;
	private String relayId;

	private static final byte STEP_NAME_RESOLVING = 1;
	private static final byte STEP_SENT_TO_RELAY = 2;
	private static final byte STEP_SENT_TO_DB = 3;

	private AmiRelayRunDbRequest agentRequest;

	private boolean isRelay;

	private byte step;

	private AmiImdbSession globalSession;

	private String permittedOverrides;

	@Override
	public byte startJourney(AmiCenterItineraryWorker worker) {

		AmiCenterState state = getState();
		this.globalSession = state.getAmiImdb().getGlobalSession();
		this.req = getInitialRequest().getAction();
		this.r = getState().nw(AmiCenterManageDatasourceResponse.class);
		this.r.setOk(true);
		this.isDelete = req.getDelete();
		this.isEdit = req.getEdit();
		this.id = req.getId();
		this.name = req.getName();
		this.adapter = req.getAdapter();
		this.url = req.getUrl();
		this.username = req.getUsername();
		this.password = req.getPassword();
		this.options = req.getOptions();
		this.permittedOverrides = req.getPermittedOverrides();
		this.selectedName = req.getSelectedName();
		this.relayId = req.getRelayId();
		if (req.getSkipTest() && !isDelete) {
			AmiCenterQueryDsResponse dummyRes = getState().nw(AmiCenterQueryDsResponse.class);
			dummyRes.setPreviewTables(new ArrayList<AmiDatasourceTable>());
			dummyRes.setOk(true);
			addDatasource(dummyRes);
			return STATUS_COMPLETE;
		}

		if (AmiConsts.DATASOURCE_ADAPTER_NAME_AMI.equals(this.adapter)) {
			r.setMessage("Can not edit/remove reserved datasource type: " + this.adapter);
			r.setOk(false);
			return STATUS_COMPLETE;
		}
		agentRequest = state.nw(AmiRelayRunDbRequest.class);
		final AmiCenterQueryDsRequest dummyReq2 = state.nw(AmiCenterQueryDsRequest.class);
		dummyReq2.setType(AmiCenterQueryDsRequest.TYPE_SHOW_TABLES);
		dummyReq2.setTimeoutMs(this.globalSession.getDefaultTimeoutMs());
		dummyReq2.setOriginType(AmiCenterQueryDsRequest.ORIGIN_FRONTEND);
		agentRequest.setClientRequest(dummyReq2);
		agentRequest.setDsAdapter(this.adapter);
		agentRequest.setDsName(this.name);
		agentRequest.setDsOptions(this.options);
		agentRequest.setDsPassword(this.password);
		agentRequest.setDsRelayId(this.relayId);
		agentRequest.setDsUrl(this.url);
		agentRequest.setDsUsername(this.username);
		agentRequest.setTimeoutMs(state.getDefaultDatasourceTimeout());
		agentRequest.setDsAmiId(0);

		AmiServiceLocator locator = AmiUtils.newServiceLocator(agentRequest);
		AmiChainedNamingServiceResolver nsr = state.getNamingServiceResolver();
		CalcFrameStack sf = getState().getReusableTopStackFrame();
		if (this.isDelete) {
			getState().getAmiImdb().getSystemSchema().__DATASOURCE.removeDatasource(req.getId(), sf);
			r.setOk(true);
			r.setDelete(this.isDelete);
			r.setEdit(this.isEdit);
			r.setAddSuccessful(this.addSuccessful);
			r.setName(this.name);
			r.setAdapter(this.adapter);
			r.setUrl(this.url);
			r.setUsername(this.username);
			r.setPassword(this.password);
			r.setOptions(this.options);
			r.setSelectedName(this.selectedName);
			r.setTables(null);
			return STATUS_COMPLETE;
		}
		if (nsr.canResolve(locator)) {
			worker.sendRunnable(this, state.getDatasourceManager().newLocatorRunnable(locator, agentRequest));
			this.step = STEP_NAME_RESOLVING;
			return STATUS_ACTIVE;
		} else {
			return doQuery(worker);
		}

	}
	private byte doQuery(AmiCenterItineraryWorker worker) {
		AmiCenterState state = getState();
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
				this.runner = new AmiDatasourceRunner(getTools(), adapter, agentRequest, (AmiCenterQueryDsRequest) agentRequest.getClientRequest(), true,
						this.getState().getLogQueryMaxChars(), this.globalSession.getDefaultTimeoutMs());
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

		final AmiCenterQueryDsResponse dummyRes;
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
				dummyRes = getState().nw(AmiCenterQueryDsResponse.class);
				dummyRes.setOk(true);
				this.runner.fillResponse(dummyRes);
				break;
			}
			case STEP_SENT_TO_RELAY: {
				AmiRelayRunDbResponse res = (AmiRelayRunDbResponse) result.getAction();
				AmiUtils.fillResponse(res, r);
				if (r.getMessage() != null)
					r.setMessage("From Relay " + agentRequest.getDsRelayId() + ": " + r.getMessage());
				if (!r.getOk()) {
					return STATUS_COMPLETE;
				}
				dummyRes = (AmiCenterQueryDsResponse) res.getClientResponse();
				break;
			}
			default:
				throw new RuntimeException("Bad step: " + this.step);
		}

		if (!dummyRes.getOk()) {
			AmiUtils.fillResponse(dummyRes, r);
			return STATUS_COMPLETE;
		}

		addDatasource(dummyRes);

		return STATUS_COMPLETE;
	}
	private void addDatasource(final AmiCenterQueryDsResponse dummyRes) {
		CalcFrameStack sf = getState().getReusableTopStackFrame();
		if (!this.isDelete) {
			String encrypted = getState().encrypt(this.password);
			try {
				this.addSuccessful = getState().getAmiImdb().getSystemSchema().__DATASOURCE.addDatasource(this.id, this.adapter, this.name, this.options, encrypted, this.url,
						this.username, this.isEdit, this.selectedName, this.relayId, this.permittedOverrides, sf);
			} catch (Exception e) {
				r.setException(e);
			}
		} else {
			getState().getAmiImdb().getSystemSchema().__DATASOURCE.removeDatasource(req.getId(), sf);
		}
		r.setOk(this.isDelete || this.addSuccessful);
		r.setDelete(this.isDelete);
		r.setEdit(this.isEdit);
		r.setAddSuccessful(this.addSuccessful);
		r.setName(this.name);
		r.setAdapter(this.adapter);
		r.setUrl(this.url);
		r.setUsername(this.username);
		r.setPassword(DBH.PASSWORD_MASK);
		r.setOptions(this.options);
		r.setSelectedName(this.selectedName);
		r.setTables(dummyRes.getPreviewTables());
	}

	@Override
	public Message endJourney(AmiCenterItineraryWorker worker) {
		return r;
	}

}
