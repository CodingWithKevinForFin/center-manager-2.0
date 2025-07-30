package com.f1.fix.oms.clientside;

import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.OmsUtils;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.schema.ClientOrder;
import com.f1.fix.oms.schema.OmsOrderStatus;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.pofo.fix.ExecType;
import com.f1.pofo.fix.FixStatusReport;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsNotification;

/**
 * 
 * process an order cancelled event for a client order
 * 
 */
public class OmsDefaultDoneForDayProcessor extends OmsAbstractProcessor {

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		ClientOrder order = state.getClientOrder();
		order.getFixOrder().setOrigRequestId(order.getFixOrder().getRequestId());
		OmsUtils.transitionTo(order, OmsOrderStatus.CANCELLED);
		order.setPending(null);
		FixStatusReport report = newStatusReport(order, ExecType.DONE_FOR_DAY);
		toFixSession.send(report, threadScope);
		order.getFixOrder().setRevision(order.getFixOrder().getRevision() + 1);
		OmsNotification notification = newNotification(state);
		notification.setType(OmsAction.DONE_FOR_DAY);
		toOMSClient.send(notification, threadScope);
	}

}
