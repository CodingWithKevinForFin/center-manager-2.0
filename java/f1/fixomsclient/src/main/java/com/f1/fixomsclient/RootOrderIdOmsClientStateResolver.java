package com.f1.fixomsclient;

import com.f1.container.impl.AbstractPartitionResolver;
import com.f1.pofo.oms.OmsNotification;

public class RootOrderIdOmsClientStateResolver extends AbstractPartitionResolver<OmsNotification> {

	@Override
	public Class<OmsNotification> getActionType() {
		return OmsNotification.class;
	}

	@Override
	public Object getPartitionId(OmsNotification action) {
		return action.getRootOrderID();
	}

}
