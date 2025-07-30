package com.f1.ami.amicommon.centerclient;

import com.f1.ami.amicommon.msg.AmiCenterGetSnapshotRequest;
import com.f1.ami.amicommon.msg.AmiCenterStatusRequest;
import com.f1.ami.amicommon.msg.AmiCenterStatusResponse;
import com.f1.base.Message;
import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.utils.LH;

public class AmiCenterClientOutboundProcessor extends BasicProcessor<RequestMessage<? extends Message>, AmiCenterClientState> {

	public OutputPort<Message> toUsers = (OutputPort) newOutputPort(Message.class);
	public OutputPort<RequestMessage<? extends Message>> out = (OutputPort) newOutputPort(RequestMessage.class);
	public final OutputPort<Message> toAmiWeb = newOutputPort(Message.class);

	public AmiCenterClientOutboundProcessor(byte centerId) {
		super((Class) RequestMessage.class, AmiCenterClientState.class);
	}

	@Override
	public void processAction(RequestMessage<? extends Message> action, AmiCenterClientState state, ThreadScope threadScope) throws Exception {
		if (action.getAction() instanceof AmiCenterClientGetSnapshotRequest) {
			RequestMessage<AmiCenterGetSnapshotRequest> action2 = state.onUserSnapshotRequest((RequestMessage<AmiCenterClientGetSnapshotRequest>) action, toUsers);
			if (action2 != null) {
				action2.setResultPort(this.toAmiWeb);
				out.send(action2, threadScope);
			}
		} else if (action.getAction() instanceof AmiCenterStatusRequest) {
			AmiCenterStatusResponse status = nw(AmiCenterStatusResponse.class);
			if (state.isSnapshotProcessed() && !state.isConnected())
				LH.warning(log, "Hit scenario where snapshotProcessed but not connected so sending ok as false");
			status.setOk(state.isSnapshotProcessed() && state.isConnected());
			ResultMessage res = nw(ResultMessage.class);
			res.setAction(status);
			reply(action, res, threadScope);
		} else
			out.send(action, threadScope);
	}
}
