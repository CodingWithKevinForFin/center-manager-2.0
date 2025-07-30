/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

import java.util.Collection;

/**
 * 
 * A class that can be connected to other connetable instance via {@link Port}s. Connectables can have any number of input ports (where they receive messages in from other
 * connectables) and any number of output ports (where they send message out on). Generally, connectables "live inside a suite" and are wired (via
 * {@link Suite#wire(OutputPort, InputPort, boolean)}) to one another. This provides a clean IOC (inversion of control) where the connectables themselves don't directly connect to
 * other connectables but instead declare endpoints ({@link Port}) where later on they can be pointer together.
 * <P>
 * IMPORTANT: the concept of connectable and Ports often get confused with inter-process-communication but in fact all of this lives within a single process. See Msg library for
 * concepts on IPC in the F1 framework
 * 
 */
public interface Connectable extends ContainerScope {

	/**
	 * The parent that owns this suite.
	 * 
	 * @return owning suite. Will be null if this is not added to a suite yet, or this is the root suite
	 */
	Suite getParent();

	/**
	 * register the parent of this object, such that parent.getChildren() should contain this object. should only be called prior to {@link StartStoppable#start()}
	 * 
	 * @param parent
	 *            parent of this object
	 */
	void setParent(Suite parent);

	/**
	 * list of input ports available;
	 * 
	 * @return immutable list
	 */
	Collection<InputPort<?>> getInputs();

	/**
	 * list of output ports available;
	 * 
	 * @return immutable list
	 */
	Collection<OutputPort<?>> getOutputs();

}
