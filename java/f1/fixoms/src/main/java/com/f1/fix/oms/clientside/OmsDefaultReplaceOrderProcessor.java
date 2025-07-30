package com.f1.fix.oms.clientside;

import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.OmsUtils;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.schema.ClientOrder;
import com.f1.fix.oms.schema.OmsOrderStatus;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.pofo.fix.ExecType;
import com.f1.pofo.fix.FixOrderReplaceRequest;
import com.f1.pofo.fix.FixStatusReport;
import com.f1.pofo.fix.OrdStatus;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsNotification;
import com.f1.pofo.oms.Order;
import com.f1.utils.MH;

/**
 * 
 * process a client order replaced
 * 
 */
public class OmsDefaultReplaceOrderProcessor extends OmsAbstractProcessor {

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		ClientOrder order = state.getClientOrder();
		FixOrderReplaceRequest replace = (FixOrderReplaceRequest) action.getFixMsgEvent();
		if (!validate(replace, order.getFixOrder())) {
			action.setOrderAction(OmsAction.REPLACE_REJECTED);
			toRootStateMachine.send(action, threadScope);
			return;
		}
		FixStatusReport report;
		OmsNotification notification = newNotification(state);
		order.setPending(replace);
		OmsUtils.transitionTo(order, OmsOrderStatus.PENDING_RPL);
		order.getFixOrder().setRevision(order.getFixOrder().getRevision() + 1);

		report = newStatusReport(order, ExecType.PENDING_REPLACE);
		toFixSession.send(report, threadScope);

		notification.setType(OmsAction.REPLACE_ORDER);
		toOMSClient.send(notification, threadScope);
	}

	private boolean validate(FixOrderReplaceRequest replace, Order fixOrder) {
		if (!replace.getRefId().equals(fixOrder.getRequestId())) {
			fixOrder.setText("Old ClOrdID used as Orig, Current : " + fixOrder.getRequestId());
			return false;
		}
		if (replace.getOrderInfo().getOrderQty() < fixOrder.getTotalExecQty()) {
			fixOrder.setText("can not reduce quantity below filled quantity of " + fixOrder.getTotalExecQty());
			return false;
		}
		if (!fixOrder.getSymbol().equalsIgnoreCase(replace.getSymbol())) {
			fixOrder.setText("symbol mismatch");
			return false;
		}
		if (replace.getOrderInfo().getOrderQty() == fixOrder.getTotalExecQty() && MH.anyBits(fixOrder.getOrderStatus(), OrdStatus.FILLED.mask)) {
			fixOrder.setText("Can not modify FILLED order without quantity increase");
			return false;
		}
		return true;
	}

}
