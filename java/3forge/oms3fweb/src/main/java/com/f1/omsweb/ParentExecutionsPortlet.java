package com.f1.omsweb;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Row;
import com.f1.base.ValuedParam;
import com.f1.pofo.fix.FixExecutionReport;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsClientAction;
import com.f1.pofo.oms.Order;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.utils.CH;
import com.f1.utils.GuidHelper;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;

public class ParentExecutionsPortlet extends FastTablePortlet implements WebContextMenuListener, OmsPortlet, WebContextMenuFactory {

	final private OrdersService service;
	final private BasicPortletSocket fromParentOrdersSocket;
	final private Set<String> selectedParentOrdersIds;
	final private Map<String, Row> ids2rows;

	public ParentExecutionsPortlet(PortletConfig config) {
		super(config, null);
		this.selectedParentOrdersIds = new HashSet<String>();
		this.ids2rows = new HashMap<String, Row>();

		String[] ids = { "id", "qty", "px", "time", "brkr", "grp", "lastMkt", "orderId", "symbol", "side", "data" };

		service = (OrdersService) getManager().getService(OrdersService.ID);
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("Parent Executions");
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

		table.setMenuFactory(this);
		super.setTable(table);
		this.fromParentOrdersSocket = addSocket(false, "parentExecutions", "Show Parent Executions", true, null, CH.s(ShowParentExecutionsInterPortletMessage.class));
		service.addOmsPortlet(this);
	}
	public void onClosed() {
		super.onClosed();
		service.removeOmsPortlet(this);
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		if ("bust".equals(action)) {
			for (Row row : table.getSelectedRows()) {
				OmsClientAction bustMessage = createBust((WebOmsExecution) row.get("data"));
				service.bustExecution(bustMessage);
			}
		}
	}

	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
	}

	@Override
	public void onDisconnect(PortletSocket localSocket, PortletSocket remoteSocket) {
	}

	@Override
	public void onConnect(PortletSocket localSocket, PortletSocket remoteSocket) {
		if (localSocket == this.fromParentOrdersSocket) {
			getTable().clear();
			ids2rows.clear();
			selectedParentOrdersIds.clear();
		}
	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket origin, InterPortletMessage message) {
		ShowParentExecutionsInterPortletMessage showMessage = (ShowParentExecutionsInterPortletMessage) message;
		List<WebOmsOrder> show = showMessage.getShowExecutions();
		if (show != null) {
			for (WebOmsOrder o : show) {
				boolean added = this.selectedParentOrdersIds.add(o.getOrder().getId());
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
				boolean removed = this.selectedParentOrdersIds.remove(o.getOrder().getId());
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
			selectedParentOrdersIds.clear();
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
		if (execution.getIsSliceExecution())
			return;
		Execution e = execution.getExecution();
		Order co = execution.getParent().getOrder();
		if (!this.fromParentOrdersSocket.hasConnections() || this.selectedParentOrdersIds.contains(e.getOrderId())) {
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

	public static class Builder extends AbstractPortletBuilder<ParentExecutionsPortlet> {

		private static final String ID = "parentExecutionsPortlet";

		public Builder() {
			super(ParentExecutionsPortlet.class);
		}

		@Override
		public ParentExecutionsPortlet buildPortlet(PortletConfig portletManager) {
			return new ParentExecutionsPortlet(portletManager);
		}

		@Override
		public String getPortletBuilderName() {
			return "Parent Executions";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onOrder(WebOmsOrder order) {
	}

	public OmsClientAction createBust(WebOmsExecution webexec) {
		Execution exec = webexec.getExecution();
		String guid = GuidHelper.getGuid();
		OmsClientAction action = nw(OmsClientAction.class);
		action.setRootOrderID(exec.getOrderId());
		action.setOrderAction(OmsAction.ATTACH_BUST_EXECUTION);
		FixExecutionReport report = nw(FixExecutionReport.class);
		Execution execution = nw(Execution.class);
		execution.setLastMkt(exec.getLastMkt());
		execution.setExecBroker(exec.getExecBroker());
		execution.setContraBroker(exec.getContraBroker());
		execution.setExecTime(getManager().getTools().getNowNanoDate());
		execution.setExecQty(exec.getExecQty());
		execution.setExecPx(exec.getExecPx());
		execution.setId(guid);
		//execution.setRefId(exec.getId());
		execution.setExecRefID(exec.getId());
		execution.setExecTransType(1);
		report.setExecution(execution);
		action.setEventDetails(report);
		action.setOrderID(exec.getOrderId());
		return action;
	}
	@Override
	public WebMenu createMenu(WebTable table) {
		if (table.hasSelectedRows()) {
			return new BasicWebMenu(new BasicWebMenuLink("Bust Parent Execution(s)", true, "bust"));
		}
		return null;
	}
	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
	}
	@Override
	public void onCellMousedown(WebTable table, Row row, WebColumn col) {
	}
	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
	}
	@Override
	public void onNoSelectedChanged(FastWebTable fastWebTable) {
	}
	@Override
	public void onScroll(int viewTop, int viewPortHeight, long contentWidth, long contentHeight) {
	}

}
