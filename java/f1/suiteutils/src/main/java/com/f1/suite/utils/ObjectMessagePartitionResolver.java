package com.f1.suite.utils;

import com.f1.container.impl.AbstractPartitionResolver;
import com.f1.povo.standard.ObjectMessage;

public class ObjectMessagePartitionResolver extends AbstractPartitionResolver<ObjectMessage> {

	@Override
	public Class<ObjectMessage> getActionType() {
		return ObjectMessage.class;
	}

	@Override
	public Object getPartitionId(ObjectMessage action) {
		return action.getObject();
	}

}
