package com.f1.generator.handler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.container.ThreadScope;
import com.f1.fix2ami.tool.ToolUtils;
import com.f1.generator.Order;
import com.f1.generator.Order.SIDE;
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

public class NewOrderHandler extends AbstractHandler {
	private static final Logger log = Logger.getLogger(NewOrderHandler.class.getName());

	//	private final int orderAckPendingProbability;
	private final int maxMarketOrderFillDelay;

	public NewOrderHandler(final PropertyController props) throws ConfigError {
		super(props);
		//		orderAckPendingProbability = props.getOptional(ATTR_GENERATOR_EXCHANGE_ORDER_ACK_PENDING_PROBABILITY, DEFAULT_ORDER_ACK_PENDING_PROBABILITY);
		maxMarketOrderFillDelay = props.getOptional(ATTR_GENERATOR_EXCHANGE_MARKET_ORDER_FILL_DELAY, DEFAULT_MARKET_ORDER_FILL_DELAY);
	}

	@Override
	public void processAction(OrderEvent event, OrderCache orderCache, ThreadScope threadScope) throws Exception {
		LH.info(log, "received a NEW ORDER (EXCHANGE) ->   ", event);

		//		if(orderAckPendingProbability <= random.nextInt(100)) {
		//			// order ack pending.
		//			Map<Integer, String> msgMap = new LinkedHashMap<>(AbstractHandler.NEW_ORDER_PENDING);
		//		}

		// order ack.
		Map<Integer, String> msgMap = new LinkedHashMap<>(AbstractHandler.NEW_ORDER_ACK);

		final String clOrdID = event.getClOrdID();
		Order order = orderCache.getOrder(clOrdID);
		order.setOrigClOrdID(clOrdID);

		// ExecID(17)
		int uniqueId = OrderCache.getOrderSequence(SEQUENCE_TYPE.EXCHANGE, 1);
		final String execID = Order.createID("ExecACK", uniqueId);
		order.setExecID(execID);

		// OrderID(37)
		if (null == order.getOrderID()) {
			final String orderID = Order.createID(Order.EXCHANGE_PREFIX, uniqueId);
			order.setOrderID(orderID);
		}

		final StockInfo stockInfo = StockInfo.getStockInfo(order.getSymbol());
		addMiscForAckResponse(msgMap, order, clOrdID, clOrdID, stockInfo);

		msgMap.put(TAG_Qty, String.valueOf(order.getQty()));
		msgMap.put(TAG_LeaveQty, String.valueOf(order.getLeaveQty()));
		msgMap.put(TAG_HandlInst, order.getHandlInst());

		quickfix.Message msg = ToolUtils.buildMessage(DATA_DICTIONARY, (Map) CH.m(TAG_MsgType, quickfix.field.MsgType.EXECUTION_REPORT), msgMap);
		event.setFIXMessage(msg);

		fixMsgPort.send(event, threadScope);

		if (order.getOrdType() == Order.MARKET_ORDER) {
			// do a fill.
			order.setExecID(Order.createID("ExecMKT", OrderCache.getOrderSequence(SEQUENCE_TYPE.EXCHANGE, 1)));
			try {
				Thread.sleep(random.nextInt(maxMarketOrderFillDelay));
			} catch (Exception e) {
			}

			msgMap = new LinkedHashMap<>(AbstractHandler.FILL);

			// try price match one of the pending order - there is bug sending duplicate fill message (ran out of time to debug it).
			//			Order matchedOrder = matchOrder(order, orderCache, ORDER_STATE.OPEN, order.getSideInEnum());

			//			Map<Integer, String> matchedMsgMap = null;
			double lastPx = -1;
			//			boolean internalCrossed = true;
			//			if (null != matchedOrder && matchedOrder.getLeaveQty() > 0 && order != matchedOrder && !matchedOrder.getOrderID().equals(order.getOrderID())) {
			//				matchedMsgMap = new LinkedHashMap<>(AbstractHandler.FILL);
			//				matchedOrder.setNoContraBroker(0);
			//				lastPx = matchedOrder.getPrice();
			//				matchedOrder.setLastPxAndShare(lastPx, matchedOrder.getLeaveQty());
			//				matchedOrder.setExecID(Order.createID(null, OrderCache.getOrderSequence(SEQUENCE_TYPE.EXCHANGE, 1)));
			//
			//				addMiscForFill(matchedMsgMap, matchedOrder, matchedOrder.getOrigClOrdID(), stockInfo);
			//				orderCache.addOrder(order.getSymbol(), ORDER_STATE.OPEN, ORDER_STATE.CLOSE, matchedOrder);
			//			} else {
			if (order.getSideInEnum() == SIDE.SELL) {
				lastPx = stockInfo.getBid();
			} else {
				lastPx = stockInfo.getAsk();
			}

			//				internalCrossed = false;
			//			}

			// fill by market maker.
			// LastPx(31) LastShare(32), CumQty(14), AvgPx(6),
			order.setLastPxAndShare(lastPx, order.getQty());
			//			if (!internalCrossed) {
			// NoContraBroker(382), ContraBroker(375) (random pick from CONTRA_BROKER_LIST), ContraTrader(337) (random generate 4 digit),
			//   ContraTradeQty(437) (same as CumQTy), ContraTradeTime(438) (current UTCTimeStamp)
			setContraBrokerInfo(order, random);
			//			}
			addMiscForFill(msgMap, order, order.getClOrdID(), stockInfo);

			msg = ToolUtils.buildMessage(DATA_DICTIONARY, (Map) CH.m(TAG_MsgType, MsgType.EXECUTION_REPORT.getEnumValue().toString()), msgMap);
			OrderEvent orderEvent = AbstractEventGenerator.createEvent(MsgType.EXECUTION_REPORT, event.getPartitionId(), order.getSymbol());
			orderEvent.setFIXMessage(msg);
			fixMsgPort.send(orderEvent, threadScope);

			//			if (null != matchedMsgMap) {
			//				quickfix.Message matchedMsg = ToolUtils.buildMessage(DATA_DICTIONARY, (Map) CH.m(TAG_MsgType, MsgType.EXECUTION_REPORT.getEnumValue().toString()), msgMap);
			//				OrderEvent matchedOrderEvent = AbstractEventGenerator.createEvent(MsgType.EXECUTION_REPORT, event.getPartitionId(), order.getSymbol());
			//				matchedOrderEvent.setFIXMessage(matchedMsg);
			//				fixMsgPort.send(matchedOrderEvent, threadScope);
			//			}

			orderCache.addOrder(order.getSymbol(), ORDER_STATE.NEW, ORDER_STATE.CLOSE, order);
			return;
		}
		orderCache.addOrder(order.getSymbol(), ORDER_STATE.NEW, ORDER_STATE.OPEN, order);
	}

}
