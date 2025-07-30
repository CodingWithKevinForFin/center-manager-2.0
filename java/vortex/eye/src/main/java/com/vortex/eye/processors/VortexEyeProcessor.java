package com.vortex.eye.processors;

import com.f1.base.Message;
import com.f1.container.OutputPort;
import com.f1.container.Processor;
import com.f1.container.RequestMessage;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultMessage;
import com.f1.container.ResultOutputPort;
import com.f1.povo.db.DbRequestMessage;
import com.f1.povo.db.DbResultMessage;
import com.f1.povo.msg.MsgMessage;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.povo.standard.TextMessage;
import com.f1.vortexcommon.msg.agent.TestTrackDeltas;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRequest;
import com.vortex.eye.VortexEyeDbService;
import com.vortex.eye.itinerary.VortexEyeItinerary;
import com.vortex.eye.messages.StartVortexEyeItineraryMessage;
import com.vortex.eye.messages.VortexVaultRequest;
import com.vortex.eye.messages.VortexVaultResponse;
import com.vortex.eye.state.VortexEyeState;

public interface VortexEyeProcessor<A extends Message> extends Processor<A, VortexEyeState> {

	//database interface
	VortexEyeDbService getDbService();

	//port for sending messages on the wire to agents, or a particular agent if requestSuffix is set on the message
	OutputPort<MsgMessage> getToAgentOutputPort();

	//port for looping messages back into same processor
	OutputPort<A> getLoopbackPort();

	//port for broadcasting messages on the wire to clients
	OutputPort<TestTrackDeltas> getToClientsPort();

	//port for sending responses
	ResultOutputPort<Message> getResponseRoutingPort();

	//port for broadcasting messages on the wire to clients
	RequestOutputPort<DbRequestMessage, DbResultMessage> getToDatabasePort();

	RequestOutputPort<VortexVaultRequest, VortexVaultResponse> getToVaultPort();

	RequestOutputPort<RunnableRequestMessage, TextMessage> getToRunnablePort();

	OutputPort<StartVortexEyeItineraryMessage> getStartItineraryOutputPort();

	//Conveniently send a message to all clients
	void sendToClients(TestTrackDeltas deltas);

	//Conveniently send a request to a single agent and send the response back on the response port
	MsgMessage sendRequestToAgent(VortexAgentRequest req, String processUid, OutputPort<?> responsePort, long delayMs);

	//Conveniently send a message to a single agent
	void sendToAgent(Message req, String processUid);

	String getProcessUidFromAgentResponse(ResultMessage<?> result);

	<M extends Message> void startItinerary(VortexEyeItinerary<M> itinerary, RequestMessage<M> action);

	void sendToDb(DbRequestMessage insertToDatabase);

}
