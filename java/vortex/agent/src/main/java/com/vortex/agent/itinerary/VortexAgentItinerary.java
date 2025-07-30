package com.vortex.agent.itinerary;

import java.util.Collection;

import com.f1.base.Message;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.vortex.agent.state.VortexAgentState;

public interface VortexAgentItinerary<M extends Message> {

	byte STATUS_COMPLETE = 1;
	byte STATUS_ACTIVE = 2;
	byte STATUS_ERROR = 3;

	byte startJourney(VortexAgentItineraryWorker worker);
	byte onResponse(ResultMessage<?> result, VortexAgentItineraryWorker worker);

	void init(long itineraryId, RequestMessage<M> requestMessage, VortexAgentState state);

	RequestMessage<M> getInitialRequest();
	long getItineraryId();

	void removePendingRequest(RequestMessage<?> requestMessage);
	void addPendingRequest(RequestMessage<?> requestMessage);

	VortexAgentState getState();
	Collection<RequestMessage<?>> getPendingRequests();

	Message endJourney(VortexAgentItineraryWorker worker);

}
