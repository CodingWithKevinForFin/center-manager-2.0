package com.f1.fix.oms.clientside;

import java.util.logging.Level;

import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.OmsUtils;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.plugin.ParentCanceledPlugin;
import com.f1.fix.oms.schema.ClientOrder;
import com.f1.fix.oms.schema.OmsOrderStatus;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.pofo.fix.ExecType;
import com.f1.pofo.fix.FixStatusReport;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsNotification;
import com.f1.utils.LH;

/**
 * 
 * process an order cancelled event for a client order
 * 
 */
public class OmsDefaultOrderCancelledProcessor extends OmsAbstractProcessor {

	private ParentCanceledPlugin plugin;

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		ClientOrder order = state.getClientOrder();
		order.getFixOrder().setOrigRequestId(order.getFixOrder().getRequestId());
		OmsUtils.transitionTo(order, OmsOrderStatus.CANCELLED);
		if (order.getPending() == null) {
			order.getFixOrder().setText("Unsolicited Cancel");
		} else {
			order.getFixOrder().setRequestId(order.getPending().getRequestId());
		}
		order.setPending(null);

		FixStatusReport report = newStatusReport(order, ExecType.CANCELLED);
		report.setText(action.getText());
		//	TODO: this would be passed into plugin: FixStatusReport orig = (FixStatusReport) action.getFixMsgEvent();
		order.getFixOrder().setRevision(order.getFixOrder().getRevision() + 1);
		OmsNotification notification = newNotification(state);
		notification.setType(OmsAction.ORDER_CANCELLED);
		toOMSClient.send(notification, threadScope);
		if (plugin != null) {
			try {
				plugin.onParentCanceled(order.getFixOrder(), report);
			} catch (Exception e) {
				LH.log(log, Level.SEVERE, "Plugin generated error for parent ack report ", order.getFixOrder(), e);
			}
		}
		toFixSession.send(report, threadScope);
	}

	public void start() {
		this.plugin = super.getOmsPlugin().getParentCanceledPlugin();
		super.start();
	}
}
