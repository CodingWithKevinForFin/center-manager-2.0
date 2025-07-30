package com.f1.ami.relay;

import com.f1.container.ThreadScope;
import com.f1.povo.msg.MsgStatusMessage;

public class AmiRelayCenterConnectionStatusProcessor extends AmiRelayBasicProcessor<MsgStatusMessage> {

	public AmiRelayCenterConnectionStatusProcessor() {
		super(MsgStatusMessage.class);
	}

	@Override
	public void processAction(MsgStatusMessage action, AmiRelayState state, ThreadScope threadScope) throws Exception {
	}
}
