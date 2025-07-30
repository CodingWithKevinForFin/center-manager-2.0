package com.vortex.eye.processors.agent;

import com.f1.base.Message;
import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRequest;
import com.vortex.eye.itinerary.VortexEyeItinerary;
import com.vortex.eye.processors.VortexEyeRequestProcessor;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeRequestToItineraryProcessor<T extends VortexEyeRequest, T2 extends VortexEyeItinerary<T>> extends VortexEyeRequestProcessor<T, Message> {

	final private Class<T2> itineraryType;
	public VortexEyeRequestToItineraryProcessor(Class<T> type, Class<T2> itineraryType) {
		super(type, Message.class);
		this.itineraryType = itineraryType;
	}
	@Override
	protected Message processRequest(RequestMessage<T> action, VortexEyeState state, ThreadScope threadScope) throws Exception {
		startItinerary(nw(itineraryType), action);
		return null;
	}

}
