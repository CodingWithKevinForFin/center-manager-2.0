package com.vortex.agent.itinerary;

import com.f1.base.Message;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.povo.f1app.reqres.F1AppRequest;
import com.f1.povo.standard.RunnableRequestMessage;
import com.vortex.agent.messages.VortexAgentDeploymentUpdateMessage;
import com.vortex.agent.messages.VortexAgentOsAdapterRequest;

public interface VortexAgentItineraryWorker {

	void sendToEye(VortexAgentItinerary<?> source, Message deltas);

	void sendRequestToF1App(VortexAgentItinerary<?> source, F1AppRequest req, String processUid);

	void sendToF1App(VortexAgentItinerary<?> source, Message req, String processUid);

	String getProcessUidFromF1AppResponse(ResultMessage<?> result);

	<M extends Message> void startItinerary(VortexAgentItinerary<?> source, VortexAgentItinerary<M> itinerary, RequestMessage<M> action);

	void replyWithIntermediateResult(VortexAgentItinerary<?> source, Message m);

	void sendRequestToOsAdapter(VortexAgentItinerary<?> source, VortexAgentOsAdapterRequest req);

	void sendToDeployment(VortexAgentItinerary vortexAgentItinerary, VortexAgentDeploymentUpdateMessage updmsg);

	void sendRunnable(VortexAgentItinerary iterary, RunnableRequestMessage rm);
}