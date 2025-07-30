/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * A Partition is a container concept which can be though of as a "thread barrier". See the {@link PartitionController} for details on how the container maintains a set of active
 * partitions. As actions are submitted to the container, they are inspected using a {@link PartitionResolver} to determine which partition they should be processed on. In the
 * general case, the action is then put onto a queue (which is associated with that partition) for processing in turn.
 * <P>
 * <B>States</B> - A partition can contain a stateful collection of data, each known as {@link State}s.
 * <P>
 * <B>Thread Pools</B> - * In a typical setup, there will only be one thread pool, but for fine level control each partition may decide which thread pool it's actions should be
 * processed on. See {@link #getThreadPoolKey()} and {@link ThreadPoolController} for details.
 * <P>
 * NOTE: Methods defined on this interface must be thread safe, meaning that many threads may call into this interface simultaneously. NOTE: one may extend this class and add
 * additional methods, such as getters/setters and access it in a {@link Processor#processAction(com.f1.base.Action, State, ThreadScope)} method via {@link State#getPartition()}.
 * Because only one processor may access a particular partition at a time, the additional methods do not need to be thread safe.
 * 
 */
public interface Partition extends ContainerUid {

	/**
	 * @return the id uniquely identifying this partition.<BR>
	 *         NOTE: must be thread safe
	 */
	Object getPartitionId();

	/**
	 * the state associated with the supplied type <BR>
	 * NOTE: must be a thread safe call
	 * 
	 * @param stateType
	 * @return the state , or null
	 */
	State getState(Object stateType);

	/**
	 * add a state to this partition. <BR>
	 * NOTE: must be thread safe
	 * 
	 * @param state
	 * @throws RuntimeException
	 *             if {@link State#getType()} returns null, or another state with the said type is already registered
	 */
	void putState(State state);

	/**
	 * @return a collection of all state types registered with this partition <BR>
	 *         NOTE: must be thread safe
	 */
	Set<Class> getStateTypes();

	/**
	 * the key representing the thread pool this partition is associated with. see {@link ThreadPoolController#execute(Runnable, Object, long, java.util.concurrent.TimeUnit)}
	 * 
	 * @return key of associated thread pool, never null<BR>
	 *         NOTE: must be thread safe
	 */
	Object getThreadPoolKey();

	/**
	 * 
	 * @return the time that the partition was created (in milliseconds) <BR>
	 *         NOTE: must be thread safe
	 */
	long getStartTimeMs();

	public long getLockForWriteDefaultTimeoutMillis();
	public void setLockForWriteDefaultTimeoutMillis(long t);
	public boolean lockForWrite(long timeOut, TimeUnit timeUnit);
	public boolean lockForRead(long timeOut, TimeUnit timeUnit);
	public void unlockForRead();
	public void unlockForWrite();
	public boolean isWriteLocked();
	public boolean isWriteLockedByCurrentThread();

	public void addProcessActionListener(ProcessActionListener listener);
	public ProcessActionListener[] getProcessActionListeners();

	public Container getContainer();

	int OPTION_SUPPORT_CONFLATION = 1;
	int OPTION_SUPPORT_HIGH_PRIORITY = 2;
	int OPTION_SUPPORT_LOW_PRIORITY = 4;
	Object NO_PARTITION = null;

	int getOptions();

	/**
	 * @return the approximate queue size..
	 */
	long getQueueSize();

}
