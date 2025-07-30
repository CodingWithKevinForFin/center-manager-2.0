package com.f1.container.inspect;

import com.f1.base.Action;
import com.f1.container.DispatchController;
import com.f1.container.Partition;
import com.f1.container.PartitionResolver;
import com.f1.container.Port;
import com.f1.container.Processor;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.State;
import com.f1.container.StateGenerator;
import com.f1.container.ThreadScope;
import com.f1.container.wrapper.InspectingDispatchController;

/**
 * This interface should be implemented by anyone interested in tracking actions and partitions as they take place throughout the container. The primary intent (while not limited)
 * is to support integration testing and high level debugging tools. Certain actions may result in multiple call backs being fired, for example a dispatch would result in a
 * onDispatch(...) and onProcess(...) call back. These call backs are in-proc blocking calls.<BR>
 * Note: dispatching or creating events from within these call backs will cause the appropriate call back to be re-fired, so recursive loops should be coded for<BR>
 * Note: This is used in combination with the {@link InspectingDispatchController}.
 * 
 */
public interface DispatchInspector {

	/**
	 * fired immediately before a message is dispatched(the message will be crossing threads) into the container (called in the same thread as the dispatch method)
	 * 
	 * @param dc
	 *            the {@link DispatchController} this inspector was added to
	 * @param p
	 *            the target {@link Processor} that will the action will be processed by (see {@link Processor#processAction(Action, State, ThreadScope)}
	 * @param a
	 *            the {@link Action} to be dispatched
	 * @param t
	 *            the {@link ThreadScope} of the thread calling the requesting the dispatch(may be null)
	 * @param partitionId
	 *            will partition for the action to execute on
	 * @param delayMs
	 *            delay before message will be dispatched. 0 = immediate.
	 */
	void onDispatch(InspectingDispatchController dc, Port<?> sourcePort, Processor<?, ?> p, Action a, ThreadScope t, Partition partition, long delayMs);

	/**
	 * fired immediately before a message is forwarded(the message is not crossing threads) (called in the same thread as the dispatch method)
	 * 
	 * @param dc
	 *            the {@link DispatchController} this inspector was added to
	 * @param p
	 *            the target {@link Processor} that will the action will be processed by (see {@link Processor#processAction(Action, State, ThreadScope)}
	 * @param a
	 *            the {@link Action} to be dispatched
	 * @param t
	 *            the {@link ThreadScope} of the thread calling the forward
	 */
	void onForward(InspectingDispatchController dc, Port<?> sourcePort, Processor<?, ?> p, Action a, ThreadScope t);

	/**
	 * fired after a 'controlled' {@link Throwable} is caught by the {@link DispatchController}. This includes throwables thrown by instances of a {@link Processor},
	 * {@link StateGenerator} or {@link PartitionResolver}. In addition, some internal exceptions may also be inadvertently caught as well and should be handle for.
	 * 
	 * @param dc
	 *            the {@link DispatchController} this inspector was added to
	 * @param p
	 *            the {@link Processor} that invoked the {@link Throwable} (may be null)
	 * @param a
	 *            the {@link Action} that participated in creating the {@link Throwable}(may be null)
	 * @param t
	 *            the {@link ThreadScope} associated with the thrown(may be null)
	 * @param state
	 *            the {@link State} that participated in creating the {@link Throwable}(may be null)
	 * @param ex
	 *            the exception that was thrown
	 */
	void onThrown(InspectingDispatchController dc, Processor<?, ?> p, Action a, ThreadScope t, State state, Throwable ex);

	/**
	 * fired immediately before a {@link DispatchController#reply(RequestMessage, ResultMessage, ThreadScope)} is called.
	 * 
	 * @param dc
	 *            the {@link DispatchController} this inspector was added to
	 * @param request
	 *            the request message portion of the reply.
	 * @param result
	 *            the result message portion of the reply.
	 * @param t
	 *            the {@link ThreadScope} of the thread calling reply
	 */
	void onReply(InspectingDispatchController dc, Port<?> sourcePort, RequestMessage<?> request, ResultMessage<?> result, ThreadScope t);

	/**
	 * fired immediately before a {@link Partition} is added.
	 * 
	 * @param dc
	 *            the {@link DispatchController} this inspector was added to
	 * @param partition
	 *            partition added
	 */
	void onPartitionAdded(InspectingDispatchController dc, Partition partition);

	/**
	 * fired immediately before {@link Processor#processAction(Action, State, ThreadScope)} is about to be invoked. This includes forwards, replies and dispatches
	 * 
	 * @param dc
	 *            the {@link DispatchController} this inspector was added to
	 * @param p
	 *            the {@link Processor} that will have processAction(...) invoked
	 * @param a
	 *            the {@link Action} to be passed into processAction(...)
	 * @param s
	 *            the {@link State} to be passed into processAction(...)
	 * @param t
	 *            the {@link ThreadScope} to be passed into processAction(...)
	 */
	void onProcess(InspectingDispatchController dc, Port<?> sourcePort, Processor<?, ?> p, Action a, State s, ThreadScope t);

	/**
	 * Fired during the start phase
	 * 
	 * @param dc
	 *            the {@link DispatchController} this inspector was added to
	 */
	void onStart(InspectingDispatchController dc);

	/**
	 * Fired during the stop phase
	 * 
	 * @param dc
	 *            the {@link DispatchController} this inspector was added to
	 */
	void onStop(InspectingDispatchController dc);

}
