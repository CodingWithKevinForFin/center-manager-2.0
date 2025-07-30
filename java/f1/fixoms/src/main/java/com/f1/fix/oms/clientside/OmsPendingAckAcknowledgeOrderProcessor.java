package com.f1.fix.oms.clientside;

import java.util.logging.Level;

import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.OmsUtils;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.plugin.ParentAckedPlugin;
import com.f1.fix.oms.schema.ClientOrder;
import com.f1.fix.oms.schema.OmsOrder;
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
 * process a pending ack acknowledgement for a client order
 * 
 */
public class OmsPendingAckAcknowledgeOrderProcessor extends OmsAbstractProcessor {

	private ParentAckedPlugin plugin;

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		OmsOrder order = action.getOrder();

		OmsUtils.transitionTo((ClientOrder) order, OmsOrderStatus.ACKED);
		FixStatusReport report = newStatusReport(order, ExecType.ACKNOWLEDGED);
		order.getFixOrder().setRevision(order.getFixOrder().getRevision() + 1);

		OmsNotification notif = newNotification(state);
		notif.setType(OmsAction.ORDER_ACKED);
		toOMSClient.send(notif, threadScope);

		FixMsg fme = action.getFixMsgEvent();
		if (fme instanceof FixReport)
			getOmsPlugin().mapParentAcked(((FixReport) fme).getPassThruTags(), report);
		if (plugin != null) {
			try {
				plugin.onParentAcked(order.getFixOrder(), report);
			} catch (Exception e) {
				LH.log(log, Level.SEVERE, "Plugin generated error for parent ack report ", order.getFixOrder(), e);
			}
		}
		toFixSession.send(report, threadScope);
	}

	public void start() {
		this.plugin = super.getOmsPlugin().getParentAckedPlugin();
		super.start();
	}

}
