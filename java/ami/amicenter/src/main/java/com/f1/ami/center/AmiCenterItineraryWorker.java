package com.f1.ami.center;

import com.f1.ami.amicommon.msg.AmiCenterChangesMessage;
import com.f1.ami.amicommon.msg.AmiRelayRequest;
import com.f1.ami.center.hdb.events.AmiHdbRequest;
import com.f1.base.Message;
import com.f1.povo.standard.RunnableRequestMessage;

public interface AmiCenterItineraryWorker {

	//Conveniently send a message to all clients
	void sendToClients(AmiCenterItinerary<?> source, AmiCenterChangesMessage deltas);

	//Conveniently send a request to a single agent and send the response back on the response port
	void sendRequestToAgent(AmiCenterItinerary<?> source, AmiRelayRequest req, String processUid);

	<M extends Message> void startItinerary(AmiCenterItinerary<?> source, AmiCenterItinerary<M> itinerary, M action);

	void sendRunnable(AmiCenterItinerary<?> source, RunnableRequestMessage rm);

	void sendHdbRequest(AmiCenterItinerary<?> itinerary, AmiHdbRequest rm);
}
