/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msgdirect;

import java.util.Collection;

import com.f1.msg.MsgConnection;
import com.f1.msg.MsgExternalConnection;
import com.f1.msg.MsgTopicConfiguration;
import com.f1.msg.impl.AbstractMsgInputTopic;
import com.f1.msgdirect.MsgDirectServerSocket.ServerReadConnection;

public class MsgDirectInputServerTopic extends AbstractMsgInputTopic {

	private ServerReadConnection serverConnection;

	public MsgDirectInputServerTopic(MsgDirectServerSocket serverSocket, MsgConnection connection, MsgTopicConfiguration configuration, String topicSuffix) {
		super(connection, configuration, topicSuffix);
		serverConnection = serverSocket.subscribe(getFullTopicName(), this);
	}

	public ServerReadConnection getServerConnection() {
		return serverConnection;
	}

	@Override
	public Collection<MsgExternalConnection> getExternalConnections() {
		return serverConnection.getExternalConnections();
	}

	@Override
	public long getReceivedMessagesCount() {
		return serverConnection.getMessagesCount();
	}

}
