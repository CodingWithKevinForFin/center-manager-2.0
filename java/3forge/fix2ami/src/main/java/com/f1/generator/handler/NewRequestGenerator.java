package com.f1.generator.handler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.container.ThreadScope;
import com.f1.fix2ami.tool.ToolUtils;
import com.f1.generator.Order;
import com.f1.generator.Order.SIDE;
import com.f1.generator.OrderCache;
import com.f1.generator.OrderCache.SEQUENCE_TYPE;
import com.f1.generator.OrderEvent;
import com.f1.generator.StockInfo;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;

import quickfix.ConfigError;

public class NewRequestGenerator extends AbstractHandler {
	private static final Logger log = Logger.getLogger(NewRequestGenerator.class.getName());

	private final int marketOrderProbility;
	private final int numberOfClient;
	private final String[] Accounts;
	private final String[] Strategies;
	private final String[] Systems;

	public NewRequestGenerator(final PropertyController props) throws ConfigError {
		super(props);
		marketOrderProbility = props.getOptional(ATTR_GENERATOR_CLIENT_MARKET_ORDER_PROBABILITY, DEFAULT_CLIENT_MARKET_ORDER_PROBABILITY);
		numberOfClient = props.getOptional(ATTR_GENERATOR_MAX_CLIENT, DEFAULT_MAX_NUMBER_OF_CLIENT);
		Accounts = SH.split(',', props.getOptional(ATTR_GENERATOR_ACCOUNTS, DEFAULT_ACCOUNT_LIST));
		Strategies = SH.split(',', props.getOptional(ATTR_GENERATOR_STRATEGIES, DEFAULT_STRATEGY_LIST));
		Systems = SH.split(',', props.getOptional(ATTR_GENERATOR_SYSTEMS, DEFAULT_SYSTEM_LIST));
		symbolToRegion = SH.splitToMap(',', '=', props.getOptional(ATTR_GENERATOR_SYMBOLS_MAP, DEFAULT_SYMBOL_MAP_LIST));
		Map<String, String> tmpRegionMap = new HashMap<String, String>();
		tmpRegionMap = SH.splitToMap(',', '=', props.getOptional(ATTR_GENERATOR_REGION_DETAILS, DEFAULT_REGION_LIST));
		for (Map.Entry<String, String> entry : tmpRegionMap.entrySet()) {
			regionToDetails.put(entry.getKey(), SH.splitToList("|", entry.getValue()));
		}
		Map<String, String> tmpExchangeMap = SH.splitToMap(',', '=', props.getOptional(ATTR_GENERATOR_EXCHANGES, DEFAULT_EXCHANGE_LIST));
		for (Map.Entry<String, String> entry : tmpExchangeMap.entrySet()) {
			regionToExchanges.put(entry.getKey(), SH.splitToList("|", entry.getValue()));
		}
	}

	@Override
	public void processAction(OrderEvent event, OrderCache orderCache, ThreadScope threadScope) throws Exception {
		LH.info(log, "generate a NEW ORDER (CLIENT) ->   ", event);
		Map<Integer, String> msgMap = new LinkedHashMap<>(AbstractHandler.NEW_ORDER);

		final int client = random.nextInt(numberOfClient);

		// ClOrdID
		final String clOrdID = Order.createID(Order.CLIENT_PREFIX, client, OrderCache.getOrderSequence(SEQUENCE_TYPE.CLIENT, client));

		// Symbol(55)
		final String symbol = event.getSymbol();

		final StockInfo stockInfo = StockInfo.getStockInfo().get(symbol);
		if (null == stockInfo) {
			LH.info(log, "internal error (stock info does not exist for ", symbol);
			return;
		}

		Order order = new Order(client, symbol, clOrdID, stockInfo.getSecurityExchangeMnemonic());

		// HandlInst(21) value 1 or 2
		order.setHandlInst(random.nextBoolean() ? "1" : "2");

		// Side(54)
		order.setSide(random.nextBoolean() ? SIDE.BUY : SIDE.SELL);

		// OrdType(40)  value 1-market 2-limit
		final char ordType = random.nextInt(100) < marketOrderProbility ? Order.MARKET_ORDER : Order.LIMIT_ORDER;
		order.setOrdType(ordType);
		if (ordType == Order.LIMIT_ORDER) {
			// Price(44) for limit order.
			double price = pickAPrice(stockInfo.getMarketPrice(), stockInfo, order.getSideInEnum(), random);
			//			msgMap.put(TAG_Price, String.valueOf(price));
			order.setPrice(price);
		}

		// Qty(38)
		long qty = random.nextInt(stockInfo.getMaxQty());
		if (qty > 100) {
			qty -= qty % 100;
		}
		msgMap.put(TAG_Qty, String.valueOf(qty));
		order.setQty(qty);

		addMiscForOrderNew(msgMap, order, clOrdID, StockInfo.getStockInfo(symbol));

		final String handlInst = order.getHandlInst();
		if (null != handlInst) {
			msgMap.put(TAG_HandlInst, handlInst);
		} else {
			msgMap.put(TAG_HandlInst, "1");
			order.setHandlInst("1");
		}

		msgMap.put(TAG_Account, Accounts[random.nextInt(Accounts.length)]);
		List<String> r2D = regionToDetails.get(symbolToRegion.get(symbol));
		msgMap.put(TAG_SecurityDesc, r2D.get(0));
		msgMap.put(TAG_Currency, r2D.get(1));
		msgMap.put(TAG_QuoteID, r2D.get(2));
		msgMap.put(TAG_ComplianceID, Strategies[random.nextInt(Strategies.length)]);
		msgMap.put(TAG_ClearingFirm, Systems[random.nextInt(Systems.length)]);
		List<String> exchanges = regionToExchanges.get(symbolToRegion.get(symbol));
		msgMap.put(TAG_ClearingAccount, exchanges.get(random.nextInt(exchanges.size())));
		//final String execBroker = order.getSecurityExchangeMnemonic();
		//msgMap.put(TAG_ExecBroker, execBroker);

		// TimeInForce(59) value 0-Day (only support day order which is default in FIX message).
		quickfix.Message msg = ToolUtils.buildMessage(DATA_DICTIONARY, (Map) CH.m(TAG_MsgType, event.getMsgType().getEnumValue().toString()), msgMap);
		event.setFIXMessage(msg);
		order.setFixMsg(msg);
		orderCache.addOrder(symbol, null, OrderCache.ORDER_STATE.NEW, order);
		fixMsgPort.send(event, threadScope);
	}

}
