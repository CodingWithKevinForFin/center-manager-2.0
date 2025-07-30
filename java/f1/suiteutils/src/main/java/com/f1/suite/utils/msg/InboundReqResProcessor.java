/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.utils.msg;

import java.io.IOException;
import java.util.logging.Level;

import com.f1.base.Action;
import com.f1.base.Message;
import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.container.exceptions.ContainerException;
import com.f1.container.impl.BasicProcessor;
import com.f1.msg.MsgBytesEvent;
import com.f1.povo.msg.MsgMessage;
import com.f1.utils.LH;

public class InboundReqResProcessor extends BasicProcessor<MsgAction, ConverterState> {

	public final OutputPort<Message> outboundReqResPort = newOutputPort(Message.class);
	public final OutputPort<Message> outputPort = newOutputPort(Message.class);
	private String description;

	public InboundReqResProcessor(String topicDescription) {
		super(MsgAction.class, ConverterState.class);
		this.description = topicDescription;
	}

	@Override
	public void processAction(MsgAction action, ConverterState state, ThreadScope threadScope) throws IOException {
		try {
			byte[] data = ((MsgBytesEvent) action.getMsgEvent()).getBytes();
			Action a;
			a = (Action) state.read(data);
			action.transferAckerTo(a);
			if (a instanceof MsgMessage) {
				final MsgMessage msgMessage = (MsgMessage) a;
				msgMessage.setSource(action.getSource());
				Message message = msgMessage.getMessage();
				if (message instanceof RequestMessage) {
					RequestMessage requestMessage = (RequestMessage) message;
					requestMessage.setCorrelationId(msgMessage);
					requestMessage.setResultPort(outboundReqResPort);
					outputPort.send(requestMessage, threadScope);
				} else if (message instanceof ResultMessage) {
					ResultMessage resultMessage = (ResultMessage) message;
					final boolean remove = !resultMessage.getIsIntermediateResult();
					RequestMessage request = state.getRequest(msgMessage.getCorrelationId(), remove);
					if (request == null)
						LH.warning(log, describe(), " Unknown correlationId: ", msgMessage.getCorrelationId());
					else {
						if (log.isLoggable(Level.FINER))
							LH.finer(log, describe(), " Correlated CorrelationId: ", msgMessage.getCorrelationId());
						getContainer().getDispatchController().reply(getInputPort(), request, resultMessage, threadScope);
					}
				} else if (outputPort.isConnected())
					outputPort.send(message, threadScope);
			} else if (outputPort.isConnected())
				outputPort.send((Message) a, threadScope);
		} catch (Exception e) {
			throw new ContainerException("Could not process message from " + action.getSource() + " on topic " + action.getTopic(), e);
		}

	}

	private String describe() {
		return description;
	}
}
