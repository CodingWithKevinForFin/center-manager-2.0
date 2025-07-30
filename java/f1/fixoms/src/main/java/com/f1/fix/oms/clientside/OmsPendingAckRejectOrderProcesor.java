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
 * process a client pending ack reject for a client order.
 */
public class OmsPendingAckRejectOrderProcesor extends OmsAbstractProcessor {

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		ClientOrder order = (ClientOrder) action.getOrder();
		order.getFixOrder().setRevision(order.getFixOrder().getRevision() + 1);
		OmsUtils.transitionTo(order, OmsOrderStatus.REJECTED);
		FixStatusReport reject = newStatusReport(order, ExecType.REJECTED);
		if (action.getText() != null)
			reject.setText(action.getText());
		toFixSession.send(reject, threadScope);

		OmsNotification notification = newNotification(state);
		notification.setType(OmsAction.REJECT_ORDER);
		toOMSClient.send(notification, threadScope);
	}

}
