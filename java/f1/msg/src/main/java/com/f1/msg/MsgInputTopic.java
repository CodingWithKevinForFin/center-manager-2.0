/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msg;

/**
 * Used for receiving messages.
 * <P>
 * Users wanting to receive messages will generally<BR>
 * (1) Write a class that implements {@link MsgEventListener} (1) Get a topic by calling {@link MsgConnection#getInputTopic(String)} or
 * {@link MsgConnection#getInputTopic(String, String)} <BR>
 * (2) Subscribe by calling {@link MsgInputTopic#subscribe(MsgEventListener)} (3) handle messages in the {@link MsgEventListener#onEvent(MsgEvent, MsgInputTopic)} implementation
 * (4) Optionally unsubscribe to stop receiving messages by calling {@link MsgInputTopic#unsubscribe(MsgEventListener)}
 * 
 * @author rcooke
 * 
 */
public interface MsgInputTopic extends MsgTopic {

	public void subscribe(MsgEventListener listener);

	public void unsubscribe(MsgEventListener listener);

	public String getName();

	public Iterable<MsgEventListener> getListeners();

	public long getReceivedMessagesCount();

}
