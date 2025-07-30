package com.vortex.agent.itinerary;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunSignalProcessRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunSignalProcessResponse;
import com.vortex.agent.messages.VortexAgentOsAdapterRequest;
import com.vortex.agent.messages.VortexAgentOsAdapterResponse;

public class VortexAgentEyeSendSignalToProcessItinerary extends AbstractVortexAgentItinerary<VortexAgentRunSignalProcessRequest> {

	private VortexAgentRunSignalProcessResponse response;

	@Override
	public byte startJourney(VortexAgentItineraryWorker worker) {
		VortexAgentOsAdapterRequest req = getState().nw(VortexAgentOsAdapterRequest.class);
		req.setCommandType(VortexAgentOsAdapterRequest.SEND_SIGNAL);
		req.setPartitionId("SEND_SIGNAL");
		VortexAgentRunSignalProcessRequest action = getInitialRequest().getAction();
		req.setRequestMessage(action);
		worker.sendRequestToOsAdapter(this, req);
		return STATUS_ACTIVE;
	}

	@Override
	public byte onResponse(ResultMessage<?> result, VortexAgentItineraryWorker worker) {
		VortexAgentOsAdapterResponse res = (VortexAgentOsAdapterResponse) result.getAction();
		this.response = (VortexAgentRunSignalProcessResponse) res.getResponseMessage();
		return STATUS_COMPLETE;
	}

	@Override
	public Message endJourney(VortexAgentItineraryWorker worker) {
		return response;
	}

}
