/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msg.impl;

import com.f1.msg.MsgBytesEvent;
import com.f1.msg.MsgConnection;
import com.f1.msg.MsgEvent;
import com.f1.msg.MsgOutputTopic;
import com.f1.msg.MsgTopicConfiguration;

public abstract class AbstractMsgOutputTopic extends AbstractMsgTopic implements MsgOutputTopic {

	public AbstractMsgOutputTopic(MsgConnection connection, MsgTopicConfiguration configuration, String topicSuffix) {
		super(connection, configuration, topicSuffix);
	}

	@Override
	public MsgEvent createMessage() {
		return new MsgBytesEvent(null);
	}

	@Override
	abstract public void send(MsgEvent event);

}
