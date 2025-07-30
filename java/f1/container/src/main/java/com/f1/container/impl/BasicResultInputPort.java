package com.f1.container.impl;

import com.f1.base.Action;
import com.f1.container.Connectable;
import com.f1.container.Processor;
import com.f1.container.ResultInputPort;
import com.f1.container.ResultMessage;

public class BasicResultInputPort<RES extends Action> extends BasicInputPort<ResultMessage<RES>> implements ResultInputPort<RES> {

	final private Class<RES> responseType;

	public BasicResultInputPort(Class<RES> responseType, Connectable parent) {
		super((Class) ResultMessage.class, parent);
		this.responseType = responseType;
		initName();
	}

	public BasicResultInputPort(Class<RES> responseType, Connectable parent, Processor<ResultMessage<RES>, ?> processor) {
		super((Class) ResultMessage.class, processor);
		this.responseType = responseType;
		initName();
	}

	@Override
	public Class<RES> getResponseActionType() {
		return this.responseType;
	}

	protected void initName() {
		if (responseType != null)
			setName(responseType.getSimpleName() + "ResultInputPort");
	}
}
