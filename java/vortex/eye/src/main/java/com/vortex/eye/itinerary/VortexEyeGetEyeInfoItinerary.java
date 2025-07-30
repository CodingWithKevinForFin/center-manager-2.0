package com.vortex.eye.itinerary;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeGetEyeInfoRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeGetEyeInfoResponse;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.state.VortexEyeState;
import com.vortex.eye.state.VortexEyeState.AgentInterface;

public class VortexEyeGetEyeInfoItinerary extends AbstractVortexEyeItinerary<VortexEyeGetEyeInfoRequest> {

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		return STATUS_COMPLETE;
	}

	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		return STATUS_COMPLETE;
	}

	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		VortexEyeState state = getState();
		VortexEyeGetEyeInfoResponse r = state.nw(VortexEyeGetEyeInfoResponse.class);
		r.setAvailableAgentVersions(state.getAgentVersions());
		r.setAgentDefaultTargetDirectory(state.getDefaultAgentTargetDirectory());
		Map<String, String> aInterfaces = new HashMap<String, String>();
		for (Entry<String, AgentInterface> ai : state.getAgentInterfaces().entrySet()) {
			aInterfaces.put(ai.getKey(), describe(ai.getValue()));
		}
		r.setAvailableAgentInterfaces(aInterfaces);
		return r;
	}
	private String describe(AgentInterface value) {
		return value.description;//value.hostname + ":" + value.port + (value.isSecure ? " (secure)" : " (unsecure)");
	}

	@Override
	protected void populateAuditEvent(VortexEyeGetEyeInfoRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_GET_EYE_INFO);
	}

}
