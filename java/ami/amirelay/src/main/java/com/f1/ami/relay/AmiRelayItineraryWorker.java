package com.f1.ami.relay;

import com.f1.base.Message;
import com.f1.container.RequestMessage;
import com.f1.povo.standard.RunnableRequestMessage;

public interface AmiRelayItineraryWorker {

	<M extends Message> void startItinerary(AmiRelayItinerary<?> source, AmiRelayItinerary<M> itinerary, RequestMessage<M> action);

	void replyWithIntermediateResult(AmiRelayItinerary<?> source, Message m);

	void sendRunnable(AmiRelayItinerary<?> itinerary, RunnableRequestMessage rm);
}
