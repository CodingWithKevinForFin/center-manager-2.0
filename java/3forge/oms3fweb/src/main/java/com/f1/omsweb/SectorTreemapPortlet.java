package com.f1.omsweb;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Row;
import com.f1.pofo.oms.Order;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.visual.TreemapPortlet;
import com.f1.utils.CH;
import com.f1.utils.structs.table.derived.AggregateTable;
import com.f1.utils.structs.table.derived.DerivedTable;
import com.f1.utils.structs.table.stack.EmptyCalcFrameStack;

public class SectorTreemapPortlet extends TreemapPortlet implements OmsPortlet {

	private Map<String, String> sectorMap;
	private DerivedTable orders;
	private Map<String, Row> id2row = new HashMap<String, Row>();
	private AggregateTable agg;
	private OrdersService service;

	public SectorTreemapPortlet(PortletConfig portletConfig, Map<String, String> sectorMap) {
		super(portletConfig);
		this.sectorMap = sectorMap;
		this.orders = new DerivedTable(EmptyCalcFrameStack.INSTANCE, String.class, "Sector", String.class, "Symbol", Integer.class, "qty");
		this.agg = new AggregateTable(this.orders, "Sector");
		this.agg.addSumColumn("qty", "qty");
		//setTable(this.agg, "Sector", "qty");
		this.service = (OrdersService) getManager().getService(OrdersService.ID);
		service.addOmsPortlet(this);
	}

	public static class Builder extends AbstractPortletBuilder<SectorTreemapPortlet> {

		private static final String ID = "SectorTreemap";
		final private Map<String, String> sectorMap;

		public Builder(Map<String, String> sectorMap) {
			super(SectorTreemapPortlet.class);
			this.sectorMap = sectorMap;
			setIcon("portlet_icon_graph");
		}

		@Override
		public SectorTreemapPortlet buildPortlet(PortletConfig config) {
			return new SectorTreemapPortlet(config, sectorMap);
		}

		@Override
		public String getPortletBuilderName() {
			return "Sector Treemap";
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

	@Override
	public void onOrder(WebOmsOrder order) {
		Order o = order.getOrder();
		final String id = o.getId();
		Row row = id2row.get(id);
		if (row == null) {
			String sector = CH.getOr(sectorMap, o.getSymbol(), "unkown");
			row = orders.getRows().addRow(sector, o.getSymbol(), o.getTotalExecQty());
			id2row.put(id, row);
		} else
			row.put("qty", o.getTotalExecQty());
	}

	@Override
	public void onExecution(WebOmsExecution execution) {

	}

}
