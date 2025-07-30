package com.f1.ami.relay;

import com.f1.ami.amicommon.msg.AmiRelayChangesMessage;
import com.f1.base.Message;
import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultOutputPort;
import com.f1.container.impl.BasicProcessor;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.povo.standard.TextMessage;

public abstract class AmiRelayBasicProcessor<A extends Message> extends BasicProcessor<A, AmiRelayState> implements AmiRelayProcessor<A> {

	private final OutputPort<A> loopback;
	private final OutputPort<AmiRelayChangesMessage> toEyePort = newOutputPort(AmiRelayChangesMessage.class).setName("toEyePort");
	private final ResultOutputPort<Message> responseRoutingPort = newResultOutputPort(Message.class).setName("resposneRoutingPort");
	private final RequestOutputPort<RunnableRequestMessage, TextMessage> runnablePort = newRequestOutputPort(RunnableRequestMessage.class, TextMessage.class);

	public AmiRelayBasicProcessor(Class<A> type) {
		super(type, AmiRelayState.class);
		loopback = newOutputPort(type).setName("loopbackPort");
	}

	@Override
	public OutputPort<A> getLoopbackPort() {
		return loopback;
	}

	@Override
	public OutputPort<AmiRelayChangesMessage> getToCenterPort() {
		return toEyePort;
	}

	@Override
	public ResultOutputPort<Message> getResponseRoutingPort() {
		return responseRoutingPort;
	}

	public final OutputPort<StartAmiRelayItineraryMessage> startItineraryOutputPort = newOutputPort(StartAmiRelayItineraryMessage.class);

	@Override
	public OutputPort<StartAmiRelayItineraryMessage> getStartItineraryOutputPort() {
		return startItineraryOutputPort;
	}

	@Override
	public <M extends Message> void startItinerary(AmiRelayItinerary<M> itinerary, RequestMessage<M> action) {
		StartAmiRelayItineraryMessage startMessage = nw(StartAmiRelayItineraryMessage.class);
		startMessage.setInitialRequest(action);
		startMessage.setItinerary(itinerary);
		startItineraryOutputPort.send(startMessage, null);
	}

	@Override
	public RequestOutputPort<RunnableRequestMessage, TextMessage> getToRunnablePort() {
		return this.runnablePort;
	}
}
