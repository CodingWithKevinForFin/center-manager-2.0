/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

public class TimeoutMonitor {

	final private long timeoutMs;
	final private int checkFrequency;
	volatile private long timeoutTimeMs;
	private int checks;
	private long timedOutMs;
	private long startTime;

	public TimeoutMonitor(long timeoutMs, int checkFrequency) {
		this.timeoutMs = timeoutMs;
		this.checkFrequency = checkFrequency;
		OH.assertGt(this.checkFrequency, 0, "check Frequency must be positive");
		reset();
	}

	public void reset() {
		this.timedOutMs = -1;
		this.checks = 0;
		this.startTime = EH.currentTimeMillis();
		this.timeoutTimeMs = timeoutMs + startTime;
	}

	public void incrementChecks() {
		checks++;
	}

	public boolean hasTimedout() {
		if (timedOutMs != -1)
			return true;
		if ((++checks % checkFrequency) == 0) {
			long now = EH.currentTimeMillis();
			if (now > timeoutTimeMs) {
				timedOutMs = now;
				return true;
			}
		}
		return false;
	}

	public int getChecksCount() {
		return checks;
	}
	public int getChecksCountAndClear() {
		int r = checks;
		this.checks = 0;
		return r;
	}

	public long getStartTimeMs() {
		return startTime;
	}

	public long getTimedOutTimeMs() {
		return timedOutMs;
	}

	public long getDurationMs() {
		if (timedOutMs == -1)
			return -1;
		return getTimedOutTimeMs() - getStartTimeMs();
	}

	public boolean hasAlreadyTimedout() {
		return timedOutMs != -1;
	}

	public void forceTimeout() {
		timeoutTimeMs = 0;
	}

	public boolean wasForced() {
		return this.timeoutTimeMs == 0;
	}

}
