/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl.dispatching;

import com.f1.base.Action;
import com.f1.base.Prioritized;
import com.f1.container.DispatchController;
import com.f1.utils.OH;

public class LowPriorityPartitionActionRunner extends QueuedPartitionActionRunner {

	private int priority;

	public LowPriorityPartitionActionRunner(DispatchController dispather, int priority) {
		super(dispather);
		OH.assertBetween(priority, Prioritized.NORMAL_PRIORITY + 1, Prioritized.LOWEST_PRIORITY);
		this.priority = priority;
	}

	@Override
	public boolean canProcess(Action action) {
		return action instanceof Prioritized && ((Prioritized) action).getPriority() >= priority;
	}

	@Override
	public int getCheckPriority() {
		return Prioritized.LOWEST_PRIORITY - priority;
	}

}
