/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msg;

import com.f1.base.Acker;
import com.f1.msg.impl.AbstractMsgEvent;

public class MsgBytesEvent extends AbstractMsgEvent {

	private byte[] bytes;
	private Acker acker;
	private int ackId = NO_ACK_ID;
	private String typeForAudit;
	private String source;

	public MsgBytesEvent(byte[] bytes) {
		this.bytes = bytes;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes, String typeForAudit) {
		this.bytes = bytes;
		this.typeForAudit = typeForAudit;
	}

	@Override
	public String toString() {
		if (bytes == null)
			return "MsgBytesEvent";
		return "MsgBytesEvent: " + bytes.length + " byte(s)";
	}

	@Override
	public long getSize() {
		return this.bytes == null ? 0 : bytes.length;
	}

	@Override
	public String getBodyForAudit() {
		return null;
	}

	@Override
	public Object getParam(Object key) {
		return null;
	}

	@Override
	public byte getType() {
		return TYPE_F1_BINARY;
	}

	@Override
	public String getSource() {
		return source;
	}

}
