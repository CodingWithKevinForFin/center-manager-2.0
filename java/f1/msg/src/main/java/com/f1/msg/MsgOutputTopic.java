/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msg;

/**
 * Used for sending messages and creating messages (vendor specific implementations should use a covariant return type on {@link MsgOutputTopic#createMessage()}.
 * <P>
 * Users wanting to send messages will generally<BR>
 * (1) Get a topic by calling {@link MsgConnection#getOutputTopic(String)} or {@link MsgConnection#getOutputTopic(String, String) <BR>
 * (2) Create a message by calling {@link MsgOutputTopic#createMessage()}<BR>
 * (3) populate the vendor-specific message with ones favorite payload<BR>
 * (4) Send the message by calling {@link MsgOutputTopic#send(MsgEvent)}
 * 
 * @author rcooke
 * 
 */
public interface MsgOutputTopic extends MsgTopic {

	public void send(MsgEvent event);

	public MsgEvent createMessage();

	public long getSentMessagesCount();

	public long getSendQueueSize();

}
