package com.vortex.agent.processors.eye;

import com.f1.container.ThreadScope;
import com.f1.povo.msg.MsgStatusMessage;
import com.f1.utils.LH;
import com.vortex.agent.processors.VortexAgentBasicProcessor;
import com.vortex.agent.state.VortexAgentState;

public class VortexAgentEyeConnectionStatusProcessor extends VortexAgentBasicProcessor<MsgStatusMessage> {

	public VortexAgentEyeConnectionStatusProcessor() {
		super(MsgStatusMessage.class);
	}

	@Override
	public void processAction(MsgStatusMessage action, VortexAgentState state, ThreadScope threadScope) throws Exception {
		if (action.getTopic().equals("f1.agent.to.server") && action.getSuffix() == null) {
			if (action.getIsConnected()) {
				if (state.getIsEyeConnected())
					LH.warning(log, "Manager connected, but already in connected state.");
				else
					LH.info(log, "Manager connected");
				state.setIsEyeConnected(true);
				state.setIsSnapshotSentToEye(false);
			} else {
				if (state.getIsEyeConnected()) {
					LH.info(log, "Manager disconnected");
				} else
					LH.warning(log, "Manager re-disconnected (was already in disconnected state)");
				state.setIsEyeConnected(false);
				state.setIsSnapshotSentToEye(false);
			}
		}
	}
}
