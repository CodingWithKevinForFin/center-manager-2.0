package com.f1.fix.oms.clientside;

import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.OmsUtils;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.schema.OmsOrder;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.pofo.fix.FixOrderReplaceReject;
import com.f1.pofo.fix.FixOrderReplaceRequest;
import com.f1.pofo.fix.FixRequest;
import com.f1.pofo.fix.MsgType;
import com.f1.pofo.fix.OrdStatus;
import com.f1.pofo.fix.VersionedMsg;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsNotification;
import com.f1.pofo.oms.Order;

/**
 * 
 * process the reject of a replace for a client order
 * 
 */
public class OmsDefaultRejectReplaceProcessor extends OmsAbstractProcessor {

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		FixOrderReplaceReject rej = nw(FixOrderReplaceReject.class);
		rej.setType(MsgType.CANCEL_REJECT);
		OmsNotification notif = null;
		if (action.getOrder() != null) {
			rej.setSessionName(action.getOrder().getFixOrder().getSessionName());
			Order order = action.getOrder().getFixOrder();
			if (action.getFixMsgEvent() == null || action.getFixMsgEvent() == action.getOrder().getPending()) {// we
																												// are
																												// rejecting
																												// the
																												// request
																												// that
																												// was
																												// pending,revert
																												// status
				OmsUtils.removePendingCxlRpl(action.getOrder());
				order.setRevision(order.getRevision() + 1);
				notif = newNotification(state);
			}
			rej.setOrderID(action.getOrder().getFixOrder().getId());
			rej.setOrderStatus(action.getOrder().getFixOrder().getOrderStatus());
			rej.setReason(getReason(action.getOrder()));
			rej.setText(action.getOrder().getFixOrder().getText());
			action.getOrder().getFixOrder().setText(null);
		} else if (action.getFixMsgEvent() instanceof FixOrderReplaceRequest) {
			FixOrderReplaceRequest frr = (FixOrderReplaceRequest) action.getFixMsgEvent();
			rej.setSessionName(action.getFixMsgEvent().getSessionName());
			rej.setOrderID(frr.getRequestId());
			rej.setOrderStatus(OrdStatus.REJECTED.getIntMask());
			rej.setReason(1);
		} else {
			rej.setSessionName(action.getFixMsgEvent().getSessionName());
			rej.setOrderID("-1");
			rej.setOrderStatus(OrdStatus.REJECTED.getIntMask());
			rej.setReason(1);
		}
		FixRequest event = (FixRequest) action.getFixMsgEvent();
		if (action.getText() != null || rej.getText() == null)
			rej.setText(action.getText());
		if (event == null && action.getOrder() != null)
			event = action.getOrder().getPending();
		if (event != null) {//TODO: is this correct??
			rej.setRequestID(event.getRequestId());
			rej.setRefId(((VersionedMsg) event).getRefId());
			rej.setResponseTo(2);
			toFixSession.send(rej, threadScope);
			if (action.getOrder() != null)
				action.getOrder().setPending(null);
		}

		if (notif != null) {
			notif.setType(OmsAction.REPLACE_REJECTED);
			toOMSClient.send(notif, threadScope);
		}
	}
	private int getReason(OmsOrder order) {
		int status = order.getOrderStatus();
		if (OmsUtils.isPending(status)) {
			return 3;
		}
		Order state = order.getFixOrder();
		if (state.getTotalExecQty() == order.getFixOrder().getOrderQty()) {
			return 0;
		} else
			return 2;
	}
}
