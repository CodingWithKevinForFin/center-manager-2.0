package com.f1.omsweb;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Row;
import com.f1.pofo.oms.Execution;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.chart.SeriesChartPortlet;
import com.f1.utils.structs.table.derived.AggregateTable;
import com.f1.utils.structs.table.derived.DerivedTable;
import com.f1.utils.structs.table.stack.EmptyCalcFrameStack;

public class ExecTimeseriesChartPortlet extends SeriesChartPortlet implements OmsPortlet {

	private Map<String, Row> ids2rows;
	private OrdersService service;
	private DerivedTable execTable;

	public ExecTimeseriesChartPortlet(PortletConfig portletConfig) {
		super(portletConfig);
		this.ids2rows = new HashMap<String, Row>();
		this.execTable = new DerivedTable(EmptyCalcFrameStack.INSTANCE, Integer.class, "id", Long.class, "time", Integer.class, "qty");
		AggregateTable aggTable = new AggregateTable(execTable, "time");
		aggTable.addSumColumn("qty", "qty");
		service = (OrdersService) getManager().getService(OrdersService.ID);
		service.addOmsPortlet(this);

	}

	public static class Builder extends AbstractPortletBuilder<ExecTimeseriesChartPortlet> {

		private static final String ID = "ExecTimeseriesChartPortlet";

		public Builder() {
			super(ExecTimeseriesChartPortlet.class);
			setIcon("portlet_icon_graph");
		}

		@Override
		public ExecTimeseriesChartPortlet buildPortlet(PortletConfig config) {
			return new ExecTimeseriesChartPortlet(config);
		}

		@Override
		public String getPortletBuilderName() {
			return "Execution Timeseries";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onExecution(WebOmsExecution execution) {
		if (!execution.getIsSliceExecution())
			return;
		Execution e = execution.getExecution();
		Row row = this.ids2rows.get(e.getId());
		if (row == null) {
			row = execTable.getRows().addRow(e.getId(), e.getExecTime().getTimeMillis() / 10000, e.getExecQty());
			this.ids2rows.put(e.getId(), row);
		} else {
			row.put("qty", e.getExecTime().getTimeMillis() / 10000);
			row.put("broker", e.getExecBroker());
		}

	}

	@Override
	public void onClosed() {
		service.removeOmsPortlet(this);
		super.onClosed();
	}

	@Override
	public void onOrder(WebOmsOrder order) {
	}

}
