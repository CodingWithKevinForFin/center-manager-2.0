package com.f1.bootstrap.appmonitor;

import com.f1.base.Action;
import com.f1.container.Partition;
import com.f1.container.ProcessActionListener;
import com.f1.container.Processor;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.povo.f1app.F1AppThreadScope;

public class AppMonitorThreadscopeListener extends AbstractAppMonitorObjectListener<F1AppThreadScope, ThreadScope> implements ProcessActionListener {

	private long processEventsCount = 0;
	private long thrownEventsCount = 0;
	private volatile long currentPartitionId = -1;
	private volatile long currentProcessorId = -1;
	final private long threadScopeControllerId;

	public AppMonitorThreadscopeListener(AppMonitorState state, ThreadScope threadScope, long threadScopeControllerId) {
		super(state, threadScope);
		this.threadScopeControllerId = threadScopeControllerId;
	}

	@Override
	public void onProcessAction(Processor processor, Partition partition, Action action, State state, ThreadScope threadScope, boolean isDispatch) {
		this.currentProcessorId = processor == null ? -1 : processor.getContainerScopeUid();
		this.currentPartitionId = partition == null ? -1 : partition.getContainerScopeUid();
		processEventsCount++;
		flagChanged();
	}

	@Override
	public void onHandleThrowable(Processor processor, Partition partition, Action action, State state, ThreadScope thread, Throwable thrown) {
		this.currentProcessorId = processor == null ? -1 : processor.getContainerScopeUid();
		this.currentPartitionId = partition == null ? -1 : partition.getContainerScopeUid();
		thrownEventsCount++;
		flagChanged();
	}

	public long getProcessEventsCount() {
		return processEventsCount;
	}

	public long getThrownEventsCount() {
		return thrownEventsCount;
	}

	@Override
	public Class<F1AppThreadScope> getAgentType() {
		return F1AppThreadScope.class;
	}
	@Override
	protected void populate(ThreadScope source, F1AppThreadScope sink) {
		sink.setThreadName(source.getName());
		sink.setThreadPoolKey(source.getThreadPoolKey());
		sink.setStartedMs(source.getStartTimeMillis());
		sink.setThreadScopeControllerId(this.threadScopeControllerId);
		sink.setContainerScopeId(source.getContainerScopeUid());
		sink.setThrownStats(getThrownEventsCount());
		sink.setProcessStats(getProcessEventsCount());
		sink.setDispatchStats(0);//TODO
		sink.setForwardStats(0);//TODO
		long pa, pr;
		do {
			pa = currentPartitionId;
			pr = currentProcessorId;
		} while (pa != currentPartitionId || pr != currentProcessorId);
		sink.setCurrentPartitionId(pa);
		sink.setCurrentProcessorId(pr);
	}

	@Override
	public void onProcessActionDone(Processor processor, Partition partition, Action action, State state, ThreadScope threadScope, boolean isDispatch) {
		this.currentProcessorId = -1;
		this.currentPartitionId = -1;

	}

	@Override
	public void onQueueAction(Processor processor, Partition partition, Action action, ThreadScope threadScope) {
	}

	@Override
	public byte getListenerType() {
		return TYPE_THREADSCOPE;
	}
}
