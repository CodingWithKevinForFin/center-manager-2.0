package com.f1.fix.oms.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.base.ObjectGeneratorForClass;
import com.f1.base.ValuedListenable;
import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.fix.oms.schema.FixCopyUtil;
import com.f1.fix.oms.schema.OmsOrder;
import com.f1.persist.structs.PersistableHashMap;
import com.f1.pofo.fix.FixRequest;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.OmsNotification;
import com.f1.pofo.oms.Order;
import com.f1.utils.LH;

/**
 * 
 * inspectes a transaction log during the processing of an OmsAction and populates the OmsNotification with all changes to orders / executions.
 * 
 */
public class OmsTransactionBundler extends BasicProcessor<OmsNotification, OmsOrderState> {

	ObjectGeneratorForClass<Order> og;
	ObjectGeneratorForClass<Execution> execg;

	public final OutputPort<OmsNotification> output = newOutputPort(OmsNotification.class);

	public OmsTransactionBundler() {
		super(OmsNotification.class, OmsOrderState.class);
	}

	public void start() {
		super.start();
		og = getGenerator(Order.class);
		execg = getGenerator(Execution.class);
	}

	@Override
	public void processAction(OmsNotification action, OmsOrderState state, ThreadScope threadScope) throws Exception {

		List<Order> addedOrders = null;
		List<Order> changedOrders = null;
		List<FixRequest> pendingRequests = null;
		List<Execution> addedExecutions = null;

		for (ValuedListenable l : state.getTransactionListener().getAdded()) {
			if (l instanceof Order) {
				if (addedOrders == null)
					addedOrders = new ArrayList<Order>(2);
				Order o = (Order) (((Order) l).clone());
				o.setPassThruTags(new PersistableHashMap<Integer, String>());
				FixCopyUtil.copyMap(o.getPassThruTags(), ((Order) l).getPassThruTags());
				addedOrders.add(o);
			} else if (l instanceof Execution) {
				if (addedExecutions == null)
					addedExecutions = new ArrayList<Execution>(2);
				addedExecutions.add((Execution) ((Execution) l).clone());
			}
		}

		for (ValuedListenable l : state.getTransactionListener().getChanged()) {
			if (l instanceof OmsOrder) {
				Map map = state.getTransactionListener().getChangedFields(l);
				if (map.containsKey("pending")) {
					if (pendingRequests == null)
						pendingRequests = new ArrayList<FixRequest>(2);
					FixRequest pending = ((OmsOrder) l).getPending();
					if (pending != null) {
						pending.setRootOrderId(((OmsOrder) l).getFixOrder().getId()); // TODO:
																						// This
																						// is
																						// a
																						// pretty
																						// big
																						// hack
						pendingRequests.add(pending);
					}
				}
			}
			if (l instanceof Order) {
				if (changedOrders == null)
					changedOrders = new ArrayList<Order>(5);
				Map map = state.getTransactionListener().getChangedFields(l);
				Order o = og.nw();
				Order current = (Order) l;
				for (Object key : map.keySet()) {
					if (key instanceof String) {
						if (key.equals("passThruTags")) {
							o.setPassThruTags(new PersistableHashMap<Integer, String>());
							FixCopyUtil.copyMap(o.getPassThruTags(), ((Order) l).getPassThruTags());
						} else {
							o.put((String) key, current.ask((String) key));
						}
					} else
						LH.warning(log, "Don't know how to handle key: ", key, "=", map.get(key));
				}
				o.setId(current.getId());
				changedOrders.add(o);
			}
		}
		state.getTransactionListener().clearChanges();
		action.setAddedOrders(addedOrders);
		action.setChangedOrders(changedOrders);
		action.setPendingRequests(pendingRequests);
		action.setAddedExecutions(addedExecutions);
		output.send(action, threadScope);
	}
}
