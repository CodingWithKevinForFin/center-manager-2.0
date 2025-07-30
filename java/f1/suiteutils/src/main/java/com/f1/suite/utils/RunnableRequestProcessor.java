package com.f1.suite.utils;

import com.f1.container.PartitionResolver;
import com.f1.container.RequestMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.povo.standard.RunnableResponseMessage;
import com.f1.utils.LH;
import com.f1.utils.MonitoredRunnable;
import com.f1.utils.concurrent.FastThreadPool;

public class RunnableRequestProcessor extends BasicRequestProcessor<RunnableRequestMessage, State, RunnableResponseMessage>
		implements PartitionResolver<RequestMessage<RunnableRequestMessage>> {

	private FastThreadPool threadpool;

	public RunnableRequestProcessor() {
		super(RunnableRequestMessage.class, State.class, RunnableResponseMessage.class);
		setPartitionResolver(this);
	}

	@Override
	public void init() {
		this.threadpool = new FastThreadPool(this.getContainer().getThreadPoolController().getMaximumPoolSizeForGeneralThreadPool(), getContainer().getName() + "-RUN-");
		this.threadpool.start();
		super.init();
	}

	@Override
	protected RunnableResponseMessage processRequest(RequestMessage<RunnableRequestMessage> action, State state, ThreadScope threadScope) throws Exception {
		RunnableRequestMessage rm = action.getAction();
		Runnable runnable = rm.getRunnable();
		MonitoredRunnable command = new MonitoredRunnable(runnable, false);
		threadpool.execute(command);
		RunnableResponseMessage r = nw(RunnableResponseMessage.class);
		try {
			if (!command.waitUntilComplete(rm.getTimeoutMs())) {
				command.interruptThreadNoThrow(true);
				r.setText("TIMEOUT_EXCEEDED");
				r.setResultCode(RunnableResponseMessage.RESULT_CODE_TIMEOUT);
			} else {
				switch (command.getState()) {
					case MonitoredRunnable.STATE_THREW_EXCEPTION:
						if (command.getThrown() != null)
							r.setText(command.getThrown().getMessage());
						r.setResultCode(RunnableResponseMessage.RESULT_CODE_THROWN);
						LH.warning(log, "Runnable threw exception: ", command.getThrown());
						r.setThrowable(command.getThrown());
						break;
					case MonitoredRunnable.STATE_COMPLETE:
						r.setResultCode(RunnableResponseMessage.RESULT_CODE_COMPLETE);
						break;
					case MonitoredRunnable.STATE_INIT:
					case MonitoredRunnable.STATE_RUNNING:
					default:
						throw new IllegalStateException("bad MonitoredRunnable state: " + command.getState());
				}
			}
		} catch (InterruptedException e) {
			command.interruptThreadNoThrow(true);
			r.setText("TIMEOUT_EXCEEDED");
			r.setResultCode(RunnableResponseMessage.RESULT_CODE_TIMEOUT);
			r.setPriority(rm.getPriority());
			LH.warning(log, "Runnable interrupted: ", e);
			return r;
		}
		r.setPriority(rm.getPriority());
		return r;
	}

	@Override
	public Object getPartitionId(RequestMessage<RunnableRequestMessage> action) {
		return action.getAction().getPartitionId();
	}
}
