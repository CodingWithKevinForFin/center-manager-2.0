package com.vortex.eye.itinerary;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.utils.AH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileDeleteRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileSearchRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentResponse;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunDeploymentRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunOsCommandRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunSignalProcessRequest;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyePassToAgentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyePassToAgentResponse;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.state.VortexEyeMachineState;

public class VortexEyeClientToAgentPassthroughItinerary extends AbstractVortexEyeItinerary<VortexEyePassToAgentRequest> {

	private static final Logger log = LH.get(VortexEyeClientToAgentPassthroughItinerary.class);
	private VortexEyePassToAgentResponse response;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		VortexEyePassToAgentRequest request = getInitialRequest().getAction();
		response = getTools().nw(VortexEyePassToAgentResponse.class);
		final VortexEyeMachineState machine = getState().getMachineByMuidNoThrow(request.getAgentMachineUid());
		if (machine == null || machine.getAgentState() == null) {
			response.setMessage("machine not found: " + request.getAgentMachineUid());
			return STATUS_COMPLETE;
		}
		if (log.isLoggable(Level.INFO))
			LH.info(log, "Client to agent: ", OH.getSimpleClassName(request.getAgentRequest()), " ==> ", machine.getMuid());
		final String agentProcessUid = machine.getAgentState().getProcessUid();
		if (agentProcessUid == null) {
			response.setMessage("machine not running: " + machine.getRemoteHost());
			return STATUS_COMPLETE;
		}
		String reqProcessUid = request.getAgentProcessUid();
		if (reqProcessUid != null && OH.ne(agentProcessUid, reqProcessUid)) {
			response.setMessage("inconsistent processUid: " + agentProcessUid + " != " + reqProcessUid);
			return STATUS_COMPLETE;
		}
		request.setAgentProcessUid(agentProcessUid);
		request.getAgentRequest().setInvokedBy(request.getInvokedBy());
		worker.sendRequestToAgent(this, request.getAgentRequest(), agentProcessUid);
		return STATUS_ACTIVE;
	}

	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		response = getState().nw(VortexEyePassToAgentResponse.class);
		final VortexAgentResponse agentResponse = (VortexAgentResponse) result.getAction();
		response.setAgentResponse(agentResponse);
		response.setMessage(agentResponse.getMessage());
		response.setOk(agentResponse.getOk());
		return STATUS_COMPLETE;
	}

	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		return response;
	}

	@Override
	protected void populateAuditEvent(VortexEyePassToAgentRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		VortexAgentRequest request = action.getAgentRequest();
		VortexEyeMachineState machine = getState().getMachineByMuidNoThrow(action.getAgentMachineUid());
		sink.setTargetMachineUid(action.getAgentMachineUid());
		if (request instanceof VortexAgentRunSignalProcessRequest) {
			VortexAgentRunSignalProcessRequest req2 = (VortexAgentRunSignalProcessRequest) request;
			sink.setEventType(VortexEyeClientEvent.TYPE_RUN_SIGNAL_ON_PROCESS);
			sink.getParams().put("OWNER", req2.getProcessOwner());
			sink.getParams().put("PID", req2.getProcessPid());
			sink.getParams().put("STIME", SH.toString(req2.getProcessStartTime()));
		} else if (request instanceof VortexAgentRunDeploymentRequest) {
			VortexAgentRunDeploymentRequest req2 = (VortexAgentRunDeploymentRequest) request;
			sink.getParams().put("DPID", SH.toString(req2.getDeploymentId()));
			if (req2.getDeployedInstanceId() != 0l)
				sink.getParams().put("DIID", SH.toString(req2.getDeployedInstanceId()));
			switch (req2.getCommandType()) {
				case VortexAgentRunDeploymentRequest.TYPE_DELETE_ALL_FILES:
					sink.setEventType(VortexEyeClientEvent.TYPE_DEPLOYMENT_DELETE_ALL_FILES);
					break;
				case VortexAgentRunDeploymentRequest.TYPE_DEPLOY:
					sink.setEventType(VortexEyeClientEvent.TYPE_DEPLOYMENT_DEPLOY);
					break;
				//case VortexAgentRunDeploymentRequest.TYPE_GET_FILE:
				//sink.setEventType(VortexEyeClientEvent.TYPE_DEPLOYMENT_GET_FILE);
				//sink.getParams().put("FILE", req2.getDataFileName());
				//break;
				//case VortexAgentRunDeploymentRequest.TYPE_GET_FILE_STRUCTURE:
				//sink.setEventType(VortexEyeClientEvent.TYPE_DEPLOYMENT_GET_FILE_STRUCTURE);
				//auditString(sink, "FILE", req2.getTargetFile());
				//break;
				case VortexAgentRunDeploymentRequest.TYPE_RUN_SCRIPT:
					sink.setEventType(VortexEyeClientEvent.TYPE_DEPLOYMENT_RUN_SCRIPT);
					auditString(sink, "FILE", req2.getTargetFile());
					break;
				case VortexAgentRunDeploymentRequest.TYPE_START_SCRIPT:
					sink.setEventType(VortexEyeClientEvent.TYPE_DEPLOYMENT_START_SCRIPT);
					break;
				case VortexAgentRunDeploymentRequest.TYPE_STOP_SCRIPT:
					sink.setEventType(VortexEyeClientEvent.TYPE_DEPLOYMENT_STOP_SCRIPT);
					break;
			}
		} else if (request instanceof VortexAgentRunOsCommandRequest) {
			VortexAgentRunOsCommandRequest req2 = (VortexAgentRunOsCommandRequest) request;
			sink.setEventType(VortexEyeClientEvent.TYPE_RUN_OS_COMMAND);
			byte[] stdin = req2.getStdin();
			if (AH.isntEmpty(stdin)) {
				auditString(sink, "STDIN", new String(stdin.length > 2000 ? AH.subarray(stdin, 0, 2000) : stdin));
			}
			auditString(sink, "CMD", req2.getCommand());
			auditString(sink, "PWD", req2.getPwd());
		} else if (request instanceof VortexAgentFileDeleteRequest) {
			VortexAgentFileDeleteRequest req2 = (VortexAgentFileDeleteRequest) request;
			sink.setEventType(VortexEyeClientEvent.TYPE_DELETE_FILES);
			auditList(sink, "PATHS", req2.getFiles());
			auditString(sink, "PERMANENT", SH.toString(req2.getIsPermanent()));
		} else if (request instanceof VortexAgentFileSearchRequest) {
			VortexAgentFileSearchRequest req2 = (VortexAgentFileSearchRequest) request;
			sink.setEventType(VortexEyeClientEvent.TYPE_GET_FILES);
			sink.getParams().put("RECURSE", SH.toString(req2.getRecurse()));
			auditString(sink, "DATAEXP", req2.getIncludeDataExpression());
			auditString(sink, "CSUMEXP", req2.getIncludeChecksumExpression());
			auditList(sink, "PATHS", req2.getRootPaths());
		} else
			throw new RuntimeException("don't know how to audit: " + request);

	}

	private void auditString(VortexEyeClientEvent sink, String key, String val) {
		if (val == null)
			return;
		sink.getParams().put(key, SH.quote(val));
	}
}
