package com.f1.ami.center.table;

import java.util.logging.Logger;

import com.f1.ami.center.AbstractAmiCenterItinerary;
import com.f1.ami.center.AmiCenterItineraryWorker;
import com.f1.ami.center.hdb.events.AmiHdbRequest;
import com.f1.ami.center.hdb.events.AmiHdbResponse;
import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.utils.LH;

public class AmiCenterHdbItinerary extends AbstractAmiCenterItinerary<AmiHdbRequest> {

	private static final Logger log = LH.get();
	private Message result;

	@Override
	public byte startJourney(AmiCenterItineraryWorker worker) {
		AmiHdbRequest req = getInitialRequest().getAction();
		worker.sendHdbRequest(this, req);
		return STATUS_ACTIVE;
	}

	@Override
	public byte onResponse(ResultMessage<?> result, AmiCenterItineraryWorker worker) {
		this.result = (Message) result.getAction();
		return STATUS_COMPLETE;
	}

	@Override
	public Message endJourney(AmiCenterItineraryWorker worker) {
		return this.result;
	}

}
