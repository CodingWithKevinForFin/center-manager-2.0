package com.f1.omsweb;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Row;
import com.f1.base.ValuedParam;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.Order;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.utils.CH;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;

public class ChildExecutionsPortlet extends FastTablePortlet implements OmsPortlet {

	final private OrdersService service;
	final private BasicPortletSocket fromChildOrdersSocket;
	final private Set<String> selectedChildOrdersIds;
	final private Map<String, Row> ids2rows;

	public ChildExecutionsPortlet(PortletConfig config) {
		super(config, null);
		this.selectedChildOrdersIds = new HashSet<String>();
		this.ids2rows = new HashMap<String, Row>();

		String[] ids = { "id", "qty", "px", "time", "brkr", "grp", "lastMkt", "orderId", "symbol", "side", "data" };

		service = (OrdersService) getManager().getService(OrdersService.ID);
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("Child Executions");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, config.getPortletManager().getTextFormatter());
		table.addColumn(true, "Quantity", "qty", service.getQuantityFormatter());
		table.addColumn(true, "Price", "px", service.getPriceWebCellFormatter());

		table.addColumn(true, "Time", "time", service.getTimeWebCellFormatter());
		table.addColumn(true, "Broker", "brkr", service.getTextWebCellFormatter(50));
		table.addColumn(true, "Group Id", "grp", service.getTextWebCellFormatter(50));
		table.addColumn(true, "LastMarket", "lastMkt", service.getTextWebCellFormatter(50));
		table.addColumn(true, "Side", "side", service.getSideFormatter());
		table.addColumn(true, "Symbol", "symbol", service.getSymbolWebCellFormatter());

		table.addColumn(false, "Id", "id", service.getTextWebCellFormatter(50));
		table.addColumn(false, "Order Id", "orderId", service.getTextWebCellFormatter(50));

		super.setTable(table);
		this.fromChildOrdersSocket = addSocket(false, "childExecutions", "Show Child Executions", true, null, CH.s(ShowChildExecutionsInterPortletMessage.class));
		service.addOmsPortlet(this);
	}
	public void onClosed() {
		super.onClosed();
		service.removeOmsPortlet(this);
	}

	@Override
	public void onDisconnect(PortletSocket localSocket, PortletSocket remoteSocket) {
	}

	@Override
	public void onConnect(PortletSocket localSocket, PortletSocket remoteSocket) {
		if (localSocket == this.fromChildOrdersSocket) {
			getTable().clear();
			ids2rows.clear();
			selectedChildOrdersIds.clear();
		}
	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket origin, InterPortletMessage message) {
		ShowChildExecutionsInterPortletMessage showMessage = (ShowChildExecutionsInterPortletMessage) message;
		List<WebOmsOrder> show = showMessage.getShowExecutions();
		if (show != null) {
			for (WebOmsOrder o : show) {
				boolean added = this.selectedChildOrdersIds.add(o.getOrder().getId());
				if (added) {
					for (WebOmsExecution e : o.getExecutions().values()) {
						this.ids2rows.put(e.getExecution().getId(), addExecutionRow(e));
					}
				}
			}
		}
		List<WebOmsOrder> hide = showMessage.getHideExecutions();
		if (hide != null) {
			for (WebOmsOrder o : hide) {
				boolean removed = this.selectedChildOrdersIds.remove(o.getOrder().getId());
				if (removed) {
					for (WebOmsExecution e : o.getExecutions().values()) {
						Row row = this.ids2rows.remove(e.getExecution().getId());
						if (row != null)
							removeRow(row);
					}
				}
			}
		}
		if (show == null && hide == null) {
			getTable().clear();
			ids2rows.clear();
			selectedChildOrdersIds.clear();
		}
	}

	private Row addExecutionRow(WebOmsExecution exec) {
		Execution e = exec.getExecution();
		Order order = exec.getParent().getOrder();
		return getTable().getTable().getRows().addRow(e.getId(), e.getExecQty(), e.getExecPx(), e.getExecTime(), e.getExecBroker(), e.getExecGroupID(), e.getLastMkt(),
				e.getOrderId(), order.getSymbol(), order.getSide(), exec);
	}

	@Override
	public void onExecution(WebOmsExecution execution) {
		if (!execution.getIsSliceExecution())
			return;
		Execution e = execution.getExecution();
		Order co = execution.getParent().getOrder();
		if (!this.fromChildOrdersSocket.hasConnections() || this.selectedChildOrdersIds.contains(e.getOrderId())) {
			Row row = this.ids2rows.get(e.getId());
			if (row == null) {
				this.ids2rows.put(e.getId(), addExecutionRow(execution));
			} else {
				Set<String> keys = row.getTable().getColumnIds();
				for (ValuedParam vp : e.askExistingValuedParams()) {
					if (!keys.contains(vp.getName()))
						continue;
					row.put(vp.getName(), vp.getValue(e));
				}
			}
		}
	}

	public static class Builder extends AbstractPortletBuilder<ChildExecutionsPortlet> {

		private static final String ID = "childExecutionsPortlet";

		public Builder() {
			super(ChildExecutionsPortlet.class);
		}

		@Override
		public ChildExecutionsPortlet buildPortlet(PortletConfig portletManager) {
			return new ChildExecutionsPortlet(portletManager);
		}

		@Override
		public String getPortletBuilderName() {
			return "Child Executions";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onOrder(WebOmsOrder order) {
	}
}
