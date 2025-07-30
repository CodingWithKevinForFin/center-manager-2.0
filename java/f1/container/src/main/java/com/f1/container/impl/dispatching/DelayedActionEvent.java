/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl.dispatching;

import java.util.logging.Logger;

import com.f1.base.Action;
import com.f1.container.DispatchController;
import com.f1.container.Port;
import com.f1.container.Processor;
import com.f1.utils.LH;

public class DelayedActionEvent implements Runnable {
	private static final Logger log = Logger.getLogger(DelayedActionEvent.class.getName());

	private DispatchController controller;
	private Processor<?, ?> processor;
	private Object partitionId;
	private Action action;
	private Port<?> sourcePort;
	private long expectedRunTime;

	public DelayedActionEvent(Port<?> sourcePort, DispatchController controller, Processor<?, ?> processor, Action action, Object partitionId, long expectedRunTime) {
		this.controller = controller;
		this.processor = processor;
		this.action = action;
		this.partitionId = partitionId;
		this.sourcePort = sourcePort;
		this.expectedRunTime = expectedRunTime;
	}

	@Override
	public void run() {
		try {
			controller.dispatch(sourcePort, processor, action, partitionId, null);
		} catch (Throwable t) {
			LH.severe(log, "Could not process delayed event: ", action, t);
		}
	}

	public long getExpectedRunTime() {
		return expectedRunTime;
	}

}
