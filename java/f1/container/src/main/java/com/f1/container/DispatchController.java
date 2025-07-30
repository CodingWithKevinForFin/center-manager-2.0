/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

import java.util.Collection;
import java.util.logging.Level;

import com.f1.base.Action;
import com.f1.container.impl.BasicThreadScope;
import com.f1.container.impl.dispatching.IterableActionProcessor;
import com.f1.container.impl.dispatching.RootPartitionActionRunner;

/**
 * The Dispatch Controller is the 'heart' of the f1 container in that it manages the association of {@link Action}s with {@link Partition}s for being processed by {@link Processor}
 * s. Like all {@link ContainerScope} objects this is at the container scope level. There is one dispatch controller per container which can be accessed by calling
 * {@link Container#getDispatchController()}. There are two 'basic' ways of processing actions:<BR>
 * <B>dispatching</B> - processing in another thread in a non-blocking fashion<BR>
 * <B>forwarding</B> - processing in the calling thread in a blocking fashion<BR>
 */
public interface DispatchController extends ContainerScope {

	/**
	 * Call {@link Processor#processAction(Action, State, ThreadScope)} on supplied processor in another thread after a specied delay (in a non-blocking fashion) such that this
	 * call will return immediately.
	 * 
	 * @param optionalSourcePort
	 *            the port that was used to call this method (may be null, otherwise only used for diagnostic purposes)
	 * @param processor
	 *            the processor to call {@link Processor#processAction(Action, State, ThreadScope)} on
	 * @param action
	 *            the action to pass into {@link Processor#processAction(Action, State, ThreadScope)}
	 * @param partitionid
	 *            the id of the partition this action should be associated with, as typically determined by {@link PartitionResolver#getPartitionId(Action)}
	 * @param delayMs
	 *            the amount of time to wait(in milliseconds) before dispatching. Zero indicates it should be dispatched now
	 * @param threadScope
	 *            the threadScope to pass into {@link Processor#processAction(Action, State, ThreadScope)}
	 * @return
	 */
	Partition dispatch(Port<?> optionalSourcePort, Processor<?, ?> processor, Action action, Object partitionId, long delayMs, ThreadScope threadScope);

	/**
	 * Call {@link Processor#processAction(Action, State, ThreadScope)} on supplied processor in another thread (in a non-blocking fashion) such that this call will return
	 * immediately.
	 * 
	 * @param optionalSourcePort
	 *            the port that was used to call this method (may be null, otherwise only used for diagnostic purposes)
	 * @param processor
	 *            the processor to call {@link Processor#processAction(Action, State, ThreadScope)} on
	 * @param action
	 *            the action to pass into {@link Processor#processAction(Action, State, ThreadScope)}
	 * @param partitionid
	 *            the id of the partition this action should be associated with, as typically determined by {@link PartitionResolver#getPartitionId(Action)}
	 * @param threadScope
	 *            the threadScope to pass into {@link Processor#processAction(Action, State, ThreadScope)}
	 */
	Partition dispatch(Port<?> optionalSourcePort, Processor<?, ?> processor, Action action, Object partitionId, ThreadScope threadScope);

	/**
	 * Call {@link Processor#processAction(Action, State, ThreadScope)} on supplied processor in this thread (in a blocking fashion)
	 * 
	 * @param optionalSourcePort
	 *            the port that the action was sent on (may be null, otherwise only used for diagnostic purposes)
	 * @param processor
	 *            the processor to call {@link Processor#processAction(Action, State, ThreadScope)} on
	 * @param action
	 *            the action to pass into {@link Processor#processAction(Action, State, ThreadScope)}
	 * @param scope
	 *            the threadScope to pass into {@link Processor#processAction(Action, State, ThreadScope)}
	 */
	void forward(Port<?> optionalSourcePort, Processor<?, ?> processor, Action action, ThreadScope scope);

	/**
	 * Used to notify this dispatch controller that a new partition has been created. This is necessary because each partition has an associated queue(for holding actions to be
	 * processed on said partition) which the DispatchController is responsible for instantiating.
	 * 
	 * @param partition
	 *            the partition that was just added.
	 * @return true iff the partition didn't exist already
	 */
	boolean onPartitionAdded(Partition partition);
	boolean onPartitionRemoved(Partition partition);

	void handleThrowableFromProcessor(Processor<?, ?> processor, Action action, State state, ThreadScope t, Throwable ex);

	/**
	 * bind the request to the result. This includes the following steps: <BR>
	 * (1) association via {@link ResultAction#setAction(Action)}<BR>
	 * (2)If {@link RequestAction#getResultProcessor()} is not null, then send the result to said processor<BR>
	 * (3)If {@link RequestAction#getFuture()} is not null, then provide the result to said future via {@link ResultActionFuture#provideResult(ResultMessage)}
	 * 
	 * @param optionalSourcePort
	 *            the port that the request was sent on (may be null, otherwise only used for diagnostic purposes)
	 * @param request
	 *            the request in the request / result, may not be null
	 * @param result
	 *            the result in the request / result, may not be null
	 * @param threadScope
	 *            optional {@link ThreadScope} of the current thread.
	 */
	void reply(Port<?> optionalSourcePort, RequestMessage<?> request, ResultMessage<?> result, ThreadScope threadScope);

	/**
	 * convenience method for creating states. see {@link PartitionController#createState(Partition, Action, Processor)} for details
	 * 
	 * @return the newly created state
	 */
	<A extends Action, S extends State> State createState(Partition partition, Processor<? super A, S> processor, A action);

	/**
	 * process the supplied action on the given processor with the guarantee (to the degree possible) that nothing will be thrown. Obviously, a major issue such as an
	 * {@link OutOfMemoryError} may be thrown
	 * 
	 * @param optionalSourcePort
	 *            the port that was used to call this method (may be null, otherwise only used for diagnostic purposes)
	 * @param processor
	 *            the processor to call {@link Processor#processAction(Action, State, ThreadScope)} on
	 * @param action
	 *            the action to pass into {@link Processor#processAction(Action, State, ThreadScope)}
	 * @param state
	 *            the state to pass into {@link Processor#processAction(Action, State, ThreadScope)}
	 * @param threadScope
	 *            the threadScope to pass into {@link Processor#processAction(Action, State, ThreadScope)}
	 */
	<A extends Action, S extends State> void safelyProcess(Port<?> optionalSourcePort, Processor<? super A, ? super S> processor, A action, S state, ThreadScope threadScope,
			boolean isDispatch);

	/**
	 * @return true if there aren't any actions being currently processed or in queue to be processed.
	 */
	boolean isIdle();

	/**
	 * 
	 * @return null if not performance logging, otherwise the level at which performance logging should be logged at
	 */
	Level getPerformanceLoggingLevel();

	public void addProcessActionListener(ProcessActionListener listener);
	public ProcessActionListener[] getProcessActionListeners();

	void safelyProcess(IterableActionProcessor iterator, MultiProcessor processor, State state, BasicThreadScope thread);

	Collection<RootPartitionActionRunner> getRootParititionRunners();

	public RootPartitionActionRunner getRootPartitionRunner(String partitionId);

	@Deprecated
	void setDefaultFutureTimeoutMs(long i);

	long getActionsProcessedCount();
	long getActionsInQueueCount();
	long getExceptionsCount();

	long getQueueTimeoutMs();
	int getQueueTimeoutCheckFrequency();

	void setQueueTimeoutMs(long timeout);

	void setQueueTimeoutCheckFrequency(int frequency);
}
