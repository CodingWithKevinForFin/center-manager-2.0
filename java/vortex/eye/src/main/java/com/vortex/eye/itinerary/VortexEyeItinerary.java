package com.vortex.eye.itinerary;

import java.util.Collection;

import com.f1.base.Message;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.state.VortexEyeState;

public interface VortexEyeItinerary<M extends Message> {

	byte STATUS_COMPLETE = 1;
	byte STATUS_ACTIVE = 2;
	byte STATE_ERROR = 3;

	byte startJourney(VortexEyeItineraryWorker worker);
	byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker);

	void init(long itineraryId, RequestMessage<M> requestMessage, VortexEyeState state);

	RequestMessage<M> getInitialRequest();
	long getItineraryId();

	void removePendingRequest(RequestMessage<?> requestMessage);
	void addPendingRequest(RequestMessage<?> requestMessage);

	VortexEyeState getState();
	Collection<RequestMessage<?>> getPendingRequests();

	Message endJourney(VortexEyeItineraryWorker worker);
	void auditClientEvent(VortexEyeItineraryProcessor vortexEyeItineraryProcessor);

}
