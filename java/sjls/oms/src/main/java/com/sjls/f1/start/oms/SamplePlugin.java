package com.sjls.f1.start.oms;

import java.util.Map;

import com.f1.container.Container;
import com.f1.fix.oms.OmsPluginManager;
import com.f1.fix.oms.OmsUtils;
import com.f1.fix.oms.plugin.CancelChildOrderPlugin;
import com.f1.fix.oms.plugin.CancelReplaceChildOrderPlugin;
import com.f1.fix.oms.plugin.NewChildOrderPlugin;
import com.f1.fix.oms.plugin.ParentAckedPlugin;
import com.f1.fix.oms.plugin.ParentCanceledPlugin;
import com.f1.fix.oms.plugin.ParentExecutionPlugin;
import com.f1.fix.oms.plugin.ParentReplacedPlugin;
import com.f1.pofo.fix.FixExecutionReport;
import com.f1.pofo.fix.FixStatusReport;
import com.f1.pofo.fix.child.FixChildOrderCancelRequest;
import com.f1.pofo.fix.child.FixChildOrderReplaceRequest;
import com.f1.pofo.fix.child.FixChildOrderRequest;
import com.f1.pofo.oms.Order;
import com.f1.utils.CH;
import com.f1.utils.PropertyController;

public class SamplePlugin {

	private SamplePlugin() {
	}

	public static class NewChildOrder implements NewChildOrderPlugin {

		private String newSubId;

		@Override
		public void onStartup(Container container) {
		}

		@Override
		public void init(PropertyController allProperties, PropertyController propertiesForThisPlugin, OmsPluginManager manager) {
			newSubId = propertiesForThisPlugin.getRequired("newsubid", String.class);
			manager.addNewOrderRetainTag(6115);
		}

		@Override
		public void onNewChildOrder(Order parentOrderRef, Order childOrderRef, FixChildOrderRequest mutableRequest) {
			if ("XIP".equals(parentOrderRef.getOnBehalfOfCompId()))
				OmsUtils.setPassThroughValue(mutableRequest, 129, newSubId);
		}

	}

	public static class CancelChildOrder implements CancelChildOrderPlugin {

		@Override
		public void onStartup(Container container) {
		}

		@Override
		public void init(PropertyController allProperties, PropertyController propertiesForThisPlugin, OmsPluginManager manager) {
		}

		@Override
		public void onCancelChildOrder(Order parentOrderRef, Order childOrderRef, FixChildOrderCancelRequest mutableRequest) {
		}

	}

	public static class CancelReplaceChildOrder implements CancelReplaceChildOrderPlugin {

		@Override
		public void onStartup(Container container) {
		}

		@Override
		public void init(PropertyController allProperties, PropertyController propertiesForThisPlugin, OmsPluginManager manager) {
		}

		@Override
		public void onCancelReplaceChildOrder(Order parentOrderRef, Order childOrderRef, FixChildOrderReplaceRequest mutableRequest) {
		}

	}

	public static class ParentReplaced implements ParentReplacedPlugin {

		@Override
		public void onStartup(Container container) {
		}

		@Override
		public void init(PropertyController allProperties, PropertyController propertiesForThisPlugin, OmsPluginManager omsPluginManager) {

		}

		@Override
		public void onParentReplaced(Order parentOrderRef, FixStatusReport report) {
			OmsUtils.setPassThroughValue(report, 6555, "REPLACED");
		}

	}

	public static class ParentCanceled implements ParentCanceledPlugin {

		@Override
		public void onStartup(Container container) {
			// TODO Auto-generated method stub

		}

		@Override
		public void init(PropertyController allProperties, PropertyController propertiesForThisPlugin, OmsPluginManager omsPluginManager) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onParentCanceled(Order parentOrderRef, FixStatusReport report) {
			OmsUtils.setPassThroughValue(report, 6555, "CANCELED");
		}

	}

	public static class ParentAcked implements ParentAckedPlugin {

		@Override
		public void onStartup(Container container) {
			// TODO Auto-generated method stub

		}

		@Override
		public void init(PropertyController allProperties, PropertyController propertiesForThisPlugin, OmsPluginManager omsPluginManager) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onParentAcked(Order fixOrder, FixStatusReport report) {
			OmsUtils.setPassThroughValue(report, 6555, "ACKED");
		}

	}

	public static class ParentExecution implements ParentExecutionPlugin {

		private static final Map<String, String> tag851map = CH.m("GSALGO", "A", "SJLSALGO", "X");
		@Override
		public void onStartup(Container container) {
		}

		@Override
		public void init(PropertyController allProperties, PropertyController propertiesForThisPlugin, OmsPluginManager manager) {
			manager.addExecutionReportRetainTag(6860);
			manager.addExecutionReportRetainTag(30);
		}

		@Override
		public void onParentExecution(Order parentOrderRef, Order childOrderRef, FixExecutionReport childExecution, FixExecutionReport mutableReport) {
			if ("SJST".equals(OmsUtils.getPassThroughValue(childExecution, 6860, null)))
				OmsUtils.setPassThroughValue(mutableReport, 4540, "SJLS");
			if ("A".equals(OmsUtils.getPassThroughValue(childExecution, 30, null))) {
				final String t9882 = OmsUtils.getPassThroughValue(childExecution, 9882, null);
				OmsUtils.setPassThroughValue(mutableReport, 851, CH.getOr(tag851map, t9882, "4"));
			} else
				OmsUtils.setPassThroughValue(mutableReport, 851, "4");
		}
	}

}
