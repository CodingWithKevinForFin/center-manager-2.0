package com.vortex.eye.itinerary;

import java.util.List;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.VortexEyeCloudInterface;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunCloudInterfaceActionRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunCloudInterfaceActionResponse;
import com.vortex.eye.cloud.AmazonAdapter;
import com.vortex.eye.cloud.CloudAdapter;
import com.vortex.eye.cloud.CloudRunnable;
import com.vortex.eye.cloud.RackspaceAdapter;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;

public class VortexEyeRunCloudInterfaceActionItinerary extends AbstractVortexEyeItinerary<VortexEyeRunCloudInterfaceActionRequest> {

	private VortexEyeRunCloudInterfaceActionResponse r;
	private CloudRunnable cloudRunner;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		r = getState().nw(VortexEyeRunCloudInterfaceActionResponse.class);
		long now = getState().getPartition().getContainer().getTools().getNow();
		VortexEyeCloudInterface ci = getState().getCloudInterface(getInitialRequest().getAction().getCloudInterfaceId());
		this.cloudRunner = new CloudRunnable(getCloudAdapter(ci.getCloudVendorType()), ci, CloudRunnable.ACTION_GET_MACHINES_IN_CLOUD);
		RunnableRequestMessage rm = getState().nw(RunnableRequestMessage.class);
		rm.setPartitionId("CI-" + ci.getId());
		rm.setRunnable(this.cloudRunner);
		rm.setTimeoutMs(30000);
		worker.sendRunnable(this, rm);
		return STATUS_ACTIVE;
	}

	public static CloudAdapter getCloudAdapter(short cloudVendorType) {
		switch (cloudVendorType) {
			case VortexEyeCloudInterface.VENDOR_AMAZON_AWS:
				return new AmazonAdapter();
			case VortexEyeCloudInterface.VENDOR_RACKSPACE:
				return new RackspaceAdapter();
			default:
				return null;
		}
	}

	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		if (this.cloudRunner.getSuccess()) {
			r.setOk(true);
			r.setValues((List<String>) this.cloudRunner.getResults());
		} else {
			r.setMessage("Failed:\n" + cloudRunner.getMessage());
			r.setOk(false);
		}
		return STATUS_COMPLETE;
	}
	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		return r;
	}

	@Override
	protected void populateAuditEvent(VortexEyeRunCloudInterfaceActionRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_MANAGE_CLOUD_INTERFACE);
		sink.getParams().put("CIID", SH.toString(action.getCloudInterfaceId()));
		sink.getParams().put("ACTION", SH.toString(action.getAction()));

	}

}
