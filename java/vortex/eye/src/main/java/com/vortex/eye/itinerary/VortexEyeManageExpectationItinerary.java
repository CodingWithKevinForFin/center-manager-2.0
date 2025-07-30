package com.vortex.eye.itinerary;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.povo.db.DbRequestMessage;
import com.f1.povo.db.DbResultMessage;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexExpectation;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageExpectationRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageExpectationResponse;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.VortexEyeUtils;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeManageExpectationItinerary extends AbstractVortexEyeItinerary<VortexEyeManageExpectationRequest> {

	private VortexExpectation old, nuw;
	private VortexEyeManageExpectationResponse r;
	private boolean isDelete;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		r = getState().nw(VortexEyeManageExpectationResponse.class);
		long now = getState().getPartition().getContainer().getTools().getNow();
		nuw = getInitialRequest().getAction().getExpectation();
		VortexEyeState state = getState();
		isDelete = nuw.getRevision() == VortexAgentEntity.REVISION_DONE;
		if (nuw.getId() != 0) {
			old = state.getExpectation(nuw.getId());
			if (old == null) {
				r.setMessage("Expectation not found for update / delete: " + nuw.getId());
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
		nuw.setNow(now);
		nuw.lock();
		sendToDb(nuw, worker);
		return STATUS_ACTIVE;
	}
	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		DbResultMessage dbResult = (DbResultMessage) result.getAction();
		if (!dbResult.getOk()) {
			r.setMessage(dbResult.getMessage());
		} else {
			if (isDelete)
				getState().removeExpectation(nuw.getId());
			else
				getState().addExpectation(nuw);
			r.setOk(true);
		}
		return STATUS_COMPLETE;
	}

	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		if (r.getOk()) {
			r.setExpectation(nuw);
			VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
			cmb.writeTransition(old, nuw);
			worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));
		}
		return r;
	}

	public void sendToDb(VortexExpectation exp, VortexEyeItineraryWorker worker) {
		final Map<Object, Object> params = new HashMap<Object, Object>();
		boolean active = exp.getRevision() < VortexAgentEntity.REVISION_DONE;
		params.put("now", exp.getNow());
		params.put("active", active);
		params.put("id", exp.getId());
		params.put("revision", exp.getRevision());
		params.put("target_type", exp.getTargetType());
		params.put("name", exp.getName());
		params.put("machine_uid", exp.getMachineUid());
		params.put("field_masks", getTools().getServices().getJsonConverter().objectToString(exp.getFieldMasks()));
		params.put("tolerances", getTools().getServices().getJsonConverter().objectToString(exp.getTolerances()));
		params.put("target_metadata", getTools().getServices().getJsonConverter().objectToString(exp.getTargetMetadata()));
		params.put("metadata", exp.getMetadata() == null ? null : VortexEyeUtils.joinMap(exp.getMetadata()));

		DbRequestMessage msg = getTools().nw(DbRequestMessage.class);
		msg.setId("insert_expectation");
		msg.setParams(params);
		worker.sendToDb(this, msg);
	}
	@Override
	protected void populateAuditEvent(VortexEyeManageExpectationRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_MANAGE_EXPECTATION);
		auditEntity(sink, "XPID", action.getExpectation());
	}

}
