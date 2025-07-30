/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import com.f1.base.Action;
import com.f1.container.PartitionResolver;

public abstract class AbstractPartitionResolver<A extends Action> extends AbstractContainerScope implements PartitionResolver<A> {

	@Override
	abstract public Object getPartitionId(A action);

}
