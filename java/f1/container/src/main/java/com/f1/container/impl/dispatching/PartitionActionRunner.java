/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl.dispatching;

import java.util.List;

import com.f1.base.Action;
import com.f1.container.Partition;
import com.f1.container.Processor;
import com.f1.container.impl.BasicThreadScope;
import com.f1.utils.TimeoutMonitor;

public interface PartitionActionRunner {

	public void addAction(Processor processor, Action action, BasicThreadScope threadScope);

	public void runActions(BasicThreadScope thread, Partition partition, TimeoutMonitor timeout);

	public boolean hasActions();

	public boolean canProcess(Action action);

	// lower number=higher priority
	public int getCheckPriority();

	public int getPendingActionsCount();

	public void getQueuedEvents(List<PartitionActionEvent<?>> sink);

}
