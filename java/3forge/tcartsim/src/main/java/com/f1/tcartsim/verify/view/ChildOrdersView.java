package com.f1.tcartsim.verify.view;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.tcartsim.verify.chain.ChildOrderChain;
import com.f1.tcartsim.verify.chain.ExecutionChain;
import com.f1.tcartsim.verify.util.VerifyFormulas;
import com.f1.utils.structs.BasicMultiMap;

/**
 * @author george
 * 
 */
public class ChildOrdersView extends View {

	public ChildOrdersView() {
		addColumn(String.class, "symbol");
		addColumn(Double.class, "base limitPx");
		addColumn(String.class, "Currency");
		addColumn(Long.class, "openQty");
		addColumn(Double.class, "openVal");
		addColumn(Long.class, "size");
		addColumn(Double.class, "% Filled");
		addColumn(Long.class, "filledQty");
		addColumn(Double.class, "filledValue");
		addColumn(String.class, "parentId");
		addColumn(String.class, "clOrderId");
	}

	public void populateWithParentOrderIDs(Set<String> parentOrderIDs, Map<String, ChildOrderChain> childOrderChainMap, Map<String, ExecutionChain> executionChainMap) {
		HashSet<String> cids = new HashSet<String>();
		for (String cid : childOrderChainMap.keySet()) {
			ChildOrderChain c = childOrderChainMap.get(cid);
			if (parentOrderIDs.contains(c.getParentOrderID())) {
				cids.add(cid);
			}
		}
		populate(cids, childOrderChainMap, executionChainMap);
	}

	public void populate(Map<String, ChildOrderChain> childOrderChainMap, Map<String, ExecutionChain> executionChainMap) {
		populate(childOrderChainMap.keySet(), childOrderChainMap, executionChainMap);
	}

	public void populate(Set<String> cids, Map<String, ChildOrderChain> cmap, Map<String, ExecutionChain> executionChainMap) {
		BasicMultiMap.List<String, ExecutionChain> mmE = new BasicMultiMap.List<String, ExecutionChain>();

		for (String eid : executionChainMap.keySet()) {
			String cid = executionChainMap.get(eid).getChildOrderID();
			if (cids.contains(cid)) {
				mmE.putMulti(cid, executionChainMap.get(eid));
			}
		}

		for (String childOrderID : cids) {
			populateRow(childOrderID, cmap.get(childOrderID), mmE.get(childOrderID));
		}
		sort("clOrderId");
	}

	public void populateRow(String childOrderID, ChildOrderChain childOrderChainMap, List<ExecutionChain> executions) {
		String symbol = childOrderChainMap.getSymbol();
		double limitPx = childOrderChainMap.getLimitPx();
		long openQty = childOrderChainMap.getOpenSize();
		double openVal = childOrderChainMap.getOpenValue();
		long size = childOrderChainMap.getSize();
		long filledQty = 0;//coc.getFilledSize();
		double filledValue = 0;//coc.getFilledValue();
		String pid = childOrderChainMap.getParentOrderID();
		String currency = childOrderChainMap.getCurrency();

		if (executions != null) {
			for (int i = 0; i < executions.size(); i++) {
				filledQty += executions.get(i).getFilledSize();
				filledValue += executions.get(i).getFilledValue();
			}
		}

		double pfilled = VerifyFormulas.percentFilled(filledQty, size);
		addRow(symbol, limitPx, currency, openQty, openVal, size, pfilled, filledQty, filledValue, pid, childOrderID);
	}

}
