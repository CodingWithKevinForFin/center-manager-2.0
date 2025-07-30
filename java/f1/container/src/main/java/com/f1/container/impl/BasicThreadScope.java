/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import com.f1.base.Action;
import com.f1.container.Processor;
import com.f1.container.ThreadScope;
import com.f1.container.impl.dispatching.PartitionActionEvent;
import com.f1.container.impl.dispatching.RootPartitionActionRunner;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.concurrent.LocalObjectPool;

public class BasicThreadScope extends ThreadScope {

	private long runCount, errorCount;
	public LocalObjectPool<PartitionActionEvent> localSaePool;

	public BasicThreadScope(String threadPoolId, LocaleFormatter formatter, Runnable runnable, String threadName) {
		super(formatter, runnable, threadName, threadPoolId);
	}

	@Override
	public void run() {
		localSaePool = PartitionActionEventPool.get();
		super.run();
	}

	public com.f1.container.State state;
	public com.f1.container.Partition partition;

	public RootPartitionActionRunner stripeActionQueue;
	private Processor processor;
	private com.f1.container.State currentState;

	public long getRunCount() {
		return runCount;
	}

	public long getErrorCount() {
		return errorCount;
	}

	public void onProcessActionBegin(Processor processor, Action action, com.f1.container.State state) {
		runCount++;
		this.processor = processor;
		this.currentState = state;
	}

	public void onProcessActionError(Throwable t) {
		errorCount++;
	}

	public void onProcessActionEnd() {
		processor = null;
		currentState = null;
	}

	public Processor getCurrentProcessor() {
		return processor;
	}

	public com.f1.container.State getCurrentState() {
		return currentState;
	}
}
