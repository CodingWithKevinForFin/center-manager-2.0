/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import com.f1.base.Action;
import com.f1.container.PartitionResolver;

public class BasicPartitionResolver<A extends Action> extends AbstractContainerScope implements PartitionResolver<A> {

	private final Object partitionId;
	private final Class<A> actionType;

	public BasicPartitionResolver(Class<A> actionType, Object partitionId) {
		super();
		this.actionType = actionType;
		this.partitionId = partitionId;
	}

	@Override
	public Object getPartitionId(A action) {
		return partitionId;
	}

	@Override
	public Class<A> getActionType() {
		return actionType;
	}

	public String toString() {
		if (getClass() == BasicPartitionResolver.class)
			return getClass().getName() + ":Constant Partition ID '" + partitionId + "'";
		else
			return super.toString();
	}

}
