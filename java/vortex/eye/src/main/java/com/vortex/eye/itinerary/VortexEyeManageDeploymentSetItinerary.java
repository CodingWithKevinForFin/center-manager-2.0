package com.vortex.eye.itinerary;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.povo.db.DbRequestMessage;
import com.f1.povo.db.DbResultMessage;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.VortexDeploymentSet;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDeploymentSetRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDeploymentSetResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeResponse;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.VortexEyeUtils;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeManageDeploymentSetItinerary extends AbstractVortexEyeItinerary<VortexEyeManageDeploymentSetRequest> {

	private static final byte STEP1_SEND_DEPLOYMENTSET_TO_DB = 1;

	private VortexDeploymentSet old, nuw;
	private VortexEyeManageDeploymentSetResponse r;
	private boolean isDelete;
	private byte step;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		r = getState().nw(VortexEyeManageDeploymentSetResponse.class);
		long now = getState().getPartition().getContainer().getTools().getNow();
		nuw = getInitialRequest().getAction().getDeploymentSet();
		VortexEyeState state = getState();
		isDelete = nuw.getRevision() == VortexAgentEntity.REVISION_DONE;
		if (nuw.getId() != 0) {
			old = state.getDeploymentSet(nuw.getId());
			if (old == null) {
				r.setMessage("DeploymentSet not found for update / delete: " + nuw.getId());
				return STATUS_COMPLETE;
			}
			if (isDelete) {
				nuw = old.clone();
				nuw.setRevision(VortexEyeUtils.REVISION_DONE);
			} else {
				if (!VortexEyeManageMetadataFieldItinerary.validateMetadata(nuw, state, r))
					return STATUS_COMPLETE;
				nuw.setRevision(old.getRevision() + 1);
			}
		} else {
			if (!VortexEyeManageMetadataFieldItinerary.validateMetadata(nuw, state, r))
				return STATUS_COMPLETE;
			nuw.setId(getState().createNextId());
			nuw.setRevision(0);
		}
		if (isDelete) {
			if (!validateDelete(old))
				return STATUS_COMPLETE;
		} else {
			if (!validateUnique(nuw, getState().getDeploymentSets(), r))
				return STATUS_COMPLETE;
		}
		nuw.setNow(now);
		nuw.lock();
		sendToDb(nuw, worker);
		step = STEP1_SEND_DEPLOYMENTSET_TO_DB;
		return STATUS_ACTIVE;
	}
	public static boolean validateUnique(VortexDeploymentSet node, Iterable<VortexDeploymentSet> existing, VortexEyeResponse r) {
		for (VortexDeploymentSet dep : existing) {
			if (dep.getId() != node.getId() && OH.eq(dep.getName(), node.getName())) {
				r.setMessage("Deployment Set name must be unique: " + node.getName());
				return false;
			}
		}
		return true;
	}
	private boolean validateDelete(VortexDeploymentSet node) {
		node.getId();
		int cnt = 0;
		for (VortexDeployment dep : getState().getDeployments())
			if (dep.getDeploymentSetId() == node.getId())
				cnt++;
		if (cnt > 0) {
			r.setMessage("Can not delete deployment set with " + cnt + " active deployments");
			return false;
		}
		return true;
	}
	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		switch (step) {
			case STEP1_SEND_DEPLOYMENTSET_TO_DB: {
				DbResultMessage dbResult = (DbResultMessage) result.getAction();
				if (!dbResult.getOk()) {
					r.setMessage(dbResult.getMessage());
				} else {
					if (isDelete)
						getState().removeDeploymentSet(nuw.getId());
					else
						getState().addDeploymentSet(nuw);
					r.setOk(true);
				}
				return STATUS_COMPLETE;
			}
			default:
				throw new RuntimeException("unknown step: " + step);
		}
	}
	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		if (r.getOk()) {
			r.setDeploymentSet(nuw);
			VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
			cmb.writeTransition(old, nuw);
			worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));
		}
		return r;
	}

	public void sendToDb(VortexDeploymentSet deploymentSet, VortexEyeItineraryWorker worker) {
		boolean active = deploymentSet.getRevision() < VortexAgentEntity.REVISION_DONE;
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("active", active);
		params.put("id", deploymentSet.getId());
		params.put("revision", deploymentSet.getRevision());
		params.put("now", deploymentSet.getNow());
		params.put("name", deploymentSet.getName());
		params.put("properties", deploymentSet.getProperties());
		params.put("metadata", deploymentSet.getMetadata() == null ? null : SH.joinMap('|', '=', deploymentSet.getMetadata()));
		DbRequestMessage msg = getTools().nw(DbRequestMessage.class);
		msg.setId("insert_deployment_set");
		msg.setParams(params);
		worker.sendToDb(this, msg);
	}
	@Override
	protected void populateAuditEvent(VortexEyeManageDeploymentSetRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_MANAGE_DEPLOYMENT_SET);
		auditEntity(sink, "DSID", action.getDeploymentSet());
	}

}
