/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msg;

/**
 * Provides feedback on the state of a connctions.
 * 
 * @author rcooke
 * 
 */
public interface MsgConnectionListener {

	public void onDisconnect(MsgConnection connection, MsgTopic msgTopic, String topic, String suffix, String remoteHost, boolean isWrite, MsgExternalConnection externalConnection);

	public void onConnect(MsgConnection connection, MsgTopic msgTopic, String topic, String suffix, String remoteHost, boolean isWrite, MsgExternalConnection externalConnection);

	public void onNewInputTopic(MsgConnection connection, MsgInputTopic r);
	public void onNewOutputTopic(MsgConnection connection, MsgOutputTopic r);
}
