package com.f1.fix.oms.sliceside;

import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.OmsUtils;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.schema.FixCopyUtil;
import com.f1.fix.oms.schema.OmsOrderStatus;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.fix.oms.schema.Slice;
import com.f1.pofo.fix.FixExecutionReport;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.Order;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;

/**
 * process executions for a child order
 */

public class OmsDefaultBustOrderProcessor extends OmsAbstractProcessor {
	ObjectGeneratorForClass<Execution> execg;

	public void start() {
		super.start();
		execg = getGenerator(Execution.class);
	}

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		Slice slice = (Slice) action.getOrder();
		FixExecutionReport report = (FixExecutionReport) action.getFixMsgEvent();

		if (report.getPosResend()) {
			if (CH.isntEmpty(slice.getExecutions()) && slice.getExecutions().containsKey(report.getExecution().getId())) {
				LH.info(log, "Ignoring pos-resend execution bust:", report);
				return;
			}
			LH.info(log, "processing pos-resend execution bust:", report);
		}
		Execution exec = report.getExecution();
		exec.setText(report.getText());
		exec.setOrderId(slice.getFixOrder().getId());
		exec.setOrderRevision(slice.getFixOrder().getRevision());
		Execution orig = OmsUtils.processBust(slice, report);
		Order myState = slice.getFixOrder();

		//DO NOT DO ANY OF THIS IF IN TERMINAL STATE
		if (!OmsUtils.isTerminal(slice.getOrderStatus()) || OmsUtils.isInState(slice.getOrderStatus(), OmsOrderStatus.FILLED)) {
			if (myState.getTotalExecQty() >= myState.getOrderQty())
				OmsUtils.transitionSliceTo(slice, OmsOrderStatus.FILLED);
			else if (myState.getTotalExecQty() == 0) {
				OmsUtils.transitionSliceTo(slice, OmsOrderStatus.ACKED); //This is a shortcut..it could be pending ack but if we received a fill and a bust...just assume the ack
				slice.setOrderStatus(MH.clearBits(slice.getOrderStatus(), OmsOrderStatus.FILLED.getMask()));
				slice.getFixOrder().setOrderStatus(MH.clearBits(slice.getFixOrder().getOrderStatus(), OmsOrderStatus.FILLED.getMask()));
			} else {
				OmsUtils.transitionSliceTo(slice, OmsOrderStatus.PARTIAL);
				slice.setExchLeaves(slice.getExchLeaves() + orig.getExecQty()); //ONLY DO THIS IF NOT TERMINAL STATE ON ORDER
				slice.setOrderStatus(MH.clearBits(slice.getOrderStatus(), OmsOrderStatus.FILLED.getMask()));
				slice.getFixOrder().setOrderStatus(MH.clearBits(slice.getFixOrder().getOrderStatus(), OmsOrderStatus.FILLED.getMask()));
			}
		}

		action.setChildOrder(slice);
		action.setOrder(slice.getParentOrder());
		action.setOrderAction(OmsAction.CHILD_EXEC_BUST);

		toRootStateMachine.send(action, threadScope);

		FixCopyUtil.copy(report, myState);
		report.getExecution().setOrderId(myState.getId());
		// report.getExecution().setExecGroupID(report.getExecution().getExecID());
		// //THIS WILL BE SET BY PARENT ORDER HANDLER...SEQUENCE ACTUALLY
		// MATTERS HERE..RETHINK THE CLIENT
		// FOR GODS SAKE
		report.getExecution().setOrderRevision(myState.getRevision());
	}
}
