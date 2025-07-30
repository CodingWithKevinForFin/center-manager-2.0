package com.f1.fix.oms.clientside;

import java.util.logging.Level;

import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.OmsUtils;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.plugin.ParentExecutionPlugin;
import com.f1.fix.oms.schema.ClientOrder;
import com.f1.fix.oms.schema.OmsOrderStatus;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.fix.oms.schema.Slice;
import com.f1.pofo.fix.ExecType;
import com.f1.pofo.fix.FixExecutionReport;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsNotification;
import com.f1.pofo.oms.Order;
import com.f1.utils.LH;
import com.f1.utils.MH;

/**
 * 
 * attach an execution and send to the client
 * 
 */
public class OmsDefaultAttachCorrectExecutionProcessor extends OmsAbstractProcessor {

	private ParentExecutionPlugin plugin;

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		final ClientOrder order = (ClientOrder) action.getOrder();
		final Slice childOrder = (Slice) action.getChildOrder();
		final Order fixOrder = order.getFixOrder();
		final FixExecutionReport report = (FixExecutionReport) action.getFixMsgEvent();
		final Execution execution = report.getExecution();

		execution.setId(getServices().getTicketGenerator("ManualExec").createNextId());
		execution.setOrderId(fixOrder.getId());
		execution.setExecGroupID(report.getExecution().getId());
		execution.setOrderRevision(fixOrder.getRevision());

		Execution orig = OmsUtils.processCorrect(order, report, "client");

		final ExecType execType;
		if (fixOrder.getOrderQty() <= fixOrder.getTotalExecQty()) {
			OmsUtils.transitionTo(order, OmsOrderStatus.FILLED);
			execType = ExecType.FILLED;
		} else {
			OmsUtils.transitionTo(order, OmsOrderStatus.PARTIAL);
			order.setOrderStatus(MH.clearBits(order.getOrderStatus(), OmsOrderStatus.FILLED.getMask()));
			order.getFixOrder().setOrderStatus(MH.clearBits(order.getFixOrder().getOrderStatus(), OmsOrderStatus.FILLED.getMask()));
			execType = ExecType.PARTIAL;
		}

		final FixExecutionReport send = newExecutionReport(order, execType, report.getExecution());
		send.setPosResend(report.getPosResend());
		send.setText(report.getText());
		report.getExecution().setExecTime(getTools().getNowNanoDate());
		if (plugin != null) {
			try {
				plugin.onParentExecution(order.getFixOrder(), childOrder.getFixOrder(), report, send);
			} catch (Exception e) {
				LH.log(log, Level.SEVERE, "Plugin generated error for execution report ", send, e);
			}
		}
		getOmsPlugin().mapParentExecutionTags(report.getPassThruTags(), send);
		toFixSession.send(send, threadScope);

		OmsNotification notification = newNotification(state);
		notification.setType(OmsAction.ATTACH_CORRECT_EXECUTION);
		toOMSClient.send(notification, threadScope);
	}

	public void start() {
		this.plugin = super.getOmsPlugin().getParentExecutionPlugin();
		super.start();
	}

}
