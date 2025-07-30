package com.f1.container.impl.dispatching;

import com.f1.base.Action;
import com.f1.container.Port;
import com.f1.container.Processor;
import com.f1.container.impl.BasicDispatcherController;

public class ActionProcessorRunnable implements Runnable {

	private Action action;
	private Processor processor;
	private BasicDispatcherController bdc;
	private Port sourcePort;

	public ActionProcessorRunnable(BasicDispatcherController basicDispatcherController, Port optionalSourcePort, Processor processor, Action action) {
		this.sourcePort = optionalSourcePort;
		this.processor = processor;
		this.action = action;
		this.bdc = basicDispatcherController;
	}

	@Override
	public void run() {
		bdc.safelyProcess(sourcePort, processor, action, null, null, true);
	}

}
