package com.vortex.agent.processors;

import com.f1.base.Message;
import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultMessage;
import com.f1.container.ResultOutputPort;
import com.f1.container.impl.BasicResultProcessor;
import com.f1.povo.f1app.reqres.F1AppRequest;
import com.f1.povo.f1app.reqres.F1AppResponse;
import com.f1.povo.msg.MsgMessage;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.povo.standard.TextMessage;
import com.vortex.agent.itinerary.VortexAgentItinerary;
import com.vortex.agent.messages.StartVortexAgentItineraryMessage;
import com.vortex.agent.messages.VortexAgentDeploymentUpdateMessage;
import com.vortex.agent.messages.VortexAgentOsAdapterRequest;
import com.vortex.agent.messages.VortexAgentOsAdapterResponse;
import com.vortex.agent.state.VortexAgentState;

public abstract class VortexAgentResultProcessor<A extends Message> extends BasicResultProcessor<A, VortexAgentState> implements VortexAgentProcessor<ResultMessage<A>> {

	final private OutputPort<Message> toEyePort = newOutputPort(Message.class).setName("toEyePort");
	final private ResultOutputPort<Message> responseRoutingPort = newResultOutputPort(Message.class).setName("responseRoutingPort");
	final private OutputPort<MsgMessage> toF1AppOutputport = newOutputPort(MsgMessage.class).setName("toF1AppOutputPort");
	final private ResultOutputPort<A> loopback;
	final private RequestOutputPort<VortexAgentOsAdapterRequest, VortexAgentOsAdapterResponse> toOsAdapterOutputPort = newRequestOutputPort(VortexAgentOsAdapterRequest.class,
			VortexAgentOsAdapterResponse.class);
	final private OutputPort<VortexAgentDeploymentUpdateMessage> toDeploymentsPort = newOutputPort(VortexAgentDeploymentUpdateMessage.class);
	private final RequestOutputPort<RunnableRequestMessage, TextMessage> runnablePort = newRequestOutputPort(RunnableRequestMessage.class, TextMessage.class);

	public VortexAgentResultProcessor(Class<A> actionType) {
		super(actionType, VortexAgentState.class);
		toF1AppOutputport.setConnectionOptional(true);
		loopback = newResultOutputPort(actionType).setName("loopbackPort");
	}

	@Override
	public OutputPort<MsgMessage> getToF1AppOutputPort() {
		return toF1AppOutputport;
	}

	@Override
	public ResultOutputPort<A> getLoopbackPort() {
		return loopback;
	}

	@Override
	public OutputPort<Message> getToEyePort() {
		return toEyePort;
	}

	@Override
	public ResultOutputPort<Message> getResponseRoutingPort() {
		return responseRoutingPort;
	}

	@Override
	public void sendToEye(Message deltas) {
		toEyePort.send(deltas, null);
	}

	@Override
	public MsgMessage sendRequestToF1App(F1AppRequest req, String processUid, OutputPort<?> responsePort) {
		if (processUid == null)
			throw new NullPointerException("processUid");
		final RequestMessage<Message> request = nw(RequestMessage.class);
		request.setResultPort(responsePort);
		request.setAction(req);
		req.setTargetF1AppProcessUid(processUid);
		final MsgMessage msg = nw(MsgMessage.class);
		msg.setMessage(request);
		msg.setRequestTopicSuffix(processUid);
		toF1AppOutputport.send(msg, null);
		return msg;
	}

	@Override
	public void sendToF1App(Message req, String processUid) {
		MsgMessage msg = nw(MsgMessage.class);
		msg.setMessage(req);
		msg.setRequestTopicSuffix(processUid);
		toF1AppOutputport.send(msg, null);
	}

	@Override
	public String getProcessUidFromF1AppResponse(ResultMessage<?> result) {
		F1AppRequest request = (F1AppRequest) result.getRequestMessage().getAction();
		return request.getTargetF1AppProcessUid();
	}

	public final OutputPort<StartVortexAgentItineraryMessage> startItineraryOutputPort = newOutputPort(StartVortexAgentItineraryMessage.class);

	@Override
	public OutputPort<StartVortexAgentItineraryMessage> getStartItineraryOutputPort() {
		return startItineraryOutputPort;
	}

	@Override
	public <M extends Message> void startItinerary(VortexAgentItinerary<M> itinerary, RequestMessage<M> action) {
		StartVortexAgentItineraryMessage startMessage = nw(StartVortexAgentItineraryMessage.class);
		startMessage.setInitialRequest(action);
		startMessage.setItinerary(itinerary);
		startItineraryOutputPort.send(startMessage, null);
	}
	@Override
	public RequestOutputPort<VortexAgentOsAdapterRequest, VortexAgentOsAdapterResponse> getToOsAdapterOutputPort() {
		return toOsAdapterOutputPort;
	}

	@Override
	public RequestMessage<VortexAgentOsAdapterRequest> sendRequestToOsAdapter(VortexAgentOsAdapterRequest req, OutputPort<? super VortexAgentOsAdapterResponse> responsePort) {
		RequestMessage<VortexAgentOsAdapterRequest> request = nw(RequestMessage.class);
		request.setAction(req);
		request.setResultPort(responsePort);
		toOsAdapterOutputPort.send(request, req.getPartitionId(), null);
		return request;
	}

	@Override
	public String getF1AppProcessUid(ResultMessage<? extends F1AppResponse> result) {
		F1AppRequest request = (F1AppRequest) result.getRequestMessage().getAction();
		return request.getTargetF1AppProcessUid();
	}

	@Override
	public OutputPort<VortexAgentDeploymentUpdateMessage> getToDeploymentsPort() {
		return toDeploymentsPort;
	}

	@Override
	public RequestOutputPort<RunnableRequestMessage, TextMessage> getToRunnablePort() {
		return this.runnablePort;
	}
}
