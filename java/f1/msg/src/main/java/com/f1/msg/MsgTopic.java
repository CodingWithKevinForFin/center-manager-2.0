/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msg;

import java.util.Collection;

/**
 * represents a communication line by which parties may publish and subscribe on. Topics are not generally uni-directional and are either input or output: {@link MsgInputTopic} or
 * {@link MsgOutputTopic}. Topic are identified by name ({@link MsgTopic#getName()}) and should be well-known, meaning all parties have prior knowledge of the topic name. Private
 * topics or context driven topics can be constructed by adding a topic suffix (MsgTopi{@link #getTopicSuffix()}).
 * 
 */
public interface MsgTopic {

	public MsgTopicConfiguration getConfiguration();

	public String getTopicSuffix();

	public MsgConnection getConnection();

	public String getFullTopicName();

	public String getName();

	public void init();

	public Collection<MsgExternalConnection> getExternalConnections();

	public void addListener(MsgTopicListener listener);

	public MsgTopicListener[] getMsgTopicListeners();
}
