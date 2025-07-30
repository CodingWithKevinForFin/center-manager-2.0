package com.f1.oms3f.sample;

import com.f1.container.Container;
import com.f1.fix.oms.OmsPluginManager;
import com.f1.fix.oms.plugin.CancelChildOrderPlugin;
import com.f1.fix.oms.plugin.CancelReplaceChildOrderPlugin;
import com.f1.fix.oms.plugin.NewChildOrderPlugin;
import com.f1.fix.oms.plugin.ParentExecutionPlugin;
import com.f1.pofo.fix.FixExecutionReport;
import com.f1.pofo.fix.child.FixChildOrderCancelRequest;
import com.f1.pofo.fix.child.FixChildOrderReplaceRequest;
import com.f1.pofo.fix.child.FixChildOrderRequest;
import com.f1.pofo.oms.Order;
import com.f1.utils.PropertyController;

public class SampleOmsPlugin implements NewChildOrderPlugin, CancelChildOrderPlugin, CancelReplaceChildOrderPlugin, ParentExecutionPlugin {

	private String acct;
	private Object account;

	@Override
	public void onStartup(Container container) {
	}

	@Override
	public void init(PropertyController allProperties, PropertyController propertiesForThisPlugin, OmsPluginManager manager) {
		account = propertiesForThisPlugin.getRequired("account");
	}

	@Override
	public void onCancelReplaceChildOrder(Order parentOrderRef, Order childOrderRef, FixChildOrderReplaceRequest mutableRequest) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCancelChildOrder(Order parentOrderRef, Order childOrderRef, FixChildOrderCancelRequest mutableRequest) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNewChildOrder(Order parentOrderRef, Order childOrderRef, FixChildOrderRequest mutableRequest) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onParentExecution(Order parentOrderRef, Order childOrderRef, FixExecutionReport report, FixExecutionReport mutableReport) {
		// TODO Auto-generated method stub

	}

}
