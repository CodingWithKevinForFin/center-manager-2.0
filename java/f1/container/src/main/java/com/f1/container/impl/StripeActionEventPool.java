package com.f1.container.impl;

import com.f1.container.impl.dispatching.PartitionActionEvent;
import com.f1.utils.BasicObjectGeneratorForClass;
import com.f1.utils.concurrent.GlobalObjectPool;
import com.f1.utils.concurrent.LocalObjectPool;

public class StripeActionEventPool extends BasicObjectGeneratorForClass<PartitionActionEvent> {
	public static final GlobalObjectPool<PartitionActionEvent> GlobalSaePool = new GlobalObjectPool<PartitionActionEvent>(new StripeActionEventPool(), 250,
			2000);
	public static final ThreadLocal<LocalObjectPool<PartitionActionEvent>> localSaePool = new ThreadLocal<LocalObjectPool<PartitionActionEvent>>() {
		@Override
		public LocalObjectPool<PartitionActionEvent> initialValue() {
			LocalObjectPool<PartitionActionEvent> r = new LocalObjectPool<PartitionActionEvent>(GlobalSaePool);
			return r;
		}
	};

	public StripeActionEventPool() {
		super(null, null, PartitionActionEvent.class);
	}

	@Override
	public PartitionActionEvent nw() {
		return new PartitionActionEvent();
	}

	public static LocalObjectPool<PartitionActionEvent> get() {
		return localSaePool.get();
	}

}
