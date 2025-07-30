/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import com.f1.base.Action;
import com.f1.container.ContainerScope;
import com.f1.container.ResultActionFuture;
import com.f1.container.ResultMessage;
import com.f1.container.exceptions.ContainerException;
import com.f1.container.exceptions.ContainerInterruptedException;
import com.f1.container.exceptions.ContainerTimeoutException;
import com.f1.utils.EH;
import com.f1.utils.LH;

public class BasicResultActionFuture<A extends Action> implements ResultActionFuture<A> {
	private static final Logger log = LH.get();
	private static final AtomicLong NEXT_TIMEOUTID = new AtomicLong(1);
	private ContainerScope containerScope;
	private final long id = NEXT_TIMEOUTID.getAndIncrement();
	private boolean timedout = false;

	public BasicResultActionFuture(ContainerScope containerScope) {
		this.containerScope = containerScope;
	}

	volatile private ResultMessage<A> result;

	@Override
	public ResultMessage<A> getResult(long delay, TimeUnit units) throws ContainerInterruptedException {
		return getResult(units.toMillis(delay));
	}

	@Override
	public ResultMessage<A> getResult() {
		return getResult(DEFAULT_TIMEOUT);
	}

	@Override
	public ResultMessage<A> getResult(long waitMs) {
		if (result != null && !result.getIsIntermediateResult())
			return result;
		synchronized (this) {
			timedout = false;
			if (result == null) {
				if (waitMs == DEFAULT_TIMEOUT)
					waitMs = containerScope.getContainer().getResultActionFutureController().getDefaultFutureTimeoutMs();
				try {
					if (waitMs == 0) {
						while (result == null)
							wait();
					} else {
						final long startTime = EH.currentTimeMillis();
						long nextTimeout = waitMs;
						while (true) {
							wait(nextTimeout);
							if (result != null)
								break;
							nextTimeout = waitMs + startTime - EH.currentTimeMillis();
							if (nextTimeout <= 0) {
								timedout = true;
								containerScope.getContainer().getResultActionFutureController().onFutureTimeout(this, waitMs);
								throw new ContainerTimeoutException("futureId: " + getFutureId() + ", timeout: " + waitMs + "ms");
							}
						}
					}
				} catch (final InterruptedException e) {
					throw new ContainerInterruptedException("Interrupt during wait for response future with timeout: " + waitMs + ", futureId: " + getFutureId(), e, null);
				}
			}
			if (result.getIsIntermediateResult())
				return result;
			ResultMessage<A> r = result;
			result = null;
			return r;
		}
	}

	@Override
	public ResultMessage<A> peekResult(long timeout, TimeUnit units) {
		return peekResult(units.toMillis(timeout));
	}

	@Override
	public ResultMessage<A> peekResult(long timeoutMs) {
		if (result != null || timeoutMs <= 0)
			return result;
		long start = EH.currentTimeMillis();
		synchronized (this) {
			long waitTime = timeoutMs;
			while (result == null && waitTime > 0) {
				try {
					wait(waitTime);
				} catch (InterruptedException e) {
					break;
				}
				waitTime = timeoutMs + start - EH.currentTimeMillis();
			}
		}
		return result;
	}

	@Override
	public void provideResult(ResultMessage<A> ra) {
		if (result != null && !result.getIsIntermediateResult())
			throw new ContainerException("future response already provided: " + ra);
		synchronized (this) {
			this.result = ra;
			notify();
			if (timedout)
				containerScope.getContainer().getResultActionFutureController().onFutureGotResultAfterTimeout(this);
		}
	}
	@Override
	public long getFutureId() {
		return id;
	}
}
