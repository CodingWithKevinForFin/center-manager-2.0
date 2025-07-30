package com.f1.utils.impl;

import java.util.logging.Logger;

import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.Retry;

public class BasicRetry implements Retry {
	private static final Logger log = Logger.getLogger(BasicRetry.class.getName());
	private int maxAttempts;
	private long maxTimeMs;
	private boolean success;
	private int failedCount = 0;
	long startTime;
	private boolean readyToTry = false;
	private long sleepMs;
	public BasicRetry() {
		this.maxAttempts = DEFAULT_MAX_ATTEMPTS;
		this.maxTimeMs = DEFAULT_MAX_TIME_MS;
		this.sleepMs = DEFAULT_SLEEP_TIME_MS;
	}

	@Override
	public <T extends Exception> void onError(T exception) throws T {
		if (!readyToTry)
			throw new IllegalStateException("tryAgain() must be called first", exception);
		if (this.success)
			throw new IllegalStateException("Already succeeded", exception);
		readyToTry = false;
		failedCount++;
		if (maxAttempts == NO_MAX_ATTEMPTS)
			LH.info(log, "Attempt ", failedCount, " has failed:", exception.getMessage());
		else
			LH.info(log, "Attempt ", failedCount, " / ", maxAttempts, " has failed: ", exception.getMessage());
		if (maxAttempts != NO_MAX_ATTEMPTS && failedCount >= maxAttempts)
			throw exception;
		if (maxTimeMs != NO_MAX_TIME_MS && startTime + maxTimeMs < System.currentTimeMillis())
			throw exception;
		if (sleepMs != NO_SLEEP_TIME_MS) {
			LH.info(log, "Sleeping for ", sleepMs, "ms before next attempt.");
			OH.sleep(sleepMs);
		}
	}

	@Override
	public void onSuccess() {
		if (!readyToTry)
			throw new IllegalStateException("tryAgain() must be called first");
		if (this.success)
			throw new IllegalStateException("Already succeeded");
		readyToTry = false;
		this.success = true;
	}

	@Override
	public boolean shouldTryAgain() {
		if (readyToTry)
			throw new IllegalStateException("tryAgain() already called");
		if (startTime == 0)
			startTime = System.currentTimeMillis();
		readyToTry = true;
		return !success;
	}

	@Override
	public Retry setSleepMs(long sleepMs) {
		if (startTime != 0)
			throw new IllegalStateException("already started");
		this.sleepMs = sleepMs;
		return this;
	}

	@Override
	public Retry setMaxAttempts(int maxAttempts) {
		if (startTime != 0)
			throw new IllegalStateException("already started");
		this.maxAttempts = maxAttempts;
		return this;
	}

	@Override
	public Retry setMaxTimeMs(long maxTimeMs) {
		if (startTime != 0)
			throw new IllegalStateException("already started");
		this.maxTimeMs = maxTimeMs;
		return this;
	}

}



