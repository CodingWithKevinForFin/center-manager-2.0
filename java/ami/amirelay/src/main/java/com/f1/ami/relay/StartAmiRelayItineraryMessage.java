package com.f1.ami.relay;

import com.f1.base.Message;
import com.f1.container.RequestMessage;

public interface StartAmiRelayItineraryMessage extends Message {

	public boolean getIsContinue();
	public void setIsContinue(boolean isContinue);

	public AmiRelayItinerary getItinerary();
	public void setItinerary(AmiRelayItinerary itinerary);

	public RequestMessage<?> getInitialRequest();
	public void setInitialRequest(RequestMessage<?> requestMessage);
}
