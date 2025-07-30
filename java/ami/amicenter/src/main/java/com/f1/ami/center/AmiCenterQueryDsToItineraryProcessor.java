package com.f1.ami.center;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.base.Message;
import com.f1.container.RequestMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;

public class AmiCenterQueryDsToItineraryProcessor extends AmiCenterRequestProcessor<AmiCenterQueryDsRequest, State, Message> {

	public AmiCenterQueryDsToItineraryProcessor() {
		super(AmiCenterQueryDsRequest.class, State.class, Message.class);
	}

	@Override
	protected Message processRequest(RequestMessage<AmiCenterQueryDsRequest> req, State state, ThreadScope threadScope) throws Exception {
		AmiCenterQueryDsRequest action = req.getAction();
		AmiCenterItinerary<AmiCenterQueryDsRequest> itinerary = createQueryItinerary(action);
		startItinerary(itinerary, req);
		return null;
	}

	public static AmiCenterItinerary<AmiCenterQueryDsRequest> createQueryItinerary(AmiCenterQueryDsRequest action) {
		AmiCenterItinerary<AmiCenterQueryDsRequest> itinerary;
		if (action.getDatasourceOverrideUsername() == null && AmiConsts.DATASOURCE_NAME_AMI.equals(action.getDatasourceName())) {
			itinerary = new AmiCenterQueryAmiItinerary();
		} else {
			itinerary = new AmiCenterQueryDsItinerary();
		}
		return itinerary;
	}

}
