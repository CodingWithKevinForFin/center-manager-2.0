package com.f1.utils;

import com.f1.base.Ackable;
import com.f1.base.Acker;

public class AbstractAckable implements Ackable {
	private int ackId = NO_ACK_ID;
	private Acker acker;

	@Override
	public boolean askAckIsPosDup() {
		return ackId < 0;
	}

	@Override
	public int askAckId() {
		return ackId < 0 ? -ackId : ackId;
	}

	@Override
	public void putAckId(int ackId, boolean ackIsDup) {
		if (ackId < 0)
			throw new RuntimeException("must be positive");
		this.ackId = ackIsDup ? -ackId : ackId;
	}

	@Override
	public void ack(Object v) {
		if (acker != null)
			acker.ack(this, v);
	}

	@Override
	public void registerAcker(Acker acker) {
		if (acker != null && this.acker != null)
			throw new RuntimeException("Acker already registered");
		this.acker = acker;
	}

	@Override
	public void transferAckerTo(Ackable ackable) {
		ackable.registerAcker(acker);
		ackable.putAckId(askAckId(), askAckIsPosDup());
		this.acker = null;
		this.ackId = NO_ACK_ID;
	}

	public Acker popAcker() {
		Acker r = acker;
		acker = null;
		this.ackId = NO_ACK_ID;
		return r;
	}
}
