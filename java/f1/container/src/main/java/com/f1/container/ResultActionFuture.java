/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

import java.util.concurrent.TimeUnit;

import com.f1.base.Action;
import com.f1.container.exceptions.ContainerInterruptedException;

public interface ResultActionFuture<A extends Action> {
	static final long DEFAULT_TIMEOUT = -1L;

	/**
	 * Called by the consumer of the result (typically this is the producer of the request)<BR>
	 * Block until the request has been provided via {@link #provideResult(ResultMessage)} or the specified timeout has lapsed. <BR>
	 * Note, that if the result is not a final result, as indicated by {@link ResultMessage#getIsIntermediateResult()} then subsequent calls to the future will again block until
	 * another result is available.
	 * 
	 * @param delay
	 *            one of the following<BR>
	 *            <i>greater than zero</i> - the amount of time to wait (in supplied units). <BR>
	 *            <i>-1</i> - indicates that it should wait the default amount of time. see {@link DispatchController#getDefaultFutureTimeoutMs()}. <BR>
	 *            <i>0</i> - do not timeout
	 * @param units
	 *            if delay is greater than zero, indicates unit of time to wait
	 * @return the result, or null if not available after lapsed timeout. See class level comments on Intermediate Responses
	 * @throws ContainerInterruptedException
	 *             if calling thread was interrupted
	 */
	public ResultMessage<A> getResult(long delay, TimeUnit units) throws ContainerInterruptedException;

	/**
	 * Called by the consumer of the result (typically this is the producer of the request)<BR>
	 * Block until the request has been provided via {@link #provideResult(ResultMessage)}, or the default timeout has lapsed. see
	 * {@link DispatchController#getDefaultFutureTimeoutMs()} <BR>
	 * Note, that if the result is not a final result, as indicated by {@link ResultMessage#getIsIntermediateResult()} then subsequent calls to the future will again block until
	 * another result is available.
	 * 
	 * @return the result, or null if not available after lapsed timeout. See class level comments on final Responses
	 * @throws ContainerInterruptedException
	 *             if calling thread was interrupted
	 */
	public ResultMessage<A> getResult() throws ContainerInterruptedException;

	/**
	 * Called by the consumer of the result (typically this is the producer of the request)<BR>
	 * Block until the request has been provided via {@link #provideResult(ResultMessage)}, or the specified timeout has lapsed. <BR>
	 * Note, that if the result is not a final result, as indicated by {@link ResultMessage#getIsIntermediateResult()} then subsequent calls to the future will again block until
	 * another result is available.
	 * 
	 * @param delay
	 *            one of the following<BR>
	 *            <i>greater than zero</i> - the amount of time to wait (in milliseconds). <BR>
	 *            <i>-1</i> - indicates that it should wait the default amount of time. see {@link DispatchController#getDefaultFutureTimeoutMs()}. <BR>
	 *            <i>0</i> - do not timeout
	 * @return the result, or null if not available after lapsed timeout. See class level comments on final Responses
	 * @throws ContainerInterruptedException
	 *             if calling thread was interrupted
	 */
	public ResultMessage<A> getResult(long delayMs) throws ContainerInterruptedException;

	/**
	 * Called by the consumer of the result (typically this is the producer of the request)<BR>
	 * Block until the request has been processed, or the specified timeout has lapsed. Because this is a 'peek', if a result is returned it is not cleared out of this future,
	 * meaning that subsequent calls may return the same result (unless a newer result is provided)
	 * 
	 * @param delay
	 *            one of the following<BR>
	 *            <i>greater than zero</i> - the amount of time to wait (in supplied units). <BR>
	 *            <i>-1</i> - indicates that it should wait the default amount of time. see {@link DispatchController#getDefaultFutureTimeoutMs()}. <BR>
	 *            <i>0</i> - do not timeout
	 * @param units
	 *            if delay is greater than zero, indicates unit of time to wait
	 * @return the result, or null if not available after lapsed timeout. See class level comments on Intermediate Responses
	 * @throws ContainerInterruptedException
	 *             if calling thread was interrupted
	 */
	public ResultMessage<A> peekResult(long timeout, TimeUnit units) throws ContainerInterruptedException;

	/**
	 * Called by the consumer of the result (typically this is the producer of the request)<BR>
	 * Block until the request has been processed, or the specified timeout has lapsed. Because this is a 'peek', if a result is returned it is not cleared out of this future,
	 * meaning that subsequent calls may return the same result (unless a newer result is provided)
	 * 
	 * @param delay
	 *            one of the following<BR>
	 *            <i>greater than zero</i> - the amount of time to wait (in milliseconds). <BR>
	 *            <i>-1</i> - indicates that it should wait the default amount of time. see {@link DispatchController#getDefaultFutureTimeoutMs()}. <BR>
	 *            <i>0</i> - do not timeout
	 * @return the result, or null if not available after lapsed timeout. See class level comments on final Responses
	 * @throws ContainerInterruptedException
	 *             if calling thread was interrupted
	 */
	public ResultMessage<A> peekResult(long timeoutMs) throws ContainerInterruptedException;

	/**
	 * Called by the producer of the result. Any number of 'non-final' results may be provided in succession but once a 'final' result is provided no further results may be
	 * provided. See {@link ResultMessage#getIsIntermediateResult()} for details on what a final result is.
	 * 
	 * @param result
	 *            the result that has been processed.
	 */
	public void provideResult(ResultMessage<A> result);

	/**
	 * @return each future has an auto-incrememting unique id (for life of the jvm)
	 */
	public long getFutureId();

}
