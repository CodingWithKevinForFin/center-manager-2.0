package com.f1.container.impl;

import java.util.concurrent.atomic.AtomicInteger;

import com.f1.base.Action;
import com.f1.utils.OH;

public class RoundRobinPartitionResolver extends AbstractPartitionResolver<Action> {

	private final AtomicInteger next = new AtomicInteger();
	private Object[] partitionIds;
	private int lastId;

	public RoundRobinPartitionResolver(Object[] partitionIds) {
		this.partitionIds = partitionIds;
		this.lastId = this.partitionIds.length - 1;
	}

	public RoundRobinPartitionResolver(String prefix, int count) {
		OH.assertGe(count, 1);
		partitionIds = new Object[count];
		for (int i = 0; i < count; i++)
			partitionIds[i] = prefix + i;
		this.lastId = this.partitionIds.length - 1;
	}

	@Override
	public Class<Action> getActionType() {
		return Action.class;
	}

	@Override
	public Object getPartitionId(Action action_) {

		int count;
		while (!next.compareAndSet(count = next.get(), count == lastId ? 0 : count + 1));
		return partitionIds[count];
	}
}
