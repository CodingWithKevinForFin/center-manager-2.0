package com.vortex.agent;

import com.f1.base.Message;
import com.f1.container.InputPort;
import com.f1.container.Processor;
import com.f1.container.impl.BasicSuite;
import com.f1.msg.MsgConnection;
import com.f1.povo.msg.MsgStatusMessage;
import com.f1.suite.utils.ClassRoutingProcessor;
import com.f1.suite.utils.RunnableRequestProcessor;
import com.f1.suite.utils.msg.MsgSuite;
import com.f1.suite.utils.msg.MsgSuiteWithSecondary;
import com.f1.utils.EH;
import com.f1.utils.MH;
import com.vortex.agent.itinerary.VortexAgentItinerary;
import com.vortex.agent.processors.VortexAgentItineraryProcessor;
import com.vortex.agent.processors.VortexAgentMonitoringProcessor;
import com.vortex.agent.processors.VortexAgentOsAdapterProcessor;
import com.vortex.agent.processors.VortexAgentProcessor;
import com.vortex.agent.processors.VortexAgentRequestProcessor;
import com.vortex.agent.processors.VortexAgentResultProcessor;
import com.vortex.agent.processors.eye.VortexAgentRequestToItineraryProcessor;

public class VortexAgentSuite extends BasicSuite {

	public static final String PARTITIONID_VORTEX_AGENT = "VORTEX_AGENT";
	public static final int RECEIVE_FROM_F1APP = 1;
	public static final int RECEIVE_FROM_EYE = 2;
	public static final int RECEIVE_STATUS_FROM_F1APP = 4;
	public static final int RECEIVE_STATUS_FROM_EYE = 8;

	final private MsgSuite msgAgentSuite;
	final public MsgSuiteWithSecondary msgClientSuite;
	final private ClassRoutingProcessor<Message> appRoutingProcessor;
	final private ClassRoutingProcessor<Message> eyeRoutingProcessor;
	final private ClassRoutingProcessor<Message> resultRoutingProcessor;
	final private VortexAgentItineraryProcessor itineraryProcessor;
	final private VortexAgentOsAdapterProcessor osAdapterProcessor;
	final private InputPort<Message> simulateFromEyeInboundPort;
	final private VortexAgentMonitoringProcessor deploymentsProcessor;
	final private RunnableRequestProcessor runnableProcessor;

	public VortexAgentSuite(MsgConnection appConnection, MsgConnection connection, MsgConnection connection2) {
		if (appConnection == null) {
			this.msgAgentSuite = null;
		} else {
			msgAgentSuite = addChild(new MsgSuite("MSG_APPS", appConnection, "f1.app.to.agent", "f1.agent.to.app", EH.getProcessUid()));
			msgAgentSuite.setName("MsgAgentSuite");
		}

		msgClientSuite = addChild(new MsgSuiteWithSecondary("MSG_EYE", connection, connection2, "f1.server.to.agent", "f1.agent.to.server", EH.getProcessUid()));
		msgClientSuite.setName("MsgClientSuite");

		appRoutingProcessor = addChild(new ClassRoutingProcessor<Message>(Message.class));
		eyeRoutingProcessor = addChild(new ClassRoutingProcessor<Message>(Message.class));
		resultRoutingProcessor = addChild(new ClassRoutingProcessor<Message>(Message.class));
		itineraryProcessor = addChild(new VortexAgentItineraryProcessor());
		osAdapterProcessor = addChild(new VortexAgentOsAdapterProcessor());
		deploymentsProcessor = addChild(new VortexAgentMonitoringProcessor());
		runnableProcessor = addChild(new RunnableRequestProcessor());

		appRoutingProcessor.bindToPartition(PARTITIONID_VORTEX_AGENT);
		eyeRoutingProcessor.bindToPartition(PARTITIONID_VORTEX_AGENT);
		resultRoutingProcessor.bindToPartition(PARTITIONID_VORTEX_AGENT);

		wire(deploymentsProcessor.loopback, deploymentsProcessor, true);
		wire(deploymentsProcessor.toEye, msgClientSuite.getOutboundInputPort(), true);
		wire(msgClientSuite.inboundOutputPort, eyeRoutingProcessor, true);
		if (msgAgentSuite != null) {
			wire(msgAgentSuite.inboundOutputPort, appRoutingProcessor, true);
		}
		wireUpProcessor(itineraryProcessor);

		this.simulateFromEyeInboundPort = exposeInputPort(eyeRoutingProcessor);
	}
	public <P extends VortexAgentProcessor<T>, T extends Message> P addVortexAgentProcessor(P processor, int flags) {
		addChild(processor);
		wireUpProcessor(processor);

		if (processor instanceof VortexAgentRequestProcessor) {
			VortexAgentRequestProcessor<?, ?> requestProcessor = (VortexAgentRequestProcessor<?, ?>) processor;
			if (MH.allBits(flags, RECEIVE_FROM_EYE))
				wireRouting(eyeRoutingProcessor, requestProcessor);
			if (MH.allBits(flags, RECEIVE_FROM_F1APP))
				wireRouting(appRoutingProcessor, requestProcessor);
		} else if (processor instanceof VortexAgentResultProcessor) {
			VortexAgentResultProcessor<?> resultProcessor = (VortexAgentResultProcessor<?>) processor;
			wireRouting(resultRoutingProcessor, resultProcessor);
		} else {
			if (MH.allBits(flags, RECEIVE_FROM_EYE))
				wireRouting(eyeRoutingProcessor, processor);
			if (MH.allBits(flags, RECEIVE_FROM_F1APP))
				wireRouting(appRoutingProcessor, processor);
		}

		if (MH.allBits(flags, RECEIVE_STATUS_FROM_F1APP)) {
			if (processor.getActionType() != MsgStatusMessage.class)
				throw new IllegalArgumentException("can not receive status messages: " + processor);
			wire(msgAgentSuite.statusPort, (Processor<MsgStatusMessage, ?>) processor, true);
		}
		if (MH.allBits(flags, RECEIVE_STATUS_FROM_EYE)) {
			if (processor.getActionType() != MsgStatusMessage.class)
				throw new IllegalArgumentException("can not receive status messages: " + processor);
			wire(msgClientSuite.statusPort, (Processor<MsgStatusMessage, ?>) processor, true);
		}
		return processor;
	}

	public <P extends VortexAgentProcessor<T>, T extends Message> void wireUpProcessor(P processor) {
		processor.bindToPartition(PARTITIONID_VORTEX_AGENT);
		wire(processor.getLoopbackPort(), processor, true);
		if (msgAgentSuite != null)
			wire(processor.getToF1AppOutputPort(), msgAgentSuite.getOutboundInputPort(), true);
		wire(processor.getToEyePort(), msgClientSuite.getOutboundInputPort(), true);
		wire(processor.getResponseRoutingPort(), resultRoutingProcessor, true);
		wire(processor.getToOsAdapterOutputPort(), osAdapterProcessor, true);
		wire(processor.getToDeploymentsPort(), deploymentsProcessor, true);
		wire(processor.getToRunnablePort(), runnableProcessor, true);
		wire(processor.getStartItineraryOutputPort(), itineraryProcessor, true);//should this be false (forward instead?)
	}
	private <A extends Message> void wireRouting(ClassRoutingProcessor<?> router, VortexAgentResultProcessor<A> requestProcessor) {
		wire(router.newResultOutputPort(requestProcessor.getResultType()), requestProcessor, true);
	}
	private <A extends Message, B extends Message> void wireRouting(ClassRoutingProcessor<?> router, VortexAgentRequestProcessor<A, B> requestProcessor) {
		wire(router.newRequestOutputPort(requestProcessor.getRequestType(), requestProcessor.getResponseType()), requestProcessor, true);
	}
	private <A extends Message, B extends Message> void wireRouting(ClassRoutingProcessor<?> router, VortexAgentProcessor<A> requestProcessor) {
		wire(router.newOutputPort(requestProcessor.getActionType()), requestProcessor, true);
	}
	public <T extends Message, T2 extends VortexAgentItinerary<T>> void addItinerary(Class<T> msgType, Class<T2> itType, int options) {
		VortexAgentRequestToItineraryProcessor<T, T2> processor = new VortexAgentRequestToItineraryProcessor<T, T2>(msgType, itType);
		processor.setName("Itinerary_" + itType.getSimpleName());
		addVortexAgentProcessor(processor, options);
	}
	public InputPort<Message> getSimulateFromEyeInboundPort() {
		return simulateFromEyeInboundPort;
	}

}
