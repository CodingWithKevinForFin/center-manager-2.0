/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msgdirect;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

import com.f1.msg.MsgBytesEvent;
import com.f1.msg.MsgEvent;
import com.f1.msg.MsgExternalConnection;
import com.f1.msg.impl.AbstractMsgOutputTopic;

public class MsgDirectOutputClientTopic extends AbstractMsgOutputTopic {
	public final Logger log;

	private MsgDirectClientSocket clientSocket;

	public MsgDirectOutputClientTopic(MsgDirectConnection connection, MsgDirectTopicConfiguration configuration, String topicSuffix) {
		super(connection, configuration, topicSuffix);
		log = Logger.getLogger(connection.getLogName(MsgDirectOutputClientTopic.class.getName()));
		this.clientSocket = new MsgDirectClientSocket(connection, configuration.getHosts(), configuration.getPorts(), configuration.getSslPorts(), this, null);
		connection.addClientSocket(clientSocket);
	}
	@Override
	public void send(MsgEvent event) {
		clientSocket.sendMessageEvent((MsgBytesEvent) event);
	}

	@Override
	public Collection<MsgExternalConnection> getExternalConnections() {
		return Collections.singleton((MsgExternalConnection) clientSocket);
	}

	@Override
	public long getSentMessagesCount() {
		return clientSocket.getMessagesCount();
	}

	public boolean isAlive() {
		return clientSocket.isAlive();
	}
	@Override
	public long getSendQueueSize() {
		return clientSocket.getSendQueueSize();
	}
}
