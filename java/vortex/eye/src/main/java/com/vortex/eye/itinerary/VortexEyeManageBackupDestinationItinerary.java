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
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;
import com.f1.vortexcommon.msg.eye.VortexEyeBackupDestination;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBackupDestinationRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBackupDestinationResponse;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.VortexEyeUtils;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeManageBackupDestinationItinerary extends AbstractVortexEyeItinerary<VortexEyeManageBackupDestinationRequest> {

	private static final int STEP1_INSERT_TO_DB = 1;
	private static final int STEP2_UPDATE_BACKUPS = 2;
	private VortexEyeBackupDestination old, nuw;
	private VortexEyeManageBackupDestinationResponse r;
	private boolean isDelete;
	private int step;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		r = getState().nw(VortexEyeManageBackupDestinationResponse.class);
		long now = getState().getPartition().getContainer().getTools().getNow();
		nuw = getInitialRequest().getAction().getBackupDestination();
		VortexEyeState state = getState();
		isDelete = nuw.getRevision() == VortexAgentEntity.REVISION_DONE;
		if (nuw.getId() != 0) {
			old = state.getBackupDestination(nuw.getId());
			if (old == null) {
				r.setMessage("Deployment not found for update / delete: " + nuw.getId());
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
				if (!validateUnique(nuw, state.getBackupDestinations(), r))
					return STATUS_COMPLETE;
				nuw.setRevision(old.getRevision() + 1);
			}
		} else {
			if (!VortexEyeManageMetadataFieldItinerary.validateMetadata(nuw, state, r))
				return STATUS_COMPLETE;
			nuw.setId(getState().createNextId());
			nuw.setRevision(0);
			if (!validateUnique(nuw, state.getBackupDestinations(), r))
				return STATUS_COMPLETE;
		}
		nuw.setNow(now);
		nuw.lock();
		this.step = STEP1_INSERT_TO_DB;
		sendToDb(nuw, worker, this);
		return STATUS_ACTIVE;
	}
	private boolean validateDelete(VortexEyeBackupDestination toDelete, VortexEyeState state, VortexEyeManageBackupDestinationResponse r) {
		for (VortexEyeBackup backup : state.getBackups()) {
			if (backup.getBackupDestinationId() == toDelete.getId()) {
				r.setMessage("Can not delete destination  BD-" + toDelete.getId() + " because it has active backups");
				return false;
			}
		}
		return true;
	}
	public static boolean validateUnique(VortexEyeBackupDestination nuw, Iterable<VortexEyeBackupDestination> backupDestinations, VortexEyeManageBackupDestinationResponse r) {
		for (VortexEyeBackupDestination existing : backupDestinations) {
			if (existing.getId() == nuw.getId())
				continue;
			if (OH.eq(existing.getName(), nuw.getName())) {
				r.setMessage("Duplicate backup destination name: " + existing.getName());
				return false;
			}
			if (OH.eq(existing.getDestinationMachineUid(), nuw.getDestinationMachineUid()) && OH.eq(existing.getDestinationPath(), nuw.getDestinationPath())) {
				r.setMessage("Duplicate backup destination path: " + existing.getDestinationMachineUid() + ":" + existing.getDestinationPath());
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
					getState().removeBackupDestination(nuw.getId());
				else
					getState().addBackupDestination(nuw);
				r.setBackupDestination(nuw);
				VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
				cmb.writeTransition(old, nuw);
				if (old != null && !isDelete) {//is an update
					if (OH.ne(old.getDestinationPath(), nuw.getDestinationPath()) || OH.ne(old.getDestinationMachineUid(), nuw.getDestinationMachineUid())) {
						//the path / machine has changed so reset status of all backups
						for (VortexEyeBackup back : CH.l(getState().getBackups())) {
							if (back.getBackupDestinationId() == nuw.getId()) {
								if (back.getStatus() != VortexEyeBackup.STATUS_NEVER_RUN) {
									VortexEyeBackup backup = back.clone();
									backup.setStatus(VortexEyeBackup.STATUS_NEVER_RUN);
									backup.setMessage("Destination changed");
									backup.setInvokedBy(getInitialRequest().getAction().getInvokedBy());
									backup.setNow(getTools().getNow());
									worker.sendToDb(this, VortexEyeRunBackupItinerary.insertBackupStatus(backup, getTools()));
									getState().addBackup(backup);
									cmb.writeTransition(back, backup);
								}
							}
						}
					}
				}
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
		return r;
	}

	public static void sendToDb(VortexEyeBackupDestination nuw2, VortexEyeItineraryWorker worker, VortexEyeItinerary<?> source) {
		boolean active = nuw2.getRevision() < VortexAgentEntity.REVISION_DONE;
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("active", active);
		params.put("id", nuw2.getId());
		params.put("revision", nuw2.getRevision());
		params.put("now", nuw2.getNow());
		params.put("destinationPath", nuw2.getDestinationPath());
		params.put("destinationMachineUid", nuw2.getDestinationMachineUid());
		params.put("name", nuw2.getName());
		params.put("metadata", nuw2.getMetadata() == null ? null : VortexEyeUtils.joinMap(nuw2.getMetadata()));
		DbRequestMessage msg = source.getState().nw(DbRequestMessage.class);
		msg.setId("insert_backup_destination");
		msg.setParams(params);
		worker.sendToDb(source, msg);
	}
	@Override
	protected void populateAuditEvent(VortexEyeManageBackupDestinationRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_MANAGE_BACKUP_DESTINATION);
		auditEntity(sink, "BDID", action.getBackupDestination());
	}

}
