package com.f1.fixomsclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.pofo.fix.FixOrderInfo;
import com.f1.pofo.fix.FixRequest;
import com.f1.pofo.fix.OrdStatus;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsNotification;
import com.f1.pofo.oms.Order;
import com.f1.pofo.oms.SliceType;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.VH;
import com.f1.utils.structs.Tuple2;

/**
 * 
 * Recieves {@link OmsNotification}s from the OMS and uses that to (a) update the clients internal SOW and braodcast notifications to {@link OmsClientOrdersExecutionsListener}s
 * 
 */
public class OmsClientNotificationProcessor extends BasicProcessor<OmsNotification, OmsClientState> {

	public final OutputPort<OmsClientNotification> out = newOutputPort(OmsClientNotification.class);
	private String sourceSystem;

	public static final int PENDING = OrdStatus.PENDING_CXL.mask | OrdStatus.PENDING_RPL.mask;

	public OmsClientNotificationProcessor() {
		super(OmsNotification.class, OmsClientState.class);
	}

	@Override
	public void processAction(OmsNotification action, OmsClientState state, ThreadScope threadScope) throws Exception {
		if (OH.eq(action.getRootOrderID(), state.getPartitionId()) && !state.isSnapshotProcessed()) {
			state.setRootOrderId((String) state.getPartitionId());
			state.setHasConnected(true);
			state.snapshotProcessed();
		}
		if (!state.isSnapshotProcessed()) {
			state.addToPendingSnapshotQueue(action);
			return;
		}
		OmsClientNotification ocm = nw(OmsClientNotification.class);
		ocm.setType(action.getType());
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("source_system", sourceSystem);
		boolean isNotAllChildCanceled = true;
		Map<String, FixRequest> pending = new HashMap<String, FixRequest>();

		if (CH.isntEmpty(action.getPendingRequests()))
			for (FixRequest request : action.getPendingRequests()) {
				pending.put(request.getRootOrderId(), request);
			}
		else
			isNotAllChildCanceled = false;

		if (CH.isntEmpty(action.getAddedOrders())) {
			for (Order o : action.getAddedOrders())
				state.putOrder(copy(o));
			ocm.setAddedOrders(action.getAddedOrders());
		} else {
			ocm.setAddedOrders(Collections.EMPTY_LIST);
			isNotAllChildCanceled = false;
		}

		if (CH.isntEmpty(action.getAddedExecutions())) {
			for (Execution e : action.getAddedExecutions())
				state.putExecution(e);
			ocm.setAddedExecutions(action.getAddedExecutions());
		} else
			ocm.setAddedExecutions(Collections.EMPTY_LIST);

		if (CH.isntEmpty(action.getChangedOrders())) {
			List<Tuple2<Order, Order>> updatedOrders = new ArrayList<Tuple2<Order, Order>>();
			for (Order deltas : action.getChangedOrders()) {
				Order existing = state.getOrder(deltas);
				if (existing == null) {
					if (action.getType() == OmsAction.ALL_CHILDREN_CANCELLED) {
						Order root = state.getOrder(sourceSystem, action.getRootOrderID());
						updatedOrders.add(new Tuple2<Order, Order>(root, root));
						ocm.setUpdatedOrders(updatedOrders);
						continue;
					} else {
						LH.warning(log, "Out of sequence update received for action : ", action.getType(), " for order ", deltas.getId(), " with root ", action.getRootOrderID());
						continue;
					}
				}
				final int status = deltas.askExists("orderStatus") ? deltas.getOrderStatus() : existing.getOrderStatus();
				boolean wasPending = MH.areAnyBitsSet(existing.getOrderStatus(), PENDING);
				boolean isPending = MH.areAnyBitsSet(status, PENDING);
				if (!wasPending && isPending) {// Entering a pending state
					FixRequest request;
					Order nuw = copy(existing);
					applyDelta(deltas, nuw);
					if (existing.getSliceType() == SliceType.CLIENT_ORDER) {
						request = CH.getOrThrow(pending, deltas.getId());
						if (request.getOrderInfo() != null)
							copyMutable(nuw, request.getOrderInfo());
						if (MH.anyBits(nuw.getOrderStatus(), OrdStatus.FILLED.getIntMask()) && nuw.getOrderQty() > existing.getOrderQty()) {
							LH.info(log, "Pending transition out of FILLED: ", existing.getOrderQty(), " ==> ", nuw.getOrderQty());
							nuw.setOrderStatus(MH.clearBits(nuw.getOrderStatus(), OrdStatus.FILLED.getIntMask()));
						}

					}
					state.pendingOrder(nuw);
					updatedOrders.add(new Tuple2<Order, Order>(existing, nuw));
				} else if (wasPending && !isPending) {// exiting a pending state
					if (existing.getSliceType() == SliceType.CLIENT_ORDER) {
						if (deltas.getRequestId() == null) {// pending state
															// rejected
							state.pendingRejected(deltas);// pop the old state
															// back off
							Order reverted = state.getOrder(deltas);
							applyDelta(deltas, reverted);
							updatedOrders.add(new Tuple2<Order, Order>(existing, reverted));
						} else {// pending state accepted
							state.pendingAccepted(deltas);
							Order nuw = state.getOrder(deltas);
							Order orig = copy(nuw);
							applyDelta(deltas, nuw);
							updatedOrders.add(new Tuple2<Order, Order>(orig, nuw));
						}
					} else {
						state.pendingAccepted(deltas);
						Order nuw = state.getOrder(deltas);
						Order orig = copy(nuw);
						applyDelta(deltas, nuw);
						updatedOrders.add(new Tuple2<Order, Order>(orig, nuw));
					}
				} else {// pending state did not change
					final FixRequest request = pending.get(deltas.getId());
					final Order orig = copy(existing);
					final Order origOrder = state.getOrigOrder(deltas);
					applyDelta(deltas, existing);
					if (origOrder != null)
						applyDelta(deltas, origOrder);
					if (request != null) {
						copyMutable(existing, request.getOrderInfo());
						if (origOrder != null)
							copyMutable(origOrder, request.getOrderInfo());
					}

					updatedOrders.add(new Tuple2<Order, Order>(orig, existing));
				}
			}
			ocm.setUpdatedOrders(updatedOrders);
		} else {
			isNotAllChildCanceled = false;
			ocm.setUpdatedOrders(Collections.EMPTY_LIST);
		}

		if (!isNotAllChildCanceled) {
			if (action.getType() == OmsAction.ALL_CHILDREN_CANCELLED) {
				Order root = state.getOrder(sourceSystem, action.getRootOrderID());
				List<Tuple2<Order, Order>> updatedOrders = new ArrayList<Tuple2<Order, Order>>();
				updatedOrders.add(new Tuple2<Order, Order>(root, root));
				ocm.setUpdatedOrders(updatedOrders);
			}
		}

		ocm.setDeletedOrders(Collections.EMPTY_LIST);
		ocm.setDeletedExecutions(Collections.EMPTY_LIST);
		out.send(ocm, threadScope);
	}

	private void applyDelta(Order deltas, Order target) {
		Map<Integer, String> ptt = target.getPassThruTags();
		VH.copyPartialFields(deltas, target);
		if (ptt != null) {
			if (deltas.getPassThruTags() != null)
				ptt.putAll(deltas.getPassThruTags());
			target.setPassThruTags(ptt);
		}

	}

	private static Order copy(Order existing) {
		Order r = existing.clone();
		if (existing.getPassThruTags() != null)
			r.setPassThruTags(new HashMap<Integer, String>(existing.getPassThruTags()));
		return r;
	}

	private static void copyMutable(Order fixorder, FixOrderInfo request) {
		if (request.askExists("orderQty"))
			fixorder.setOrderQty(request.getOrderQty());

		if (request.askExists("orderType"))
			fixorder.setOrderType(request.getOrderType());

		if (request.askExists("limitPx"))
			fixorder.setLimitPx(request.getLimitPx());

		if (request.askExists("currency"))
			fixorder.setCurrency(request.getCurrency());

		if (request.askExists("timeInForce"))
			fixorder.setTimeInForce(request.getTimeInForce());

		if (request.askExists("passThruTags")) {
			if (fixorder.getPassThruTags() == null)
				fixorder.setPassThruTags(new HashMap<Integer, String>());
			copyMap(fixorder.getPassThruTags(), request.getPassThruTags());
		}

		if (request.askExists("side"))
			fixorder.setSide(request.getSide());
	}

	private static void copyMap(Map<Integer, String> target, Map<Integer, String> source) {
		if (source == null)
			return;
		for (Integer i : CH.comm(target.keySet(), source.keySet(), true, false, false))
			target.remove(i);
		for (Integer i : CH.comm(target.keySet(), source.keySet(), false, true, true))
			target.put(i, source.get(i));
	}

	public void start() {
		super.start();
		this.sourceSystem = getTools().getOptional("SYSTEM_NAME");
	}

}