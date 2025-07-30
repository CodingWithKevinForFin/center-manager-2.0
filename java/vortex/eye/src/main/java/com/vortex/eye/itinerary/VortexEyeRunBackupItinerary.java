package com.vortex.eye.itinerary;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.f1.base.Message;
import com.f1.container.ContainerTools;
import com.f1.container.ResultMessage;
import com.f1.povo.db.DbRequestMessage;
import com.f1.povo.db.DbResultMessage;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentGetBackupChangedFilesRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentGetBackupChangedFilesResponse;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentGetBackupDestinationManifestRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentGetBackupDestinationManifestResponse;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentSendBackupFilesToDestinationRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentSendBackupFilesToDestinationResponse;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;
import com.f1.vortexcommon.msg.eye.VortexEyeBackupDestination;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunBackupRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunBackupResponse;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.state.VortexEyeMachineState;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeRunBackupItinerary extends AbstractVortexEyeItinerary<VortexEyeRunBackupRequest> {

	public static class BackupItem {
		public VortexEyeBackup backup;
		public final VortexEyeBackupDestination dest;
		public final VortexDeployment dep;
		public final VortexEyeMachineState srcMachine;
		public final VortexEyeMachineState dstMachine;
		public final String fullPath;
		public boolean isComplete = false;
		public int currentPacket = 0;

		public BackupItem(VortexEyeBackup backup, VortexDeployment dep, VortexEyeBackupDestination dest, VortexEyeMachineState srcMachine, VortexEyeMachineState dstMachine) {
			this.backup = backup;
			this.dep = dep;
			this.dest = dest;
			this.srcMachine = srcMachine;
			this.dstMachine = dstMachine;
			this.fullPath = dep == null ? backup.getSourcePath() : dep.getTargetDirectory() + "/" + backup.getSourcePath();
		}

		public String describeState() {
			if (currentPacket < 2)
				return backup.getSourcePath();
			else
				return backup.getSourcePath() + " [packet " + currentPacket + "]";
		}
	}

	private static final byte STEP1_UPDATE_DATABASE_FOR_BEGIN = 1;
	private static final byte STEP2_GET_DEST_MANIFEST = 2;
	private static final byte STEP3_GET_CHANGED_FILES_FROM_SOURCE = 3;
	private static final byte STEP4_SEND_CHANGED_FILES_TO_DEST = 4;
	private static final byte STEP5_UPDATE_DATABASE_FOR_END = 5;
	private static final byte STEP_SUBMIT_ERROR_TO_DB = 50;
	private int filesAddedStats = 0;
	private int filesUpdatedStats = 0;
	private int filesDeletedStats = 0;
	private int filesUnchangedStats = 0;
	private int totalBytesTransfered = 0;
	private int backupsProcessed = 0;

	private VortexEyeRunBackupResponse r;
	private byte step;
	private Set<String> allBackups;
	private List<String> toBackup;

	//true=backup,false=archive
	private Queue<BackupItem> remainingBackupItems = new LinkedList<BackupItem>();
	private BackupItem currentBackupItem;
	private Queue<BackupItem> processedBackupItems = new LinkedList<BackupItem>();
	private VortexEyeBackupDestination backupDest;

	private String invokedBy;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		VortexEyeState state = getState();
		r = getState().nw(VortexEyeRunBackupResponse.class);
		final long now = getTools().getNow();
		final VortexEyeRunBackupRequest request = getInitialRequest().getAction();
		//this.deployment = getState().getDeployment(request.getDeploymentId());
		this.invokedBy = request.getInvokedBy();

		for (final long backupId : request.getBackups()) {
			final VortexEyeBackup backup = getState().getBackup(backupId);
			if (backup == null) {
				return setMessage("backup not found: BU-" + backupId);
			}
			final long depId = backup.getDeploymentId();
			final VortexDeployment deployment;
			final String srcMachineUid;
			if (depId != VortexEyeBackup.NO_DEPLOYMENT) {
				deployment = getState().getDeployment(depId);
				if (deployment == null)
					return setMessage("backup BU-" + backupId + " has unknown deployment: DP-" + depId);
				srcMachineUid = deployment.getTargetMachineUid();
			} else {
				srcMachineUid = backup.getSourceMachineUid();
				deployment = null;
			}
			//check source is available
			final VortexEyeMachineState srcMachine = getState().getMachineByMuidNoThrow(srcMachineUid);
			if (srcMachine == null || srcMachine.getAgentState() == null)
				return setMessage("source machine not available: " + srcMachineUid);
			if (srcMachine.getAgentState().getProcessUid() == null)
				return setMessage("source machine not available: " + srcMachine.getRemoteHost() + " (uid=" + srcMachineUid + ")");

			//check destination is available
			long destId = backup.getBackupDestinationId();
			backupDest = state.getBackupDestination(destId);
			if (backupDest == null)
				return setMessage("backup destination not available: " + destId);
			final String dstMachineUid = backupDest.getDestinationMachineUid();
			final VortexEyeMachineState dstMachine = getState().getMachineByMuidNoThrow(dstMachineUid);
			if (dstMachine == null || dstMachine.getAgentState() == null)
				return setMessage("destination machine not available: " + dstMachineUid);
			if (dstMachine.getAgentState().getProcessUid() == null)
				return setMessage("destination machine not available: " + dstMachine.getRemoteHost() + " (uid=" + dstMachineUid + ")");
			remainingBackupItems.add(new BackupItem(backup, deployment, backupDest, srcMachine, dstMachine));

		}

		//validate archives;
		if (remainingBackupItems.isEmpty())
			return setMessage("no backups supplied");

		if (popNextBackupItem())
			requestDestinationManifest(worker);
		return STATUS_ACTIVE;

	}
	private byte setMessage(String string) {
		r.setMessage(string);
		r.setOk(false);
		return STATUS_COMPLETE;
	}
	private void updateBackupStatus(byte backupStatus, String message, VortexEyeItineraryWorker worker) {
		VortexEyeBackup old = currentBackupItem.backup;
		VortexEyeBackup nuw = old.clone();
		nuw.setStatus(backupStatus);
		nuw.setNow(getTools().getNow());
		if (message != null)
			nuw.setMessage(message);
		currentBackupItem.backup = nuw;
		getState().addBackup(nuw);
		VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
		cmb.writeTransition(old, nuw);
		if (old.getStatus() != nuw.getStatus())
			worker.sendToDb(this, insertBackupStatus(nuw, this.getTools()));
		worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));
	}
	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		if (result.getError() != null) {
			return setMessage("General error: " + result.getError().getMessage());
		}
		if (result.getAction() instanceof DbResultMessage) {
			DbResultMessage dbresponse = (DbResultMessage) result.getAction();
			if (!dbresponse.getOk()) {
				setMessage("Error processing db: " + dbresponse.getMessage());
				return STATUS_COMPLETE;
			}
			return pendingRequests.isEmpty() && remainingBackupItems.isEmpty() && currentBackupItem == null ? STATUS_COMPLETE : STATUS_ACTIVE;
		}
		switch (step) {
			case STEP_SUBMIT_ERROR_TO_DB: {
				return STATUS_COMPLETE;
			}
			case STEP2_GET_DEST_MANIFEST: {
				VortexAgentGetBackupDestinationManifestResponse manifestResponse = (VortexAgentGetBackupDestinationManifestResponse) result.getAction();
				if (!manifestResponse.getOk()) {
					setMessage("Error preparing backup destination at " + currentBackupItem.dstMachine.getRemoteHost() + ": " + manifestResponse.getMessage());
					updateBackupStatus(VortexEyeBackup.STATUS_FAILURE, r.getMessage(), worker);
					step = STEP_SUBMIT_ERROR_TO_DB;
					return STATUS_ACTIVE;
				}
				requestChangedFilesFromSource(worker, manifestResponse.getBackupFiles());

				return STATUS_ACTIVE;
			}
			case STEP3_GET_CHANGED_FILES_FROM_SOURCE: {
				VortexAgentGetBackupChangedFilesResponse response = (VortexAgentGetBackupChangedFilesResponse) result.getAction();
				if (!response.getOk()) {
					setMessage("Failed processing backup retrieval on " + currentBackupItem.srcMachine.getRemoteHost() + " at " + currentBackupItem.fullPath + ": "
							+ response.getMessage());
					updateBackupStatus(VortexEyeBackup.STATUS_FAILURE, r.getMessage(), worker);
					step = STEP_SUBMIT_ERROR_TO_DB;
					return STATUS_ACTIVE;
				}
				currentBackupItem.isComplete = response.getIsComplete();
				sendChangedFilesToDestination(worker, response.getBackupFiles());
				return STATUS_ACTIVE;
			}
			case STEP4_SEND_CHANGED_FILES_TO_DEST: {
				VortexAgentSendBackupFilesToDestinationResponse response = (VortexAgentSendBackupFilesToDestinationResponse) result.getAction();
				if (!response.getOk()) {
					step = STEP_SUBMIT_ERROR_TO_DB;
					setMessage("Failed processing storage of backup on " + currentBackupItem.dstMachine.getRemoteHost() + " for source path " + currentBackupItem.fullPath + ": "
							+ response.getMessage());
					updateBackupStatus(VortexEyeBackup.STATUS_FAILURE, r.getMessage(), worker);
					return STATUS_ACTIVE;
				}
				this.filesDeletedStats += response.getFilesDeletedStats();
				this.totalBytesTransfered += response.getBytesTransfered();
				if (currentBackupItem.isComplete) {
					this.backupsProcessed++;
					this.filesUpdatedStats += response.getFilesUpdatedStats();
					this.filesAddedStats += response.getFilesAddedStats();
					this.filesUnchangedStats += response.getFilesUnchangedStats();
					this.filesDeletedStats += response.getFilesUnchangedStats();

					int totalFiles = filesUnchangedStats + filesAddedStats + filesUpdatedStats;
					updateBackupStatus(totalFiles == 0 ? VortexEyeBackup.STATUS_PARTIAL_OKAY : VortexEyeBackup.STATUS_OKAY, "Backup successful. " + totalFiles
							+ " file(s) verified", worker);
					this.filesUnchangedStats = this.filesUpdatedStats = this.filesAddedStats = this.filesDeletedStats = this.totalBytesTransfered = 0;
					if (popNextBackupItem())
						requestDestinationManifest(worker);
					else {
						r.setOk(true);
						return getPendingRequests().isEmpty() ? STATUS_COMPLETE : STATUS_ACTIVE;
					}
				} else {
					requestDestinationManifest(worker);
				}
				return STATUS_ACTIVE;
			}
			default:
				throw new RuntimeException("unknown step: " + step);
		}
	}
	private void requestDestinationManifest(VortexEyeItineraryWorker worker) {
		currentBackupItem.currentPacket++;
		updateBackupStatus(VortexEyeBackup.STATUS_RUNNING, "Getting manifest for: " + currentBackupItem.describeState(), worker);
		VortexAgentGetBackupDestinationManifestRequest req = getTools().nw(VortexAgentGetBackupDestinationManifestRequest.class);
		req.setInvokedBy(getInitialRequest().getAction().getInvokedBy());
		req.setDestinationPath(currentBackupItem.dest.getDestinationPath());
		req.setSourceMuid(currentBackupItem.srcMachine.getMuid());
		req.setSourceHostName(currentBackupItem.srcMachine.getRemoteHost());
		req.setInvokedBy(invokedBy);
		req.setBackupPath(currentBackupItem.fullPath);
		worker.sendRequestToAgent(this, req, currentBackupItem.dstMachine.getAgentState().getProcessUid());
		step = STEP2_GET_DEST_MANIFEST;
	}
	private void requestChangedFilesFromSource(VortexEyeItineraryWorker worker, List<VortexAgentFile> list) {
		updateBackupStatus(VortexEyeBackup.STATUS_RUNNING, "Retrieving files for: " + currentBackupItem.describeState(), worker);
		VortexAgentGetBackupChangedFilesRequest req = getTools().nw(VortexAgentGetBackupChangedFilesRequest.class);
		req.setBackupPath(currentBackupItem.fullPath);
		req.setDestinationManifest(list);
		req.setInvokedBy(invokedBy);
		worker.sendRequestToAgent(this, req, currentBackupItem.srcMachine.getAgentState().getProcessUid());
		step = STEP3_GET_CHANGED_FILES_FROM_SOURCE;
	}
	private void sendChangedFilesToDestination(VortexEyeItineraryWorker worker, List<VortexAgentFile> list) {
		updateBackupStatus(VortexEyeBackup.STATUS_RUNNING, "Storing files for: " + currentBackupItem.describeState(), worker);
		VortexAgentSendBackupFilesToDestinationRequest req = getTools().nw(VortexAgentSendBackupFilesToDestinationRequest.class);
		req.setSourceMuid(currentBackupItem.srcMachine.getMuid());
		req.setSourceHostName(currentBackupItem.srcMachine.getRemoteHost());
		req.setDestinationPath(currentBackupItem.dest.getDestinationPath());
		req.setBackupPath(currentBackupItem.fullPath);
		req.setInvokedBy(invokedBy);
		req.setFiles(list);
		req.setIsComplete(currentBackupItem.isComplete);
		worker.sendRequestToAgent(this, req, currentBackupItem.dstMachine.getAgentState().getProcessUid());
		step = STEP4_SEND_CHANGED_FILES_TO_DEST;
	}
	private boolean popNextBackupItem() {
		if (currentBackupItem != null)
			processedBackupItems.add(currentBackupItem);
		if (remainingBackupItems.isEmpty()) {
			currentBackupItem = null;
			return false;
		} else {
			currentBackupItem = remainingBackupItems.remove();
			return true;
		}

	}

	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		return r;
	}

	static public DbRequestMessage insertBackupStatus(VortexEyeBackup backup, ContainerTools tools) {
		boolean active = backup.getRevision() < VortexAgentEntity.REVISION_DONE;
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("active", active);
		params.put("revision", backup.getRevision());
		params.put("now", backup.getNow());
		params.put("id", backup.getId());
		params.put("status", backup.getStatus());
		params.put("message", backup.getMessage());
		params.put("invoked_by", backup.getInvokedBy());
		DbRequestMessage dbRequest = tools.nw(DbRequestMessage.class);
		dbRequest.setId("insert_backup_status");
		dbRequest.setParams(params);
		return dbRequest;
	}
	@Override
	protected void populateAuditEvent(VortexEyeRunBackupRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_RUN_BACKUP);
		auditList(sink, "BUID", action.getBackups());
	}
}
