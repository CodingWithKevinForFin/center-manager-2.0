package com.f1.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.f1.container.impl.BasicState;
import com.f1.generator.Order.SIDE;
import com.f1.utils.LH;

public class OrderCache extends BasicState {
	private static final Logger log = Logger.getLogger(OrderCache.class.getName());

	final static Map<Integer, AtomicInteger> CLIENT_ORDER_SEQUENCE = new ConcurrentHashMap<>();
	final static Map<Integer, AtomicInteger> EXCHANGE_ORDER_SEQUENCE = new ConcurrentHashMap<>();

	public static enum SEQUENCE_TYPE {
										CLIENT,
										EXCHANGE
	}

	public static int getOrderSequence(SEQUENCE_TYPE sequenceType, int which) {
		Map<Integer, AtomicInteger> sequence = EXCHANGE_ORDER_SEQUENCE;
		if (SEQUENCE_TYPE.CLIENT == sequenceType) {
			sequence = CLIENT_ORDER_SEQUENCE;
		}

		AtomicInteger counter = sequence.get(which);
		if (null == counter) {
			counter = new AtomicInteger(0);
			sequence.put(which, counter);
		}
		return counter.getAndIncrement();
	}

	public static enum ORDER_STATE {
									NEW,
									OPEN,
									CLOSE
	}

	private final Map<String, Map<ORDER_STATE, Set<Order>>> orders = new HashMap<>();
	//           ^          ^              ^
	//           |          |              |
	//        Symbol     Order state    unsorted order
	//
	//   ex.
	//        MSFT        NEW           O3,O6,O8...
	//                    OPEN          O1,O7...       (Ack, partial fill excluding Cancel/pending Cancel)
	//                    CLOSE         O2,O4,O13,O5...

	private final Map<String, Order> orderByClOrdID = new HashMap<>();

	private final Map<String, Map<ORDER_STATE, Map<SIDE, List<Order>>>> orderedLists = new HashMap<>();

	public List<Order> getOrderedList(final String symbol, ORDER_STATE orderState, SIDE side) {

		Map<ORDER_STATE, Map<SIDE, List<Order>>> listInSymbol = orderedLists.get(symbol);
		if (null == listInSymbol) {
			listInSymbol = new EnumMap<ORDER_STATE, Map<SIDE, List<Order>>>(ORDER_STATE.class);
			orderedLists.put(symbol, listInSymbol);
		}

		Map<SIDE, List<Order>> listInOrderState = listInSymbol.get(orderState);
		if (null == listInOrderState) {
			listInOrderState = new EnumMap<SIDE, List<Order>>(SIDE.class);
			listInSymbol.put(orderState, listInOrderState);
		}

		List<Order> listOfOrder = listInOrderState.get(side);
		if (null == listOfOrder) {
			listOfOrder = new ArrayList<>();
			for (Order order : getOrders(symbol, orderState)) {
				if (order.getSideInEnum() == side) {
					listOfOrder.add(order);
					Collections.sort(listOfOrder);
					listInOrderState.put(side, listOfOrder);
				}
			}
		}
		return listOfOrder;
	}

	public void removeOrderedList(final String symbol, Order order) {
		Map<ORDER_STATE, Map<SIDE, List<Order>>> orderInSymbol = orderedLists.get(symbol);
		if (null != orderInSymbol) {
			Map<SIDE, List<Order>> orderInOrderState = orderInSymbol.get(order.getOrderState());
			if (null != orderInOrderState) {
				orderInOrderState.remove(order.getSideInEnum());
			}
		}
	}

	public Set<Order> getOrders(final String symbol, ORDER_STATE orderState) {
		Set<Order> orderSet = Collections.emptySet();
		final Map<ORDER_STATE, Set<Order>> orderInSymbol = orders.get(symbol);
		if (null != orderInSymbol) {
			orderSet = orderInSymbol.get(orderState);
			if (null == orderSet) {
				return Collections.emptySet();
			}
		}
		return orderSet;
	}

	public Order getOrder(final String clOrdID) {
		return orderByClOrdID.get(clOrdID);
	}

	// This function may return stale information.
	// It is only being used by ExecutionReportGenerator which 
	// will check for an available order to be filled.
	public List<String> getSymbols(ORDER_STATE orderState) {
		List<String> symbols = new ArrayList<>();

		Set<Order> orderInOrderState = null;
		for (Map.Entry<String, Map<ORDER_STATE, Set<Order>>> entry : orders.entrySet()) {
			orderInOrderState = entry.getValue().get(orderState);
			if (null != orderInOrderState && !orderInOrderState.isEmpty()) {
				symbols.add(entry.getKey());
			}
		}
		return symbols;
	}

	public void addOrder(final String symbol, ORDER_STATE currentOrderState, ORDER_STATE newOrderState, final Order order) {
		if (orders.size() > 500) {
			orders.clear();
			orderByClOrdID.clear();
			orderedLists.clear();
		}

		if (null == newOrderState) {
			LH.warning(log, "new order state cannot be null - skip adding order: ", order);
			return;
		}

		Map<ORDER_STATE, Set<Order>> orderInSymbol = orders.get(symbol);
		if (null == orderInSymbol) {
			orderInSymbol = new EnumMap<ORDER_STATE, Set<Order>>(ORDER_STATE.class);
			orders.put(symbol, orderInSymbol);
		}

		Set<Order> orderSet = orderInSymbol.get(currentOrderState);

		switch (newOrderState) {
			case NEW:
				if (null == orderSet) {
					orderSet = new HashSet<>();
					orderInSymbol.put(ORDER_STATE.NEW, orderSet);
				}
				orderSet.add(order);
				break;
			default:
				if (null == orderSet) {
					LH.warning(log, "current order state is empty.");
				} else {
					orderSet.remove(order);
				}
				orderSet = orderInSymbol.get(newOrderState);
				if (null == orderSet) {
					orderSet = new HashSet<>();
					orderInSymbol.put(newOrderState, orderSet);
				}
				orderSet.add(order);

				break;
		}
		order.setOrderState(newOrderState);
		removeOrderedList(symbol, order);
		orderByClOrdID.put(order.getClOrdID(), order);
	}

}
