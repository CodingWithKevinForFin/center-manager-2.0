/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

import com.f1.base.Action;

/**
 * A processor takes an action and processes some 'logic', typically 'business logic' by overriding {@link #processAction(Action, State, ThreadScope)}.
 * <P>
 * <B>Connections</B><BR>
 * Note that processors are types of {@link Connectable}, <PB>
 * 
 * <B>Scopes</B><BR>
 * As outlined in the f1 container document there are four scopes. All for scopes are accessible from the {@link #processAction(Action, State, ThreadScope)} call.<BR>
 * 1. <I>Container</I> - Because a processor is a {@link ContainerScope} instance, access to the container scope is available by calling {@link ContainerScope#getContainer()}. <BR>
 * 2. <I>Partition</I> - the {@link State} is passed in as a parameter.<BR>
 * 3. <I>Action</I> - the {@link Action} is passed in as a parameter.<BR>
 * 4. <I>Thread</I> - the {@link ThreadScope} is passed in as a parameter.
 * <P>
 * <B>Decorators</B> There are three main decorators used to dictate how the {@link Processor} operates<BR>
 * 1. {@link PartitionResolver} - used to determine which partition will be passed into the {@link #processAction(Action, State, ThreadScope)} for a given action<BR>
 * 2. {@link StateGenerator} - in the case the {@link PartitionResolver} returns a partition id which is not associated an existing state then the state generator will create the
 * state<BR>
 * 3. {@link ThrowableHandler} - if the {@link #processAction(Action, State, ThreadScope)} implementation throws an exception, the exception and supplied parameters will be passed
 * into the {@link ThrowableHandler#handleThrowable(Processor, Action, State, ThreadScope, Throwable)} <BR>
 * <P>
 * Threading: It should be assumed that only one thread will be calling start and various other event level methods. On the other hand, any number of threads could be accessing the
 * {@link #processAction(Action, State, ThreadScope)} simultaneously. As such calls to processAction should in now way mutate the processor
 * 
 * @param <A>
 * @param <S>
 */
public interface Processor<A extends Action, S extends State> extends Connectable {

	/**
	 * called by the f1 container framework, when an action is destined for this processor.
	 * 
	 * @param action
	 *            the action to process
	 * @param state
	 *            the state assoicated with this action (as returned by this {@link PartitionResolver}'s {@link PartitionResolver#getPartitionId(Action)})
	 * @param threadScope
	 *            the threadscope associated w/ the thread that is executing this method
	 * @throws Exception
	 *             if the business logic throws some sort of acception
	 */
	void processAction(A action, S state, ThreadScope threadScope) throws Exception;

	/**
	 * should return the partition resolver used to determine which partition an action destined for this {@link Processor} belongs to
	 * 
	 * @return said partition resolver, must not return null.
	 */
	PartitionResolver<? super A> getPartitionResolver();

	void setPartitionResolver(PartitionResolver<? super A> resolver);

	/**
	 * Each processor will have exactly one input port. This is a convience function equivalent to {@link #getInputs()} .get(0)
	 * 
	 * @return the input port whose actions will be processed by this processor
	 */
	InputPort<A> getInputPort();

	/**
	 * Override to handle exceptions thrown by the {@link #processAction(Action, State, ThreadScope)} method
	 * 
	 * @param action
	 * @param state
	 * @param thread
	 * @param thrown
	 * @throws Throwable
	 */
	void handleThrowable(A action, S state, ThreadScope thread, Throwable thrown) throws Throwable;

	/**
	 * @return the type of state that this processor can process.Never returns null. see {@link #processAction(Action, State, ThreadScope)

	 */
	Class<S> getStateType();

	/**
	 * @return the type of action that this processor can process.Never returns null. see {@link #processAction(Action, State, ThreadScope)

	 */
	Class<A> getActionType();

	public void addProcessActionListener(ProcessActionListener listener);
	public ProcessActionListener[] getProcessActionListeners();

	void bindToPartition(Object partitionId);

}
