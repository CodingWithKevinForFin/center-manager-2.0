package com.vortex.agent.itinerary;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.base.Message;
import com.f1.base.ValuedParam;
import com.f1.container.ResultMessage;
import com.f1.povo.f1app.F1AppEntity;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.VH;
import com.f1.utils.structs.Tuple2;
import com.f1.vortexcommon.msg.VortexEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentChanges;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentSnapshotRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentSnapshotResponse;
import com.vortex.agent.VortexAgentUtils;
import com.vortex.agent.messages.VortexAgentOsAdapterRequest;
import com.vortex.agent.messages.VortexAgentOsAdapterResponse;
import com.vortex.agent.state.VortexAgentF1AppState;

public class VortexAgentEyeSnapshotRequestItinerary extends AbstractVortexAgentItinerary<VortexAgentSnapshotRequest> {

	private static final Logger log = LH.get(VortexAgentEyeSnapshotRequestItinerary.class);

	private Map<String, VortexAgentEntity> entities = new HashMap<String, VortexAgentEntity>();
	private VortexAgentMachine machine;
	@Override
	public byte startJourney(VortexAgentItineraryWorker worker) {
		worker.sendRequestToOsAdapter(this, newOsRequest(VortexAgentOsAdapterRequest.INSPECT_MACHINE));
		worker.sendRequestToOsAdapter(this, newOsRequest(VortexAgentOsAdapterRequest.INSPECT_NET_ADDRESSES));
		worker.sendRequestToOsAdapter(this, newOsRequest(VortexAgentOsAdapterRequest.INSPECT_FILESYSTEMS));

		//worker.sendRequestToOsAdapter(this, newOsRequest(VortexAgentOsAdapterRequest.INSPECT_AGENT_MACHINE_EVENTS));

		worker.sendRequestToOsAdapter(this, newOsRequest(VortexAgentOsAdapterRequest.INSPECT_NET_CONNECTIONS));
		worker.sendRequestToOsAdapter(this, newOsRequest(VortexAgentOsAdapterRequest.INSPECT_NET_LINKS));
		worker.sendRequestToOsAdapter(this, newOsRequest(VortexAgentOsAdapterRequest.INSPECT_PROCESSES));
		worker.sendRequestToOsAdapter(this, newOsRequest(VortexAgentOsAdapterRequest.INSPECT_CRON));
		return STATUS_ACTIVE;
	}

	private VortexAgentOsAdapterRequest newOsRequest(int commandType) {
		VortexAgentOsAdapterRequest r = getState().nw(VortexAgentOsAdapterRequest.class);
		r.setCommandType(commandType);
		r.setPartitionId(VortexAgentOsAdapterRequest.PARTITION_DEFAULT);
		return r;
	}

	@Override
	public byte onResponse(ResultMessage<?> result, VortexAgentItineraryWorker worker) {
		VortexAgentOsAdapterRequest req = (VortexAgentOsAdapterRequest) result.getRequestMessage().getAction();
		VortexAgentOsAdapterResponse res = (VortexAgentOsAdapterResponse) result.getAction();
		if (req.getCommandType() == VortexAgentOsAdapterRequest.INSPECT_MACHINE)
			this.machine = (VortexAgentMachine) res.getEntities().get(0);
		else {
			if (result.getError() == null)
				VortexAgentUtils.keyAgentMachineEvents(res.getEntities(), entities);
			else
				log.warning("Error Running command: " + result.getError().getMessage());

		}
		return getPendingRequests().isEmpty() ? STATUS_COMPLETE : STATUS_ACTIVE;
	}
	@Override
	public Message endJourney(VortexAgentItineraryWorker worker) {
		VortexAgentSnapshotResponse res = getState().nw(VortexAgentSnapshotResponse.class);
		//handle the machine
		VortexAgentMachine existingMachine = getState().getMachine();
		if (existingMachine == null)
			this.machine.setId(getState().createNextId());
		else
			this.machine.setId(existingMachine.getId());
		final long machineInstanceId = this.machine.getId();
		this.machine.setMachineInstanceId(machineInstanceId);
		String version = getTools().getOptional("vortex.agent.version");
		String deployuid = getTools().getOptional("vortex.agent.deployuid");
		String eyeInterface = getTools().getOptional("vortex.agent.eye.interface");
		String path = IOH.getFullPath(new File("."));
		getState().setMachine(this.machine);
		//getState().getMachine().setAgentVersion(version);
		getState().getMachine().setAgentDetails((Map) CH.m("version", version, "path", path, "eye_interface", eyeInterface, "deployuid", SH.noNull(deployuid)));

		//handle other entities
		final Map<String, Tuple2<VortexAgentEntity, VortexAgentEntity>> joined = CH.join(getState().getEntitiesMap(), entities);
		final List<VortexAgentEntity> added = new ArrayList<VortexAgentEntity>();
		added.add(machine.clone());
		for (Entry<String, Tuple2<VortexAgentEntity, VortexAgentEntity>> entry : joined.entrySet()) {
			final String key = entry.getKey();
			final VortexAgentEntity existing = entry.getValue().getA();
			final VortexAgentEntity current = entry.getValue().getB();
			if (existing == null) {
				current.setId(getState().createNextId());
				current.setMachineInstanceId(machineInstanceId);
				getState().addEntity(key, current);
				added.add(current);//TODO:clone
			} else if (current == null) {
				getState().removeEntity(key);
			} else {
				for (ValuedParam<VortexAgentEntity> v : VH.getValuedParams(existing)) {
					switch (v.getPid()) {
						case VortexEntity.PID_ID:
						case VortexEntity.PID_NOW:
						case VortexAgentEntity.PID_MACHINE_INSTANCE_ID:
						case VortexEntity.PID_REVISION:
							continue;
						default:
							if (!v.areEqual(existing, current))
								v.copy(current, existing);
					}
				}
				added.add(existing);//TODO:clone
			}
		}

		final List<F1AppEntity> f1AppEntities = new ArrayList<F1AppEntity>();
		for (VortexAgentF1AppState app : getState().getApps()) {
			F1AppInstance cloned = app.getF1AppInstance().clone();
			cloned.setAgentMachineUid(machine.getMachineUid());
			f1AppEntities.add(cloned);
		}
		for (VortexAgentF1AppState app : getState().getApps())
			for (F1AppEntity entity : app.getEntities().values())
				f1AppEntities.add(entity.clone());

		final VortexAgentChanges changes = nw(VortexAgentChanges.class);
		changes.setAgentEntitiesAdded(added);
		changes.setF1AppEntitiesAdded(f1AppEntities);
		changes.setSeqNum(getState().currentSequenceNumber());
		changes.setAgentProcessUid(EH.getProcessUid());
		//		changes.setAmiStringPoolMap(getState().getAmiStringKeys());
		//		List<AgentAmiMessage> amiEvents = new ArrayList<AgentAmiMessage>();
		//		for (VortexAgentAmiConnectionState i : getState().getActiveAmiLogins())
		//			i.drainEvent(amiEvents);
		//changes.setAmiEvents(amiEvents);

		res.setSnapshot(changes);
		res.setOk(true);

		getState().setIsSnapshotSentToEye(true);
		return res;
	}

	//Step 1) Get snapshot
	//Step 2) Diff to current snapshot
	//Step 3) Get Java snapshot
	//Step 4) respond
}
