package com.f1.generator.handler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.container.ThreadScope;
import com.f1.fix2ami.tool.ToolUtils;
import com.f1.generator.Order;
import com.f1.generator.OrderCache;
import com.f1.generator.OrderCache.ORDER_STATE;
import com.f1.generator.OrderCache.SEQUENCE_TYPE;
import com.f1.generator.OrderEvent;
import com.f1.generator.StockInfo;
import com.f1.pofo.fix.MsgType;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;

import quickfix.ConfigError;

public class FillGenerator extends AbstractHandler {
	private static final Logger log = Logger.getLogger(FillGenerator.class.getName());

	private final int fillToPartialFillProbability;
	private final int fillAtLimitPriceProbability;
	private final int tradeBustProbability;
	private final int tradeCorrectProbability;

	public FillGenerator(final PropertyController props) throws ConfigError {
		super(props);
		fillToPartialFillProbability = props.getOptional(AbstractHandler.ATTR_GENERATOR_EXCHANGE_FILL_TO_PARTIAL_FILL_PROBABILTY, DEFAULT_FILL_TO_PARTIAL_FILL_PROBABILITY);
		fillAtLimitPriceProbability = props.getOptional(AbstractHandler.ATTR_GENERATOR_EXCHANGE_FILL_AT_LIMIT_PRICE_PROBABILTY, DEFAULT_FILL_AT_LIMIT_PRICE_PROBABILITY);
		tradeBustProbability = props.getOptional(AbstractHandler.ATTR_GENERATOR_EXCHANGE_TRADE_BUST_PROBABILTY, DEFAULT_TRADE_BUST_PROBABILITY);
		tradeCorrectProbability = props.getOptional(AbstractHandler.ATTR_GENERATOR_EXCHANGE_TRADE_CORRECT_PROBABILTY, DEFAULT_TRADE_CORRECT_PROBABILITY);
	}

	@Override
	public void processAction(OrderEvent event, OrderCache orderCache, ThreadScope threadScope) throws Exception {
		LH.info(log, "Got FILL Generation event ->   ", event);

		Order order = randomOrderPick(random, orderCache, event.getSymbol(), true);
		if (null == order) {
			LH.info(log, "Generarte Fill or PARTIAL FILL (EXCHANGE) - no OPEN order.", event);
			return;
		}

		if (order.getLeaveQty() == 0 || order.getOrderState() == ORDER_STATE.CLOSE) {
			LH.info(log, "order is already filled.  Skip fill processing.");
			orderCache.addOrder(order.getSymbol(), ORDER_STATE.OPEN, ORDER_STATE.CLOSE, order);
			return;
		}

		// OrigClOrdID(41)
		if (null == order.getOrigClOrdID()) {
			order.setOrigClOrdID(order.getClOrdID());
		}

		// ExecID(17)
		int uniqueId = OrderCache.getOrderSequence(SEQUENCE_TYPE.EXCHANGE, 1);
		final String execID = Order.createID("ExecFPF", uniqueId);
		order.setExecID(execID);

		// OrderID(37)
		if (null == order.getOrderID()) {
			final String orderID = Order.createID(Order.EXCHANGE_PREFIX, uniqueId);
			order.setOrderID(orderID);
		}

		LH.info(log, "Genearte Fill or PARTIAL FILL (EXCHANGE) - working on a fill/partial fill.", event);
		Map<Integer, String> msgMap = null;
		StockInfo stockInfo = StockInfo.getStockInfo(order.getSymbol());
		double lastPx = pickAPrice(order.getPrice(), stockInfo, order.getSideInEnum(), random, fillAtLimitPriceProbability);

		// keep a copy of previous cumQty and avgPx in case for Trade Correct.
		long lastShare = order.getLeaveQty();

		if (random.nextInt(100) <= fillToPartialFillProbability) {
			// Fill	
			order.setLastPxAndShare(lastPx, lastShare);
			orderCache.addOrder(order.getSymbol(), ORDER_STATE.OPEN, ORDER_STATE.CLOSE, order);
			msgMap = new LinkedHashMap<>(AbstractHandler.FILL);
		} else {
			//partial fill.
			lastShare = order.getLeaveQty() / 4;
			lastShare = lastShare > 100 ? lastShare / 100 * 100 : lastShare;
			order.setLastPxAndShare(lastPx, lastShare);
			msgMap = new LinkedHashMap<>(AbstractHandler.PARTIAL_FILL);
		}

		setContraBrokerInfo(order, random);
		addMiscForFill(msgMap, order, order.getClOrdID(), stockInfo);

		quickfix.Message msg = ToolUtils.buildMessage(DATA_DICTIONARY, (Map) CH.m(TAG_MsgType, MsgType.EXECUTION_REPORT.getEnumValue().toString()), msgMap);
		event.setFIXMessage(msg);
		order.setFixMsg(msg);
		fixMsgPort.send(event, threadScope);

		// random check for trade bust and trade correct.
		if (random.nextInt(100) < tradeCorrectProbability && lastShare != order.getQty()) {
			// trade correct (back out the last fill or partial fill).
			// for the case of fill, the trade correct will change the accordingly; however, 
			// the order will not be worked (just try to keep it simple - no moving from CLOSE to OPEN).
			msgMap = new LinkedHashMap<>(AbstractHandler.TRADE_CORRECT);

		} else if (random.nextInt(100) < tradeBustProbability) {
			// trade bust.
			msgMap = new LinkedHashMap<>(AbstractHandler.TRADE_BUST);
		} else {
			return;
		}

		// ExecID(17)
		int newId = OrderCache.getOrderSequence(SEQUENCE_TYPE.EXCHANGE, 1);
		final String newExecID = Order.createID("Exec17E", newId);
		order.setExecID(newExecID);
		order.setLastPxAndShare(lastPx, -1 * lastShare);

		msgMap.put(TAG_ExecRefID, execID);
		addMiscForFill(msgMap, order, order.getClOrdID(), stockInfo);

		msg = ToolUtils.buildMessage(DATA_DICTIONARY, (Map) CH.m(TAG_MsgType, MsgType.EXECUTION_REPORT.getEnumValue().toString()), msgMap);
		OrderEvent orderEvent = AbstractEventGenerator.createEvent(MsgType.EXECUTION_REPORT, event.getPartitionId(), order.getSymbol());
		orderEvent.setFIXMessage(msg);
		fixMsgPort.send(orderEvent, threadScope);
	}

}
