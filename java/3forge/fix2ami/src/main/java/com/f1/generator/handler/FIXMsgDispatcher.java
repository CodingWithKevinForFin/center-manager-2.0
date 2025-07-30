package com.f1.generator.handler;

import java.util.logging.Logger;

import com.f1.container.Container;
import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.fix2ami.processor.AbstractFix2AmiProcessor;
import com.f1.generator.OrderCache;
import com.f1.generator.OrderEvent;
import com.f1.pofo.fix.MsgType;
import com.f1.transportManagement.MessageDispatcher;
import com.f1.utils.LH;

import quickfix.DataDictionary;
import quickfix.Message;

public class FIXMsgDispatcher extends BasicProcessor<OrderEvent, OrderCache> implements MessageDispatcher {
	private static final Logger log = Logger.getLogger(FIXMsgDispatcher.class.getName());

	public OutputPort<OrderEvent> newOrderPort = newOutputPort(OrderEvent.class);
	public OutputPort<OrderEvent> executionReportPort = newOutputPort(OrderEvent.class);
	public OutputPort<OrderEvent> cancelRequestPort = newOutputPort(OrderEvent.class);
	public OutputPort<OrderEvent> replaceRequestPort = newOutputPort(OrderEvent.class);
	public OutputPort<OrderEvent> cancelRejectPort = newOutputPort(OrderEvent.class);

	private volatile DataDictionary dataDictionary;

	public FIXMsgDispatcher() {
		super(OrderEvent.class, OrderCache.class);
		dataDictionary = AbstractHandler.getDictionary();
	}

	public void sendRequest(final Container container, final String clOrdID, final MsgType msgType, final String partitionId, final Message msg) {
		if (null == dataDictionary) {
			dataDictionary = AbstractHandler.getDictionary();
			if (null == dataDictionary) {
				LH.warning(log, "internal error did not get data dictionary");
				return;
			}
		}
		OrderEvent orderEvent = container.nw(OrderEvent.class);
		orderEvent.setClOrdID(clOrdID);
		orderEvent.setMsgType(msgType);
		orderEvent.setPartitionId(partitionId);
		orderEvent.setFIXMessage(msg);
		String symbol = AbstractFix2AmiProcessor.getTagValue(dataDictionary, msg, AbstractHandler.TAG_Symbol);
		orderEvent.setSymbol(symbol);
		this.getInputPort().dispatch(orderEvent);
	}

	@Override
	public void processAction(OrderEvent event, OrderCache state, ThreadScope threadScope) throws Exception {
		LH.info(log, event + ": " + "Partiont id: " + state.getPartition().getPartitionId());

		if (null == event.getMsgType()) {
			LH.warning(log, "Null MsgType");
			return;
		}

		switch (event.getMsgType()) {
			case NEW_ORDER_SINGLE:
				newOrderPort.send(event, threadScope);
				break;
			case EXECUTION_REPORT:
				executionReportPort.send(event, threadScope);
				break;
			case CANCEL_REQUEST:
				cancelRequestPort.send(event, threadScope);
				break;
			case REPLACE_REQUEST:
				replaceRequestPort.send(event, threadScope);
				break;
			case CANCEL_REJECT:
				cancelRejectPort.send(event, threadScope);
				break;
			default:
				LH.warning(log, "Unsupport MsgType: ", event.getMsgType());
		}
	}

}
