package com.f1.ami.relay;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiRelayChangesMessage;
import com.f1.ami.amicommon.msg.AmiRelayGetSnapshotRequest;
import com.f1.ami.amicommon.msg.AmiRelayGetSnapshotResponse;
import com.f1.ami.amicommon.msg.AmiRelayMessage;
import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.utils.EH;
import com.f1.utils.LH;

public class AmiRelayCenterSnapshotItinerary extends AmiRelayAbstractItinerary<AmiRelayGetSnapshotRequest> {

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
		AmiRelayGetSnapshotRequest action = this.getInitialRequest().getAction();
		long seqnum = action.getLastSeqnumReceivedByCenter();
		byte centerId = action.getCenterId();
		AmiRelayGetSnapshotResponse res = getState().nw(AmiRelayGetSnapshotResponse.class);

		final AmiRelayChangesMessage changes = nw(AmiRelayChangesMessage.class);
		changes.setSeqNum(getState().currentSequenceNumber());
		changes.setAgentProcessUid(EH.getProcessUid());
		changes.setAmiStringPoolMap(getState().getAmiStringKeys());
		List<AmiRelayMessage> amiEvents = new ArrayList<AmiRelayMessage>();
		for (AmiRelayConnectionState i : getState().getActiveAmiLogins())
			i.drainEvent(amiEvents);

		changes.setAmiEvents(amiEvents);
		res.setSnapshot(changes);
		res.setProcessUid(EH.getProcessUid());

		res.setOk(true);
		getState().getJournal().getEventsAfterSeqnum(centerId, seqnum, amiEvents);

		getState().getAmiServer().onCenterConnected(centerId);
		return res;
	}

}
