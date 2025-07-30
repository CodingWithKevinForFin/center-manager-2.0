package com.vortex.eye.itinerary;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.povo.db.DbResultMessage;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexBuildResult;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBuildResultRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBuildResultResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeResponse;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeManageBuildResultItinerary extends AbstractVortexEyeItinerary<VortexEyeManageBuildResultRequest> {

	private VortexBuildResult old;
	VortexBuildResult nuw;
	private VortexEyeManageBuildResultResponse r;
	private boolean isDelete;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		r = getState().nw(VortexEyeManageBuildResultResponse.class);
		long now = getState().getPartition().getContainer().getTools().getNow();
		nuw = getInitialRequest().getAction().getBuildResult();
		VortexEyeState state = getState();
		isDelete = nuw.getRevision() == VortexAgentEntity.REVISION_DONE;
		if (nuw.getId() != 0) {
			old = state.getBuildResult(nuw.getId());
			if (old == null) {
				r.setMessage("Build Result not found for update / delete: " + nuw.getId());
				return STATUS_COMPLETE;
			}
			if (isDelete) {
				if (!validateDelete(old, state, r))
					return STATUS_COMPLETE;
				nuw = old.clone();
				nuw.setRevision(VortexAgentEntity.REVISION_DONE);
			} else {
				if (!VortexEyeManageMetadataFieldItinerary.validateMetadata(nuw, state, r))
					return STATUS_COMPLETE;
				if (!validateUnique(nuw, state.getBuildResults(), r))
					return STATUS_COMPLETE;
				nuw.setRevision(old.getRevision() + 1);
			}
		} else {
			if (!VortexEyeManageMetadataFieldItinerary.validateMetadata(nuw, state, r))
				return STATUS_COMPLETE;
			nuw.setId(getState().createNextId());
			nuw.setRevision(0);
			if (!validateUnique(nuw, state.getBuildResults(), r))
				return STATUS_COMPLETE;
		}
		nuw.setNow(now);
		nuw.lock();
		VortexEyeRunBuildProcedureItinerary.insertBuildResult(nuw, worker, this);
		return STATUS_ACTIVE;
	}
	private boolean validateDelete(VortexBuildResult old, VortexEyeState state, VortexEyeManageBuildResultResponse r) {
		for (VortexDeployment deployment : state.getDeployments())
			if (deployment.getCurrentBuildResultId() != null && deployment.getCurrentBuildResultId() == old.getId()) {
				r.setMessage("Can not delete build result BR-" + old.getId() + " because it is actively deployed");
				return false;
			}
		return true;

	}
	public static boolean validateUnique(VortexBuildResult nuw, Iterable<VortexBuildResult> existings, VortexEyeResponse r) {
		return true;
	}
	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		DbResultMessage dbResult = (DbResultMessage) result.getAction();
		if (!dbResult.getOk()) {
			r.setMessage(dbResult.getMessage());
		} else {
			if (isDelete)
				getState().removeBuildResult(nuw.getId());
			else
				getState().addBuildResult(nuw);
			r.setOk(true);
		}
		return STATUS_COMPLETE;
	}

	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		if (r.getOk()) {
			r.setBuildResult(nuw);
			VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
			cmb.writeTransition(old, nuw);
			worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));
		}
		return r;
	}
	@Override
	protected void populateAuditEvent(VortexEyeManageBuildResultRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_MANAGE_BUILD_RESULT);
		auditEntity(sink, "BRID", action.getBuildResult());
	}

}
