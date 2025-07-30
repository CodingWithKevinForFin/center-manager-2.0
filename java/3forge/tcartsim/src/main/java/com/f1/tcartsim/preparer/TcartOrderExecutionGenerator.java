package com.f1.tcartsim.preparer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import com.f1.utils.CH;
import com.f1.utils.FastBufferedOutputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.BasicMultiMap;

public class TcartOrderExecutionGenerator {

	private static final Logger log = LH.get();
	static private List<String> systems = new ArrayList<String>();
	static private List<String> strategies = new ArrayList<String>();
	private static List<String> indicators = CH.l("AL", "RL", "OL", "AL", "AL", "RL");
	private static int nextExecId;

	public static void main(String args[]) throws IOException {
		File tradeFile = new File(args[0]);
		File accountFile = new File(args[1]);
		File orderOutFile = new File(args[2]);
		double pctOrders = SH.parseDouble(args[3]);//.405;
		double pctExecs = SH.parseDouble(args[4]);//.3;
		double pctBustCorrect = SH.parseDouble(args[5]);//.3;
		double pctOrdersWithNoChildren = .5;
		main(new TcartSimManager(), tradeFile, accountFile, orderOutFile, pctOrders, pctExecs, pctBustCorrect, pctOrdersWithNoChildren, 1);
	}
	public static void main(TcartSimManager r, File tradeInFile, File accountInFile, File orderOutFile, double pctOrders, double pctExecs, double pctBustCorrect,
			double pctOrdersWithNoChildren, double accountsPercent) throws IOException {
		for (int i = 1; i < 8; i++) {
			systems.add("ALG_" + i);
			systems.add("SOR:" + i);
			systems.add("PortfolioTrader-" + i);
			systems.add("SALESDESK" + i);
			systems.add("AMSNorthAmerica" + i);
			systems.add("ExecManager--" + i);
			systems.add("GlobalRouter " + i);
		}
		strategies.add("TWAP");
		strategies.add("VWAP");
		strategies.add("VWAP2");
		strategies.add("MARK");
		strategies.add("MKT");
		strategies.add("SLICER");
		strategies.add("QUICK");
		strategies.add("CUSTOM1");
		strategies.add("CUSTOM2");
		strategies.add("CUSTOM3");
		strategies.add("TWAP_STP");
		strategies.add("AGG1");
		strategies.add("AGG2");
		strategies.add("AGG3");
		strategies.add("POST");
		systems.add("3F-OMS");
		List<String> accountNames = CH.l(SH.splitLines(IOH.readText(accountInFile)));
		if (accountsPercent < 1) {
			accountNames = new ArrayList(accountNames.subList(0, (int) (accountNames.size() * accountsPercent)));
		}
		//		double pctOrders = .605;
		//		double pctExecs = .5;
		//		double pctEmptyOrder = .6;
		//		double pctCancel = .01;
		//		double pctModify = .04;
		//double pctExecs = .3;
		//		double pctEmptyOrder = .6;
		//		double pctCancel = .01;
		//		double pctModify = .04;

		com.f1.utils.structs.BasicMultiMap.List<String, TcaParentOrder> ordersBySymbol = new BasicMultiMap.List<String, TcaParentOrder>();
		LineNumberReader tradeReader = new LineNumberReader(new FileReader(tradeInFile), 100000);
		FastBufferedOutputStream orderWriter = new FastBufferedOutputStream(new FileOutputStream(orderOutFile), 10000);
		int currentOrderID = 0;
		//		int executions = 0;
		long offset = -1;
		HashMap<String, String> exMapping = new HashMap<String, String>();
		initExMapping(exMapping);
		//		int emptyOrders = 0;
		int unititedOrders = 0;
		//int cancels = 0;
		//int mods = 0;
		int childOrders = 0;

		try {
			for (int cnt = 0;; cnt++) {
				String line = null;
				try {
					/* ------------------------------------
					 * ------------------------------------
					 * CREATE PARENT ORDERS
					 * ------------------------------------
					 * ------------------------------------
					 */
					if (cnt % 1000000 == 0)
						LH.info(log, "Processed ", cnt);
					//StringBuilder sb = new StringBuilder();
					line = tradeReader.readLine();
					if (line == null)
						break;
					String ex = line.substring(9, 10);
					ex = exMapping.get(ex);
					String sym = SH.trim(line.substring(10, 26));
					int size = SH.parseInt(line, 30, 39, 10);
					long time = r.parseTimeFromLine(line);
					int price_w = SH.parseInt(line, 39, 46, 10);
					int price_d = SH.parseInt(line, 46, 50, 10);
					double price = price_w + price_d / 10000.0;
					if (r.nextDouble() >= pctExecs) {
						continue;
					}
					String oID = "";
					String side = "";
					String orderSymbol = "";
					TcaParentOrder order;
					if (!ordersBySymbol.containsKey(sym) || r.nextDouble() < pctOrders) {
						order = new TcaParentOrder(r, currentOrderID++, r.nextBoolean() ? 'B' : 'S', sym, ex, accountNames.get(r.nextInt(accountNames.size()))).update(time, price,
								size);
						oID = order.getID();
						side = SH.toString(order.getSide());
						orderSymbol = order.sym;
						ordersBySymbol.putMulti(sym, order);
					} else {
						order = r.getRandom(ordersBySymbol.get(sym));
						while (order.isCanceled) {
							order = r.getRandom(ordersBySymbol.get(sym));
						}
						order.update(time, price, size);
						oID = order.getID();
						side = SH.toString(order.getSide());
						orderSymbol = order.sym;
					}
					String execId = "EXEID-" + MH.abs(r.nextInt()) + "." + (nextExecId++);
					char sideChar = side.charAt(0);
					String ind = r.getRandom(indicators);
					String currency = r.getCurrency();
					TcartExecution execution = new TcartExecution(currency, oID, sideChar, r.getPrice(currency, price), size, time, orderSymbol, ex, ind, execId);
					order.attachExecution(execution);
					if (r.nextDouble() <= pctBustCorrect) {
						long time2 = time + r.nextInt(1000 * 600);
						switch (r.nextInt(3)) {
							case 0: {//correct
								//System.out.println("correct: " + oID + "|" + orderSymbol + "|" + execId);
								ind = r.getRandom(indicators);
								order.attachExecution(new TcartExecution(currency, oID, sideChar, r.getPrice(currency, price - .01), size / 2, time2, orderSymbol, ex, ind, execId));
								break;
							}
							case 1: {//bust
								//System.out.println("bust: " + oID + "|" + orderSymbol + "|" + execId);
								order.attachExecution(new TcartExecution(currency, oID, sideChar, r.getPrice(currency, price), 0, time2, orderSymbol, ex, ind, execId));
								break;
							}
							case 2: {//correct and bust
								//System.out.println("correct and bust: " + oID + "|" + orderSymbol + "|" + execId);
								ind = r.getRandom(indicators);
								order.attachExecution(new TcartExecution(currency, oID, sideChar, r.getPrice(currency, price + .01), size / 2, time2, orderSymbol, ex, ind, execId));
								order.attachExecution(new TcartExecution(currency, oID, sideChar, r.getPrice(currency, price + .01), 0, time2 + 1000, orderSymbol, ex, ind, execId));
								break;
							}
						}
					}
					//executions++;

				} catch (NumberFormatException e) {
					LH.warning(log, "Error on line ", cnt, ": ", e.getMessage());
					continue;
				}
			}

		} finally {
			orderWriter.flush();
			IOH.close(tradeReader);
		}
		TcartDistributionGenerator childMessageDist = new TcartDistributionGenerator(20, .26, 0, r.getRandom());
		TcartDistributionGenerator childOrderDistribution = new TcartDistributionGenerator(50, 2.5, 1, r.getRandom());
		List<TcaParentOrder> orders = CH.l(ordersBySymbol.valuesMulti());
		ordersBySymbol.clear();
		LH.info(log, "Sorting Orders");
		Collections.sort(orders);
		LH.info(log, "done sorting");
		int childOrdersCnt;
		/* ------------------------------------
		 * ------------------------------------
		 * CREATE CHILD ORDERS
		 * ------------------------------------
		 * ------------------------------------
		 */
		int modsCnt = 0;
		int placedEx = 0;
		int lostEx = 0;

		for (int i = 0; i < orders.size(); i++) {
			TcaParentOrder order = orders.set(i, null);
			if (order.isInit()) {
				if (order.getExecutions().size() > 0) {
					childOrdersCnt = childOrderDistribution.getValueFromDistribution();
					for (int b = 0; b < childOrdersCnt; b++) {
						childOrders++;
						TcartChildOrder childOrder = order.createChildOrder();
						int modifyCounts = childMessageDist.getValueFromDistribution();
						for (int m = 0; m < modifyCounts; m++) {
							childOrder = childOrder.modify(m + 1 == modifyCounts && m > 0 && r.nextBoolean() ? 'C' : 'U');
							modsCnt++;
						}
					}
					for (TcartExecution execution : order.getExecutions()) {
						if (order.attachExecutionToChildOrder(execution))
							placedEx++;
						else
							lostEx++;
					}
				}

			} else {
				unititedOrders++;
			}
			order.shrinkChildOrderTimes();
			order.finalizeTargetSize();
			boolean writeChildren = r.nextDouble() >= pctOrdersWithNoChildren;
			order.writeOrderChildrenAndExecutions(orderWriter, writeChildren);
			if (i % 10000 == 0)
				LH.info(log, new Date() + ": processed " + i);
		}
		LH.info(log, unititedOrders);
		orderWriter.flush();
		LH.info(log, "ParentOrders: " + currentOrderID + " CHILD ORDERS: " + childOrders + " MODS ==> " + modsCnt + " ORDERS to MODS RATIO ==> " + (modsCnt * 1D / currentOrderID)
				+ " LOST EXECUTIONS ==> " + lostEx + " PLACED EX ==> " + placedEx);

	}
	String generateAccount() {
		return null;
	}
	private static void initExMapping(HashMap<String, String> exMapping) {
		exMapping.put("A", "NYSE MKT");
		exMapping.put("B", "NASDAQ BX");
		exMapping.put("C", "NSE");
		exMapping.put("D", "FINRA");
		exMapping.put("I", "ISE");
		exMapping.put("J", "EDGA");
		exMapping.put("K", "EDGX");
		exMapping.put("M", "CHX");
		exMapping.put("N", "NYSE");
		exMapping.put("P", "NYSE ARCA");
		exMapping.put("S", "CTS");
		exMapping.put("T", "NASDAQ");
		exMapping.put("Q", "NASDAQ");
		exMapping.put("W", "CBOE");
		exMapping.put("X", "NASDAQ PSX");
		exMapping.put("Y", "BATS Y");
		exMapping.put("Z", "BATS");
	}

	static class TcartExecution {
		private String parentOrderId;
		private String exIndicator;
		private double price;
		private char side;
		private int size;
		private long time;
		private String sym;
		private String currency;
		private String ex;
		private String orderID;
		private String execId;

		public TcartExecution(String currency, String parentOrderID, char side, double price, int size, long time, String orderSymbol, String ex, String exInd, String execId) {
			this.time = time;
			this.parentOrderId = parentOrderID;
			this.side = side;
			this.price = price;
			this.size = size;
			this.sym = orderSymbol;
			this.currency = currency;
			this.ex = ex;
			this.exIndicator = exInd;
			this.execId = execId;
		}
		public byte[] composeMessage(boolean writeChildren) {
			StringBuilder sb = new StringBuilder();
			sb.append(time).append("|E|");
			sb.append(sym).append("|");
			sb.append(ex).append("|");
			sb.append(price).append("|");
			sb.append(size).append("|");
			if (writeChildren) {
				sb.append(orderID).append("|");
				sb.append("|");
			} else {
				sb.append("|");
				sb.append(parentOrderId).append("|");
			}
			sb.append(side).append("|");
			sb.append(exIndicator);
			sb.append("|");
			sb.append(execId);
			sb.append("|");
			sb.append(currency);
			sb.append("\n");
			return sb.toString().getBytes();
		}
	}

	static class TcaParentOrder implements Comparable<TcaParentOrder> {
		private final String id;
		private long startTime = -1;
		private long endTime = -1;
		private double priceLow;
		private double priceHigh;
		private long size = 0;
		private char side;
		public final String sym;
		private final String ex;
		private boolean isCanceled;
		String account;
		String system;
		String strategy;
		private ArrayList<TcartExecution> executions;
		private TcartSimManager r;
		public ArrayList<TcartChildOrder> childOrders = new ArrayList<TcartChildOrder>();
		final private String currency;

		public TcaParentOrder(TcartSimManager r2, int id, char c, String sym, String ex, String account) {
			this.r = r2;
			StringBuilder sb = new StringBuilder();
			this.currency = r2.getCurrency();
			this.system = r.getRandom(systems);
			this.strategy = r.getRandom(strategies);
			sb.append("I").append(system, 0, 2).append(sym.charAt(0)).append("-").append(id).append('-').append(r2.getGuid(5));
			SH.uppercaseInplace(sb);
			this.id = sb.toString();
			this.side = c;
			this.sym = sym;
			this.ex = ex;
			this.account = account;
			setIsCanceled(false);
			this.setExecutions(new ArrayList<TcartExecution>());
		}
		public void writeOrderChildrenAndExecutions(FastBufferedOutputStream orderWriter, boolean writeChildren) throws IOException {
			orderWriter.write(compseParentOrderMessage());
			for (TcartChildOrder childOrder : childOrders) {
				if (writeChildren) {
					orderWriter.write(childOrder.composeMessage());
					orderWriter.write(childOrder.composeAckMessages());
				}
				childOrder.writeExecutions(orderWriter, writeChildren);
			}
			boolean print = r.nextBoolean();
			long time = startTime;
			while (r.nextBoolean() && time < endTime - 10000) {
				time = time + r.nextInt(10000);
				orderWriter.write(writeModify(time).getBytes());
			}
			if (r.nextBoolean() && time < endTime - 100)
				orderWriter.write(writeCancel(time + 100).getBytes());
		}
		private byte[] compseParentOrderMessage() {
			StringBuilder sb = new StringBuilder();
			sb.append(startTime + "|O|" + sym + "|" + side + "|");
			if (side == 'B')
				sb.append(getPrice(r.nextBoolean() ? priceHigh : priceHigh + priceHigh / 100 * r.nextInt(5)));
			else
				sb.append(getPrice(r.nextBoolean() ? priceLow : priceLow - priceLow / 100 * r.nextInt(5)));
			sb.append("|");
			sb.append(size);
			sb.append("|" + id + "|N|" + system + "|" + strategy + "|" + account + "|");
			sb.append(startTime);
			sb.append("|");
			sb.append(endTime);
			sb.append("|");
			sb.append(currency);
			sb.append('\n');
			return sb.toString().getBytes();
		}
		private boolean finalized = false;
		public void finalizeTargetSize() {
			OH.assertFalse(finalized);
			finalized = true;
			long target = 0;
			int previousExecutedAmount = 0;
			for (TcartChildOrder childOrder : childOrders) {
				if (childOrder.origId == "")
					previousExecutedAmount = 0;
				target += childOrder.finalizeTargetQtyAndIds(previousExecutedAmount);
				previousExecutedAmount += childOrder.getExecutionQuantity();

			}
			if (r.nextDouble() < .0002) {
				this.size = 100 + 100 * r.nextInt(3) + (r.nextBoolean() ? 50 : 0);
				//OH.assertGt(size, 0);
			} else {
				this.size = target;
				//OH.assertGt(size, 0);
			}
		}
		public void shrinkChildOrderTimes() {
			for (TcartChildOrder childOrder : childOrders) {
				childOrder.shrinkTimes();
			}
		}
		public boolean attachExecutionToChildOrder(TcartExecution execution) {
			long time = execution.time;
			int exitCnt = 0;
			for (;;) {
				TcartChildOrder childOrder = childOrders.get(r.nextInt(childOrders.size()));
				if (time > childOrder.startTime && time < childOrder.endTime) {
					childOrder.attachExecution(execution);
					return true;
				}
				if (exitCnt++ > 25)
					return false;
			}

		}
		public TcartChildOrder createChildOrder() {
			long duration = endTime - startTime;
			if (duration == 0)
				LH.info(log, this + "had no duration");
			long childStart = startTime;
			//			long childStart = startTime + r.nextInt((int) MH.round(duration * .01, MH.ROUND_DOWN));
			long childEnd = endTime;
			TcartChildOrder childOrder = new TcartChildOrder(r, this, childStart, childEnd, 'N');
			childOrders.add(childOrder);
			return childOrder;
		}
		public void attachExecution(TcartExecution execution) {
			getExecutions().add(execution);
		}

		public String writeModify(long time) {
			StringBuilder sb = new StringBuilder();
			sb.append(time + "|O|" + sym + "|" + side + "|");
			if (side == 'B') {
				if (r.nextBoolean())
					priceHigh += priceHigh / 100d * r.nextInt(5);
				sb.append(getPrice(priceHigh));
			} else {
				if (r.nextBoolean())
					priceLow -= priceLow / 100d * r.nextInt(5);
				sb.append(getPrice(priceLow));
			}
			sb.append("|");
			if (r.nextBoolean())
				size += size / 100 * r.nextInt(5);
			sb.append(size);
			sb.append("|" + id + "|U|" + system + "|" + strategy + "|" + account + "|");
			//			sb.append(r.nextBoolean() ? startTime : startTime - 1000 * r.nextInt(150));
			sb.append("|");
			sb.append(endTime + r.nextInt(5));
			sb.append("|");
			sb.append(this.currency);
			sb.append("\n");
			return sb.toString();
		}

		private double getPrice(double d) {
			return r.getPrice(currency, d);
		}
		public String writeCancel(long time) {
			// TODO Auto-generated method stub
			StringBuilder sb = new StringBuilder();
			sb.append(time + "|O|" + sym + "|" + side + "|");
			if (side == 'B')
				sb.append(getPrice(priceHigh));
			else
				sb.append(getPrice(priceLow));
			sb.append("|");
			sb.append(size);//
			//sb.append(r.nextBoolean() ? size : size + size / 100 * r.nextInt(5));
			sb.append("|" + id + "|C|" + system + "|" + strategy + "|" + account + "|");
			//			sb.append(r.nextBoolean() ? startTime : startTime - 1000 * r.nextInt(150));
			sb.append("|");
			sb.append(time + r.nextInt(10) + 5);
			sb.append("|");
			sb.append(this.currency);
			sb.append("\n");
			return sb.toString();
		}

		public TcaParentOrder generateSize(int i) {
			this.size = i;
			//OH.assertGt(size, 0);
			return this;
		}

		public Object getSide() {
			return side;
		}

		public boolean isInit() {
			return startTime != -1;
		}

		public TcaParentOrder update(long exTime, double exPrice, int exSize) {
			if (startTime == -1) {
				startTime = exTime - (600 + 100 * r.nextInt(4));
				priceLow = exPrice;
				priceHigh = exPrice;
				size += exSize;
				//OH.assertGt(size, 0);
			} else {
				if (priceLow > exPrice)
					priceLow = exPrice;
				else if (priceHigh < exPrice)
					priceHigh = exPrice;
				size += exSize;
				OH.assertGe(size, 0);
			}
			endTime = exTime + 60000 + 10000 * r.nextInt(4);
			return this;
		}

		public String getID() {
			return id;
		}
		public String writeOrder(Random r) {
			StringBuilder sb = new StringBuilder();
			sb.append(startTime + "|O|" + sym + "|" + side + "|");
			if (side == 'B')
				sb.append(getPrice(r.nextBoolean() ? priceHigh : priceHigh + priceHigh / 100 * r.nextInt(5)));
			else
				sb.append(getPrice(r.nextBoolean() ? priceLow : priceLow - priceLow / 100 * r.nextInt(5)));
			sb.append("|");
			sb.append(r.nextBoolean() ? size : size + size / 100 * r.nextInt(5));
			sb.append("|" + id + "|N|" + system + "|" + strategy + "|" + account + "|");
			sb.append(r.nextBoolean() ? startTime : startTime - 1000 * r.nextInt(150));
			sb.append("|");
			sb.append(r.nextBoolean() ? endTime : endTime + 1000 * r.nextInt(150));
			sb.append("|");
			sb.append(this.currency);
			return sb.toString();
		}
		@Override
		public int compareTo(TcaParentOrder o) {
			return OH.compare(startTime, o.startTime);
		}

		public boolean isCanceled() {
			return isCanceled;
		}

		public void setIsCanceled(boolean isOpen) {
			this.isCanceled = isOpen;
		}
		public double getLimitPx() {
			if (side == 'B')
				return priceHigh;
			else
				return priceLow;
		}
		public ArrayList<TcartExecution> getExecutions() {
			return executions;
		}
		public void setExecutions(ArrayList<TcartExecution> executions) {
			this.executions = executions;
		}
	}

	static class TcartChildOrder {

		private long time;
		private long qty;
		private String symbol;
		private double px;
		private char side;
		private String parentOrderId;
		private String orderId;
		private String origId = "";
		private long startTime;
		private long endTime;
		private TcaParentOrder parentOrder;
		private char status;
		private ArrayList<TcartExecution> executions;
		private boolean wasModified = false;
		private TcartSimManager r;

		public TcartChildOrder(TcartSimManager r2, TcaParentOrder tcaParentOrder, long childStart, long childEnd, char status) {
			this.r = r2;
			this.status = status;
			parentOrder = tcaParentOrder;
			startTime = childStart;
			endTime = childEnd;
			parentOrderId = tcaParentOrder.getID();
			executions = new ArrayList<TcartExecution>();
			symbol = tcaParentOrder.sym;
			px = tcaParentOrder.side == 'B' ? tcaParentOrder.priceHigh : tcaParentOrder.priceLow;
			if (status == 'N') {
				StringBuilder sb = new StringBuilder();
				sb.append("I").append(symbol).append("-").append(r2.getGuid(10));
				SH.uppercaseInplace(sb);
				orderId = sb.toString();
			}
		}
		public int getExecutionQuantity() {
			int r = 0;
			for (TcartExecution i : this.executions)
				r += i.size;
			return r;
		}
		public byte[] composeAckMessages() {
			StringBuilder sb = new StringBuilder();
			long ackTime = time;
			if (r.nextDouble() < .99)
				appendPendingAck(r, sb, ackTime);
			boolean childHasExecutionsOrWasModified = hasExecutions() || wasModified;
			boolean rejected = false;
			if (childHasExecutionsOrWasModified)
				appendAckMsg(r, sb, ackTime, "A");
			else if (r.nextDouble() > .999) {
				appendAckMsg(r, sb, ackTime, "R");
				rejected = true;
			} else
				appendAckMsg(r, sb, ackTime, "A");
			if (!childHasExecutionsOrWasModified && r.nextDouble() > .9999 && !rejected)
				appendAckMsg(r, sb, ackTime, "C");
			return sb.toString().getBytes();
		}
		private void appendAckMsg(TcartSimManager r2, StringBuilder sb, long ackTime, String msgType) {
			ackTime = ackTime + 2 + r2.nextInt(8);
			sb.append(ackTime);
			sb.append("|R|");
			sb.append(symbol);
			sb.append("|");
			sb.append(msgType);
			sb.append("|");
			sb.append(orderId);
			sb.append('\n');

		}
		private boolean hasExecutions() {
			return executions.size() > 0;
		}
		private void appendPendingAck(TcartSimManager r2, StringBuilder sb, long ackTime) {
			ackTime = ackTime + r2.nextInt(5);
			sb.append(ackTime);
			sb.append("|R|");
			sb.append(symbol);
			sb.append("|P|");
			sb.append(orderId);
			sb.append('\n');
		}
		public void writeExecutions(FastBufferedOutputStream orderWriter, boolean writeChildren) throws IOException {
			for (TcartExecution execution : executions) {
				orderWriter.write(execution.composeMessage(writeChildren));
			}
		}
		private boolean finalized = false;
		public long finalizeTargetQtyAndIds(int previousExecutedAmount) {
			OH.assertFalse(finalized);
			finalized = true;
			OH.assertGe(previousExecutedAmount, 0);
			long target = previousExecutedAmount;
			for (TcartExecution execution : executions) {
				target += execution.size;
				execution.orderID = orderId;
			}
			if (target == 0)
				target = 100;
			//OH.assertBetween(target, 0, 100000000);
			qty = target;
			return target;
		}
		public void shrinkTimes() {
			long minTime = -1;
			long maxTime = -1;
			boolean isFirst = true;
			for (TcartExecution execution : executions) {
				if (isFirst) {
					minTime = maxTime = execution.time;
					isFirst = false;
				} else {
					minTime = Math.min(minTime, execution.time);
					maxTime = Math.max(maxTime, execution.time);
				}
			}
			if (!wasModified) {
				if (minTime == -1) {
					long duration = endTime = startTime;
					startTime = (long) (startTime + r.nextDouble() * duration * .45);
					endTime = (long) (endTime - r.nextDouble() * duration * .45);
				} else {
					startTime = MH.avg(minTime, startTime);
					endTime = MH.avg(maxTime, endTime);
				}
			}
			time = startTime - 5;
		}
		public void attachExecution(TcartExecution execution) {
			executions.add(execution);
		}
		public TcartChildOrder modify(char status) {
			long duration = endTime - startTime;
			long modStart = (long) (startTime + r.nextDouble() * .4 * duration) + 1;
			long modEnd = endTime;
			this.endTime = modStart;
			TcartChildOrder childOrder = new TcartChildOrder(r, parentOrder, modStart, modEnd, status);
			childOrder.orderId = orderId;
			childOrder.changeOrderIds();
			parentOrder.childOrders.add(childOrder);
			this.wasModified = true;
			return childOrder;
		}

		private void changeOrderIds() {
			origId = orderId;
			StringBuilder sb = new StringBuilder();
			sb.append("I").append(symbol).append("-").append(r.getGuid(10));
			SH.uppercaseInplace(sb);
			orderId = sb.toString();
		}
		public byte[] composeMessage() {
			String status = SH.toString(this.status);
			StringBuilder sb = new StringBuilder();
			sb.append(time);
			sb.append("|");
			sb.append("C");
			sb.append("|");
			sb.append(symbol);
			sb.append("|");
			sb.append(this.parentOrder.getPrice(px));
			sb.append("|");
			sb.append(qty);
			sb.append("|");
			sb.append(parentOrderId);
			sb.append("|");
			sb.append(status);
			sb.append("|");
			sb.append(orderId);
			sb.append("|");
			sb.append(origId);
			sb.append("|");
			sb.append(this.parentOrder.currency);
			sb.append('\n');
			return sb.toString().getBytes();
		}
	}
}