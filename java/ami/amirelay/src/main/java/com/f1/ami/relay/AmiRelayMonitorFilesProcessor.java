package com.f1.ami.relay;

import java.util.concurrent.TimeUnit;

import com.f1.base.Action;
import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;

public class AmiRelayMonitorFilesProcessor extends BasicProcessor<Action, AmiRelayMonitorFilesState> {

	OutputPort<Action> self = newOutputPort(Action.class);
	private long delay;

	public AmiRelayMonitorFilesProcessor(long delay) {
		super(Action.class, AmiRelayMonitorFilesState.class);
		this.delay = delay;
	}

	@Override
	public void startDispatching() {
		super.startDispatching();
		self.send(nw(Action.class), null);
	}

	@Override
	public void processAction(Action action, AmiRelayMonitorFilesState state, ThreadScope threadScope) throws Exception {
		state.getTransformManager().parseIfChanged(false);
		state.getRoutes().parseIfChanged(false);
		self.sendDelayed(action, threadScope, this.delay, TimeUnit.MILLISECONDS);
	}

}
