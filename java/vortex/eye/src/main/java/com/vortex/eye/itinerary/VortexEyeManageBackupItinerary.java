package com.vortex.eye.itinerary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.povo.db.DbRequestMessage;
import com.f1.povo.db.DbResultMessage;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.vortexcommon.msg.agent.VortexAgentBackupFile;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentUpdateBackupRequest;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBackupRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBackupResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeResponse;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.VortexEyeUtils;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.processors.agent.VortexEyeAgentBackupChangesProcessor;
import com.vortex.eye.state.VortexEyeMachineState;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeManageBackupItinerary extends AbstractVortexEyeItinerary<VortexEyeManageBackupRequest> {

	final private int STEP1_UPDATE_DB = 1;
	private static final int STEP2_REMOVE_FILES_FROM_DB = 2;
	private VortexEyeBackup old, nuw;
	private VortexEyeManageBackupResponse r;
	private boolean isDelete;
	private int step;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		r = getState().nw(VortexEyeManageBackupResponse.class);
		long now = getState().getPartition().getContainer().getTools().getNow();
		nuw = getInitialRequest().getAction().getBackup();
		VortexEyeState state = getState();
		isDelete = nuw.getRevision() == VortexAgentEntity.REVISION_DONE;
		if (!isDelete && !nuw.getSourcePath().endsWith("/"))
			nuw.setSourcePath(nuw.getSourcePath() + "/");
		if (nuw.getId() != 0) {
			old = state.getBackup(nuw.getId());
			if (old == null) {
				r.setMessage("Managed Directory not found for update / delete: " + nuw.getId());
				return STATUS_COMPLETE;
			}
			if (isDelete) {
				nuw = old.clone();
				nuw.setRevision(VortexAgentEntity.REVISION_DONE);
			} else {
				if (!VortexEyeManageMetadataFieldItinerary.validateMetadata(nuw, state, r))
					return STATUS_COMPLETE;
				if (!validateUnique(nuw, state.getBackups(), r))
					return STATUS_COMPLETE;
				nuw.setRevision(old.getRevision() + 1);
			}
		} else {
			if (!VortexEyeManageMetadataFieldItinerary.validateMetadata(nuw, state, r))
				return STATUS_COMPLETE;
			nuw.setId(getState().createNextId());
			nuw.setRevision(0);
			if (!validateUnique(nuw, state.getBackups(), r))
				return STATUS_COMPLETE;
		}
		nuw.setNow(now);
		nuw.lock();
		sendToDb(nuw, worker, this);
		step = STEP1_UPDATE_DB;
		return STATUS_ACTIVE;
	}
	public static boolean validateUnique(VortexEyeBackup nuw, Iterable<VortexEyeBackup> existings, VortexEyeResponse r) {
		for (VortexEyeBackup existing : existings) {
			if (existing.getId() == nuw.getId())
				continue;
			if (OH.eq(existing.getSourceMachineUid(), nuw.getSourceMachineUid()) && nuw.getDeploymentId() == existing.getDeploymentId()
					&& isOverlap(existing.getSourcePath(), nuw.getSourcePath()) && OH.eq(existing.getBackupDestinationId(), nuw.getBackupDestinationId())) {
				r.setMessage("Duplicate backup source: " + existing.getSourceMachineUid() + "::" + existing.getSourcePath());
				return false;
			}
		}
		return true;
	}
	private static boolean isOverlap(String pwd1, String pwd2) {
		return pwd1.startsWith(pwd2) || pwd2.startsWith(pwd1);
	}
	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		if (step == STEP1_UPDATE_DB) {
			DbResultMessage dbResult = (DbResultMessage) result.getAction();
			if (!dbResult.getOk()) {
				r.setMessage(dbResult.getMessage());
			} else {
				if (isDelete)
					getState().removeBackup(nuw.getId());
				else
					getState().addBackup(nuw);
				r.setOk(true);
			}
			if (nuw.getDeploymentId() == -1) {
				boolean machineChanged = old != null && OH.ne(nuw.getSourceMachineUid(), old.getSourceMachineUid());
				VortexEyeMachineState machine = getState().getMachineByMuidNoThrow(nuw.getSourceMachineUid());
				long miid = machine.getMiid();

				DbRequestMessage head = null;
				long id = nuw.getId();
				if (isDelete || machineChanged) {//delete all the files for this backup
					Iterable<VortexAgentBackupFile> files = getState().getBackupFiles();//TODO: avoid forward scan
					List<VortexAgentBackupFile> keep = new ArrayList<VortexAgentBackupFile>();
					if (files != null) {
						VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
						DbRequestMessage tail = null;
						for (VortexAgentBackupFile file : CH.l(files)) {
							if (file.getBackupId() == id) {
								getState().removeBackupFile(file.getId());
								file = file.clone();
								file.setRevision(VortexAgentFile.REVISION_DONE);
								DbRequestMessage rq = VortexEyeAgentBackupChangesProcessor.insertBackupFile(file, getTools());
								cmb.writeRemove(file);
								if (head == null)
									head = tail = rq;
								else {
									tail.setNextRequest(rq);
									tail = rq;
								}
							}
						}
						worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));
					}
				}
				if (isDelete) {
					final VortexAgentUpdateBackupRequest backupUpdate = getState().nw(VortexAgentUpdateBackupRequest.class);
					backupUpdate.setIsSnapshot(false);
					backupUpdate.setUpdated(new ArrayList<VortexEyeBackup>());
					backupUpdate.setRemoved(new long[] { nuw.getId() });
					if (machine.getAgentState() != null)
						worker.sendToAgent(this, backupUpdate, machine.getPuid());
				} else {
					VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
					Collection<VortexAgentBackupFile> files = getState().getBackupFileByMiid(miid);
					List<VortexAgentBackupFile> keep = new ArrayList<VortexAgentBackupFile>();
					if (CH.isntEmpty(files)) {
						DbRequestMessage tail = null;
						for (VortexAgentBackupFile file : CH.l(files)) {
							if (file.getBackupId() == id) {
								if (file.getPath().startsWith(nuw.getSourcePath())) {
									keep.add(file);
								} else {
									getState().removeBackupFile(file.getId());
									file = file.clone();
									file.setRevision(VortexAgentFile.REVISION_DONE);
									cmb.writeRemove(file);
									DbRequestMessage rq = VortexEyeAgentBackupChangesProcessor.insertBackupFile(file, getTools());
									if (head == null)
										head = tail = rq;
									else {
										tail.setNextRequest(rq);
										tail = rq;
									}
								}
							}
						}
					}
					worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));
					final VortexAgentUpdateBackupRequest backupUpdate = getState().nw(VortexAgentUpdateBackupRequest.class);
					backupUpdate.setIsSnapshot(false);
					backupUpdate.setUpdated(CH.l(nuw));
					backupUpdate.setFiles(keep);
					if (machine.getAgentState() != null)
						worker.sendToAgent(this, backupUpdate, machine.getPuid());
				}
				if (head != null) {
					worker.sendToDb(this, head);
					step = STEP2_REMOVE_FILES_FROM_DB;
					return STATUS_ACTIVE;
				}
			}
		} else if (step == STEP2_REMOVE_FILES_FROM_DB) {
		}
		return STATUS_COMPLETE;
	}
	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		if (r.getOk()) {
			r.setBackup(nuw);
			VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
			cmb.writeTransition(old, nuw);
			worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));
		}
		return r;
	}

	public static void sendToDb(VortexEyeBackup nw, VortexEyeItineraryWorker worker, VortexEyeItinerary<?> source) {
		boolean active = nw.getRevision() < VortexAgentEntity.REVISION_DONE;
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("active", active);
		params.put("id", nw.getId());
		params.put("revision", nw.getRevision());
		params.put("now", nw.getNow());
		params.put("source_path", nw.getSourcePath());
		params.put("source_machine_uid", nw.getSourceMachineUid());
		params.put("backup_destination_id", nw.getBackupDestinationId());
		params.put("deployment_id", nw.getDeploymentId());
		params.put("invoked_by", nw.getInvokedBy());
		params.put("description", nw.getDescription());
		params.put("ignore_expression", nw.getIgnoreExpression());
		params.put("file_count", nw.getFileCount());
		params.put("ignored_file_count", nw.getIgnoredFileCount());
		params.put("bytes_count", nw.getBytesCount());
		params.put("latest_modified_time", nw.getLatestModifiedTime());
		params.put("manifest_length", nw.getManifestLength());
		params.put("manifest", nw.getManifest());
		params.put("manifest_vvid", nw.getManifestVvid());
		params.put("manifest_time", nw.getManifestTime());
		params.put("message", nw.getMessage());
		params.put("options", nw.getOptions());
		params.put("status", nw.getStatus());
		params.put("metadata", nw.getMetadata() == null ? null : VortexEyeUtils.joinMap(nw.getMetadata()));
		DbRequestMessage msg = source.getState().nw(DbRequestMessage.class);
		msg.setId("insert_backup");
		msg.setParams(params);
		worker.sendToDb(source, msg);
	}
	@Override
	protected void populateAuditEvent(VortexEyeManageBackupRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_MANAGE_BACKUP);
		auditEntity(sink, "BUID", action.getBackup());
	}

}
