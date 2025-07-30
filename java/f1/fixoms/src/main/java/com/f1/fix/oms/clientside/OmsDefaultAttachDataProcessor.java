package com.f1.fix.oms.clientside;

import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsNotification;
import com.f1.pofo.oms.Order;

/**
 * 
 * attach custom data to a client order (see
 * {@link OmsAction#CUSTOM_DATA_UPDATED}
 * 
 */
public class OmsDefaultAttachDataProcessor extends OmsAbstractProcessor {

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		Order o = action.getOrder().getFixOrder();
		o.setUserData(action.getClientMsg().getUserData());
		OmsNotification notif = newNotification(state);
		notif.setType(OmsAction.CUSTOM_DATA_UPDATED);
		toOMSClient.send(notif, threadScope);
	}

}
