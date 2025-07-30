/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

import java.util.Date;
import java.util.Map;

import com.f1.base.Action;
import com.f1.base.Clock;
import com.f1.base.DateNanos;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.BundledTextFormatter;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.PropertyController;

/**
 * common set of (expanding) tools useful for interacting with the {@link Container}. Note, it has all of the functionality of the {@link PropertyController} and is itself a
 * {@link ContainerScope}
 */
public interface ContainerTools extends ContainerScope, PropertyController {

	/**
	 * dispatch an {@link Action} to the {@link Port}'s {@link Processor} (see {@link Port#getProcessor()}) using the container's
	 * {@link DispatchController#dispatch(Processor, Action, Object, ThreadScope)} method. The partition's queue will be determined by visiting the {@link Processor}'s
	 * {@link PartitionResolver}
	 * 
	 * @param <A>
	 *            type of action to send
	 * @param inputPort
	 *            the port of the processor to dispatch to
	 * @param action
	 *            the action to dispatch
	 * @see DispatchController
	 */
	<A extends Action> void dispatch(InputPort<? extends A> inputPort, A action);

	/**
	 * dispatch an {@link Action} to the {@link Port}'s {@link Processor} (see {@link Port#getProcessor()}) using the container's
	 * {@link DispatchController#dispatch(Processor, Action, Object, ThreadScope)} method. The action will be placed on the queue with the specified partitionid.
	 * 
	 * @param <A>
	 *            type of action to send
	 * @param inputPort
	 *            the port of the processor to dispatch to
	 * @param action
	 *            the action to dispatch
	 * @see DispatchController
	 */
	<A extends Action> void dispatch(InputPort<? extends A> inputPort, A action, Object partitionId);

	/**
	 * wrap the suppied {@link Action} in a {@link RequestAction} and dispatch to the supplied {@link Port}'s {@link Processor} (see {@link Port#getProcessor()} ) using the
	 * container's {@link DispatchController#dispatch(Processor, Action, Object, ThreadScope)} method. The partitions' queue will be determined by visiting the {@link Processor}'s
	 * {@link PartitionResolver}
	 * 
	 * @param <A>
	 *            type of action to send
	 * @param inputPort
	 *            the port of the processor to dispatch to. Not the processor should be configured to receive {@link RequestAction}'s (likely by extending the
	 *            {@link BasicRequestProcessor})
	 * @param a
	 *            the action to wrap in a {@link RequestAction} and send
	 * @return a future which will hold the {@link ActionResponse} associated w/ the supplied request
	 * @see DispatchController
	 */
	<A extends Action> ResultActionFuture request(InputPort<? extends A> outputPort, A a);

	/**
	 * wrap the suppied {@link Action} in a {@link RequestAction} and dispatch to the supplied {@link Port}'s {@link Processor} (see {@link Port#getProcessor()} ) using the
	 * container's {@link DispatchController#dispatch(Processor, Action, Object, ThreadScope)} method. The partitions' queue will be determined by visiting the {@link Processor}'s
	 * {@link PartitionResolver}. After the request is processed by said processor, the response will be send to the supplied resultPort's {@link Processor}
	 * 
	 * @param <A>
	 *            type of action to send
	 * @param inputPort
	 *            the port of the processor to dispatch to. Note, the processor should be configured to receive {@link RequestAction}s (likely by extending the
	 *            {@link BasicRequestProcessor})
	 * @param a
	 *            the action to wrap in a {@link RequestAction} and send
	 * @param resultPort
	 *            the port of the processor to dispatch the response to. Note, the processor should be configured to receive {@link ResultAction}s
	 * 
	 * @return a future which will hold the {@link ActionResponse} associated w/ the supplied request
	 * @see DispatchController
	 */
	<A extends Action> void request(InputPort<? extends A> inputPort, A a, OutputPort<ResultMessage<?>> resultPort);

	/**
	 * wrap the suppied {@link Action} in a {@link RequestAction} and dispatch to the supplied {@link Port}'s {@link Processor} (see {@link Port#getProcessor()} ) using the
	 * container's {@link DispatchController#dispatch(Processor, Action, Object, ThreadScope)} method. The partitions' queue will be determined by the supplied partitionId
	 * 
	 * @param <A>
	 *            type of action to send
	 * @param inputPort
	 *            the port of the processor to dispatch to. Not the processor should be configured to receive {@link RequestAction}'s (likely by extending the
	 *            {@link BasicRequestProcessor})
	 * @param a
	 *            the action to wrap in a {@link RequestAction} and send
	 * @param partitionId
	 *            used to determine the partition this event will be dispatched to
	 * @return a future which will hold the {@link ActionResponse} associated w/ the supplied request
	 * @see DispatchController
	 */
	<A extends Action> ResultActionFuture request(InputPort<? extends A> inputPort, A a, Object partitionId);

	/**
	 * wrap the suppied {@link Action} in a {@link RequestAction} and dispatch to the supplied {@link Port}'s {@link Processor} (see {@link Port#getProcessor()} ) using the
	 * container's {@link DispatchController#dispatch(Processor, Action, Object, ThreadScope)} method. The partitions' queue will be determined by the supplied partitionId. After
	 * the request is processed by said processor, the response will be send to the supplied resultPort's {@link Processor}
	 * 
	 * @param <A>
	 *            type of action to send
	 * @param inputPort
	 *            the port of the processor to dispatch to. Note, the processor should be configured to receive {@link RequestAction}s (likely by extending the
	 *            {@link BasicRequestProcessor})
	 * @param a
	 *            the action to wrap in a {@link RequestAction} and send
	 * @param resultPort
	 *            the port of the processor to dispatch the response to. Note, the processor should be configured to receive {@link ResultAction}s
	 * @param partitionId
	 *            used to determine the partition this event will be dispatched to
	 * 
	 * @return a future which will hold the {@link ActionResponse} associated w/ the supplied request
	 * @see DispatchController
	 */
	<A extends Action> void request(InputPort<? extends A> inputPort, A a, Object partitionId, OutputPort<ResultMessage<?>> resultPort);

	/**
	 * return the current time, as prescribed by the container's {@link Clock#getNow()} method
	 * 
	 * @return time in milliseconds since epoch. see {@link System#currentTimeMillis()}
	 */
	long getNow();

	/**
	 * return the current time, as prescribed by the container's {@link Clock#getNowNano()} method
	 * 
	 * @return time in milliseconds since epoch. see {@link System#currentTimeMillis()}
	 */
	long getNowNano();

	/**
	 * Convenience method which Uses the default locale formatter available via the container service's {@link ContainerServices#getLocaleFormatter()}.
	 * {@link LocaleFormatter#getBundledTextFormatter()}
	 * <P>
	 * see {@link BundledTextFormatter#formatBundledText(String, Object...)} for usage
	 * 
	 * @param bundleTextKey
	 *            key to look up in bundled text files
	 * @param arguments
	 *            arguments to pass into bundled text formatter
	 * @return text, never null
	 */
	String formatBundledText(String bundleTextKey, Object... arguments);

	String formatBundledTextFromMap(String bundleTextKey, Map<String, Object> arguments, int options);

	String formatBundledTextFromMap(String bundleTextKey, Map<String, Object> arguments);

	DateNanos getNowNanoDate();

	Date getNowDate();

	String getUidString(String namespace);

	long getUidLong(String namespace);

	ThreadScope getThreadScope();

	String generateErrorTicket();

}
