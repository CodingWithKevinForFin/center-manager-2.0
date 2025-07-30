package com.f1.ami.amicommon.centerclient;

import java.util.List;

import com.f1.ami.amicommon.msg.AmiCenterChanges;
import com.f1.ami.amicommon.msg.AmiCenterGetSnapshotRequest;
import com.f1.ami.amicommon.msg.AmiCenterGetSnapshotResponse;
import com.f1.ami.amicommon.msg.AmiCenterRequest;
import com.f1.base.Action;
import com.f1.base.Message;
import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.povo.msg.MsgStatusMessage;
import com.f1.utils.LH;

public class AmiCenterClientInboundProcessor extends BasicProcessor<Message, AmiCenterClientState> {

	public OutputPort<Message> toUsers = (OutputPort) newOutputPort(Message.class);

	public OutputPort<RequestMessage<? extends AmiCenterRequest>> toCenter = (OutputPort) newOutputPort(RequestMessage.class);
	public OutputPort<Message> toAmi = (OutputPort) newOutputPort();

	private AmiCenterClientCenterConnectionStatuses wcs;

	private MsgStatusMessage tmpConnectionMessage;

	private byte centerId;

	public AmiCenterClientInboundProcessor(byte centerId) {
		super(Message.class, AmiCenterClientState.class);
		this.centerId = centerId;
		wcs = new AmiCenterClientCenterConnectionStatuses();
	}

	@Override
	public void processAction(Message action, AmiCenterClientState state, ThreadScope threadScope) throws Exception {
		if (action instanceof MsgStatusMessage) {
			boolean wasFullyConnected = wcs.isFullyConnected();
			MsgStatusMessage msg = (MsgStatusMessage) action;
			wcs.onMessage(msg);
			if (!wasFullyConnected && wcs.isFullyConnected()) {
				state.onAmiCenterConnect();

				AmiCenterGetSnapshotRequest acr = nw(AmiCenterGetSnapshotRequest.class);
				acr = state.createSnapshotRequest();
				RequestMessage<AmiCenterRequest> req = nw(RequestMessage.class);
				req.setAction(acr);
				req.setResultPort(this.toAmi);
				toCenter.send(req, threadScope);
				this.tmpConnectionMessage = msg;
			} else if (wasFullyConnected && !wcs.isFullyConnected()) {
				state.onAmiCenterDisconnect();
				AmiCenterClientMsgStatusMessage action2 = nw(AmiCenterClientMsgStatusMessage.class);
				action2.setCenterId(this.centerId);
				action2.setMsgStatusMessage(msg);
				toUsers.send(action2, threadScope);//let the clients know we're disconnected
			}
			return;
		} else if (action instanceof ResultMessage) {
			Action result = ((ResultMessage) action).getAction();
			if (result instanceof AmiCenterGetSnapshotResponse) {
				ResultMessage<AmiCenterGetSnapshotResponse> result2 = (ResultMessage<AmiCenterGetSnapshotResponse>) action;
				AmiCenterChanges snapshot = result2.getAction().getSnapshot();
				AmiCenterGetSnapshotRequest origRequest = (AmiCenterGetSnapshotRequest) result2.getRequestMessage().getAction();
				List<AmiCenterClientObjectMessage> sink = AmiCenterClientStateHelper.processAmiCenterChanges(state, snapshot, state.getBuffer());
				if (state.onSnapshotResponse(sink, snapshot.getSeqNum(), origRequest, toUsers, result2.getIsIntermediateResult())) {
					AmiCenterClientMsgStatusMessage action2 = nw(AmiCenterClientMsgStatusMessage.class);
					action2.setCenterId(this.centerId);
					action2.setMsgStatusMessage(this.tmpConnectionMessage);
					toUsers.send(action2, threadScope);//now we can let the clients know we're connected
					this.tmpConnectionMessage = null;
				}
				return;
			}
		} else if (action instanceof AmiCenterChanges) {
			AmiCenterChanges action2 = (AmiCenterChanges) action;
			List<AmiCenterClientObjectMessage> sink = AmiCenterClientStateHelper.processAmiCenterChanges(state, action2, state.getBuffer());
			AmiCenterClientObjectMessages messages = nw(AmiCenterClientObjectMessages.class);
			messages.setMessages(sink);
			messages.setSeqNum(action2.getSeqNum());
			messages.setCenterId(state.getCenterId());
			sink = state.onChanges(messages);
			messages.setMessages(sink);
			toUsers.send(messages, threadScope);
		} else
			LH.info(log, "Unknown message type: " + action);
	}

}
