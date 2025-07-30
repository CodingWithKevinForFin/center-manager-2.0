package com.vortex.eye;

import com.f1.base.Message;
import com.f1.container.Processor;
import com.f1.container.impl.BasicSuite;
import com.f1.msg.MsgConnection;
import com.f1.povo.msg.MsgStatusMessage;
import com.f1.suite.utils.ClassRoutingProcessor;
import com.f1.suite.utils.RunnableRequestProcessor;
import com.f1.suite.utils.msg.MsgSuite;
import com.f1.utils.EH;
import com.f1.utils.MH;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRequest;
import com.vortex.eye.itinerary.VortexEyeItinerary;
import com.vortex.eye.processors.VortexEyeDbRequestProcessor;
import com.vortex.eye.processors.VortexEyeDbResponseProcessor;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.processors.VortexEyeProcessor;
import com.vortex.eye.processors.VortexEyeRequestProcessor;
import com.vortex.eye.processors.VortexEyeResultProcessor;
import com.vortex.eye.processors.VortexEyeVaultRequestProcessor;
import com.vortex.eye.processors.agent.VortexEyeRequestToItineraryProcessor;

public class VortexEyeSuite extends BasicSuite {
	public static final String PARTITIONID_VORTEX_EYE = "VORTEX_EYE";
	public static final String PARTITIONID_DATABASE = "DATABASE";
	public static final String PARTITIONID_VAULT = "VAULT";
	public static final String PARTITIONID_CLOUD = "CLOUD";
	public static final int RECEIVE_FROM_CLIENT = 1;
	public static final int RECEIVE_FROM_AGENT = 2;
	public static final int RECEIVE_STATUS_FROM_AGENT = 4;
	public static final int RECEIVE_STATUS_FROM_CLIENT = 8;

	final private MsgSuite msgAgentSuite;
	final private MsgSuite msgClientSuite;
	final private ClassRoutingProcessor<Message> agentRoutingProcessor;
	final public ClassRoutingProcessor<Message> clientRoutingProcessor;
	final private ClassRoutingProcessor<Message> resultRoutingProcessor;
	final private VortexEyeDbRequestProcessor dbRequestProcessor;
	final private VortexEyeVaultRequestProcessor vaultProcessor;
	final private RunnableRequestProcessor runnableProcessor;
	final private VortexEyeDbResponseProcessor dbResultProcessor;
	final private VortexEyeItineraryProcessor itineraryProcessor;

	public VortexEyeSuite(MsgConnection agentConnection, MsgConnection glassConnection) {

		msgAgentSuite = addChild(new MsgSuite("MSG_AGENTS", agentConnection, "f1.agent.to.server", "f1.server.to.agent", EH.getProcessUid()));
		msgAgentSuite.setName("MsgAgentSuite");

		msgClientSuite = addChild(new MsgSuite("MSG_CLIENTS", glassConnection == null ? agentConnection : glassConnection, "gui.to.server", "server.to.gui", EH.getProcessUid()));
		msgClientSuite.setName("MsgClientSuite");

		dbRequestProcessor = addChild(new VortexEyeDbRequestProcessor());
		dbResultProcessor = addChild(new VortexEyeDbResponseProcessor());

		agentRoutingProcessor = addChild(new ClassRoutingProcessor<Message>(Message.class));
		clientRoutingProcessor = addChild(new ClassRoutingProcessor<Message>(Message.class));
		resultRoutingProcessor = addChild(new ClassRoutingProcessor<Message>(Message.class));
		itineraryProcessor = addChild(new VortexEyeItineraryProcessor());
		vaultProcessor = addChild(new VortexEyeVaultRequestProcessor());
		runnableProcessor = addChild(new RunnableRequestProcessor());

		vaultProcessor.bindToPartition(PARTITIONID_VAULT);
		dbRequestProcessor.bindToPartition(PARTITIONID_DATABASE);
		dbResultProcessor.bindToPartition(PARTITIONID_VORTEX_EYE);
		agentRoutingProcessor.bindToPartition(PARTITIONID_VORTEX_EYE);
		clientRoutingProcessor.bindToPartition(PARTITIONID_VORTEX_EYE);
		resultRoutingProcessor.bindToPartition(PARTITIONID_VORTEX_EYE);

		wire(msgClientSuite.inboundOutputPort, clientRoutingProcessor, true);
		wire(msgAgentSuite.inboundOutputPort, agentRoutingProcessor, true);
		wireUpProcessor(dbResultProcessor);
		wireUpProcessor(itineraryProcessor);

	}

	@Override
	public void init() {
		super.init();
	}
	public <P extends VortexEyeProcessor<T>, T extends Message> P addVortexEyeProcessor(P processor, int flags) {
		addChild(processor);
		wireUpProcessor(processor);

		if (processor instanceof VortexEyeRequestProcessor) {
			VortexEyeRequestProcessor<?, ?> requestProcessor = (VortexEyeRequestProcessor<?, ?>) processor;
			if (MH.allBits(flags, RECEIVE_FROM_CLIENT))
				wireRouting(clientRoutingProcessor, requestProcessor);
			if (MH.allBits(flags, RECEIVE_FROM_AGENT))
				wireRouting(agentRoutingProcessor, requestProcessor);
		} else if (processor instanceof VortexEyeResultProcessor) {
			VortexEyeResultProcessor<?> resultProcessor = (VortexEyeResultProcessor<?>) processor;
			wireRouting(resultRoutingProcessor, resultProcessor);
		} else {
			if (MH.allBits(flags, RECEIVE_FROM_CLIENT))
				wireRouting(clientRoutingProcessor, processor);
			if (MH.allBits(flags, RECEIVE_FROM_AGENT))
				wireRouting(agentRoutingProcessor, processor);
		}

		if (MH.allBits(flags, RECEIVE_STATUS_FROM_AGENT)) {
			if (processor.getActionType() != MsgStatusMessage.class)
				throw new IllegalArgumentException("can not receive status messages: " + processor);
			wire(msgAgentSuite.statusPort, (Processor<MsgStatusMessage, ?>) processor, true);
		}
		if (MH.allBits(flags, RECEIVE_STATUS_FROM_CLIENT)) {
			if (processor.getActionType() != MsgStatusMessage.class)
				throw new IllegalArgumentException("can not receive status messages: " + processor);
			wire(msgClientSuite.statusPort, (Processor<MsgStatusMessage, ?>) processor, true);
		}
		return processor;
	}

	public VortexEyeDbRequestProcessor getDbProcessor() {
		return this.dbRequestProcessor;
	}

	public <P extends VortexEyeProcessor<T>, T extends Message> void wireUpProcessor(P processor) {
		wire(processor.getLoopbackPort(), processor, true);
		processor.bindToPartition("VORTEX_EYE");
		wire(processor.getToAgentOutputPort(), msgAgentSuite.getOutboundInputPort(), true);
		wire(processor.getToClientsPort(), msgClientSuite.getOutboundInputPort(), true);
		wire(processor.getResponseRoutingPort(), resultRoutingProcessor, true);
		wire(processor.getToDatabasePort(), dbRequestProcessor, true);
		wire(processor.getToDatabasePort().getResponsePort(), dbResultProcessor, true);
		wire(processor.getStartItineraryOutputPort(), itineraryProcessor, true);
		wire(processor.getToVaultPort(), vaultProcessor, true);
		wire(processor.getToRunnablePort(), runnableProcessor, true);

	}

	private <A extends Message> void wireRouting(ClassRoutingProcessor<?> router, VortexEyeResultProcessor<A> requestProcessor) {
		wire(router.newResultOutputPort(requestProcessor.getResultType()), requestProcessor, true);
	}
	private <A extends VortexEyeRequest, B extends Message> void wireRouting(ClassRoutingProcessor<?> router, VortexEyeRequestProcessor<A, B> requestProcessor) {
		wire(router.newRequestOutputPort(requestProcessor.getRequestType(), requestProcessor.getResponseType()), requestProcessor, true);
	}
	private <A extends Message, B extends Message> void wireRouting(ClassRoutingProcessor<?> router, VortexEyeProcessor<A> requestProcessor) {
		wire(router.newOutputPort(requestProcessor.getActionType()), requestProcessor, true);
	}

	public <T extends VortexEyeRequest, T2 extends VortexEyeItinerary<T>> void addItinerary(Class<T> msgType, Class<T2> itType, int options) {
		VortexEyeRequestToItineraryProcessor<T, T2> processor = new VortexEyeRequestToItineraryProcessor<T, T2>(msgType, itType);
		processor.setName("Itinerary_" + itType.getSimpleName());
		addVortexEyeProcessor(processor, options);
	}

}
