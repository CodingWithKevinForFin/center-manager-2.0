package com.f1.fix.oms.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.base.Message;
import com.f1.base.ValuedListenable;
import com.f1.container.impl.BasicState;
import com.f1.fix.oms.schema.ClientOrder;
import com.f1.fix.oms.schema.OmsOrder;
import com.f1.fix.oms.schema.Slice;
import com.f1.persist.Persistable;
import com.f1.persist.impl.RefCountingPersistValuedListener;
import com.f1.persist.structs.PersistableHashMap;
import com.f1.persist.structs.PersistableMap;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.Order;
import com.f1.utils.CH;
import com.f1.utils.OH;

/**
 * 
 * maintains the state of the order
 * 
 */
public class OmsOrderState extends BasicState {
	ClientOrder order;
	PersistableMap<String, OmsOrder> idToOrderMap = new PersistableHashMap<String, OmsOrder>();
	HashMap<String, Slice> remoteIdToSliceMap = new HashMap<String, Slice>();
	private TransactionListener listener = new TransactionListener();
	private Set<OmsOrder> slices = new HashSet<OmsOrder>();
	private boolean notifyCancelAllChildOrders;

	public OmsOrderState() {
		setPersistedRoot(idToOrderMap);
	}

	public boolean isCancellingChildOrders() {
		return notifyCancelAllChildOrders;
	}

	public void setCancellingChildOrders(boolean val) {
		this.notifyCancelAllChildOrders = val;
	}

	@Override
	public void initPersisted(boolean isRecovering) {
		if (isRecovering) {
			idToOrderMap = (PersistableMap<String, OmsOrder>) getPersistedRoot();
			for (OmsOrder o : new ArrayList<OmsOrder>(idToOrderMap.values()))
				if (o instanceof ClientOrder && o.getFixOrder() != null && getPartitionId().equals(o.getFixOrder().getId())) {
					setClientOrder((ClientOrder) o);
				} else if (o instanceof Slice && o.getFixOrder() != null) {
					remoteIdToSliceMap.put(o.getFixOrder().getRequestId(), (Slice) o);
					slices.add(o);
				}
		} else
			setPersistedRoot(idToOrderMap);
		listener.onValuedAdded(idToOrderMap);
		listener.clearChanges();
	}

	public void setClientOrder(ClientOrder order) {
		this.order = order;
		order.getFixOrder().setRequestId(order.getFixOrder().getRequestId());
		addOrder(order);
	}

	public ClientOrder getClientOrder() {
		return order;
	}

	public Set<OmsOrder> getSlices() {
		return slices;
	}

	public void addOrder(OmsOrder order) {
		if (order instanceof Slice) {
			idToOrderMap.put(order.getFixOrder().getId(), order);
			remoteIdToSliceMap.put(order.getFixOrder().getRequestId(), (Slice) order);
			slices.add(order);
		} else {
			idToOrderMap.put(order.getFixOrder().getRequestId(), order);
			idToOrderMap.put(order.getFixOrder().getId(), order);
		}
	}

	public OmsOrder getOrder(String id) {
		return idToOrderMap.get(id);
	}

	public TransactionListener getTransactionListener() {
		return listener;
	}

	public Set<String> getRequestIds() {
		return idToOrderMap.keySet();
	}

	public static class TransactionListener extends RefCountingPersistValuedListener {

		final private Map<ValuedListenable, Map<Object, Object>> changedFields = new IdentityHashMap<ValuedListenable, Map<Object, Object>>();
		final private Map<ValuedListenable, ValuedListenable> added = new IdentityHashMap<ValuedListenable, ValuedListenable>();
		final private List<ValuedListenable> addedList = new ArrayList<ValuedListenable>();
		final private Map<ValuedListenable, ValuedListenable> removed = new IdentityHashMap<ValuedListenable, ValuedListenable>();
		final private Map<Map<?, ?>, ValuedListenable> passthruFieldsParent = new IdentityHashMap<Map<?, ?>, ValuedListenable>();

		@Override
		protected void onRefCount(ValuedListenable target, int count) {
			switch (count) {
				case 1:
					if (target instanceof Order) {
						Map<Integer, String> ptt = ((Order) target).getPassThruTags();
						if (ptt != null)
							passthruFieldsParent.put(ptt, target);
					} else if (target instanceof Execution) {
						Map<Integer, String> ptt = ((Execution) target).getPassThruTags();
						if (ptt != null)
							passthruFieldsParent.put(ptt, target);
					}
					if (target instanceof Message) {// skip datastructures
						added.put(target, target);
						addedList.add(target);
					}

					break;
				case 0:
					if (target instanceof Order) {
						Map<Integer, String> ptt = ((Order) target).getPassThruTags();
						if (ptt != null)
							passthruFieldsParent.remove(ptt);
					} else if (target instanceof Execution) {
						Map<Integer, String> ptt = ((Execution) target).getPassThruTags();
						if (ptt != null)
							passthruFieldsParent.remove(ptt);
					}
					removed.put(target, target);
					break;
			}
		}

		@Override
		public void onValued(ValuedListenable target, String name, byte pid, Object old, Object value) {
			super.onValued(target, name, pid, old, value);
			onChange(target, name, old, value);
		}

		@Override
		public void onKeyedParamChanged(Persistable target, Object key, Object oldValue, Object newValue) {
			super.onKeyedParamChanged(target, key, oldValue, newValue);
			onChange(target, key, oldValue, newValue);
		}

		@Override
		public void onKeyedParamRemoved(Persistable target, Object key, Object oldValue) {
			super.onKeyedParamRemoved(target, key, oldValue);
			onChange(target, key, oldValue, null);
		}

		@Override
		public void onKeyedParamAdded(Persistable target, Object key, Object newValue) {
			super.onKeyedParamAdded(target, key, newValue);
			onChange(target, key, null, newValue);
		}

		@Override
		public void onKeyedParamsCleared(Persistable target, Iterable<Entry<Object, Object>> oldEntries) {
			super.onKeyedParamsCleared(target, oldEntries);
			for (Map.Entry<Object, Object> e : oldEntries)
				onChange(target, e.getKey(), e.getValue(), null);
		}

		private void onChange(ValuedListenable target, Object key, Object oldValue, Object newValue) {
			if (key instanceof Integer) {// passthru
				if (newValue == null)
					throw new RuntimeException("can not remove passthrutags");
				Order order = (Order) passthruFieldsParent.get(target);
				target = order;
				key = "passThruTags";
			}
			if (target instanceof Map) {
				target = passthruFieldsParent.get(target);
				if (target == null)
					return;
			}
			if (added.containsKey(target) || OH.eq(oldValue, newValue))
				return;
			Map<Object, Object> changes = changedFields.get(target);
			if (changes == null)
				changedFields.put(target, CH.m(key, oldValue));
			else if (!changes.containsKey(key))
				changes.put(key, oldValue);
		}

		public Map<ValuedListenable, Map<Object, Object>> getChangedFields() {
			return changedFields;
		}

		public Set<ValuedListenable> getChanged() {
			return changedFields.keySet();
		}

		public Map<Object, Object> getChangedFields(ValuedListenable vl) {
			return OH.noNull(changedFields.get(vl), Collections.EMPTY_MAP);
		}

		public List<ValuedListenable> getAdded() {
			return addedList;
		}

		public Set<ValuedListenable> getRemoved() {
			return removed.keySet();
		}

		public void clearChanges() {
			changedFields.clear();
			added.clear();
			addedList.clear();
			removed.clear();
		}
	}
}
