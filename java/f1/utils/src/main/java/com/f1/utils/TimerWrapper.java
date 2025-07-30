package com.f1.utils;

public class TimerWrapper implements Timer {
	private long endTime;
	private long startTime;
	private Timer inner;

	/**
	 * 
	 * @param inner
	 * @param startTime
	 *            inclusive
	 * @param endTime
	 *            exclusive
	 */
	public TimerWrapper(Timer inner, long startTime, long endTime) {
		this.inner = inner;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public long getStartTime() {
		return startTime;
	}

	@Override
	public long calculateNextOccurance(long now) {
		if (now >= endTime)
			return -1;
		if (now < startTime)
			now = startTime;
		long r = inner.calculateNextOccurance(now);
		if (r >= endTime)
			return -1;
		return r;
	}

}
