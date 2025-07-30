package com.f1.fix.oms.clientside;

import com.f1.base.DateNanos;
import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.OmsUtils;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.plugin.ParentAckedPlugin;
import com.f1.fix.oms.schema.ClientOrder;
import com.f1.fix.oms.schema.OmsOrderStatus;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.pofo.fix.ExecType;
import com.f1.pofo.fix.FixMsg;
import com.f1.pofo.fix.FixStatusReport;
import com.f1.pofo.fix.OrdStatus;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsNotification;
import com.f1.utils.LH;

/**
 * 
 * process a new client order received request
 * 
 */
public class OmsUninitializedNewOrderRcvdProcessor extends OmsAbstractProcessor {

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		FixMsg event = action.getFixMsgEvent();
		ClientOrder order = (ClientOrder) action.getOrder();
		if (state.getClientOrder() != null) {

			if (event.getPosDup() || event.getPosResend()) {
				FixStatusReport report = newStatusReport(order, ExecType.ACKNOWLEDGED);
				report.setOrdStatus(OrdStatus.ACKED.mask);
				report.setExecTransType(3);
                                if(plugin!=null)
				  plugin.onParentAcked(order.getFixOrder(), report);
				toFixSession.send(report, threadScope);
				LH.info(log, "Sending status for pos-resend: ", action);
				return;
			}
			LH.severe(log, "Duplicate order received ", event.toString());
			order.getFixOrder().getPassThruTags().put(103, "6"); // Duplicate
																	// clordid;
			action.setText("Duplicate ClOrdId");
			action.setOrderAction(OmsAction.REJECT_ORDER);
			toRootStateMachine.send(action, threadScope);
		} else {
			// TODO:Validate Security and add it to the order
			state.setClientOrder(order);
			state.addOrder(order);
			DateNanos now = getTools().getNowNanoDate();
			order.getFixOrder().setCreatedTime(now);
			order.getFixOrder().setUpdatedTime(now);
			order.getFixOrder().setRevision(0);
			order.getFixOrder().setOrderGroupId(order.getFixOrder().getId());
			order.getFixOrder().setRequestId(order.getFixOrder().getRequestId());
			OmsUtils.transitionTo(order, OmsOrderStatus.PENDING_ACK);
			FixStatusReport pendingAck = newStatusReport(order, ExecType.PENDING_NEW);
			toFixSession.send(pendingAck, threadScope);

			OmsNotification notification = newNotification(state);
			notification.setType(OmsAction.NEW_ORDER_RCVD);
			toOMSClient.send(notification, threadScope);
		}
	}

	private ParentAckedPlugin plugin;
	public void start() {
		this.plugin = super.getOmsPlugin().getParentAckedPlugin();
		super.start();
	}

}
