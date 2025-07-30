package com.sjls.f1.start.oms;

import com.f1.container.impl.BasicPartitionResolver;
import com.f1.pofo.oms.OmsClientAction;

public class OmsClientActionResolver extends BasicPartitionResolver<OmsClientAction>{

	public OmsClientActionResolver() {
		super(OmsClientAction.class, null);
	}

	@Override
	public Object getPartitionId(OmsClientAction action) {
		//TODO: Check for already existing partition
		return action.getRootOrderID();
	}

}
