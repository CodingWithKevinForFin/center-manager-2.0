package com.vortex.agent.itinerary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.base.BasicTypes;
import com.f1.base.Message;
import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.container.ResultMessage;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.LH;
import com.f1.utils.LongArrayList;
import com.f1.utils.MH;
import com.f1.utils.VH;
import com.f1.utils.converter.bytes.BasicToByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;
import com.f1.utils.structs.Tuple2;
import com.f1.vortexcommon.msg.VortexEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentChanges;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentChangesRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentChangesResponse;
import com.vortex.agent.VortexAgentUtils;
import com.vortex.agent.messages.VortexAgentOsAdapterRequest;
import com.vortex.agent.messages.VortexAgentOsAdapterResponse;

public class VortexAgentEyeChangesRequestItinerary extends AbstractVortexAgentItinerary<VortexAgentChangesRequest> {
	private static final Logger log = LH.get(VortexAgentEyeChangesRequestItinerary.class);

	private Map<String, VortexAgentEntity> entities = new HashMap<String, VortexAgentEntity>();
	private VortexAgentMachine machine;
	@Override
	public byte startJourney(VortexAgentItineraryWorker worker) {
		//worker.sendRequestToOsAdapter(this, newOsRequest(VortexAgentOsAdapterRequest.INSPECT_CRON));
		//worker.sendRequestToOsAdapter(this, newOsRequest(VortexAgentOsAdapterRequest.INSPECT_AGENT_MACHINE_EVENTS));
		short mask = getInitialRequest().getAction().getMask();
		if (MH.anyBits(mask, VortexAgentChangesRequest.FILESYSTEMS))
			worker.sendRequestToOsAdapter(this, newOsRequest(VortexAgentOsAdapterRequest.INSPECT_FILESYSTEMS));

		if (MH.anyBits(mask, VortexAgentChangesRequest.MACHINE))
			worker.sendRequestToOsAdapter(this, newOsRequest(VortexAgentOsAdapterRequest.INSPECT_MACHINE));

		if (MH.anyBits(mask, VortexAgentChangesRequest.NET_ADDRESSES))
			worker.sendRequestToOsAdapter(this, newOsRequest(VortexAgentOsAdapterRequest.INSPECT_NET_ADDRESSES));

		if (MH.anyBits(mask, VortexAgentChangesRequest.NET_CONNECTIONS))
			worker.sendRequestToOsAdapter(this, newOsRequest(VortexAgentOsAdapterRequest.INSPECT_NET_CONNECTIONS));

		if (MH.anyBits(mask, VortexAgentChangesRequest.NET_LINKS))
			worker.sendRequestToOsAdapter(this, newOsRequest(VortexAgentOsAdapterRequest.INSPECT_NET_LINKS));

		if (MH.anyBits(mask, VortexAgentChangesRequest.PROCESSES))
			worker.sendRequestToOsAdapter(this, newOsRequest(VortexAgentOsAdapterRequest.INSPECT_PROCESSES));

		if (MH.anyBits(mask, VortexAgentChangesRequest.CRON))
			worker.sendRequestToOsAdapter(this, newOsRequest(VortexAgentOsAdapterRequest.INSPECT_CRON));

		return getPendingRequests().isEmpty() ? STATUS_COMPLETE : STATUS_ACTIVE;
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
		VortexAgentChangesResponse res = getState().nw(VortexAgentChangesResponse.class);
		ObjectToByteArrayConverter converter = (ObjectToByteArrayConverter) getState().getPartition().getContainer().getServices().getConverter();
		long now = getTools().getNow();

		//handle the machine
		VortexAgentMachine existingMachine = getState().getMachine();
		if (existingMachine == null) {
			this.machine.setId(getState().createNextId());
		} else {
			this.machine.setId(existingMachine.getId());
			this.machine.setAgentDetails(existingMachine.getAgentDetails());
		}
		final long machineInstanceId = this.machine.getId();
		this.machine.setMachineInstanceId(machineInstanceId);

		//handle the remaining entities
		final Map<String, Tuple2<VortexAgentEntity, VortexAgentEntity>> joined = CH.join(getState().getEntitiesMap(), entities);
		final List<VortexAgentEntity> added = new ArrayList<VortexAgentEntity>();
		FastByteArrayDataOutputStream updates = new FastByteArrayDataOutputStream();
		ToByteArrayConverterSession updatesSession = new BasicToByteArrayConverterSession(converter, updates, false);
		final LongArrayList removed = new LongArrayList();
		joined.put(VortexAgentUtils.getKeyForEntity(this.machine), new Tuple2<VortexAgentEntity, VortexAgentEntity>(getState().getMachine(), this.machine));
		getState().setMachine(this.machine);
		for (Entry<String, Tuple2<VortexAgentEntity, VortexAgentEntity>> entry : joined.entrySet()) {
			final String key = entry.getKey();
			final VortexAgentEntity existing = entry.getValue().getA();
			final VortexAgentEntity current = entry.getValue().getB();
			if (existing == null) {
				current.setId(getState().createNextId());
				current.setMachineInstanceId(machineInstanceId);
				added.add(current);
				getState().addEntity(key, current);
			} else if (current == null) {
				removed.add(existing.getMachineInstanceId());
				removed.add(existing.getId());
				getState().removeEntity(key);
			} else {
				boolean isChanged = false;
				for (ValuedParam<VortexAgentEntity> v : VH.getValuedParams(existing)) {
					switch (v.getPid()) {
						case VortexEntity.PID_ID:
						case VortexAgentEntity.PID_MACHINE_INSTANCE_ID:
						case VortexEntity.PID_NOW:
						case VortexEntity.PID_REVISION:
							continue;
						default:
							if (!v.areEqual(existing, current)) {
								if (!isChanged) {
									isChanged = true;
									updates.writeLong(existing.getMachineInstanceId());
									updates.writeLong(existing.getId());
								}
								updates.writeByte(v.getPid());
								try {
									if (v.isPrimitive()) {
										updates.writeByte(v.getBasicType());
										v.write(current, updates);
									} else
										converter.write(v.getValue(current), updatesSession);
								} catch (IOException e) {
									throw new RuntimeException("IO with writting: " + v + ", " + current, e);
								}
								v.copy(current, existing);
							}
					}
				}
				if (isChanged) {
					current.setNow(now);
					updates.writeByte(VortexEntity.PID_NOW);
					updates.writeByte(BasicTypes.PRIMITIVE_LONG);
					updates.writeLong(now);
					updates.write(Valued.NO_PID);
				}
			}
		}

		VortexAgentChanges changes = nw(VortexAgentChanges.class);
		//res.setChanges(changes);
		if (!added.isEmpty())
			changes.setAgentEntitiesAdded(added);
		if (updates.size() != 0)
			changes.setAgentEntitiesUpdated(updates.toByteArray());
		if (!removed.isEmpty())
			changes.setAgentEntitiesRemoved(removed.toLongArray());
		changes.setSeqNum(getState().nextSequenceNumber());
		changes.setAgentProcessUid(EH.getProcessUid());
		//log.fine("Sending seqnum: " + changes.getSeqNum());
		worker.sendToEye(this, changes);

		return res;
	}

	//Step 1) Get snapshot
	//Step 2) Diff to current snapshot
	//Step 3) Get Java snapshot
	//Step 4) respond

}
