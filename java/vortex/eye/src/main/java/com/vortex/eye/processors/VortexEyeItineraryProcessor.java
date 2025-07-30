package com.vortex.eye.processors;

import com.f1.base.Message;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.povo.db.DbRequestMessage;
import com.f1.povo.msg.MsgMessage;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.utils.LH;
import com.f1.vortexcommon.msg.agent.TestTrackDeltas;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRequest;
import com.vortex.eye.itinerary.VortexEyeItinerary;
import com.vortex.eye.itinerary.VortexEyeItineraryWorker;
import com.vortex.eye.messages.StartVortexEyeItineraryMessage;
import com.vortex.eye.messages.VortexVaultRequest;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeItineraryProcessor extends VortexEyeBasicProcessor<Message> implements VortexEyeItineraryWorker {

	public VortexEyeItineraryProcessor() {
		super(Message.class);
	}

	@Override
	public void processAction(Message action, VortexEyeState state, ThreadScope threadScope) throws Exception {
		final byte status;
		VortexEyeItinerary<?> itinerary;
		if (action instanceof StartVortexEyeItineraryMessage) {
			StartVortexEyeItineraryMessage startItinerary = (StartVortexEyeItineraryMessage) action;
			itinerary = startItinerary.getItinerary();
			itinerary.init(state.generateNextItineraryId(), (RequestMessage) startItinerary.getInitialRequest(), state);
			state.addItinerary(itinerary);
			itinerary.auditClientEvent(this);
			status = itinerary.startJourney(this);
		} else {
			ResultMessage<?> result = (ResultMessage<?>) action;
			RequestMessage<?> request = result.getRequestMessage();
			itinerary = state.popItineraryForRequest(request);
			itinerary.removePendingRequest(request);
			status = itinerary.onResponse(result, this);
		}
		if (status == VortexEyeItinerary.STATUS_COMPLETE) {
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
	public void sendToClients(VortexEyeItinerary<?> source, TestTrackDeltas deltas) {
		super.sendToClients(deltas);
	}

	@Override
	public void sendRequestToAgent(VortexEyeItinerary<?> source, VortexAgentRequest req, String processUid) {
		if (processUid == null)
			throw new NullPointerException("processUid");
		MsgMessage msg = super.sendRequestToAgent(req, processUid, getLoopbackPort(), 0);
		RequestMessage<?> request = (RequestMessage<?>) msg.getMessage();
		source.getState().pushRequestForItinerary(source, request);
		source.addPendingRequest(request);

	}

	@Override
	public void sendToAgent(VortexEyeItinerary<?> source, Message req, String processUid) {
		if (processUid == null)
			throw new NullPointerException("processUid");
		super.sendToAgent(req, processUid);

	}

	@Override
	public String getProcessUidFromAgentResponse(VortexEyeItinerary<?> source, ResultMessage<?> result) {
		return super.getProcessUidFromAgentResponse(result);
	}

	@Override
	public void sendToDb(VortexEyeItinerary<?> source, DbRequestMessage insertToDatabase) {
		RequestMessage<DbRequestMessage> req = nw(RequestMessage.class);
		req.setResultPort(this.getLoopbackPort());
		req.setAction(insertToDatabase);
		getToDatabasePort().send(req, null);
		source.getState().pushRequestForItinerary(source, req);
		source.addPendingRequest(req);
	}

	@Override
	public <M extends Message> void startItinerary(VortexEyeItinerary<?> source, VortexEyeItinerary<M> itinerary, M action) {
		RequestMessage<M> req = nw(RequestMessage.class);
		req.setResultPort(this.getLoopbackPort());
		req.setAction(action);
		startItinerary(itinerary, req);
		source.getState().pushRequestForItinerary(source, req);
		source.addPendingRequest(req);
	}

	@Override
	public void sendToVault(VortexEyeItinerary<?> source, VortexVaultRequest vvq) {
		RequestMessage<VortexVaultRequest> req = nw(RequestMessage.class);
		req.setResultPort(this.getLoopbackPort());
		req.setAction(vvq);
		getToVaultPort().send(req, null);
		source.getState().pushRequestForItinerary(source, req);
		source.addPendingRequest(req);
	}

	@Override
	public void sendRunnable(VortexEyeItinerary<?> source, RunnableRequestMessage rm) {
		RequestMessage<RunnableRequestMessage> req = nw(RequestMessage.class);
		req.setResultPort(this.getLoopbackPort());
		req.setAction(rm);
		getToRunnablePort().send(req, null);
		source.getState().pushRequestForItinerary(source, req);
		source.addPendingRequest(req);
	}

}
