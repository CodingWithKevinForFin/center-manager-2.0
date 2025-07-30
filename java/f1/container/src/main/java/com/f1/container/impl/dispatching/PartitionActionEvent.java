/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl.dispatching;

import com.f1.base.Action;
import com.f1.base.Clearable;
import com.f1.container.Port;
import com.f1.container.Processor;
import com.f1.utils.concurrent.ConcurrentNode;

public class PartitionActionEvent<A extends Action> extends ConcurrentNode<Object> implements Clearable {

	public Processor<? super A, ?> processor;
	public A action;
	public Port<? super A> sourcePort;

	public PartitionActionEvent() {
	}

	@Override
	public void clear() {
		this.processor = null;
		this.action = null;
		this.sourcePort = null;
	}

	public void init(Port<? super A> sourcePort, Processor<? super A, ?> processor, A action) {
		this.sourcePort = sourcePort;
		this.processor = processor;
		this.action = action;

	}
}
