package com.vortex.eye.itinerary;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Message;
import com.f1.container.ContainerTools;
import com.f1.container.ResultMessage;
import com.f1.povo.db.DbRequestMessage;
import com.f1.povo.db.DbResultMessage;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.ToDoException;
import com.f1.vortexcommon.msg.agent.VortexAgentDbServer;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentResponse;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunDeploymentRequest;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.VortexEyeScheduledTask;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunBackupRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunBackupResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunDbInspectionRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunDbInspectionResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunScheduledTaskRequest;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.VortexEyeUtils;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.state.VortexEyeMachineState;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeRunScheduledTaskItinerary extends AbstractVortexEyeItinerary<VortexEyeRunScheduledTaskRequest> {

	private static final byte STEP1_UPDATE_DATABASE_FOR_BEGIN = 1;
	private static final byte STEP2_RUN_REQUEST = 2;
	private static final byte STEP3_UPDATE_DATABSE_FOR_END = 3;

	private VortexEyeRunBackupResponse r;
	private byte step;

	private String invokedBy;
	private VortexEyeScheduledTask task;
	private VortexDeployment deployment;
	private VortexEyeMachineState machine;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		VortexEyeState state = getState();
		r = getState().nw(VortexEyeRunBackupResponse.class);
		final long now = getTools().getNow();
		final VortexEyeRunScheduledTaskRequest request = getInitialRequest().getAction();
		this.invokedBy = request.getInvokedBy();

		long stid = request.getScheduledTaskId();
		task = getState().getScheduledTask(stid);
		if (task == null)
			return setMessage("Scheduled task not found: ST-" + stid);

		final long depId = task.getDeploymentId();

		if (depId != VortexEyeScheduledTask.NO_DEPLOYMENT) {
			this.deployment = getState().getDeployment(depId);
			if (deployment == null)
				return setMessage("Scheduled task ST-" + stid + " has unknown deployment: DP-" + depId);
			String machineUid = deployment.getTargetMachineUid();
			this.machine = getState().getMachineByMuidNoThrow(machineUid);
			if (machine == null || machine.getAgentState() == null)
				return setMessage("machine not available: " + machineUid);
			if (machine.getAgentState().getProcessUid() == null)
				return setMessage("machine not available: " + machine.getRemoteHost() + " (uid=" + machineUid + ")");
		} else
			deployment = null;
		this.step = STEP1_UPDATE_DATABASE_FOR_BEGIN;
		updateScheduledTaskStatus(VortexEyeScheduledTask.STATUS_RUNNING, null, worker);

		return STATUS_ACTIVE;

	}
	private byte setMessage(String string) {
		r.setMessage(string);
		r.setOk(false);
		return STATUS_COMPLETE;
	}
	private void updateScheduledTaskStatus(byte status, String message, VortexEyeItineraryWorker worker) {
		VortexEyeScheduledTask nuw = task.clone();
		nuw.setNow(getTools().getNow());
		nuw.setStatus(status);
		nuw.setMessage(message);
		if (status == VortexEyeScheduledTask.STATUS_RUNNING) {
			nuw.setLastRuntime(nuw.getNow());
			nuw.setRunCount(nuw.getRunCount() + 1);
		} else if (status == VortexEyeScheduledTask.STATUS_OKAY) {
			nuw.setNextRuntime(VortexEyeUtils.getNextOccurence(nuw, nuw.getNow()));
		}
		VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
		cmb.writeTransition(task, nuw);
		getState().addScheduledTask(nuw);
		worker.sendToDb(this, insertScheduledTaskStatus(nuw, this.getTools()));
		this.task = nuw;
		worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));
	}

	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		if (result.getError() != null) {
			updateScheduledTaskStatus(VortexEyeScheduledTask.STATUS_FAILURE, "General Error Processing task ", worker);
			return setMessage("General error: " + result.getError().getMessage());
		}
		if (result.getAction() instanceof DbResultMessage) {
			DbResultMessage dbresponse = (DbResultMessage) result.getAction();
			if (!dbresponse.getOk()) {
				updateScheduledTaskStatus(VortexEyeScheduledTask.STATUS_FAILURE, "Error Processing db: " + dbresponse.getMessage(), worker);
				return setMessage("Error processing db: " + dbresponse.getMessage());
			}
		}
		switch (step) {
			case STEP1_UPDATE_DATABASE_FOR_BEGIN: {
				switch (task.getType()) {
					case VortexEyeScheduledTask.TYPE_START: {
						if (deployment == null)
							return setMessage("Task ST-" + task.getId() + " invalid, should be associated with a deployment");
						VortexAgentRunDeploymentRequest req = getState().nw(VortexAgentRunDeploymentRequest.class);
						req.setInvokedBy(this.invokedBy);
						req.setDeploymentId(deployment.getId());
						req.setCommandType(VortexAgentRunDeploymentRequest.TYPE_START_SCRIPT);
						worker.sendRequestToAgent(this, req, machine.getAgentState().getProcessUid());
						step = STEP2_RUN_REQUEST;
						break;
					}
					case VortexEyeScheduledTask.TYPE_STOP: {
						if (deployment == null)
							return setMessage("Task ST-" + task.getId() + " invalid, should be associated with a deployment");
						VortexAgentRunDeploymentRequest req = getState().nw(VortexAgentRunDeploymentRequest.class);
						req.setInvokedBy(this.invokedBy);
						req.setDeploymentId(deployment.getId());
						req.setCommandType(VortexAgentRunDeploymentRequest.TYPE_STOP_SCRIPT);
						worker.sendRequestToAgent(this, req, machine.getAgentState().getProcessUid());
						step = STEP2_RUN_REQUEST;
						break;
					}
					case VortexEyeScheduledTask.TYPE_SCRIPT: {
						if (deployment != null) {
							VortexAgentRunDeploymentRequest req = getState().nw(VortexAgentRunDeploymentRequest.class);
							req.setInvokedBy(this.invokedBy);
							req.setDeploymentId(deployment.getId());
							req.setCommandType(VortexAgentRunDeploymentRequest.TYPE_RUN_SCRIPT);
							req.setTargetFile(this.task.getCommand());
							worker.sendRequestToAgent(this, req, machine.getAgentState().getProcessUid());
						} else
							throw new ToDoException("run non-deployment script ");
						step = STEP2_RUN_REQUEST;
						break;
					}
					case VortexEyeScheduledTask.TYPE_BACKUP: {
						VortexEyeBackup backup = getState().getBackup(task.getTargetId());
						if (backup != null) {
							VortexEyeRunBackupRequest req = getState().nw(VortexEyeRunBackupRequest.class);
							req.setInvokedBy(this.invokedBy);
							req.setBackups(CH.l(backup.getId()));
							worker.startItinerary(this, new VortexEyeRunBackupItinerary(), req);
						} else {
							this.updateScheduledTaskStatus(VortexEyeScheduledTask.STATUS_INVALID, "Unkown backup BU-" + task.getTargetId(), worker);
							this.r.setMessage(task.getMessage());
							step = STEP3_UPDATE_DATABSE_FOR_END;
							return STATUS_ACTIVE;
						}
						step = STEP2_RUN_REQUEST;
						break;
					}
					case VortexEyeScheduledTask.TYPE_DATABASE_INSPECT: {
						VortexAgentDbServer db = getState().getDbServer(task.getTargetId());
						if (db != null) {
							VortexEyeRunDbInspectionRequest req = getState().nw(VortexEyeRunDbInspectionRequest.class);
							req.setInvokedBy(this.invokedBy);
							req.setDbServerId(db.getId());
							worker.startItinerary(this, new VortexEyeInspectDbSchemaItinerary(), req);
						} else {
							this.updateScheduledTaskStatus(VortexEyeScheduledTask.STATUS_INVALID, "Unkown db DB-" + task.getTargetId(), worker);
							this.r.setMessage(task.getMessage());
							step = STEP3_UPDATE_DATABSE_FOR_END;
							return STATUS_ACTIVE;
						}
						step = STEP2_RUN_REQUEST;
						break;
					}
					default:
						throw new ToDoException("type: " + task.getType());
				}
				return STATUS_ACTIVE;
			}
			case STEP2_RUN_REQUEST: {
				final String message;
				final boolean ok;
				switch (task.getType()) {
					case VortexEyeScheduledTask.TYPE_SCRIPT:
					case VortexEyeScheduledTask.TYPE_START:
					case VortexEyeScheduledTask.TYPE_STOP: {
						VortexAgentResponse res = (VortexAgentResponse) result.getAction();
						ok = res.getOk();
						message = res.getMessage();

						break;
					}
					case VortexEyeScheduledTask.TYPE_BACKUP: {
						VortexEyeRunBackupResponse res = (VortexEyeRunBackupResponse) result.getAction();
						ok = res.getOk();
						message = res.getMessage();
						break;
					}
					case VortexEyeScheduledTask.TYPE_DATABASE_INSPECT: {
						VortexEyeRunDbInspectionResponse res = (VortexEyeRunDbInspectionResponse) result.getAction();
						ok = res.getOk();
						message = res.getMessage();
						break;
					}
					default: {
						ok = false;
						message = "unknown response type: " + OH.getClassName(result.getAction());
					}

				}
				if (ok) {
					updateScheduledTaskStatus(VortexEyeScheduledTask.STATUS_OKAY, null, worker);
					r.setOk(true);
					step = STEP3_UPDATE_DATABSE_FOR_END;
				} else {
					r.setMessage(message);
				}
				return STATUS_ACTIVE;
			}
			case STEP3_UPDATE_DATABSE_FOR_END: {
				return STATUS_COMPLETE;
			}

			default:
				throw new RuntimeException("unknown step: " + step);
		}
	}

	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		return r;
	}

	static public DbRequestMessage insertScheduledTaskStatus(VortexEyeScheduledTask task, ContainerTools tools) {
		boolean active = task.getRevision() < VortexAgentEntity.REVISION_DONE;
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("active", active);
		params.put("revision", task.getRevision());
		params.put("now", task.getNow());
		params.put("id", task.getId());
		params.put("status", task.getStatus());
		params.put("message", task.getMessage());
		params.put("invoked_by", task.getInvokedBy());
		params.put("nextRuntime", task.getNextRuntime());
		params.put("lastRuntime", task.getLastRuntime());
		params.put("runCount", task.getRunCount());
		DbRequestMessage dbRequest = tools.nw(DbRequestMessage.class);
		dbRequest.setId("insert_scheduled_task_status");
		dbRequest.setParams(params);
		return dbRequest;
	}
	@Override
	protected void populateAuditEvent(VortexEyeRunScheduledTaskRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_RUN_SCHEDULED_TASK);
		sink.getParams().put("STID", SH.toString(action.getScheduledTaskId()));
	}
}
