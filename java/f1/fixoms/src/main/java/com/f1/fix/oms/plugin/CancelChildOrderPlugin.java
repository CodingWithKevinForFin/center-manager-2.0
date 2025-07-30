package com.f1.fix.oms.plugin;

import com.f1.pofo.fix.child.FixChildOrderCancelRequest;
import com.f1.pofo.oms.Order;

public interface CancelChildOrderPlugin extends OmsPlugin {

	//Called each time a cxl for a child order is sent to street. THREADING NOTE: Should assume concurrent access
	void onCancelChildOrder(Order parentOrderRef, Order childOrderRef, FixChildOrderCancelRequest mutableRequest);

}
