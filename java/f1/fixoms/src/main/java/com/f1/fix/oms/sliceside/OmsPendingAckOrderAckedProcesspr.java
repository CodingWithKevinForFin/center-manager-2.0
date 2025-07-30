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

/**
 * 
 * process a pending ack for a child order
 * 
 */
public class OmsPendingAckOrderAckedProcesspr extends OmsAbstractProcessor {

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		Slice slice = (Slice) action.getOrder();
		FixChildStatusReport report = (FixChildStatusReport) action.getFixMsgEvent();
		if (slice.getFixRevisionID() < report.getChildId().getRequestId()) {
			slice.setFixRevisionID(report.getChildId().getRequestId());
		} else {
			// TODO: log error and DK
		}
		slice.getFixOrder().setExternalOrderId(report.getOrderId());
		OmsUtils.transitionSliceTo(slice, OmsOrderStatus.ACKED);
		slice.getFixOrder().setRevision(slice.getFixOrder().getRevision() + 1);
		action.setChildOrder(slice);
		action.setOrder(slice.getParentOrder());
		action.setOrderAction(OmsAction.CHILD_ORDER_ACKNOWLEDGED);
		toRootStateMachine.send(action, threadScope);
		// Notification can be sent on the slice order, in which case the remote
		// side needs to know the ID mapping
		OmsNotification notification = newNotification(state);
		notification.setType(OmsAction.CHILD_ORDER_ACKNOWLEDGED);
		toOMSClient.send(notification, threadScope);
	}
}
