package com.vortex.eye.processors.agent;

import java.util.ArrayList;
import java.util.List;

import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.vortexcommon.msg.agent.VortexAgentBackupFile;
import com.f1.vortexcommon.msg.agent.VortexAgentChanges;
import com.f1.vortexcommon.msg.agent.VortexAgentDbServer;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentChangesRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentSnapshotResponse;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentUpdateBackupRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentUpdateDeploymentRequest;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;
import com.f1.vortexcommon.msg.eye.VortexEyeChanges;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.VortexEyeUtils;
import com.vortex.eye.processors.VortexEyeResultProcessor;
import com.vortex.eye.state.VortexEyeAgentState;
import com.vortex.eye.state.VortexEyeMachineState;
import com.vortex.eye.state.VortexEyeState;
import com.vortex.eye.state.VortexEyeStateUtils;

public class VortexEyeAgentSnapshotResponseProcessor extends VortexEyeResultProcessor<VortexAgentSnapshotResponse> {

	//public final OutputPort<ResultMessage<AgentInspectDbResponse>> inspectDbResponsePort = (OutputPort) newOutputPort(ResultMessage.class);
	//public final OutputPort<ResultMessage<AgentChangesResponse>> changesResponsePort = (OutputPort) newOutputPort(ResultMessage.class);

	public VortexEyeAgentSnapshotResponseProcessor() {
		super(VortexAgentSnapshotResponse.class);
	}

	@Override
	public void processAction(ResultMessage<VortexAgentSnapshotResponse> result, VortexEyeState state, ThreadScope threadScope) throws Exception {
		final long now = getTools().getNow();
		if (!VortexEyeUtils.verifyOk(log, result))
			return;

		final VortexAgentChanges changes = result.getAction().getSnapshot();
		final String agentProcessUid = changes.getAgentProcessUid();

		//validation
		if (changes.getF1AppEntitiesUpdated() != null)
			throw new RuntimeException("snapshot should not have f1 entity updates");
		if (changes.getF1AppEntitiesRemoved() != null)
			throw new RuntimeException("snapshot should not have f1 entity removes");
		if (changes.getF1AppEvents() != null)
			throw new RuntimeException("snapshot should not have f1 events");
		if (changes.getAgentEntitiesUpdated() != null)
			throw new RuntimeException("snapshot should not have agent entity updates");
		if (changes.getAgentEntitiesRemoved() != null)
			throw new RuntimeException("snapshot should not have agent entity removes");
		if (OH.ne(agentProcessUid, VortexEyeUtils.getProcessUidFromF1AppResponse(result)))
			throw new RuntimeException("bad processUid: " + agentProcessUid + ", expecting: " + VortexEyeUtils.getProcessUidFromF1AppResponse(result));
		VortexEyeAgentState agentState = state.getAgentByPuidNoThrow(agentProcessUid);
		if (agentState == null)
			throw new IllegalStateException("agent not connected: " + agentProcessUid);
		if (agentState.getCurrentSeqNum() != -1)
			throw new IllegalStateException("agent already has sequence number, can  not process snapshot: " + agentProcessUid);
		agentState.setCurrentSeqNum(changes.getSeqNum());

		VortexEyeChangesMessageBuilder msgBuilder = state.getChangesMessageBuilder();
		VortexEyeStateUtils.processF1AppEntityAdds(agentState, changes.getF1AppEntitiesAdded(), this, now, msgBuilder);
		VortexEyeStateUtils.processAgentEntityAdds(agentState, changes.getAgentEntitiesAdded(), this, now, msgBuilder);
		//VortexEyeAmiUtils.processAgentAmiEvents(state, agentState, changes.getAmiEvents(), changes.getAmiStringPoolMap(), this, now, msgBuilder);
		if (msgBuilder.hasChanges()) {
			VortexEyeChanges toClient = msgBuilder.popToChangesMsg(state.nextSequenceNumber());
			//toClient.setAmiKeysStringPoolMap(state.popPendingNewAmiKeysSink());
			//toClient.setAmiValuesStringPoolMap(state.popPendingAmiValuesStringPool());
			sendToClients(toClient);
		}
		//build the f1 applications

		//TODO:
		//VortexChangesBroadcast broadcast = nw(VortexChangesBroadcast.class);
		//broadcast.setAdded(VH.cloneListEntries(agentState.getEntities()));
		//sendToClients(broadcast);

		VortexAgentChangesRequest req = nw(VortexAgentChangesRequest.class);
		req.setMask((short) VortexAgentChangesRequest.SNAPSHOT);
		sendRequestToAgent(req, agentProcessUid, getResponseRoutingPort(), 0);

		//loop through each machine and check if there are configurations pertinent for it
		for (VortexEyeMachineState machine : agentState.getMachines()) {
			String muid = machine.getMuid();
			//Find applicable databases for this agent
			int dbCount = 0;
			for (VortexAgentDbServer server : CH.i(state.getDbServers())) {
				if (OH.eq(muid, server.getMachineUid())) {
					//VortexEyeRunDbInspectionRequest d = nw(VortexEyeRunDbInspectionRequest.class);
					//d.setDbServerId(server.getId());
					//RequestMessage<VortexEyeRunDbInspectionRequest> dbRequest = nw(RequestMessage.class);
					//dbRequest.setAction(d);
					//startItinerary(new VortexEyeInspectDbSchemaItinerary(), dbRequest);
					dbCount++;
				}
			}

			//Find applicable rules for this agent
			//			final int rulesCount = state.getAuditTrailRulesCount();
			//			final List<VortexEyeAuditTrailRule> rules = new ArrayList<VortexEyeAuditTrailRule>(rulesCount);
			//			for (VortexEyeAuditTrailRule rule : state.getAuditTrailRules())
			//				rules.add(rule);
			//			final VortexEyeAuditTrailRuleSet batch = nw(VortexEyeAuditTrailRuleSet.class);
			//			batch.setRules(rules);
			//			batch.setIsSnapshot(true);
			//			sendToAgent(batch, agentProcessUid);

			//Find applicable Deployments for this agent
			List<VortexDeployment> deployments = new ArrayList<VortexDeployment>();
			for (VortexDeployment dep : state.getDeployments())
				if (OH.eq(machine.getMuid(), dep.getTargetMachineUid()))
					deployments.add(dep);

			final VortexAgentUpdateDeploymentRequest update = nw(VortexAgentUpdateDeploymentRequest.class);
			update.setIsSnapshot(true);
			update.setUpdated(deployments);
			sendToAgent(update, agentProcessUid);

			List<VortexEyeBackup> backups = new ArrayList<VortexEyeBackup>();
			ArrayList<VortexAgentBackupFile> backupFiles = new ArrayList<VortexAgentBackupFile>(state.getBackupFileByMiid(machine.getMiid()));
			for (VortexEyeBackup backup : state.getBackups())
				if (OH.eq(muid, backup.getSourceMachineUid()))
					backups.add(backup);
			final VortexAgentUpdateBackupRequest backupUpdate = nw(VortexAgentUpdateBackupRequest.class);
			backupUpdate.setIsSnapshot(true);
			backupUpdate.setUpdated(backups);
			backupUpdate.setFiles(backupFiles);
			sendToAgent(backupUpdate, agentProcessUid);

			//			LH.info(log, "Informing agent of ", dbCount, " database(s), ", rulesCount, " audit rule(s), ", deployments.size(), " deployments, ", backups.size(), " backup(s), "
			//					+ backupFiles.size() + " file(s). Agent MUID: ", machine.getMuid() + ". Agent PUID: " + agentProcessUid);
			LH.info(log, "Informing agent of ", dbCount, " database(s), ", deployments.size(), " deployments, ", backups.size(), " backup(s), " + backupFiles.size()
					+ " file(s). Agent MUID: ", machine.getMuid() + ". Agent PUID: " + agentProcessUid);
		}
	}
}
