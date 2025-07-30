package com.vortex.eye.itinerary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.povo.db.DbRequestMessage;
import com.f1.utils.OH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.VortexDeploymentSet;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.VortexEyeEntity;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeCreateDeploymentEnvironmentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeCreateDeploymentEnvironmentResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBackupRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBackupResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDeploymentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDeploymentResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDeploymentSetRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDeploymentSetResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeResponse;
import com.vortex.eye.VortexEyeUtils;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;

public class VortexEyeCreateDeploymentEnvironmentItinerary extends AbstractVortexEyeItinerary<VortexEyeCreateDeploymentEnvironmentRequest> {

	private static final byte STEP1_SEND_DEPLOYMENTSET_TO_DB = 1;
	private static final byte STEP2_SEND_DEPLOYMENTS_TO_DB = 2;

	private VortexDeploymentSet old, nuw;
	private VortexEyeCreateDeploymentEnvironmentResponse r;
	private boolean isDelete;
	private byte step;
	private LongKeyMap<VortexDeploymentSet> deploymentSets;
	private LongKeyMap<VortexDeployment> deployments;
	private LongKeyMap<VortexEyeBackup> backups;

	final private LongKeyMap<VortexDeploymentSet> deploymentSetResults = new LongKeyMap<VortexDeploymentSet>();
	final private LongKeyMap<VortexDeployment> deploymentResults = new LongKeyMap<VortexDeployment>();
	final private LongKeyMap<VortexEyeBackup> backupResults = new LongKeyMap<VortexEyeBackup>();

	final private Queue<VortexEyeEntity> remainingEntityTasks = new LinkedList<VortexEyeEntity>();
	final private Queue<VortexEyeEntity> processedEntityTasks = new LinkedList<VortexEyeEntity>();
	private VortexEyeEntity currentEntityTask = null;
	private long currentEntityTaskId;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		r = getState().nw(VortexEyeCreateDeploymentEnvironmentResponse.class);
		r.setDeployments(new ArrayList<VortexDeployment>());
		r.setDeploymentSets(new ArrayList<VortexDeploymentSet>());
		r.setBackups(new ArrayList<VortexEyeBackup>());

		final VortexEyeCreateDeploymentEnvironmentRequest req = getInitialRequest().getAction();
		deploymentSets = VortexEyeUtils.mapById(req.getDeploymentSets());
		deployments = VortexEyeUtils.mapById(req.getDeployments());
		backups = VortexEyeUtils.mapById(req.getBackups());

		for (VortexDeploymentSet entity : deploymentSets.values()) {
			if (entity.getId() >= 0)
				throw new IllegalArgumentException("ids must be negative: " + entity);
			if (!VortexEyeManageDeploymentSetItinerary.validateUnique(entity, getState().getDeploymentSets(), r))
				return STATUS_COMPLETE;
			if (!VortexEyeManageDeploymentSetItinerary.validateUnique(entity, deploymentSets.values(), r))
				return STATUS_COMPLETE;
			remainingEntityTasks.add(entity);
		}

		for (VortexDeployment entity : deployments.values()) {
			if (entity.getId() >= 0)
				throw new IllegalArgumentException("ids must be negative: " + entity);
			if (entity.getDeploymentSetId() < 0 && !deploymentSets.containsKey(entity.getDeploymentSetId()))
				throw new IllegalArgumentException("bad temp deployment set id: " + entity);
			if (!VortexEyeManageDeploymentItinerary.validateUnique(entity, getState().getDeployments(), r))
				return STATUS_COMPLETE;
			if (!VortexEyeManageDeploymentItinerary.validateUnique(entity, deployments.values(), r))
				return STATUS_COMPLETE;
			remainingEntityTasks.add(entity);
		}

		for (VortexEyeBackup entity : backups.values()) {
			if (entity.getId() >= 0)
				throw new IllegalArgumentException("ids must be negative: " + entity);
			if (entity.getDeploymentId() < 0 && !deployments.containsKey(entity.getDeploymentId()))
				throw new IllegalArgumentException("bad temp deployment set id: " + entity);
			if (!VortexEyeManageBackupItinerary.validateUnique(entity, getState().getBackups(), r))
				return STATUS_COMPLETE;
			if (!VortexEyeManageBackupItinerary.validateUnique(entity, backups.values(), r))
				return STATUS_COMPLETE;
			remainingEntityTasks.add(entity);
		}

		if (!startNextTask(worker)) {
			r.setMessage("Nothing to create");
			return STATUS_COMPLETE;
		}
		return STATUS_ACTIVE;
	}
	private boolean startNextTask(VortexEyeItineraryWorker worker) {
		currentEntityTask = null;
		if (remainingEntityTasks.isEmpty())
			return false;
		currentEntityTask = remainingEntityTasks.poll();
		currentEntityTaskId = currentEntityTask.getId();
		if (currentEntityTask instanceof VortexDeploymentSet) {
			VortexEyeManageDeploymentSetRequest req = getTools().nw(VortexEyeManageDeploymentSetRequest.class);
			VortexDeploymentSet entity = (VortexDeploymentSet) currentEntityTask;
			entity.setId(0);
			req.setDeploymentSet(entity);
			worker.startItinerary(this, new VortexEyeManageDeploymentSetItinerary(), req);
		} else if (currentEntityTask instanceof VortexDeployment) {
			VortexEyeManageDeploymentRequest req = getTools().nw(VortexEyeManageDeploymentRequest.class);
			VortexDeployment entity = (VortexDeployment) currentEntityTask;
			if (entity.getDeploymentSetId() < 0) //is a temporary id so it needs mapping
				entity.setDeploymentSetId(deploymentSetResults.getOrThrow(entity.getDeploymentSetId()).getId());
			entity.setId(0);
			req.setDeployment(entity);
			worker.startItinerary(this, new VortexEyeManageDeploymentItinerary(), req);
		} else if (currentEntityTask instanceof VortexEyeBackup) {
			VortexEyeManageBackupRequest req = getTools().nw(VortexEyeManageBackupRequest.class);
			VortexEyeBackup entity = (VortexEyeBackup) currentEntityTask;
			if (entity.getDeploymentId() < 0) //is a temporary id so it needs mapping
				entity.setDeploymentId(deploymentResults.getOrThrow(entity.getDeploymentId()).getId());
			entity.setId(0);
			req.setBackup(entity);
			worker.startItinerary(this, new VortexEyeManageBackupItinerary(), req);
		} else
			throw new RuntimeException("unknown type: " + currentEntityTask);
		return true;
	}
	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		VortexEyeResponse action = (VortexEyeResponse) result.getAction();
		if (!action.getOk()) {
			r.setMessage("Unexpected error occured resulting in partial creation of " + processedEntityTasks.size() + " entities. " + (remainingEntityTasks.size() + 1)
					+ " entities were NOT created. Caused by: " + action.getMessage());
			return STATUS_COMPLETE;
		}
		if (currentEntityTask instanceof VortexDeploymentSet) {
			VortexDeploymentSet entity = OH.assertNotNull(((VortexEyeManageDeploymentSetResponse) action).getDeploymentSet());
			deploymentSetResults.put(currentEntityTaskId, entity);
			r.getDeploymentSets().add(entity);
		} else if (currentEntityTask instanceof VortexDeployment) {
			VortexDeployment entity = OH.assertNotNull(((VortexEyeManageDeploymentResponse) action).getDeployment());
			deploymentResults.put(currentEntityTaskId, entity);
			r.getDeployments().add(entity);
		} else if (currentEntityTask instanceof VortexEyeBackup) {
			VortexEyeBackup entity = OH.assertNotNull(((VortexEyeManageBackupResponse) action).getBackup());
			backupResults.put(currentEntityTaskId, entity);
			r.getBackups().add(entity);
		}
		if (!startNextTask(worker)) {
			r.setOk(true);
			return STATUS_COMPLETE;
		}
		return STATUS_ACTIVE;
	}
	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
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
		DbRequestMessage msg = getTools().nw(DbRequestMessage.class);
		msg.setId("insert_deployment_set");
		msg.setParams(params);
		worker.sendToDb(this, msg);
	}
	@Override
	protected void populateAuditEvent(VortexEyeCreateDeploymentEnvironmentRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_CREATE_DEPLOYMENT_ENVIRONMENT);
		//auditList(sink, "DPID", action.getDeployments());
		//auditList(sink, "BUID", action.getBackups());
	}

}
