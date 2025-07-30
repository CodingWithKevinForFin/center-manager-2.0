package com.f1.tcartsim.verify.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Console;
import com.f1.tcartsim.verify.chain.ChildOrderChain;
import com.f1.tcartsim.verify.chain.ExecutionChain;
import com.f1.tcartsim.verify.chain.NBBOChain;
import com.f1.tcartsim.verify.chain.ParentOrderChain;
import com.f1.tcartsim.verify.chain.TradeChain;
import com.f1.tcartsim.verify.record.ChildOrderRecord;
import com.f1.tcartsim.verify.record.ExecutionRecord;
import com.f1.tcartsim.verify.record.NBBORecord;
import com.f1.tcartsim.verify.record.ParentOrderRecord;
import com.f1.tcartsim.verify.record.Record;
import com.f1.tcartsim.verify.record.TradeRecord;
import com.f1.tcartsim.verify.util.VerifyMath;
import com.f1.tcartsim.verify.view.ChildOrdersView;
import com.f1.tcartsim.verify.view.ExecutionsView;
import com.f1.tcartsim.verify.view.ParentOrdersView;
import com.f1.tcartsim.verify.view.SymbolsView;
import com.f1.tcartsim.verify.view.TopBar1View;
import com.f1.tcartsim.verify.view.TopBar3View;
import com.f1.utils.IOH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.table.BasicTable;

@Console
public class AnvilData {
	private static long TIME_ONEMINUTE = 60000;
	private static long TIME_FIVEMINUTES = 300000;
	private HashMap<String, ParentOrderChain> parentOrdersMap;
	private HashMap<String, ChildOrderChain> childOrdersMap;
	private HashMap<String, ExecutionChain> executionsMap;
	private HashMap<String, NBBOChain> nbbosMap;
	private HashMap<String, TradeChain> tradesMap;

	private File tradeInFile;
	private File orderInFile;
	private File nbboInFile;

	private File symbolOut;
	private File parentOut;
	private File childOut;
	private File topBarOut;
	private File topBarOut3;
	private File executionOut;

	public AnvilData(PropertyController p) {
		tradeInFile = p.getRequired("trade.file.out", File.class);
		orderInFile = p.getRequired("order.file.out", File.class);
		nbboInFile = p.getRequired("nbbo.file.out", File.class);

		symbolOut = p.getOptional("verify.symbol.file.out", File.class);
		parentOut = p.getOptional("verify.parent.file.out", File.class);
		childOut = p.getOptional("verify.child.file.out", File.class);
		topBarOut = p.getOptional("verify.topBar.file.out", File.class);
		topBarOut3 = p.getOptional("verify.topBar3.file.out", File.class);
		executionOut = p.getOptional("verify.execution.file.out", File.class);

		long timeBegin = SH.parseLong(p.getRequired("verify.timeBegin"));
		long timeEnd = SH.parseLong(p.getRequired("verify.timeEnd"));

		init(timeBegin, timeEnd);
	}

	@Console(params = { "timeBegin", "timeEnd" })
	public void init(long timeBegin, long timeEnd) {
		ArrayList<String[]> orders = parseFile(orderInFile);
		ArrayList<String[]> trades = parseFile(tradeInFile);
		ArrayList<String[]> nbbos = parseFile(nbboInFile);

		String b[] = SH.split('|', SH.toString(timeBegin));
		String e[] = SH.split('|', SH.toString(timeEnd));
		ArrayList<String[]> orders1 = VerifyMath.limitDataBetween(orders, b, e, 'L');
		ArrayList<String[]> trades1 = VerifyMath.limitDataBetween(trades, b, e, 'L');
		ArrayList<String[]> nbbos1 = VerifyMath.limitDataBetween(nbbos, b, e, 'L');

		generateOrderMaps(orders1);
		generateNbboMap(nbbos1);
		generateTradeMap(trades1);
	}

	private void generateTradeMap(ArrayList<String[]> trades) {
		tradesMap = new HashMap<String, TradeChain>();
		for (int i = 0; i < trades.size(); i++) {
			TradeRecord r = new TradeRecord(trades.get(i));
			String symbol = r.getSymbol();
			if (tradesMap.containsKey(symbol)) {
				tradesMap.get(symbol).addTrade(r);
			} else {
				TradeChain c = new TradeChain(symbol);
				c.addTrade(r);
				tradesMap.put(symbol, c);
			}
		}
		addTrades(executionsMap, tradesMap);
	}
	private void generateNbboMap(ArrayList<String[]> nbbos) {
		nbbosMap = new HashMap<String, NBBOChain>();
		for (int i = 0; i < nbbos.size(); i++) {
			NBBORecord r = new NBBORecord(nbbos.get(i));
			String symbol = r.getSymbol();
			if (nbbosMap.containsKey(symbol)) {
				nbbosMap.get(symbol).addNBBO(r);
			} else {
				NBBOChain c = new NBBOChain(symbol);
				c.addNBBO(r);
				nbbosMap.put(symbol, c);
			}
		}
		addNbbos(executionsMap, nbbosMap);
	}

	private void generateOrderMaps(ArrayList<String[]> orders) {
		parentOrdersMap = new HashMap<String, ParentOrderChain>();
		childOrdersMap = new HashMap<String, ChildOrderChain>();
		executionsMap = new HashMap<String, ExecutionChain>();

		BasicMultiMap.List<String, ParentOrderRecord> pmap = new BasicMultiMap.List<String, ParentOrderRecord>();
		BasicMultiMap.List<String, ChildOrderRecord> cmap = new BasicMultiMap.List<String, ChildOrderRecord>();
		BasicMultiMap.List<String, ExecutionRecord> emap = new BasicMultiMap.List<String, ExecutionRecord>();

		for (int i = 0; i < orders.size(); i++) {
			String record[] = orders.get(i);
			switch (Record.getFormat(record)) {
				case 'O':
					ParentOrderRecord parentOrder = new ParentOrderRecord(record);
					pmap.putMulti(parentOrder.getParentOrderID(), parentOrder);
					break;
				case 'C':
					ChildOrderRecord childOrder = new ChildOrderRecord(record);
					cmap.putMulti(childOrder.getChildOrderID(), childOrder);
					break;
				case 'E':
					ExecutionRecord execution = new ExecutionRecord(record);
					emap.putMulti(execution.getExecutionID(), execution);
					break;
			}
		}

		for (String pid : pmap.keySet()) {
			ParentOrderChain pChain = new ParentOrderChain(pmap.get(pid).get(0));
			for (int i = 1; i < pmap.get(pid).size(); i++) {
				pChain.addParentOrder(pmap.get(pid).get(i));
			}
			pChain.update();
			parentOrdersMap.put(pid, pChain);
		}

		for (String cid : cmap.keySet()) {
			ChildOrderChain cChain = new ChildOrderChain(cmap.get(cid).get(0));
			for (int i = 1; i < cmap.get(cid).size(); i++) {
				cChain.addChildOrder(cmap.get(cid).get(i));
			}
			childOrdersMap.put(cid, cChain);
			cChain.update();
			if (SH.equals(cChain.getParentOrderID(), "")) {
				//System.out.println("C" + cChain.getChildOrderID());
			} else {
				parentOrdersMap.get(cChain.getParentOrderID()).addChildOrder(cid);
			}

		}

		for (String eid : emap.keySet()) {
			ExecutionChain eChain = new ExecutionChain(emap.get(eid).get(0));
			for (int i = 1; i < emap.get(eid).size(); i++) {
				eChain.addExecution(emap.get(eid).get(i));
			}
			eChain.update();
			executionsMap.put(eid, eChain);
			if (SH.equals(eChain.getChildOrderID(), "")) {
				if (SH.equals(eChain.getParentOrderID(), "") || eChain.getParentOrderID() == null) {
					//	System.out.println("E" + eChain.getExecutionID());
				} else {
					parentOrdersMap.get(eChain.getParentOrderID()).addExecution(eid);
				}

			} else {
				String cid = eChain.getChildOrderID();
				if (childOrdersMap.containsKey(cid)) {
					childOrdersMap.get(cid).addExecution(eid);
				}
			}
		}

		for (String eid : executionsMap.keySet()) {
			executionsMap.get(eid).updateWithChildOrders(childOrdersMap);
		}

		for (String cid : childOrdersMap.keySet()) {
			childOrdersMap.get(cid).updateWithExecutions(executionsMap);
			childOrdersMap.get(cid).updateWithParents(parentOrdersMap);
		}

		for (String pid : parentOrdersMap.keySet()) {
			parentOrdersMap.get(pid).updateWithExecutions(executionsMap);
			parentOrdersMap.get(pid).updateWithChildOrders(childOrdersMap);
		}
	}

	private ArrayList<String[]> parseFile(File f) {
		ArrayList<String[]> records = new ArrayList<String[]>();
		String text;
		try {
			text = IOH.readText(f);
			String data[] = SH.splitLines(text);
			for (int i = 0; i < data.length; i++) {
				String parts[] = SH.split('|', data[i]);
				records.add(parts);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return records;
	}

	@Console(help = "Runs a view; it displays only rows with a value of filter at the column: columnid. If columnid is empty string, no filter is used.", params = { "columnid",
			"filter" })
	public BasicTable runSymbolsView(String columnid, String filter) {
		SymbolsView view = new SymbolsView();
		view.populate(parentOrdersMap, childOrdersMap, executionsMap);
		if (!SH.equals(columnid, "")) {
			view.filter(columnid, filter);
		}
		view.print();

		if (symbolOut != null) {
			view.writeToFile(symbolOut);
		}
		return view.getTable();
	}

	@Console(help = "Runs a view; it displays only rows with a value of filter at the column: columnid. If columnid is empty string, no filter is used.", params = { "columnid",
			"filter" })
	public BasicTable runParentOrdersView(String columnid, String filter) {
		ParentOrdersView view = new ParentOrdersView();
		view.populate(parentOrdersMap, childOrdersMap, executionsMap);
		//		view.populateWithSymbols(new HashSet<String>(Arrays.asList("ORCL")), parentOrders, childOrders, executions);
		if (!SH.equals(columnid, "")) {
			view.filter(columnid, filter);
		}
		view.print();

		if (parentOut != null) {
			view.writeToFile(parentOut);
		}
		return view.getTable();
	}

	@Console(help = "Runs a view; it displays only rows with a value of filter at the column: columnid. If columnid is empty string, no filter is used.", params = { "columnid",
			"filter" })
	public BasicTable runChildOrdersView(String columnid, String filter) {
		ChildOrdersView view = new ChildOrdersView();
		//		view.populateWithParentOrderIDs(new HashSet<String>(Arrays.asList("IAMO-3902-62QQB", "IEXO-2214-UUPGZ", "IALO-366-M1DR0", "IEXO-3144-2L1VA")), childOrders, executions);
		view.populate(childOrdersMap, executionsMap);
		if (!SH.equals(columnid, "")) {
			view.filter(columnid, filter);
		}
		view.print();

		if (childOut != null) {
			view.writeToFile(childOut);
		}
		return view.getTable();
	}

	@Console
	public BasicTable runTopBarView1() {
		TopBar1View view4 = new TopBar1View();
		//		view4.populate(parentOrders);
		view4.populate2(parentOrdersMap, childOrdersMap, executionsMap);
		view4.print();

		if (topBarOut != null) {
			view4.writeToFile(topBarOut);
		}
		return view4.getTable();
	}
	@Console
	public BasicTable runTopBarView3() {
		TopBar3View view5 = new TopBar3View();
		view5.populate2(parentOrdersMap, executionsMap);
		//		view5.populate(parentOrders);
		view5.print();
		if (topBarOut3 != null) {
			view5.appendToFile(topBarOut3);
		}
		return view5.getTable();
	}

	@Console(help = "Runs a view; it displays only rows with a value of filter at the column: columnid. If columnid is empty string, no filter is used.", params = { "columnid",
			"filter" })
	public BasicTable runExecutionsView(String columnid, String filter) {
		ExecutionsView view = new ExecutionsView();
		view.populate(executionsMap, parentOrdersMap);

		if (!SH.equals(columnid, "")) {
			view.filter(columnid, filter);
		}
		//if (print == true)
		//view.print();

		//view.populateWithParentOrderIDs(new HashSet<String>(Arrays.asList("IAMO-3902-62QQB", "IEXO-2214-UUPGZ", "IALO-366-M1DR0", "IEXO-3144-2L1VA")), executions, parentOrders);
		if (executionOut != null) {
			view.writeToFile(executionOut);
		}
		return view.getTable();
	}

	public void run() {
		runSymbolsView("", "");
		runParentOrdersView("", "");
		runChildOrdersView("", "");
		runTopBarView1();
		runTopBarView3();
		runExecutionsView("", "");
	}

	private void addNbbos(HashMap<String, ExecutionChain> executionChainMap, Map<String, NBBOChain> nbboChainMap) {
		BasicMultiMap.List<String, ExecutionChain> em = new BasicMultiMap.List<String, ExecutionChain>();

		for (String eid : executionChainMap.keySet()) {
			ExecutionChain e = executionChainMap.get(eid);
			em.putMulti(e.getSymbol(), e);
		}

		for (String symbol : em.keySet()) {
			addNbbosH(em.get(symbol), nbboChainMap.get(symbol));
		}
	}

	private void addNbbosH(List<ExecutionChain> executions, NBBOChain nbboChain) {
		if (nbboChain != null) {
			Collections.sort(executions, ExecutionChain.compareTime);

			double lastBid = 0;
			double lastOffer = 0;

			int executionsPosition = 0;
			int offerPosition = 0;
			int bidPosition = 0;

			long executionTime = 0;
			long offerTime = 0;
			long bidTime = 0;

			while (executionsPosition < executions.size()) {
				ExecutionChain ec = executions.get(executionsPosition);
				executionTime = executions.get(executionsPosition).getTime();

				if (executionTime <= offerTime && executionTime <= bidTime) {
					ec.setNationalBestBid(lastBid);
					ec.setNationalBestOffer(lastOffer);
					//					if (SH.equals(ec.getSymbol(), "ICF"))
					//						System.out.println(ec.getExecutionID() + " - " + ec.getTime() + "- " + ec.getNationalBestBid() + "-" + ec.getNationalBestOffer());
					executionsPosition++;
				} else {
					if (executionTime > offerTime && offerPosition < (nbboChain.getOfferLength() - 1)) {
						lastOffer = nbboChain.getOffer(offerPosition);
						offerPosition++;
						offerTime = nbboChain.getOfferTime(offerPosition);
					} else if (offerPosition == (nbboChain.getOfferLength() - 1)) {
						lastOffer = nbboChain.getOffer(offerPosition);
						offerTime = executionTime;
					} else {
						offerTime = executionTime;
					}
					if (executionTime > bidTime && bidPosition < (nbboChain.getBidLength() - 1)) {
						lastBid = nbboChain.getBid(bidPosition);
						bidPosition++;
						bidTime = nbboChain.getBidTime(bidPosition);
					} else if (bidPosition == (nbboChain.getBidLength() - 1)) {
						lastBid = nbboChain.getBid(bidPosition);
						bidTime = executionTime;
					} else {
						bidTime = executionTime;
					}
				}
			}
		}
	}

	private void addTrades(HashMap<String, ExecutionChain> executionChainMap, Map<String, TradeChain> tradeChainMap) {
		BasicMultiMap.List<String, ExecutionChain> em = new BasicMultiMap.List<String, ExecutionChain>();

		for (String eid : executionChainMap.keySet()) {
			ExecutionChain e = executionChainMap.get(eid);
			em.putMulti(e.getSymbol(), e);
		}

		for (String symbol : em.keySet()) {
			addTradesH(em.get(symbol), tradeChainMap.get(symbol));
		}
	}

	private void addTradesH(List<ExecutionChain> executions, TradeChain tradeChain) {
		Collections.sort(executions, ExecutionChain.compareTime);
		double lastTrade1M = 0;
		double lastTrade5M = 0;
		double currentTrade1M = 0;
		double currentTrade5M = 0;

		int execPos = 0;
		long execTime = 0;
		int currentPosition1M = -1;
		int currentPosition5M = -1;
		long currentTime1M = 0;
		long lastTime1M = 0;
		long currentTime5M = 0;
		long lastTime5M = 0;

		while (execPos < executions.size()) {
			ExecutionChain ec = executions.get(execPos);
			execTime = ec.getTime();

			long et1 = execTime + TIME_ONEMINUTE;
			long et5 = execTime + TIME_FIVEMINUTES;
			if (tradeChain == null) {
				ec.setTradePricePlusOne(ec.getPx());
				ec.setTradePricePlusFive(ec.getPx());
				execPos++;
			} else {

				if (currentTime1M > et1 && currentTime5M > et5 && (et1 - lastTime1M) <= TIME_ONEMINUTE && lastTime1M <= et1 && (et5 - lastTime5M) <= TIME_FIVEMINUTES
						&& lastTime5M <= et5) {
					ec.setTradePricePlusOne(lastTrade1M);
					ec.setTradePricePlusFive(lastTrade5M);
					execPos++;
				} else {
					//Inc scenario
					if (currentTime1M <= et1 || currentTime5M <= et5) {
						if (currentTime1M <= et1) {
							lastTrade1M = currentTrade1M;
							lastTime1M = currentTime1M;
							if (currentPosition1M < tradeChain.getLength() - 1) {
								currentPosition1M++;
								currentTrade1M = tradeChain.getPx(currentPosition1M);
								currentTime1M = tradeChain.getTime(currentPosition1M);
							} else {
								currentTime1M = et1 + 1;
							}
						}

						if (currentTime5M <= et5) {
							lastTrade5M = currentTrade5M;
							lastTime5M = currentTime5M;
							if (currentPosition5M < tradeChain.getLength() - 1) {
								currentPosition5M++;
								currentTrade5M = tradeChain.getPx(currentPosition5M);
								currentTime5M = tradeChain.getTime(currentPosition5M);
							} else {
								currentTime5M = et5 + 1;
							}
						}
					} else {
						//BallPark
						if (lastTime1M <= et1 && lastTime5M <= et5) {
							ec.setTradePricePlusOne(lastTrade1M);
							ec.setTradePricePlusFive(lastTrade5M);
							execPos++;
						} else {
							throw new UnsupportedOperationException();
						}

					}
				}

			}

		}
	}

	private Set<String> getParentsFromSymbol(String symbol) {
		HashSet<String> result = new HashSet<String>();
		for (String pid : parentOrdersMap.keySet()) {
			if (SH.equals(parentOrdersMap.get(pid).getSymbol(), symbol)) {
				result.add(pid);
			}
		}
		return result;
	}

	private Set<String> getChildsFromSymbol(String symbol) {
		HashSet<String> result = new HashSet<String>();
		for (String cid : childOrdersMap.keySet()) {
			if (SH.equals(childOrdersMap.get(cid).getSymbol(), symbol)) {
				result.add(cid);
			}
		}
		return result;
	}

	private Set<String> getExecutionsFromSymbol(String symbol) {
		HashSet<String> result = new HashSet<String>();
		for (String eid : executionsMap.keySet()) {
			if (SH.equals(executionsMap.get(eid).getSymbol(), symbol)) {
				result.add(eid);
			}
		}
		return result;
	}

	private Set<String> getChildsFromParents(String pid) {
		HashSet<String> result = new HashSet<String>();
		for (String cid : childOrdersMap.keySet()) {
			if (SH.equals(childOrdersMap.get(cid).getParentOrderID(), pid)) {
				result.add(cid);
			}
		}
		return result;
	}
	private Set<String> getExecutionsFromParents(String pid) {
		HashSet<String> result = new HashSet<String>();
		for (String eid : executionsMap.keySet()) {
			if (SH.equals(executionsMap.get(eid).getParentOrderID(), pid)) {
				result.add(eid);
			}
		}
		return result;
	}
	private Set<String> getExecutionsFromChilds(String cid) {

		HashSet<String> result = new HashSet<String>();
		for (String eid : executionsMap.keySet()) {
			if (SH.equals(executionsMap.get(eid).getChildOrderID(), cid)) {
				result.add(eid);
			}
		}
		return result;
	}
}
