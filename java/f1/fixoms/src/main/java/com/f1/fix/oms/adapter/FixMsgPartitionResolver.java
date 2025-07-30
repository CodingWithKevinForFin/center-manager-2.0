package com.f1.fix.oms.adapter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.f1.container.Container;
import com.f1.container.ContainerListener;
import com.f1.container.ContainerScope;
import com.f1.container.Partition;
import com.f1.container.PartitionController;
import com.f1.container.impl.BasicPartitionResolver;
import com.f1.fix.oms.schema.ClientOrder;
import com.f1.fix.oms.schema.OmsOrder;
import com.f1.pofo.fix.ChildMessage;
import com.f1.pofo.fix.FixCancelRequest;
import com.f1.pofo.fix.FixMsg;
import com.f1.pofo.fix.FixOrderReplaceReject;
import com.f1.pofo.fix.FixOrderReplaceRequest;
import com.f1.pofo.fix.FixOrderRequest;
import com.f1.pofo.fix.FixReport;
import com.f1.pofo.fix.FixRequest;
import com.f1.pofo.fix.VersionedMsg;
import com.f1.pofo.fix.child.FixChildOrderRequest;
import com.f1.pofo.oms.Order;
import com.f1.utils.LH;

/**
 * 
 * for a given message looks up the root order by resolving the order ids on the supplied action.
 * 
 */
public class FixMsgPartitionResolver extends BasicPartitionResolver<FixMsg> implements ContainerListener {
	// TODO:How to emulate the concurrentmap below with partition aliases? The
	// current implementation uses copyonwrite which will be very expensive
	ConcurrentHashMap<String, String> idMap = new ConcurrentHashMap<String, String>();

	public FixMsgPartitionResolver() {
		super(FixMsg.class, null);
	}

	@Override
	public Object getPartitionId(FixMsg action) {

		String rootId = action.getRootOrderId();

		if (action instanceof FixChildOrderRequest) {// Root Id should never be
														// null on a new
														// ChildOrderRequest
			String childId = ((FixChildOrderRequest) action).getChildId().getOrderId();
			idMap.put(childId, rootId);
			return rootId;
		}

		if (rootId != null) { // If it is populated, use it
			return rootId;
		}

		if (action instanceof ChildMessage) {
			return idMap.get(((ChildMessage) action).getChildId().getOrderId()); // This
																					// should
																					// already
																					// be
																					// in
																					// the
																					// map
																					// due
																					// to
																					// outbound
		}

		// Now come the unknowns
		String id = null;
		String newid = null;
		if (action instanceof FixRequest) {
			if (action instanceof VersionedMsg) {
				id = ((VersionedMsg) action).getRefId();
				newid = ((FixRequest) action).getRequestId();
			} else
				id = ((FixRequest) action).getRequestId();
		} else if (action instanceof FixReport) {
			id = ((FixReport) action).getRequestId();
		} else if (action instanceof VersionedMsg) {
			id = ((VersionedMsg) action).getRefId();
		} else if (action instanceof FixOrderReplaceReject) {
			id = ((FixOrderReplaceReject) action).getRefId();
		}

		String mappedId = idMap.get(id);
		if (mappedId != null) {
			if (newid != null)
				idMap.put(newid, mappedId);
			return mappedId;
		}
		Partition state = getContainer().getPartitionController().getPartition(id);
		if (state != null) {
			if (newid != null)
				idMap.put(newid, id); // TODO:revisit this and see if it is ever
										// possible
			return id;
		} else {
			if (action instanceof FixOrderRequest || action instanceof FixOrderReplaceRequest || action instanceof FixCancelRequest) {
				id = ((FixRequest) action).getRequestId();
				mappedId = getContainer().getServices().getTicketGenerator("OMSOrderState").createNextId().toString();
				idMap.put(id, mappedId);
			}
			return mappedId;
		}
	}

	@Override
	public void onPreStart(Container container) {
	}

	@Override
	public void onPostStart(Container container) {
		Map<String, String> m = new HashMap<String, String>();
		PartitionController pc = getContainer().getPartitionController();
		for (Object partitionId : pc.getPartitions()) {
			Partition p = pc.getPartition(partitionId);
			if (p == null)
				continue;
			OmsOrderState state = (OmsOrderState) p.getState(OmsOrderState.class);
			if (state == null)
				continue;
			for (String requestId : state.getRequestIds()) {
				OmsOrder order = state.getOrder(requestId);
				Order fo = order.getFixOrder();
				if (fo != null) {
					if (order instanceof ClientOrder) {
						FixRequest pending = order.getPending();
						m.put(fo.getRequestId(), (String) partitionId);
						if (pending != null) {
							m.put(pending.getRequestId(), (String) partitionId);
						}
					} else {
						m.put(fo.getId(), (String) partitionId);
					}
				}
			}
		}

		for (Map.Entry<String, String> e : m.entrySet())
			LH.fine(log, "Recovered Id Mappings: ", e.getKey(), " --> ", e.getValue());
		idMap.putAll(m);
	}

	@Override
	public void onPreStop(Container container) {
	}

	@Override
	public void onPostStop(Container container) {
	}

	@Override
	public void onPreStartDispatching(Container container) {
	}

	@Override
	public void onPostStartDispatching(Container container) {
	}

	@Override
	public void onPreStopDispatching(Container container) {
	}

	@Override
	public void onPostStopDispatching(Container container) {
	}

	@Override
	public void onContainerScopeAdded(ContainerScope abstractContainerScope) {
	}

}
