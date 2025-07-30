/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.qfix.msg;

import quickfix.Message;

import com.f1.base.Acker;
import com.f1.msg.impl.AbstractMsgEvent;
import com.f1.utils.OH;

public class FixMsgEvent extends AbstractMsgEvent {

	private Message message;
	private Acker acker;
	private int ackId = NO_ACK_ID;
	private String sessionName;

	public FixMsgEvent(Message message, String sessionName) {
		this.message = message;
		this.sessionName = sessionName;
	}
	public FixMsgEvent() {
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	@Override
	public String toString() {
		if (message == null)
			return "FixMsgEvent";
		return "FixMsgEvent: " + message.toString();

	}
	@Override
	public long getSize() {
		return message == null ? 0 : message.bodyLength();
	}
	@Override
	public Object getParam(Object key) {
		try {
			if (message == null)
				return null;
			return message.getString(Integer.parseInt(key.toString()));
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}
	@Override
	public String getBodyForAudit() {
		return message == null ? null : message.toString();
	}
	@Override
	public byte getType() {
		return TYPE_FIX;
	}
	@Override
	public String getSource() {
		return sessionName;
	}

}
