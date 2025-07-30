package com.f1.transportManagement;

import com.f1.container.Container;
import com.f1.pofo.fix.MsgType;

import quickfix.Message;

public interface MessageDispatcher {
	public void sendRequest(final Container container, final String clOrdID, final MsgType msgType, final String partitionId, final Message msg);
}
