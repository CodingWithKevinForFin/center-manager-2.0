package com.f1.fix.oms.sliceside;

import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.OmsUtils;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.fix.oms.schema.Slice;
import com.f1.pofo.fix.child.FixChildReplaceReject;
import com.f1.pofo.oms.ChildOrderRequest;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsNotification;

/**
 * process cancel rejected messages for child orders
 */

public class OmsDefaultCOCxlOrModifyRejectedProcessor extends OmsAbstractProcessor {

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		Slice order = (Slice) action.getOrder();
		ChildOrderRequest pendingReplace = order.getPendingChildReplace();
		FixChildReplaceReject rej = ((FixChildReplaceReject) (action.getFixMsgEvent()));
		order.getFixOrder().setText(rej.getText());
		OmsUtils.removePendingCxlRpl(order);
		order.getFixOrder().setRevision(order.getFixOrder().getRevision() + 1);
		order.getFixOrder().setUpdatedTime(getTools().getNowNanoDate());
		order.getFixOrder().setCxlRejReason(rej.getReason());
		order.getFixOrder().setCxlRejResponseTo(rej.getResponseTo());
		if (pendingReplace != null) {// alternately can check the response to on
										// the message but we don't allow
										// queueing
			final int qtyIncrease = pendingReplace.getOrderQty() - order.getFixOrder().getOrderQty();
			if (qtyIncrease > 0)
				order.getParentOrder().setExchLeaves(order.getParentOrder().getExchLeaves() - qtyIncrease);

			final int remainingQuantity = order.getParentOrder().getFixOrder().getOrderQty() - order.getParentOrder().getExchLeaves()
					- order.getParentOrder().getFixOrder().getTotalExecQty();
			OmsNotification notification = newNotification(state);
			notification.setType(OmsAction.CHILD_REPLACE_REJECTED);
			order.setPendingChildReplace(null);
			toOMSClient.send(notification, threadScope);
		} else {
			OmsNotification notification = newNotification(state);
			notification.setType(OmsAction.CHILD_CANCEL_REJECTED);
			toOMSClient.send(notification, threadScope);
		}
	}

}
