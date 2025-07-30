package com.f1.generator.handler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.container.ThreadScope;
import com.f1.fix2ami.tool.ToolUtils;
import com.f1.generator.Order;
import com.f1.generator.OrderCache;
import com.f1.generator.OrderCache.SEQUENCE_TYPE;
import com.f1.generator.OrderEvent;
import com.f1.generator.StockInfo;
import com.f1.pofo.fix.MsgType;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;

import quickfix.ConfigError;

public class CancelRequestGenerator extends AbstractHandler {
	private static final Logger log = Logger.getLogger(CancelRequestGenerator.class.getName());

	public CancelRequestGenerator(final PropertyController props) throws ConfigError {
		super(props);
	}

	@Override
	public void processAction(OrderEvent event, OrderCache orderCache, ThreadScope threadScope) throws Exception {
		LH.info(log, "generate a CANCEL REQUEST (CLIENT) -> ", event);
		Map<Integer, String> msgMap = new LinkedHashMap<>(AbstractHandler.CANCEL_REQUEST);

		Order order = randomOrderPick(random, orderCache, event.getSymbol(), true);
		if (null == order) {
			LH.info(log, "There is no possible cancel request candidate (skip cancel request).");
			return;
		}

		// update the specific fields.
		msgMap.put(TAG_Qty, String.valueOf(order.getQty()));

		addMiscForRequest(msgMap, order, Order.createID(Order.CLIENT_PREFIX, order.getClient(), OrderCache.getOrderSequence(SEQUENCE_TYPE.CLIENT, order.getClient())),
				StockInfo.getStockInfo(event.getSymbol()));
		msgMap.remove(TAG_OrdType);
		msgMap.remove(TAG_Price);

		quickfix.Message msg = ToolUtils.buildMessage(DATA_DICTIONARY, (Map) CH.m(TAG_MsgType, event.getMsgType().getEnumValue().toString()), msgMap);
		event.setFIXMessage(msg);
		fixMsgPort.send(event, threadScope);
		order.setLastMsgType(MsgType.CANCEL_REQUEST);
		orderCache.addOrder(event.getSymbol(), null, OrderCache.ORDER_STATE.OPEN, order);
	}

}
