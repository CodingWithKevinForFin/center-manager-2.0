package com.f1.utils;

import java.util.logging.Logger;

import com.f1.utils.concurrent.Cancellable;

public class MonitoredRunnable implements Runnable {

	public static final byte STATE_INIT = 1;
	public static final byte STATE_RUNNING = 2;
	public static final byte STATE_COMPLETE = 3;
	public static final byte STATE_THREW_EXCEPTION = 4;
	private static final Logger log = LH.get();
	final private Runnable inner;
	private volatile byte state;
	private final Object semephore = new Object();
	private Thread runningThread;
	private Throwable thrown;
	private boolean throwOnException;

	public MonitoredRunnable(Runnable inner) {
		this(inner, true);
	}
	public MonitoredRunnable(Runnable inner, boolean throwOnException) {
		this.inner = inner;
		this.throwOnException = throwOnException;
		state = STATE_INIT;
	}

	@Override
	public void run() {
		this.runningThread = Thread.currentThread();
		try {
			setState(STATE_RUNNING);
			inner.run();
			this.runningThread = null;
			setState(STATE_COMPLETE);
		} catch (Throwable e) {
			this.thrown = e;
			this.runningThread = null;
			setState(STATE_THREW_EXCEPTION);
			if (throwOnException)
				throw e;
		}
	}

	public Throwable getThrown() {
		return thrown;
	}

	private void setState(byte state) {
		synchronized (semephore) {
			this.state = state;
			semephore.notifyAll();
		}
	}
	public byte getState() {
		return state;
	}

	public void waitUntilComplete() throws InterruptedException {
		if (!isComplete())
			synchronized (semephore) {
				while (!isComplete()) {
					semephore.wait();
				}
			}
	}
	/**
	 * 
	 * @param timeout
	 * @return true iff completed before timeout
	 * @throws InterruptedException
	 */
	public boolean waitUntilComplete(long timeout) throws InterruptedException {
		long now = EH.currentTimeMillis();
		long end = now + timeout;
		if (!isComplete())
			synchronized (semephore) {
				while (!isComplete()) {
					if (now > end)
						return false;
					semephore.wait(timeout);
					now = EH.currentTimeMillis();
				}
			}
		return true;
	}

	private boolean isComplete() {
		switch (state) {
			case STATE_COMPLETE:
			case STATE_THREW_EXCEPTION:
				return true;
		}
		return false;
	}

	public void interruptThread() {
		Thread rt = this.runningThread;
		if (rt != null)
			rt.interrupt();
		if (this.inner instanceof Cancellable) {
			((Cancellable) this.inner).cancel();
		}
	}
	public void interruptThreadNoThrow(boolean shouldLog) {
		try {
			Thread rt = this.runningThread;
			if (rt != null)
				rt.interrupt();
			if (this.inner instanceof Cancellable) {
				((Cancellable) this.inner).cancel();
			}
		} catch (Exception e) {
			if (shouldLog)
				LH.info(log, "Exception while interrupting monitored thread", e);
		}
	}

}
