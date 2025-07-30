package com.f1.msg;

/**
 * Enables callbacks for administrative/instrumentation purposes such as eavesdropping, connection management, etc.
 * 
 * @author rcooke
 * 
 */
public interface MsgTopicListener {

	public void onIncomingMessage(MsgTopic topic, MsgEvent data);
	public void onOutgoingMessage(MsgTopic topic, MsgEvent data, int connectionsCount);

	public void onConnect(MsgTopic topic, MsgExternalConnection externalInterface);
	public void onDisconnect(MsgTopic topic, MsgExternalConnection externalInterface);
}
