/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.utils.msg;

import java.util.concurrent.TimeUnit;

import com.f1.base.Action;
import com.f1.base.Message;
import com.f1.container.InputPort;
import com.f1.container.OutputPort;
import com.f1.container.PartitionResolver;
import com.f1.container.RequestInputPort;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadPoolController;
import com.f1.container.impl.BasicPartitionResolver;
import com.f1.container.impl.BasicSuite;
import com.f1.msg.MsgConnection;
import com.f1.msg.MsgConnectionListener;
import com.f1.msg.MsgExternalConnection;
import com.f1.msg.MsgInputTopic;
import com.f1.msg.MsgManager;
import com.f1.msg.MsgOutputTopic;
import com.f1.msg.MsgTopic;
import com.f1.povo.msg.MsgMessage;
import com.f1.povo.msg.MsgStatusMessage;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.concurrent.IdentityHashSet;

public class MsgSuiteWithSecondary extends BasicSuite implements MsgConnectionListener, Runnable {

	final private InboundMsgToProcessor inboundMsg;
	final private InboundReqResProcessor inboundReqRes;
	final private MsgStatusProcessor connectionProcessor;
	final private OutboundReqResProcessor outboundReqRes;
	final public OutputPort<Message> inboundOutputPort;
	final public OutputPort<MsgStatusMessage> statusPort;
	final public InputPort<Message> outboundInputPort;
	final public RequestInputPort<Message, Message> outboundRequestInputPort;

	private PartitionResolver partitionResolver;
	private MsgConnection activeConnection;
	private MsgConnection primaryConnection;
	private MsgConnection secondaryConnection;
	private MsgInputTopic inboundTopic;
	private MsgOutputTopic outboundTopic;
	private MsgInputTopic uniqueSuffix;
	final private String inboundTopicName;
	final private String outboundTopicName;
	final private String uniqueSuffixName;
	final private String description;

	private MsgSuiteWithSecondary(PartitionResolver resolver, String inboundTopicName, String outboundTopicName, String uniqueSuffixName) {
		partitionResolver = resolver;
		inboundMsg = new InboundMsgToProcessor();
		this.description = inboundTopicName + "/" + outboundTopicName + (uniqueSuffixName == null ? "" : ("@" + uniqueSuffixName));
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
		this.inboundTopicName = inboundTopicName;
		this.outboundTopicName = outboundTopicName;
		this.uniqueSuffixName = uniqueSuffixName;
	}

	public MsgSuiteWithSecondary(String partitionId, MsgConnection primaryConnection, MsgConnection secondaryConnection, String inboundTopic, String outboundTopic) {
		this(partitionId, primaryConnection, secondaryConnection, inboundTopic, outboundTopic, EH.getProcessUid());
	}
	public MsgSuiteWithSecondary(String partitionId, MsgConnection primaryConnection, MsgConnection secondaryConnection, String inboundTopicName, String outboundTopicName,
			String uniqueSuffixName) {
		this(new BasicPartitionResolver<Action>(Action.class, partitionId), inboundTopicName, outboundTopicName, uniqueSuffixName);
		if (primaryConnection == secondaryConnection)
			throw new RuntimeException("primary must be distict from secondary");
		this.primaryConnection = primaryConnection;
		this.secondaryConnection = secondaryConnection;
		if (this.primaryConnection.isRunning())
			throw new IllegalStateException("primary connection already running");
		if (this.secondaryConnection.isRunning())
			throw new IllegalStateException("secondary connection already running");

		if (outboundTopicName != null) {
			outboundReqRes.setResultTopicSuffix(uniqueSuffixName);
			outboundReqRes.setName(outboundReqRes.getName() + "_" + outboundTopicName.replace('/', '_'));
			setName(getName() + "_" + outboundTopicName.replace('/', '_'));
		}
		if (inboundTopicName != null) {
			inboundReqRes.setName(inboundReqRes.getName() + "_" + inboundTopicName.replace('/', '_'));
			setName(getName() + "_" + inboundTopicName.replace('/', '_'));
		}
		synchronized (this.connectedTopics) {
			this.connectedTopics.clear();
			this.hasConnections = false;
		}
	}

	private void bringUpOnActive() {
		this.activeConnection.addMsgConnectionListener(this);
		if (outboundTopicName != null) {
			this.outboundTopic = activeConnection.getOutputTopic(outboundTopicName);
			outboundReqRes.replaceTopic(this.outboundTopic);
		}
		if (inboundTopicName != null) {
			this.inboundTopic = activeConnection.getInputTopic(inboundTopicName, null);
			this.inboundTopic.subscribe(inboundMsg);
			if (uniqueSuffixName != null) {
				this.uniqueSuffix = activeConnection.getInputTopic(inboundTopicName, uniqueSuffixName);
				this.uniqueSuffix.subscribe(inboundMsg);
			}
		}
		synchronized (this.connectedTopics) {
			this.connectedTopics.clear();
			this.hasConnections = false;
		}
		activeConnection.init();
	}
	private void teardownActive() {
		activeConnection.removeMsgConnectionListener(this);
		activeConnection.shutdown();
		if (outboundTopicName != null) {
			outboundReqRes.replaceTopic(null);
		}
		if (inboundTopicName != null) {
			this.inboundTopic.unsubscribe(inboundMsg);
			if (uniqueSuffixName != null)
				this.uniqueSuffix.unsubscribe(inboundMsg);
		}
		this.inboundTopic = null;
		this.outboundTopic = null;
		this.uniqueSuffix = null;
		synchronized (this.connectedTopics) {
			this.connectedTopics.clear();
			this.hasConnections = false;
		}
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
		MsgManager msgManager = getContainer().getServices().getMsgManager();
		if (!msgManager.getConnections().contains(primaryConnection.getConfiguration().getName()))
			msgManager.addConnection(primaryConnection);
		if (!msgManager.getConnections().contains(secondaryConnection.getConfiguration().getName()))
			msgManager.addConnection(secondaryConnection);
		getContainer().getServices().getGenerator().register(RequestMessage.class, ResultMessage.class, MsgMessage.class);
		getContainer().getThreadPoolController().execute(this, ThreadPoolController.POOLKEY_DELAYED, 0, TimeUnit.SECONDS);
	}

	public void init() {
		super.init();
	}

	public void setSupportCircularReferences(boolean supported) {
		this.outboundReqRes.setSupportCicRefs(supported);
	}

	final private IdentityHashSet<MsgTopic> connectedTopics = new IdentityHashSet<MsgTopic>();
	volatile private boolean hasConnections = false;

	@Override
	public void onDisconnect(MsgConnection connection, MsgTopic msgTopic, String topic, String suffix, String remoteHost, boolean isWrite, MsgExternalConnection externalConnection) {
		connectionProcessor.onDisconnect(connection, msgTopic, topic, suffix, remoteHost, isWrite, externalConnection);
		synchronized (connectedTopics) {
			connectedTopics.remove(msgTopic);
			this.hasConnections = !connectedTopics.isEmpty();
		}
	}

	@Override
	public void onConnect(MsgConnection connection, MsgTopic msgTopic, String topic, String suffix, String remoteHost, boolean isWrite, MsgExternalConnection externalConnection) {
		connectionProcessor.onConnect(connection, msgTopic, topic, suffix, remoteHost, isWrite, externalConnection);
		synchronized (connectedTopics) {
			connectedTopics.add(msgTopic);
			this.hasConnections = true;
		}
	}

	@Override
	public void onNewInputTopic(MsgConnection connection, MsgInputTopic r) {
		connectionProcessor.onNewInputTopic(connection, r);
	}

	@Override
	public void onNewOutputTopic(MsgConnection connection, MsgOutputTopic r) {
		connectionProcessor.onNewOutputTopic(connection, r);
	}

	@Override
	public void run() {
		try {
			if (activeConnection == null) {
				this.activeConnection = this.primaryConnection;
				bringUpOnActive();
			} else if (this.hasConnections == false) {
				teardownActive();
				this.activeConnection = this.activeConnection == this.primaryConnection ? this.secondaryConnection : this.primaryConnection;
				bringUpOnActive();
			}
		} catch (Exception e) {
			LH.severe(log, "Error with connection thread", e);
		}
		getContainer().getThreadPoolController().execute(this, ThreadPoolController.POOLKEY_DELAYED, 5, TimeUnit.SECONDS);
	}

}
