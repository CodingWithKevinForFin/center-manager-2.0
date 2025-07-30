package com.f1.omsweb;

import java.util.HashMap;
import java.util.Map;

import com.f1.pofo.oms.Order;
import com.f1.pofo.oms.SliceType;

public class WebOmsOrder {

	private Order order;
	private Map<String, WebOmsOrder> children = new HashMap<String, WebOmsOrder>();
	private Map<String, WebOmsExecution> executions = new HashMap<String, WebOmsExecution>();
	private WebOmsOrder parent;
	private WebOmsFundamentals fundamentals;

	public WebOmsOrder(Order order) {
		this.order = order;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Map<String, WebOmsOrder> getChildren() {
		return children;
	}

	public void setChildren(Map<String, WebOmsOrder> children) {
		this.children = children;
	}

	public Map<String, WebOmsExecution> getExecutions() {
		return executions;
	}

	public void setExecutions(Map<String, WebOmsExecution> executions) {
		this.executions = executions;
	}

	public void setParent(WebOmsOrder parent) {
		this.parent = parent;
	}

	public WebOmsOrder getParent() {
		return this.parent;
	}

	public void setFundamentals(WebOmsFundamentals fundamentals) {
		this.fundamentals = fundamentals;
	}

	public WebOmsFundamentals getFundamentals() {
		return fundamentals;
	}

	public boolean getIsClientOrder() {
		return order.getSliceType() == SliceType.CLIENT_ORDER;
	}

	public boolean getIsSliceOrder() {
		return order.getSliceType() == SliceType.SLICE;
	}

}
