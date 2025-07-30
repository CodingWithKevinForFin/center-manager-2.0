package com.f1.ami.relay;

import com.f1.base.Message;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.utils.Duration;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class AmiRelayItineraryProcessor extends AmiRelayBasicProcessor<Message> implements AmiRelayItineraryWorker {

	public AmiRelayItineraryProcessor() {
		super(Message.class);
	}

	@Override
	public void processAction(Message action, AmiRelayState state, ThreadScope threadScope) throws Exception {
		Duration d = new Duration();
		final byte status;
		AmiRelayItinerary<?> itinerary;
		if (action instanceof StartAmiRelayItineraryMessage) {
			StartAmiRelayItineraryMessage startItinerary = (StartAmiRelayItineraryMessage) action;
			itinerary = startItinerary.getItinerary();
			if (startItinerary.getIsContinue()) {
				status = itinerary.continueItinerary();
			} else {
				itinerary.init(state.generateNextItineraryId(), (RequestMessage) startItinerary.getInitialRequest(), state);
				state.addItinerary(itinerary);
				status = itinerary.startJourney(this);
			}
		} else {
			ResultMessage<?> result = (ResultMessage<?>) action;
			RequestMessage<?> request = result.getRequestMessage();
			itinerary = state.popItineraryForRequest(request);
			itinerary.removePendingRequest(request);
			status = itinerary.onResponse(result, this);
		}
		if (status == AmiRelayItinerary.STATUS_COMPLETE) {
			if (!itinerary.getPendingRequests().isEmpty())
				LH.warning(log, "Still pending requests for completed itinerary '", itinerary, "': ", itinerary.getPendingRequests());
			Message r = itinerary.endJourney(this);
			ResultMessage<Message> resultMessage = nw(ResultMessage.class);
			resultMessage.setAction(r);
			reply(itinerary.getInitialRequest(), resultMessage, threadScope);
			state.removeItinerary(itinerary);
		} else if (status == AmiRelayItinerary.STATUS_PAUSED) {
			LH.info(log, "Itinerary paused: ", OH.getSimpleClassName(itinerary));
		} else {
			if (itinerary.getPendingRequests().isEmpty()) {
				LH.warning(log, "no more pending requests for active itinerary, auto responding: ", itinerary);
				ResultMessage<?> res = nw(ResultMessage.class);
				res.setError(new RuntimeException("itinerary failed to complete: " + OH.getSimpleClassName(itinerary)));
				reply(itinerary.getInitialRequest(), res, threadScope);
				state.removeItinerary(itinerary);
			}
		}
	}


	@Override
	public <M extends Message> void startItinerary(AmiRelayItinerary<?> source, AmiRelayItinerary<M> itinerary, RequestMessage<M> action) {
		super.startItinerary(itinerary, action);
		source.getState().pushRequestForItinerary(itinerary, action);
		source.addPendingRequest(action);
	}

	@Override
	public void replyWithIntermediateResult(AmiRelayItinerary<?> source, Message m) {
		ResultMessage<Message> result = nw(ResultMessage.class);
		result.setIsIntermediateResult(true);
		result.setAction(m);
		reply(source.getInitialRequest(), result, null);
	}

	@Override
	public void sendRunnable(AmiRelayItinerary<?> source, RunnableRequestMessage rm) {
		RequestMessage<RunnableRequestMessage> req = nw(RequestMessage.class);
		req.setResultPort(this.getLoopbackPort());
		req.setAction(rm);
		getToRunnablePort().send(req, null);
		source.getState().pushRequestForItinerary(source, req);
		source.addPendingRequest(req);

	}
}
