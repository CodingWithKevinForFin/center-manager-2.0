package com.f1.ami.relay;

import com.f1.ami.amicommon.msg.AmiRelayRequest;
import com.f1.base.Action;
import com.f1.base.Message;
import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.container.impl.BasicState;

public class AmiRelayFromCenterPreProcessor extends BasicProcessor<Message, BasicState> {

	public OutputPort<Message> out = newOutputPort(Message.class);

	public AmiRelayFromCenterPreProcessor(byte centerId) {
		super(Message.class, BasicState.class);
		this.centerId = centerId;
	}

	final private byte centerId;

	@Override
	public void processAction(Message action, BasicState state, ThreadScope threadScope) throws Exception {
		if (action instanceof RequestMessage) {
			Action req = ((RequestMessage) action).getAction();
			if (req instanceof AmiRelayRequest)
				((AmiRelayRequest) req).setCenterId(this.centerId);
		}
		out.send(action, threadScope);

	}

}
