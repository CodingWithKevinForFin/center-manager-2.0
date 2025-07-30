package com.f1.ami.center;

import com.f1.base.Message;
import com.f1.base.Prioritized;
import com.f1.container.RequestMessage;

public interface AmiCenterStartItineraryMessage extends Message, Prioritized {

	public AmiCenterItinerary<?> getItinerary();
	public void setItinerary(AmiCenterItinerary<?> itinerary);

	public RequestMessage<?> getInitialRequest();
	public void setInitialRequest(RequestMessage<?> requestMessage);
	public int getPriority();
	public void setPriority(int priority);
}
