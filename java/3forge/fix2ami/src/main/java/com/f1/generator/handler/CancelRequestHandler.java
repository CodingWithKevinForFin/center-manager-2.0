package com.f1.generator.handler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.container.ThreadScope;
import com.f1.fix2ami.processor.AbstractFix2AmiProcessor;
import com.f1.fix2ami.tool.ToolUtils;
import com.f1.generator.Order;
import com.f1.generator.OrderCache;
import com.f1.generator.OrderCache.ORDER_STATE;
import com.f1.generator.OrderCache.SEQUENCE_TYPE;
import com.f1.generator.OrderEvent;
import com.f1.generator.StockInfo;
import com.f1.pofo.fix.MsgType;
import com.f1.pofo.fix.OrdStatus;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;

import quickfix.ConfigError;

public class CancelRequestHandler extends AbstractHandler {
	private static final Logger log = Logger.getLogger(CancelRequestHandler.class.getName());

	private final int cancelPendingProbability;
	private final int cancelRejectProbability;
	private final int fillAtLimitPriceProbability;

	public CancelRequestHandler(final PropertyController props) throws ConfigError {
		super(props);
		cancelPendingProbability = props.getOptional(ATTR_GENERATOR_EXCHANGE_CANCEL_PENDING_PROBABILITY, DEFAULT_CANCEL_PENDING_PROBABILITY);
		cancelRejectProbability = props.getOptional(ATTR_GENERATOR_EXCHANGE_CANCEL_REJECT_PROBABILITY, DEFAULT_CANCEL_REJECT_PROBABILITY);
		fillAtLimitPriceProbability = props.getOptional(AbstractHandler.ATTR_GENERATOR_EXCHANGE_FILL_AT_LIMIT_PRICE_PROBABILTY, DEFAULT_FILL_AT_LIMIT_PRICE_PROBABILITY);
	}

	@Override
	public void processAction(OrderEvent event, OrderCache orderCache, ThreadScope threadScope) throws Exception {
		LH.info(log, "received a CANCEL REQUEST (EXCHANGE) -> ", event);

		final String origClOrdID = AbstractFix2AmiProcessor.getTagValue(DATA_DICTIONARY, event.getFIXMessage(), TAG_OrigClOrdID);
		Order order = orderCache.getOrder(origClOrdID);
		if (null == order) {
			// should never happen.
			LH.warning(log, "internal error - order is not in orderCache for clOrdID: ", event.getClOrdID());
			return;
		}

		boolean cancelReject = false;
		String text = null;
		if (order.getOrderState() != ORDER_STATE.OPEN
				|| (order.getLastOrdStatus() != OrdStatus.PENDING_ACK && order.getLastOrdStatus() != OrdStatus.ACKED && order.getCumQty() > 0)) {
			text = "Order already being worked on";
			cancelReject = true;
		}

		// ExecID(17)
		int uniqueId = OrderCache.getOrderSequence(SEQUENCE_TYPE.EXCHANGE, 1);
		final String execID = Order.createID("ExecCRE", uniqueId);
		order.setExecID(execID);

		// OrderID(37)
		if (null == order.getOrderID()) {
			final String orderID = Order.createID(Order.EXCHANGE_PREFIX, uniqueId);
			order.setOrderID(orderID);
		}

		// OrigClOrdID(41)
		order.setOrigClOrdID(origClOrdID);

		if (!cancelReject && cancelRejectProbability <= random.nextInt(100)) {
			//  do a fully filled.
			fixMsgPort.send(creatFillEvent(order, random, orderCache, event.getPartitionId(), fillAtLimitPriceProbability), threadScope);

			cancelReject = true;
			order.setExecID(Order.createID("ExecALF", OrderCache.getOrderSequence(SEQUENCE_TYPE.EXCHANGE, 1)));
			text = "Order has already been filled";
		}

		order.setClOrdID(event.getClOrdID());

		if (cancelReject) {
			Map<Integer, String> msgMap = new LinkedHashMap<>(AbstractHandler.CANCEL_REJECT);

			msgMap.put(TAG_CxlRejResponseTo, "1"); // cancel reject.
			//addMiscForCancelReject(msgMap, order, event.getClOrdID(), text);
			addMiscForCancelReject(msgMap, order, origClOrdID, text);

			quickfix.Message msg = ToolUtils.buildMessage(DATA_DICTIONARY, (Map) CH.m(TAG_MsgType, quickfix.field.MsgType.ORDER_CANCEL_REJECT), msgMap);
			event.setFIXMessage(msg);
			fixMsgPort.send(event, threadScope);
			return;
		}
		final StockInfo stockInfo = StockInfo.getStockInfo(event.getSymbol());

		if (cancelPendingProbability <= random.nextInt(100)) {
			// cancel pending.
			Map<Integer, String> msgMap = new LinkedHashMap<>(AbstractHandler.CANCEL_PENDING);
			addMiscForResponse(msgMap, order, origClOrdID, stockInfo);

			quickfix.Message msg = ToolUtils.buildMessage(DATA_DICTIONARY, (Map) CH.m(TAG_MsgType, quickfix.field.MsgType.EXECUTION_REPORT), msgMap);
			OrderEvent orderEvent = AbstractEventGenerator.createEvent(MsgType.EXECUTION_REPORT, event.getPartitionId(), order.getSymbol());
			orderEvent.setFIXMessage(msg);
			fixMsgPort.send(orderEvent, threadScope);

			try {
				Thread.sleep(random.nextInt(maxPendingToAckTime));
			} catch (Exception e) {
			}
		}

		// cancel ack.
		Map<Integer, String> msgMap = new LinkedHashMap<>(AbstractHandler.CANCELLED);

		addMiscForResponse(msgMap, order, origClOrdID, stockInfo);

		quickfix.Message msg = ToolUtils.buildMessage(DATA_DICTIONARY, (Map) CH.m(TAG_MsgType, quickfix.field.MsgType.EXECUTION_REPORT), msgMap);
		event.setFIXMessage(msg);
		fixMsgPort.send(event, threadScope);

		orderCache.addOrder(order.getSymbol(), ORDER_STATE.OPEN, ORDER_STATE.CLOSE, order);
	}

}
