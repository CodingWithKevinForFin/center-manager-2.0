package com.f1.fix.oms.clientside;

import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.OmsUtils;
import com.f1.fix.oms.adapter.OmsOrderState;
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
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;

/**
 * 
 * process the fill of a child order onto a client order
 * 
 */
public class OmsDefaultChildCorrectProcessor extends OmsAbstractProcessor {
	ObjectGeneratorForClass<Execution> execg;
	private boolean copyDeliverToCompIdToExecBroker;

	public void start() {
		super.start();
		execg = getGenerator(Execution.class);
		copyDeliverToCompIdToExecBroker = getTools().getOptional("com.f1.fix.copy.128.to.76", false);
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

		if (copyDeliverToCompIdToExecBroker) {
			OmsOrder slice = state.getOrder(report.getExecution().getOrderId());
			String sessionName = slice.getFixOrder().getSessionName();
			parentExec.setExecBroker(sessionName);
		}

		parentExec.setOrderRevision(order.getFixOrder().getRevision());
		Execution origChildExec = CH.getOrThrow(action.getChildOrder().getExecutions(), report.getExecution().getExecRefID());
		Execution orig = CH.getOrThrow(order.getExecutions(), origChildExec.getExecGroupID());
		parentExec.setExecRefID(orig.getId());
		report.setExecution(parentExec);// TODO: must cleanup

		Execution nuw = report.getExecution();
		order.getExecutions().put(parentExec.getId(), parentExec);
		Order fixOrder = order.getFixOrder();
		long origTotQty = fixOrder.getTotalExecQty();
		double origTotVal = fixOrder.getTotalExecValue();
		if (orig != null && orig.getExecStatus() != 1) {
			orig.setExecStatus(2);//TODO: USE A DAMN EXECSTATUS ENUM
			fixOrder.setTotalExecQty(fixOrder.getTotalExecQty() - orig.getExecQty() + nuw.getExecQty());
			fixOrder.setTotalExecValue(fixOrder.getTotalExecValue() - orig.getExecPx() * orig.getExecQty() + nuw.getExecPx() * nuw.getExecQty());
		}
		{//TODO: remove debug
			long origQty = orig == null ? -1 : orig.getExecQty();
			double origPx = orig == null ? -1 : orig.getExecPx();
			long newQty = nuw.getExecQty();
			double newPx = nuw.getExecPx();
			long newTotQty = fixOrder.getTotalExecQty();
			double newTotVal = fixOrder.getTotalExecValue();
			LH.info(log, "correct for refId=", report.getExecution().getExecRefID(), ", origTotQty=", origTotQty, ", origTotVal=", origTotVal, ", origQty=", origQty, ", origPx=",
					origPx, ", newQty=", newQty, ", newPx=", newPx, ", newTotQty=", newTotQty, ", newTotVal=", newTotVal);
		}

		ExecType execType = null;

		if (!OmsUtils.isTerminal(order.getOrderStatus()) || OmsUtils.isInState(order.getOrderStatus(), OmsOrderStatus.FILLED)) {
			order.setExchLeaves(order.getExchLeaves() + orig.getExecQty() - nuw.getExecQty());
		}

		if (fixOrder.getOrderQty() <= fixOrder.getTotalExecQty()) {
			OmsUtils.transitionTo((ClientOrder) order, OmsOrderStatus.FILLED);
			execType = ExecType.FILLED;
		} else {
			OmsUtils.transitionTo((ClientOrder) order, OmsOrderStatus.PARTIAL);
			order.setOrderStatus(MH.clearBits(order.getOrderStatus(), OmsOrderStatus.FILLED.getMask()));
			order.getFixOrder().setOrderStatus(MH.clearBits(order.getFixOrder().getOrderStatus(), OmsOrderStatus.FILLED.getMask()));
			execType = ExecType.PARTIAL;
		}

		FixExecutionReport send = newExecutionReport(order, execType, parentExec);
		toFixSession.send(send, threadScope);

		OmsNotification notification = newNotification(state);
		notification.setType(OmsAction.ATTACH_CORRECT_EXECUTION);
		toOMSClient.send(notification, threadScope);
	}

}
