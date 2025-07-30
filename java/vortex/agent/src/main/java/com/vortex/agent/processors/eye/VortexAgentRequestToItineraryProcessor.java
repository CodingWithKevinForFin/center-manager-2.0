package com.vortex.agent.processors.eye;

import com.f1.base.Message;
import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.vortex.agent.itinerary.VortexAgentItinerary;
import com.vortex.agent.processors.VortexAgentRequestProcessor;
import com.vortex.agent.state.VortexAgentState;

public class VortexAgentRequestToItineraryProcessor<T extends Message, T2 extends VortexAgentItinerary<T>> extends VortexAgentRequestProcessor<T, Message> {

	final private Class<T2> itineraryType;
	public VortexAgentRequestToItineraryProcessor(Class<T> type, Class<T2> itineraryType) {
		super(type, Message.class);
		this.itineraryType = itineraryType;
	}
	@Override
	protected Message processRequest(RequestMessage<T> action, VortexAgentState state, ThreadScope threadScope) throws Exception {
		startItinerary(nw(itineraryType), action);
		return null;
	}

}
