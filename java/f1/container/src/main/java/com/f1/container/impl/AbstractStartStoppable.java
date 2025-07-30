/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import com.f1.container.ContainerScope;
import com.f1.container.StartStoppable;
import com.f1.container.exceptions.ContainerException;
import com.f1.utils.EH;

public class AbstractStartStoppable implements StartStoppable {

	private boolean started = false;
	private long startedMs = StartStoppable.NO_TIME;

	@Override
	final public void assertNotStarted() {
		if (!started)
			return;
		ContainerException e = new ContainerException("already Started: start() already called.");
		if (this instanceof ContainerScope)
			e.setContainerScope((ContainerScope) this);
		throw e;
	}

	@Override
	final public void assertStarted() {
		if (started)
			return;
		ContainerException e = new ContainerException("not Started: start() not called.");
		if (this instanceof ContainerScope)
			e.setContainerScope((ContainerScope) this);
		throw e;
	}

	@Override
	final public boolean isStarted() {
		return started;
	}

	@Override
	public void start() {
		assertNotStarted();
		synchronized (this) {
			started = true;
			startedMs = EH.currentTimeMillis();
		}
	}

	@Override
	public void stop() {
		assertStarted();
		synchronized (this) {
			started = false;
			startedMs = -1;
		}
	}

	@Override
	public long getStartedMs() {
		return startedMs;
	}

}
