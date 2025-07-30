/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.utils.msg;

import java.util.logging.Level;

import com.f1.base.Message;
import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.msg.MsgBytesEvent;
import com.f1.msg.MsgOutputTopic;
import com.f1.povo.msg.MsgMessage;
import com.f1.utils.LH;

public class OutboundReqResProcessor extends BasicProcessor<Message, ConverterState> {

	private ObjectGeneratorForClass<MsgMessage> requestMessageGenerator;
	private String outputTopic;
	private String resultTopicSuffix;
	private boolean supportCicRefs = false;
	private ObjectGeneratorForClass<MsgAction> msgActionGenerator;
	private MsgOutputTopic channel;
	private boolean supportConnectionMonitoring = false;
	final private String description;

	public OutboundReqResProcessor(String topicDescription) {
		super(Message.class, ConverterState.class);
		this.description = topicDescription;
	}

	@Override
	public void processAction(Message action, ConverterState state, ThreadScope threadScope) throws Exception {

		Message outputMessage;
		MsgOutputTopic channel = this.channel;
		if (channel == null)
			return;
		Long correlationId = null;
		if (action instanceof MsgMessage) {
			MsgMessage msg = (MsgMessage) action;
			if (msg.getRequestTopicSuffix() != null) {
				channel = channel.getConnection().getOutputTopic(channel.getName(), msg.getRequestTopicSuffix());
			}
			outputMessage = action;
			if (msg.getMessage() instanceof RequestMessage) {
				msg.setCorrelationId(correlationId = state.addRequest((RequestMessage) msg.getMessage()));
				if (log.isLoggable(Level.FINER))
					LH.finer(log, describe(), " Added correlationId: ", msg.getCorrelationId());
			}
		} else if (action instanceof RequestMessage) {
			final MsgMessage message = requestMessageGenerator.nw();
			message.setCorrelationId(correlationId = state.addRequest((RequestMessage) action));
			if (log.isLoggable(Level.FINER))
				LH.finer(log, describe(), " Added correlationId: ", message.getCorrelationId());
			message.setMessage(action);
			message.setResultTopicSuffix(resultTopicSuffix);
			outputMessage = message;
		} else if (action instanceof ResultMessage) {
			final MsgMessage message = requestMessageGenerator.nw();
			MsgMessage origMessage = (MsgMessage) ((ResultMessage) action).getRequestMessage().getCorrelationId();
			message.setCorrelationId(origMessage.getCorrelationId());
			String resultTopicSuffix = origMessage.getResultTopicSuffix();
			message.setMessage(action);
			if (resultTopicSuffix != null) {
				channel = channel.getConnection().getOutputTopic(channel.getName(), resultTopicSuffix);
			}
			outputMessage = message;
		} else
			outputMessage = action;

		final byte[] data = state.write(outputMessage, supportCicRefs);
		final MsgBytesEvent e = new MsgBytesEvent(data);
		action.transferAckerTo(e);
		if (!supportConnectionMonitoring) {
			channel.send(e);
		} else {
			if (correlationId == null) {
				if (state.getIsAllConnected())
					channel.send(e);
				else
					LH.info(log, "Dropping request, not connected to ", channel, " ==> ", action);//TODO suppress if too frequent
			} else {
				if (state.getIsAllConnected()) {
					channel.send(e);
				}
				state.monitorRequest(correlationId, channel, e, outputMessage);
			}
		}
	}
	public void setResultTopicSuffix(String inputTopic) {
		assertNotStarted();
		this.resultTopicSuffix = inputTopic;
	}

	@Override
	public void start() {
		super.start();
		this.requestMessageGenerator = getGenerator(MsgMessage.class);
		this.msgActionGenerator = getGenerator(MsgAction.class);
	}

	@Override
	public void init() {
		super.init();
		getContainer().getPartitionController().registerStateGenerator(new ConverterStateGenerator());
	}

	public void setSupportCicRefs(boolean supportCicRefs) {
		assertNotStarted();
		this.supportCicRefs = supportCicRefs;
	}

	public boolean isSupportCicRefs() {
		return supportCicRefs;
	}
	public void setSupportConnectionMonitoring(boolean supportConnectionMonitoring) {
		assertNotStarted();
		this.supportConnectionMonitoring = supportConnectionMonitoring;
	}

	public boolean isSupportConnectionMonitoring() {
		return supportConnectionMonitoring;
	}

	public void setTopic(MsgOutputTopic channel) {
		assertNotStarted();
		this.channel = channel;
	}

	public void replaceTopic(MsgOutputTopic channel) {
		this.channel = channel;
	}

	public MsgOutputTopic getChannel() {
		return channel;
	}

	private String describe() {
		return description;
	}
}
