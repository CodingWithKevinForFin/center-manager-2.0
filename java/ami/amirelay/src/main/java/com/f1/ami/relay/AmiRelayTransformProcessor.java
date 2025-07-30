package com.f1.ami.relay;

import com.f1.ami.amicommon.msg.AmiRelayMessage;
import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;

public class AmiRelayTransformProcessor extends BasicProcessor<AmiRelayMessage, AmiRelayTransformState> {
	public OutputPort<AmiRelayMessage> out = newOutputPort(AmiRelayMessage.class);

	public AmiRelayTransformProcessor() {
		super(AmiRelayMessage.class, AmiRelayTransformState.class);
	}

	@Override
	public void processAction(AmiRelayMessage action, AmiRelayTransformState state, ThreadScope threadScope) throws Exception {
		state.map(action, out);
	}

}
