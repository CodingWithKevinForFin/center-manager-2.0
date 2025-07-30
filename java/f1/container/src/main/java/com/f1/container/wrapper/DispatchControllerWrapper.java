package com.f1.container.wrapper;

import java.util.Collection;
import java.util.logging.Level;

import com.f1.base.Action;
import com.f1.container.DispatchController;
import com.f1.container.MultiProcessor;
import com.f1.container.Partition;
import com.f1.container.Port;
import com.f1.container.ProcessActionListener;
import com.f1.container.Processor;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicThreadScope;
import com.f1.container.impl.dispatching.IterableActionProcessor;
import com.f1.container.impl.dispatching.RootPartitionActionRunner;

public class DispatchControllerWrapper extends ContainerScopeWrapper implements DispatchController {

	final private DispatchController inner;

	@Override
	public Partition dispatch(Port<?> optionalSourcePort, Processor<?, ?> processor, Action action, Object partitionId, long delayMs, ThreadScope threadScope) {
		return inner.dispatch(optionalSourcePort, processor, action, partitionId, delayMs, threadScope);
	}

	@Override
	public Partition dispatch(Port<?> optionalSourcePort, Processor<?, ?> processor, Action action, Object partitionId, ThreadScope threadScope) {
		return inner.dispatch(optionalSourcePort, processor, action, partitionId, threadScope);
	}

	@Override
	public void forward(Port<?> optionalSourcePort, Processor<?, ?> processor, Action action, ThreadScope scope) {
		inner.forward(optionalSourcePort, processor, action, scope);
	}

	@Override
	public boolean onPartitionAdded(Partition partition) {
		return inner.onPartitionAdded(partition);
	}

	@Override
	public void handleThrowableFromProcessor(Processor<?, ?> processor, Action action, State state, ThreadScope thread, Throwable t) {
		inner.handleThrowableFromProcessor(processor, action, state, thread, t);
	}

	@Override
	public void reply(Port<?> optionalSourcePort, RequestMessage<?> request, ResultMessage<?> result, ThreadScope threadScope) {
		inner.reply(optionalSourcePort, request, result, threadScope);

	}

	public DispatchControllerWrapper(DispatchController inner) {
		super(inner);
		this.inner = inner;
	}

	@Override
	public <A extends Action, S extends State> State createState(Partition partition, Processor<? super A, S> processor, A action) {
		return inner.createState(partition, processor, action);
	}

	@Override
	public <A extends Action, S extends State> void safelyProcess(Port<?> optionalSourcePort, Processor<? super A, ? super S> processor, A action, S state,
			ThreadScope threadScope, boolean isDispatch) {
		inner.safelyProcess(optionalSourcePort, processor, action, state, threadScope, isDispatch);
	}
	@Override
	public boolean isIdle() {
		return inner.isIdle();
	}

	@Override
	public void setDefaultFutureTimeoutMs(long defaultFutureTimeoutMs) {
		inner.setDefaultFutureTimeoutMs(defaultFutureTimeoutMs);
	}

	@Override
	public Level getPerformanceLoggingLevel() {
		return inner.getPerformanceLoggingLevel();
	}

	public DispatchController getInner() {
		return inner;
	}

	@Override
	public void addProcessActionListener(ProcessActionListener listener) {
		inner.addProcessActionListener(listener);
	}

	@Override
	public ProcessActionListener[] getProcessActionListeners() {
		return inner.getProcessActionListeners();
	}

	@Override
	public void safelyProcess(IterableActionProcessor iterator, MultiProcessor processor, State state, BasicThreadScope thread) {
		inner.safelyProcess(iterator, processor, state, thread);
	}

	@Override
	public boolean onPartitionRemoved(Partition partition) {
		return inner.onPartitionRemoved(partition);
	}

	@Override
	public Collection<RootPartitionActionRunner> getRootParititionRunners() {
		return inner.getRootParititionRunners();
	}

	@Override
	public RootPartitionActionRunner getRootPartitionRunner(String partitionId) {
		return inner.getRootPartitionRunner(partitionId);
	}

	@Override
	public long getActionsInQueueCount() {
		return inner.getActionsInQueueCount();
	}

	@Override
	public long getActionsProcessedCount() {
		return inner.getActionsProcessedCount();
	}

	@Override
	public long getExceptionsCount() {
		return this.inner.getExceptionsCount();
	}

	@Override
	public long getQueueTimeoutMs() {
		return this.inner.getQueueTimeoutMs();
	}

	@Override
	public int getQueueTimeoutCheckFrequency() {
		return this.inner.getQueueTimeoutCheckFrequency();
	}

	@Override
	public void setQueueTimeoutMs(long timeout) {
		this.inner.setQueueTimeoutMs(timeout);
	}

	@Override
	public void setQueueTimeoutCheckFrequency(int frequency) {
		this.inner.setQueueTimeoutCheckFrequency(frequency);

	}

}
