package com.f1.fix.oms.clientside;

import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.OmsUtils;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.schema.ClientOrder;
import com.f1.fix.oms.schema.OmsOrderStatus;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.pofo.fix.ExecType;
import com.f1.pofo.fix.FixCancelRequest;
import com.f1.pofo.fix.FixStatusReport;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsNotification;

/**
 * 
 * process a cancel request for a client order
 * 
 */
public class OmsDefaultCancelOrderProcessor extends OmsAbstractProcessor {

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		ClientOrder order = state.getClientOrder();
		FixCancelRequest cancel = (FixCancelRequest) action.getFixMsgEvent();
		FixStatusReport report;
		cancel.setDestination(order.getFixOrder().getDestination());

		order.setPending(cancel); // Store the request
		order.getFixOrder().setRevision(order.getFixOrder().getRevision() + 1);
		OmsUtils.transitionTo(order, OmsOrderStatus.PENDING_CXL);
		report = newStatusReport(order, ExecType.PENDING_CANCEL);
		toFixSession.send(report, threadScope);

		OmsNotification notification = newNotification(state);
		notification.setType(OmsAction.CANCEL_ORDER);
		toOMSClient.send(notification, threadScope);
	}

}
