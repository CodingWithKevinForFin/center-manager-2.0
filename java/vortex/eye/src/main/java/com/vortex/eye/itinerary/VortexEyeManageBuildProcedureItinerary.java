package com.vortex.eye.itinerary;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.povo.db.DbRequestMessage;
import com.f1.povo.db.DbResultMessage;
import com.f1.utils.OH;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexBuildProcedure;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBuildProcedureRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBuildProcedureResponse;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.VortexEyeUtils;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeManageBuildProcedureItinerary extends AbstractVortexEyeItinerary<VortexEyeManageBuildProcedureRequest> {

	private VortexBuildProcedure old, nuw;
	private VortexEyeManageBuildProcedureResponse r;
	private boolean isDelete;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		r = getState().nw(VortexEyeManageBuildProcedureResponse.class);
		long now = getState().getPartition().getContainer().getTools().getNow();
		nuw = getInitialRequest().getAction().getBuildProcedure();
		VortexEyeState state = getState();
		isDelete = nuw.getRevision() == VortexAgentEntity.REVISION_DONE;
		if (nuw.getId() != 0) {
			old = state.getBuildProcedure(nuw.getId());
			if (old == null) {
				r.setMessage("BuildProcedure not found for update / delete: " + nuw.getId());
				return STATUS_COMPLETE;
			}
			if (isDelete) {
				int cnt = 0;
				for (VortexDeployment dep : state.getDeployments())
					if (dep.getProcedureId() == old.getId())
						cnt++;
				if (cnt > 0) {
					r.setMessage("Build Procedure has " + cnt + " existing Deployment(s)");
					return STATUS_COMPLETE;
				}
				nuw = old.clone();
				nuw.setRevision(VortexEyeUtils.REVISION_DONE);
			} else {
				if (!VortexEyeManageMetadataFieldItinerary.validateMetadata(nuw, state, r))
					return STATUS_COMPLETE;
				String msg = isDup(nuw, state.getBuildProcedures());
				if (msg != null) {
					r.setMessage(msg);
					return STATUS_COMPLETE;
				}
				nuw.setRevision(old.getRevision() + 1);
			}
		} else {
			if (!VortexEyeManageMetadataFieldItinerary.validateMetadata(nuw, state, r))
				return STATUS_COMPLETE;
			nuw.setId(getState().createNextId());
			nuw.setRevision(0);
			String msg = isDup(nuw, state.getBuildProcedures());
			if (msg != null) {
				r.setMessage(msg);
				return STATUS_COMPLETE;
			}
		}
		nuw.setNow(now);
		nuw.lock();
		sendToDb(nuw, worker);
		return STATUS_ACTIVE;
	}
	private String isDup(VortexBuildProcedure nuw, Iterable<VortexBuildProcedure> bps) {
		for (VortexBuildProcedure existing : bps) {
			if (existing.getId() == nuw.getId())
				continue;
			if (OH.eq(existing.getName(), nuw.getName()))
				return "Duplicate build procedure name: " + existing.getName();
		}
		return null;
	}
	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		DbResultMessage dbResult = (DbResultMessage) result.getAction();
		if (!dbResult.getOk()) {
			r.setMessage(dbResult.getMessage());
		} else {
			if (isDelete)
				getState().removeBuildProcedure(nuw.getId());
			else
				getState().addBuildProcedure(nuw);
			r.setOk(true);
		}
		return STATUS_COMPLETE;
	}

	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		if (r.getOk()) {
			r.setBuildProcedure(nuw);
			VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
			cmb.writeTransition(old, nuw);
			worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));
		}
		return r;
	}

	public void sendToDb(VortexBuildProcedure buildProcedure, VortexEyeItineraryWorker worker) {
		boolean active = buildProcedure.getRevision() < VortexAgentEntity.REVISION_DONE;
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("active", active);
		params.put("id", buildProcedure.getId());
		params.put("revision", buildProcedure.getRevision());
		params.put("now", buildProcedure.getNow());
		params.put("name", buildProcedure.getName());
		params.put("build_machine_uid", buildProcedure.getBuildMachineUid());
		params.put("template_user", buildProcedure.getTemplateUser());
		params.put("template_command", buildProcedure.getTemplateCommand());
		params.put("template_stdin", buildProcedure.getTemplateStdin());
		params.put("template_result_file", buildProcedure.getTemplateResultFile());
		params.put("template_result_verify_file", buildProcedure.getTemplateResultVerifyFile());
		params.put("template_result_name", buildProcedure.getTemplateResultName());
		params.put("template_result_version", buildProcedure.getTemplateResultVersion());
		params.put("metadata", buildProcedure.getMetadata() == null ? null : VortexEyeUtils.joinMap(buildProcedure.getMetadata()));
		DbRequestMessage msg = getTools().nw(DbRequestMessage.class);
		msg.setId("insert_build_procedure");
		msg.setParams(params);
		worker.sendToDb(this, msg);
	}
	@Override
	protected void populateAuditEvent(VortexEyeManageBuildProcedureRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_MANAGE_BUILD_PROCEDURE);
		auditEntity(sink, "BPID", action.getBuildProcedure());
	}

}
