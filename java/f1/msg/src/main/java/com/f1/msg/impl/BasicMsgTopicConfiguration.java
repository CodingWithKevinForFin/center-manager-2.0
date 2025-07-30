/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msg.impl;

import com.f1.msg.MsgTopicConfiguration;

public class BasicMsgTopicConfiguration implements MsgTopicConfiguration {

	private String name, topic;

	public BasicMsgTopicConfiguration(String name, String topicName) {
		this.name = name;
		this.topic = topicName;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getTopicName() {
		return topic;
	}

	@Override
	public String toString() {
		return "name=" + name + ", topicName=" + topic;
	}
}
