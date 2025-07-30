package com.f1.exchSim;

import com.f1.container.impl.BasicPartitionResolver;
import com.f1.pofo.oms.OmsClientAction;

public class TestOCAResolver extends BasicPartitionResolver<OmsClientAction>{

	public TestOCAResolver() {
		super(OmsClientAction.class, null);
	}

	@Override
	public Object getPartitionId(OmsClientAction action) {
		return action.getRootOrderID();
	}

}
