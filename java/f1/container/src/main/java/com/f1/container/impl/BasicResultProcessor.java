package com.f1.container.impl;

import com.f1.base.Action;
import com.f1.container.PartitionResolver;
import com.f1.container.ResultInputPort;
import com.f1.container.ResultMessage;
import com.f1.container.ResultProcessor;
import com.f1.container.State;

public abstract class BasicResultProcessor<A extends Action, S extends State> extends BasicProcessor<ResultMessage<A>, S> implements ResultProcessor<A, S> {

	final private Class<A> innerActionType;

	public BasicResultProcessor(Class<A> actionType, Class<S> stateType, PartitionResolver<? super ResultMessage<A>> resolver) {
		super((Class) ResultMessage.class, stateType, resolver);
		innerActionType = actionType;
	}

	public BasicResultProcessor(Class<A> actionType, Class<S> stateType) {
		super((Class) ResultMessage.class, stateType);
		innerActionType = actionType;
	}

	@Override
	public Class<A> getResultType() {
		return innerActionType;
	}

	@Override
	public ResultInputPort<A> getInputPort() {
		return (ResultInputPort<A>) super.getInputPort();
	}

	@Override
	protected ResultInputPort<A> createInputPort() {
		ResultInputPort<A> r = new BasicResultInputPort<A>(innerActionType, this, this);
		addInputPort(r);
		return r;
	}

}
