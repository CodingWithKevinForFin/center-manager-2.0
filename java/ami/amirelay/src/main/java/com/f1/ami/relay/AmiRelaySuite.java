package com.f1.ami.relay;

import java.util.ArrayList;
import java.util.List;

import com.f1.base.Message;
import com.f1.container.InputPort;
import com.f1.container.Processor;
import com.f1.container.impl.BasicSuite;
import com.f1.msg.MsgConnection;
import com.f1.povo.msg.MsgStatusMessage;
import com.f1.suite.utils.ClassRoutingProcessor;
import com.f1.suite.utils.RunnableRequestProcessor;
import com.f1.suite.utils.msg.MsgSuite;
import com.f1.utils.EH;
import com.f1.utils.MH;

public class AmiRelaySuite extends BasicSuite {

	public static final String PARTITIONID_AMI_RELAY = "AMI_RELAY";
	public static final int RECEIVE_FROM_F1APP = 1;
	public static final int RECEIVE_FROM_EYE = 2;
	public static final int RECEIVE_STATUS_FROM_F1APP = 4;
	public static final int RECEIVE_STATUS_FROM_EYE = 8;

	final private MsgSuite msgAgentSuite;
	final public List<MsgSuite> msgClientSuites = new ArrayList<MsgSuite>();
	final private ClassRoutingProcessor<Message> appRoutingProcessor;
	final private ClassRoutingProcessor<Message> eyeRoutingProcessor;
	final private ClassRoutingProcessor<Message> resultRoutingProcessor;
	final private AmiRelayItineraryProcessor itineraryProcessor;
	final private InputPort<Message> simulateFromEyeInboundPort;
	final private RunnableRequestProcessor runnableProcessor;
	final private AmiRelayMulticastProcessor toCenterProcessor;

	public AmiRelaySuite(MsgConnection appConnection, int centersCount, AmiRelayRoutes router) {
		if (appConnection == null) {
			this.msgAgentSuite = null;
		} else {
			msgAgentSuite = addChild(new MsgSuite("MSG_APPS", appConnection, "f1.app.to.agent", "f1.agent.to.app", EH.getProcessUid()));
			msgAgentSuite.setName("MsgAgentSuite");
		}

		appRoutingProcessor = addChild(new ClassRoutingProcessor<Message>(Message.class));
		eyeRoutingProcessor = addChild(new ClassRoutingProcessor<Message>(Message.class));
		resultRoutingProcessor = addChild(new ClassRoutingProcessor<Message>(Message.class));
		itineraryProcessor = addChild(new AmiRelayItineraryProcessor());
		runnableProcessor = addChild(new RunnableRequestProcessor());

		appRoutingProcessor.bindToPartition(PARTITIONID_AMI_RELAY);
		eyeRoutingProcessor.bindToPartition(PARTITIONID_AMI_RELAY);
		resultRoutingProcessor.bindToPartition(PARTITIONID_AMI_RELAY);
		toCenterProcessor = addChild(new AmiRelayMulticastProcessor(router, centersCount));
		toCenterProcessor.bindToPartition(PARTITIONID_AMI_RELAY);

		if (msgAgentSuite != null) {
			wire(msgAgentSuite.inboundOutputPort, appRoutingProcessor, true);
		}
		wireUpProcessor(itineraryProcessor);
		itineraryProcessor.bindToPartition(PARTITIONID_AMI_RELAY);

		this.simulateFromEyeInboundPort = exposeInputPort(eyeRoutingProcessor);
	}

	public void addCenterConnection(MsgConnection connection, byte centerId) {
		MsgSuite msgClientSuite = addChild(new MsgSuite("MSG_CENTER_" + this.msgClientSuites.size(), connection, "center.to.relay", "relay.to.center", EH.getProcessUid()));
		AmiRelayFromCenterPreProcessor fcpp = new AmiRelayFromCenterPreProcessor(centerId);
		addChild(fcpp);
		msgClientSuite.setName("MsgClientSuite");
		wire(msgClientSuite.inboundOutputPort, fcpp, true);
		wire(fcpp.out, eyeRoutingProcessor, true);
		this.msgClientSuites.add(msgClientSuite);
		wire(this.toCenterProcessor.getToCenterPort(centerId), msgClientSuite.getOutboundInputPort(), true);
	}
	public <P extends Processor<T, AmiRelayState>, T extends Message> P addAmiRelayProcessor(P processor, int flags) {
		addChild(processor);
		if (processor instanceof AmiRelayProcessor)
			wireUpProcessor((AmiRelayProcessor) processor);

		processor.bindToPartition(PARTITIONID_AMI_RELAY);
		if (processor instanceof AmiRelayRequestProcessor) {
			AmiRelayRequestProcessor<?, ?> requestProcessor = (AmiRelayRequestProcessor<?, ?>) processor;
			if (MH.allBits(flags, RECEIVE_FROM_EYE))
				wireRouting(eyeRoutingProcessor, requestProcessor);
			if (MH.allBits(flags, RECEIVE_FROM_F1APP))
				wireRouting(appRoutingProcessor, requestProcessor);
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
			for (MsgSuite msgClientSuite : this.msgClientSuites)
				wire(msgClientSuite.statusPort, (Processor<MsgStatusMessage, ?>) processor, true);
		}
		return processor;
	}

	public <P extends AmiRelayProcessor<T>, T extends Message> void wireUpProcessor(P processor) {
		wire(processor.getLoopbackPort(), processor, true);
		wire(processor.getToCenterPort(), this.toCenterProcessor, false);
		wire(processor.getResponseRoutingPort(), resultRoutingProcessor, true);
		wire(processor.getToRunnablePort(), runnableProcessor, true);
		wire(processor.getStartItineraryOutputPort(), itineraryProcessor, true);//should this be false (forward instead?)
	}
	private <A extends Message, B extends Message> void wireRouting(ClassRoutingProcessor<?> router, AmiRelayRequestProcessor<A, B> requestProcessor) {
		wire(router.newRequestOutputPort(requestProcessor.getRequestType(), requestProcessor.getResponseType()), requestProcessor, true);
	}
	private <A extends Message, B extends Message> void wireRouting(ClassRoutingProcessor<?> router, Processor<A, AmiRelayState> requestProcessor) {
		wire(router.newOutputPort(requestProcessor.getActionType()), requestProcessor, true);
	}
	public <T extends Message, T2 extends AmiRelayItinerary<T>> void addItinerary(Class<T> msgType, Class<T2> itType, int options) {
		AmiRelayRequestToItineraryProcessor<T, T2> processor = new AmiRelayRequestToItineraryProcessor<T, T2>(msgType, itType);
		processor.setName("Itinerary_" + itType.getSimpleName());
		addAmiRelayProcessor(processor, options);
	}
	public InputPort<Message> getSimulateFromEyeInboundPort() {
		return simulateFromEyeInboundPort;
	}

}
