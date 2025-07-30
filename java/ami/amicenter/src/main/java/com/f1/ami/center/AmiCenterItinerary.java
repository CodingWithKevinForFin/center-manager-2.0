package com.f1.ami.center;

import java.util.Collection;

import com.f1.base.Message;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;

public interface AmiCenterItinerary<M extends Message> {

	byte STATUS_COMPLETE = 1;
	byte STATUS_ACTIVE = 2;

	byte startJourney(AmiCenterItineraryWorker worker);
	byte onResponse(ResultMessage<?> result, AmiCenterItineraryWorker worker);

	void init(RequestMessage<M> requestMessage, AmiCenterState state, boolean isReadonly);

	RequestMessage<M> getInitialRequest();

	void removePendingRequest(RequestMessage<?> requestMessage);
	void addPendingRequest(RequestMessage<?> requestMessage);

	AmiCenterState getState();
	Collection<RequestMessage<?>> getPendingRequests();

	Message endJourney(AmiCenterItineraryWorker worker);
	int getTimeoutMs();

}
