package com.f1.generator;

import java.util.Date;
import java.util.Random;

import com.f1.generator.OrderCache.ORDER_STATE;
import com.f1.pofo.fix.MsgType;
import com.f1.pofo.fix.OrdStatus;

import quickfix.Message;
import quickfix.field.converter.UtcDateOnlyConverter;

public class Order implements Comparable<Order> {
	public static final String CLIENT_PREFIX = "3f";
	public static final String EXCHANGE_PREFIX = "3fE";
	private static final String TODAY_DATE_STRING = UtcDateOnlyConverter.convert(new Date());

	public static final char MARKET_ORDER = '1';
	public static final char LIMIT_ORDER = '2';

	public static enum SIDE {
								BUY,
								SELL
	}

	final static Random random = new Random();

	private String clOrdID;
	private final String symbol;
	private String origClOrdID = null;
	private String handlInst = "1";
	private SIDE side = SIDE.BUY;
	private final String securityExchangeMnemonic;

	private char ordType = MARKET_ORDER;
	private long qty = 100;
	private long leaveQty = 100;
	private long cumQty = 0;
	private double price;
	private String orderID = null;
	private final int client;

	private String execID = null;

	private double avgPx = 0;

	private double lastPx = 0;

	private long lastShare = 0;

	private int noContraBroker = 0;
	private String[] contraBroker = null;
	private String[] contraTrader = null;
	private String[] contraTradeQty = null;
	private String[] contraTradeTime = null;

	private Message fixMsg;
	private ORDER_STATE orderState = ORDER_STATE.NEW;
	private MsgType lastMsgType = MsgType.NEW_ORDER_SINGLE;
	private OrdStatus lastOrdStatus = OrdStatus.PENDING_ACK;

	static char[] randomPrefixChars = { 'a', 'b', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'w', 'x', 'y', 'z', 'A', 'B', 'D', 'E', 'F',
			'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
	static char[] randomPostfixChars = { 'a', 'b', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9' };

	public Order(int client, final String symbol, final String clOrdID, final String securityExchangeMnemonic) {
		this.symbol = symbol;
		this.clOrdID = clOrdID;
		this.client = client;
		this.securityExchangeMnemonic = securityExchangeMnemonic;
	}

	public Order setNoContraBroker(int noContraBroker) {
		this.noContraBroker = noContraBroker;
		if (null == contraBroker || contraBroker.length < noContraBroker) {
			this.contraBroker = new String[noContraBroker];
			this.contraTrader = new String[noContraBroker];
			this.contraTradeQty = new String[noContraBroker];
			this.contraTradeTime = new String[noContraBroker];
		}

		return this;
	}

	public int getNoContraBroker() {
		return noContraBroker;
	}

	public MsgType getLastMsgType() {
		return lastMsgType;
	}

	public Order setLastMsgType(MsgType msgType) {
		this.lastMsgType = msgType;
		return this;
	}

	public OrdStatus getLastOrdStatus() {
		return lastOrdStatus;
	}

	public Order setLastOrdStatus(OrdStatus ordStatus) {
		lastOrdStatus = ordStatus;
		return this;
	}

	public Order setContraBroker(int i, final String contraBroker) {
		if (i >= 0 && i < noContraBroker) {
			this.contraBroker[i] = contraBroker;
		}
		return this;
	}

	public String getContraBroker(int i) {
		if (i >= 0 && i < noContraBroker) {
			return contraBroker[i];
		}
		return null;
	}

	public Order setContraTrader(int i, final String contraTrader) {
		if (i >= 0 && i < noContraBroker) {
			this.contraTrader[i] = contraTrader;
		}
		return this;
	}

	public String getContraTrader(int i) {
		if (i >= 0 && i < noContraBroker) {
			return contraTrader[i];
		}
		return null;
	}

	public Order setContraTradeQty(int i, final String contraTradeQty) {
		if (i >= 0 && i < noContraBroker) {
			this.contraTradeQty[i] = contraTradeQty;
		}
		return this;
	}

	public String getContraTradeQty(int i) {
		if (i >= 0 && i < noContraBroker) {
			return contraTradeQty[i];
		}
		return null;
	}

	public Order setContraTradeTime(int i, final String contraTradeTime) {
		if (i >= 0 && i < noContraBroker) {
			this.contraTradeTime[i] = contraTradeTime;
		}
		return this;
	}

	public String getContraTradeTime(int i) {
		if (i >= 0 && i < noContraBroker) {
			return contraTradeTime[i];
		}
		return null;
	}

	public Order setLastPxAndShare(double lastPx, long lastShare) {
		if (lastShare > leaveQty) {
			// should never happen.
			throw new IllegalStateException("cannot fill more than the leaveQty: " + leaveQty + " lastShare: " + lastShare);
		}

		avgPx = Math.round(((cumQty * avgPx + lastPx * lastShare) / (lastShare + cumQty) * 100.0)) / 100.0;
		setCumQty(lastShare + cumQty);
		this.lastPx = lastPx;
		this.lastShare = Math.abs(lastShare);
		return this;
	}

	public double getLastPx() {
		return lastPx;
	}

	public long getLastShare() {
		return lastShare;
	}

	public double getAvgPx() {
		return avgPx;
	}

	public String getSecurityExchangeMnemonic() {
		return securityExchangeMnemonic;
	}

	public Order setExecID(final String execID) {
		this.execID = execID;
		return this;
	}

	public String getExecID() {
		return execID;
	}

	public int getClient() {
		return client;
	}

	public String getOrderID() {
		return orderID;
	}

	public Order setOrderID(final String orderID) {
		this.orderID = orderID;
		return this;
	}

	public Order setOrderState(ORDER_STATE orderState) {
		this.orderState = orderState;
		return this;
	}

	public ORDER_STATE getOrderState() {
		return orderState;
	}

	public String getHandlInst() {
		return handlInst;
	}

	public Order setHandlInst(String handlInst) {
		this.handlInst = handlInst;
		return this;
	}

	public int getSide() {
		return side2Int(side);
	}

	public Order setSide(SIDE side) {
		this.side = side;
		return this;
	}

	public SIDE getSideInEnum() {
		return side;
	}

	public Order setSideInEnum(SIDE side) {
		this.side = side;
		return this;
	}

	public char getOrdType() {
		return ordType;
	}

	public Order setOrdType(char ordType) {
		this.ordType = ordType;
		return this;
	}

	public long getQty() {
		return qty;
	}

	public Order setQty(long qty) {
		this.qty = qty;
		this.leaveQty = Math.max(qty - cumQty, 0);
		return this;
	}

	public long getLeaveQty() {
		return leaveQty;
	}

	//	public Order setLeaveQty(long leaveQty) {
	//		this.leaveQty = leaveQty;
	//		this.cumQty = qty - leaveQty;
	//		return this;
	//	}

	public long getCumQty() {
		return cumQty;
	}

	// used by Trade Cancel.  May need to update avgPx with lastPx and lastShare property?
	public Order setCumQty(long cumQty) {
		this.cumQty = cumQty;
		this.leaveQty = qty - cumQty;
		return this;
	}

	public double getPrice() {
		return price;
	}

	public Order setPrice(double price) {
		this.price = price;
		return this;
	}

	public Message getFixMsg() {
		return fixMsg;
	}

	public Order setFixMsg(Message fixMsg) {
		this.fixMsg = fixMsg;
		return this;
	}

	public Order setClOrdID(final String clOrdID) {
		this.clOrdID = clOrdID;
		return this;
	}

	public String getClOrdID() {
		return clOrdID;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getOrigClOrdID() {
		return origClOrdID;
	}

	public Order setOrigClOrdID(final String origClOrdID) {
		this.origClOrdID = origClOrdID;
		return this;
	}

	@Override
	public int compareTo(Order o) {
		//return side == SIDE.BUY ? Double.compare(o.price, price) : Double.compare(price, o.price);
		return Double.compare(price, o.price);
	}

	public static int side2Int(SIDE side) {
		return side == SIDE.BUY ? 1 : 2;
	}

	public static String createID(final String prefix, int clientId, int nextId) {
		//return String.format("%s%03d-%s:%03d", prefix, clientId, TODAY_DATE_STRING, nextId);
		return String.format("%s%c%c%c-%03d-%03d-%c%c%c", prefix, randomPrefixChars[random.nextInt(randomPrefixChars.length)],
				randomPrefixChars[random.nextInt(randomPrefixChars.length)], randomPrefixChars[random.nextInt(randomPrefixChars.length)], clientId, nextId,
				randomPostfixChars[random.nextInt(randomPostfixChars.length)], randomPostfixChars[random.nextInt(randomPostfixChars.length)],
				randomPostfixChars[random.nextInt(randomPostfixChars.length)]);
		//return String.format("%03d", nextId);
	}

	public static String createID(final String prefix, int nextId) {
		//if (null != prefix) {
		//	return String.format("%s-%s:%03d", prefix, TODAY_DATE_STRING, nextId);
		//}
		//return String.format("%s:%03d", TODAY_DATE_STRING, nextId);
		return String.format("%s%c%c-%c%c%c-%03d-%c%c%c", prefix, randomPrefixChars[random.nextInt(randomPrefixChars.length)],
				randomPrefixChars[random.nextInt(randomPrefixChars.length)], randomPrefixChars[random.nextInt(randomPrefixChars.length)],
				randomPrefixChars[random.nextInt(randomPrefixChars.length)], randomPrefixChars[random.nextInt(randomPrefixChars.length)], nextId,
				randomPostfixChars[random.nextInt(randomPostfixChars.length)], randomPostfixChars[random.nextInt(randomPostfixChars.length)],
				randomPostfixChars[random.nextInt(randomPostfixChars.length)]);
	}

}
