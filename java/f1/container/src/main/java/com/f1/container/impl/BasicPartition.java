/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.f1.container.Container;
import com.f1.container.Partition;
import com.f1.container.ProcessActionListener;
import com.f1.container.State;
import com.f1.container.exceptions.ContainerException;
import com.f1.container.exceptions.ContainerInterruptedException;
import com.f1.container.impl.dispatching.IterableActionProcessor;
import com.f1.container.impl.dispatching.RootPartitionActionRunner;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.CopyOnWriteHashMap;

public class BasicPartition implements Partition {

	transient private final Container container;

	private final long startTime;
	private Object partitionId;
	private ConcurrentMap<Class, State> states = new CopyOnWriteHashMap<Class, State>();
	private Object threadPoolKey;

	final private int options;

	public BasicPartition(Container container, Object partitionId, Object threadPoolKey, long startTime, int options) {
		this.partitionId = partitionId;
		if (partitionId == null)
			throw new NullPointerException("partitionId");
		this.threadPoolKey = threadPoolKey;
		this.startTime = startTime;
		this.container = container;
		this.options = options;
	}

	@Override
	public Object getPartitionId() {
		return partitionId;
	}

	@Override
	public State getState(Object id) {
		return states.get(id);
	}

	@Override
	public Set<Class> getStateTypes() {
		return states.keySet();
	}

	@Override
	public void putState(State state) {
		if (state.getPeer() == null)
			state.setPeer(new BasicStatePeer());
		if (state.getType() == null)
			throw new NullPointerException("State type can not be null: " + state);
		if (state.getPartition() == null)
			state.setPartition(this);
		else if (state.getPartition() != this)
			throw new ContainerException("invalid id for partion '" + partitionId + "': " + state.getPartition());
		CH.putOrThrow(states, state.getType(), state);
	}

	@Override
	public String toString() {
		return "partition: " + partitionId;
	}

	@Override
	public Object getThreadPoolKey() {
		return threadPoolKey;
	}

	@Override
	public long getStartTimeMs() {
		return startTime;
	}

	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private Lock writeLock = lock.writeLock();
	private Lock readLock = lock.readLock();
	final private long containerScopeUid = ContainerHelper.nextContainerScopeUid();

	@Override
	public boolean lockForWrite(long time, TimeUnit unit) {
		try {
			return lock.writeLock().tryLock(time, unit);
		} catch (InterruptedException e) {
			throw new ContainerInterruptedException("failed to aquire read lock", e, null).set("delay", time).set("timeunits", unit);
		}
	}

	@Override
	public boolean lockForRead(long time, TimeUnit unit) {
		try {
			return lock.readLock().tryLock(time, unit);
		} catch (InterruptedException e) {
			throw new ContainerInterruptedException("failed to aquire read lock", e, null).set("delay", time).set("timeunits", unit);
		}
	}

	@Override
	public void unlockForWrite() {
		writeLock.unlock();
	}

	@Override
	public void unlockForRead() {
		readLock.unlock();
	}

	@Override
	public boolean isWriteLocked() {
		return lock.isWriteLocked();
	}
	@Override
	public boolean isWriteLockedByCurrentThread() {
		return lock.isWriteLockedByCurrentThread();
	}

	@Override
	public long getContainerScopeUid() {
		return containerScopeUid;
	}

	private ProcessActionListener[] processListeners = ContainerHelper.EMPTY_PROCESS_EVENT_LISTENER_ARRAY;

	private IterableActionProcessor iterableActionProcessor;

	private RootPartitionActionRunner rpar;

	private long lockForWriteDefaultTimeoutMillis = RootPartitionActionRunner.AQUIRE_LOCK_TIMEOUT_MS;

	@Override
	public void addProcessActionListener(ProcessActionListener listener) {
		processListeners = AH.insert(processListeners, processListeners.length, listener);
	}

	@Override
	public ProcessActionListener[] getProcessActionListeners() {
		return processListeners;
	}

	@Override
	public Container getContainer() {
		return container;
	}

	@Override
	public int getOptions() {
		return options;
	}

	public IterableActionProcessor borrowMultiProcessorIterator() {
		if (this.iterableActionProcessor == null)
			this.iterableActionProcessor = new IterableActionProcessor();
		IterableActionProcessor r = this.iterableActionProcessor;
		if (r == null)
			return new IterableActionProcessor();
		this.iterableActionProcessor = null;
		return r;
	}

	public void returnMultiProcessorIterator(IterableActionProcessor iterator) {
		iterator.clear();
		this.iterableActionProcessor = iterator;
	}

	@Override
	public long getQueueSize() {
		return rpar == null ? 0 : rpar.getQueueSize();
	}

	public void setRootPartitionActionRunner(RootPartitionActionRunner rootPartitionActionRunner) {
		this.rpar = rootPartitionActionRunner;
	}

	@Override
	public long getLockForWriteDefaultTimeoutMillis() {
		return lockForWriteDefaultTimeoutMillis;
	}
	@Override
	public void setLockForWriteDefaultTimeoutMillis(long t) {
		lockForWriteDefaultTimeoutMillis = t;
	}

}
