package com.f1.omsweb;

import java.util.List;

import com.f1.suite.web.portal.InterPortletMessage;

public class ShowChildOrdersInterPortletMessage implements InterPortletMessage {

	final private List<WebOmsOrder> showOrders, hideOrders;

	public ShowChildOrdersInterPortletMessage(List<WebOmsOrder> showOrders, List<WebOmsOrder> hideOrders) {
		this.showOrders = showOrders;
		this.hideOrders = hideOrders;
	}

	public List<WebOmsOrder> getShowOrders() {
		return showOrders;
	}

	public List<WebOmsOrder> getHideOrders() {
		return hideOrders;
	}

}
