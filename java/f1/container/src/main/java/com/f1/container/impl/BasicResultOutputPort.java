package com.f1.container.impl;

import com.f1.base.Action;
import com.f1.container.Connectable;
import com.f1.container.ResultMessage;
import com.f1.container.ResultOutputPort;

public class BasicResultOutputPort<RES extends Action> extends BasicOutputPort<ResultMessage<RES>> implements ResultOutputPort<RES> {

	final private Class<RES> innerType;

	public BasicResultOutputPort(Class<RES> actionType, Connectable parent) {
		super((Class) ResultMessage.class, parent);
		this.innerType = actionType;
	}

	@Override
	public Class<RES> getResponseActionType() {
		return innerType;
	}

	public BasicResultOutputPort<RES> setName(String name) {
		super.setName(name);
		return this;
	}

}
