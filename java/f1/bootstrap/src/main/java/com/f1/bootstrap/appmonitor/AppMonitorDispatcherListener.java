package com.f1.bootstrap.appmonitor;

import java.util.concurrent.atomic.AtomicLong;

import com.f1.base.Action;
import com.f1.container.DispatchController;
import com.f1.container.Partition;
import com.f1.container.ProcessActionListener;
import com.f1.container.Processor;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.povo.f1app.F1AppDispatcher;

public class AppMonitorDispatcherListener extends AbstractAppMonitorObjectListener<F1AppDispatcher, DispatchController> implements ProcessActionListener {

	private AtomicLong processEventsCount = new AtomicLong();
	private AtomicLong thrownEventsCount = new AtomicLong();

	public AppMonitorDispatcherListener(AppMonitorState state, DispatchController dispatchController) {
		super(state, dispatchController);
	}

	@Override
	public void onProcessAction(Processor processor, Partition partition, Action action, State state, ThreadScope threadScope, boolean isDispatch) {
		processEventsCount.incrementAndGet();
		flagChanged();
	}

	@Override
	public void onHandleThrowable(Processor processor, Partition partition, Action action, State state, ThreadScope thread, Throwable thrown) {
		thrownEventsCount.incrementAndGet();
		flagChanged();
	}

	public long getProcessEventsCount() {
		return processEventsCount.get();
	}

	public long getThrownEventsCount() {
		return thrownEventsCount.get();
	}
	@Override
	public Class<F1AppDispatcher> getAgentType() {
		return F1AppDispatcher.class;
	}

	@Override
	protected void populate(DispatchController source, F1AppDispatcher sink) {
		AppMonitorContainerScopeListener.populateContainerScope(source, sink);
		sink.setThrownStats(getThrownEventsCount());
		sink.setProcessStats(getProcessEventsCount());
		sink.setDispatchStats(0);//TODO
		sink.setForwardStats(0);//TODO
	}

	@Override
	public void onProcessActionDone(Processor processor, Partition partition, Action action, State state, ThreadScope threadScope, boolean isDispatch) {
	}

	@Override
	public void onQueueAction(Processor processor, Partition partition, Action action, ThreadScope threadScope) {

	}

	@Override
	public byte getListenerType() {
		return TYPE_DISPATCHER;
	}
}
