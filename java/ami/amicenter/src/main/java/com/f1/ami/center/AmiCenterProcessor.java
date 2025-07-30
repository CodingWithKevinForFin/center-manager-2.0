package com.f1.ami.center;

import com.f1.ami.amicommon.msg.AmiCenterChangesMessage;
import com.f1.ami.amicommon.msg.AmiRelayRequest;
import com.f1.ami.center.hdb.events.AmiHdbRequest;
import com.f1.ami.center.hdb.events.AmiHdbResponse;
import com.f1.base.Message;
import com.f1.container.OutputPort;
import com.f1.container.Processor;
import com.f1.container.RequestMessage;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultMessage;
import com.f1.container.ResultOutputPort;
import com.f1.container.State;
import com.f1.povo.msg.MsgMessage;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.povo.standard.TextMessage;

public interface AmiCenterProcessor<A extends Message, S extends State> extends Processor<A, S> {

	//port for sending messages on the wire to agents, or a particular agent if requestSuffix is set on the message
	OutputPort<MsgMessage> getToAgentOutputPort();

	//port for looping messages back into same processor
	OutputPort<A> getLoopbackPort();

	//port for broadcasting messages on the wire to clients
	OutputPort<AmiCenterChangesMessage> getToClientsPort();

	//port for sending responses
	ResultOutputPort<Message> getResponseRoutingPort();

	RequestOutputPort<RunnableRequestMessage, TextMessage> getToRunnablePort();

	OutputPort<AmiCenterStartItineraryMessage> getStartItineraryOutputPort();

	RequestOutputPort<AmiHdbRequest, AmiHdbResponse> getToHdbPort();

	//Conveniently send a message to all clients
	void sendToClients(AmiCenterChangesMessage deltas);

	//Conveniently send a request to a single agent and send the response back on the response port
	MsgMessage sendRequestToAgent(AmiRelayRequest req, String processUid, OutputPort<?> responsePort, long delayMs);

	//Conveniently send a message to a single agent
	void sendToAgent(Message req, String processUid);

	String getProcessUidFromAgentResponse(ResultMessage<?> result);

	<M extends Message> void startItinerary(AmiCenterItinerary<M> itinerary, RequestMessage<M> action);

	void sendHdbRequest(AmiHdbRequest rm);
}
