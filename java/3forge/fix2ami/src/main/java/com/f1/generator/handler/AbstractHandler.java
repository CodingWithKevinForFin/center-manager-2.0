package com.f1.generator.handler;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.fix2ami.processor.AbstractFix2AmiProcessor;
import com.f1.fix2ami.tool.ToolUtils;
import com.f1.generator.Order;
import com.f1.generator.Order.SIDE;
import com.f1.generator.OrderCache;
import com.f1.generator.OrderCache.ORDER_STATE;
import com.f1.generator.OrderEvent;
import com.f1.generator.StockInfo;
import com.f1.pofo.fix.MsgType;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;

import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.DefaultDataDictionaryProvider;
import quickfix.FieldConvertError;
import quickfix.field.converter.UtcTimestampConverter;

public abstract class AbstractHandler extends BasicProcessor<OrderEvent, OrderCache> {
	private static final Logger log = Logger.getLogger(AbstractHandler.class.getName());

	public OutputPort<OrderEvent> fixMsgPort = newOutputPort(OrderEvent.class);

	final Random random = new Random();

	final static int TAG_Account = 1;
	final static int TAG_Currency = 15;
	final static int TAG_SecurityDesc = 107;
	final static int TAG_QuoteID = 117;
	final static int TAG_ComplianceID = 376;
	final static int TAG_ClearingFirm = 439;
	final static int TAG_ClearingAccount = 440;
	final static int TAG_ExecBroker = 76;
	final static int TAG_MsgType = 35;
	final static int TAG_ClOrdID = 11;
	final static int TAG_OrigClOrdID = 41;
	final static int TAG_OrdStatus = 39;
	final static int TAG_TransactTime = 60;
	final static int TAG_ExecType = 150;
	final static int TAG_ExecTransType = 20;
	public final static int TAG_HandlInst = 21;
	final static int TAG_Symbol = 55;
	final static int TAG_Side = 54;
	final static int TAG_Qty = 38;
	final static int TAG_OrdType = 40;
	final static int TAG_Price = 44;
	final static int TAG_OrderID = 37;
	final static int TAG_SecurityExchange = 207;
	final static int TAG_Text = 58;
	final static int TAG_LeaveQty = 151;
	final static int TAG_ExecID = 17;
	final static int TAG_CumQty = 14;
	final static int TAG_AvgPx = 6;
	final static int TAG_LastMkt = 30;
	final static int TAG_LastPx = 31;
	final static int TAG_LastShare = 32;
	final static int TAG_NoContraBroker = 382;
	final static int TAG_ContraBroker = 375;
	final static int TAG_ContraTrader = 337;
	final static int TAG_ContraTradeQty = 437;
	final static int TAG_ContraTradeTime = 438;
	final static int TAG_ExecRefID = 19;
	final static int TAG_CxlRejResponseTo = 434;

	final static String ATTR_DATA_DICTIONARY = "generator.DataDictionary";
	final static String ATTR_DATA_DICTIONARY_VERSION = "generator.datadictionary.Version";
	final static String ATTR_GENERATOR_MAX_SLEEP_RANDOM = "generator.MaxRandomSleepTimeInMilli";
	final static String ATTR_GENERATOR_CLIENT_CANCEL_PROBABILITY = "generator.client.CancelProbabilityInPercent";
	final static String ATTR_GENERATOR_CLIENT_MOD_PROBABILITY = "generator.client.ModProbabilityInPercent";
	final static String ATTR_GENERATOR_CLIENT_MOD_PRICE_PROBABILITY = "generator.client.ModPriceProbabilityInPercent";
	final static String ATTR_GENERATOR_CLIENT_MARKET_ORDER_PROBABILITY = "generator.client.MarketOrderProbabilityInPercent";

	final static String ATTR_GENERATOR_MAX_CLIENT = "generator.MaxNumberOfClient";

	public final static String ATTR_GENERATOR_PRICE_SWING = "generator.price.swing.percent"; // ex. generator.price.swing.percent.MSFT=10
																								//     generator.price.swing.All=20 applies to all unspecified symbol.
	public final static String ATTR_GENERATOR_MAX_QTY = "generator.max.order.quantity"; // ex. generator.max.order.quantity.MSFT=5000
																						//     generator.max.order.quantity.All=10000
	public final static String ATTR_GENERATOR_SYMBOLS = "generator.Symbols"; // ex. generator.Symbols=MSFT,IBM,AMZN,...
	public final static String ATTR_GENERATOR_SYMBOLS_MAP = "generator.SymbolsMap";
	public final static String ATTR_GENERATOR_ACCOUNTS = "generator.Accounts";
	public final static String ATTR_GENERATOR_STRATEGIES = "generator.Strategies";
	public final static String ATTR_GENERATOR_SYSTEMS = "generator.Systems";
	public final static String ATTR_GENERATOR_REGION_DETAILS = "generator.RegionDetails";
	public final static String ATTR_GENERATOR_EXCHANGES = "generator.Exchanges";

	final static String ATTR_GENERATOR_PARTITION_ID_LENGTH = "generator.PartitionIdLength";

	final static String ATTR_GENERATOR_EXCHANGE_SENDER_SESSSION_NAME = "generator.exchange.SenderSessionName";
	final static String ATTR_GENERATOR_CLIENT_SENDER_SESSSION_NAME = "generator.client.SenderSessionName";

	final static String ATTR_GENERATOR_EXCHANGE_ORDER_ACK_PENDING_PROBABILITY = "generator.exchange.OrderAckPendingProbabilityInPercent"; // unsupport.
	final static String ATTR_GENERATOR_EXCHANGE_CANCEL_PENDING_PROBABILITY = "generator.exchange.CancelPendingProbabilityInPercent";
	final static String ATTR_GENERATOR_EXCHANGE_CANCEL_REJECT_PROBABILITY = "generator.exchange.CancelRejectProbabilityInPercent";
	final static String ATTR_GENERATOR_EXCHANGE_MOD_PENDING_PROBABILITY = "generator.exchange.ModPendingProbabilityInPercent";
	final static String ATTR_GENERATOR_EXCHANGE_MOD_REJECT_PROBABILITY = "generator.exchange.ModRejectProbabilityInPercent";
	final static String ATTR_GENERATOR_EXCHANGE_MAX_PENDING_TO_ACK_TIME = "generator.exchange.MaxPendingToAckTimeInMilli";
	final static String ATTR_GENERATOR_EXCHANGE_MARKET_ORDER_FILL_DELAY = "generator.exchange.MarketOrderFillDelayInMilli";
	final static String ATTR_GENERATOR_EXCHANGE_FILL_PROBABILITY = "generator.exchange.FillProbabilityInPercent";
	final static String ATTR_GENERATOR_EXCHANGE_FILL_TO_PARTIAL_FILL_PROBABILTY = "generator.exchange.FillToPartialFillProbabiltyInPercent";
	final static String ATTR_GENERATOR_EXCHANGE_FILL_AT_LIMIT_PRICE_PROBABILTY = "generator.exchange.FillAtLimitPriceProbabiltyInPercent";
	final static String ATTR_GENERATOR_EXCHANGE_TRADE_BUST_PROBABILTY = "generator.exchange.TradeBustProbabiltyInPercent";
	final static String ATTR_GENERATOR_EXCHANGE_TRADE_CORRECT_PROBABILTY = "generator.exchange.TradeCorrectProbabiltyInPercent";

	final static int DEFAULT_MAX_SLEEP = 1000;
	final static int DEFAULT_MAX_NUMBER_OF_CLIENT = 100;
	public final static String DEFAULT_SYMBOL_LIST = "IBM,MSFT,MMM,PLTR,NIO";
	public final static String DEFAULT_SYMBOL_MAP_LIST = "IBM=US,MSFT=US,MMM=US,PLTR=US,NIO=US";
	public final static String DEFAULT_ACCOUNT_LIST = "Account1,Account2,Account3";
	public final static String DEFAULT_STRATEGY_LIST = "Strategy1,Strategy2,Strategy3";
	public final static String DEFAULT_SYSTEM_LIST = "Fidessa,Bloomberg,Interactive Brokers,Merryl Lynch,Reuters,Factset,FlexTrade,Fidelity";
	public final static String DEFAULT_REGION_LIST = "US=USA|USD|NA,CA=Canada|CAD|NA,UK=United Kingdom|GBP|EMEA,JP=Japan|JPY|APAC,HK=HK|HKU|APAC,NO=Norway|NOK|EMEA,SE=Sweden|SEK|EMEA,IN=India|INR|APAC";
	public final static String DEFAULT_EXCHANGE_LIST = "US=NYSE|ARCA|BATS|IEX|NASDAQ,CA=TSX|NEO,UK=LSE,EU=FSE|Euronext,HK=HKEX,NO=OSE,SE=SSE,IN=NSE|BSE";
	public final static int DEFAULT_MAX_PRICE_SWING = 10;
	public final static int DEFAULT_MAX_QTY = 10000;
	final static int DEFAULT_CLIENT_CANCEL_PROBABILITY = 5;
	final static int DEFAULT_CLIENT_MOD_PROBABILITY = 5;
	final static int DEFAULT_CLIENT_MOD_PRICE_PROBABILITY = 30;
	final static int DEFAULT_CLIENT_MARKET_ORDER_PROBABILITY = 5;

	final static int DEFAULT_ORDER_ACK_PENDING_PROBABILITY = 10;
	final static int DEFAULT_CANCEL_PENDING_PROBABILITY = 30;
	final static int DEFAULT_CANCEL_REJECT_PROBABILITY = 10;
	final static int DEFAULT_MOD_PENDING_PROBABILITY = 30;
	final static int DEFAULT_MOD_REJECT_PROBABILITY = 10;
	final static int DEFAULT_MAX_PENDING_TO_ACK_TIME = 100;
	final static int DEFAULT_MARKET_ORDER_FILL_DELAY = 50;
	final static int DEFAULT_FILL_PROBABILITY = 50;
	final static int DEFAULT_FILL_TO_PARTIAL_FILL_PROBABILITY = 20;
	final static int DEFAULT_FILL_AT_LIMIT_PRICE_PROBABILITY = 80;
	final static int DEFAULT_TRADE_BUST_PROBABILITY = 1;
	final static int DEFAULT_TRADE_CORRECT_PROBABILITY = 1;

	final static String DEFAULT_EXCHANGE_SENDER_SESSION_NAME = "exchangesim";
	final static String DEFAULT_CLIENT_SENDER_SESSION_NAME = "csim";

	volatile static DataDictionary DATA_DICTIONARY = null;

	public Map<String, String> symbolToRegion = new HashMap<String, String>();
	public Map<String, List<String>> regionToDetails = new HashMap<String, List<String>>();
	public Map<String, List<String>> regionToExchanges = new HashMap<String, List<String>>();

	//final static String[] ExchangesUS = { "NYSE", "ARCA", "BATS", "IEX", "NASDAQ" };
	//final static String[] ExchangesCA = { "TSX", "NEO" };
	//final static String[] ExchangesGB = { "LSE" };
	//final static String[] ExchangesEU = { "FSE", "Euronext" };
	//final static String[] ExchangesHK = { "HKEX" };
	//final static String[] ExchangesNO = { "OSE" };
	//final static String[] ExchangesSE = { "SSE" };
	//final static String[] ExchangesIN = { "NSE", "BSE" };

	// template message
	// NEW
	static final Map<Integer, String> NEW_ORDER = ToolUtils.createMap(11, "NF 0710/04032009", 54, "1", 38, "100", 55, "MSFT", 40, "1", 59, "0", 47, "A", 60, "20090403-18:11:47",
			21, "1", 207, "N", 58, "New order");

	// ACK_PENDING

	// ACK
	final static Map<Integer, String> NEW_ORDER_ACK = ToolUtils.createMap(55, "MSFT", 37, "NF 0710/04032009", 11, "NF 0710/04032009", 17, "0", 20, "0", 39, "0", 150, "0", 54, "1",
			38, "100", 40, "1", 31, "0", 32, "0", 14, "0", 6, "0", 151, "100", 60, "20090403-18:11:47", 58, "Order Ack", 47, "A", 30, "N");

	// Cancel Request
	final static Map<Integer, String> CANCEL_REQUEST = ToolUtils.createMap(41, "NF 0570/03252009", 37, "NF 0570/03252009", 11, "NF 0571/03252009", 54, "1", 38, "1000", 55, "PLTR",
			60, "20090325-15:08:50", 58, "Cancel Request");

	// Cancel Pending
	final static Map<Integer, String> CANCEL_PENDING = ToolUtils.createMap(55, "PLTR", 37, "NF 0570/03252009", 11, "NF 0571/03252009", 41, "NF 0570/03252009", 17, "0", 20, "0", 39,
			"6", 150, "6", 54, "1", 38, "1000", 31, "0", 32, "0", 14, "0", 6, "0", 151, "0", 60, "20090325-15:08:50", 58, "Cancel Pending", 207, "N", 30, "N");

	// Cancel Ack
	final static Map<Integer, String> CANCELLED = ToolUtils.createMap(55, "PLTR", 37, "NF 0570/03252009", 11, "NF 0571/03252009", 41, "NF 0570/03252009", 17, "0", 20, "0", 39, "4",
			150, "4", 54, "1", 38, "1000", 40, "2", 44, "28.4700", 59, "0", 31, "0", 32, "0", 14, "0", 6, "0", 151, "0", 60, "20090325-15:08:50", 58, "Cancelled", 30, "N", 47, "A",
			207, "N", 29, "1");

	// Cancel Reject (35=9) (434=1 for cancel reject, 434=2 for Mod reject).
	final static Map<Integer, String> CANCEL_REJECT = ToolUtils.createMap(37, "NF 0810/04032009", 11, "NF 0811/04032009", 41, "NF 0810/04032009", 39, "8", 434, "1", 58,
			"Cancel Reject");

	// Mod Request
	final static Map<Integer, String> REPLACE_REQUEST = ToolUtils.createMap(11, "NF 0574/03252009", 37, "NF 0573/03252009", 41, "NF 0573/03252009", 54, "1", 38, "2000", 55, "LI",
			40, "2", 59, "0", 60, "20090325-15:14:47", 21, "1", 58, "Replace Request");

	// Mod Pending (35=8)
	final static Map<Integer, String> REPLACE_PENDING = ToolUtils.createMap(55, "LI", 37, "NF 0574/03252009", 11, "NF 0574/03252009", 41, "NF 0573/03252009", 17, "0", 20, "0", 39,
			"E", 150, "E", 54, "1", 38, "2000", 40, "2", 31, "0", 32, "0", 14, "0", 6, "0", 151, "2000", 60, "20090325-15:14:48", 58, "Replace Pending", 207, "N", 30, "N");

	// Mod Ack (35=8)
	final static Map<Integer, String> REPLACED = ToolUtils.createMap(55, "LI", 37, "NF 0573/03252009", 11, "NF 0574/03252009", 41, "NF 0573/03252009", 17, "0", 20, "0", 39, "5",
			150, "5", 54, "1", 38, "2000", 40, "2", 44, "25.4700", 59, "0", 31, "0", 32, "0", 14, "0", 6, "0", 151, "2000", 60, "20090325-15:14:48", 58, "Replaced", 30, "N", 47,
			"A", 207, "N", 29, "1");

	// Partial Fill
	final static Map<Integer, String> PARTIAL_FILL = ToolUtils.createMap(55, "PAC", 37, "NF 0644/04022009", 11, "NF 0644/04022009", 17, "NF 0644/04022009001001001", 20, "0", 39,
			"1", 150, "1", 54, "1", 38, "500", 40, "1", 59, "0", 31, "17.00", 32, "100", 14, "100", 6, "17.00", 151, "400", 60, "20090402-18:11:47", 58, "Partial Fill", 30, "N",
			207, "N", 47, "A", 382, "1", 375, "TOD", 337, "0000", 437, "100", 438, "20090402-18:11:47", 29, "1");

	// Fill
	final static Map<Integer, String> FILL = ToolUtils.createMap(55, "MSFT", 37, "NF 0710/04032009", 11, "NF 0710/04032009", 17, "NF 0710/04032009001001001", 20, "0", 39, "2", 150,
			"2", 54, "1", 38, "100", 40, "1", 59, "0", 31, "243", 32, "100", 14, "100", 6, "243", 151, "0", 60, "20090403-18:20:06", 58, "Fill", 30, "N", 47, "A", 207, "N", 382,
			"1", 375, "BARC", 337, "0000", 437, "100", 438, "20090403-18:11:55", 29, "1");

	// Trade Correct
	final static Map<Integer, String> TRADE_CORRECT = ToolUtils.createMap(55, "MSFT", 37, "NF 0710/04032009", 11, "NF 0710/04032009", 17, "NF 0710/04032009001001002", 20, "2", 39,
			"2", 150, "2", 54, "1", 38, "100", 40, "1", 59, "0", 31, "0", 32, "0", 14, "100", 6, "243.77", 151, "0", 60, "20090402-18:20:28", 47, "A", 19,
			"NF 0710/04032009 001001001", 29, "1", 382, "1", 375, "BARC", 337, "0000", 437, "100", 438, "20090403-18:12:01", 207, "N", 30, "N", 58, "Trade Correct");

	// Trade Bust
	final static Map<Integer, String> TRADE_BUST = ToolUtils.createMap(55, "MSFT", 37, "NF 0710/04032009", 11, "NF 0710/04032009", 17, "NF 0710/04032009001001003", 20, "1", 39,
			"1", 150, "1", 54, "1", 38, "100", 40, "1", 59, "0", 31, "0", 32, "0", 14, "0", 6, "243.77", 151, "0", 60, "20090403-18:20:40", 382, "1", 375, "BARC", 337, "0000", 437,
			"100", 438, "20090403-18:12:06", 19, "NF0710/04032009 001001002", 47, "A", 29, "1", 207, "N", 30, "N", 58, "Trade bust");

	final int maxPendingToAckTime;

	public AbstractHandler(final PropertyController props) throws ConfigError {
		super(OrderEvent.class, OrderCache.class);
		maxPendingToAckTime = props.getOptional(ATTR_GENERATOR_EXCHANGE_MAX_PENDING_TO_ACK_TIME, DEFAULT_MAX_PENDING_TO_ACK_TIME);

		if (null == DATA_DICTIONARY) {
			synchronized (AbstractFix2AmiProcessor.class) {
				if (null == DATA_DICTIONARY) {
					final String dictionaryLocation = props.getOptional(ATTR_DATA_DICTIONARY);
					if (null != dictionaryLocation) {
						DATA_DICTIONARY = new DataDictionary(dictionaryLocation);
					} else {
						DATA_DICTIONARY = new DefaultDataDictionaryProvider().getSessionDataDictionary(props.getOptional(ATTR_DATA_DICTIONARY_VERSION, "FIX.4.2"));
					}
				}
			}
		}
	}

	static DataDictionary getDictionary() {
		return DATA_DICTIONARY;
	}

	public static Order randomOrderPick(final Random random, final OrderCache orderCache, final String symbol, boolean openOrderOnly) {
		List<Order> orders = null;
		orders = new ArrayList<>(orderCache.getOrders(symbol, OrderCache.ORDER_STATE.OPEN));
		LH.fine(log, "open order cache size is ", orders.size());

		if (null == orders || orders.isEmpty()) {
			if (openOrderOnly) {
				return null;
			}

			orders = new ArrayList<>(orderCache.getOrders(symbol, OrderCache.ORDER_STATE.NEW));
			if (orders.isEmpty()) {
				orders = new ArrayList<>(orderCache.getOrders(symbol, OrderCache.ORDER_STATE.CLOSE));
				LH.info(log, "close order cache size is ", orders.size());
			}
		}

		if (orders.isEmpty()) {
			return null;
		}

		return orders.get(random.nextInt(orders.size()));
	}

	private static void addCommonTag(Map<Integer, String> msgMap, final Order order, final String clOrdID) {
		msgMap.put(TAG_ClOrdID, clOrdID);

		final String orderID = order.getOrderID();
		if (null != orderID) {
			msgMap.put(AbstractHandler.TAG_OrderID, orderID);
		} else {
			msgMap.remove(AbstractHandler.TAG_OrderID);
		}

		msgMap.put(TAG_Symbol, order.getSymbol());

		msgMap.put(TAG_OrdType, String.valueOf(order.getOrdType()));

		msgMap.put(TAG_Side, String.valueOf(order.getSide()));
		msgMap.put(TAG_TransactTime, UtcTimestampConverter.convert(new Date(), true));
		if (order.getOrdType() == Order.LIMIT_ORDER) {
			msgMap.put(TAG_Price, String.valueOf(order.getPrice()));
		}
	}

	public static void addMiscForOrderNew(Map<Integer, String> msgMap, final Order order, final String clOrdID, final StockInfo stockInfo) {
		addCommonTag(msgMap, order, clOrdID);

		if (null != stockInfo) {
			msgMap.put(TAG_SecurityExchange, SECURITY_EXCHANGE_MNEMONIC.get(stockInfo.getExchange()));
		} else {
			msgMap.remove(TAG_SecurityExchange);
		}
	}

	public static void addMiscForRequest(Map<Integer, String> msgMap, final Order order, final String clOrdID, final StockInfo stockInfo) {
		addMiscForOrderNew(msgMap, order, clOrdID, stockInfo);
		msgMap.put(TAG_OrigClOrdID, order.getClOrdID());
	}

	public static void addMiscForAckResponse(Map<Integer, String> msgMap, final Order order, final String clOrdID, final String origClOrdID, final StockInfo stockInfo) {
		addCommonTag(msgMap, order, clOrdID);
		msgMap.put(TAG_OrigClOrdID, origClOrdID);
		msgMap.put(TAG_ExecID, order.getExecID());

		if (null != stockInfo) {
			msgMap.put(TAG_LastMkt, SECURITY_EXCHANGE_MNEMONIC.get(stockInfo.getExchange()));
		} else {
			msgMap.put(TAG_LastMkt, "O");
		}
	}

	public static void addMiscForResponse(final Map<Integer, String> msgMap, final Order order, final String origClOrdID, final StockInfo stockInfo) {
		// cmumQty(14) and avgPx(6)
		msgMap.put(TAG_CumQty, String.valueOf(order.getCumQty()));
		msgMap.put(TAG_AvgPx, String.valueOf(order.getAvgPx()));
		msgMap.put(TAG_Qty, String.valueOf(order.getQty()));
		msgMap.put(TAG_LeaveQty, String.valueOf(order.getLeaveQty()));

		if (order.getOrdType() == Order.LIMIT_ORDER) {
			msgMap.put(TAG_Price, String.valueOf(order.getPrice()));
		}

		addMiscForAckResponse(msgMap, order, order.getClOrdID(), origClOrdID, stockInfo);

		if (null != stockInfo) {
			msgMap.put(TAG_SecurityExchange, SECURITY_EXCHANGE_MNEMONIC.get(stockInfo.getExchange()));
		} else {
			msgMap.remove(TAG_SecurityExchange);
		}
	}

	public static void setContraBrokerInfo(final Order order, final Random random) {
		order.setNoContraBroker(1);
		order.setContraBroker(0, AbstractHandler.CONTRA_BROKER_LIST.get(random.nextInt(CONTRA_BROKER_LIST_SIZE)));
		order.setContraTrader(0, String.format("%4d", random.nextInt(9999)));
		order.setContraTradeQty(0, String.valueOf(order.getLastShare()));
		order.setContraTradeTime(0, UtcTimestampConverter.convert(new Date(), true));
	}

	public static double pickAPrice(double price, final StockInfo stockInfo, SIDE side, final Random random) {
		return pickAPrice(price, stockInfo, side, random, false);
	}

	public static double pickAPrice(double price, final StockInfo stockInfo, SIDE side, final Random random, int fillAtLimitPriceProbability) {
		if (random.nextInt(100) <= fillAtLimitPriceProbability) {
			return price;
		}
		return pickAPrice(price, stockInfo, side, random, true);
	}

	private static double pickAPrice(double price, final StockInfo stockInfo, SIDE side, final Random random, boolean betterPriceForFill) {
		double priceMultiplier = random.nextInt(100) / 100.0;
		if (null != stockInfo) {
			priceMultiplier *= stockInfo.getPriceSwing() / 100.0;
		}
		if (null != side) {
			if (betterPriceForFill) {
				// SELL at higher price or BUY at lower price (fill for pending limit order).
				priceMultiplier = (side == SIDE.SELL) ? 1 + priceMultiplier : 1 - priceMultiplier;
			} else {
				// SELL at lower price or BUY at higher price (LIMIT order by client).
				priceMultiplier = (side == SIDE.SELL) ? 1 - priceMultiplier : 1 + priceMultiplier;
			}
		} else {
			// random price change for client side replace request.
			priceMultiplier = random.nextBoolean() ? 1 + priceMultiplier : 1 - priceMultiplier;
		}
		return Math.round(price * priceMultiplier * 100.0) / 100.0;
	}

	public static void addMiscForFill(final Map<Integer, String> msgMap, final Order order, final String origClOrdID, final StockInfo stockInfo) {
		addMiscForResponse(msgMap, order, origClOrdID, stockInfo);
		msgMap.put(TAG_OrigClOrdID, origClOrdID);
		msgMap.put(TAG_LastPx, String.valueOf(order.getLastPx()));
		msgMap.put(TAG_LastShare, String.valueOf(order.getLastShare()));
		msgMap.put(TAG_CumQty, String.valueOf(order.getCumQty()));
		msgMap.put(TAG_AvgPx, String.valueOf(order.getAvgPx()));
		msgMap.put(TAG_HandlInst, order.getHandlInst());

		int noContraBroker = order.getNoContraBroker();
		if (noContraBroker > 0) {
			msgMap.put(TAG_NoContraBroker, String.valueOf(noContraBroker));
			String tmp;
			for (int i = 0; i < noContraBroker; i++) {
				tmp = order.getContraBroker(i);
				if (null != tmp) {
					msgMap.put(TAG_ContraBroker, tmp);
				}
				tmp = order.getContraTrader(i);
				if (null != tmp) {
					msgMap.put(TAG_ContraTrader, tmp);
				}
				tmp = order.getContraTradeQty(i);
				if (null != tmp) {
					msgMap.put(TAG_ContraTradeQty, tmp);
				}
				tmp = order.getContraTradeTime(i);
				if (null != tmp) {
					msgMap.put(TAG_ContraTradeTime, tmp);
				}
			}
		} else {
			msgMap.remove(TAG_NoContraBroker);
			msgMap.remove(TAG_ContraBroker);
			msgMap.remove(TAG_ContraTrader);
			msgMap.remove(TAG_ContraTradeQty);
			msgMap.remove(TAG_ContraTradeTime);

		}
	}

	public static void addMiscForCancelReject(final Map<Integer, String> msgMap, final Order order, final String clOrdID, final String text) {
		msgMap.put(TAG_OrderID, order.getOrderID());
		msgMap.put(TAG_ClOrdID, clOrdID);
		msgMap.put(TAG_OrigClOrdID, order.getOrigClOrdID());
		msgMap.put(TAG_Text, text);
	}

	// Ignore the order quantity for simplicity sake.  Just try to match by the price.
	// Otherwise, we run into matching multiple order due to different order quantity as well as partial fill of an existing order.
	// Simulator has a random logic to do partial fill.
	public static Order matchOrder(final Order order, final OrderCache orderCache, ORDER_STATE orderState, SIDE currentOrderSide) {
		List<Order> matchedOrder = new ArrayList<>();

		List<Order> orders = orderCache.getOrderedList(order.getSymbol(), ORDER_STATE.OPEN, currentOrderSide == SIDE.BUY ? SIDE.SELL : SIDE.BUY);

		if (order.getOrdType() == Order.MARKET_ORDER) {
			// if book depth is zero, return null; otherwise return the top of book as counter order.
			if (orders.isEmpty()) {
				return null;
			}
			for (int i = 0; i < orders.size(); i++) {

				matchedOrder.add(orders.get(i));
			}
			return orders.get(0);
		}

		// Limit Order
		if (currentOrderSide == SIDE.BUY) {
			if (order.getPrice() >= orders.get(0).getPrice()) {
				return orders.get(0);
			}
		} else {
			if (order.getPrice() <= orders.get(0).getPrice()) {
				return orders.get(0);
			}
		}
		return null;
	}

	static OrderEvent creatFillEvent(final Order order, final Random random, final OrderCache orderCache, final String partitionId, int fillAtLimitPriceProbability)
			throws ParseException, FieldConvertError {

		StockInfo stockInfo = StockInfo.getStockInfo(order.getSymbol());
		double price = order.getPrice();
		if (order.getOrdType() == Order.MARKET_ORDER) {
			if (null == stockInfo) {
				price = 100;
			} else {
				price = order.getSideInEnum() == SIDE.SELL ? stockInfo.getBid() : stockInfo.getAsk();
			}
		}
		double lastPx = pickAPrice(price, stockInfo, order.getSideInEnum(), random, fillAtLimitPriceProbability);

		order.setLastPxAndShare(lastPx, order.getLeaveQty());
		setContraBrokerInfo(order, random);
		orderCache.addOrder(order.getSymbol(), ORDER_STATE.OPEN, ORDER_STATE.CLOSE, order);
		Map<Integer, String> msgMap = new LinkedHashMap<>(AbstractHandler.FILL);
		addMiscForFill(msgMap, order, order.getOrigClOrdID() == null ? order.getClOrdID() : order.getOrigClOrdID(), stockInfo);

		quickfix.Message msg = ToolUtils.buildMessage(DATA_DICTIONARY, (Map) CH.m(TAG_MsgType, MsgType.EXECUTION_REPORT.getEnumValue().toString()), msgMap);
		OrderEvent orderEvent = AbstractEventGenerator.createEvent(MsgType.EXECUTION_REPORT, partitionId, order.getSymbol());
		orderEvent.setFIXMessage(msg);
		return orderEvent;
	}

	@Override
	public abstract void processAction(OrderEvent action, OrderCache state, ThreadScope threadScope) throws Exception;

	static final Map<String, String> CONTRA_BROKERS = new HashMap<>();
	static final List<String> CONTRA_BROKER_LIST = new ArrayList<>();
	static final int CONTRA_BROKER_LIST_SIZE;

	static {
		CONTRA_BROKERS.put("AA", "Bid and offer are CAES aggregates");
		CONTRA_BROKERS.put("AI", "Bid is CAES aggregate; offer is non-CAES or exchange aggregate");
		CONTRA_BROKERS.put("CAES", "NASD Market Services, Inc");
		CONTRA_BROKERS.put("DEAN", "Dean Witter Reynolds Inc");
		CONTRA_BROKERS.put("FAHN", "Fahnestock & Co., Inc");
		CONTRA_BROKERS.put("IA", "Bid is non-CAES or exchange aggregate; offer is CAES aggregat");
		CONTRA_BROKERS.put("II", "Bid and offer are non-CAES or exchange aggregates");
		CONTRA_BROKERS.put("NASD", "End of day quote for that issue");
		CONTRA_BROKERS.put("RHCO", "Robinson-Humphrey Company Inc");
		CONTRA_BROKERS.put("SHAW", "Shaw (D.E.) Securities");
		CONTRA_BROKERS.put("TRIM", "Trimark Securities, Inc");

		CONTRA_BROKER_LIST.addAll(CONTRA_BROKERS.keySet());
		CONTRA_BROKER_LIST_SIZE = CONTRA_BROKER_LIST.size();
	}

	static final Map<String, String> SECURITY_EXCHANGE_MNEMONIC = new HashMap<>();
	static {
		SECURITY_EXCHANGE_MNEMONIC.put("NasdaqGS", "O");
		SECURITY_EXCHANGE_MNEMONIC.put("NYSE", "N");
	}
}
