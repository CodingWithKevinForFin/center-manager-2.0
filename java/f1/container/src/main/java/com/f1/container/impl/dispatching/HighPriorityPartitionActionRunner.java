/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl.dispatching;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.base.Action;
import com.f1.base.NestedAction;
import com.f1.base.Prioritized;
import com.f1.container.DispatchController;
import com.f1.container.Partition;
import com.f1.container.ResultMessage;
import com.f1.container.impl.BasicThreadScope;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.TimeoutMonitor;

public class HighPriorityPartitionActionRunner extends QueuedPartitionActionRunner {

	private int priority;
	private static final Logger log = LH.get();

	public HighPriorityPartitionActionRunner(DispatchController dispather, int priority) {
		super(dispather);
		OH.assertBetween(priority, Prioritized.HIGHEST_PRIORITY, Prioritized.NORMAL_PRIORITY - 1);
		this.priority = priority;
	}

	@Override
	public boolean canProcess(Action action) {
		if (action instanceof NestedAction && !(action instanceof Prioritized)) {
			if (action instanceof ResultMessage)
				action = OH.noNull(((ResultMessage) action).getActionNoThrowable(), action);
			else
				action = ((NestedAction) action).getAction();
		}
		if (action instanceof Prioritized && ((Prioritized) action).getPriority() <= priority) {
			if (log.isLoggable(Level.FINE))
				LH.fine(log, "Received High Priority request:", OH.getClassName(action));
			return true;
		}
		return false;
	}
	@Override
	public void runActions(BasicThreadScope thread, Partition partition, TimeoutMonitor timeout) {
		int before = timeout.getChecksCount();
		super.runActions(thread, partition, timeout);
		int after = timeout.getChecksCount();
		if (log.isLoggable(Level.FINE))
			LH.fine(log, "Ran High Priority events on ", partition.getPartitionId(), ": ", after - before);
	}

	@Override
	public int getCheckPriority() {
		return priority;
	}

}
