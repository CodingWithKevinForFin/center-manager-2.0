/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import com.f1.base.Action;
import com.f1.container.PartitionResolver;
import com.f1.container.RequestInputPort;
import com.f1.container.RequestMessage;
import com.f1.container.RequestProcessor;
import com.f1.container.State;

public abstract class AbstractRequestProcessor<A extends Action, S extends State, R extends Action> extends BasicProcessor<RequestMessage<A>, S>
		implements
			RequestProcessor<A, S, R> {

	private Class<A> innerActionType;
	private Class<R> responseType;

	public AbstractRequestProcessor(Class<A> innerActionType, Class<S> stateType, Class<R> responseType, PartitionResolver<A> resolver) {
		super((Class) RequestMessage.class, stateType);
		if (resolver != null)
			setPartitionResolver(new RequestPartitionResolver<A>(resolver));
		clearInputPorts();
		this.innerActionType = innerActionType;
		this.responseType=responseType;
		this.inputPort=createInputPort();
	}
	public AbstractRequestProcessor(Class<A> innerActionType, Class<S> stateType, Class<R> responseType) {
		this(innerActionType, stateType, responseType, null);
	}

	public Class<A> getInnerActionType() {
		return innerActionType;
	}
	@Override
	public Class<A> getRequestType() {
		return innerActionType;
	}
	@Override
	public Class<R> getResponseType() {
		return responseType;
	}
	@Override
	public RequestInputPort<A, R> getInputPort() {
		return (RequestInputPort<A, R>) super.getInputPort();
	}

	@Override
	protected RequestInputPort<A, R> createInputPort() {
		RequestInputPort<A, R> r = new BasicRequestInputPort<A, R>(innerActionType, responseType, this, this);
		addInputPort(r);
		return r;
	}
}
