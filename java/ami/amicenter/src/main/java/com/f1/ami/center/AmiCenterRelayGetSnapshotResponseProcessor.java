package com.f1.ami.center;

import com.f1.ami.amicommon.msg.AmiRelayAckMessage;
import com.f1.ami.amicommon.msg.AmiRelayChangesMessage;
import com.f1.ami.amicommon.msg.AmiRelayGetSnapshotResponse;
import com.f1.container.OutputPort;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.utils.LH;

public class AmiCenterRelayGetSnapshotResponseProcessor extends AmiCenterResultProcessor<AmiRelayGetSnapshotResponse> {

	public final OutputPort<AmiRelayAckMessage> ackPort = newOutputPort(AmiRelayAckMessage.class);

	public AmiCenterRelayGetSnapshotResponseProcessor() {
		super(AmiRelayGetSnapshotResponse.class);
	}

	@Override
	public void processAction(ResultMessage<AmiRelayGetSnapshotResponse> result, AmiCenterState state, ThreadScope threadScope) throws Exception {
		final long now = getTools().getNow();
		if (!AmiCenterUtils.verifyOk(log, result))
			return;

		final AmiRelayGetSnapshotResponse action = result.getAction();
		final AmiRelayChangesMessage changes = action.getSnapshot();
		final String relayProcessUid = changes.getAgentProcessUid();
		final AmiCenterChangesMessageBuilder msgBuilder = state.getChangesMessageBuilder();
		AmiCenterRelayState agentState = state.getAgentByPuid(action.getProcessUid());
		LH.info(log, "Received snapshot with ", changes.getAmiEvents().size(), " message(s) from relay ", agentState.getRelayId(), " (processuid=", relayProcessUid,
				"). Resetting seqnum from ", agentState.getCurrentSeqNum(), " to ", changes.getSeqNum());
		agentState.setCurrentSeqNum(changes.getSeqNum());
		AmiCenterAmiUtils.processAgentAmiEvents(state, agentState, changes.getAmiEvents(), changes.getAmiStringPoolMap(), this.getTools(), now, msgBuilder,
				state.getReusableTopStackFrame());
		state.onProcessedEventsComplete();
		sendAck(threadScope, agentState);
	}
	private void sendAck(ThreadScope threadScope, AmiCenterRelayState agentState) {
		if (agentState.getGuaranteedMessaging()) {
			AmiRelayAckMessage ack = nw(AmiRelayAckMessage.class);
			ack.setAgentProcessUid(agentState.getProcessUid());
			ack.setSeqNum(agentState.getCurrentSeqNum());
			ack.setCenterId(agentState.getCenterId());
			ackPort.send(ack, threadScope);
			agentState.saveSeqnumToDisk();
		}
	}
}
