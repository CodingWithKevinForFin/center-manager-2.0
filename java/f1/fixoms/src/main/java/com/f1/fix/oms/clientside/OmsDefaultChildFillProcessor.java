package com.f1.fix.oms.clientside;

import java.util.logging.Level;

import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.OmsUtils;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.plugin.ParentExecutionPlugin;
import com.f1.fix.oms.schema.ClientOrder;
import com.f1.fix.oms.schema.OmsOrder;
import com.f1.fix.oms.schema.OmsOrderStatus;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.pofo.fix.ExecType;
import com.f1.pofo.fix.FixExecutionReport;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsNotification;
import com.f1.pofo.oms.Order;
import com.f1.utils.LH;

/**
 * 
 * process the fill of a child order onto a client order
 * 
 */
public class OmsDefaultChildFillProcessor extends OmsAbstractProcessor {
	ObjectGeneratorForClass<Execution> execg;
	private boolean copyDeliverToCompIdToExecBroker;
	private ParentExecutionPlugin plugin;

	public void start() {
		super.start();
		execg = getGenerator(Execution.class);
		copyDeliverToCompIdToExecBroker = getTools().getOptional("com.f1.fix.copy.128.to.76", false);
		this.plugin = super.getOmsPlugin().getParentExecutionPlugin();
	}

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		OmsOrder order = action.getOrder();

		FixExecutionReport report = (FixExecutionReport) action.getFixMsgEvent();
		Execution parentExec = execg.nw();

		parentExec.setId(getServices().getTicketGenerator("ParentExec").createNextId());
		report.getExecution().setExecGroupID(report.getExecution().getId());
		parentExec.setOrderId(order.getFixOrder().getId());
		OmsUtils.inherit(parentExec, report.getExecution());

		OmsOrder slice = state.getOrder(report.getExecution().getOrderId());
		if (copyDeliverToCompIdToExecBroker) {
			String sessionName = slice.getFixOrder().getSessionName();
			parentExec.setExecBroker(sessionName);
		}

		parentExec.setOrderRevision(order.getFixOrder().getRevision());
		report.setExecution(parentExec);// TODO: must cleanup
		OmsUtils.processFill(order, report);
		Order fixOrder = order.getFixOrder();
		ExecType execType = null;
		order.setExchLeaves(order.getExchLeaves() - parentExec.getExecQty());
		if (fixOrder.getOrderQty() <= fixOrder.getTotalExecQty()) {
			OmsUtils.transitionTo((ClientOrder) order, OmsOrderStatus.FILLED);
			execType = ExecType.FILLED;
		} else {
			OmsUtils.transitionTo((ClientOrder) order, OmsOrderStatus.PARTIAL);
			execType = ExecType.PARTIAL;
		}
		FixExecutionReport send = newExecutionReport(order, execType, parentExec);

		if (plugin != null) {
			try {
				plugin.onParentExecution(order.getFixOrder(), slice.getFixOrder(), report, send);
			} catch (Exception e) {
				LH.log(log, Level.SEVERE, "Plugin generated error for execution report ", send, e);
			}
		}
		getOmsPlugin().mapParentExecutionTags(report.getExecution().getPassThruTags(), send);
		toFixSession.send(send, threadScope);

		OmsNotification notification = newNotification(state);
		notification.setType(OmsAction.ATTACH_EXECUTION);
		toOMSClient.send(notification, threadScope);
		if (state.isCancellingChildOrders() && order.getExchLeaves() <= 0) {
			OmsNotification notif = newNotification(state);
			notif.setType(OmsAction.ALL_CHILDREN_CANCELLED);
			toOMSClient.send(notif, threadScope);
			state.setCancellingChildOrders(false);
		}
	}

}
