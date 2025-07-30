package com.f1.ami.relay;

import com.f1.ami.amicommon.msg.AmiRelayChangesMessage;
import com.f1.base.Message;
import com.f1.container.OutputPort;
import com.f1.container.Processor;
import com.f1.container.RequestMessage;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultOutputPort;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.povo.standard.TextMessage;

public interface AmiRelayProcessor<A extends Message> extends Processor<A, AmiRelayState> {

	//port for looping messages back into same processor
	OutputPort<A> getLoopbackPort();

	//port for broadcasting messages on the wire to clients
	OutputPort<AmiRelayChangesMessage> getToCenterPort();

	//port for sending responses
	ResultOutputPort<Message> getResponseRoutingPort();

	//port for starting itineraries
	OutputPort<StartAmiRelayItineraryMessage> getStartItineraryOutputPort();

	<M extends Message> void startItinerary(AmiRelayItinerary<M> itinerary, RequestMessage<M> action);

	RequestOutputPort<RunnableRequestMessage, TextMessage> getToRunnablePort();
}
