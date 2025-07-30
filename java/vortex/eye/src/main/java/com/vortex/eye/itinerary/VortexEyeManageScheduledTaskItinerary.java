package com.vortex.eye.itinerary;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.povo.db.DbRequestMessage;
import com.f1.povo.db.DbResultMessage;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.VortexEyeScheduledTask;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageScheduledTaskRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageScheduledTaskResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeResponse;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.VortexEyeUtils;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeManageScheduledTaskItinerary extends AbstractVortexEyeItinerary<VortexEyeManageScheduledTaskRequest> {

	private static final int STEP1_INSERT_TO_DB = 1;
	private static final int STEP2_UPDATE_BACKUPS = 2;
	private VortexEyeScheduledTask old, nuw;
	private VortexEyeManageScheduledTaskResponse r;
	private boolean isDelete;
	private int step;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		r = getState().nw(VortexEyeManageScheduledTaskResponse.class);
		long now = getState().getPartition().getContainer().getTools().getNow();
		nuw = getInitialRequest().getAction().getScheduledTask();
		VortexEyeState state = getState();
		isDelete = nuw.getRevision() == VortexAgentEntity.REVISION_DONE;
		if (nuw.getId() != 0) {
			old = state.getScheduledTask(nuw.getId());
			if (old == null) {
				r.setMessage("ScheduledTask not found for update / delete: " + nuw.getId());
				return STATUS_COMPLETE;
			}
			if (isDelete) {
				if (!validateDelete(old, getState(), r))
					return STATUS_COMPLETE;
				nuw = old.clone();
				nuw.setRevision(VortexAgentEntity.REVISION_DONE);
			} else {
				if (!VortexEyeManageMetadataFieldItinerary.validateMetadata(nuw, state, r))
					return STATUS_COMPLETE;
				if (!validateUnique(nuw, getState().getScheduledTasks(), r))
					return STATUS_COMPLETE;
				nuw.setRevision(old.getRevision() + 1);
				nuw.setStatus(old.getStatus());
				//nuw.setState(old.getState());
				nuw.setRunCount(old.getRunCount());
				nuw.setLastRuntime(old.getLastRuntime());
			}
		} else {
			if (!VortexEyeManageMetadataFieldItinerary.validateMetadata(nuw, state, r))
				return STATUS_COMPLETE;
			if (!validateUnique(nuw, getState().getScheduledTasks(), r))
				return STATUS_COMPLETE;
			nuw.setId(getState().createNextId());
			nuw.setRevision(0);
			nuw.setLastRuntime(0);
			nuw.setStatus(VortexEyeScheduledTask.STATUS_NEVER_RUN);
		}
		nuw.setNow(now);
		nuw.setInvokedBy(getInitialRequest().getAction().getInvokedBy());
		nuw.setNextRuntime(VortexEyeUtils.getNextOccurence(nuw, now));
		if (nuw.getNextRuntime() == -1) {
			nuw.setMessage("The configuration is invalid, this job will never run");
			nuw.setStatus(VortexEyeScheduledTask.STATUS_INVALID);
		}
		nuw.lock();
		this.step = STEP1_INSERT_TO_DB;
		sendToDb(nuw, worker, this);
		return STATUS_ACTIVE;
	}
	private boolean validateDelete(VortexEyeScheduledTask old, VortexEyeState state, VortexEyeManageScheduledTaskResponse r) {
		return true;
	}
	static public boolean validateUnique(VortexEyeScheduledTask nuw, Iterable<VortexEyeScheduledTask> existing, VortexEyeResponse r) {
		for (VortexEyeScheduledTask d : existing) {
			if (d.getId() == nuw.getId())
				continue;
			//if (OH.eq(d.getType(), nuw.getType()) && OH.eq(d.getTargetId(), nuw.getTargetId()) && OH.eq(d.getCommand(), nuw.getCommand())) {
			//r.setMessage("ScheduledTask must be unique. Task conflicts with: ST-" + d.getId());
			//return false;
			//}
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
					getState().removeScheduledTask(nuw.getId());
				else
					getState().addScheduledTask(nuw);
				r.setScheduledTask(nuw);
				VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
				cmb.writeTransition(old, nuw);
				worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));
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
			r.setScheduledTask(nuw);
			VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
			cmb.writeTransition(old, nuw);
			worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));
		}
		return r;
	}

	public static void sendToDb(VortexEyeScheduledTask stask, VortexEyeItineraryWorker worker, VortexEyeItinerary<?> source) {
		boolean active = stask.getRevision() < VortexAgentEntity.REVISION_DONE;
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("active", active);
		params.put("id", stask.getId());
		params.put("revision", stask.getRevision());
		params.put("now", stask.getNow());
		params.put("deploymentId", stask.getDeploymentId());
		params.put("description", stask.getDescription());
		params.put("options", stask.getOptions());
		params.put("targetId", stask.getTargetId());
		params.put("taskType", stask.getType());
		params.put("command", stask.getCommand());
		params.put("timezone", stask.getTimezone());
		params.put("hours", stask.getHours());
		params.put("minutes", stask.getMinutes());
		params.put("seconds", stask.getSeconds());
		params.put("weekdays", stask.getWeekdays());
		params.put("monthInYears", stask.getMonthInYears());
		params.put("weekInMonths", stask.getWeekInMonths());
		params.put("weekInYears", stask.getWeekInYears());
		params.put("dayInMonths", stask.getDayInMonths());
		params.put("dayOfWeekInMonths", stask.getDayOfWeekInMonths());
		params.put("dayOfYears", stask.getDayOfYears());
		params.put("type", stask.getType());
		params.put("status", stask.getStatus());
		params.put("state", stask.getState());
		params.put("message", stask.getMessage());
		params.put("invoked_by", stask.getInvokedBy());
		params.put("nextRuntime", stask.getNextRuntime());
		params.put("lastRuntime", stask.getLastRuntime());
		params.put("runCount", stask.getRunCount());
		params.put("comments", stask.getComments());
		params.put("metadata", stask.getMetadata() == null ? null : VortexEyeUtils.joinMap(stask.getMetadata()));

		DbRequestMessage msg = source.getState().nw(DbRequestMessage.class);
		msg.setId("insert_scheduled_task");
		msg.setParams(params);
		worker.sendToDb(source, msg);
	}
	@Override
	protected void populateAuditEvent(VortexEyeManageScheduledTaskRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_MANAGE_SCHEDULED_TASK);
		auditEntity(sink, "STID", action.getScheduledTask());
	}

}
