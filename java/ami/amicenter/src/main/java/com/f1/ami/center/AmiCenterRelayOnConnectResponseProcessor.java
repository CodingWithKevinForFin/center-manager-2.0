package com.f1.ami.center;

import com.f1.ami.amicommon.msg.AmiRelayGetSnapshotRequest;
import com.f1.ami.amicommon.msg.AmiRelayGetSnapshotResponse;
import com.f1.ami.amicommon.msg.AmiRelayMachine;
import com.f1.ami.amicommon.msg.AmiRelayOnConnectResponse;
import com.f1.ami.center.table.AmiRow;
import com.f1.container.OutputPort;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.povo.msg.MsgStatusMessage;
import com.f1.utils.LH;

public class AmiCenterRelayOnConnectResponseProcessor extends AmiCenterResultProcessor<AmiRelayOnConnectResponse> {

	public OutputPort<ResultMessage<AmiRelayGetSnapshotResponse>> responsePort = newResultOutputPort(AmiRelayGetSnapshotResponse.class);

	public AmiCenterRelayOnConnectResponseProcessor() {
		super(AmiRelayOnConnectResponse.class);
	}

	@Override
	public void processAction(ResultMessage<AmiRelayOnConnectResponse> result, AmiCenterState state, ThreadScope threadScope) throws Exception {
		final long now = getTools().getNow();
		if (!AmiCenterUtils.verifyOk(log, result))
			return;

		final AmiRelayOnConnectResponse action = result.getAction();
		final AmiRelayMachine machine = nw(AmiRelayMachine.class);
		final MsgStatusMessage msgAction = (MsgStatusMessage) result.getRequestMessage().getCorrelationId();
		final String machineUid = action.getMachineUid();
		final long startTime = action.getStartTime();
		final int amiServerPort = action.getAmiServerPort();
		final String relayId = action.getRelayId();
		final String hostname = action.getHostname();
		final String processUid = msgAction.getRemoteProcessUid();
		final boolean gme = action.getGuaranteedMessagingEnabled();
		LH.info(log, "Received response from relay '" + relayId + "' at '", hostname, "'. Guaranteed Messaging ", (gme ? "Enabled" : "Disabled"), ", ProcessUid=", processUid);

		final AmiRow amiRow = state.getAmiImdb().getSystemSchema().__RELAY.addRow(null, machineUid, processUid, startTime, amiServerPort, relayId, hostname, now,
				state.getReusableTopStackFrame());

		machine.setId(amiRow.getAmiId());
		machine.setMachineUid(machineUid);
		machine.setModifiedOn(now);
		machine.setStartTime(startTime);
		machine.setAmiServerPort(amiServerPort);
		machine.setRelayProcessUid(action.getProcessUid());
		machine.setHostName(hostname);
		AmiCenterRelayState agentState = state.createAgentState(processUid, relayId, msgAction.getRemoteHost(), msgAction.getRemotePort(), now, machine, amiRow,
				action.getCenterId(), gme);
		machine.lock();
		AmiRelayGetSnapshotRequest snapshotRequest = nw(AmiRelayGetSnapshotRequest.class);
		snapshotRequest.setLastSeqnumReceivedByCenter(agentState.getCurrentSeqNum());
		snapshotRequest.setCenterId(agentState.getCenterId());
		sendRequestToAgent(snapshotRequest, processUid, this.responsePort, 0);
		state.onProcessedEventsComplete();

	}
}
