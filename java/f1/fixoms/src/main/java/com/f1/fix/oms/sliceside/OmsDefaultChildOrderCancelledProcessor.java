package com.f1.fix.oms.sliceside;

import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.OmsUtils;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.schema.OmsOrderStatus;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.fix.oms.schema.Slice;
import com.f1.pofo.fix.child.FixChildStatusReport;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsNotification;
import com.f1.pofo.oms.TimeInForce;
import com.f1.utils.LH;

/**
 * process cancel accepted messages for child orders
 */
public class OmsDefaultChildOrderCancelledProcessor extends OmsAbstractProcessor {

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		Slice order = (Slice) action.getOrder();

		// If received ID is lower than current ID, process ANYWAY but complain
		// in log message

		FixChildStatusReport report = (FixChildStatusReport) action.getFixMsgEvent();

		int recvdRevision = report.getChildId().getRequestId();
		int currentRevision = order.getFixRevisionID();

		if (currentRevision <= recvdRevision) {
			order.setFixRevisionID(recvdRevision);
		} else {
			// TODO: How do we notify error and stop processing
			TimeInForce tif = order.getFixOrder().getTimeInForce();
			LH.warning(log, "Expired CANCELED Message Received for Child. WILL PROCESS ANYWAY ", action);
		}

		OmsUtils.transitionSliceTo(order, OmsOrderStatus.CANCELLED);
		order.setExchLeaves(0);
		order.getFixOrder().setRevision(order.getFixOrder().getRevision() + 1);
		action.setChildOrder(order);
		action.setOrder(order.getParentOrder());
		action.setOrderAction(OmsAction.CHILD_CANCEL_SUCCEEDED);
		toRootStateMachine.send(action, threadScope);
		OmsNotification notification = newNotification(state);
		notification.setType(OmsAction.CHILD_CANCEL_SUCCEEDED);
		toOMSClient.send(notification, threadScope);
	}

}
