/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

import java.util.Collection;

import com.f1.base.Action;

/**
 * Suites provide a level of encapsulation and facilicate the logical grouping of {@link Processor}s. Internally, suites can contain any number of
 * {@link Processor}s and nested {@link Suite}s. Externally, suites can have any number of input and output ports (called 'exposed' ports). Generally the
 * exposed ports are connected to the various internal ports. Additionally, childrens' various ports can be directly "wired" to each other.
 */
public interface Suite extends Connectable {

	/**
	 * @return all children connectables added to this suite. For example, all sub-{@link Suite}s and {@link Processor}s
	 */
	Collection<Connectable> getChildConnectables();

	/**
	 * Creates a new exposed input port which will be connected to the supplied processor's input port
	 * 
	 * @param <A>
	 *            type of action that can be sent on the port
	 * @param processor
	 *            must be a child of the suite. see {@link #addChild(Connectable)}
	 * @return the newly created input port
	 */
	<A extends Action> InputPort<A> exposeInputPort(Processor<A, ?> processor);

	<A extends Action, D extends Action> RequestInputPort<A, D> exposeInputPort(RequestProcessor<A, ?, D> processor);

	/**
	 * Connects the supplied existing exposed input port to the supplied port
	 * 
	 * @param <A>
	 *            type of action that can be sent on the port
	 * @param processor
	 *            must be a child of the suite. see {@link #addChild(Connectable)}
	 * @param existing
	 *            exposed input port. Must already by a child of this suite, Must be in an "unconnected" state.
	 * @return the newly created input port
	 */
	<A extends Action> void exposeInputPortTo(Processor<? super A, ?> processor, InputPort<A> existing);

	/**
	 * Creates a new exposed output port which will be connected to the supplied processor
	 * 
	 * @param <A>
	 *            type of action that can be sent on the port
	 * @param existingOutputPort
	 *            must be an output port of a child of the suite.
	 * @return the newly created output port
	 */
	<A extends Action, P extends OutputPort<A>> P exposeOutputPort(P existingOutputPort);

	/**
	 * Connects the output port of an existing child to a existing output.
	 * 
	 * @param <A>
	 *            type of action that can be sent on the port
	 * @param existingOutputPort
	 *            must be an output port of a child of the suite.
	 * @param existing
	 *            an existing, exposed output port
	 * @return the supplied existing port(for convenience)
	 */
	<A extends Action, P extends OutputPort<A>> P exposeOutputPortTo(OutputPort<A> existingOutputPort, P existing);

	/**
	 * Creates a new exposed input port which will be connected to the supplied input port
	 * 
	 * @param <A>
	 *            type of action that can be sent on the port
	 * @param port
	 *            must be an input port of a child of the suite. see {@link #addChild(Connectable)}
	 * @return the newly created input port
	 */
	<A extends Action, P extends InputPort<A>> P exposeInputPort(P port);

	/**
	 * Creates a new exposed output port which will be connected to the supplied input port
	 * 
	 * @param <A>
	 *            type of action that can be sent on the port
	 * @param port
	 *            must be an input port of a child of the suite. see {@link #addChild(Connectable)}
	 * @param isDispatch
	 *            if true, then actions sent on the output port will be 'dispatched' to the supplied input port (asynchronous). If false, then actions are
	 *            'forwarded' (synchronous)
	 * @return the newly created output port
	 */
	<A extends Action> OutputPort<A> exposeInputPortAsOutput(InputPort<A> port, boolean isDispatch);
	<REQ extends Action, RES extends Action> RequestOutputPort<REQ, RES> exposeInputPortAsOutput(RequestInputPort<REQ, RES> port, boolean isDispatch);

	/**
	 * Creates a new exposed output port which will be connected to the supplied processor's input port
	 * 
	 * @param <A>
	 *            type of action that can be sent on the port
	 * @param processor
	 *            must be a child of the suite. see {@link #addChild(Connectable)}
	 * @param isDispatch
	 *            if true, then actions sent on the output port will be 'dispatched' to the supplied input port (asynchronous). If false, then actions are
	 *            'forwarded' (synchronous)
	 * @return the newly created output port
	 */
	<A extends Action> OutputPort<A> exposeInputPortAsOutput(Processor<A, ?> processor, boolean isDispatch);
	<A extends Action, D extends Action> RequestOutputPort<A, D> exposeInputPortAsOutput(RequestProcessor<A, ?, D> processor, boolean isDispatch);

	/**
	 * Connects the supplied output port of an existing child to the supplied input port of another existing child
	 * 
	 * @param <A>
	 *            type of action that can be sent on the port
	 * @param output
	 *            must be an output port of a child of this suite. Must not already be connected. see {@link #addChild(Connectable)}
	 * @param input
	 *            must be an input port of a child of this suite. see {@link #addChild(Connectable)}
	 * @param isDispatch
	 *            if true, then actions sent on the output port will be 'dispatched' to the supplied input port (asynchronous). If false, then actions are
	 *            'forwarded' (synchronous)
	 */
	<A extends Action> void wire(OutputPort<A> output, InputPort<? super A> input, boolean isDispatch);

	/**
	 * Connects the supplied output port of an existing child to the supplied existing child processor
	 * 
	 * @param <A>
	 *            type of action that can be sent on the port
	 * @param output
	 *            must be an output port of a child of this suite. Must not already be connected. see {@link #addChild(Connectable)}
	 * @param input
	 *            must be a child processor of this suite. see {@link #addChild(Connectable)}
	 * @param isDispatch
	 *            if true, then actions sent on the output port will be 'dispatched' to the supplied input port (asynchronous). If false, then actions are
	 *            'forwarded' (synchronous)
	 */
	<A extends Action> void wire(OutputPort<A> output, Processor<? super A, ?> input, boolean isDispatch);

	/**
	 * Connects the supplied output port of an existing child to the supplied input port of another existing child
	 * 
	 * @param <A>
	 *            type of action that can be sent on the port
	 * @param output
	 *            must be an output port of a child of this suite. if already connected, will be reconnected. see {@link #addChild(Connectable)}
	 * @param input
	 *            must be an input port of a child of this suite. see {@link #addChild(Connectable)}
	 * @param isDispatch
	 *            if true, then actions sent on the output port will be 'dispatched' to the supplied input port (asynchronous). If false, then actions are
	 *            'forwarded' (synchronous)
	 */
	<A extends Action> void rewire(OutputPort<A> output, InputPort<? super A> input, boolean isDispatch);

	/**
	 * Connects the supplied output port of an existing child to the supplied existing child processor
	 * 
	 * @param <A>
	 *            type of action that can be sent on the port
	 * @param output
	 *            must be an output port of a child of this suite. if already connected, will be reconnected. see {@link #addChild(Connectable)}
	 * @param input
	 *            must be a child processor of this suite. see {@link #addChild(Connectable)}
	 * @param isDispatch
	 *            if true, then actions sent on the output port will be 'dispatched' to the supplied input port (asynchronous). If false, then actions are
	 *            'forwarded' (synchronous)
	 */
	<A extends Action> void rewire(OutputPort<A> output, Processor<? super A, ?> input, boolean isDispatch);

	/**
	 * Same as addChild, but takes an array for convenience. see {@link #addChild(Connectable)}
	 * 
	 * @param children
	 *            array of children to be added to this suite.
	 */
	void addChildren(Connectable... children);

	/**
	 * add a child connectable (typically either a suite or processor) to this suite.
	 * 
	 * @param <R>
	 *            the type of connectable added
	 * @param child
	 *            the child to add.. must not already exist in this suite as a child
	 * @return the added child (for convenience)
	 */
	<R extends Connectable> R addChild(R child);

	/**
	 * Apply the supplied resolver to all {@link Processor}s of this suite. Please note, it will only be added to those processors that are applicable based on
	 * the action type of the processor and supplied resolver.
	 * 
	 * @param <A>
	 *            the type of action the resolve handlers
	 * @param resolver
	 *            the resolver to apply to all internal processors.
	 * @param overrideExisting
	 *            replace existing resolvers on inner processors.
	 * @param recurse
	 *            nest recursively into sub suites as well.
	 */
	<A extends Action> void applyPartitionResolver(PartitionResolver<A> resolver, boolean overrideExisting, boolean recurse);

}
