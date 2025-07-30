package com.f1.fix.oms.clientside;

import java.util.logging.Level;

import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.OmsUtils;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.plugin.ParentReplacedPlugin;
import com.f1.fix.oms.schema.ClientOrder;
import com.f1.fix.oms.schema.FixCopyUtil;
import com.f1.fix.oms.schema.OmsOrderStatus;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.pofo.fix.ExecType;
import com.f1.pofo.fix.FixMsg;
import com.f1.pofo.fix.FixReport;
import com.f1.pofo.fix.FixStatusReport;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsNotification;
import com.f1.utils.LH;

/**
 * 
 * process an order replaced for a client order
 * 
 */
public class OmsDefaultOrderReplacedProcessor extends OmsAbstractProcessor {

	private ParentReplacedPlugin plugin;

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		ClientOrder order = state.getClientOrder();
		if (order.getPending() == null) {
			LH.info(log, "received replaced w/o pending order so ignoring for state: ", state.getPartition().getPartitionId());
			return;
		}
		// TODO: This is scary that the tags in the message may
		// be wrong based on instantiation
		FixCopyUtil.copyMutable(order.getFixOrder(), order.getPending().getOrderInfo());

		order.getFixOrder().setOrigRequestId(order.getFixOrder().getRequestId());
		order.getFixOrder().setUpdatedTime(getTools().getNowNanoDate());
		order.getFixOrder().setRevision(order.getFixOrder().getRevision() + 1);
		order.getFixOrder().setRequestId(order.getPending().getRequestId());
		order.setPending(null);
		OmsUtils.transitionTo(order, OmsOrderStatus.REPLACED);
		state.addOrder(order);

		FixStatusReport report = newStatusReport(order, ExecType.REPLACED);

		OmsNotification notification = newNotification(state);
		notification.setType(OmsAction.ORDER_REPLACED);
		toOMSClient.send(notification, threadScope);
		FixMsg fme = action.getFixMsgEvent();
		if (fme instanceof FixReport)
			getOmsPlugin().mapParentReplaced(((FixReport) fme).getPassThruTags(), report);
		if (plugin != null) {
			try {
				plugin.onParentReplaced(order.getFixOrder(), report);
			} catch (Exception e) {
				LH.log(log, Level.SEVERE, "Plugin generated error for parent ack report ", order.getFixOrder(), e);
			}
		}
		toFixSession.send(report, threadScope);
	}

	public void start() {
		this.plugin = super.getOmsPlugin().getParentReplacedPlugin();
		super.start();
	}

}
