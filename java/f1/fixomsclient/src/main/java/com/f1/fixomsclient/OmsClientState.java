package com.f1.fixomsclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.container.Partition;
import com.f1.container.impl.BasicState;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.OmsNotification;
import com.f1.pofo.oms.Order;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.concurrent.HasherMap;

/**
 * An implementation of the {@link OmsClientOrdersExecutions} which keeps the SOW for all order and exections for attached OMS and properly handles pending states (with rollback on
 * rejection). THREADING NOTE: when accessing directly, be sure to obtain a lock first via {@link #getPartition()}.
 * {@link Partition#lockForRead(long, java.util.concurrent.TimeUnit)}, and {@link Partition#unlockForRead()}
 * 
 */
public class OmsClientState extends BasicState implements OmsClientOrdersExecutions {
	private static final Logger log = Logger.getLogger(OmsClientState.class.getName());

	private class OE {
		public HasherMap<String, PendableOrder> orders = new HasherMap<String, PendableOrder>();
		public Map<String, Execution> executions = new HashMap<String, Execution>();
	}

	private HasherMap<String, OE> systemToOrdersExecutions = new HasherMap<String, OE>();
	private boolean snapshotProcessed = false;
	private List<OmsNotification> pendingSnapshotQueue = new ArrayList<OmsNotification>();

	public Order getOrder(Order order) {
		String system = order.getSourceSystem();
		String id = OH.assertNotNull(order.getId());
		return getOrder(system, id);
	}

	public Order getOrigOrder(Order order) {
		String system = order.getSourceSystem();
		String id = OH.assertNotNull(order.getId());
		return getOrigOrder(system, id);
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
			this.origOrder = this.order;
			this.order = order;
		}

		public Order getOrder() {
			return order;
		}

		public Order getOrigOrder() {
			return origOrder;
		}

		public void pendingAccepted() {
			this.origOrder = null;
		}

		public void pendingRejected() {
			if (origOrder == null)
				throw new RuntimeException("not in pending state");
			this.order = origOrder;
			origOrder = null;
		}
	}

	/**
	 * @return the execution in the SOW based on the supplied executions sourcesystem and execution id
	 */
	public Execution getExecution(Execution execution) {
		String system = execution.getSourceSystem();
		String id = OH.assertNotNull(execution.getId());
		return getOE(system).executions.get(id);
	}

	/**
	 * @return the order in the SOW based on the supplied sourcesystem and order id
	 */
	public Order getOrder(String system, String id) {
		PendableOrder po = getOE(system).orders.get(id);
		return po == null ? null : po.getOrder();
	}

	/**
	 * @return the original (distinc from getorder(...) when in pending state) order in the SOW based on the supplied sourcesystem and order id
	 */
	public Order getOrigOrder(String system, String id) {
		PendableOrder po = getOE(system).orders.get(id);
		return po == null ? null : po.getOrigOrder();
	}

	/**
	 * @return the execution in the SOW based on the supplied sourcesystem and execution id
	 */
	public Execution getExecution(String system, String id) {
		return getOE(system).executions.get(id);
	}

	/**
	 * WARNING: for internal use
	 */
	public void pendingAccepted(Order order) {
		String system = order.getSourceSystem();
		String id = OH.assertNotNull(order.getId());
		getOE(system).orders.get(id).pendingAccepted();
	}

	/**
	 * WARNING: for internal use
	 */
	public void pendingRejected(Order order) {
		String system = order.getSourceSystem();
		String id = OH.assertNotNull(order.getId());
		getOE(system).orders.get(id).pendingRejected();
	}

	/**
	 * WARNING: for internal use
	 */
	public void putOrder(Order order) {
		final String system = order.getSourceSystem();
		final String id = OH.assertNotNull(order.getId());
		final Entry<String, PendableOrder> e = getOE(system).orders.getOrCreateEntry(id);
		PendableOrder po = e.getValue();
		if (po != null)
			LH.info(log, "Order already exists: ", order);
		e.setValue(po = new PendableOrder(order));
	}

	/**
	 * WARNING: for internal use
	 */
	public void putExecution(Execution execution) {
		String system = execution.getSourceSystem();
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

	/**
	 * WARNING: for internal use
	 */
	public void pendingOrder(Order order) {
		String system = order.getSourceSystem();
		String id = OH.assertNotNull(order.getId());
		getOE(system).orders.get(id).setPending(order);
	}

	/**
	 * return all orders.
	 */
	@Override
	public Iterable<Order> getOrders() {
		List<Order> r = new ArrayList<Order>();
		for (OE oe : this.systemToOrdersExecutions.values())
			for (PendableOrder o : oe.orders.values())
				r.add(o.getOrder());
		return r;
	}

	/**
	 * return all executions.
	 */
	@Override
	public Iterable<Execution> getExecutions() {
		List<Execution> r = new ArrayList<Execution>();
		for (OE oe : this.systemToOrdersExecutions.values())
			r.addAll(oe.executions.values());
		return r;
	}

	public List<OmsNotification> snapshotProcessed() {
		if (isSnapshotProcessed())
			throw new IllegalStateException("snapshot already processed");
		List<OmsNotification> r = pendingSnapshotQueue;
		pendingSnapshotQueue = null;
		return r;
	}

	public boolean isSnapshotProcessed() {
		return pendingSnapshotQueue == null;
	}

	public void addToPendingSnapshotQueue(OmsNotification action) {
		if (isSnapshotProcessed())
			throw new IllegalStateException("snapshot alread processed");
		pendingSnapshotQueue.add(action);
	}

	public boolean getHasConnected() {
		return hasConnected;
	}

	public void setHasConnected(boolean hasConnected) {
		this.hasConnected = hasConnected;
	}

	private boolean hasConnected = false;
	private String rootOrderId;

	public String getRootOrderId() {
		return rootOrderId;
	}

	public void setRootOrderId(String rootOrderId) {
		this.rootOrderId = rootOrderId;
	}

}
