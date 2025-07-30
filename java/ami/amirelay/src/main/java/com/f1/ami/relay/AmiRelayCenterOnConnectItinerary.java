package com.f1.ami.relay;

import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiRelayOnConnectRequest;
import com.f1.ami.amicommon.msg.AmiRelayOnConnectResponse;
import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.povo.msg.MsgMessage;
import com.f1.utils.EH;
import com.f1.utils.LH;

public class AmiRelayCenterOnConnectItinerary extends AmiRelayAbstractItinerary<AmiRelayOnConnectRequest> {

	private static final Logger log = LH.get();

	@Override
	public byte startJourney(AmiRelayItineraryWorker worker) {
		return STATUS_COMPLETE;
	}
	@Override
	public byte onResponse(ResultMessage<?> result, AmiRelayItineraryWorker worker) {
		return getPendingRequests().isEmpty() ? STATUS_COMPLETE : STATUS_ACTIVE;
	}
	@Override
	public Message endJourney(AmiRelayItineraryWorker worker) {
		MsgMessage msg = (MsgMessage) getInitialRequest().getCorrelationId();
		byte centerHostName = getInitialRequest().getAction().getCenterId();
		AmiRelayOnConnectResponse res = getState().nw(AmiRelayOnConnectResponse.class);
		res.setMachineUid(getMachineUid());
		res.setStartTime(EH.getStartTime());
		res.setAmiServerPort(getState().getAmiServer().getServerPort());
		res.setProcessUid(EH.getProcessUid());
		res.setRelayId(getState().getAmiServer().getId());
		res.setHostname(EH.getLocalHost());
		res.setCenterId(centerHostName);
		res.setOk(true);
		res.setGuaranteedMessagingEnabled(getState().getJournal().guaranteedMessagingEnabled());
		return res;
	}

	static public String getMachineUid() {
		return "UID_" + EH.getLocalHost();

	}
}
