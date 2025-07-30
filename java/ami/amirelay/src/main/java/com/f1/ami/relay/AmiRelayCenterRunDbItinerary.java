package com.f1.ami.relay;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.ds.AmiDatasourceRunner;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse;
import com.f1.ami.amicommon.msg.AmiRelayRunDbRequest;
import com.f1.ami.amicommon.msg.AmiRelayRunDbResponse;
import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.povo.standard.RunnableResponseMessage;

public class AmiRelayCenterRunDbItinerary extends AmiRelayAbstractItinerary<AmiRelayRunDbRequest> {

	private AmiCenterQueryDsResponse r;
	private AmiRelayRunDbResponse response;
	private AmiDatasourceRunner runner;
	private RunnableRequestMessage runnable;

	@Override
	public byte startJourney(AmiRelayItineraryWorker worker) {
		final AmiRelayRunDbRequest req = getInitialRequest().getAction();
		this.response = nw(AmiRelayRunDbResponse.class);
		this.response.setOk(true);
		final AmiRelayState state = getState();

		AmiCenterQueryDsRequest clientRequest = req.getClientRequest();
		AmiCenterQueryDsRequest request = (AmiCenterQueryDsRequest) clientRequest;
		r = nw(AmiCenterQueryDsResponse.class);
		r.setOk(true);
		AmiDatasourceAdapter adapter = state.getDatasourceManager().createDatasourceAdapter(req.getDsAdapter(), response);
		if (!r.getOk())
			return STATUS_COMPLETE;
		this.runner = new AmiDatasourceRunner(getTools(), adapter, req, request, false, getState().getLogQueryMaxChars(), request.getTimeoutMs());
		this.response.setClientResponse(r);
		runnable = state.getDatasourceManager().createRunnableMessage(req, this.runner);
		if (!r.getOk())
			return STATUS_COMPLETE;
		worker.sendRunnable(this, this.runnable);
		return STATUS_ACTIVE;
	}
	@Override
	public byte onResponse(ResultMessage<?> result, AmiRelayItineraryWorker worker) {
		AmiUtils.fillResponse(((RunnableResponseMessage) result.getAction()), this.response);
		runner.fillResponse(r);
		return STATUS_COMPLETE;
	}
	@Override
	public Message endJourney(AmiRelayItineraryWorker worker) {
		return response;
	}

}
