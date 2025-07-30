package com.f1.omsweb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.base.Row;
import com.f1.base.TableList;
import com.f1.base.ValuedParam;
import com.f1.fixomsclient.OmsClientUtils;
import com.f1.pofo.oms.Order;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
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
import com.f1.suite.web.table.impl.BasicWebColumn;
import com.f1.utils.CH;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;

public class ChildOrdersPortlet extends FastTablePortlet implements WebContextMenuListener, WebContextMenuFactory, OmsPortlet {

	private static final Logger log = Logger.getLogger(ChildOrdersPortlet.class.getName());

	private static final String COPY_ID = "copy";
	private static final String SLICE_TYPE_ID = "sliceType";
	private static final String EXEC_INSTRUCTIONS_ID = "execInstructions";
	private static final String TEXT_ID = "text";
	private static final String PASS_THRU_TAGS_ID = "passThruTags";
	private static final String SLICE_TYPE_TITLE = "Slice Type";
	private static final String EXECUTION_INSTRUCTIONS_TITLE = "Execution Instructions";
	private static final String TEXT_TITLE = "Text";
	private static final String PASS_THRU_TAGS_TITLE = "Pass Thru Tags";
	private static final String TIME_IN_FORCE_TITLE = "Time In Force";
	private static final String ORDER_TYPE_TITLE = "Order Type";
	private static final String ACCOUNT_TITLE = "Account";
	private static final String USER_DATA_TITLE = "User Data";
	private static final String SIDE_TITLE = "Side";
	private static final String LIMIT_PRICE_TITLE = "Limit Price";
	private static final String ORIGINAL_REQUEST_ID_TITLE = "Original Request Id";
	private static final String SESSION_NAME_TITLE = "Session Name";
	private static final String UPDATED_TIME_TITLE = "Updated Time";
	private static final String CREATED_TIME_TITLE = "Created Time";
	private static final String DESTINATION_TITLE = "Destination";
	private static final String RULE_80A_TITLE = "Rule 80A";
	private static final String LOCATE_ID_TITLE = "Locate Id";
	private static final String LOCATE_BROKER_TITLE = "Locate Broker";
	private static final String LOCATE_BROKER_REQD_TITLE = "Locate Broker Reqd";
	private static final String ORDER_CAPACITY_TITLE = "Order Capacity";
	private static final String SYMBOL_SFX_TITLE = "Symbol Sfx";
	private static final String LATEST_TITLE = "Latest";
	private static final String SECURITY_REF_ID_TITLE = "Security Ref Id";
	private static final String SECURITY_ID_TITLE = "Security Id";
	private static final String REQUEST_ID_TITLE = "Request Id";
	private static final String ID_TYPE_TITLE = "Id Type";
	private static final String GROUP_ID_TITLE = "Group Id";
	private static final String EXTERNAL_ID_TITLE = "External Id";
	private static final String STATUS_TITLE = "Status";
	private static final String EXEC_VALUE_TITLE = "Exec Value";
	private static final String CELL_NUMBER = "cell_number";
	private static final String EXEC_QTY_TITLE = "Exec Qty";
	private static final String TARGET_TITLE = "Target";
	private static final String ID_TITLE = "Id";
	private static final String SYMBOL_TITLE = "Symbol";
	private static final String CANCELLED = "cancelled";
	private static final String OPEN = "open";
	private static final String RSC_BTN_CANCEL_UP_GIF = "rsc/btn_cancel_up.gif";
	private static final String RSC_BTN_CANCEL_DN_GIF = "rsc/btn_cancel_dn.gif";
	private static final String CANCEL_TITLE = "Cancel";
	private static final String ACTIVE = "active";
	private static final String PAUSED = "paused";
	private static final String RSC_BTN_PAUSE_UP_GIF = "rsc/btn_pause_up.gif";
	private static final String RSC_BTN_PAUSE_DN_GIF = "rsc/btn_pause_dn.gif";
	private static final String PAUSE_TITLE = "Pause";
	private static final String HIDDEN = "hidden";
	private static final String RSC_BTN_SHOW_UP_GIF = "rsc/btn_show_up.gif";
	private static final String RSC_BTN_SHOW_DN_GIF = "rsc/btn_show_dn.gif";
	private static final String CELL_BUTTON = "cell_button";
	private static final String SHOW_TITLE = "Show";
	private static final String SHOW = "show";
	private static final String CHILD_ORDERS = "Child Orders";
	private static final String CANCEL_ID = "cancel";
	private static final String PAUSE_ID = "pause";
	private static final String SHOWN_ID = "shown";
	private static final String TIME_IN_FORCE_ID = "timeInForce";
	private static final String ORDER_TYPE_ID = "orderType";
	private static final String ACCOUNT_ID = "account";
	private static final String USER_DATA_ID = "userData";
	private static final String SIDE_ID = "side";
	private static final String LIMIT_PX_ID = "limitPx";
	private static final String ORIG_REQUEST_ID_ID = "origRequestId";
	private static final String SESSION_NAME_ID = "sessionName";
	private static final String UPDATED_TIME_ID = "updatedTime";
	private static final String CREATED_TIME_ID = "createdTime";
	private static final String DESTINATION_ID = "destination";
	private static final String RULE80A_ID = "rule80A";
	private static final String LOCATE_ID_ID = "locateId";
	private static final String LOCATE_BROKER_ID_ID = "locateBroker";
	private static final String LOCATE_BROKER_REQUIRED_ID = "locateBrokerRequired";
	private static final String ORDER_CAPACITY_ID = "orderCapacity";
	private static final String SYMBOL_SFX_ID = "symbolSfx";
	private static final String IS_LATEST_ID = "isLatest";
	private static final String SECURITY_REF_ID_ID = "securityRefId";
	private static final String SECURITY_ID_ID = "securityId";
	private static final String REQUEST_ID_ID = "requestId";
	private static final String ID_TYPE_ID = "idType";
	private static final String ORDER_GROUP_ID_ID = "orderGroupId";
	private static final String EXTERNAL_ORDER_ID_ID = "externalOrderId";
	private static final String ORDER_STATUS_ID = "orderStatus";
	private static final String TOTAL_EXEC_VALUE_ID = "totalExecValue";
	private static final String TOTAL_EXEC_QTY_ID = "totalExecQty";
	private static final String ORDER_QTY_ID = "orderQty";
	private static final String SYMBOL_ID = "symbol";
	private static final String ID_ID = "id";

	private OrdersService service;
	private BasicPortletSocket fromParentOrdersSocket;
	private Set<String> selectedParentGroupIds;
	private Map<String, Row> rowsMap;
	private BasicPortletSocket showExecutionsSocket;

	private BasicWebColumn showColumn;

	public ChildOrdersPortlet(PortletConfig config) {
		super(config, null);
		this.selectedParentGroupIds = new HashSet<String>();
		this.rowsMap = new HashMap<String, Row>();

		String[] ids = { ID_ID, SYMBOL_ID, ORDER_QTY_ID, TOTAL_EXEC_QTY_ID, TOTAL_EXEC_VALUE_ID, ORDER_STATUS_ID, EXTERNAL_ORDER_ID_ID, ORDER_GROUP_ID_ID, ID_TYPE_ID,
				REQUEST_ID_ID, SECURITY_ID_ID, SECURITY_REF_ID_ID, IS_LATEST_ID, SYMBOL_SFX_ID, ORDER_CAPACITY_ID, LOCATE_BROKER_REQUIRED_ID, LOCATE_BROKER_ID_ID, LOCATE_ID_ID,
				RULE80A_ID, DESTINATION_ID, CREATED_TIME_ID, UPDATED_TIME_ID, SESSION_NAME_ID, ORIG_REQUEST_ID_ID, LIMIT_PX_ID, SIDE_ID, USER_DATA_ID, ACCOUNT_ID, ORDER_TYPE_ID,
				TIME_IN_FORCE_ID, PASS_THRU_TAGS_ID, TEXT_ID, EXEC_INSTRUCTIONS_ID, SLICE_TYPE_ID, SHOWN_ID, PAUSE_ID, CANCEL_ID, "order" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle(CHILD_ORDERS);
		SmartTable st = new BasicSmartTable(inner);
		service = (OrdersService) getManager().getService(OrdersService.ID);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());

		this.showColumn = table.addColumn(true, "Show", SHOWN_ID, service.getShowButtonWebCellFormatter());
		table.addColumn(true, "Symbol", SYMBOL_ID, service.getSymbolWebCellFormatter());
		table.addColumn(true, "Side", SIDE_ID, service.getSideFormatter());
		table.addColumn(true, "Target", ORDER_QTY_ID, service.getQuantityFormatter());
		table.addColumn(true, "Limit Px", LIMIT_PX_ID, service.getPriceWebCellFormatter());
		table.addColumn(true, "Exec Qty", TOTAL_EXEC_QTY_ID, service.getQuantityFormatter());
		table.addColumn(true, "Exec Value", TOTAL_EXEC_VALUE_ID, service.getPriceWebCellFormatter());
		table.addColumn(true, "Status", ORDER_STATUS_ID, service.getOrderStatusWebCellFormatter());
		table.addColumn(true, "Created", CREATED_TIME_ID, service.getTimeWebCellFormatter());
		table.addColumn(true, "Updated", UPDATED_TIME_ID, service.getTimeWebCellFormatter());
		table.addColumn(true, "Session", SESSION_NAME_ID, service.getTextWebCellFormatter(50));
		table.addColumn(true, "Type", ORDER_TYPE_ID, service.getOrderTypeWebCellFormatter());
		table.addColumn(true, "TIF", TIME_IN_FORCE_ID, service.getTimeInForceFormatter());

		table.addColumn(false, SLICE_TYPE_TITLE, SLICE_TYPE_ID, service.getTextWebCellFormatter(50));
		table.addColumn(false, EXTERNAL_ID_TITLE, EXTERNAL_ORDER_ID_ID, service.getTextWebCellFormatter(50));
		table.addColumn(false, ORDER_CAPACITY_TITLE, ORDER_CAPACITY_ID, service.getTextWebCellFormatter(50));
		table.addColumn(false, LOCATE_BROKER_REQD_TITLE, LOCATE_BROKER_REQUIRED_ID, service.getTextWebCellFormatter(50));
		table.addColumn(false, LOCATE_ID_TITLE, LOCATE_ID_ID, service.getTextWebCellFormatter(50));
		table.addColumn(false, RULE_80A_TITLE, RULE80A_ID, service.getTextWebCellFormatter(50));
		table.addColumn(false, DESTINATION_TITLE, DESTINATION_ID, service.getTextWebCellFormatter(50));
		table.addColumn(false, ORIGINAL_REQUEST_ID_TITLE, ORIG_REQUEST_ID_ID, service.getTextWebCellFormatter(50));
		table.addColumn(false, USER_DATA_TITLE, USER_DATA_ID, service.getTextWebCellFormatter(50));
		table.addColumn(false, ACCOUNT_TITLE, ACCOUNT_ID, service.getTextWebCellFormatter(50));
		table.addColumn(false, PASS_THRU_TAGS_TITLE, PASS_THRU_TAGS_ID, service.getPassThruWebCellFormatter());
		table.addColumn(false, TEXT_TITLE, TEXT_ID, service.getTextWebCellFormatter(50));
		table.addColumn(false, EXECUTION_INSTRUCTIONS_TITLE, EXEC_INSTRUCTIONS_ID, service.getTextWebCellFormatter(50));
		table.addColumn(false, ID_TITLE, ID_ID, service.getTextWebCellFormatter(50));
		table.addColumn(false, SYMBOL_SFX_TITLE, SYMBOL_SFX_ID, service.getTextWebCellFormatter(50));
		table.addColumn(false, GROUP_ID_TITLE, ORDER_GROUP_ID_ID, service.getTextWebCellFormatter(50));
		table.addColumn(false, REQUEST_ID_TITLE, REQUEST_ID_ID, service.getTextWebCellFormatter(50));
		table.addColumn(false, SECURITY_ID_TITLE, SECURITY_ID_ID, service.getTextWebCellFormatter(50));
		table.addColumn(false, LATEST_TITLE, IS_LATEST_ID, service.getTextWebCellFormatter(50));
		super.setTable(table);
		//table.addMenuListener(this);
		this.fromParentOrdersSocket = addSocket(false, "childOrders", "Show selected child orders", true, null, CH.s(ShowChildOrdersInterPortletMessage.class));
		this.showExecutionsSocket = addSocket(true, "childExecutions", "Show selected child executions", true, CH.s(ShowChildExecutionsInterPortletMessage.class), null);
		service.addOmsPortlet(this);
		table.setMenuFactory(this);
	}

	public void onClosed() {
		super.onClosed();
		service.removeOmsPortlet(this);
	}

	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
		if (col == showColumn)
			onShow(row, !row.get(SHOWN_ID, Boolean.class));
	}

	private void onShow(Row row, boolean show) {
		row.put(SHOWN_ID, show);
		if (showExecutionsSocket.hasConnections()) {
			List<WebOmsOrder> orders = CH.l((WebOmsOrder) row.get("order"));
			if (show)
				showExecutionsSocket.sendMessage(new ShowChildExecutionsInterPortletMessage(orders, null));
			else
				showExecutionsSocket.sendMessage(new ShowChildExecutionsInterPortletMessage(null, orders));
		}
	}

	@Override
	public void onDisconnect(PortletSocket localSocket, PortletSocket remoteSocket) {
		//setConnected(false);
	}

	@Override
	public void onConnect(PortletSocket localSocket, PortletSocket remoteSocket) {

		TableList tableRows = (TableList) getTable().getTable().getRows();
		if (localSocket == this.showExecutionsSocket) {
			List<WebOmsOrder> orders = new ArrayList<WebOmsOrder>();
			for (Row row : getTable().getTable().getRows()) {
				if (row.get(SHOWN_ID, Boolean.class)) {
					WebOmsOrder order = (WebOmsOrder) row.get("order");
					orders.add(order);
				}
			}
			localSocket.sendMessage(new ShowChildExecutionsInterPortletMessage(orders, null));
		} else if (localSocket == this.fromParentOrdersSocket) {
			getTable().clear();
			this.rowsMap.clear();
			this.selectedParentGroupIds.clear();
			if (this.showExecutionsSocket.hasConnections())
				this.showExecutionsSocket.sendMessage(new ShowChildExecutionsInterPortletMessage(null, null));
		}

	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket origin, InterPortletMessage message) {
		ShowChildOrdersInterPortletMessage showMessage = (ShowChildOrdersInterPortletMessage) message;
		List<WebOmsOrder> show = showMessage.getShowOrders();
		List<WebOmsOrder> childrenShown = new ArrayList<WebOmsOrder>();
		List<WebOmsOrder> childrenHidden = new ArrayList<WebOmsOrder>();
		if (show != null)
			for (WebOmsOrder o : show) {
				boolean added = this.selectedParentGroupIds.add(o.getOrder().getOrderGroupId());
				if (added) {
					Collection<WebOmsOrder> orders = o.getChildren().values();
					if (orders != null) {
						for (WebOmsOrder order : orders) {
							if (!this.rowsMap.containsKey(order.getOrder().getId())) {
								this.rowsMap.put(order.getOrder().getId(), addOrderRow(order));
								//childrenShown.add(order);
							}
						}
					}
				}
			}
		List<WebOmsOrder> hide = showMessage.getHideOrders();
		if (hide != null)
			for (WebOmsOrder o : hide) {
				boolean removed = this.selectedParentGroupIds.remove(o.getOrder().getOrderGroupId());
				if (removed) {
					for (WebOmsOrder child : o.getChildren().values()) {
						Row row = this.rowsMap.remove(child.getOrder().getId());
						if (row != null) {
							getTable().getTable().removeRow(row);
							if ((Boolean) row.get(SHOWN_ID))
								childrenHidden.add(child);
						}
					}
				}
			}
		if (hide == null && show == null) {
			this.getTable().clear();
			this.rowsMap.clear();
			this.selectedParentGroupIds.clear();
			showExecutionsSocket.sendMessage(new ShowChildExecutionsInterPortletMessage(null, null));
		} else if (showExecutionsSocket.hasConnections() && (childrenShown.size() > 0 || childrenHidden.size() > 0))
			showExecutionsSocket.sendMessage(new ShowChildExecutionsInterPortletMessage(childrenShown, childrenHidden));
	}

	@Override
	public void onOrder(WebOmsOrder order) {
		if (!order.getIsSliceOrder())
			return;
		Order o = order.getOrder();
		if (!this.fromParentOrdersSocket.hasConnections() || this.selectedParentGroupIds.contains(o.getOrderGroupId())) {
			Row row = this.rowsMap.get(o.getId());
			if (row == null) {
				this.rowsMap.put(o.getId(), addOrderRow(order));
			} else {
				Set<String> keys = row.getTable().getColumnIds();
				for (ValuedParam vp : o.askExistingValuedParams()) {
					if (!keys.contains(vp.getName()))
						continue;
					row.put(vp.getName(), vp.getValue(o));
				}
			}
		}
	}

	private Row addOrderRow(WebOmsOrder order) {
		Order o = order.getOrder();
		return getTable().getTable().getRows().addRow(o.getId(), o.getSymbol(), o.getOrderQty(), o.getTotalExecQty(), o.getTotalExecValue(), o.getOrderStatus(),
				o.getExternalOrderId(), o.getOrderGroupId(), o.getIDType(), o.getRequestId(), o.getSecurityID(), o.getSecurityRefId(), o.getIsLatest(), o.getSymbolSfx(),
				o.getOrderCapacity(), o.getLocateBrokerRequired(), o.getLocateBroker(), o.getLocateId(), o.getRule80A(), o.getDestination(), o.getCreatedTime(), o.getUpdatedTime(),
				o.getSessionName(), o.getOrigRequestId(), o.getLimitPx(), o.getSide(), o.getUserData(), o.getAccount(), o.getOrderType(), o.getTimeInForce(), o.getPassThruTags(),
				o.getText(), o.getExecInstructions(), o.getSliceType(), false, false, false, order);
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		List<Row> selectedRows = table.getSelectedRows();
		if (CANCEL_ID.equals(action)) {
			for (Row row : selectedRows) {
				Integer val = (Integer) row.get(ORDER_STATUS_ID);
				if (val != null) {
					if (!OmsClientUtils.canCancel(val.intValue()))
						continue;
				}
				this.service.cancelChildOrder((WebOmsOrder) row.get("order"));
			}
		} else if (COPY_ID.equals(action)) {
			for (Row row : selectedRows) {
				String id = row.get(ID_ID).toString();
				WebOmsOrder worder = (WebOmsOrder) row.get("order");
				Order order = worder.getOrder();
				Order dest = getManager().getGenerator().nw(Order.class);
				OrdersPortletHelper.copy(order, dest);
			}
		} else if (SHOW.equals(action)) {
			for (Row row : selectedRows) {
				boolean showing = row.get(SHOWN_ID, Boolean.class);
				if (!showing) {
					onShow(row, true);
				}
			}
		} else if (OrdersPortletHelper.HIDE_ID.equals(action)) {
			for (Row row : selectedRows) {
				boolean showing = row.get(SHOWN_ID, Boolean.class);
				if (showing) {
					onShow(row, false);
				}
			}
		}
	}

	@Override
	public WebMenu createMenu(WebTable table) {
		return OrdersPortletHelper.createMenu(table, false);
	}

	public static class Builder extends AbstractPortletBuilder<ChildOrdersPortlet> {

		private static final String ID = "childOrdersPortlet";

		public Builder() {
			super(ChildOrdersPortlet.class);
		}

		@Override
		public ChildOrdersPortlet buildPortlet(PortletConfig portletManager) {
			return new ChildOrdersPortlet(portletManager);
		}

		@Override
		public String getPortletBuilderName() {
			return "portlet.childorders";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}
	}

	@Override
	public void onExecution(WebOmsExecution execution) {
		// TODO Auto-generated method stub

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
