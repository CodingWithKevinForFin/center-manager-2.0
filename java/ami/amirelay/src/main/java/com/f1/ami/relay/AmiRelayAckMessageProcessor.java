package com.f1.ami.relay;

import com.f1.ami.amicommon.msg.AmiRelayAckMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;

public class AmiRelayAckMessageProcessor extends BasicProcessor<AmiRelayAckMessage, AmiRelayState> {

	public AmiRelayAckMessageProcessor() {
		super(AmiRelayAckMessage.class, AmiRelayState.class);
	}

	@Override
	public void processAction(AmiRelayAckMessage action, AmiRelayState state, ThreadScope threadScope) throws Exception {
		state.getJournal().onCenterAcked(action.getCenterId(), action.getSeqNum());
	}
}
