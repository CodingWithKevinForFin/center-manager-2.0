package com.f1.omsweb;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Row;
import com.f1.pofo.oms.Execution;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.visual.PieChartPortlet;
import com.f1.utils.structs.table.derived.AggregateTable;
import com.f1.utils.structs.table.derived.DerivedTable;
import com.f1.utils.structs.table.stack.EmptyCalcFrameStack;

public class ExecBrokerChartPortlet extends PieChartPortlet implements OmsPortlet {

	private Map<String, Row> ids2rows;
	private OrdersService service;
	private DerivedTable execTable;

	public ExecBrokerChartPortlet(PortletConfig config) {
		super(config);
		this.ids2rows = new HashMap<String, Row>();
		this.execTable = new DerivedTable(EmptyCalcFrameStack.INSTANCE, Integer.class, "id", String.class, "broker", Integer.class, "qty");
		AggregateTable aggTable = new AggregateTable(execTable, "broker");
		aggTable.setTitle("Broker Executions");
		aggTable.addSumColumn("qty", "qty");
		service = (OrdersService) getManager().getService(OrdersService.ID);
		service.addOmsPortlet(this);
		super.setTable(aggTable, "broker", "qty");
	}

	public static class Builder extends AbstractPortletBuilder<ExecBrokerChartPortlet> {

		private static final String ID = "ExecBrokerChartPortlet";

		public Builder() {
			super(ExecBrokerChartPortlet.class);
			setIcon("portlet_icon_graph");
		}

		@Override
		public ExecBrokerChartPortlet buildPortlet(PortletConfig portletManager) {
			return new ExecBrokerChartPortlet(portletManager);
		}

		@Override
		public String getPortletBuilderName() {
			return "Executing Broker Chart";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onClosed() {
		service.removeOmsPortlet(this);
		super.onClosed();
	}

	private Row addExecution(Execution e) {
		return execTable.getRows().addRow(e.getId(), e.getExecBroker(), e.getExecQty());
	}
	@Override
	public void onExecution(WebOmsExecution execution) {
		if (!execution.getIsSliceExecution())
			return;
		Execution e = execution.getExecution();
		Row row = this.ids2rows.get(e.getId());
		if (row == null) {
			this.ids2rows.put(e.getId(), addExecution(e));
		} else {
			row.put("qty", e.getExecQty());
			row.put("broker", e.getExecBroker());
		}
	}

	@Override
	public void onOrder(WebOmsOrder order) {
	}

}
