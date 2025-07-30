/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

import com.f1.base.Action;

/**
 * A port allows for decoupling of processors, and more generally facilitates
 * decoupling from those wishing to produce {@link Action}s with those wishing
 * to consume actions. Without ports, producers of actions would need to have
 * explicit knowledge of there consumers so that they could send the actions to
 * them. <BR>
 * Using the port concept, a producer can declare a port which it will send
 * actions on (known as an 'output' port) and the consumer can, in turn, declare
 * a port which it will receive actions on (known as an 'input' port). Then,
 * some third party with knowledge of both the producer and consumer can
 * 'connect' the output port to the input port (see
 * {@link #wire(RequestPort, boolean)}). Note, that all consumers and producers
 * of actions should implement the {@link Connectable} interface. It's worth
 * mentioning that the {@link Processor} and {@link Suite} already implement the
 * {@link Connectable} interface.
 * <P>
 * 
 * Ports either act in an output fashion(meaning you send actions out on them)
 * or an input fashion(meaning you receive actions on them). The act of
 * 'connecting' to ports is the declaration of a unidirectional flow of actions
 * from the left to right. In order to facilitate decoupling to the maximum
 * degree, ports may have n-number of indirections. The following rules apply
 * for 'connecting' ports(from left to right):<BR>
 * 1. an Output Port can be 'connected to' another output port<BR>
 * 2. an Output port can be 'connected to' an input port<BR>
 * 3. an input port can be 'connected to' another input port<BR>
 * 4. an input port can be 'connected to' a processer<BR>
 * 5. any number of ports can be 'connected to' to the same port (while
 * maintaining the other rules)<BR>
 * For example: <BR>
 * &nbsp;outputPort1a -> outputPort2 (rule 1)<BR>
 * &nbsp;outputPort1b -> outputPort2 (rule 5)<BR>
 * &nbsp;outputPort2 -> inputPort1 (rule 2)<BR>
 * &nbsp;inputPort1 -> inputPort2 (rule 3)<BR>
 * &nbsp;inputPort2 -> processor (rule 4) <BR>
 * 
 * 
 * @param <A>
 */
public interface RequestOutputPort<A extends Action, RES extends Action> extends OutputPort<RequestMessage<A>> {

	/**
	 * Convenience method which wraps the action in a {@link RequestAction} and
	 * then sends it similar to {@link #send(Action, ThreadScope)}
	 * 
	 * @param a
	 *            the action to send
	 * @param threadScope
	 *            optionally supplied {@link ThreadScope} (if you conveniently
	 *            have access to it, otherwise may be null)
	 * @return a future which will contain the response (at some later time if
	 *         this port is configured for dispatching)
	 * @see ResultActionFuture
	 */
	ResultActionFuture<RES> requestWithFuture(A a, ThreadScope threadScope);

	/**
	 * Convenience method which wraps the action in a {@link RequestAction} and
	 * then sends it similar to {@link #send(Action, ThreadScope)}. the response
	 * will be sent to the supplied resultPort.
	 * 
	 * @param a
	 *            the action to send
	 * @param threadScope
	 *            optionally supplied {@link ThreadScope} (if you conveniently
	 *            have access to it, otherwise may be null)
	 * @param resultPort
	 *            the port that the result of this request will be sent back on.
	 */
	void request(A a, ThreadScope ts);

	/**
	 * Convenience method which wraps the action in a {@link RequestAction} and
	 * then sends it similar to {@link #send(Action, Object,ThreadScope)}
	 * 
	 * @param a
	 *            the action to send
	 * @param threadScope
	 *            optionally supplied {@link ThreadScope} (if you conveniently
	 *            have access to it, otherwise may be null)
	 * @return a future which will contain the response (at some later time if
	 *         this port is configured for dispatching)
	 * @see ResultActionFuture
	 */
	ResultActionFuture<RES> requestWithFuture(A a, Object partitionId, ThreadScope threadScope);

	/**
	 * Convenience method which wraps the action in a {@link RequestAction} and
	 * then sends it similar to {@link #send(Action, Object,ThreadScope)}
	 * 
	 * @param a
	 *            the action to send
	 * @param threadScope
	 *            optionally supplied {@link ThreadScope} (if you conveniently
	 *            have access to it, otherwise may be null)
	 * @param correlationId
	 *            the correlation Id to put on the request
	 * @return a future which will contain the response (at some later time if
	 *         this port is configured for dispatching)
	 * @see ResultActionFuture
	 */
	ResultActionFuture<RES> requestWithFuture(A a, Object partitionId, Object correlationId, ThreadScope threadScope);

	/**
	 * Convenience method which wraps the action in a {@link RequestAction} and
	 * then sends it similar to {@link #send(Action,Object, ThreadScope)}. the
	 * response will be sent to the supplied resultPort.
	 * 
	 * @param a
	 *            the action to send
	 * @param partitionId
	 *            the parittion that the request is sent on
	 * @param threadScope
	 *            optionally supplied {@link ThreadScope} (if you conveniently
	 *            have access to it, otherwise may be null)
	 */
	void request(A a, Object partitionId, ThreadScope threadScope);

	/**
	 * Convenience method which wraps the action in a {@link RequestAction} and
	 * then sends it similar to {@link #send(Action,Object, ThreadScope)}. the
	 * response will be sent to the supplied resultPort.
	 * 
	 * @param a
	 *            the action to send
	 * @param partitionId
	 *            the partition that the request is sent on
	 * @param correlationId
	 *            the correlation Id to put on the request
	 * @param threadScope
	 *            optionally supplied {@link ThreadScope} (if you conveniently
	 *            have access to it, otherwise may be null)
	 */
	void request(A a, Object partitionId, Object correlationId, ThreadScope threadScope);

	/**
	 * @return the type of actions this port expects to receive in the
	 *         {@link RequestMessage#getAction()}.
	 */
	Class<A> getRequestActionType();

	/**
	 * @return the type of actions this port expects to return in the
	 *         {@link ResultMessage#getAction()}.
	 */
	Class<RES> getResponseActionType();

	RequestOutputPort<A, RES> setName(String name);

	OutputPort<ResultMessage<RES>> getResponsePort();
}
