/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl.dispatching;

import java.util.List;
import java.util.logging.Logger;

import com.f1.base.Action;
import com.f1.base.Conflatable;
import com.f1.container.DispatchController;
import com.f1.container.Partition;
import com.f1.container.Processor;
import com.f1.container.State;
import com.f1.container.impl.BasicThreadScope;
import com.f1.container.impl.PartitionActionEventPool;
import com.f1.utils.TimeoutMonitor;
import com.f1.utils.concurrent.ConflatingMap;
import com.f1.utils.concurrent.ConflatingMap.ConflatingMapListener;

public class ConflatedPartitionActionRunner implements PartitionActionRunner, ConflatingMapListener<PartitionActionEvent> {
	private static final Logger log = Logger.getLogger(ConflatedPartitionActionRunner.class.getName());

	final private ConflatingMap<Object, PartitionActionEvent<?>> conflatingMap;
	final private DispatchController dispatcher;
	private int actionsCount;

	public ConflatedPartitionActionRunner(boolean ignoreDups, DispatchController dispatcher) {
		conflatingMap = new ConflatingMap<Object, PartitionActionEvent<?>>(false, this);
		this.dispatcher = dispatcher;
	}

	@Override
	public void addAction(Processor processor, Action action, BasicThreadScope threadScope) {
		Object key = ((Conflatable) action).getConflatingKey();
		PartitionActionEvent sae = threadScope == null ? PartitionActionEventPool.get().nw() : threadScope.localSaePool.nw();
		sae.action = action;
		sae.processor = processor;
		conflatingMap.put(key, sae);
	}

	@Override
	public void runActions(BasicThreadScope thread, Partition partition, TimeoutMonitor timeout) {
		State state = null;
		int cnt = 0;
		for (PartitionActionEvent sae : conflatingMap) {
			Class<?> type = sae.processor.getStateType();
			if (state == null || (type != state.getType() && type.equals(state.getType())) || !state.isAlive()) {
				state = partition.getState(sae.processor.getStateType());
				if (state == null || !state.isAlive()) {
					if (type == null)
						state = null;
					else
						state = dispatcher.createState(partition, sae.processor, sae.action);
				}
				thread.state = state;
			}
			dispatcher.safelyProcess(sae.sourcePort, sae.processor, sae.action, state, thread, true);
			thread.localSaePool.old(sae);
			sae = null;
			timeout.incrementChecks();
		}
		actionsCount += cnt;
	}

	@Override
	public boolean hasActions() {
		return !conflatingMap.isEmpty();
	}

	@Override
	public boolean canProcess(Action action) {
		return action instanceof Conflatable && ((Conflatable) action).getConflatingKey() != null;
	}

	@Override
	public int getCheckPriority() {
		return 10;
	}

	@Override
	public void onConflatedOut(PartitionActionEvent oldValue) {
		PartitionActionEventPool.get().old(oldValue);
	}

	@Override
	public int getPendingActionsCount() {
		return this.conflatingMap.size();
	}

	@Override
	public void getQueuedEvents(List<PartitionActionEvent<?>> sink) {
		this.conflatingMap.getValues(sink);
	}
}
