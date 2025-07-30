package com.vortex.agent.itinerary;

import java.util.logging.Logger;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.utils.LH;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileSearchRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileSearchResponse;
import com.vortex.agent.messages.VortexAgentOsAdapterRequest;
import com.vortex.agent.messages.VortexAgentOsAdapterResponse;

public class VortexAgentFileSearchingItinerary extends AbstractVortexAgentItinerary<VortexAgentFileSearchRequest> {
	private static final Logger log = LH.get(VortexAgentFileSearchingItinerary.class);
	private VortexAgentFileSearchResponse response;

	@Override
	public byte startJourney(VortexAgentItineraryWorker worker) {
		VortexAgentOsAdapterRequest req = getState().nw(VortexAgentOsAdapterRequest.class);
		req.setCommandType(VortexAgentOsAdapterRequest.FILE_SEARCH);
		req.setPartitionId("FILE_SEARCH");
		VortexAgentFileSearchRequest action = getInitialRequest().getAction();
		req.setRequestMessage(action);
		worker.sendRequestToOsAdapter(this, req);
		return STATUS_ACTIVE;
	}

	@Override
	public byte onResponse(ResultMessage<?> result, VortexAgentItineraryWorker worker) {
		VortexAgentOsAdapterResponse res = (VortexAgentOsAdapterResponse) result.getAction();
		this.response = (VortexAgentFileSearchResponse) res.getResponseMessage();
		return STATUS_COMPLETE;
	}

	@Override
	public Message endJourney(VortexAgentItineraryWorker worker) {
		return response;
	}

}
