package com.f1.fixomsclient;

import com.f1.base.Message;
import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.povo.msg.MsgStatusMessage;

/**
 * receives connection status (example a disconnect from the OMS) and translates them to useful state information in the SOW
 * 
 */
public class OmsClientStatusProcessor extends BasicProcessor<MsgStatusMessage, OmsClientState> {

	final public OutputPort<Message> output = newOutputPort(Message.class);

	public OmsClientStatusProcessor() {
		super(MsgStatusMessage.class, OmsClientState.class);
	}

	@Override
	public void processAction(MsgStatusMessage action, OmsClientState state, ThreadScope threadScope) throws Exception {
		if (action.getIsConnected() == state.getHasConnected())
			return;
		if (!"fe.snapshot.request".equals(action.getTopic()))
			return;
		state.setHasConnected(action.getIsConnected());
		if (action.getIsConnected())
			output.send(nw(Message.class), threadScope);
	}

}
