/**
 * 
 */
package com.f1.tcartsim.verify.view;

import java.util.HashSet;
import java.util.Map;

import com.f1.tcartsim.verify.chain.ChildOrderChain;
import com.f1.tcartsim.verify.chain.ExecutionChain;
import com.f1.tcartsim.verify.chain.ParentOrderChain;
import com.f1.utils.SH;

/**
 * @author george
 * 
 */
public class TopBar1View extends View {

	public TopBar1View() {
		super();
		addColumn(Long.class, "Parents");
		addColumn(Long.class, "Children");
		addColumn(Long.class, "Open Orders");
		addColumn(Long.class, "Executions");
	}

	public void populate2(Map<String, ParentOrderChain> parentOrderChainMap, Map<String, ChildOrderChain> coc, Map<String, ExecutionChain> ec) {
		HashSet<String> pids = new HashSet<String>();
		HashSet<String> cids = new HashSet<String>();

		pids.addAll(parentOrderChainMap.keySet());
		cids.addAll(coc.keySet());

		long children = 0;
		long openOrders = 0;
		for (String cid : coc.keySet()) {
			ChildOrderChain c = coc.get(cid);
			if (!SH.equals(c.getParentOrderID(), "")) {
				pids.add(c.getParentOrderID());
				children += c.getnNewRecords();
			}
			//			if (c.isOpen()) {
			//				openOrders++;
			//			}
		}
		for (String eid : ec.keySet()) {
			ExecutionChain e = ec.get(eid);
			if (!SH.equals(e.getParentOrderID(), "")) {
				pids.add(e.getParentOrderID());
			}
			if (!SH.equals(e.getChildOrderID(), "")) {
				cids.add(e.getChildOrderID());
			}
		}
		long parents = pids.size();
		//		long children = cids.size();
		long executions = ec.keySet().size();

		for (String pid : parentOrderChainMap.keySet()) {
			ParentOrderChain p = parentOrderChainMap.get(pid);
			if (p.isOpen()) {
				openOrders++;
			}
		}
		addRow(parents, children, openOrders, executions);
	}

	@Deprecated
	public void populate(Map<String, ParentOrderChain> parentOrderChainMap) {
		long parents = 0;
		long children = 0;
		long openOrders = 0;
		long executions = 0;
		for (String pid : parentOrderChainMap.keySet()) {
			ParentOrderChain p = parentOrderChainMap.get(pid);
			parents++;
			children += p.getnChildOrders();
			if (p.isOpen()) {
				openOrders++;
			}
			executions += p.getTnFills();
		}
		addRow(parents, children, openOrders, executions);
	}

}
