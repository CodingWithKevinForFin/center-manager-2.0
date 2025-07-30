package com.vortex.eye.processors.agent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.container.ContainerTools;
import com.f1.container.OutputPort;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.povo.db.DbRequestMessage;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.vortexcommon.msg.agent.VortexAgentBackupFile;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;
import com.f1.vortexcommon.msg.eye.VortexUpdateBackupStatusesFromAgent;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.itinerary.VortexEyeRunBackupItinerary;
import com.vortex.eye.messages.VortexVaultRequest;
import com.vortex.eye.messages.VortexVaultResponse;
import com.vortex.eye.processors.VortexEyeBasicProcessor;
import com.vortex.eye.state.VortexEyeMachineState;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeAgentBackupChangesProcessor extends VortexEyeBasicProcessor<VortexUpdateBackupStatusesFromAgent> {

	public static byte STATUS_PIDS[] = new byte[] { VortexEyeBackup.PID_BYTES_COUNT, VortexEyeBackup.PID_FILE_COUNT, VortexEyeBackup.PID_IGNORED_FILE_COUNT,
			VortexEyeBackup.PID_LATEST_MODIFIED_TIME };
	public final OutputPort<ResultMessage<VortexVaultResponse>> responsePort = newResultOutputPort(VortexVaultResponse.class);
	public VortexEyeAgentBackupChangesProcessor() {
		super(VortexUpdateBackupStatusesFromAgent.class);
	}

	@Override
	public void processAction(VortexUpdateBackupStatusesFromAgent action, VortexEyeState state, ThreadScope threadScope) throws Exception {
		VortexEyeChangesMessageBuilder cmb = state.getChangesMessageBuilder();
		//Send to guis
		//sendToClients(cmb.popToChangesMsg(state.nextSequenceNumber()));
		long miid = -1, backupId = -1;
		DbRequestMessage head = nw(DbRequestMessage.class);
		DbRequestMessage req = head;
		for (VortexEyeBackup fromAgent : action.getUpdated()) {
			VortexEyeBackup orig = state.getBackup(fromAgent.getId());
			VortexEyeBackup nuw = orig.clone();
			boolean dbChange = false;
			for (byte pid : STATUS_PIDS)
				fromAgent.askSchema().askValuedParam(pid).copy(fromAgent, nuw);
			DbRequestMessage rq = VortexEyeRunBackupItinerary.insertBackupStatus(fromAgent, getTools());
			cmb.writeUpdate(nuw, STATUS_PIDS);
			state.addBackup(nuw);
			req.setNextRequest(rq);
			req = rq;

		}
		Map<Long, byte[]> toVault = new HashMap<Long, byte[]>();
		process(action.getAddedFiles(), state, req, false, cmb, toVault);
		req = process(action.getUpdatedFiles(), state, req, false, cmb, toVault);
		req = process(action.getRemovedFiles(), state, req, true, cmb, toVault);
		req = head.getNextRequest();
		if (req != null) {
			sendToDb(req);
		}

		if (!toVault.isEmpty()) {
			VortexVaultRequest vvq = nw(VortexVaultRequest.class);
			vvq.setDataToStore(toVault);
			sendToVault(vvq, responsePort);
		}

		sendToClients(cmb.popToChangesMsg(state.nextSequenceNumber()));
		//System.out.println("Backup Status: " + CH.size(action.getAdded()) + ", " + CH.size(action.getUpdated()) + ", " + CH.size(action.getRemoved()) + ", ");
	}
	private DbRequestMessage process(List<VortexAgentBackupFile> bu, VortexEyeState state, DbRequestMessage req, boolean remove, VortexEyeChangesMessageBuilder cmb,
			Map<Long, byte[]> toStore) {
		if (CH.isEmpty(bu))
			return req;
		long miid = -1, backupId = -1;
		for (VortexAgentBackupFile bf : bu) {

			if (bf.getBackupId() != backupId) {//resolve miid
				backupId = bf.getBackupId();
				VortexEyeBackup backup = state.getBackup(bf.getBackupId());
				if (backup == null) {
					LH.warning(log, "unknown backup_id for file: " + bf);
					continue;
				}
				VortexEyeMachineState machine = state.getMachineByMuidNoThrow(backup.getSourceMachineUid());
				if (machine == null) {
					LH.warning(log, "unknown machine for muid: " + backup.getSourceMachineUid());
					continue;
				}
				miid = machine.getMiid();
			}

			bf.setMachineInstanceId(miid);
			VortexAgentBackupFile existing = state.getBackupFileByMiidAndPath(miid, bf.getPath());
			boolean sendToDb = true;
			if (remove) {
				if (existing != null) {
					state.removeBackupFile(existing.getId());
					long mtime = bf.getModifiedTime();
					bf = existing.clone();
					bf.setModifiedTime(mtime);
					bf.setRevision(VortexAgentEntity.REVISION_DONE);
					cmb.writeRemove(existing);
				} else {
					LH.warning(log, "delete from unknown file: " + bf);
					continue;
				}
			} else {
				if (bf.getData() != null) {
					long vvid = state.createNextId();
					toStore.put(vvid, bf.getData());
					bf.setDataVvid(vvid);
					bf.setData(null);
				}
				if (existing != null) {
					if (bf.getDataVvid() == 0) {
						bf.setDataVvid(existing.getDataVvid());
						boolean deflated = MH.anyBits(existing.getMask(), VortexAgentFile.DATA_DEFPLATED);
						bf.setMask(MH.setBits(bf.getMask(), VortexAgentFile.DATA_DEFPLATED, deflated));
					}
					if (bf.getChecksum() == existing.getChecksum() && bf.getMask() == existing.getMask() && bf.getSize() == existing.getSize()
							&& bf.getModifiedTime() == existing.getModifiedTime()) {
						sendToDb = false;
						bf.setRevision(existing.getRevision());
					} else {
						bf.setRevision(existing.getRevision() + 1);
					}
					bf.setId(existing.getId());
					state.addBackupFile(bf);
					cmb.writeUpdate(existing, bf);
				} else {
					bf.setRevision(0);
					bf.setId(state.createNextId());
					cmb.writeAdd(bf);
					state.addBackupFile(bf);
				}
			}
			if (sendToDb) {
				DbRequestMessage rq = insertBackupFile(bf, getTools());
				req.setNextRequest(rq);
				req = rq;
			}
		}
		return req;

	}
	static public DbRequestMessage insertBackupFile(VortexAgentBackupFile backupFile, ContainerTools tools) {
		boolean active = backupFile.getRevision() < VortexAgentEntity.REVISION_DONE;
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("active", active);
		params.put("id", backupFile.getId());
		params.put("now", backupFile.getNow());
		params.put("revision", backupFile.getRevision());
		params.put("machine_instance_id", backupFile.getMachineInstanceId());
		params.put("mask", backupFile.getMask());
		params.put("modified_time", backupFile.getModifiedTime());
		params.put("path", backupFile.getPath());
		params.put("checksum", backupFile.getChecksum());
		params.put("size", backupFile.getSize());
		params.put("data_vvid", backupFile.getDataVvid());//deploymentStatus.getD());
		params.put("backup_id", backupFile.getBackupId());
		DbRequestMessage dbRequest = tools.nw(DbRequestMessage.class);
		dbRequest.setId("insert_backup_file");
		dbRequest.setParams(params);
		return dbRequest;
	}

}
