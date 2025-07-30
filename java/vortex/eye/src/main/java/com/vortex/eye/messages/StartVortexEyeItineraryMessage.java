package com.vortex.eye.messages;

import com.f1.base.Message;
import com.f1.container.RequestMessage;
import com.vortex.eye.itinerary.VortexEyeItinerary;

public interface StartVortexEyeItineraryMessage extends Message {

	public VortexEyeItinerary<?> getItinerary();
	public void setItinerary(VortexEyeItinerary<?> itinerary);

	public RequestMessage<?> getInitialRequest();
	public void setInitialRequest(RequestMessage<?> requestMessage);
}
