package com.f1.ami.client;

import java.util.Map;

/**
 * 
 * Attached to an {@link AmiClient} using the {@link AmiClient#addListener(AmiClientListener)}. Then the listener receives callbacks on interesting events from the client.
 * <P>
 * Generally the order or events is:
 * 
 * 
 * <P>
 * On connected:<BR>
 * 1) {@link #onConnect(RawAmiClient)}<BR>
 * 2) {@link #onLoggedIn(RawAmiClient)}<BR>
 * While connected:<BR>
 * 3.a) {@link #onMessageSent(RawAmiClient, CharSequence)} 3.b) {@link #onMessageReceived(RawAmiClient, long, int, int, CharSequence)}<BR>
 * 3.c) {@link #onCommand(RawAmiClient, String, String, String, String, String, Map)}<BR>
 * On Disconnected:<BR>
 * 4) {@link #onDisconnect(RawAmiClient)}
 * 
 * 
 */
public interface AmiClientListener {

	/**
	 * If {@link AmiClient#ENABLE_QUIET} is not set in the options then each message sent to AMI Server will be followed by a status (S|) message back from AMI server. This message
	 * contains useful information about the success/failure status of processing the message
	 * 
	 * @param rawClient
	 *            represents the {@link AmiClient}'s internal connection to the AMI server
	 * @param now
	 *            time in millisconds that the message was received
	 * @param seqnum
	 *            sequence number (Q=) of the message
	 * @param status
	 *            status (S=) of the message
	 * @param message
	 *            messsage (M=) contents
	 */
	public void onMessageReceived(AmiClient rawClient, long now, long seqnum, int status, CharSequence message);

	/**
	 * fired after each message is sent using either {@link AmiClient#sendMessage()}, {@link AmiClient#sendMessageAndFlush()} or any other send* message
	 * 
	 * @param rawClient
	 *            represents the {@link AmiClient}'s internal connection to the AMI server
	 * @param message
	 *            the buffer of the message that was just sent
	 */
	public void onMessageSent(AmiClient rawClient, CharSequence message);

	/**
	 * called after a successful connection, but before a login attempt has been made
	 * 
	 * @param rawAmiClient
	 *            represents the {@link AmiClient}'s internal connection to the AMI server
	 */
	public void onConnect(AmiClient rawClient);

	/**
	 * called after the client/server connection has been disconnected
	 * 
	 * @param rawAmiClient
	 *            represents the {@link AmiClient}'s internal connection to the AMI server
	 */
	public void onDisconnect(AmiClient rawClient);

	/**
	 * Called when an execute (E|) command is received from the server.
	 * 
	 * @param rawClient
	 *            represents the {@link AmiClient}'s internal connection to the AMI server
	 * @param requestId
	 *            unique identifier for this execute command (I) , should be included in response
	 * @param cmd
	 *            (C=) command id to executed
	 * @param userName
	 *            name of caller
	 * @param objectType
	 *            type of object the command was called on
	 * @param objectId
	 *            the id of the object
	 * @param params
	 *            additional parameters, see AMI Backed API document for details
	 */
	public void onCommand(AmiClient rawClient, String requestId, String cmd, String userName, String objectType, String objectId, Map<String, Object> params);

	/**
	 * called after a successful logging (L|...) has processed by the AMI relay server
	 * 
	 * @param rawAmiClient
	 *            represents the {@link AmiClient}'s internal connection to the AMI server
	 */
	public void onLoggedIn(AmiClient rawClient);

}
