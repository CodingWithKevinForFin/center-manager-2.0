/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msgdirect;

import java.util.Collection;

import com.f1.msg.MsgBytesEvent;
import com.f1.msg.MsgConnection;
import com.f1.msg.MsgEvent;
import com.f1.msg.MsgExternalConnection;
import com.f1.msg.MsgTopicConfiguration;
import com.f1.msg.impl.AbstractMsgOutputTopic;
import com.f1.msgdirect.MsgDirectServerSocket.ServerWriteConnection;

public class MsgDirectOutputServerTopic extends AbstractMsgOutputTopic {

	private ServerWriteConnection serverWriteConnection;

	public MsgDirectOutputServerTopic(MsgDirectServerSocket serverSocket, MsgConnection connection, MsgTopicConfiguration configuration, String topicSuffix) {
		super(connection, configuration, topicSuffix);
		this.serverWriteConnection = serverSocket.getServerWriteConnection(getFullTopicName());
		this.serverWriteConnection.setTopic(this);
	}

	protected ServerWriteConnection getServerWriteConnection() {
		return this.serverWriteConnection;
	}

	@Override
	public void send(MsgEvent event) {
		serverWriteConnection.sendMsgEvent((MsgBytesEvent) event);
	}

	@Override
	public String toString() {
		return super.toString() + " (" + serverWriteConnection.getConnectionsCount() + " connections)";
	}
	@Override
	public Collection<MsgExternalConnection> getExternalConnections() {
		return serverWriteConnection.getExternalConnections();
	}

	@Override
	public long getSentMessagesCount() {
		return serverWriteConnection.getMessagesCount();
	}

	@Override
	public long getSendQueueSize() {
		return serverWriteConnection.getSendQueueSize();
	}
}
