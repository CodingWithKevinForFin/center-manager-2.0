package com.vortex.agent.itinerary;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunOsCommandRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunOsCommandResponse;
import com.vortex.agent.messages.VortexAgentOsAdapterRequest;
import com.vortex.agent.messages.VortexAgentOsAdapterResponse;

public class VortexAgentEyeRunOsCommandItinerary extends AbstractVortexAgentItinerary<VortexAgentRunOsCommandRequest> {

	private VortexAgentRunOsCommandResponse response;

	@Override
	public byte startJourney(VortexAgentItineraryWorker worker) {
		VortexAgentOsAdapterRequest req = getState().nw(VortexAgentOsAdapterRequest.class);
		req.setCommandType(VortexAgentOsAdapterRequest.RUN_COMMAND);
		VortexAgentRunOsCommandRequest action = getInitialRequest().getAction();
		req.setPartitionId("RUN_COMMAND_" + action.getOwner());
		req.setRequestMessage(action);
		worker.sendRequestToOsAdapter(this, req);
		return STATUS_ACTIVE;
	}

	@Override
	public byte onResponse(ResultMessage<?> result, VortexAgentItineraryWorker worker) {
		VortexAgentOsAdapterResponse res = (VortexAgentOsAdapterResponse) result.getAction();
		this.response = (VortexAgentRunOsCommandResponse) res.getResponseMessage();
		return STATUS_COMPLETE;
	}

	@Override
	public Message endJourney(VortexAgentItineraryWorker worker) {
		return response;
	}

}
