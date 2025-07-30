package com.f1.ami.center;

import java.io.File;
import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiCenterManageResourcesRequest;
import com.f1.ami.amicommon.msg.AmiCenterManagerResourceResponse;
import com.f1.ami.amicommon.msg.AmiCenterResource;
import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;

public class AmiCenterManagerResourcesItinerary extends AbstractAmiCenterItinerary<AmiCenterManageResourcesRequest> {

	private static final Logger log = LH.get();

	private AmiCenterManagerResourceResponse r;

	@Override
	public byte startJourney(AmiCenterItineraryWorker worker) {
		AmiCenterState state = getState();
		r = state.nw(AmiCenterManagerResourceResponse.class);
		state.incrementAmiMessageStats(AmiCenterState.STATUS_TYPE_QUERY_DATASOURCE);
		File resourceRoot = getState().getResourcesManager().getRoot();
		try {
			AmiCenterManageResourcesRequest request = getInitialRequest().getAction();
			AmiCenterResource resource = request.getResource();
			File file = new File(resourceRoot, resource.getPath());
			if (resource.getData() == null) {
				if (file.isFile())
					file.delete();
			} else {
				IOH.ensureDir(file.getParentFile());
				IOH.writeData(file, resource.getData());
			}
			r.setResources(CH.l(resource));
			state.getResourcesManager().wakeup();
			return STATUS_COMPLETE;
		} catch (Exception e) {
			String ticket = getTools().generateErrorTicket();
			r.setTicket(ticket);
			LH.warning(log, "Error processing request:", getInitialRequest(), e);
			r.setMessage(e.getMessage());
			r.setException(e);
			return STATUS_COMPLETE;
		}
	}
	@Override
	public byte onResponse(ResultMessage<?> result, AmiCenterItineraryWorker worker) {
		return STATUS_COMPLETE;
	}

	@Override
	public Message endJourney(AmiCenterItineraryWorker worker) {
		return r;
	}

}
