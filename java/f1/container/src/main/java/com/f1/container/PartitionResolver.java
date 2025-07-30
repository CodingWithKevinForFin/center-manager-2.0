/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

import com.f1.base.Action;

/**
 * Typically, a parititon resolver is associate with one or more
 * {@link Processor}s. See
 * {@link Processor#setPartitionResolver(PartitionResolver)}.<BR>
 * All {@link Action} that are processed by {@link Processor}s must first by
 * associated with a {@link Partition}. As such, the
 * {@link #getPartitionId(Action)} should be implemented to inspect the action
 * in order to determine which partition it is destined for. See
 * {@link Processor#processAction(Action, State, ThreadScope)} for details.
 * 
 * @param <A>
 *            the type of action this partition resolver can inspect
 */
public interface PartitionResolver<A extends Action> extends ContainerScope {

	/**
	 * Inspect the supplied action to determine which partition this action
	 * should be associated with.
	 * 
	 * @param action
	 *            the action to inspect (never null)
	 * @return the partition id of the partition the action will be associated
	 *         with. See {@link Partition#getPartitionId()}. Should not return
	 *         null. Note that if no partition is associated with returned
	 *         partitionId, then the {@link PartitionController} will create a
	 *         new one.
	 */
	public Object getPartitionId(A action);

	/**
	 * Return the type of action that this partition controller can inspect.
	 * This is used by the framework for ensuring proper configuration at
	 * runtime.
	 * 
	 * @return the type of action this class can inspect
	 */
	public Class<A> getActionType();

}
