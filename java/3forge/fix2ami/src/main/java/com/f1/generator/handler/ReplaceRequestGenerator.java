package com.f1.generator.handler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.container.ThreadScope;
import com.f1.fix2ami.tool.ToolUtils;
import com.f1.generator.Order;
import com.f1.generator.OrderCache;
import com.f1.generator.OrderCache.ORDER_STATE;
import com.f1.generator.OrderEvent;
import com.f1.generator.StockInfo;
import com.f1.pofo.fix.MsgType;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;

import quickfix.ConfigError;

public class ReplaceRequestGenerator extends AbstractHandler {
	private static final Logger log = Logger.getLogger(ReplaceRequestGenerator.class.getName());

	private final int modPriceProbability;

	public ReplaceRequestGenerator(final PropertyController props) throws ConfigError {
		super(props);
		modPriceProbability = props.getOptional(ATTR_GENERATOR_CLIENT_MOD_PRICE_PROBABILITY, DEFAULT_CLIENT_MOD_PRICE_PROBABILITY);
	}

	@Override
	public void processAction(OrderEvent event, OrderCache orderCache, ThreadScope threadScope) throws Exception {
		LH.info(log, "generate a MOD REQUEST (CLIENT) -> ", event);
		Map<Integer, String> msgMap = new LinkedHashMap<>(AbstractHandler.REPLACE_REQUEST);

		final String symbol = event.getSymbol();

		Order order = randomOrderPick(random, orderCache, symbol, true);
		if (null == order) {
			LH.info(log, "There is no possible replace request candidate (skip replace request).");
			return;
		}

		long leaveQty = order.getLeaveQty();
		if (leaveQty == 0 || order.getOrderState() == ORDER_STATE.CLOSE) {
			LH.info(log, "order is filled (skip replace request)");
			orderCache.addOrder(order.getSymbol(), ORDER_STATE.OPEN, ORDER_STATE.CLOSE, order);
			return;
		}

		StockInfo stockInfo = StockInfo.getStockInfo(symbol);

		final char ordType = order.getOrdType();
		double price = order.getPrice();
		if (ordType == Order.LIMIT_ORDER && modPriceProbability >= random.nextInt(100)) {
			//mod price
			price = pickAPrice(order.getPrice(), stockInfo, null, random);
			msgMap.put(TAG_Text, "MOD price to " + price);
			msgMap.put(TAG_Qty, String.valueOf(order.getQty()));
		} else {
			// mod qty
			double randomFactor = random.nextDouble();
			long qty = order.getQty();

			//if (null != stockInfo) {
			//	if (order.getSideInEnum() == SIDE.SELL) {
			//		qty = Math.max(leaveQty, stockInfo.getAskSize());
			//	} else {
			//		qty = Math.max(leaveQty, stockInfo.getBidSize());
			//	}
			//}

			//if (random.nextBoolean()) {
			//	qty *= (1 + randomFactor);
			//} else {
			//	qty *= randomFactor;
			//}

			//if (qty > 100) {
			//	qty %= 100;
			//}
			qty *= (1 + randomFactor);

			msgMap.put(TAG_Qty, String.valueOf(qty));
			msgMap.put(TAG_Text, "MOD qty to " + qty);
		}

		// update the specific fields
		//addMiscForRequest(msgMap, order, Order.createID(Order.CLIENT_PREFIX, order.getClient(), OrderCache.getOrderSequence(SEQUENCE_TYPE.CLIENT, order.getClient())), stockInfo);
		addMiscForRequest(msgMap, order, order.getClOrdID(), stockInfo);

		if (ordType == Order.LIMIT_ORDER) {
			msgMap.put(TAG_Price, String.valueOf(price));
		}
		msgMap.put(TAG_HandlInst, order.getHandlInst());

		quickfix.Message msg = ToolUtils.buildMessage(DATA_DICTIONARY, (Map) CH.m(TAG_MsgType, event.getMsgType().getEnumValue().toString()), msgMap);
		event.setFIXMessage(msg);
		fixMsgPort.send(event, threadScope);

		order.setLastMsgType(MsgType.REPLACE_REQUEST);
		orderCache.addOrder(symbol, null, OrderCache.ORDER_STATE.OPEN, order);
	}

}
