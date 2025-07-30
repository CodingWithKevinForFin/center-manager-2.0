package com.f1.ami.center;

import com.f1.ami.amicommon.msg.AmiCenterRequest;
import com.f1.ami.amicommon.msg.AmiCenterResponse;
import com.f1.ami.center.hdb.events.AmiHdbReqResProcessor;
import com.f1.base.Message;
import com.f1.container.InputPort;
import com.f1.container.Processor;
import com.f1.container.RequestOutputPort;
import com.f1.container.RequestProcessor;
import com.f1.container.ResultProcessor;
import com.f1.container.State;
import com.f1.container.impl.BasicSuite;
import com.f1.msg.MsgConnection;
import com.f1.povo.msg.MsgStatusMessage;
import com.f1.suite.utils.ClassRoutingProcessor;
import com.f1.suite.utils.RunnableRequestProcessor;
import com.f1.suite.utils.msg.MsgSuite;
import com.f1.utils.EH;
import com.f1.utils.MH;

public class AmiCenterSuite extends BasicSuite {
	public static final String PARTITIONID_AMI_CENTER = "AMI_CENTER";
	public static final int RECEIVE_FROM_CLIENT = 1;
	public static final int RECEIVE_FROM_AGENT = 2;
	public static final int RECEIVE_STATUS_FROM_AGENT = 4;
	public static final int RECEIVE_STATUS_FROM_CLIENT = 8;

	final private MsgSuite msgAgentSuite;
	final private MsgSuite msgClientSuite;
	final private ClassRoutingProcessor<Message> agentRoutingProcessor;
	private final ClassRoutingProcessor<Message> clientRoutingProcessor;
	final private ClassRoutingProcessor<Message> resultRoutingProcessor;
	final private RunnableRequestProcessor runnableProcessor;
	final private AmiCenterItineraryProcessor itineraryProcessor;
	final private RequestOutputPort<AmiCenterRequest, AmiCenterResponse> fromClientRequestPort = newRequestOutputPort(AmiCenterRequest.class, AmiCenterResponse.class);
	final private AmiHdbReqResProcessor hdbReqResProcessor;

	public AmiCenterSuite(MsgConnection connection) {

		msgAgentSuite = addChild(new MsgSuite("MSG_AGENTS", connection, "relay.to.center", "center.to.relay", EH.getProcessUid()));
		msgAgentSuite.setName("MsgAgentSuite");

		msgClientSuite = addChild(new MsgSuite("MSG_CLIENTS", connection, "web.to.center", "center.to.web", EH.getProcessUid()));
		msgClientSuite.setName("MsgClientSuite");

		agentRoutingProcessor = addChild(new ClassRoutingProcessor<Message>(Message.class));
		clientRoutingProcessor = addChild(new ClassRoutingProcessor<Message>(Message.class));
		resultRoutingProcessor = addChild(new ClassRoutingProcessor<Message>(Message.class));
		itineraryProcessor = addChild(new AmiCenterItineraryProcessor());
		runnableProcessor = addChild(new RunnableRequestProcessor());
		hdbReqResProcessor = addChild(new AmiHdbReqResProcessor());

		agentRoutingProcessor.bindToPartition(PARTITIONID_AMI_CENTER + "_AR");
		clientRoutingProcessor.bindToPartition(PARTITIONID_AMI_CENTER + "_CR");
		resultRoutingProcessor.bindToPartition(PARTITIONID_AMI_CENTER + "_RR");
		wire(fromClientRequestPort, this.clientRoutingProcessor, true);

		wire(msgClientSuite.inboundOutputPort, clientRoutingProcessor, true);
		wire(msgAgentSuite.inboundOutputPort, agentRoutingProcessor, true);
		wireUpProcessor(itineraryProcessor);

	}

	@Override
	public void init() {
		super.init();
	}
	public <P extends Processor<T, S>, T extends Message, S extends State> P addAmiCenterProcessor(P processor, int flags) {
		addChild(processor);
		if (processor instanceof AmiCenterProcessor)
			wireUpProcessor((AmiCenterProcessor) processor);

		if (processor instanceof RequestProcessor) {
			RequestProcessor<T, ?, ?> requestProcessor = (RequestProcessor<T, ?, ?>) processor;
			if (MH.allBits(flags, RECEIVE_FROM_CLIENT))
				wireRoutingRequest(clientRoutingProcessor, requestProcessor);
			if (MH.allBits(flags, RECEIVE_FROM_AGENT))
				wireRoutingRequest(agentRoutingProcessor, requestProcessor);
		} else if (processor instanceof ResultProcessor) {
			ResultProcessor<T, ?> resultProcessor = (ResultProcessor<T, ?>) processor;
			wireRoutingResult(resultRoutingProcessor, resultProcessor);
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

	public InputPort<Message> getPortToRelay() {
		return this.msgAgentSuite.getOutboundInputPort();
	}

	public <P extends AmiCenterProcessor<T, ?>, T extends Message> void wireUpProcessor(P processor) {
		wire(processor.getLoopbackPort(), processor, true);
		processor.bindToPartition(PARTITIONID_AMI_CENTER);
		wire(processor.getToAgentOutputPort(), msgAgentSuite.getOutboundInputPort(), true);
		wire(processor.getToClientsPort(), msgClientSuite.getOutboundInputPort(), true);
		wire(processor.getResponseRoutingPort(), resultRoutingProcessor, true);
		wire(processor.getStartItineraryOutputPort(), itineraryProcessor, true);
		wire(processor.getToRunnablePort(), runnableProcessor, true);
		wire(processor.getToHdbPort(), hdbReqResProcessor, true);

	}

	private <A extends Message> void wireRoutingResult(ClassRoutingProcessor<?> router, ResultProcessor<A, ?> requestProcessor) {
		wire(router.newResultOutputPort(requestProcessor.getResultType()), requestProcessor, true);
	}
	private <A extends Message, B extends Message> void wireRoutingRequest(ClassRoutingProcessor<?> router, RequestProcessor<A, ?, ?> requestProcessor) {
		wire(router.newRequestOutputPort(requestProcessor.getRequestType(), requestProcessor.getResponseType()), requestProcessor, true);
	}
	private <A extends Message, B extends Message> void wireRouting(ClassRoutingProcessor<?> router, Processor<A, ?> requestProcessor) {
		wire(router.newOutputPort(requestProcessor.getActionType()), requestProcessor, true);
	}

	public <T extends AmiCenterRequest, T2 extends AmiCenterItinerary<T>> void addItinerary(Class<T> msgType, Class<T2> itType, int options) {
		AmiCenterRequestToItineraryProcessor<T, T2> processor = new AmiCenterRequestToItineraryProcessor<T, T2>(msgType, itType);
		processor.setName("Itinerary_" + itType.getSimpleName());
		addAmiCenterProcessor(processor, options);
		processor.bindToPartition(PARTITIONID_AMI_CENTER + "_RQ");
	}

	public MsgSuite getMsgClientSuite() {
		return msgClientSuite;
	}

	public ClassRoutingProcessor<Message> getClientRoutingProcessor() {
		return clientRoutingProcessor;
	}

	public RequestOutputPort<AmiCenterRequest, AmiCenterResponse> getFromClientRequestPort() {
		return this.fromClientRequestPort;
	}

	public AmiCenterItineraryProcessor getItineraryProcessor() {
		return itineraryProcessor;
	}

}
