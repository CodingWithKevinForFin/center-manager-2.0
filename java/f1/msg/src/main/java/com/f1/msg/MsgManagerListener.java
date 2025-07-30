package com.f1.msg;

/**
 * Listener at the Msg Manager level.
 * 
 * @author rcooke
 * 
 */
public interface MsgManagerListener {

	public void onConnectionAdded(MsgManager manager, MsgConnection connection);
}
