package com.f1.tcartsim.verify.view;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.tcartsim.verify.chain.ChildOrderChain;
import com.f1.tcartsim.verify.chain.ExecutionChain;
import com.f1.tcartsim.verify.chain.ParentOrderChain;
import com.f1.tcartsim.verify.util.VerifyFormulas;
import com.f1.utils.structs.BasicMultiMap;

/**
 * @author george
 * 
 */
public class ParentOrdersView extends View {

	public ParentOrdersView() {
		super();
		addColumn(Long.class, "Create");
		addColumn(String.class, "Strategy");
		addColumn(String.class, "system");
		addColumn(String.class, "account");
		addColumn(String.class, "Id");
		addColumn(String.class, "Symbol");
		addColumn(String.class, "side");
		addColumn(Double.class, "Base Lmt Px");
		addColumn(String.class, "Currency");
		addColumn(Long.class, "Tgt Qty");
		addColumn(Double.class, "% Filled");
		addColumn(Double.class, "VWAP");
		addColumn(Long.class, "Fills");
		addColumn(Long.class, "Filled");
		addColumn(Long.class, "childCnt");
		addColumn(Long.class, "leaves");
		addColumn(Double.class, "Avg Fill Sz");
		addColumn(Double.class, "Filled Value");
		//bid ask last
	}

	public void populateWithSymbols(Set<String> symbols, Map<String, ParentOrderChain> parentOrderChainMap, Map<String, ChildOrderChain> childOrderChainMap,
			Map<String, ExecutionChain> executionChainMap) {
		HashSet<String> pids = new HashSet<String>();
		for (String pid : parentOrderChainMap.keySet()) {
			ParentOrderChain p = parentOrderChainMap.get(pid);
			if (symbols.contains(p.getSymbol())) {
				pids.add(pid);
			}
		}
		populate(pids, parentOrderChainMap, childOrderChainMap, executionChainMap);
	}

	public void populate(Map<String, ParentOrderChain> parentOrderChainMap, Map<String, ChildOrderChain> childOrderChainMap, Map<String, ExecutionChain> executionChainMap) {
		populate(parentOrderChainMap.keySet(), parentOrderChainMap, childOrderChainMap, executionChainMap);
	}
	public void populate(Set<String> parentOrderIDs, Map<String, ParentOrderChain> parentOrderChainMap, Map<String, ChildOrderChain> childOrderChainMap,
			Map<String, ExecutionChain> executionChainMap) {

		BasicMultiMap.List<String, ChildOrderChain> mmC = new BasicMultiMap.List<String, ChildOrderChain>();
		BasicMultiMap.List<String, ExecutionChain> mmE = new BasicMultiMap.List<String, ExecutionChain>();

		for (String cid : childOrderChainMap.keySet()) {
			String pid = childOrderChainMap.get(cid).getParentOrderID();
			if (parentOrderIDs.contains(pid)) {
				mmC.putMulti(pid, childOrderChainMap.get(cid));
			}
		}

		for (String eid : executionChainMap.keySet()) {
			String pid = executionChainMap.get(eid).getParentOrderID();
			if (parentOrderIDs.contains(pid)) {
				mmE.putMulti(pid, executionChainMap.get(eid));
			}
		}
		for (String pid : parentOrderIDs) {
			populateRow(pid, parentOrderChainMap.get(pid), mmC.get(pid), mmE.get(pid));
		}
		sort("Create");
	}

	public void populateRow(String parentOrderID, ParentOrderChain parentOrderChain, List<ChildOrderChain> childOrders, List<ExecutionChain> executions) {
		long create = parentOrderChain.getTime();
		String strategy = parentOrderChain.getStrategy();
		String system = parentOrderChain.getSystem();
		String account = parentOrderChain.getAccount();
		String symbol = parentOrderChain.getSymbol();
		String side = parentOrderChain.getSide() == 'B' ? "Buy" : parentOrderChain.getSide() == 'S' ? "Sell" : "null";
		long tgtQty = parentOrderChain.getSize();
		long filled = 0;//parentOrderChain.getTnFilledSize();
		long fills = 0;//parentOrderChain.getTnFills();
		long childCnt = 0;//parentOrderChain.getnChildOrders();
		long leaves = 0;//parentOrderChain.getTnLeavesSize();
		double filledValue = 0;// parentOrderChain.getTnFilledValue();
		double avgFillSize = 0;
		double pFilled = 0;
		double vwap = 0;
		double baselmtpx = parentOrderChain.getLimitPx();
		String currency = parentOrderChain.getCurrency();

		if (childOrders != null) {
			for (int i = 0; i < childOrders.size(); i++) {
				childCnt++;
				leaves += childOrders.get(i).getOpenSize();
			}
		}

		if (executions != null) {
			for (int i = 0; i < executions.size(); i++) {
				fills++;
				filled += executions.get(i).getFilledSize();
				filledValue += executions.get(i).getFilledValue();
			}
		}

		avgFillSize = VerifyFormulas.avgFillSize(filled, fills);
		pFilled = VerifyFormulas.percentFilled(filled, tgtQty);
		vwap = VerifyFormulas.vwap(filledValue, filled);
		addRow(create, strategy, system, account, parentOrderID, symbol, side, baselmtpx, currency, tgtQty, pFilled, vwap, fills, filled, childCnt, leaves, avgFillSize,
				filledValue);
	}
}
