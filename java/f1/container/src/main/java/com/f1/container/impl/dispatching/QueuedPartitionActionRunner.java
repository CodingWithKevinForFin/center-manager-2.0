/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl.dispatching;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.f1.base.Action;
import com.f1.container.DispatchController;
import com.f1.container.MultiProcessor;
import com.f1.container.Partition;
import com.f1.container.Processor;
import com.f1.container.State;
import com.f1.container.impl.BasicPartition;
import com.f1.container.impl.BasicThreadScope;
import com.f1.container.impl.PartitionActionEventPool;
import com.f1.utils.TimeoutMonitor;
import com.f1.utils.concurrent.FastQueue;

public class QueuedPartitionActionRunner implements PartitionActionRunner {
	private static final Logger log = Logger.getLogger(QueuedPartitionActionRunner.class.getName());

	final public FastQueue<PartitionActionEvent<?>> queue = new FastQueue<PartitionActionEvent<?>>();

	final private DispatchController dispatcher;

	public QueuedPartitionActionRunner(DispatchController dispather) {
		this.dispatcher = dispather;
	}

	@Override
	public void runActions(BasicThreadScope thread, Partition partition, TimeoutMonitor timeout) {
		int cnt = 0;
		try {
			PartitionActionEvent sae;
			State state = null;
			sae = queue.get();
			while (sae != null) {
				Object type = sae.processor.getStateType();
				if (state == null || (type != state.getType() && !state.getType().equals(type)) || !state.isAlive()) {
					state = partition.getState(sae.processor.getStateType());
					if (state == null) {
						if (type == null)
							state = null;
						else
							state = dispatcher.createState(partition, sae.processor, sae.action);
					}
					thread.state = state;
				}
				if (sae.processor instanceof MultiProcessor) {
					IterableActionProcessor iterator = ((BasicPartition) partition).borrowMultiProcessorIterator();
					iterator.reset(dispatcher.getProcessActionListeners(), true, sae, queue, state, thread, timeout);
					try {
						dispatcher.safelyProcess(iterator, (MultiProcessor) sae.processor, state, thread);
						if (timeout.hasAlreadyTimedout())
							break; //too much time.
						sae = iterator.getRemaining();
						if (sae == null) {
							sae = queue.get();
						}
					} finally {
						cnt += iterator.getReturnedCount();
						((BasicPartition) partition).returnMultiProcessorIterator(iterator);
					}
				} else {
					cnt++;
					dispatcher.safelyProcess(sae.sourcePort, sae.processor, sae.action, state, thread, true);
					if (timeout.hasTimedout())
						break;
					sae = queue.get();
				}
			}
		} finally {
			if (cnt > 0)
				size.addAndGet(-cnt);
		}
	}
	private String toString(Action action) {
		return action == null ? null : action.toString();
	}

	private final AtomicInteger size = new AtomicInteger();
	@Override
	public void addAction(Processor processor, Action action, BasicThreadScope threadScope) {
		PartitionActionEvent sae = threadScope == null ? PartitionActionEventPool.get().nw() : threadScope.localSaePool.nw();
		sae.init(sae.sourcePort, processor, action);
		queue.put(sae);
		size.incrementAndGet();
	}

	@Override
	public boolean hasActions() {
		return !queue.isEmpty();
	}

	@Override
	public boolean canProcess(Action action) {
		return true;
	}

	@Override
	public int getCheckPriority() {
		return 1000;
	}

	@Override
	public int getPendingActionsCount() {
		return size.get();
	}

	@Override
	public void getQueuedEvents(List<PartitionActionEvent<?>> sink) {
		this.queue.getEvents(sink);
	}

}
