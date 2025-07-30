package com.f1.omsweb;

import java.util.HashMap;
import java.util.Map;

import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.Order;
import com.f1.pofo.oms.SliceType;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.structs.MapInMap;

public class WebOmsManager {

	private static final WebOmsFundamentals UNKNOWN_FUNDAMENTALS = new WebOmsFundamentals("UNKNOWN");
	private Map<String, WebOmsOrder> parentOrders = new HashMap<String, WebOmsOrder>();
	private Map<String, WebOmsOrder> parentOrdersByGroupId = new HashMap<String, WebOmsOrder>();
	private Map<String, WebOmsOrder> childOrders = new HashMap<String, WebOmsOrder>();
	private MapInMap<String, String, WebOmsOrder> childOrdersByGroupId = new MapInMap<String, String, WebOmsOrder>();
	private Map<String, WebOmsExecution> executions = new HashMap<String, WebOmsExecution>();
	private MapInMap<String, String, WebOmsExecution> executionsByOrderId = new MapInMap<String, String, WebOmsExecution>();
	private Map<String, WebOmsFundamentals> sectorMap = new HashMap<String, WebOmsFundamentals>();

	public WebOmsOrder onOrder(Order order) {
		WebOmsOrder webOrder;
		if (order.getSliceType().equals(SliceType.SLICE)) {
			webOrder = childOrders.get(order.getId());
			if (webOrder != null) {
				webOrder.setOrder(order);
			} else {
				webOrder = new WebOmsOrder(order);
				webOrder.setFundamentals(OH.noNull(sectorMap.get(order.getSymbol()), UNKNOWN_FUNDAMENTALS));
				childOrders.put(order.getId(), webOrder);
				childOrdersByGroupId.putMulti(order.getOrderGroupId(), order.getId(), webOrder);
				WebOmsOrder parent = parentOrdersByGroupId.get(order.getOrderGroupId());
				if (parent != null) {
					parent.getChildren().put(order.getId(), webOrder);
					webOrder.setParent(parent);
				}
				Map<String, WebOmsExecution> execs = executionsByOrderId.get(order.getId());
				if (execs != null)
					for (WebOmsExecution exec : execs.values()) {
						webOrder.getExecutions().put(exec.getExecution().getId(), exec);
						exec.setParent(webOrder);
					}
			}
		} else {
			webOrder = parentOrders.get(order.getId());
			if (webOrder != null) {
				webOrder.setOrder(order);
			} else {
				webOrder = new WebOmsOrder(order);
				webOrder.setFundamentals(OH.noNull(sectorMap.get(order.getSymbol()), UNKNOWN_FUNDAMENTALS));
				parentOrders.put(order.getId(), webOrder);
				parentOrdersByGroupId.put(order.getOrderGroupId(), webOrder);
				Map<String, WebOmsOrder> children = childOrdersByGroupId.get(order.getOrderGroupId());
				if (CH.isntEmpty(children)) {
					for (WebOmsOrder child : children.values()) {
						webOrder.getChildren().put(child.getOrder().getId(), child);
						child.setParent(webOrder);
					}
				}
				Map<String, WebOmsExecution> execs = executionsByOrderId.get(order.getId());
				if (execs != null)
					for (WebOmsExecution exec : execs.values()) {
						webOrder.getExecutions().put(exec.getExecution().getId(), exec);
						exec.setParent(webOrder);
					}
			}
		}
		return webOrder;
	}
	public WebOmsExecution onExecution(Execution execution) {
		WebOmsExecution webExecution = executions.get(execution.getId());
		if (webExecution != null) {
			webExecution.setExecution(execution);
		} else {
			webExecution = new WebOmsExecution(execution);
			executions.put(execution.getId(), webExecution);
			executionsByOrderId.putMulti(execution.getOrderId(), execution.getId(), webExecution);
			WebOmsOrder order = parentOrders.get(execution.getOrderId());
			if (order == null)
				order = childOrders.get(execution.getOrderId());
			if (order != null) {
				order.getExecutions().put(execution.getId(), webExecution);
				webExecution.setParent(order);
			}
		}
		return webExecution;
	}

	public Map<String, WebOmsOrder> getParentOrders() {
		return parentOrders;
	}
	public Map<String, WebOmsOrder> getChildOrders() {
		return childOrders;
	}
	public Map<String, WebOmsExecution> getExecutions() {
		return executions;
	}
	public void setSectorMap(Map<String, String> sectorMap) {
		for (Map.Entry<String, String> e : sectorMap.entrySet())
			this.sectorMap.put(e.getKey(), new WebOmsFundamentals(e.getValue()));
	}
}
