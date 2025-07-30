package com.f1.omsweb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.base.Row;
import com.f1.fixomsclient.OmsClientUtils;
import com.f1.pofo.oms.Order;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.table.WebTable;
import com.f1.utils.CH;
import com.f1.utils.SH;

public class OrdersPortletHelper {

	private static final String COPY_TITLE = "Copy";
	private static final String MODIFY_TITLE = "Modify";

	public static final String HIDE_ID = "hide";
	private static final String CANCEL_A_ORDER_TITLE = "Cancel a Order";

	public static WebMenu createMenu(WebTable table, boolean isPO) {
		List<WebMenuItem> children = new ArrayList<WebMenuItem>();
		WebMenu r = new BasicWebMenu("test", true, children);

		List<Row> selectedRows = table.getSelectedRows();
		if (selectedRows.size() == 0)
			return r;

		boolean canModify = selectedRows.size() == 1;

		int count = 0;
		for (Row row : selectedRows) {
			Integer val = (Integer) row.get(ParentOrdersPortlet.ORDER_STATUS_ID);
			if (val != null) {
				if (OmsClientUtils.canCancel(val.intValue()))
					++count;
			}
		}

		if (count == 0)
			children.add(new BasicWebMenuLink(ParentOrdersPortlet.CANCEL_TITLE, false, ParentOrdersPortlet.CANCEL_ID));
		else if (count == 1)
			children.add(new BasicWebMenuLink(CANCEL_A_ORDER_TITLE, true, ParentOrdersPortlet.CANCEL_ID));
		else
			children.add(new BasicWebMenuLink(SH.f("Cancel %d Orders", count), true, ParentOrdersPortlet.CANCEL_ID));
		children.add(new BasicWebMenuLink(MODIFY_TITLE, count > 0 && canModify, ParentOrdersPortlet.MODIFY_ID));

		if (selectedRows.size() == 1) {
			if (isPO) {
				children.add(new BasicWebMenuLink("Show COs for a Order", true, ParentOrdersPortlet.SHOW_ID));
				children.add(new BasicWebMenuLink("Hide COs for a Order", true, HIDE_ID));
			} else {
				children.add(new BasicWebMenuLink("Show CEs for a Order", true, ParentOrdersPortlet.SHOW_ID));
				children.add(new BasicWebMenuLink("Hide CEs for a Order", true, HIDE_ID));
			}
		} else {
			if (isPO) {
				children.add(new BasicWebMenuLink(String.format("Show COs for %d Orders", selectedRows.size()), true, ParentOrdersPortlet.SHOW_ID));
				children.add(new BasicWebMenuLink(String.format("Hide COs for %d Orders", selectedRows.size()), true, HIDE_ID));
			} else {
				children.add(new BasicWebMenuLink(String.format("Show CEs for %d Orders", selectedRows.size()), true, ParentOrdersPortlet.SHOW_ID));
				children.add(new BasicWebMenuLink(String.format("Hide CEs for %d Orders", selectedRows.size()), true, HIDE_ID));
			}
		}
		children.add(new BasicWebMenuLink(COPY_TITLE, true, ParentOrdersPortlet.COPY_ID));

		return r;
	}

	public static void copy(Order source, Order dest) {
		dest.setSecurityID(source.getSecurityID());
		dest.setIDType(source.getIDType());
		dest.setSymbol(source.getSymbol());
		dest.setSymbolSfx(source.getSymbolSfx());
		dest.setOrderCapacity(source.getOrderCapacity());
		dest.setRule80A(source.getRule80A());
		dest.setLocateBrokerRequired(source.getLocateBrokerRequired());
		dest.setLocateBroker(source.getLocateBroker());
		dest.setLocateId(source.getLocateId());
		dest.setSenderSubId(source.getSenderSubId());
		dest.setDestination(source.getDestination());
		dest.setRequestId(source.getRequestId());
		dest.setOrderQty(source.getOrderQty());
		dest.setOrderType(source.getOrderType());
		dest.setLimitPx(source.getLimitPx());
		dest.setTimeInForce(source.getTimeInForce());
		if (dest.getPassThruTags() == null)
			dest.setPassThruTags(new HashMap<Integer, String>());
		copyMap(dest.getPassThruTags(), source.getPassThruTags());
		dest.setSide(source.getSide());
		dest.setAccount(source.getAccount());
		dest.setText(source.getText());
		dest.setSessionName(source.getSessionName());
		dest.setExecInstructions(source.getExecInstructions());
	}
	public static void copyMap(Map<Integer, String> target, Map<Integer, String> source) {
		if (source == null)
			return;
		for (Integer i : CH.comm(target.keySet(), source.keySet(), true, false, false))
			target.remove(i);
		for (Integer i : CH.comm(target.keySet(), source.keySet(), false, true, true))
			target.put(i, source.get(i));
	}

}
