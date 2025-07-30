package com.f1.fix.oms.adapter;

import java.util.ArrayList;
import java.util.List;

import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.fix.oms.schema.ClientOrder;
import com.f1.fix.oms.schema.OmsOrder;
import com.f1.pofo.fix.FixRequest;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsNotification;
import com.f1.pofo.oms.Order;
import com.f1.povo.standard.ObjectMessage;
import com.f1.suite.utils.ObjectMessagePartitionResolver;
import com.f1.utils.CH;
import com.f1.utils.LH;

/**
 * processes a snapshot and drains any pending deltas queued during the snapshot request.
 * 
 */
public class OrderSnapshotRequestProcessor extends BasicRequestProcessor<ObjectMessage, OmsOrderState, OmsNotification> {

	public OrderSnapshotRequestProcessor() {
		super(ObjectMessage.class, OmsOrderState.class, OmsNotification.class, new ObjectMessagePartitionResolver());
	}

	@Override
	protected OmsNotification processRequest(RequestMessage<ObjectMessage> action, OmsOrderState state, ThreadScope threadScope) {
		OmsNotification resp = nw(OmsNotification.class);
		resp.setType(OmsAction.SNAPSHOT);
		resp.setRootOrderID(state.getClientOrder().getFixOrder().getId());
		ClientOrder order = state.getClientOrder();
		List<Order> orders = new ArrayList<Order>();
		List<FixRequest> requests = new ArrayList<FixRequest>();
		List<Execution> executions = new ArrayList<Execution>();
		populate(order, orders, requests, executions);
		if (CH.isntEmpty(state.getSlices()))
			for (OmsOrder o : state.getSlices()) {
				populate(o, orders, requests, executions);
			}
		resp.setAddedExecutions(executions);
		resp.setAddedOrders(orders);
		resp.setPendingRequests(requests);
		return resp;
	}

	private void populate(OmsOrder o, List<Order> orders, List<FixRequest> requests, List<Execution> executions) {
		if (o == null) {
			LH.severe(log, "Skipping null order.");
			return;
		}
		orders.add(o.getFixOrder().clone());
		if (o.getPending() != null) {
			o.getPending().setRootOrderId(o.getFixOrder().getId());
			requests.add(o.getPending()); // TODO: Should we clone this given
											// that its fixed more or less
		}
		if (o.getExecutions() != null) {
			for (Execution e : o.getExecutions().values()) {
				executions.add(e.clone());
			}
		}
	}
}
