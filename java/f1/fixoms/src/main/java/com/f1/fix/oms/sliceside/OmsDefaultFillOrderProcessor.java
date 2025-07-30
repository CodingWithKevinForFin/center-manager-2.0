package com.f1.fix.oms.sliceside;

import java.util.logging.Level;

import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.OmsUtils;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.schema.FixCopyUtil;
import com.f1.fix.oms.schema.OmsOrderStatus;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.fix.oms.schema.Slice;
import com.f1.persist.structs.PersistableHashMap;
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

public class OmsDefaultFillOrderProcessor extends OmsAbstractProcessor {

	public void start() {
		super.start();
	}

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		Slice slice = (Slice) action.getOrder();
		FixExecutionReport report = (FixExecutionReport) action.getFixMsgEvent();

		if (report.getPosResend()) {
			if (CH.isntEmpty(slice.getExecutions()) && slice.getExecutions().containsKey(report.getExecution().getId())) {
				LH.info(log, "Ignoring pos-resend execution:", report);
				return;
			}
			LH.info(log, "processing pos-resend execution:", report);
		}
		slice.getFixOrder().setText(report.getText());
		Execution exec = report.getExecution();
		exec.setText(report.getText());
		if (report.getPassThruTags() != null && exec.getPassThruTags() == null)
			exec.setPassThruTags(new PersistableHashMap<Integer, String>());
		FixCopyUtil.copyMap(exec.getPassThruTags(), report.getPassThruTags());
		exec.setPassThruTags(report.getPassThruTags());
		exec.setOrderId(slice.getFixOrder().getId());
		exec.setOrderRevision(slice.getFixOrder().getRevision());
		if (log.isLoggable(Level.FINEST))
			LH.log(log, Level.FINEST, "Attaching Execution: " + report + " to " + slice);
		OmsUtils.processFill(slice, report);
		Order myState = slice.getFixOrder();
		if ((myState.getTotalExecQty() != report.getCumQty()) || MH.diff(myState.getTotalExecValue(), report.getExecValue()) > 0.00001) {
			LH.severe(log, "Fill received does not reflect correct state (totexeqty = ", myState.getTotalExecQty(), ", val = ", myState.getTotalExecValue(), "): ",
					report.toString());
			// Generate DK or substitute fill here?
		}

		if (myState.getTotalExecQty() >= myState.getOrderQty())
			OmsUtils.transitionSliceTo(slice, OmsOrderStatus.FILLED);
		else
			OmsUtils.transitionSliceTo(slice, OmsOrderStatus.PARTIAL);
		slice.setExchLeaves(slice.getExchLeaves() - exec.getExecQty());
		action.setChildOrder(slice);
		action.setOrder(slice.getParentOrder());
		if (myState.getTotalExecQty() == slice.getFixOrder().getOrderQty()) {
			action.setOrderAction(OmsAction.CHILD_FULLY_FILLED);
		} else {
			action.setOrderAction(OmsAction.CHILD_PARTIALLY_FILLED);
		}
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
