package com.f1.fix.oms.clientside;

import java.util.logging.Level;

import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.plugin.ParentReplacedPlugin;
import com.f1.fix.oms.schema.ClientOrder;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.pofo.fix.ExecType;
import com.f1.pofo.fix.FixMsg;
import com.f1.pofo.fix.FixReport;
import com.f1.pofo.fix.FixStatusReport;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsNotification;
import com.f1.pofo.oms.Order;
import com.f1.utils.LH;

/**
 * 
 * process a restate for a client order
 * 
 */
public class OmsDefaultOrderRestatedProcessor extends OmsAbstractProcessor {

	private ParentReplacedPlugin plugin;

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		try {
			FixMsg rpt = action.getFixMsgEvent();
			if (!(rpt instanceof FixReport)) {
				action.setText("RESTATED FAILED: event details must contain FixReport");
				return;
			}
			FixReport fixReport = (FixReport) rpt;
			fixReport.getOrderQty();
			if (fixReport.getExecType() != ExecType.RESTATED) {
				action.setText("RESTATED FAILED: exec type must be RESTATED");
				return;
			}
			final ClientOrder order = state.getClientOrder();
			if (order.getPending() != null) {
				action.setText("RESTATED FAILED: Order in pending state");
				return;
			}
			Order fixOrder = order.getFixOrder();
			if (fixReport.getOrderQty() > fixOrder.getOrderQty()) {
				action.setText("RESTATED FAILED: Quantity can not exceed current order quantity(" + fixReport.getOrderQty() + " > " + fixOrder.getOrderQty() + ")");
				return;
			}
			int min = order.getExchLeaves() + fixOrder.getTotalExecQty();
			if (fixReport.getOrderQty() < min) {
				action.setText("RESTATED FAILED: Quantity can not be less than exchLeaves + execQty(" + fixReport.getOrderQty() + " < " + order.getExchLeaves() + " + "
						+ fixOrder.getOrderQty() + ")");
				return;
			}
			order.getFixOrder().setOrderQty(fixReport.getOrderQty());
			FixStatusReport report = newStatusReport(order, ExecType.RESTATED);
			report.setRestatementReason(fixReport.getRestatementReason());
			OmsNotification notification = newNotification(state);
			notification.setType(OmsAction.ORDER_RESTATED);
			toOMSClient.send(notification, threadScope);
			if (plugin != null) {
				try {
					plugin.onParentReplaced(fixOrder, report);
				} catch (Exception e) {
					LH.log(log, Level.SEVERE, "Plugin generated error for parent ack report ", fixOrder, e);
				}
			}
			toFixSession.send(report, threadScope);
		} finally {
			LH.info(log, "RESTATED: ", action.getClientMsg(), " ==> ", action.getText());
		}
	}

	public void start() {
		this.plugin = super.getOmsPlugin().getParentReplacedPlugin();
		super.start();
	}

}
