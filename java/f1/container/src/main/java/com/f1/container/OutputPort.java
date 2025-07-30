/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

import java.util.concurrent.TimeUnit;

import com.f1.base.Action;

/**
 * A port allows for decoupling of processors, and more generally facilitates decoupling from those wishing to produce {@link Action}s with those wishing to consume actions.
 * Without ports, producers of actions would need to have explicit knowledge of there consumers so that they could send the actions to them. <BR>
 * Using the port concept, a producer can declare a port which it will send actions on (known as an 'output' port) and the consumer can, in turn, declare a port which it will
 * receive actions on (known as an 'input' port). Then, some third party with knowledge of both the producer and consumer can 'connect' the output port to the input port (see
 * {@link #wire(OutputPort, boolean)}). Note, that all consumers and producers of actions should implement the {@link Connectable} interface. It's worth mentioning that the
 * {@link Processor} and {@link Suite} already implement the {@link Connectable} interface.
 * <P>
 * 
 * Ports either act in an output fashion(meaning you send actions out on them) or an input fashion(meaning you receive actions on them). The act of 'connecting' to ports is the
 * declaration of a unidirectional flow of actions from the left to right. In order to facilitate decoupling to the maximum degree, ports may have n-number of indirections. The
 * following rules apply for 'connecting' ports(from left to right):<BR>
 * 1. an Output Port can be 'connected to' another output port<BR>
 * 2. an Output port can be 'connected to' an input port<BR>
 * 3. an input port can be 'connected to' another input port<BR>
 * 4. an input port can be 'connected to' a processor<BR>
 * 5. any number of ports can be 'connected to' to the same port (while maintaining the other rules)<BR>
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
public interface OutputPort<A extends Action> extends Port<A> {

	/**
	 * determines whether this is an input or output port.
	 * 
	 * @return false = output
	 */
	boolean isInput();

	/**
	 * connects this output port to some input port. Please note, that this port must not already be connected to another port... If you wish to alter the connection of an existing
	 * port see {@link #rewire(OutputPort, boolean)}
	 * 
	 * @param inputPort
	 *            the port this output port should be connected to.
	 * @param dispatch
	 *            true = actions will be 'dispatched'. false = actions will be 'forwarded'. see {@link DispatchController} for distinction between dispatching and forwarding
	 * @throws RuntimeException
	 *             if this port is not an output port, if the inputPort supplied is not an input port, or if this port is already connected to another port.
	 */
	void wire(InputPort<? super A> inputPort, boolean dispatch);

	/**
	 * connects this output port to some input port, but will not fail if the port is already connected and instead will just reconnect to a new port. The intented us for this
	 * method over {@link #wire(OutputPort, boolean)} is to override default connection settings.
	 * 
	 * @param inputPort
	 *            the port this output port should be connected to.
	 * @param dispatch
	 *            true = actions will be 'dispatched'. false = actions will be 'forwarded'. see {@link DispatchController} for distinction between dispatching and forwarding
	 * @throws RuntimeException
	 *             if this port is not an output port, if the inputPort supplied is not an input port
	 */
	void rewire(InputPort<? super A> inputPort, boolean dispatch);

	/**
	 * point the port to another port. Please note that if this port is an input port, then the supplied port must be an output port. And likewise, if this is an output then the
	 * supplied port must also be an output port
	 * 
	 * @param port
	 *            the port this port should point to
	 */
	void pointTo(OutputPort<? super A> port);
	void repointTo(OutputPort<? super A> port);

	/**
	 * @return true is this port will dispatch messages, false if it will forward. see {@link DispatchController} for distinctions between the two.
	 */
	boolean isDispatch();

	/**
	 * @return true is this port will forward messages, false if it will dispatch. see {@link DispatchController} for distinctions between the two.
	 */
	boolean isForward();

	/**
	 * sends an action down a connected port (either in a forward or dispatch fashion, depending on how it was {@link #wire(OutputPort, boolean)}ed)
	 * 
	 * @param a
	 *            the action to send
	 * @param threadScope
	 *            optionally supplied the {@link ThreadScope} (if you conveniently have access to it otherwise may be null)
	 */
	void send(A a, ThreadScope threadScope);

	/**
	 * 
	 * sends an action down a connected port and force the partition that it will resolve to. see the {@link DispatchController} for details on partition resolving.
	 * {@link #wire(OutputPort, boolean)}ed)
	 * 
	 * @param a
	 *            the action to send
	 * @param partitionId
	 *            the partition to send the action to (if null, {@link DispatchController} will determine the partition)
	 * @param threadScope
	 *            optionally supplied {@link ThreadScope} (if you conveniently have access to it, otherwise may be null)
	 */
	void send(A a, Object partitionId, ThreadScope threadScope);

	void sendDelayed(A a, Object partitionId, ThreadScope threadScope, long delay, TimeUnit timeUnit);

	void sendDelayed(A a, ThreadScope threadScope, long delay, TimeUnit timeUnit);

	/**
	 * @return the type of actions this port expects to receive.
	 */
	Class<A> getActionType();

	@Override
	OutputPort<A> setName(String name);

}
