package com.vortex.agent.processors;

import com.f1.base.Message;
import com.f1.container.OutputPort;
import com.f1.container.Processor;
import com.f1.container.RequestMessage;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultMessage;
import com.f1.container.ResultOutputPort;
import com.f1.povo.f1app.reqres.F1AppRequest;
import com.f1.povo.f1app.reqres.F1AppResponse;
import com.f1.povo.msg.MsgMessage;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.povo.standard.TextMessage;
import com.vortex.agent.itinerary.VortexAgentItinerary;
import com.vortex.agent.messages.StartVortexAgentItineraryMessage;
import com.vortex.agent.messages.VortexAgentDeploymentUpdateMessage;
import com.vortex.agent.messages.VortexAgentOsAdapterRequest;
import com.vortex.agent.messages.VortexAgentOsAdapterResponse;
import com.vortex.agent.state.VortexAgentState;

public interface VortexAgentProcessor<A extends Message> extends Processor<A, VortexAgentState> {

	//port for sending messages on the wire to agents, or a particular agent if requestSuffix is set on the message
	OutputPort<MsgMessage> getToF1AppOutputPort();

	//port for looping messages back into same processor
	OutputPort<A> getLoopbackPort();

	//port for broadcasting messages on the wire to clients
	OutputPort<Message> getToEyePort();

	//port for sending responses
	ResultOutputPort<Message> getResponseRoutingPort();

	//port for starting itineraries
	OutputPort<StartVortexAgentItineraryMessage> getStartItineraryOutputPort();

	//port for running os commands
	RequestOutputPort<VortexAgentOsAdapterRequest, VortexAgentOsAdapterResponse> getToOsAdapterOutputPort();

	//Conveniently send a message to all clients
	void sendToEye(Message deltas);

	//Conveniently send a request to a single agent and send the response back on the response port
	MsgMessage sendRequestToF1App(F1AppRequest req, String processUid, OutputPort<?> responsePort);

	//Conveniently send a message to a single agent
	void sendToF1App(Message req, String processUid);

	String getProcessUidFromF1AppResponse(ResultMessage<?> result);

	<M extends Message> void startItinerary(VortexAgentItinerary<M> itinerary, RequestMessage<M> action);

	RequestMessage<VortexAgentOsAdapterRequest> sendRequestToOsAdapter(VortexAgentOsAdapterRequest req, OutputPort<? super VortexAgentOsAdapterResponse> responsePort);

	String getF1AppProcessUid(ResultMessage<? extends F1AppResponse> result);

	OutputPort<VortexAgentDeploymentUpdateMessage> getToDeploymentsPort();

	RequestOutputPort<RunnableRequestMessage, TextMessage> getToRunnablePort();
}
