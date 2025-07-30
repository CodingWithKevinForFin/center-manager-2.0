package com.f1.fix.oms.adapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.container.impl.BasicState;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.Order;
import com.f1.utils.OH;
import com.f1.utils.concurrent.HasherMap;

public class OmsDbState extends BasicState {

	private class OE {
		public HasherMap<String, PendableOrder> orders = new HasherMap<String, PendableOrder>();
		public Map<String, Execution> executions = new HashMap<String, Execution>();
	}
	private HasherMap<String, OE> systemToOrdersExecutions = new HasherMap<String, OE>();

	public Order getOrder(Order order) {
		String system = OH.assertNotNull(order.getSourceSystem());
		String id = OH.assertNotNull(order.getId());
		return getOrder(system, id);
	}

	public class PendableOrder {
		private Order order;
		private Order origOrder;

		public PendableOrder(Order order) {
			this.order = order;
		}
		public void setPending(Order order) {
			if (origOrder != null)
				throw new RuntimeException("already in pending state");
			this.origOrder = order;
			this.order = order;
		}
		public Order getOrder() {
			return order;
		}
		public void pendingAccepted() {
			this.origOrder = null;
		}
		public void pendingRejected() {
			if (origOrder != null)
				throw new RuntimeException("not in pending state");
			this.order = origOrder;
		}
	}

	public Execution getExecution(Execution execution) {
		String system = OH.assertNotNull(execution.getSourceSystem());
		String id = OH.assertNotNull(execution.getId());
		return getOE(system).executions.get(id);
	}

	public Order getOrder(String system, String id) {
		PendableOrder po = getOE(system).orders.get(id);
		return po == null ? null : po.getOrder();
	}

	public Execution getExecution(String system, String id) {
		return getOE(system).executions.get(id);
	}

	public void pendingAccepted(Order order) {
		String system = OH.assertNotNull(order.getSourceSystem());
		String id = OH.assertNotNull(order.getId());
		getOE(system).orders.get(id).pendingAccepted();
	}
	public void pendingRejected(Order order) {
		String system = OH.assertNotNull(order.getSourceSystem());
		String id = OH.assertNotNull(order.getId());
		getOE(system).orders.get(id).pendingRejected();
	}

	public void putOrder(Order order) {
		final String system = OH.assertNotNull(order.getSourceSystem());
		final String id = OH.assertNotNull(order.getId());
		final Entry<String, PendableOrder> e = getOE(system).orders.getOrCreateEntry(id);
		PendableOrder po = e.getValue();
		if (po != null)
			throw new RuntimeException("Order already exists: " + order);
		e.setValue(po = new PendableOrder(order));
	}

	public void putExecution(Execution execution) {
		String system = OH.assertNotNull(execution.getSourceSystem());
		String id = OH.assertNotNull(execution.getId());
		getOE(system).executions.put(id, execution);
	}

	private OE getOE(String system) {
		java.util.Map.Entry<String, OE> e = systemToOrdersExecutions.getOrCreateEntry(system);
		OE r = e.getValue();
		if (r == null)
			e.setValue(r = new OE());
		return r;
	}

	public void pendingOrder(Order order) {
		String system = OH.assertNotNull(order.getSourceSystem());
		String id = OH.assertNotNull(order.getId());
		getOE(system).orders.get(id).setPending(order);
	}
}
