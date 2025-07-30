/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.utils;

import com.f1.base.Action;
import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.container.impl.BasicState;

public class MulticastProcessor<A extends Action> extends BasicProcessor<A, BasicState> {

	public MulticastProcessor(Class<A> actionType) {
		super(actionType, BasicState.class);
	}

	@Override
	public void processAction(A action, BasicState state, ThreadScope threadScope) {
		for (OutputPort<?> p : getOutputs())
			((OutputPort<A>) p).send(action, threadScope);
	}

	public OutputPort<A> newOutputPort() {
		return super.newOutputPort(getActionType());
	}

}
