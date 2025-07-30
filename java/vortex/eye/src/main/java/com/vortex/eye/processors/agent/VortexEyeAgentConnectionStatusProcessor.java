package com.vortex.eye.processors.agent;

import java.util.ArrayList;
import java.util.List;

import com.f1.container.ThreadScope;
import com.f1.povo.msg.MsgStatusMessage;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.agent.VortexAgentBackupFile;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentSnapshotRequest;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.VortexEyeChanges;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.processors.VortexEyeBasicProcessor;
import com.vortex.eye.state.VortexEyeAgentState;
import com.vortex.eye.state.VortexEyeF1AppState;
import com.vortex.eye.state.VortexEyeMachineState;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeAgentConnectionStatusProcessor extends VortexEyeBasicProcessor<MsgStatusMessage> {

	private static final byte[] BACKUP_STATUS_PID = new byte[] { VortexAgentBackupFile.PID_STATUS };

	public VortexEyeAgentConnectionStatusProcessor() {
		super(MsgStatusMessage.class);
	}

	@Override
	public void processAction(MsgStatusMessage action, VortexEyeState state, ThreadScope threadScope) throws Exception {
		if (!"f1.server.to.agent".equals(action.getTopic()))
			return;
		String processUid = action.getSuffix();
		if (SH.isnt(processUid))
			return;
		final long now = getTools().getNow();

		if (action.getIsConnected()) {
			VortexEyeAgentState existing = state.getAgentByPuidNoThrow(processUid);
			if (existing != null) {
				LH.warning(log, "Received a duplicate connection from: ", processUid);
			} else {
				LH.info(log, "Received a new connection from: '", action.getRemoteHost(), "', puid: ", processUid, ". Requesting snapshot.");
				VortexAgentSnapshotRequest req = nw(VortexAgentSnapshotRequest.class);
				sendRequestToAgent(req, processUid, getResponseRoutingPort(), 0);
				state.createAgentState(processUid, action.getRemoteHost(), action.getRemotePort(), now);
			}

		} else {
			if (state.getAgentPuids().contains(processUid)) {
				final VortexEyeAgentState toRemove = state.getAgentByPuid(processUid);
				final VortexEyeChangesMessageBuilder msgBuilder = state.getChangesMessageBuilder();
				for (VortexEyeF1AppState app : toRemove.getF1Apps())
					msgBuilder.writeRemove(app.getF1AppInstance());
				List<VortexAgentBackupFile> backups = new ArrayList<VortexAgentBackupFile>();
				for (VortexEyeMachineState mac : toRemove.getMachines()) {
					for (VortexAgentBackupFile backupFile : state.getBackupFileByMiid(mac.getMiid())) {
						VortexAgentBackupFile bf = backupFile.clone();
						bf.setStatus(VortexAgentBackupFile.STATUS_OFFLINE);
						msgBuilder.writeUpdate(bf, BACKUP_STATUS_PID);
						backups.add(bf);
					}
					mac.getMachine().setAgentProcessUid(null);
					mac.getMachine().setAgentDetails(null);
					msgBuilder.writeUpdate(mac.getMachine(), VortexAgentMachine.PID_AGENT_PROCESS_UID);
					msgBuilder.writeUpdate(mac.getMachine(), VortexAgentMachine.PID_AGENT_DETAILS);
				}
				for (VortexAgentBackupFile backupFile : backups)
					state.addBackupFile(backupFile);
				VortexEyeAgentState removedAgent = state.removeAgent(processUid);
				LH.info(log, "Received an orderly disconnect from agent: ", processUid, " at ", removedAgent.getRemoteHost());
				LH.info(log, "Cleaning up f1 snapshots: ", removedAgent.getF1AppPuids());
				LH.info(log, "Marking machines as disconnected: ", removedAgent.getMachineMuids());
				for (String machineUid : removedAgent.getMachineMuids()) {
					for (VortexDeployment dep : state.getDeploymentsByMachineUid(machineUid)) {
						if (dep.getStatus() != VortexDeployment.STATUS_PROCESS_AGENT_DOWN___) {
							dep = dep.clone();
							dep.setNow(now);
							dep.setStatus(VortexDeployment.STATUS_PROCESS_AGENT_DOWN___);
							msgBuilder.writeUpdate(dep, VortexDeployment.PID_STATUS);
							state.addDeployment(dep);
							VortexEyeAgentDeploymentChangesProcessor.insertDeploymentStatus(dep, this.getTools());
						}
					}
				}

				//VortexEyeAmiUtils.processAgentAmiDisconnect(state, toRemove, this, now, msgBuilder);
				VortexEyeChanges changes = msgBuilder.popToChangesMsg(state.nextSequenceNumber());
				sendToClients(changes);

			} else {
				LH.warning(log, "Received disconnect from unknown agent: ", processUid);
			}

		}
		//sendToClients(deltas);
	}

}
