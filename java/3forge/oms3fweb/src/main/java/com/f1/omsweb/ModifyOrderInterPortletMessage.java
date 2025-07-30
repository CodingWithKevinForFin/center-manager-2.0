package com.f1.omsweb;

import com.f1.pofo.oms.Order;
import com.f1.suite.web.portal.InterPortletMessage;

public class ModifyOrderInterPortletMessage implements InterPortletMessage {

	final private Order order;

	public ModifyOrderInterPortletMessage(Order order) {
		this.order = order;
	}

	public Order getOrder() {
		return order;
	}
}
