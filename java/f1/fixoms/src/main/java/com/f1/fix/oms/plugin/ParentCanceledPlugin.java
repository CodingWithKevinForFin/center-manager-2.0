package com.f1.fix.oms.plugin;

import com.f1.pofo.fix.FixStatusReport;
import com.f1.pofo.oms.Order;

public interface ParentCanceledPlugin extends OmsPlugin {

	//Called each time a cxl for a child order is sent to street. THREADING NOTE: Should assume concurrent access
	void onParentCanceled(Order parentOrderRef, FixStatusReport report);
}
