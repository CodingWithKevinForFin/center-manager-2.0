package com.vortex.eye.itinerary;

import java.util.Map;

import com.f1.base.Message;
import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.base.ValuedSchema;
import com.f1.container.ResultMessage;
import com.f1.povo.f1app.reqres.F1AppChangeLogLevelRequest;
import com.f1.povo.f1app.reqres.F1AppInspectPartitionRequest;
import com.f1.povo.f1app.reqres.F1AppInterruptThreadRequest;
import com.f1.povo.f1app.reqres.F1AppRequest;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentPassToF1AppRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentPassToF1AppResponse;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyePassToF1AppRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyePassToF1AppResponse;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.state.VortexEyeF1AppState;

public class VortexEyeClientToAgentForF1AppPassthroughItinerary extends AbstractVortexEyeItinerary<VortexEyePassToF1AppRequest> {

	private VortexEyePassToF1AppResponse response;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		final VortexEyePassToF1AppRequest request = getInitialRequest().getAction();
		response = getTools().nw(VortexEyePassToF1AppResponse.class);
		VortexEyeF1AppState f1App = getState().getF1SnapshotByProcessId(request.getF1AppId());
		if (SH.isnt(request.getInvokedBy())) {
			response.setMessage("invoked by field required");
			return STATUS_COMPLETE;
		}
		if (f1App == null || f1App.getF1AppInstance().getProcessUid() == null) {
			response.setMessage("Vortex Agent not found for java process: " + request.getF1AppId());
			return STATUS_COMPLETE;
		}
		if (OH.ne(request.getF1AppRequest().getTargetF1AppProcessUid(), f1App.getF1AppInstance().getProcessUid())) {
			response.setMessage("ProcessUid mismatch: " + request.getF1AppRequest().getTargetF1AppProcessUid() + " vs. " + f1App.getF1AppInstance().getProcessUid());
			return STATUS_COMPLETE;
		}
		VortexAgentPassToF1AppRequest toAgentRequest = getTools().nw(VortexAgentPassToF1AppRequest.class);
		toAgentRequest.setF1AppId(f1App.getOrigId());
		toAgentRequest.setInvokedBy(request.getInvokedBy());
		String errorMessage = mapIds(f1App, request.getF1AppRequest());
		if (errorMessage != null) {
			response.setMessage(errorMessage);
			return STATUS_COMPLETE;
		}
		toAgentRequest.setF1AppRequest(request.getF1AppRequest());
		worker.sendRequestToAgent(this, toAgentRequest, f1App.getAgentState().getProcessUid());
		return STATUS_ACTIVE;
	}
	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		VortexAgentPassToF1AppResponse fromAgentResponse = (VortexAgentPassToF1AppResponse) result.getAction();
		response = getTools().nw(VortexEyePassToF1AppResponse.class);
		response.setF1AppResponse(fromAgentResponse.getF1AppResponse());
		response.setMessage(fromAgentResponse.getMessage());
		response.setOk(fromAgentResponse.getOk());
		result.getAction();
		return STATUS_COMPLETE;
	}

	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		return response;
	}

	private String mapIds(VortexEyeF1AppState f1App, F1AppRequest f1AppRequest) {
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
			if (val > 0) {
				final Long origId = f1App.getOrigIdById(val);
				if (origId == null)
					return "In eye, child not found for param " + f1AppRequest.getClass().getName() + "." + vp.getName() + ": " + val;
				vp.setLong(f1AppRequest, origId);
			}
		}
		return null;
	}
	@Override
	protected void populateAuditEvent(VortexEyePassToF1AppRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		VortexEyeF1AppState f1App = getState().getF1SnapshotByProcessId(action.getF1AppId());

		if (f1App != null) {
			sink.setTargetMachineUid(CH.first(f1App.getAgentState().getMachineMuids()));//TODO: this is bad
		}
		F1AppRequest req = action.getF1AppRequest();
		Map<String, String> params = sink.getParams();
		sink.getParams().put("APID", SH.toString(action.getF1AppId()));
		sink.getParams().put("APUID", req.getTargetF1AppProcessUid());
		if (req instanceof F1AppChangeLogLevelRequest) {
			sink.setEventType(VortexEyeClientEvent.TYPE_APP_CHANGE_LOG_LEVEL);
			F1AppChangeLogLevelRequest req2 = (F1AppChangeLogLevelRequest) req;
			params.put("LVL", SH.toString(req2.getLevel()));
			params.put("LGRID", SH.toString(req2.getLoggerIds()));
			params.put("SNKID", SH.toString(req2.getSinkIds()));
		} else if (req instanceof F1AppInspectPartitionRequest) {
			sink.setEventType(VortexEyeClientEvent.TYPE_APP_INSPECT_PARTITION);
			F1AppInspectPartitionRequest req2 = (F1AppInspectPartitionRequest) req;
			params.put("PARTID", SH.toString(req2.getPartitionId()));
			params.put("TIMEOUTMS", SH.toString(req2.getTimeoutMs()));
		} else if (req instanceof F1AppInterruptThreadRequest) {
			sink.setEventType(VortexEyeClientEvent.TYPE_APP_INTERRUPT_THREAD);
			F1AppInterruptThreadRequest req2 = (F1AppInterruptThreadRequest) req;
			params.put("PROCCNT", SH.toString(req2.getProcessedEventsCount()));
			params.put("PROCID", SH.toString(req2.getProcessorMonitorId()));
			params.put("THREADID", SH.toString(req2.getThreadMonitorId()));
		} else
			throw new RuntimeException("can't audit: " + req);
	}
}
