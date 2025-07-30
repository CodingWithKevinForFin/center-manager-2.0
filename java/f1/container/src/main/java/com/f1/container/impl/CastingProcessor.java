package com.f1.container.impl;

import com.f1.base.Action;
import com.f1.container.OutputPort;
import com.f1.container.State;
import com.f1.container.ThreadScope;

public class CastingProcessor<A extends Action, B extends Action> extends BasicProcessor<A, State> {

	private final OutputPort<B> output;
	private final Class<B> castToType;

	public CastingProcessor(Class<A> actionType, Class<B> castToType) {
		super(actionType, null);
		this.castToType = castToType;
		output = newOutputPort(castToType);
	}

	@Override
	public void processAction(A action, State state, ThreadScope threadScope) throws Exception {
		getOutput().send((B) action, threadScope);
	}

	public OutputPort<B> getOutput() {
		return output;
	}

	public Class<B> getCastToType() {
		return castToType;
	}

}
