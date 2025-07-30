package com.vortex.eye.processors;

import java.util.concurrent.TimeUnit;

import com.f1.base.Message;
import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultMessage;
import com.f1.container.ResultOutputPort;
import com.f1.container.impl.BasicProcessor;
import com.f1.povo.db.DbRequestMessage;
import com.f1.povo.db.DbResultMessage;
import com.f1.povo.msg.MsgMessage;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.povo.standard.TextMessage;
import com.f1.vortexcommon.msg.agent.TestTrackDeltas;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRequest;
import com.vortex.eye.VortexEyeDbService;
import com.vortex.eye.VortexEyeUtils;
import com.vortex.eye.itinerary.VortexEyeItinerary;
import com.vortex.eye.messages.StartVortexEyeItineraryMessage;
import com.vortex.eye.messages.VortexVaultRequest;
import com.vortex.eye.messages.VortexVaultResponse;
import com.vortex.eye.state.VortexEyeState;

public abstract class VortexEyeBasicProcessor<A extends Message> extends BasicProcessor<A, VortexEyeState> implements VortexEyeProcessor<A> {

	private VortexEyeDbService dbservice;
	private final OutputPort<MsgMessage> toAgentOutputPort = newOutputPort(MsgMessage.class);
	private final OutputPort<TestTrackDeltas> toClientsPort = newOutputPort(TestTrackDeltas.class);
	private final ResultOutputPort<Message> resultOutputPort = newResultOutputPort(Message.class);
	private final RequestOutputPort<DbRequestMessage, DbResultMessage> dbPort = newRequestOutputPort(DbRequestMessage.class, DbResultMessage.class);
	private final OutputPort<A> loopback;
	private final RequestOutputPort<VortexVaultRequest, VortexVaultResponse> vaultPort = newRequestOutputPort(VortexVaultRequest.class, VortexVaultResponse.class);
	private final RequestOutputPort<RunnableRequestMessage, TextMessage> runnablePort = newRequestOutputPort(RunnableRequestMessage.class, TextMessage.class);

	public VortexEyeBasicProcessor(Class<A> type) {
		super(type, VortexEyeState.class);
		loopback = newOutputPort(type);
	}

	@Override
	public OutputPort<A> getLoopbackPort() {
		return loopback;
	}

	@Override
	public void init() {
		super.init();
		this.dbservice = VortexEyeUtils.getVortexDb(this);
	}
	@Override
	public VortexEyeDbService getDbService() {
		return dbservice;
	}

	@Override
	public OutputPort<MsgMessage> getToAgentOutputPort() {
		return toAgentOutputPort;
	}

	@Override
	public OutputPort<TestTrackDeltas> getToClientsPort() {
		return toClientsPort;
	}

	@Override
	public void sendToClients(TestTrackDeltas deltas) {
		toClientsPort.send(deltas, null);
	}
	@Override
	public MsgMessage sendRequestToAgent(VortexAgentRequest req, String processUid, OutputPort<?> responsePort, long delayMs) {
		if (processUid == null)
			throw new NullPointerException("processUid");
		//req.setSentRequestTime(getTools().getNow());
		req.setTargetAgentProcessUid(processUid);
		final RequestMessage<VortexAgentRequest> request = nw(RequestMessage.class);
		request.setResultPort(responsePort);
		request.setAction(req);
		final MsgMessage msg = nw(MsgMessage.class);
		msg.setMessage(request);
		msg.setRequestTopicSuffix(processUid);
		toAgentOutputPort.sendDelayed(msg, null, delayMs, TimeUnit.MILLISECONDS);
		return msg;
	}
	@Override
	public void sendToAgent(Message req, String processUid) {
		MsgMessage msg = nw(MsgMessage.class);
		msg.setRequestTopicSuffix(processUid);
		msg.setMessage(req);
		toAgentOutputPort.send(msg, null);
	}

	@Override
	public ResultOutputPort<Message> getResponseRoutingPort() {
		return resultOutputPort;
	}

	@Override
	public RequestOutputPort<DbRequestMessage, DbResultMessage> getToDatabasePort() {
		return dbPort;
	}

	@Override
	public RequestOutputPort<VortexVaultRequest, VortexVaultResponse> getToVaultPort() {
		return vaultPort;
	}

	@Override
	public RequestOutputPort<RunnableRequestMessage, TextMessage> getToRunnablePort() {
		return runnablePort;
	}

	@Override
	public void sendToDb(DbRequestMessage dbRequest) {
		if (dbRequest != null)
			getToDatabasePort().request(dbRequest, null);
	}

	@Override
	public String getProcessUidFromAgentResponse(ResultMessage<?> result) {
		VortexAgentRequest req = (VortexAgentRequest) result.getRequestMessage();
		return req.getTargetAgentProcessUid();
	}

	public final OutputPort<StartVortexEyeItineraryMessage> startItineraryOutputPort = newOutputPort(StartVortexEyeItineraryMessage.class);

	@Override
	public OutputPort<StartVortexEyeItineraryMessage> getStartItineraryOutputPort() {
		return startItineraryOutputPort;
	}

	@Override
	public <M extends Message> void startItinerary(VortexEyeItinerary<M> itinerary, RequestMessage<M> action) {
		StartVortexEyeItineraryMessage startMessage = nw(StartVortexEyeItineraryMessage.class);
		startMessage.setInitialRequest(action);
		startMessage.setItinerary(itinerary);
		startItineraryOutputPort.send(startMessage, null);
	}

	public void sendToVault(VortexVaultRequest vvq, OutputPort<?> responsePort) {
		RequestMessage<VortexVaultRequest> req = nw(RequestMessage.class);
		req.setResultPort(responsePort);
		req.setAction(vvq);
		getToVaultPort().send(req, null);
	}

}
