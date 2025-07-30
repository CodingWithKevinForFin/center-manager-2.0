/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msg.impl;

import com.f1.msg.MsgConnection;
import com.f1.msg.MsgEvent;
import com.f1.msg.MsgExternalConnection;
import com.f1.msg.MsgTopic;
import com.f1.msg.MsgTopicConfiguration;
import com.f1.msg.MsgTopicListener;
import com.f1.utils.AH;

public abstract class AbstractMsgTopic implements MsgTopic {

	final private MsgConnection connection;
	final private MsgTopicConfiguration configuration;
	final private String topicSuffix;
	final private String fullTopicName;

	public AbstractMsgTopic(MsgConnection connection, MsgTopicConfiguration configuration, String topicSuffix) {
		this.connection = connection;
		this.configuration = configuration;
		this.topicSuffix = topicSuffix;
		this.fullTopicName = topicSuffix == null ? getConfiguration().getTopicName() : (getConfiguration().getTopicName() + "$" + getTopicSuffix());
	}

	public MsgConnection getConnection() {
		return connection;
	}

	public MsgTopicConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public String getTopicSuffix() {
		return topicSuffix;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + getConfiguration().toString();
	}

	@Override
	public String getFullTopicName() {
		return fullTopicName;
	}

	@Override
	public String getName() {
		return configuration.getName();
	}

	@Override
	public void init() {

	}

	private static final MsgTopicListener[] EMPTY = new MsgTopicListener[0];
	private MsgTopicListener[] listeners = EMPTY;

	@Override
	final public void addListener(MsgTopicListener listener) {
		listeners = AH.append(listeners, listener);
	}

	@Override
	final public MsgTopicListener[] getMsgTopicListeners() {
		return listeners;
	}

	public final void fireOutgoing(MsgEvent data, int destinationsCount) {
		for (int i = 0; i < listeners.length; i++)
			listeners[i].onOutgoingMessage(this, data, destinationsCount);
	}

	public final void fireIncoming(MsgEvent data) {
		for (int i = 0; i < listeners.length; i++)
			listeners[i].onIncomingMessage(this, data);
	}

	public final void fireConnected(MsgExternalConnection externalInterface) {
		for (int i = 0; i < listeners.length; i++)
			listeners[i].onConnect(this, externalInterface);
	}
	public final void fireDisconnected(MsgExternalConnection externalInterface) {
		for (int i = 0; i < listeners.length; i++)
			listeners[i].onDisconnect(this, externalInterface);
	}

}
