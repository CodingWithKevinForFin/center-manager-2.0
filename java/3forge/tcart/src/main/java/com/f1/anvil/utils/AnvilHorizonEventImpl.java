package com.f1.anvil.utils;

import com.f1.ami.center.table.AmiRow;

public class AnvilHorizonEventImpl implements AnvilHorizonEvent {

	private AnvilHorizonEventImpl prior;
	private AnvilHorizonEventImpl next;
	final private AnvilHorizonImpl parent;
	private AmiRow row;
	private long time;

	public AnvilHorizonEventImpl(AnvilHorizonImpl parent) {
		this.parent = parent;
	}

	public void reset(AmiRow row, long time, AnvilHorizonEventImpl next) {
		if (this.row != null)
			throw new IllegalStateException();
		this.row = row;
		this.time = time;
		this.next = next;
		this.prior = next.prior;
		this.prior.next = this;
		this.next.prior = this;
	}

	@Override
	public AmiRow remove() {
		this.prior.next = this.next;
		this.next.prior = this.prior;
		AmiRow r = row;
		row = null;
		time = 0;
		parent.returnToPool(this);
		return r;
	}

	@Override
	public long getTime() {
		return time;
	}

	public AnvilHorizonEventImpl getNext() {
		return this.next;
	}

	public void setNext(AnvilHorizonEventImpl next) {
		this.next = next;
	}

	public AnvilHorizonEventImpl getPrior() {
		return this.prior;
	}

	public void setPrior(AnvilHorizonEventImpl prior) {
		this.prior = prior;
	}

	@Override
	public void updateEvent(long now, AmiRow row) {
		if (now < parent.newest)
			throw new RuntimeException();
		parent.newest = now;
		this.row = row;
	}

	@Override
	public AmiRow peek() {
		return this.row;
	}

}
