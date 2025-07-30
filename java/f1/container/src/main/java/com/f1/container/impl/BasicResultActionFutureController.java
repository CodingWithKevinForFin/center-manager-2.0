package com.f1.container.impl;

import java.util.logging.Logger;

import com.f1.container.ContainerScope;
import com.f1.container.ResultActionFuture;
import com.f1.container.ResultActionFutureController;
import com.f1.utils.LH;

public class BasicResultActionFutureController extends AbstractContainerScope implements ResultActionFutureController {
	private static final Logger log = LH.get();
	private long defaultFutureTimeoutMs = DEFAULT_FUTURE_TIMEOUT_MS;

	@Override
	public void onFutureGotResultAfterTimeout(ResultActionFuture<?> raf) {
		LH.warning(log, "Received result for future after timeout, futureId: " + raf.getFutureId());
	}

	@Override
	public void onFutureTimeout(ResultActionFuture<?> raf, long waitMs) {
		LH.warning(log, "future timeout, futureId: " + raf.getFutureId(), " timeout: ", waitMs);
	}

	@Override
	public long getDefaultFutureTimeoutMs() {
		return defaultFutureTimeoutMs;
	}

	@Override
	public void setDefaultFutureTimeoutMs(long defaultFutureTimeoutMs) {
		assertNotStarted();
		this.defaultFutureTimeoutMs = defaultFutureTimeoutMs;
	}

	@Override
	public ResultActionFuture<?> createFuture(ContainerScope containerScope) {
		return new BasicResultActionFuture(containerScope);
	}
}
