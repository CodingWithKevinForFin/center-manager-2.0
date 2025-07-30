package com.vortex.agent.itinerary;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileDeleteRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileDeleteResponse;
import com.vortex.agent.messages.VortexAgentOsAdapterRequest;
import com.vortex.agent.messages.VortexAgentOsAdapterResponse;

public class VortexAgentEyeFileDeleteItinerary extends AbstractVortexAgentItinerary<VortexAgentFileDeleteRequest> {

	private VortexAgentFileDeleteResponse response;

	@Override
	public byte startJourney(VortexAgentItineraryWorker worker) {
		VortexAgentOsAdapterRequest req = getState().nw(VortexAgentOsAdapterRequest.class);
		req.setCommandType(VortexAgentOsAdapterRequest.DELETE_FILE);
		VortexAgentFileDeleteRequest action = getInitialRequest().getAction();
		req.setPartitionId("DELETE_FILE");
		req.setRequestMessage(action);
		worker.sendRequestToOsAdapter(this, req);
		return STATUS_ACTIVE;
	}

	@Override
	public byte onResponse(ResultMessage<?> result, VortexAgentItineraryWorker worker) {
		VortexAgentOsAdapterResponse res = (VortexAgentOsAdapterResponse) result.getAction();
		this.response = (VortexAgentFileDeleteResponse) res.getResponseMessage();
		return STATUS_COMPLETE;
	}

	@Override
	public Message endJourney(VortexAgentItineraryWorker worker) {
		return response;
	}

}
