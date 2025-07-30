package com.f1.fix.oms.clientside;

import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.schema.OrderEventCtx;

/**
 * reject an event
 */
public class OmsRejectClientActionProcessor extends OmsAbstractProcessor {

	private final String rejectText;

	public OmsRejectClientActionProcessor(String rejectText) {
		this.rejectText = rejectText;
	}

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		action.setRejectText(rejectText);
	}

	public String getRejectText() {
		return rejectText;
	}

}
