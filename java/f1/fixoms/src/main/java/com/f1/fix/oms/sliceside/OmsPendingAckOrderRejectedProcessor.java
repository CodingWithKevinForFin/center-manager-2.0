package com.f1.fix.oms.sliceside;

import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.OmsUtils;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.schema.OmsOrderStatus;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.fix.oms.schema.Slice;
import com.f1.pofo.fix.FixStatusReport;
import com.f1.pofo.fix.child.FixChildStatusReport;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsNotification;

/**
 * 
 * process a order reject message for a new child order request
 * 
 */
public class OmsPendingAckOrderRejectedProcessor extends OmsAbstractProcessor {

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		Slice slice = (Slice) action.getOrder();
		FixStatusReport report = (FixStatusReport) action.getFixMsgEvent();
		OmsUtils.transitionSliceTo(slice, OmsOrderStatus.REJECTED);
		slice.getFixOrder().setText(((FixChildStatusReport) action.getFixMsgEvent()).getText());
		slice.getFixOrder().setRevision(slice.getFixOrder().getRevision() + 1);
		slice.setExchLeaves(0);
		action.setChildOrder(slice);
		action.setOrder(slice.getParentOrder());
		action.setOrderAction(OmsAction.CHILD_REJECTED);
		toRootStateMachine.send(action, threadScope);

		// Notification can be sent on the slice order, in which case the remote
		// side needs to know the ID mapping
		OmsNotification notification = newNotification(state);
		notification.setType(OmsAction.CHILD_REJECTED);
		toOMSClient.send(notification, threadScope);
	}

}
