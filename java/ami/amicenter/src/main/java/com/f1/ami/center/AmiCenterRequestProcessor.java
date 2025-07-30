package com.f1.ami.center;

import java.util.concurrent.TimeUnit;

import com.f1.ami.amicommon.msg.AmiCenterChangesMessage;
import com.f1.ami.amicommon.msg.AmiCenterRequest;
import com.f1.ami.amicommon.msg.AmiRelayRequest;
import com.f1.ami.center.hdb.events.AmiHdbRequest;
import com.f1.ami.center.hdb.events.AmiHdbResponse;
import com.f1.base.Message;
import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultMessage;
import com.f1.container.ResultOutputPort;
import com.f1.container.State;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.povo.msg.MsgMessage;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.povo.standard.TextMessage;

public abstract class AmiCenterRequestProcessor<A extends AmiCenterRequest, S extends State, B extends Message> extends BasicRequestProcessor<A, S, B>
		implements AmiCenterProcessor<RequestMessage<A>, S> {

	private final OutputPort<MsgMessage> toAgentOutputPort = newOutputPort(MsgMessage.class);
	private final OutputPort<AmiCenterChangesMessage> toClientsPort = newOutputPort(AmiCenterChangesMessage.class);
	private final ResultOutputPort<Message> resultOutputPort = newResultOutputPort(Message.class);
	private final RequestOutputPort<RunnableRequestMessage, TextMessage> runnablePort = newRequestOutputPort(RunnableRequestMessage.class, TextMessage.class);
	private final RequestOutputPort<AmiHdbRequest, AmiHdbResponse> toHdbReqResPort = newRequestOutputPort(AmiHdbRequest.class, AmiHdbResponse.class);
	private RequestOutputPort<A, B> loopback;

	public AmiCenterRequestProcessor(Class<A> type, Class<S> state, Class<B> resultType) {
		super(type, state, resultType);
		loopback = newRequestOutputPort(type, resultType);
	}

	@Override
	public RequestOutputPort<A, B> getLoopbackPort() {
		return loopback;
	}

	public void init() {
		super.init();
	}

	@Override
	public OutputPort<MsgMessage> getToAgentOutputPort() {
		return toAgentOutputPort;
	}

	@Override
	public OutputPort<AmiCenterChangesMessage> getToClientsPort() {
		return toClientsPort;
	}

	@Override
	public void sendToClients(AmiCenterChangesMessage deltas) {
		toClientsPort.send(deltas, null);
	}

	@Override
	public MsgMessage sendRequestToAgent(AmiRelayRequest req, String processUid, OutputPort<?> responsePort, long delayMs) {
		if (processUid == null)
			throw new NullPointerException("processUid");
		req.setTargetAgentProcessUid(processUid);
		final RequestMessage<AmiRelayRequest> request = nw(RequestMessage.class);
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
	public String getProcessUidFromAgentResponse(ResultMessage<?> result) {
		AmiRelayRequest req = (AmiRelayRequest) result.getRequestMessage();
		return req.getTargetAgentProcessUid();
	}

	public final OutputPort<AmiCenterStartItineraryMessage> startItineraryOutputPort = newOutputPort(AmiCenterStartItineraryMessage.class);

	@Override
	public OutputPort<AmiCenterStartItineraryMessage> getStartItineraryOutputPort() {
		return startItineraryOutputPort;
	}

	@Override
	public <M extends Message> void startItinerary(AmiCenterItinerary<M> itinerary, RequestMessage<M> action) {
		AmiCenterStartItineraryMessage startMessage = nw(AmiCenterStartItineraryMessage.class);
		startMessage.setInitialRequest(action);
		startMessage.setItinerary(itinerary);
		startItineraryOutputPort.send(startMessage, null);
	}

	@Override
	public RequestOutputPort<RunnableRequestMessage, TextMessage> getToRunnablePort() {
		return runnablePort;
	}

	@Override
	public void sendHdbRequest(AmiHdbRequest req) {
		final RequestMessage<AmiHdbRequest> request = nw(RequestMessage.class);
		request.setAction(req);
		this.toHdbReqResPort.send(request, null);
	}

	@Override
	public RequestOutputPort<AmiHdbRequest, AmiHdbResponse> getToHdbPort() {
		return this.toHdbReqResPort;
	}

}
