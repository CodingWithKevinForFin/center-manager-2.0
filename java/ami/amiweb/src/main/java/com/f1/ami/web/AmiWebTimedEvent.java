package com.f1.ami.web;

import com.f1.base.DateMillis;
import com.f1.utils.OH;

public abstract class AmiWebTimedEvent implements Comparable<AmiWebTimedEvent> {

	private long time = -1;
	private long id = -1;

	public long getId() {
		return id;
	}

	@Override
	public int compareTo(AmiWebTimedEvent o) {
		return OH.compare(this.time, o.time);
	}

	public long getTime() {
		return time;
	}

	@Override
	public String toString() {
		return "AmiWebTimedEvent[id=" + id + "  time=" + DateMillis.format(time) + "]";
	}

	abstract public void execute(AmiWebService s);

	abstract public String describe();

	public void init(long time, long id) {
		if (id == -1L || time == -1L)
			throw new IllegalArgumentException();
		if (this.id != -1L || this.time != -1L)
			throw new IllegalStateException();
		this.time = time;
		this.id = id;
	}

}
