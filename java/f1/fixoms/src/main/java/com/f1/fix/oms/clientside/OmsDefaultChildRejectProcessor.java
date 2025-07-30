package com.f1.fix.oms.clientside;

import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.schema.OmsOrder;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsNotification;

/**
 * 
 * process the rejection of a child for a client order
 * 
 */
public class OmsDefaultChildRejectProcessor extends OmsAbstractProcessor {

	public void start() {
		super.start();
	}

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		OmsOrder order = action.getOrder();
		OmsOrder slice = action.getChildOrder();
		order.setExchLeaves(order.getExchLeaves() - slice.getFixOrder().getOrderQty());
		if (state.isCancellingChildOrders() && order.getExchLeaves() <= 0) {
			OmsNotification notif = newNotification(state);
			notif.setType(OmsAction.ALL_CHILDREN_CANCELLED);
			toOMSClient.send(notif, threadScope);
			state.setCancellingChildOrders(false);
		}
	}
}
