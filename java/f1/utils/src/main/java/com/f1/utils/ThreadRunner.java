package com.f1.utils;

import java.util.concurrent.TimeUnit;

public final class ThreadRunner implements Runnable {
	private boolean isThreadActive = false;
	private boolean isRunning = false;
	private RestartableThread thread;
	private Runnable runnable;

	public ThreadRunner(Runnable runnable) {
		this("Thread Runner", false, runnable);
	}

	public ThreadRunner(String threadName, boolean deamon, Runnable runnable) {
		this.thread = new RestartableThread(this, threadName);
		thread.setDaemon(deamon);
		this.runnable = runnable;
	}

	synchronized public void startRunning() {
		if (isRunning)
			throw new IllegalStateException("already running");
		isRunning = true;
		thread.start();
	}

	@Override
	public void run() {
		isThreadActive = true;
		while (isRunning)
			try {
				runnable.run();
			} catch (RuntimeException e) {
				isThreadActive = false;
				throw e;
			} catch (Error e) {
				isThreadActive = false;
				throw e;
			}
		isThreadActive = false;
	}

	synchronized public void stopRunning() {
		if (!isRunning)
			throw new IllegalStateException("already not running");
		isRunning = false;
		while (isThreadActive || thread.isAlive()) {
			thread.interrupt();
			if (isThreadActive)
				OH.sleep(TimeUnit.MILLISECONDS, 10);
		}
	}

	public boolean getIsRunning() {
		return isRunning;
	}

	public void setDaemon(boolean isDeamon) {
		thread.setDaemon(isDeamon);
	}

}
