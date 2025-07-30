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

public class ReplaceRequestHandler extends AbstractHandler {
	private static final Logger log = Logger.getLogger(ReplaceRequestHandler.class.getName());

	private final int modPendingProbability;
	private final int modRejectProbability;
	private final int fillAtLimitPriceProbability;

	public ReplaceRequestHandler(final PropertyController props) throws ConfigError {
		super(props);
		modPendingProbability = props.getOptional(ATTR_GENERATOR_EXCHANGE_MOD_PENDING_PROBABILITY, DEFAULT_MOD_PENDING_PROBABILITY);
		modRejectProbability = props.getOptional(ATTR_GENERATOR_EXCHANGE_MOD_REJECT_PROBABILITY, DEFAULT_MOD_REJECT_PROBABILITY);
		fillAtLimitPriceProbability = props.getOptional(AbstractHandler.ATTR_GENERATOR_EXCHANGE_FILL_AT_LIMIT_PRICE_PROBABILTY, DEFAULT_FILL_AT_LIMIT_PRICE_PROBABILITY);
	}

	@Override
	public void processAction(OrderEvent event, OrderCache orderCache, ThreadScope threadScope) throws Exception {
		LH.info(log, "received a MOD REQUEST (EXCHANGE) -> ", event);

		final String origClOrdID = AbstractFix2AmiProcessor.getTagValue(DATA_DICTIONARY, event.getFIXMessage(), TAG_OrigClOrdID);
		Order order = orderCache.getOrder(origClOrdID);
		if (null == order) {
			// should never happen.
			LH.warning(log, "internal error - order is not in orderCache for clOrdID: ", event.getClOrdID());
			return;
		}

		// ExecID(17)
		int uniqueId = OrderCache.getOrderSequence(SEQUENCE_TYPE.EXCHANGE, 1);
		final String execID = Order.createID("ExecMOD", uniqueId);
		order.setExecID(execID);

		// OrderID(37)
		if (null == order.getOrderID()) {
			final String orderID = Order.createID(Order.EXCHANGE_PREFIX, uniqueId);
			order.setOrderID(orderID);
		}

		// OrigClOrdID(41)
		order.setOrigClOrdID(origClOrdID);

		String text = null;
		boolean modReject = false;

		if (order.getOrderState() != ORDER_STATE.OPEN) {
			text = "Mod reject - order is already completed.";
			modReject = true;
		}

		if (order.getLastOrdStatus() == OrdStatus.CANCELLED || order.getLastOrdStatus() == OrdStatus.PENDING_CXL) {
			text = "Mod reject - order has been cancelled.";
			modReject = true;
		}

		if (!modReject && modRejectProbability <= random.nextInt(100)) {
			//  do a fully filled.
			fixMsgPort.send(creatFillEvent(order, random, orderCache, event.getPartitionId(), fillAtLimitPriceProbability), threadScope);

			text = "Mod reject - order already filled.";
			modReject = true;

			order.setExecID(Order.createID("ExecMRE", OrderCache.getOrderSequence(SEQUENCE_TYPE.EXCHANGE, 1)));
		}

		order.setClOrdID(event.getClOrdID());

		if (modReject) {
			Map<Integer, String> msgMap = new LinkedHashMap<>(AbstractHandler.CANCEL_REJECT);

			msgMap.put(TAG_CxlRejResponseTo, "2"); // Mod reject.
			addMiscForCancelReject(msgMap, order, event.getClOrdID(), text);

			quickfix.Message msg = ToolUtils.buildMessage(DATA_DICTIONARY, (Map) CH.m(TAG_MsgType, quickfix.field.MsgType.ORDER_CANCEL_REJECT), msgMap);
			event.setFIXMessage(msg);
			fixMsgPort.send(event, threadScope);
			return;
		}

		final StockInfo stockInfo = StockInfo.getStockInfo(event.getSymbol());

		final String qtyOnMsg = AbstractFix2AmiProcessor.getTagValue(DATA_DICTIONARY, event.getFIXMessage(), TAG_Qty);
		if (qtyOnMsg.isEmpty()) {
			LH.info(log, "missing qty in message.");
			return;
		}
		order.setQty(Integer.parseInt(qtyOnMsg));

		if (order.getOrdType() == Order.LIMIT_ORDER) {
			final String priceOnMsg = AbstractFix2AmiProcessor.getTagValue(DATA_DICTIONARY, event.getFIXMessage(), TAG_Price);
			if (priceOnMsg.isEmpty()) {
				LH.info(log, "missing price on message.");
				return;
			}
			order.setPrice(Double.parseDouble(priceOnMsg));
		}

		if (modPendingProbability <= random.nextInt(100)) {
			// Mod pending.
			Map<Integer, String> msgMap = new LinkedHashMap<>(AbstractHandler.REPLACE_PENDING);
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

		// mod ack.
		Map<Integer, String> msgMap = new LinkedHashMap<>(AbstractHandler.REPLACED);
		addMiscForResponse(msgMap, order, origClOrdID, stockInfo);

		quickfix.Message msg = ToolUtils.buildMessage(DATA_DICTIONARY, (Map) CH.m(TAG_MsgType, quickfix.field.MsgType.EXECUTION_REPORT), msgMap);
		event.setFIXMessage(msg);
		fixMsgPort.send(event, threadScope);

		if (order.getLeaveQty() == 0) {
			orderCache.addOrder(order.getSymbol(), ORDER_STATE.OPEN, ORDER_STATE.CLOSE, order);
		}
	}

}
