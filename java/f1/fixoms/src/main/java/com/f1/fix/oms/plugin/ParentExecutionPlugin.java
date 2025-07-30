package com.f1.fix.oms.plugin;

import com.f1.pofo.fix.FixExecutionReport;
import com.f1.pofo.oms.Order;

public interface ParentExecutionPlugin extends OmsPlugin {

	//Called each time an execution will be send back to client. THREADING NOTE: Should assume concurrent access
	void onParentExecution(Order parentOrderRef, Order childOrderRef, FixExecutionReport report, FixExecutionReport mutableReport);

}
