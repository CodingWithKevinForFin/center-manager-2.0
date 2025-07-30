package com.f1.container.impl.dispatching;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.f1.base.Action;
import com.f1.base.Clearable;
import com.f1.container.Partition;
import com.f1.container.ProcessActionListener;
import com.f1.container.Processor;
import com.f1.container.State;
import com.f1.container.impl.BasicThreadScope;
import com.f1.container.impl.ContainerHelper;
import com.f1.utils.TimeoutMonitor;
import com.f1.utils.concurrent.FastQueue;

public class IterableActionProcessor implements Iterator<Action>, Clearable {

	private Processor<?, ?> processor;
	private State state;
	private PartitionActionEvent next;
	private FastQueue<PartitionActionEvent<?>> queue;
	private PartitionActionEvent remaining;
	private TimeoutMonitor timeout;
	private Action current;
	private int count = 0;
	private ProcessActionListener[] targets = ContainerHelper.EMPTY_PROCESS_EVENT_LISTENER_ARRAY;
	private Partition partition;
	private BasicThreadScope threadScope;
	private boolean isDispatching;

	public void reset(ProcessActionListener[] processActionListeners, boolean isDispatching, PartitionActionEvent sae, FastQueue<PartitionActionEvent<?>> queue, State state,
			BasicThreadScope thread, TimeoutMonitor timeout) {
		this.isDispatching = isDispatching;
		this.targets = processActionListeners;
		this.processor = sae.processor;
		this.state = state;
		this.partition = state.getPartition();
		this.threadScope = thread;
		this.next = sae;
		this.queue = queue;
		this.timeout = timeout;
		this.remaining = null;
		this.current = null;
		this.count = 0;
	}
	public void clear() {
		this.targets = ContainerHelper.EMPTY_PROCESS_EVENT_LISTENER_ARRAY;
		this.current = null;
		this.processor = null;
		this.state = null;
		this.partition = null;
		this.threadScope = null;
		this.next = null;
		this.queue = null;
		this.remaining = null;
		this.timeout = null;
		this.count = 0;
	}

	@Override
	public boolean hasNext() {
		if (next != null)
			return true;//first iterator, or subsequent call to hasNext()
		if (remaining != null)
			return false;//subsequent call to hasNext(), even after it returned false;
		if (timeout.hasAlreadyTimedout())
			return false;//too much time.
		next = queue.get();
		if (next == null)
			return false;//no more events in queue
		else if (next.processor == processor)
			return true;// same processor, so more events
		else {
			this.remaining = next;
			this.next = null;
			return false;//the processor is different so hasNext is now false
		}
	}

	@Override
	public Action next() {
		if (next == null && !hasNext())
			throw new NoSuchElementException("no more elements, should check with call to hasNext() first");
		if (current != null)
			fireProcessActionDone(current);
		current = next.action;
		next = null;
		count++;
		timeout.hasTimedout();
		fireProcessAction(current);
		return current;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public PartitionActionEvent getRemaining() {
		if (this.next != null)
			return next;
		return this.remaining;
	}

	public Action getLastReturnedAction() {
		return current;
	}

	public int getReturnedCount() {
		return count;
	}

	public void fireProcessAction(Action action) {
		for (int i = 0; i < targets.length; i++)
			targets[i].onProcessAction(processor, partition, action, state, threadScope, isDispatching);
	}
	public void fireProcessActionDone(Action action) {
		for (int i = 0; i < targets.length; i++)
			targets[i].onProcessActionDone(processor, partition, action, state, threadScope, isDispatching);
	}
	public final void fireHandleThrowable(Action action, Throwable thrown) {
		for (int i = 0; i < targets.length; i++)
			targets[i].onHandleThrowable(processor, partition, action, state, threadScope, thrown);
	}
}
