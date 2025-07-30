/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msg;

/**
 * This is the binding for actually receiving messages.
 * 
 * 
 * @see MsgInputTopic#subscribe(MsgEventListener)
 * @author rcooke
 * 
 */
public interface MsgEventListener {

	public void onEvent(MsgEvent event, MsgInputTopic topic);
}
