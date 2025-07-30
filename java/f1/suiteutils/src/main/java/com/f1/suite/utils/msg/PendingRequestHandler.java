package com.f1.suite.utils.msg;

import com.f1.container.RequestMessage;
import com.f1.msg.MsgBytesEvent;
import com.f1.msg.MsgOutputTopic;

public interface PendingRequestHandler {

	void onExpired(MsgOutputTopic channel, MsgBytesEvent e, RequestMessage request, long originalSendTime, long now, PendingRequestMonitorProcessor pendingRequestMonitorProcessor);
	void onResend(MsgOutputTopic channel, MsgBytesEvent e, RequestMessage request, long originalSendTime, long now, PendingRequestMonitorProcessor pendingRequestMonitorProcessor);
	void onConnectionStatusChanged(boolean isConnected, ConverterState state);

}
