package com.f1.fix2ami.processor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.f1.container.Container;
import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.fix2ami.Fix2AmiEvent;
import com.f1.fix2ami.Fix2AmiState;
import com.f1.pofo.fix.MsgType;
import com.f1.transportManagement.MessageDispatcher;
import com.f1.utils.LH;

import quickfix.Message;

public class MsgRoutingProcessor extends BasicProcessor<Fix2AmiEvent, Fix2AmiState> implements MessageDispatcher {
	public OutputPort<Fix2AmiEvent> newOrderPort = newOutputPort(Fix2AmiEvent.class);
	public OutputPort<Fix2AmiEvent> executionReportPort = newOutputPort(Fix2AmiEvent.class);
	public OutputPort<Fix2AmiEvent> cancelRequestPort = newOutputPort(Fix2AmiEvent.class);
	public OutputPort<Fix2AmiEvent> replaceRequestPort = newOutputPort(Fix2AmiEvent.class);
	public OutputPort<Fix2AmiEvent> cancelRejectPort = newOutputPort(Fix2AmiEvent.class);

	public OutputPort<Fix2AmiEvent> unsupportMessagePort = newOutputPort(Fix2AmiEvent.class);

	public MsgRoutingProcessor() {
		super(Fix2AmiEvent.class, Fix2AmiState.class);
	}

	@Override
	public void processAction(Fix2AmiEvent event, Fix2AmiState state, ThreadScope threadScope) throws Exception {
		LH.info(log, event + ": " + "Partiont id: " + state.getPartition().getPartitionId());
		if (AmiPublishProcessor.getLastTrackLastMessage()) {
			incomingMsgs.put(event.getClOrdID(), event.getFIXMessage());
		}

		if (null == event.getMsgType()) {
			unsupportMessagePort.send(event, threadScope);
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
				unsupportMessagePort.send(event, threadScope);
		}
	}

	// message for junit testing.
	private static final Map<String, Message> incomingMsgs = new ConcurrentHashMap<>();

	public static Message getLastIncomingMessage(final String clOrdID) {
		return incomingMsgs.get(clOrdID);
	}

	@Override
	public void sendRequest(Container container, String clOrdID, MsgType msgType, String partitionId, Message msg) {
		Fix2AmiEvent fixEvent = container.nw(Fix2AmiEvent.class);
		fixEvent.setClOrdID(clOrdID);
		fixEvent.setMsgType(msgType);
		fixEvent.setPartitionId(partitionId);
		fixEvent.setFIXMessage(msg);

		this.getInputPort().dispatch(fixEvent);
	}
}
