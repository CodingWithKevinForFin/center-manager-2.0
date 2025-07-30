/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msg.impl;

import com.f1.base.Ackable;
import com.f1.base.Acker;
import com.f1.msg.MsgEvent;

public abstract class AbstractMsgEvent implements MsgEvent {

	private Acker acker;
	private int ackId = NO_ACK_ID;

	@Override
	public int askAckId() {
		return ackId < 0 ? -ackId : ackId;
	}

	@Override
	public void putAckId(int ackId, boolean isDup) {
		this.ackId = isDup ? ackId : -ackId;
	}

	@Override
	public void ack(Object optionalResult) {
		if (acker != null)
			acker.ack(this, optionalResult);
		acker = null;
	}

	@Override
	public void registerAcker(Acker acker) {
		this.acker = acker;
	}

	@Override
	public void transferAckerTo(Ackable ackable) {
		ackable.putAckId(ackId, askAckIsPosDup());
		ackable.registerAcker(acker);
		acker = null;
		ackId = NO_ACK_ID;
	}

	@Override
	public boolean askAckIsPosDup() {
		return ackId < 0;
	}

}
