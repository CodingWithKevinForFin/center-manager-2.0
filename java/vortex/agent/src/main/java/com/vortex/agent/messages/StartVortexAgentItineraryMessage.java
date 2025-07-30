package com.vortex.agent.messages;

import com.f1.base.Message;
import com.f1.container.RequestMessage;
import com.vortex.agent.itinerary.VortexAgentItinerary;

public interface StartVortexAgentItineraryMessage extends Message {

	public VortexAgentItinerary getItinerary();
	public void setItinerary(VortexAgentItinerary itinerary);

	public RequestMessage<?> getInitialRequest();
	public void setInitialRequest(RequestMessage<?> requestMessage);
}
