/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msg;

import com.f1.base.Ackable;

/**
 * The base message type.All Incoming and outgoing messages should implement this interface.
 * 
 * @author rcooke
 * 
 */
public interface MsgEvent extends Ackable {
	byte TYPE_F1_BINARY = 1;
	byte TYPE_JSON = 2;
	byte TYPE_FIX = 3;

	long getSize();

	//optional (for auditing)
	Object getParam(Object key);

	//should be either a string or byte array.  Nothing highlevel!
	Object getBodyForAudit();

	//see type above
	byte getType();

	String getSource();

}
