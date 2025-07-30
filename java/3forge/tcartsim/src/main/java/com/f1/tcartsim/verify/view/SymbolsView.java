/**
 * 
 */
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
public class SymbolsView extends View {

	public SymbolsView() {
		super();
		addColumn(String.class, "symbol");
		addColumn(Long.class, "ParentOrder");
		addColumn(Long.class, "Executions");
		//		addColumn(Double.class, "Last");
		addColumn(Double.class, "% Filled");
		addColumn(Long.class, "Total leaves");
		addColumn(Long.class, "Filled");
		addColumn(Double.class, "Avg Fill Sz");
		addColumn(Long.class, "Target");
		addColumn(Long.class, "Filled Notional");
		addColumn(Long.class, "VWAP");
	}

	public void populate(Map<String, ParentOrderChain> parentOrderChainMap, Map<String, ChildOrderChain> childOrderChainMap, Map<String, ExecutionChain> executionChainMap) {
		HashSet<String> symbols = new HashSet<String>();
		for (String pid : parentOrderChainMap.keySet()) {
			symbols.add(parentOrderChainMap.get(pid).getSymbol());
		}
		populate(symbols, parentOrderChainMap, childOrderChainMap, executionChainMap);
	}

	public void populate(Set<String> symbols, Map<String, ParentOrderChain> parentOrderChainMap, Map<String, ChildOrderChain> childOrderChainMap,
			Map<String, ExecutionChain> executionChainMap) {
		BasicMultiMap.List<String, ParentOrderChain> mmP = new BasicMultiMap.List<String, ParentOrderChain>();
		BasicMultiMap.List<String, ChildOrderChain> mmC = new BasicMultiMap.List<String, ChildOrderChain>();
		BasicMultiMap.List<String, ExecutionChain> mmE = new BasicMultiMap.List<String, ExecutionChain>();
		for (String pid : parentOrderChainMap.keySet()) {
			String symbol = parentOrderChainMap.get(pid).getSymbol();
			if (symbols.contains(symbol)) {
				mmP.putMulti(symbol, parentOrderChainMap.get(pid));
			}
		}

		for (String cid : childOrderChainMap.keySet()) {
			String symbol = childOrderChainMap.get(cid).getSymbol();
			if (symbols.contains(symbol)) {
				mmC.putMulti(symbol, childOrderChainMap.get(cid));
			}
		}

		for (String eid : executionChainMap.keySet()) {
			String symbol = executionChainMap.get(eid).getSymbol();
			if (symbols.contains(symbol)) {
				mmE.putMulti(symbol, executionChainMap.get(eid));
			}
		}

		for (String symbol : symbols) {
			populateRow(symbol, mmP.get(symbol), mmC.get(symbol), mmE.get(symbol));
		}
		sort("symbol");
	}

	private void populateRow(String symbol, List<ParentOrderChain> parentOrders, List<ChildOrderChain> childOrders, List<ExecutionChain> executions) {
		long nParentOrders = 0;
		long nFills = 0;
		double pFilled = 0;
		long totalLeaves = 0;
		long totalFilled = 0;
		double avgFillSize = 0;
		long totalTarget = 0;
		double totalFilledValue = 0;
		double vwap = 0;

		if (childOrders != null) {
			for (int i = 0; i < childOrders.size(); i++) {
				totalLeaves += childOrders.get(i).getOpenSize();
			}
		}
		if (executions != null) {
			for (int i = 0; i < executions.size(); i++) {
				nFills++;
				totalFilled += executions.get(i).getFilledSize();
				totalFilledValue += executions.get(i).getFilledValue();
			}
		}
		if (parentOrders != null) {
			for (int i = 0; i < parentOrders.size(); i++) {
				nParentOrders++;
				totalTarget += parentOrders.get(i).getSize();
			}
		}
		vwap = VerifyFormulas.vwap(totalFilledValue, totalFilled);
		pFilled = VerifyFormulas.percentFilled(totalFilled, totalTarget);
		avgFillSize = VerifyFormulas.avgFillSize(totalFilled, nFills);
		addRow(symbol, nParentOrders, nFills, pFilled, totalLeaves, totalFilled, avgFillSize, totalTarget, totalFilledValue, vwap);
	}
}
