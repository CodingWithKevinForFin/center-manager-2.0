/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * Manages a set of thread pools. When {@link Runnable}s are submitted for execution they can be associated with a pool key. The pool key is used to determine which pool the
 * runnable should be submitted to. Having multiple pools ensures events of differening importance will not exaust pools for events for other key types. It is intended that there
 * is a default pool which will process events unassociated with a specific pool.
 */
public interface ThreadPoolController extends ContainerScope, Executor {

	/*
	 * The default name of the thread pool responsible for handling delayed events. see
	 * {@link DispatchController#dispatch(Port, Processor, com.f1.base.Action, Object, long, ThreadScope)}
	 */
	String POOLKEY_DELAYED = "POOLKEY_DELAYED";

	/**
	 * run the supplied runnable using a thread pool associated with the supplied pool key after the specified delay
	 * 
	 * @param runnable
	 *            runnable to run
	 * @param poolKey
	 *            the key of the pool to execute the runnable in.
	 * @param time
	 *            the minimum time (in supplied units) to delay before running. 0 for no delay.
	 * @param units
	 *            units for supplied delay time
	 */
	void execute(Runnable runnable, Object poolKey, long time, TimeUnit units);

	/**
	 * set the default size for a thread pool, must be positive number
	 * 
	 * @param defaultThreadPoolSize
	 */
	void setDefaultThreadPoolSize(int defaultThreadPoolSize);

	/**
	 * get the default size for a thread pool
	 * 
	 * @return positive integer
	 */

	int getDefaultThreadPoolSize();

	/**
	 * @param threadPoolName
	 *            the name of the "default" threadpool.
	 */
	public void setDefaultThreadPoolKey(String threadPoolName);

	/**
	 * @return the name of the "default" threadpool.
	 */
	public String getDefaultThreadPoolKey();

	/**
	 * @return Should the default thread pools be F1 Aggressive thread pools, which are much more agressive (keep at least one thread spinning)
	 */
	boolean getUseAggressiveThreadPool();

	/**
	 * 
	 * @param useAggressiveThreadPool
	 *            Should the default thread pools be F1 Aggressive thread pools, which are much more agressive (keep at least one thread spinning)
	 */
	void setUseAggressiveThreadPool(boolean useAggressiveThreadPool);

	boolean getUseThreadPoolFastExecute();
	void setUseThreadPoolFastExecute(boolean useFastExecute);

	/**
	 * @return max thread pool size per pool
	 */
	int getMaximumPoolSizeForGeneralThreadPool();

	/**
	 * @param maximumPoolSizeForGeneralThreadPool
	 *            max thread pool size per pool
	 */
	void setMaximumPoolSizeForGeneralThreadPool(int maximumPoolSizeForGeneralThreadPool);

	/**
	 * @param poolKey
	 * @return A thread pool assoctiated with the given key or null.
	 */
	Executor getThreadPool(Object poolKey);

	/**
	 * Manually set a thread pool. Must be done prior to starting up container. Can be used to replace a threadpool
	 * 
	 * @param poolkey
	 *            the id ot the thread pool.
	 * @param executor
	 *            the nmew threadpool.
	 */
	void putThreadPool(Object poolkey, Executor executor);
}
