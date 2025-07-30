package com.f1.ami.relay;

import java.util.Collection;

import com.f1.base.Message;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;

public interface AmiRelayItinerary<M extends Message> {

	byte STATUS_COMPLETE = 1;
	byte STATUS_ACTIVE = 2;
	byte STATUS_ERROR = 3;
	byte STATUS_PAUSED = 4;

	byte startJourney(AmiRelayItineraryWorker worker);
	byte onResponse(ResultMessage<?> result, AmiRelayItineraryWorker worker);

	void init(long itineraryId, RequestMessage<M> requestMessage, AmiRelayState state);

	RequestMessage<M> getInitialRequest();
	long getItineraryId();

	void removePendingRequest(RequestMessage<?> requestMessage);
	void addPendingRequest(RequestMessage<?> requestMessage);

	AmiRelayState getState();
	Collection<RequestMessage<?>> getPendingRequests();

	Message endJourney(AmiRelayItineraryWorker worker);

	byte continueItinerary();

}
