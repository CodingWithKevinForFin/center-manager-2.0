/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msgdirect;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

import com.f1.msg.MsgBytesEvent;
import com.f1.msg.MsgExternalConnection;
import com.f1.msg.impl.AbstractMsgInputTopic;

public class MsgDirectInputClientTopic extends AbstractMsgInputTopic {
	public final Logger log;

	private MsgDirectClientSocket clientSocket;

	public MsgDirectInputClientTopic(MsgDirectConnection connection, MsgDirectTopicConfiguration configuration, String topicSuffix) {
		super(connection, configuration, topicSuffix);
		log = Logger.getLogger(connection.getLogName(MsgDirectInputClientTopic.class.getName()));
		this.clientSocket = new MsgDirectClientSocket(connection, configuration.getHosts(), configuration.getPorts(), configuration.getSslPorts(), this, this);
		connection.addClientSocket(clientSocket);
	}

	public void onMessage(MsgBytesEvent msgBytesEvent) {
		broadcastMsgEvent(msgBytesEvent);
	}

	@Override
	public Collection<MsgExternalConnection> getExternalConnections() {
		return Collections.singleton((MsgExternalConnection) clientSocket);
	}

	@Override
	public long getReceivedMessagesCount() {
		return clientSocket.getMessagesCount();
	}
	public boolean isAlive() {
		return clientSocket.isAlive();
	}

}
