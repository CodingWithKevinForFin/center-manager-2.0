/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.utils.msg;

import com.f1.base.Action;
import com.f1.base.Message;
import com.f1.container.InputPort;
import com.f1.container.OutputPort;
import com.f1.container.PartitionResolver;
import com.f1.container.RequestInputPort;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.impl.BasicPartitionResolver;
import com.f1.container.impl.BasicSuite;
import com.f1.msg.MsgConnection;
import com.f1.msg.MsgInputTopic;
import com.f1.msg.MsgManager;
import com.f1.msg.MsgOutputTopic;
import com.f1.povo.msg.MsgMessage;
import com.f1.povo.msg.MsgStatusMessage;
import com.f1.utils.EH;

public class MsgSuite extends BasicSuite {

	final private InboundMsgToProcessor inboundMsg;
	final private InboundReqResProcessor inboundReqRes;
	final private MsgStatusProcessor connectionProcessor;
	final private OutboundReqResProcessor outboundReqRes;
	final public OutputPort<Message> inboundOutputPort;
	final public OutputPort<MsgStatusMessage> statusPort;
	final public InputPort<Message> outboundInputPort;
	final public RequestInputPort<Message, Message> outboundRequestInputPort;

	private PartitionResolver partitionResolver;
	private MsgConnection connection;
	private MsgOutputTopic outboundTopic;
	private MsgInputTopic inboundTopic;
	private MsgInputTopic inboundUniqueTopic;
	private PendingRequestMonitorProcessor pendingRequestMonitorProcessor;
	final private String description;

	public MsgOutputTopic getOutputTopic() {
		return this.outboundTopic;
	}
	public MsgInputTopic getInputTopic() {
		return this.inboundTopic;
	}
	public MsgInputTopic getInputUniqueTopic() {
		return this.inboundUniqueTopic;
	}

	private MsgSuite(PartitionResolver resolver, String inboundTopicName, String outboundTopicName, String uniqueSuffixName) {
		partitionResolver = resolver;
		this.description = inboundTopicName + "/" + outboundTopicName + (uniqueSuffixName == null ? "" : ("@" + uniqueSuffixName));
		inboundMsg = new InboundMsgToProcessor();
		inboundReqRes = new InboundReqResProcessor(description);
		outboundReqRes = new OutboundReqResProcessor(description);
		connectionProcessor = new MsgStatusProcessor();
		addChildren(inboundMsg, inboundReqRes, outboundReqRes, connectionProcessor);
		wire(inboundMsg.output, inboundReqRes, true);
		inboundOutputPort = exposeOutputPort(inboundReqRes.outputPort);
		statusPort = exposeOutputPort(connectionProcessor.outputPort);
		inboundOutputPort.setConnectionOptional(true);
		wire(inboundReqRes.outboundReqResPort, outboundReqRes, false);
		outboundInputPort = exposeInputPort(outboundReqRes);
		outboundInputPort.setConnectionOptional(true);
		outboundRequestInputPort = newRequestInputPort(Message.class, Message.class);
		exposeInputPortTo(outboundReqRes, outboundRequestInputPort);
		applyPartitionResolver(resolver, true, true);
	}

	public MsgSuite(String partitionId, MsgConnection connection, String inboundTopic, String outboundTopic) {
		this(partitionId, connection, inboundTopic, outboundTopic, EH.getProcessUid());
	}
	public MsgSuite(String partitionId, MsgConnection connection, String inboundTopic, String outboundTopic, String uniqueSuffix) {
		this(new BasicPartitionResolver<Action>(Action.class, partitionId), inboundTopic, outboundTopic, uniqueSuffix);
		this.connection = connection;
		this.connection.addMsgConnectionListener(connectionProcessor);
		if (outboundTopic != null) {
			this.outboundTopic = connection.getOutputTopic(outboundTopic);
			outboundReqRes.setTopic(this.outboundTopic);
			outboundReqRes.setResultTopicSuffix(uniqueSuffix);
			outboundReqRes.setName(outboundReqRes.getName() + "_" + outboundTopic.replace('/', '_'));
			setName(getName() + "_" + outboundTopic.replace('/', '_'));
		}
		if (inboundTopic != null) {
			this.inboundTopic = connection.getInputTopic(inboundTopic, null);
			this.inboundTopic.subscribe(inboundMsg);
			if (uniqueSuffix != null) {
				this.inboundUniqueTopic = connection.getInputTopic(inboundTopic, uniqueSuffix);
				this.inboundUniqueTopic.subscribe(inboundMsg);
			}
			inboundReqRes.setName(inboundReqRes.getName() + "_" + inboundTopic.replace('/', '_'));
			setName(getName() + "_" + inboundTopic.replace('/', '_'));
		}

		connectionProcessor.setOutboundTopic(this.outboundTopic);
		connectionProcessor.setInboundTopic(this.inboundTopic);
		connectionProcessor.setInboundUniqueTopic(this.inboundUniqueTopic);
	}
	public OutputPort<Message> getInboundOutputPort() {
		return inboundOutputPort;
	}

	public InputPort<Message> getOutboundInputPort() {
		return outboundInputPort;
	}

	public RequestInputPort<Message, Message> getOutboundRequestInputPort() {
		return outboundRequestInputPort;
	}

	public PartitionResolver getResolver() {
		return partitionResolver;
	}

	@Override
	public void start() {
		super.start();
		if (!connection.isRunning())
			connection.init();
		MsgManager msgManager = getContainer().getServices().getMsgManager();
		if (!msgManager.getConnections().contains(connection.getConfiguration().getName()))
			msgManager.addConnection(connection);
		getContainer().getServices().getGenerator().register(RequestMessage.class, ResultMessage.class, MsgMessage.class, Message.class);
	}

	public void init() {
		super.init();
	}

	public void setSupportCircularReferences(boolean supported) {
		this.outboundReqRes.setSupportCicRefs(supported);
		if (this.pendingRequestMonitorProcessor != null)
			this.pendingRequestMonitorProcessor.setSupportCircRefs(supported);
	}

	public MsgConnection getConnection() {
		return this.connection;
	}

	public void enableConnectionMonitoring(boolean resetTimeoutOnReconnect, long timeout, long checkFrequency) {
		assertNotStarted();
		if (checkFrequency > timeout)
			throw new IllegalArgumentException("check frequency must not be greater than timeout");
		this.outboundReqRes.setSupportConnectionMonitoring(true);
		this.pendingRequestMonitorProcessor = new PendingRequestMonitorProcessor(this.connectionProcessor, resetTimeoutOnReconnect, timeout, checkFrequency, this.description);
		this.addChild(pendingRequestMonitorProcessor);
		this.pendingRequestMonitorProcessor.setPartitionResolver(this.partitionResolver);
		this.pendingRequestMonitorProcessor.setSupportCircRefs(this.outboundReqRes.isSupportCicRefs());
		wire(this.pendingRequestMonitorProcessor.loopback, this.pendingRequestMonitorProcessor, true);
	}
	public void setPendingRequestHandler(PendingRequestHandler pendingRequestHandler) {
		this.pendingRequestMonitorProcessor.setPendingRequestHandler(pendingRequestHandler);
	}
	public String getTopicDescription() {
		return description;
	}
}
