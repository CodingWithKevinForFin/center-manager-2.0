package com.vortex.eye.itinerary;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.povo.db.DbRequestMessage;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.vortexcommon.msg.agent.TestTrackDeltas;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRequest;
import com.vortex.eye.messages.VortexVaultRequest;

public interface VortexEyeItineraryWorker {

	//Conveniently send a message to all clients
	void sendToClients(VortexEyeItinerary<?> source, TestTrackDeltas deltas);

	//Conveniently send a request to a single agent and send the response back on the response port
	void sendRequestToAgent(VortexEyeItinerary<?> source, VortexAgentRequest req, String processUid);

	//Conveniently send a message to a single agent
	void sendToAgent(VortexEyeItinerary<?> source, Message req, String processUid);

	String getProcessUidFromAgentResponse(VortexEyeItinerary<?> source, ResultMessage<?> result);

	void sendToDb(VortexEyeItinerary<?> source, DbRequestMessage insertToDatabase);

	<M extends Message> void startItinerary(VortexEyeItinerary<?> source, VortexEyeItinerary<M> itinerary, M action);

	void sendToVault(VortexEyeItinerary<?> source, VortexVaultRequest vvq);

	void sendRunnable(VortexEyeItinerary<?> source, RunnableRequestMessage rm);
}