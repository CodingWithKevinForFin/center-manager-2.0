/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

import com.f1.base.Action;

/**
 * Responsible for creating new states.
 * 
 */
public interface StateGenerator<A extends Action, S extends State> extends ContainerScope {

	/**
	 * create a new state which will be added to a particular partition.<BR>
	 * <B> VERY IMPORTANT THREADING NOTE:</B> This is called by the framework in a blocking fashion during the creation of the thread, For example, message A,B,C arrive to the
	 * dispatcher. Message A is passed to the {@link PartitionResolver} and then the Processor is inspected for what type of State should be passed into the processor. If either
	 * the partition or corresponding state type does not already exist (in the {@link PartitionController}) then this factory is imm. Anything beyond basic setup of a state should
	 * be done in a {@link Processor}.
	 * 
	 * @param partition
	 *            the partition that the newely created state will be a 'member' of
	 * @param a
	 *            the action that inspired the creation of this state.
	 * @param processor
	 *            the processor that the supplied action will be processed on
	 * @return the newely created state. must not return null
	 */
	S createState(Partition partition, A a, Processor<? extends A, ?> processor);

	/**
	 * @return the type of state that this generator will create. see {@link State#getType()}
	 */
	Class getStateType();

}
