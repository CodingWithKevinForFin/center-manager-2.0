/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.utils;

import com.f1.base.Action;
import com.f1.container.OutputPort;
import com.f1.container.PartitionResolver;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;

public class ChainProcessor<A extends Action, S extends State> extends BasicProcessor<A, S> {

	public final OutputPort<A> outputPort;

	public ChainProcessor(Class<A> actionType, Class<S> stateType, PartitionResolver<? super A> resolver) {
		super(actionType, stateType, resolver);
		outputPort = newOutputPort(actionType);
		outputPort.setConnectionOptional(true);
	}

	public ChainProcessor(Class<A> actionType, Class<S> stateType) {
		super(actionType, stateType);
		outputPort = newOutputPort(actionType);
		outputPort.setConnectionOptional(true);
	}

	@Override
	public void processAction(A action, S state, ThreadScope theadLocal) throws Exception {
		if (outputPort.isConnected())
			outputPort.send(action, theadLocal);
	}

}
