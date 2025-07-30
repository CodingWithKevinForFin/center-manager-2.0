package com.f1.ami.center;

import java.util.Map;

import com.f1.ami.amicommon.msg.AmiCenterChanges;
import com.f1.ami.amicommon.msg.AmiRelayMachine;
import com.f1.ami.amicommon.msg.AmiRelayOnConnectRequest;
import com.f1.base.Action;
import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.povo.msg.MsgMessage;
import com.f1.povo.msg.MsgStatusMessage;
import com.f1.utils.LH;
import com.f1.utils.SH;

public class AmiCenterRelayConnectionStatusProcessor extends AmiCenterBasicProcessor<MsgStatusMessage> {

	public AmiCenterRelayConnectionStatusProcessor() {
		super(MsgStatusMessage.class);
	}

	@Override
	public void processAction(MsgStatusMessage action, AmiCenterState state, ThreadScope threadScope) throws Exception {
		final String processUid = action.getRemoteProcessUid();
		if (SH.isnt(processUid)) {
			LH.warning(log, "Message missing processUid: ", action);
		}
		final long now = getTools().getNow();
		final Map<String, AmiCenterRelayConnectionStatuses> statuses = state.getRelayConnectionStatuses();

		AmiCenterRelayConnectionStatuses rcs = statuses.get(processUid);
		if (rcs == null)
			statuses.put(processUid, rcs = new AmiCenterRelayConnectionStatuses(processUid));
		boolean wasFullyConnected = rcs.isFullyConnected();
		rcs.onMessage(action);

		if (!wasFullyConnected && rcs.isFullyConnected()) {
			AmiCenterRelayState existing = state.getAgentByPuidNoThrow(processUid);
			if (existing != null) {
				LH.warning(log, "Received a duplicate connection from: ", processUid);
			} else {
				LH.info(log, "Received a new connection from: '", action.getRemoteHost(), "', puid: ", processUid, ". Requesting snapshot.");
				AmiRelayOnConnectRequest req = nw(AmiRelayOnConnectRequest.class);
				MsgMessage msg = sendRequestToAgent(req, processUid, getResponseRoutingPort(), 0);
				RequestMessage<Action> rq = (RequestMessage<Action>) msg.getMessage();
				rq.setCorrelationId(action);
			}

		} else if (wasFullyConnected && !rcs.isFullyConnected()) {
			if (state.getAgentPuids().contains(processUid)) {
				final AmiCenterRelayState toRemove = state.getAgentByPuid(processUid);
				final AmiCenterChangesMessageBuilder msgBuilder = state.getChangesMessageBuilder();
				AmiRelayMachine mac = toRemove.getMachineState().clone();
				mac.setRelayProcessUid(null);

				AmiCenterRelayState removedAgent = state.removeAgent(processUid);
				LH.info(log, "Received an orderly disconnect from agent: ", processUid, " at ", removedAgent.getRemoteHost());

				AmiCenterAmiUtils.processAgentAmiDisconnect(state, toRemove, this.getTools(), now, msgBuilder, state.getReusableTopStackFrame());
				AmiCenterChanges changes = msgBuilder.popToChangesMsg(state.nextSequenceNumber());
				sendToClients(changes);

			} else {
				LH.warning(log, "Received disconnect from unknown agent: ", processUid);
			}
		}

		if (rcs.isFullyDisconnected())
			statuses.remove(processUid);
	}
}
