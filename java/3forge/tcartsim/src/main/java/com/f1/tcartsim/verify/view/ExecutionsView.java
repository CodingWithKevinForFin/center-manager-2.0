/**
 * 
 */
package com.f1.tcartsim.verify.view;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.f1.tcartsim.verify.chain.ExecutionChain;
import com.f1.tcartsim.verify.chain.ParentOrderChain;

/**
 * @author george
 * 
 */
public class ExecutionsView extends View {

	public ExecutionsView() {
		super();
		addColumn(Long.class, "time");
		addColumn(String.class, "side");
		addColumn(String.class, "ParentOrdId");
		addColumn(String.class, "execId");
		addColumn(String.class, "ChildOrdId");
		addColumn(String.class, "symbol");
		addColumn(String.class, "ex");
		addColumn(Double.class, "px");
		addColumn(Long.class, "size");
		addColumn(Double.class, "value");
		addColumn(Double.class, "vT+1");
		addColumn(Double.class, "vt+5");
		addColumn(Double.class, "T+1 Px");
		addColumn(Double.class, "T+5 Px");
		addColumn(Double.class, "bid");
		addColumn(Double.class, "ask");
		addColumn(String.class, "account");
		addColumn(String.class, "system");
	}

	public void populateWithParentOrderIDs(Set<String> parentOrderIDs, Map<String, ExecutionChain> executionsChainMap, Map<String, ParentOrderChain> parentOrdersChainMap) {
		HashSet<String> eids = new HashSet<String>();
		for (String eid : executionsChainMap.keySet()) {
			ExecutionChain e = executionsChainMap.get(eid);
			if (parentOrderIDs.contains(e.getParentOrderID())) {
				eids.add(eid);
			}
		}
		populate(eids, executionsChainMap, parentOrdersChainMap);
	}

	public void populate(Map<String, ExecutionChain> executionsChainMap, Map<String, ParentOrderChain> parentOrdersChainMap) {
		populate(executionsChainMap.keySet(), executionsChainMap, parentOrdersChainMap);
	}

	public void populate(Set<String> executionIDs, Map<String, ExecutionChain> executionsChainMap, Map<String, ParentOrderChain> parentOrdersChainMap) {
		int i = 0;
		int total = executionsChainMap.keySet().size();
		for (String eid : executionsChainMap.keySet()) {
			//			System.out.println("Progress: " + i++ + " out of: " + total);
			ExecutionChain e = executionsChainMap.get(eid);
			if (executionIDs.contains(e.getExecutionID())) {
				populateRow(e, parentOrdersChainMap);
			}
		}
		sort("time");
	}

	public void populateRow(ExecutionChain execution, Map<String, ParentOrderChain> parentOrdersChainMap) {
		ParentOrderChain p = parentOrdersChainMap.get(execution.getParentOrderID());
		long time = execution.getTime();
		String side = execution.getSide() == 'B' ? "Buy" : execution.getSide() == 'S' ? "Sell" : "null";
		String parentOrdId = execution.getParentOrderID();
		String childOrdId = execution.getChildOrderID();
		String symbol = execution.getSymbol();
		String ex = execution.getExchange();
		double px = execution.getPx();
		long size = execution.getFilledSize();
		double value = execution.getFilledValue();
		double bid = execution.getNationalBestBid();
		double ask = execution.getNationalBestOffer();
		String eid = execution.getExecutionID();
		String account;
		String system;
		double t1 = execution.getTradePricePlusOne();
		double t5 = execution.getTradePricePlusFive();
		double vt1 = 10000 * (t1 - px) / px;
		double vt5 = 10000 * (t5 - px) / px;
		if (execution.getSide() == 'B') {
			vt1 *= -1;
			vt5 *= -1;
		}
		if (p != null) {
			account = p.getAccount();
			system = p.getSystem();
		} else {
			account = "null";
			system = "null";
		}
		addRow(time, side, parentOrdId, eid, childOrdId, symbol, ex, px, size, value, vt1, vt5, t1, t5, bid, ask, account, system);
	}
}
