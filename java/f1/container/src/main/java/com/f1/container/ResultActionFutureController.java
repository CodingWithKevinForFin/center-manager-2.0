package com.f1.container;

public interface ResultActionFutureController extends ContainerScope {

	long DEFAULT_FUTURE_TIMEOUT_MS = 60 * 1000;
	/**
	 * @return the default amount of time to block (in milliseconds) when waiting for a result. See {@link ResultActionFuture} for details
	 */
	long getDefaultFutureTimeoutMs();

	/**
	 * @param defaultFutureTimeoutMs
	 *            the amount of time, unless specified that a Future on a request will wait before timing out.
	 */
	void setDefaultFutureTimeoutMs(long defaultFutureTimeoutMs);

	/**
	 * @param containerScope
	 *            the container scope associated with the future
	 * @return a new future instance
	 */
	ResultActionFuture<?> createFuture(ContainerScope containerScope);

	void onFutureGotResultAfterTimeout(ResultActionFuture<?> raf);

	/**
	 * call back indicating that someone was waiting for a result but the timeout was reached. Generally this should throw an exception
	 * 
	 * @param raf
	 * @param waitMs
	 */
	void onFutureTimeout(ResultActionFuture<?> raf, long waitMs);

}
