package com.f1.fix.oms.sliceside;

import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.OmsUtils;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.schema.FixCopyUtil;
import com.f1.fix.oms.schema.OmsOrderStatus;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.fix.oms.schema.Slice;
import com.f1.pofo.fix.child.FixChildStatusReport;
import com.f1.pofo.oms.ChildOrderRequest;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsNotification;
import com.f1.pofo.oms.Order;
import com.f1.utils.LH;

/**
 * process cancel-replace accepted messages for child orders
 */
public class OmsDefaultChildOrderReplacedProcessor extends OmsAbstractProcessor {

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		Slice order = (Slice) action.getOrder();
		ChildOrderRequest pendingReplace = order.getPendingChildReplace();

		// We need to get the current revision FROM the action that was passed
		// in. This is because there may be intermediate requests that failed.
		FixChildStatusReport report = (FixChildStatusReport) action.getFixMsgEvent();

		// ID management for FIX Outbound
		int recvdRevision = report.getChildId().getRequestId();
		int currentRevision = order.getFixRevisionID();

		if (currentRevision < recvdRevision) {
			order.setFixRevisionID(recvdRevision);
		} else {
			// TODO: How do we notify error and stop processing
			LH.warning(log, "Expired Status Message Received for Child ", action);
			return;
		}
		OmsUtils.transitionSliceTo(order, OmsOrderStatus.REPLACED);
		order.setExchLeaves(0); // TODO: This needs to be fixed

		order.setFixRevisionID(((FixChildStatusReport) action.getFixMsgEvent()).getChildId().getRequestId());
		Order fo = order.getFixOrder();
		final int qtyIncrease = pendingReplace.getOrderQty() - order.getFixOrder().getOrderQty();
		if (qtyIncrease < 0)
			order.getParentOrder().setExchLeaves(order.getParentOrder().getExchLeaves() + qtyIncrease);
		final int remainingQuantity = order.getParentOrder().getFixOrder().getOrderQty() - order.getParentOrder().getExchLeaves()
				- order.getParentOrder().getFixOrder().getTotalExecQty();
		log.info("LIMITS: ------------------- " + remainingQuantity);//TODO:REMOVE 
		log.info("LIMITS: Remaining Quantity: " + remainingQuantity);//TODO:REMOVE 
		log.info("LIMITS: Quantity Increase: " + qtyIncrease);//TODO:REMOVE 
		log.info("LIMITS: Exchange Leaves: " + order.getParentOrder().getExchLeaves());//TODO:REMOVE 
		FixCopyUtil.copy(pendingReplace, fo);

		// ID management for OFR
		order.getFixOrder().setOrigRequestId(order.getFixOrder().getRequestId());
		order.getFixOrder().setUpdatedTime(getTools().getNowNanoDate());
		order.getFixOrder().setRevision(order.getFixOrder().getRevision() + 1);
		order.getFixOrder().setRequestId(pendingReplace.getRequestId());

		action.setChildOrder(order);
		action.setOrder(order.getParentOrder());
		action.setOrderAction(OmsAction.CHILD_REPLACE_SUCCEEDED);
		toRootStateMachine.send(action, threadScope);
		OmsNotification notification = newNotification(state);
		notification.setType(OmsAction.CHILD_REPLACE_SUCCEEDED);
		toOMSClient.send(notification, threadScope);
	}

}
