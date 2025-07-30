package com.f1.container.impl;

import com.f1.container.Container;
import com.f1.utils.ThreadRunner;

public abstract class AbstractThreadedAdapter extends AbstractContainerListener implements Runnable {
	private final ThreadRunner runner;

	public AbstractThreadedAdapter() {
		this(null);
	}

	public AbstractThreadedAdapter(String threadName) {
		if (threadName == null)
			threadName = getClass().getSimpleName();
		this.runner = new ThreadRunner(threadName, false, this);
	}

	public void setDaemon(boolean isDeamon) {
		runner.setDaemon(isDeamon);
	}

	@Override
	public void onPostStart(Container container) {
		runner.startRunning();
	}

	@Override
	public void onPreStop(Container container) {
		if (runner.getIsRunning())
			runner.stopRunning();
	}

	public boolean shouldKeepRunning() {
		return runner.getIsRunning();
	}

	public void pause() {
		runner.stopRunning();
	}

	public void unpause() {
		runner.startRunning();
	}
}
