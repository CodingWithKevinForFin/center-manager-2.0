/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import com.f1.container.ContainerConstants;
import com.f1.container.ThreadPoolController;
import com.f1.container.ThreadScope;
import com.f1.container.exceptions.ContainerException;
import com.f1.utils.CopyOnWriteHashMap;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.FastThreadPool;

public class BasicThreadPoolController extends AbstractContainerScope implements ThreadPoolController {

	private String defaultThreadPoolKey = ContainerConstants.DEFAULT_THREAD_POOL_KEY;

	public static class PendingExecute {

		public Runnable runnable;
		public Object poolKey;
		public long time;
		public TimeUnit units;

		public PendingExecute(Runnable runnable, Object poolKey, long time, TimeUnit units) {
			this.runnable = runnable;
			this.poolKey = poolKey;
			this.time = time;
			this.units = units;
		}

	}

	private static final int MAX_THREAD_POOL_PREFIX = 16;

	ScheduledThreadPoolExecutor scheduledExecutor = null;
	FastThreadPool generalExecutor = null;
	private ConcurrentMap<Object, Executor> threadPoolExecutors = new CopyOnWriteHashMap<Object, Executor>();
	private int defaultThreadPoolSize = ContainerConstants.DEFAULT_THREAD_POOL_SIZE;

	private List<PendingExecute> temporaryEvents = new ArrayList<PendingExecute>();
	private List<Runnable> temporaryRunnables = new ArrayList<Runnable>();

	static final private byte STATE_REJECT_EXT_ACTIONS = 1;
	static final private byte STATE_REJECT_ALL_ACTIONS = 2;
	static final private byte STATE_QUEUE_ACTIONS = 3;
	static final private byte STATE_PROCESS_ACTIONS = 4;
	private byte state = STATE_REJECT_ALL_ACTIONS;

	private boolean threadPoolKeyLocked;

	@Override
	public void execute(Runnable runnable, Object poolKey, long time, TimeUnit units) {
		switch (state) {
			case STATE_PROCESS_ACTIONS:
				break;
			case STATE_QUEUE_ACTIONS:
				LH.info(log, "Received runnable before started, queueing action.");
				synchronized (temporaryEvents) {
					if (state == STATE_QUEUE_ACTIONS) {
						temporaryEvents.add(new PendingExecute(runnable, poolKey, time, units));
						return;
					}
				}
				LH.info(log, "State changed, resubmitting");
				execute(runnable, poolKey, time, units);//the state changed underneath us, lets try again
				return;
			case STATE_REJECT_EXT_ACTIONS:
				if (Thread.currentThread() instanceof ThreadScope)
					break;
				throw new ContainerException("In a stoppedDispatching state & no longer receiving external actions").set("poolKey", poolKey).set("runnable", runnable);
			case STATE_REJECT_ALL_ACTIONS:
				throw new ContainerException("In a stopped state & not processing actions").set("poolKey", poolKey).set("runnable", runnable);
			default:
				throw new RuntimeException("unknown state: " + state);
		}

		if (time > 0) {
			if (poolKey != null && !POOLKEY_DELAYED.equals(poolKey) && log.isLoggable(Level.INFO))
				LH.info(log, "Ignoring poolkey '", poolKey, "' beacuse this is a scheduled (", time, " ", units, ")event: ", runnable);
			scheduledExecutor.schedule(runnable, time, units);
			return;
		}

		Executor pool = threadPoolExecutors.get(poolKey);
		if (pool == null) {
			Executor existing = threadPoolExecutors.putIfAbsent(poolKey, pool = createPool(poolKey));
			if (existing != null)
				pool = existing;
		}
		pool.execute(runnable);
	}

	private boolean useAggressiveThreadPool = true;

	private int maximumPoolSizeForGeneralThreadPool = 32;

	private boolean useFastExecute = true;

	private Executor createPool(Object poolKey) {
		assertInit();
		if (useAggressiveThreadPool) {
			FastThreadPool r = new FastThreadPool(getDefaultThreadPoolSize(), createThreadPoolFactory(poolKey));
			r.start();
			return r;
		} else {
			ScheduledThreadPoolExecutor r = new ScheduledThreadPoolExecutor(getDefaultThreadPoolSize());
			r.setThreadFactory(createThreadPoolFactory(poolKey));
			return r;
		}
	}

	@Override
	public Executor getThreadPool(Object poolKey) {
		return this.threadPoolExecutors.get(poolKey);
	}

	@Override
	public void putThreadPool(Object poolkey, Executor executor) {
		this.threadPoolExecutors.put(poolkey, executor);
		assertNotStarted();
	}

	private ThreadFactory createThreadPoolFactory(Object poolKey) {
		return new ThreadScopeFactory(SH.substring(SH.toString(poolKey), 0, MAX_THREAD_POOL_PREFIX));
	}

	@Override
	public void setDefaultThreadPoolSize(int defaultThreadPoolSize) {
		assertNotStarted();
		this.defaultThreadPoolSize = OH.assertGt(defaultThreadPoolSize, 0);
	}

	@Override
	public int getDefaultThreadPoolSize() {
		return defaultThreadPoolSize;
	}

	private class ThreadScopeFactory implements ThreadFactory {
		private AtomicInteger id = new AtomicInteger(0);
		private final String name;

		public ThreadScopeFactory(String name) {
			this.name = name;
		}

		@Override
		public Thread newThread(final Runnable r) {
			return getContainer().getThreadScopeController().newThreadScope(name, r, name + "-" + SH.rightAlign('0', SH.toString(id.getAndIncrement()), 2, false));
		}
	}

	@Override
	public void startDispatching() {
		super.startDispatching();
		synchronized (temporaryEvents) {
			state = STATE_PROCESS_ACTIONS;
			LH.info(log, "Dispatching ", temporaryEvents.size(), " event(s)");
			for (PendingExecute pe : temporaryEvents)
				execute(pe.runnable, pe.poolKey, pe.time, pe.units);
			temporaryEvents.clear();
		}
		synchronized (temporaryRunnables) {
			state = STATE_PROCESS_ACTIONS;
			LH.info(log, "Dispatching ", temporaryRunnables.size(), " runnables(s)");
			for (Runnable pe : temporaryRunnables)
				execute(pe);
			temporaryRunnables.clear();
		}
	}

	@Override
	public void stopDispatching() {
		super.stopDispatching();
		this.scheduledExecutor.shutdown();
		this.generalExecutor.stop();
		state = STATE_REJECT_EXT_ACTIONS;
	}

	@Override
	public void init() {
		super.init();
		this.scheduledExecutor = new ScheduledThreadPoolExecutor(2);
		this.scheduledExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
		this.scheduledExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
		this.generalExecutor = new FastThreadPool(getMaximumPoolSizeForGeneralThreadPool(), this.getContainer().getName() + "-GP-");
		this.generalExecutor.start();
		if (getUseAggressiveThreadPool())
			LH.info(log, "Threadpool operating in Aggressive mode. " + getDefaultThreadPoolSize() + " default threads");
		else
			LH.info(log, "Threadpool operating in Standard mode. " + getDefaultThreadPoolSize() + " default threads");
		this.state = STATE_QUEUE_ACTIONS;
	}

	@Override
	public void start() {
		this.state = STATE_QUEUE_ACTIONS;
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
		state = STATE_REJECT_ALL_ACTIONS;
	}

	@Override
	public void setDefaultThreadPoolKey(String threadPoolName) {
		assertNotDispatchingStarted();
		assertNotStarted();
		this.defaultThreadPoolKey = threadPoolName;
	}

	@Override
	public String getDefaultThreadPoolKey() {
		return this.defaultThreadPoolKey;
	}

	@Override
	public void execute(Runnable command) {
		assertInit();
		switch (state) {
			case STATE_PROCESS_ACTIONS:
				generalExecutor.execute(command);
				return;
			case STATE_QUEUE_ACTIONS:
				LH.info(log, "Received runnable before started, queueing action.");
				synchronized (temporaryRunnables) {
					if (state == STATE_QUEUE_ACTIONS) {
						temporaryRunnables.add(command);
						return;
					}
				}
				LH.info(log, "State changed, resubmitting");
				generalExecutor.execute(command);
				return;
			case STATE_REJECT_EXT_ACTIONS:
				if (Thread.currentThread() instanceof ThreadScope)
					break;
				throw new ContainerException("In a stoppedDispatching state & no longer receiving external actions").set("runnable", command);
			case STATE_REJECT_ALL_ACTIONS:
				throw new ContainerException("In a stopped state & not processing actions").set("runnable", command);
			default:
				throw new RuntimeException("unknown state: " + state);
		}
	}

	@Override
	public boolean getUseAggressiveThreadPool() {
		return useAggressiveThreadPool;
	}

	@Override
	public void setUseAggressiveThreadPool(boolean useAggressiveThreadPool) {
		assertNotInit();
		this.useAggressiveThreadPool = useAggressiveThreadPool;
	}

	@Override
	public int getMaximumPoolSizeForGeneralThreadPool() {
		return maximumPoolSizeForGeneralThreadPool;
	}

	@Override
	public void setMaximumPoolSizeForGeneralThreadPool(int maximumPoolSizeForGeneralThreadPool) {
		assertNotInit();
		this.maximumPoolSizeForGeneralThreadPool = maximumPoolSizeForGeneralThreadPool;
	}

	@Override
	public boolean getUseThreadPoolFastExecute() {
		return this.useFastExecute;
	}

	@Override
	public void setUseThreadPoolFastExecute(boolean useFastExecute) {
		this.useFastExecute = useFastExecute;
	}
}
