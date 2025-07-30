package com.f1.bootstrap.appmonitor;

import java.util.concurrent.atomic.AtomicLong;

import com.f1.base.Action;
import com.f1.container.Partition;
import com.f1.container.ProcessActionListener;
import com.f1.container.Processor;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.povo.f1app.F1AppPartition;

public class AppMonitorPartitionListener extends AbstractAppMonitorObjectListener<F1AppPartition, Partition> implements ProcessActionListener {

	volatile private long processEventsCount = 0;
	volatile private long thrownEventsCount = 0;
	volatile private long queueEventsPoped = 0;
	private long partitionControllerId;
	private final AtomicLong queueEventsPushed = new AtomicLong(0);

	public AppMonitorPartitionListener(AppMonitorState state, Partition partition, long partitionControllerId) {
		super(state, partition);
		this.partitionControllerId = partitionControllerId;
	}

	@Override
	public void onProcessAction(Processor processor, Partition partition, Action action, State state, ThreadScope threadScope, boolean isDispatch) {
		processEventsCount++;
		if (isDispatch)
			queueEventsPoped++;
		flagChanged();
	}

	@Override
	public void onHandleThrowable(Processor processor, Partition partition, Action action, State state, ThreadScope thread, Throwable thrown) {
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
	public Class<F1AppPartition> getAgentType() {
		return F1AppPartition.class;
	}
	@Override
	public void onProcessActionDone(Processor processor, Partition partition, Action action, State state, ThreadScope threadScope, boolean isDispatch) {
	}

	@Override
	protected void populate(Partition source, F1AppPartition sink) {
		sink.setContainerScopeId(source.getContainerScopeUid());
		sink.setPartitionControllerId(partitionControllerId);
		sink.setPartitionId(source.getPartitionId());
		sink.setThreadPoolKey(source.getThreadPoolKey());
		sink.setStartedMs(source.getStartTimeMs());
		sink.setThrownStats(getThrownEventsCount());
		sink.setProcessStats(getProcessEventsCount());
		sink.setQueuePopStats(queueEventsPoped);
		sink.setQueuePushStats(queueEventsPushed.get());
	}

	@Override
	public void onQueueAction(Processor processor, Partition partition, Action action, ThreadScope threadScope) {
		queueEventsPushed.incrementAndGet();
		flagChanged();
	}

	@Override
	public byte getListenerType() {
		return TYPE_PARTITION;
	}

}
