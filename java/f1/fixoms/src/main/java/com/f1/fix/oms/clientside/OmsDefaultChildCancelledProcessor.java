package com.f1.fix.oms.clientside;

import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.schema.OmsOrder;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsNotification;

/**
 * 
 * process the cancel of a child to its client order
 * 
 */
public class OmsDefaultChildCancelledProcessor extends OmsAbstractProcessor {
	ObjectGeneratorForClass<Execution> execg;

	public void start() {
		super.start();
		execg = getGenerator(Execution.class);
	}

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		OmsOrder order = action.getOrder();
		order.setExchLeaves(order.getExchLeaves() - Math.max(0, action.getChildOrder().getFixOrder().getOrderQty() - action.getChildOrder().getFixOrder().getTotalExecQty()));

		if (state.isCancellingChildOrders() && order.getExchLeaves() <= 0) {
			OmsNotification notif = newNotification(state);
			notif.setType(OmsAction.ALL_CHILDREN_CANCELLED);
			toOMSClient.send(notif, threadScope);
			state.setCancellingChildOrders(false);
		}
	}
}
