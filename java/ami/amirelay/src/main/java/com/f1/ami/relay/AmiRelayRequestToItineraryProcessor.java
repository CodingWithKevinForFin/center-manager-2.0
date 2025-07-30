package com.f1.ami.relay;

import com.f1.base.Message;
import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;

public class AmiRelayRequestToItineraryProcessor<T extends Message, T2 extends AmiRelayItinerary<T>> extends AmiRelayRequestProcessor<T, Message> {

	final private Class<T2> itineraryType;
	public AmiRelayRequestToItineraryProcessor(Class<T> type, Class<T2> itineraryType) {
		super(type, Message.class);
		this.itineraryType = itineraryType;
	}
	@Override
	protected Message processRequest(RequestMessage<T> action, AmiRelayState state, ThreadScope threadScope) throws Exception {
		startItinerary(nw(itineraryType), action);
		return null;
	}

}
