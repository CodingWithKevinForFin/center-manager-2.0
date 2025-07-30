/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import com.f1.base.Action;
import com.f1.container.PartitionResolver;
import com.f1.container.RequestMessage;

public class RequestPartitionResolver<A extends Action> extends AbstractContainerScope implements PartitionResolver<com.f1.container.RequestMessage<A>> {

	private PartitionResolver<A> inner;

	public RequestPartitionResolver(PartitionResolver<A> resolver) {
		if (resolver == null)
			throw new NullPointerException("inner resolver");
		this.inner = resolver;
	}

	@Override
	public Object getPartitionId(RequestMessage<A> action) {
		if (action == null)
			throw new NullPointerException("action");
		return inner.getPartitionId(action.getAction());
	}

	@Override
	public Class<RequestMessage<A>> getActionType() {
		return (Class) RequestMessage.class;
	}

}
