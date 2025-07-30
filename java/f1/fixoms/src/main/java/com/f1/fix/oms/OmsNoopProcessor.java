package com.f1.fix.oms;

import java.util.logging.Level;

import com.f1.container.ThreadScope;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.schema.OmsOrderStatus;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.utils.LH;
import com.f1.utils.MH;

/**
 * A do-nothing processor, meaning this action for the state of the order is not-important
 * 
 */
public class OmsNoopProcessor extends OmsAbstractProcessor {

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		final int orderStatus;
		if (action.getOrder() != null)
			orderStatus = action.getOrder().getOrderStatus();
		else
			orderStatus = OmsOrderStatus.UNINITIALIZED.getMask();
		final int status = MH.indexOfLastBitSet(orderStatus);
		if (log.isLoggable(Level.INFO))
			LH.info(log, "ignoring action ", action.getOrderAction(), " for status ", OmsOrderStatus.get(status));
		if (log.isLoggable(Level.FINEST))
			LH.finest(log, "ignored action: ", action.toString());
	}

}
