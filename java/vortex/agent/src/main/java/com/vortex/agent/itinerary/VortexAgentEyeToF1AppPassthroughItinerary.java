package com.vortex.agent.itinerary;

import com.f1.base.Message;
import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.base.ValuedSchema;
import com.f1.container.ResultMessage;
import com.f1.povo.f1app.reqres.F1AppInspectPartitionRequest;
import com.f1.povo.f1app.reqres.F1AppInterruptThreadRequest;
import com.f1.povo.f1app.reqres.F1AppRequest;
import com.f1.povo.f1app.reqres.F1AppResponse;
import com.f1.utils.OH;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentPassToF1AppRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentPassToF1AppResponse;
import com.vortex.agent.state.VortexAgentF1AppState;

public class VortexAgentEyeToF1AppPassthroughItinerary extends AbstractVortexAgentItinerary<VortexAgentPassToF1AppRequest> {

	private VortexAgentPassToF1AppResponse response;

	@Override
	public byte startJourney(VortexAgentItineraryWorker worker) {
		final VortexAgentPassToF1AppRequest request = getInitialRequest().getAction();
		response = getTools().nw(VortexAgentPassToF1AppResponse.class);
		String puid = request.getF1AppRequest().getTargetF1AppProcessUid();
		VortexAgentF1AppState agent = getState().getF1AppByProcessUidNoThrow(puid);
		if (agent == null) {
			response.setMessage("f1 app not found: " + puid);
			return STATUS_COMPLETE;
		}
		String message = mapIds(agent, request.getF1AppRequest());
		if (message != null) {
			response.setMessage("f1 app not found: " + puid);
			return STATUS_COMPLETE;
		}
		final String f1ProcessUid = agent.getF1AppInstance().getProcessUid();
		worker.sendRequestToF1App(this, request.getF1AppRequest(), f1ProcessUid);
		return STATUS_ACTIVE;
	}

	@Override
	public byte onResponse(ResultMessage<?> result, VortexAgentItineraryWorker worker) {
		F1AppResponse appResponse = (F1AppResponse) result.getAction();
		response.setF1AppResponse(appResponse);
		if (appResponse == null)
			response.setMessage("F1 App sent empty response");
		else if (!appResponse.getOk())
			response.setMessage(appResponse.getMessage());
		else
			response.setOk(true);
		return STATUS_COMPLETE;
	}
	@Override
	public Message endJourney(VortexAgentItineraryWorker worker) {
		return response;
	}

	private String mapIds(VortexAgentF1AppState f1App, F1AppRequest f1AppRequest) {
		final byte pidsToMap[];
		if (f1AppRequest instanceof F1AppInterruptThreadRequest)
			pidsToMap = new byte[] { F1AppInterruptThreadRequest.PID_THREAD_MONITOR_ID };
		else if (f1AppRequest instanceof F1AppInspectPartitionRequest)
			pidsToMap = new byte[] { F1AppInspectPartitionRequest.PID_PARTITION_ID };
		else
			pidsToMap = OH.EMPTY_BYTE_ARRAY;

		for (final byte pid : pidsToMap) {
			ValuedSchema<Valued> schema = f1AppRequest.askSchema();
			ValuedParam<Valued> vp = schema.askValuedParam(pid);
			long val = (Long) vp.getLong(f1AppRequest);
			if (val != 0) {
				final Long origId = f1App.getOrigIdById(val);
				if (origId == null)
					return "In Agent, child not found for param " + f1AppRequest.getClass().getName() + "." + vp.getName() + ": " + val;
				vp.setLong(f1AppRequest, origId);
			}
		}
		return null;
	}
}
