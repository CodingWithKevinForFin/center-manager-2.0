package com.f1.fix.oms.plugin;

import com.f1.pofo.fix.child.FixChildOrderRequest;
import com.f1.pofo.oms.Order;

public interface NewChildOrderPlugin extends OmsPlugin {

	//Called each time a new child order is sent to street. THREADING NOTE: Should assume concurrent access
	void onNewChildOrder(Order parentOrderRef, Order childOrderRef, FixChildOrderRequest mutableRequest);

}
