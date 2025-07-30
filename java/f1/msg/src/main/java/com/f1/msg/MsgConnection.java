/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msg;

import java.util.Collection;
import java.util.Set;

/**
 * Represents a logical connection and is wrapped around other vendor implementations to normalize otherwise vendor specific implementations. Typically there would be one
 * implementation per vendor type. A Connection has a list of input topics and output topics. These topics may have specific a set of attributes, so much be 'registered with this
 * connection first using {@link #addTopic(MsgTopicConfiguration)}. A MsgConnectionListener can be added to be notified as external physical connections are made to a topic. Topics
 * (by name) may be input or output.
 * <P>
 * The driving goal of the msg suite is to reduce vendor specific code within an application using wrapper interfaces.
 * <P>
 * The procedure for the creation of new topics (spontaneous vs. preconfigured) is implementation specific
 * <P>
 * Unless otherwise stated all implementations <B>should be thread safe and are expected to be used in a multi-threading capacity<B>. Generally, its expected that the sending of
 * messages from the application to the Msg suite is initiated by the callers thread(s) (application level threads) and the receiving of messages from the msg suite to the
 * application is initiated by the msg suites thread(s).
 * <P>
 * <B>The scope-hierarchy is: <BR>
 * {@link MsgManager} -- Can administer many connections. Contains a collection of connections<BR>
 * {@link MsgConnection}s -- Vendor specific connection.<BR>
 * {@link MsgTopic}s -- the topics used to communicate on a connections <BR>
 * {@link MsgExternalConnection} -- the physical connections that connect to the application (because one topic can have any number of physical connections) <BR>
 * 
 * @author rcooke
 * 
 */
public interface MsgConnection {

	public MsgInputTopic getInputTopic(String topicName);

	public MsgOutputTopic getOutputTopic(String topicName);

	public MsgInputTopic getInputTopic(String topicName, String topicSuffix);

	public MsgOutputTopic getOutputTopic(String topicName, String topicSuffix);

	public void addMsgConnectionListener(MsgConnectionListener listener);

	public void removeMsgConnectionListener(MsgConnectionListener listener);

	public Collection<MsgConnectionListener> getListeners();

	public MsgConnectionConfiguration getConfiguration();

	public Set<String> getInputTopicNames();

	public Set<String> getOutputTopicNames();

	public Collection<MsgInputTopic> getInputTopics();
	public Collection<MsgOutputTopic> getOutputTopics();

	public Set<String> getTopicNames();

	public void addTopic(MsgTopicConfiguration config);

	public void shutdown();

	MsgTopicConfiguration getConfiguration(String topicName);

	boolean isRunning();

	void init();

	public Iterable<MsgConnectionExternalInterfaces> getExternalInterfaces();

	public String getLogName(String name);

}
