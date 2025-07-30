package com.f1.bootstrap.appmonitor;

import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.povo.msg.MsgStatusMessage;

public class AppMonitorClientConnectedProcessor extends BasicProcessor<MsgStatusMessage, AppMonitorState> {

	public AppMonitorClientConnectedProcessor() {
		super(MsgStatusMessage.class, AppMonitorState.class);
	}

	@Override
	public void processAction(MsgStatusMessage action, AppMonitorState state, ThreadScope threadScope) throws Exception {
		//NOTHING TODO
		if ("f1.app.to.agent".equals(action.getTopic()) && action.getSuffix() == null) {
			if (!action.getIsConnected()) {
				state.removeAppMonitorClientNoThrow(action.getSuffix());
			}
		}
	}
}
