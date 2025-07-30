package com.vortex.eye.processors.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.container.exceptions.ContainerException;
import com.f1.povo.f1app.F1AppEntity;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.VH;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeChanges;
import com.f1.vortexcommon.msg.eye.VortexEyeEntity;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeSnapshotRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeSnapshotResponse;
import com.vortex.eye.processors.VortexEyeRequestProcessor;
import com.vortex.eye.state.VortexEyeAgentState;
import com.vortex.eye.state.VortexEyeF1AppState;
import com.vortex.eye.state.VortexEyeMachineState;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeClientSnapshotRequestProcessor extends VortexEyeRequestProcessor<VortexEyeSnapshotRequest, VortexEyeSnapshotResponse> {

	public VortexEyeClientSnapshotRequestProcessor() {
		super(VortexEyeSnapshotRequest.class, VortexEyeSnapshotResponse.class);
	}

	@Override
	protected VortexEyeSnapshotResponse processRequest(RequestMessage<VortexEyeSnapshotRequest> action, VortexEyeState state, ThreadScope threadScope) throws Exception {
		VortexEyeSnapshotRequest request = action.getAction();
		String invokedBy = request.getInvokedBy();
		if (SH.isnt(invokedBy)) {
			throw new ContainerException("Received snapshotrequest from unknown user");
		}

		LH.info(log, "Received Snapshot Request From: ", invokedBy);
		List<VortexAgentEntity> entities = new ArrayList<VortexAgentEntity>();
		List<F1AppEntity> f1Entities = new ArrayList<F1AppEntity>();
		for (VortexEyeMachineState machine : state.getAllMachines()) {
			entities.add(machine.getMachine());
			if (machine.getAgentState() != null)//is connected?
				entities.addAll(machine.getEntities());
			//for (VortexAmiApplication ami : machine.getAmiApplications()) {
			//entities.add(ami);
			//entities.addAll(machine.getAmiAlerts(ami.getId()));
			//entities.addAll(machine.getAmiObjects(ami.getId()));
			//}
		}
		//		boolean[] types = new boolean[Short.MAX_VALUE + 1];
		//		for (String type : request.getAmiObjectTypesToSend())
		//			types[state.getAmiKeyIdNoCreate(type)] = true;
		//		types[0] = false;
		//
		//		int amiCount = 0;
		//		for (VortexEyeAmiApplication ami : state.getAmiApplications()) {
		//			amiCount += ami.getAmiAlertsCount();
		//			for (VortexAmiObject i : ami.getAmiObjects())
		//				if (types[i.getType()])
		//					amiCount++;
		//		}
		//		for (VortexEyeAmiConnection c : state.getAmiConnections())
		//			amiCount += 1 + c.getCommandsCount();
		//		final List<VortexAmiEntity> amiEntities = new ArrayList<VortexAmiEntity>(amiCount);
		//		for (VortexEyeAmiConnection i : state.getAmiConnections()) {
		//			amiEntities.add(i.getConnection());
		//			amiEntities.addAll(i.getCommands());
		//		}
		//		for (VortexEyeAmiApplication ami : state.getAmiApplications()) {
		//			for (VortexAmiAlert i : ami.getAmiAlerts())
		//				amiEntities.add(i);
		//			for (VortexAmiObject i : ami.getAmiObjects()) {
		//				if (types[i.getType()])
		//					amiEntities.add(i);
		//			}
		//		}
		for (VortexEyeAgentState agent : state.getAgents()) {
			for (VortexEyeF1AppState f1App : agent.getF1Apps()) {
				f1Entities.add(f1App.getF1AppInstance());
				f1Entities.addAll(CH.l(f1App.getEntities().values()));
			}
		}

		//TODO: AMI OBJECTS

		List<VortexEyeEntity> eyeEntitiesAdded = new ArrayList<VortexEyeEntity>();
		CH.addAll(eyeEntitiesAdded, state.getMetadataFields());
		CH.addAll(eyeEntitiesAdded, state.getBuildProcedures());
		CH.addAll(eyeEntitiesAdded, state.getBuildResults());
		CH.addAll(eyeEntitiesAdded, state.getDeploymentSets());
		CH.addAll(eyeEntitiesAdded, state.getDeployments());
		CH.addAll(eyeEntitiesAdded, state.getBackups());
		CH.addAll(eyeEntitiesAdded, state.getBackupDestinations());
		CH.addAll(eyeEntitiesAdded, state.getExpectations());
		CH.addAll(eyeEntitiesAdded, state.getAuditTrailRules());
		CH.addAll(eyeEntitiesAdded, state.getDbServers());
		CH.addAll(eyeEntitiesAdded, state.getScheduledTasks());
		CH.addAll(eyeEntitiesAdded, state.getJournal().getReports());
		CH.addAll(eyeEntitiesAdded, state.getCloudInterfaces());
		CH.addAll(eyeEntitiesAdded, state.getClientEvents());
		CH.addAll(eyeEntitiesAdded, state.getCloudMachineInfos());
		CH.addAll(entities, state.getBackupFiles());
		long seqNum = state.currentSequenceNumber();
		LH.info(log, "Returning Snapshot to: ", invokedBy, " at seqnum ", seqNum, ". Entity Counts: ", eyeEntitiesAdded.size(), " eye, ", entities.size(), " agent, ",
				f1Entities.size(), " app");

		//build intermediate result message

		final int totalObjects = (eyeEntitiesAdded.size() + entities.size() + f1Entities.size());

		if (request.getSupportsIntermediate()) {

			sendIntermediateResponse(action, totalObjects, seqNum, null, null, null);

			int maxBatchSize = Math.max(10000, request.getMaxBatchSize());

			for (List<VortexAgentEntity> i : CH.batchSublists(entities, maxBatchSize, true))
				sendIntermediateResponse(action, totalObjects, seqNum, null, i, null);

			for (List<VortexEyeEntity> i : CH.batchSublists(eyeEntitiesAdded, maxBatchSize, true))
				sendIntermediateResponse(action, totalObjects, seqNum, i, null, null);

			for (List<F1AppEntity> i : CH.batchSublists(f1Entities, maxBatchSize, true))
				sendIntermediateResponse(action, totalObjects, seqNum, null, null, i);

			//			for (List<VortexAmiEntity> i : CH.batchSublists(amiEntities, maxBatchSize, true))
			//				sendIntermediateResponse(action, totalObjects, seqNum, null, null, null);

			VortexEyeSnapshotResponse response = nw(VortexEyeSnapshotResponse.class);
			final VortexEyeChanges changes = nw(VortexEyeChanges.class);
			changes.setEyeProcessUid(EH.getProcessUid());
			changes.setSeqNum(state.currentSequenceNumber());
			response.setSnapshot(changes);
			response.setOk(true);
			return response;
		} else {
			final VortexEyeChanges changes = nw(VortexEyeChanges.class);
			changes.setEyeEntitiesAdded(eyeEntitiesAdded);
			changes.setAgentEntitiesAdded(VH.cloneListEntries(entities));
			changes.setF1AppEntitiesAdded(VH.cloneListEntries(f1Entities));
			changes.setEyeProcessUid(EH.getProcessUid());
			//changes.setAmiEntitiesAdded(amiEntities);
			changes.setSeqNum(state.currentSequenceNumber());
			//changes.setAmiKeysStringPoolMap(new HashMap<Short, String>(state.getAmiStringPoolMap()));
			//changes.setAmiValuesStringPoolMap(state.getAmiValuesStringPoolAsBytes());
			VortexEyeSnapshotResponse response = nw(VortexEyeSnapshotResponse.class);
			response.setSnapshot(changes);
			response.setOk(true);

			return response;
		}
	}
	private void sendIntermediateResponse(RequestMessage<VortexEyeSnapshotRequest> action, int totalObject, long seqNum, List<VortexEyeEntity> eyeEntitiesAdded,
			Collection<VortexAgentEntity> entities, Collection<F1AppEntity> f1Entities) {
		final VortexEyeChanges changes = nw(VortexEyeChanges.class);
		changes.setEyeEntitiesAdded(eyeEntitiesAdded == null ? Collections.EMPTY_LIST : eyeEntitiesAdded);
		changes.setAgentEntitiesAdded(entities == null ? Collections.EMPTY_LIST : VH.cloneListEntries(entities));
		changes.setF1AppEntitiesAdded(f1Entities == null ? Collections.EMPTY_LIST : VH.cloneListEntries(f1Entities));
		changes.setEyeProcessUid(EH.getProcessUid());
		changes.setSeqNum(seqNum);
		VortexEyeSnapshotResponse tmpResponse = nw(VortexEyeSnapshotResponse.class);
		tmpResponse.setTotalObjectsCount(totalObject);
		tmpResponse.setSnapshot(changes);
		ResultMessage<VortexEyeSnapshotResponse> tmp = nw(ResultMessage.class);
		tmp.setAction(tmpResponse);
		tmp.setIsIntermediateResult(true);
		reply(action, tmp, null);
	}
}
