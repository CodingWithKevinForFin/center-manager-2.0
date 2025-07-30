package com.vortex.eye.itinerary;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.povo.db.DbRequestMessage;
import com.f1.povo.db.DbResultMessage;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentUpdateDeploymentRequest;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDeploymentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDeploymentResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeResponse;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.VortexEyeUtils;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.state.VortexEyeMachineState;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeManageDeploymentItinerary extends AbstractVortexEyeItinerary<VortexEyeManageDeploymentRequest> {

	private static final int STEP1_INSERT_TO_DB = 1;
	private static final int STEP2_UPDATE_BACKUPS = 2;
	private VortexDeployment old, nuw;
	private VortexEyeManageDeploymentResponse r;
	private boolean isDelete;
	private int step;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		r = getState().nw(VortexEyeManageDeploymentResponse.class);
		long now = getState().getPartition().getContainer().getTools().getNow();
		nuw = getInitialRequest().getAction().getDeployment();
		VortexEyeState state = getState();
		isDelete = nuw.getRevision() == VortexAgentEntity.REVISION_DONE;
		if (nuw.getId() != 0) {
			old = state.getDeployment(nuw.getId());
			if (old == null) {
				r.setMessage("Deployment not found for update / delete: " + nuw.getId());
				return STATUS_COMPLETE;
			}
			if (isDelete) {
				if (!validateDelete(old, getState(), r))
					return STATUS_COMPLETE;
				nuw = old.clone();
				nuw.setRevision(VortexAgentEntity.REVISION_DONE);
			} else {
				if (!validateUnique(nuw, getState().getDeployments(), r))
					return STATUS_COMPLETE;
				if (!VortexEyeManageMetadataFieldItinerary.validateMetadata(nuw, state, r))
					return STATUS_COMPLETE;
				nuw.setRevision(old.getRevision() + 1);
				nuw.setCurrentBuildResultId(old.getCurrentBuildResultId());
				nuw.setGeneratedPropertiesFile(old.getGeneratedPropertiesFile());
				nuw.setStatus(old.getStatus());
				nuw.setRunningPid(old.getRunningPid());
				nuw.setRunningProcessUid(old.getRunningProcessUid());
				nuw.setDeployedInstanceId(old.getDeployedInstanceId());
				nuw.setScriptsFound(old.getScriptsFound());
			}
		} else {
			if (!VortexEyeManageMetadataFieldItinerary.validateMetadata(nuw, state, r))
				return STATUS_COMPLETE;
			if (!validateUnique(nuw, getState().getDeployments(), r))
				return STATUS_COMPLETE;
			nuw.setId(getState().createNextId());
			nuw.setRevision(0);
		}
		nuw.setNow(now);
		nuw.setCurrentBuildInvokedBy(getInitialRequest().getAction().getInvokedBy());
		nuw.lock();
		this.step = STEP1_INSERT_TO_DB;
		sendToDb(nuw, worker, this);
		return STATUS_ACTIVE;
	}
	private boolean validateDelete(VortexDeployment old, VortexEyeState state, VortexEyeManageDeploymentResponse r) {
		for (VortexEyeBackup backup : getState().getBackups()) {
			if (backup.getDeploymentId() == old.getId()) {
				r.setMessage("Can not delete deployment with existing backups");
				return false;
			}
		}
		return true;
	}
	static public boolean validateUnique(VortexDeployment nuw, Iterable<VortexDeployment> existing, VortexEyeResponse r) {
		//if (SH.is(nuw.getVerifyScriptFile())) {
		//if (nuw.getVerifyScriptFile().indexOf("${script_file}") == -1) {
		//r.setMessage("Verify Script must have a '${script_file}' variable.  For example: \n" + nuw.getVerifyScriptFile() + " ${script_file}");
		//return false;
		//}
		//
		//}
		for (VortexDeployment d : existing) {
			if (d.getId() == nuw.getId())
				continue;
			if (OH.eq(d.getTargetMachineUid(), nuw.getTargetMachineUid()) && OH.eq(d.getTargetDirectory(), nuw.getTargetDirectory())) {
				r.setMessage("Deployment must have unique target (machine and directory): " + d.getTargetDirectory());
				return false;
			}
		}
		return true;
	}
	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		switch (step) {
			case STEP1_INSERT_TO_DB: {
				DbResultMessage dbResult = (DbResultMessage) result.getAction();
				if (!dbResult.getOk()) {
					r.setMessage(dbResult.getMessage());
					return STATUS_COMPLETE;
				}
				if (isDelete)
					getState().removeDeployment(nuw.getId());
				else
					getState().addDeployment(nuw);
				r.setDeployment(nuw);
				if (old != null && !isDelete) {//is an update
					VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
					if (OH.ne(old.getTargetDirectory(), nuw.getTargetDirectory()) || OH.ne(old.getTargetMachineUid(), nuw.getTargetMachineUid())) {
						//the path / machine has changed so reset status of all backups
						for (VortexEyeBackup back : CH.l(getState().getBackups())) {
							if (back.getDeploymentId() != VortexEyeBackup.NO_DEPLOYMENT && back.getDeploymentId() == nuw.getId()) {
								if (back.getStatus() != VortexEyeBackup.STATUS_NEVER_RUN) {
									VortexEyeBackup backup = back.clone();
									backup.setStatus(VortexEyeBackup.STATUS_NEVER_RUN);
									backup.setMessage("Deployment changed");
									backup.setInvokedBy(getInitialRequest().getAction().getInvokedBy());
									backup.setNow(getTools().getNow());
									worker.sendToDb(this, VortexEyeRunBackupItinerary.insertBackupStatus(backup, getTools()));
									getState().addBackup(backup);
									cmb.writeTransition(back, backup);
								}
							}
						}
					}
					worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));
				}
				break;
			}
			case STEP2_UPDATE_BACKUPS: {
				DbResultMessage dbResult = (DbResultMessage) result.getAction();
				if (!dbResult.getOk()) {
					r.setMessage(dbResult.getMessage());
					return STATUS_COMPLETE;
				}
				break;
			}
			default:
				throw new IllegalStateException("unknown step: " + step);
		}
		if (getPendingRequests().isEmpty()) {
			r.setOk(true);
			return STATUS_COMPLETE;
		} else
			return STATUS_ACTIVE;
	}

	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		if (r.getOk()) {
			r.setDeployment(nuw);
			VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
			cmb.writeTransition(old, nuw);
			worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));
			VortexEyeMachineState machine = getState().getMachineByMuidNoThrow(nuw.getTargetMachineUid());
			if (machine != null && machine.getAgentState() != null) {
				VortexAgentUpdateDeploymentRequest updateMsg = getState().nw(VortexAgentUpdateDeploymentRequest.class);
				updateMsg.setUpdated(CH.l(nuw));
				worker.sendToAgent(this, updateMsg, machine.getAgentState().getProcessUid());
			}
		}
		return r;
	}

	public static void sendToDb(VortexDeployment deployment, VortexEyeItineraryWorker worker, VortexEyeItinerary<?> source) {
		boolean active = deployment.getRevision() < VortexAgentEntity.REVISION_DONE;
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("active", active);
		params.put("id", deployment.getId());
		params.put("revision", deployment.getRevision());
		params.put("now", deployment.getNow());
		params.put("build_result_id", deployment.getCurrentBuildResultId());
		params.put("build_invoked_by", deployment.getCurrentBuildInvokedBy());
		params.put("deployment_set_id", deployment.getDeploymentSetId());
		params.put("procedure_id", deployment.getProcedureId());
		params.put("target_machine_uid", deployment.getTargetMachineUid());
		params.put("target_directory", deployment.getTargetDirectory());
		params.put("generated_properties_file", deployment.getGeneratedPropertiesFile());//TODO:delete
		params.put("properties", deployment.getProperties());//TODO:delete

		params.put("generated_files", deployment.getGeneratedFiles() == null ? null : VortexEyeUtils.joinMap(deployment.getGeneratedFiles()));
		params.put("env_vars", deployment.getEnvVars());

		params.put("start_script_file", deployment.getStartScriptFile());
		params.put("stop_script_file", deployment.getStopScriptFile());
		params.put("target_user", deployment.getTargetUser());
		params.put("status", deployment.getStatus());
		params.put("deployed_instance_id", deployment.getDeployedInstanceId());
		params.put("running_pid", deployment.getRunningPid());
		params.put("running_process_uid", deployment.getRunningProcessUid());
		params.put("message", deployment.getMessage());
		params.put("scripts_directory", deployment.getScriptsDirectory());
		params.put("description", deployment.getDescription());
		params.put("metadata", deployment.getMetadata() == null ? null : VortexEyeUtils.joinMap(deployment.getMetadata()));

		params.put("install_script_file", deployment.getInstallScriptFile());
		params.put("uninstall_script_file", deployment.getUninstallScriptFile());

		params.put("verify_script_file", deployment.getVerifyScriptFile());
		params.put("auto_delete_files", deployment.getAutoDeleteFiles());
		params.put("log_directories", deployment.getLogDirectories());

		params.put("options", deployment.getOptions());

		DbRequestMessage msg = source.getState().nw(DbRequestMessage.class);
		msg.setId("insert_deployment");
		msg.setParams(params);
		worker.sendToDb(source, msg);
	}

	@Override
	protected void populateAuditEvent(VortexEyeManageDeploymentRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_MANAGE_DEPLOYMENT);
		auditEntity(sink, "DPID", action.getDeployment());
	}

}
