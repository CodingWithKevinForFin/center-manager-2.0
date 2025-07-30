package com.f1.container.impl;

import com.f1.base.Action;
import com.f1.container.Connectable;
import com.f1.container.Processor;
import com.f1.container.RequestInputPort;
import com.f1.container.RequestMessage;

public class BasicRequestInputPort<REQ extends Action, RES extends Action> extends BasicInputPort<RequestMessage<REQ>> implements RequestInputPort<REQ, RES> {

	final private Class<RES> responseType;
	final private Class<REQ> requestType;

	public BasicRequestInputPort(Class<REQ> requestType, Class<RES> responseType, Connectable parent) {
		super((Class) RequestMessage.class, parent);
		this.requestType = requestType;
		this.responseType = responseType;
		initName();
	}

	public BasicRequestInputPort(Class<REQ> requestType, Class<RES> responseType, Connectable parent, Processor<RequestMessage<REQ>, ?> processor) {
		super((Class) RequestMessage.class, processor);
		this.requestType = requestType;
		this.responseType = responseType;
		initName();
	}
	@Override
	public Class<REQ> getRequestActionType() {
		return this.requestType;
	}

	@Override
	public Class<RES> getResponseActionType() {
		return this.responseType;
	}

	protected void initName() {
		if (requestType != null)
			setName(requestType.getSimpleName() + "RequestInputPort");
	}
}
