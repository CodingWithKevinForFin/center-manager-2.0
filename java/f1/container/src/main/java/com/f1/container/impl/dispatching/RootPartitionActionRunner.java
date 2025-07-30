/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl.dispatching;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.base.Action;
import com.f1.container.DispatchController;
import com.f1.container.Partition;
import com.f1.container.Processor;
import com.f1.container.ThreadPoolController;
import com.f1.container.exceptions.ContainerException;
import com.f1.container.impl.BasicPartition;
import com.f1.container.impl.BasicThreadScope;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.TimeoutMonitor;

public class RootPartitionActionRunner implements PartitionActionRunner, Runnable, Comparator<PartitionActionRunner> {

	private static final Logger log = Logger.getLogger(RootPartitionActionRunner.class.getName());
	public static final long AQUIRE_LOCK_TIMEOUT_MS = 1000;
	final private PartitionActionRunner runnersInRunOrder[];
	final private PartitionActionRunner runnersInCheckOrder[];

	public final AtomicBoolean inThreadPool = new AtomicBoolean(false);

	public final Object partitionId;

	final private DispatchController dispatcher;

	final private ThreadPoolController threadPoolController;
	final private Partition partition;

	public final TimeoutMonitor timeoutMonitor;

	private volatile long statActionsProcessed;
	private volatile long statTimeSpentMs;
	private volatile long statQueueRuns;
	private final long startTime = EH.currentTimeMillis();
	private final AtomicLong actionsAdded = new AtomicLong();
	private HighPriorityPartitionActionRunner highPriorityRunner;

	public long getActionsAdded() {
		return actionsAdded.get();
	}

	public RootPartitionActionRunner(PartitionActionRunner runners[], DispatchController dispatcher, Partition partition) {
		this.timeoutMonitor = new TimeoutMonitor(dispatcher.getQueueTimeoutMs(), dispatcher.getQueueTimeoutCheckFrequency());
		if (partition instanceof BasicPartition)
			((BasicPartition) partition).setRootPartitionActionRunner(this);
		runnersInCheckOrder = runners.clone();
		runnersInRunOrder = runners.clone();
		for (PartitionActionRunner runner : runnersInRunOrder) {
			if (runner instanceof HighPriorityPartitionActionRunner)
				this.highPriorityRunner = (HighPriorityPartitionActionRunner) runner;
		}
		Arrays.sort(runnersInCheckOrder, this);
		this.partition = partition;
		this.partitionId = partition.getPartitionId();
		this.dispatcher = dispatcher;
		this.threadPoolController = this.dispatcher.getContainer().getThreadPoolController();
	}

	private volatile PartitionActionRunner currentRunner;

	@Override
	public void addAction(Processor processor, Action action, BasicThreadScope threadScope) {
		for (PartitionActionRunner r : runnersInCheckOrder)
			if (r.canProcess(action)) {
				r.addAction(processor, action, threadScope);
				if (r instanceof HighPriorityPartitionActionRunner && currentRunner != null && !(currentRunner instanceof HighPriorityPartitionActionRunner))
					timeoutMonitor.forceTimeout();
				sendToThreadPool();
				actionsAdded.incrementAndGet();
				return;
			}
		throw new ContainerException("can not process action").setAction(action);

	}

	@Override
	public void runActions(BasicThreadScope thread, Partition partition, TimeoutMonitor timeout) {
		for (PartitionActionRunner r : runnersInRunOrder) {
			if (r.hasActions()) {
				currentRunner = r;
				r.runActions(thread, partition, timeout);
				currentRunner = null;
				if (timeout.hasAlreadyTimedout()) {
					int count = getPendingActionsCount();
					if (count > 0 && !timeout.wasForced() && timeout.getChecksCount() > 1)
						LH.info(log, "timeout for ", partition.getPartitionId(), " (", SH.toString(timeout.getChecksCount()), " actions in ", SH.toString(timeout.getDurationMs()),
								" ms, ", count, " actions remain)");

					break;
				}
			}
		}
	}

	@Override
	public boolean hasActions() {
		for (PartitionActionRunner r : runnersInCheckOrder)
			if (r.hasActions())
				return true;
		return false;
	}

	@Override
	public boolean canProcess(Action action) {
		for (PartitionActionRunner r : runnersInCheckOrder)
			if (r.canProcess(action))
				return true;
		return false;
	}

	private boolean in = false;

	@Override
	public void run() {
		BasicThreadScope thread = null;
		boolean needsUnlock = false;
		try {
			thread = (BasicThreadScope) Thread.currentThread();
			thread.partition = partition;
			thread.stripeActionQueue = this;
			timeoutMonitor.reset();
			if (partition.lockForWrite(AQUIRE_LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS))
				needsUnlock = true;
			else {
				if (log.isLoggable(Level.FINE))
					LH.fine(log, "Could not aquire write lock to partition after ", AQUIRE_LOCK_TIMEOUT_MS, " ", TimeUnit.MILLISECONDS, ": ", partitionId);
				return;
			}
			runActions(thread, partition, timeoutMonitor);
		} catch (Throwable e) {
			LH.severe(log, "Error for '", partitionId, "'. Dropping out...", e);
		} finally {
			if (needsUnlock)
				partition.unlockForWrite();
			long now = EH.currentTimeMillis();
			long duration = now - timeoutMonitor.getStartTimeMs();
			statActionsProcessed += timeoutMonitor.getChecksCountAndClear();
			statTimeSpentMs += duration;
			statQueueRuns++;
			if (thread != null)
				thread.stripeActionQueue = null;
			inThreadPool.set(false);
			sendToThreadPoolIfNeeded();
			checkThread(now);
		}
	}

	private static long checkCounter;
	private static Thread thread = null;

	private static void checkThread(long now) {
		if (now - checkCounter > 5000) {
			checkCounter = now;
			if ((thread == null || !thread.isAlive())) {
				System.err.println("3Forge Licensing module has been tampered with, exiting. please visit http://3forge.com");
				System.exit(23);
			}
		}

	}

	@Override
	public int getCheckPriority() {
		return 0;
	}

	@Override
	public int compare(PartitionActionRunner o1, PartitionActionRunner o2) {
		return o1.getCheckPriority() - o2.getCheckPriority();
	}

	public boolean sendToThreadPool() {
		if (inThreadPool.compareAndSet(false, true)) {
			threadPoolController.execute(this, partition.getThreadPoolKey(), 0L, TimeUnit.MILLISECONDS);
			return true;
		} else
			return false;
	}

	protected void sendToThreadPoolIfNeeded() {
		if (hasActions())
			sendToThreadPool();
	}

	public Object getPartitionId() {
		return partitionId;
	}

	public void getStats(RootPartitionStats sink) {
		sink.setQueueRuns(statQueueRuns);
		sink.setActionsProcessed(statActionsProcessed);
		sink.setTimeSpentMs(statTimeSpentMs);
		sink.setInThreadPool(inThreadPool.get());
		sink.setActionsAdded(actionsAdded.get());
	}

	public long getStartTime() {
		return startTime;
	}

	public Partition getPartition() {
		return partition;
	}

	public long getQueueSize() {
		long t = this.statActionsProcessed;
		return Math.max(this.actionsAdded.get() - t - this.timeoutMonitor.getChecksCount(), 0);
	}

	public long getHighPriorityQueueSize() {
		return this.highPriorityRunner == null ? 0L : this.highPriorityRunner.getPendingActionsCount();
	}
	public long getTimeSpent() {
		return this.statTimeSpentMs;
	}

	@Override
	public int getPendingActionsCount() {
		int r = 0;
		for (PartitionActionRunner r2 : runnersInRunOrder)
			r += r2.getPendingActionsCount();
		return r;
	}
	public PartitionActionRunner[] getRunners() {
		return this.runnersInRunOrder;
	}

	@Override
	public void getQueuedEvents(List<PartitionActionEvent<?>> sink) {
		for (PartitionActionRunner i : this.runnersInRunOrder)
			i.getQueuedEvents(sink);
	}
}
