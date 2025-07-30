package com.f1.generator.handler;

import java.util.logging.Logger;

import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.fix2ami.processor.AbstractFix2AmiProcessor;
import com.f1.generator.Order;
import com.f1.generator.OrderCache;
import com.f1.generator.OrderCache.ORDER_STATE;
import com.f1.generator.OrderEvent;
import com.f1.pofo.fix.OrdStatus;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;

import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.Message;

public class ExecutionReportHandler extends BasicProcessor<OrderEvent, OrderCache> {
	private static Logger log = Logger.getLogger(ExecutionReportHandler.class.getName());

	private final DataDictionary dataDictionary;

	public ExecutionReportHandler(final PropertyController props) throws ConfigError {
		super(OrderEvent.class, OrderCache.class);
		dataDictionary = AbstractHandler.getDictionary();
		//		super(props);
		//		fixMsgPort.setConnectionOptional(true);
	}

	@Override
	public void processAction(OrderEvent event, OrderCache orderCache, ThreadScope threadScope) throws Exception {
		LH.info(log, "received an EXECUTION REPORT (CLIENT) ->   ", event);

		final String clOrdID = event.getClOrdID();
		Order order = orderCache.getOrder(clOrdID);

		if (null == order) {
			return;
		}

		final Message msg = event.getFIXMessage();

		try {
			final String ordStatus = msg.getString(AbstractHandler.TAG_OrdStatus);
			if (null != ordStatus) {
				switch (ordStatus) {
					case "0": // Ack
						order.setLastOrdStatus(OrdStatus.ACKED);
						orderCache.addOrder(event.getSymbol(), ORDER_STATE.NEW, ORDER_STATE.OPEN, order);
						order.setOrderState(ORDER_STATE.OPEN);
						break;
					case "3": //Done for Day
						orderCache.addOrder(event.getSymbol(), ORDER_STATE.OPEN, ORDER_STATE.CLOSE, order);
						order.setOrderState(ORDER_STATE.CLOSE);
						break;
					case "4": // Canceled
						orderCache.addOrder(event.getSymbol(), ORDER_STATE.OPEN, ORDER_STATE.CLOSE, order);
						order.setOrderState(ORDER_STATE.CLOSE);
						order.setLastOrdStatus(OrdStatus.CANCELLED);
						break;
					case "5": // Replaced
						order.setLastOrdStatus(OrdStatus.REPLACED);
						break;
					case "6": // pending cancel
						order.setLastOrdStatus(OrdStatus.PENDING_CXL);
						break;
					case "8": // rejected
						order.setLastOrdStatus(OrdStatus.REJECTED);
						break;
					case "A": // pending ack
						order.setLastOrdStatus(OrdStatus.PENDING_ACK);
						break;
					case "E": // pending replace
						order.setLastOrdStatus(OrdStatus.PENDING_RPL);
						break;
					case "1": // partial fill
					case "2": // fill
						if (ordStatus.equals("1")) {
							order.setLastOrdStatus(OrdStatus.PARTIAL);
						} else {
							order.setLastOrdStatus(OrdStatus.FILLED);
						}

						// trade correct or trade bust.
						final String execTransType = AbstractFix2AmiProcessor.getTagValue(dataDictionary, msg, AbstractFix2AmiProcessor.TAG_ExecTransType);
						if (execTransType.isEmpty()) {
							final String execType = AbstractFix2AmiProcessor.getTagValue(dataDictionary, msg, AbstractFix2AmiProcessor.TAG_ExecType);
							if (!execType.isEmpty()) {
								if (execType.charAt(0) == AbstractFix2AmiProcessor.ExecType_BUST) {
									// trade bust - unfortunately there is no OrdStatus.TRADE_BUST.
								}
							}
						} else {
							switch (execTransType.charAt(0)) {
								case AbstractFix2AmiProcessor.ExecTransType_CANCEL:
									// trade bust - unfortunately there is no OrdStatus.TRADE_BUST.
									break;
								case AbstractFix2AmiProcessor.ExecTransType_CORRECT:
									// trade bust - unfortunately there is no OrdStatus.TRADE_CORRECT.
									break;
								default:
									break;
							}
						}

						break;
					default:
						LH.warning(log, "unsupport ordStatus(39): ", ordStatus);
						break;
				}
			}
		} catch (FieldNotFound fe) {
			LH.warning(log, "ClOrdID(", event.getClOrdID(), ") does not have OrdStatus(39) tag");
		}
	}
}
