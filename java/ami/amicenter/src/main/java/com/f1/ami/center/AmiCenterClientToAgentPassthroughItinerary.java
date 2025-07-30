package com.f1.ami.center;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiCenterPassToRelayRequest;
import com.f1.ami.amicommon.msg.AmiCenterPassToRelayResponse;
import com.f1.ami.amicommon.msg.AmiRelayResponse;
import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandRequest;
import com.f1.ami.amicommon.msg.AmiRelaySendEmailRequest;
import com.f1.base.IterableAndSize;
import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class AmiCenterClientToAgentPassthroughItinerary extends AbstractAmiCenterItinerary<AmiCenterPassToRelayRequest> {

	private static final Logger log = LH.get();
	private AmiCenterPassToRelayResponse response;
	private String agentProcessUid;

	@Override
	public byte startJourney(AmiCenterItineraryWorker worker) {
		AmiCenterPassToRelayRequest request = getInitialRequest().getAction();
		final AmiCenterRelayState machine;
		response = getTools().nw(AmiCenterPassToRelayResponse.class);

		if (request.getAgentRequest() instanceof AmiRelaySendEmailRequest) {
			AmiCenterRelayState m = getState().getMachineByMiidNoThrow(request.getRelayMiid());
			if (m == null) {
				IterableAndSize<AmiCenterRelayState> relays = getState().getRelays();
				if (relays.size() > 0)
					m = relays.iterator().next();
			}
			if (m == null) {
				this.response.setMessage("Relay not found");
				this.response.setOk(false);
				return STATUS_COMPLETE;
			} else
				machine = m;
		} else if (request.getRelayMiid() == -1 && request.getAgentRequest() instanceof AmiRelayRunAmiCommandRequest) {
			AmiRelayRunAmiCommandRequest cmdReq = (AmiRelayRunAmiCommandRequest) request.getAgentRequest();
			String id = cmdReq.getCommandDefinitionId();
			LH.info(log, "Received command from ", cmdReq.getInvokedBy(), ": ", id);
			response.setOk(false);
			response.setMessage("Unknown command id: " + id);
			return STATUS_COMPLETE;
		} else
			machine = getState().getMachineByMiidNoThrow(request.getRelayMiid());
		if (machine == null) {
			response.setMessage("Relay not found: " + request.getRelayMiid());
			response.setOk(false);
			return STATUS_COMPLETE;
		}
		if (log.isLoggable(Level.INFO))
			LH.info(log, "Client to relay: ", OH.getSimpleClassName(request.getAgentRequest()), " ==> ", machine.getMachineState().getMachineUid());
		agentProcessUid = machine.getProcessUid();
		if (agentProcessUid == null) {
			response.setMessage("Relay not running: " + machine.getRemoteHost());
			return STATUS_COMPLETE;
		}
		request.getAgentRequest().setInvokedBy(request.getInvokedBy());
		worker.sendRequestToAgent(this, request.getAgentRequest(), agentProcessUid);
		return STATUS_ACTIVE;
	}
	@Override
	public byte onResponse(ResultMessage<?> result, AmiCenterItineraryWorker worker) {
		response = getState().nw(AmiCenterPassToRelayResponse.class);
		final AmiRelayResponse agentResponse = (AmiRelayResponse) result.getAction();
		response.setAgentResponse(agentResponse);
		response.setMessage(agentResponse.getMessage());
		response.setOk(agentResponse.getOk());
		return STATUS_COMPLETE;
	}

	@Override
	public Message endJourney(AmiCenterItineraryWorker worker) {
		return response;
	}

}
