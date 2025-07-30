package com.vortex.agent.processors;

import com.f1.base.Message;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.povo.f1app.reqres.F1AppRequest;
import com.f1.povo.msg.MsgMessage;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.utils.Duration;
import com.f1.utils.LH;
import com.vortex.agent.itinerary.VortexAgentItinerary;
import com.vortex.agent.itinerary.VortexAgentItineraryWorker;
import com.vortex.agent.messages.StartVortexAgentItineraryMessage;
import com.vortex.agent.messages.VortexAgentDeploymentUpdateMessage;
import com.vortex.agent.messages.VortexAgentOsAdapterRequest;
import com.vortex.agent.state.VortexAgentState;

public class VortexAgentItineraryProcessor extends VortexAgentBasicProcessor<Message> implements VortexAgentItineraryWorker {

	public VortexAgentItineraryProcessor() {
		super(Message.class);
	}

	@Override
	public void processAction(Message action, VortexAgentState state, ThreadScope threadScope) throws Exception {
		Duration d = new Duration();
		final byte status;
		VortexAgentItinerary<?> itinerary;
		if (action instanceof StartVortexAgentItineraryMessage) {
			StartVortexAgentItineraryMessage startItinerary = (StartVortexAgentItineraryMessage) action;
			itinerary = startItinerary.getItinerary();
			itinerary.init(state.generateNextItineraryId(), (RequestMessage) startItinerary.getInitialRequest(), state);
			state.addItinerary(itinerary);
			status = itinerary.startJourney(this);
		} else {
			ResultMessage<?> result = (ResultMessage<?>) action;
			RequestMessage<?> request = result.getRequestMessage();
			itinerary = state.popItineraryForRequest(request);
			itinerary.removePendingRequest(request);
			status = itinerary.onResponse(result, this);
		}
		if (status == VortexAgentItinerary.STATUS_COMPLETE) {
			if (!itinerary.getPendingRequests().isEmpty())
				LH.warning(log, "Still pending requests for completed itinerary '", itinerary, "': ", itinerary.getPendingRequests());
			Message r = itinerary.endJourney(this);
			ResultMessage<Message> resultMessage = nw(ResultMessage.class);
			resultMessage.setAction(r);
			reply(itinerary.getInitialRequest(), resultMessage, threadScope);
			state.removeItinerary(itinerary);
		} else {
			if (itinerary.getPendingRequests().isEmpty()) {
				LH.warning(log, "no more pending requests for active itinerary, auto responding: ", itinerary);
				ResultMessage<?> res = nw(ResultMessage.class);
				res.setError(new RuntimeException("itinerary failed to complete"));
				reply(itinerary.getInitialRequest(), res, threadScope);
				state.removeItinerary(itinerary);
			}
		}
	}

	@Override
	public void sendToEye(VortexAgentItinerary<?> source, Message deltas) {
		super.sendToEye(deltas);
	}

	@Override
	public void sendRequestToF1App(VortexAgentItinerary<?> source, F1AppRequest req, String processUid) {
		MsgMessage msg = super.sendRequestToF1App(req, processUid, getLoopbackPort());
		RequestMessage<?> request = (RequestMessage<?>) msg.getMessage();
		source.getState().pushRequestForItinerary(source, request);
		source.addPendingRequest(request);
	}

	@Override
	public void sendToF1App(VortexAgentItinerary<?> source, Message req, String processUid) {
		super.sendToF1App(req, processUid);
	}

	@Override
	public <M extends Message> void startItinerary(VortexAgentItinerary<?> source, VortexAgentItinerary<M> itinerary, RequestMessage<M> action) {
		super.startItinerary(itinerary, action);
		source.getState().pushRequestForItinerary(itinerary, action);
		source.addPendingRequest(action);
	}

	@Override
	public void replyWithIntermediateResult(VortexAgentItinerary<?> source, Message m) {
		ResultMessage<Message> result = nw(ResultMessage.class);
		result.setIsIntermediateResult(true);
		result.setAction(m);
		reply(source.getInitialRequest(), result, null);
	}

	@Override
	public void sendRequestToOsAdapter(VortexAgentItinerary<?> source, VortexAgentOsAdapterRequest req) {
		RequestMessage<VortexAgentOsAdapterRequest> request = super.sendRequestToOsAdapter(req, getLoopbackPort());
		source.getState().pushRequestForItinerary(source, request);
		source.addPendingRequest(request);
	}

	@Override
	public void sendToDeployment(VortexAgentItinerary vortexAgentItinerary, VortexAgentDeploymentUpdateMessage updmsg) {
		getToDeploymentsPort().send(updmsg, null);
	}

	@Override
	public void sendRunnable(VortexAgentItinerary source, RunnableRequestMessage rm) {
		RequestMessage<RunnableRequestMessage> req = nw(RequestMessage.class);
		req.setResultPort(this.getLoopbackPort());
		req.setAction(rm);
		getToRunnablePort().send(req, null);
		source.getState().pushRequestForItinerary(source, req);
		source.addPendingRequest(req);

	}
}
