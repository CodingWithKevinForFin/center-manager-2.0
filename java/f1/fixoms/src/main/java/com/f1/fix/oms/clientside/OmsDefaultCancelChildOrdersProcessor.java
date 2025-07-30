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
 * process a cancel request for a child of a client order
 * 
 */
public class OmsDefaultCancelChildOrdersProcessor extends OmsAbstractProcessor {

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {

		for (OmsOrder o : state.getSlices()) {
			if (o.getExchLeaves() > 0) {
				action.setOrder(o);
				action.setOrderAction(OmsAction.CANCEL_CHILD_ORDER);
				action.getClientMsg().setOrderID(o.getFixOrder().getId());
				toRootStateMachine.send(action, threadScope);
				state.setCancellingChildOrders(true);
			}
		}
		if (!state.isCancellingChildOrders()) {
			OmsNotification notif = newNotification(state);
			notif.setType(OmsAction.ALL_CHILDREN_CANCELLED);
			toOMSClient.send(notif, threadScope);
		}
	}

}
