package com.f1.fix.oms.plugin;

import com.f1.pofo.fix.child.FixChildOrderReplaceRequest;
import com.f1.pofo.oms.Order;

public interface CancelReplaceChildOrderPlugin extends OmsPlugin {

	//Called each time a c/r child order is sent to street. THREADING NOTE: Should assume concurrent access
	void onCancelReplaceChildOrder(Order parentOrderRef, Order childOrderRef, FixChildOrderReplaceRequest mutableRequest);
}
