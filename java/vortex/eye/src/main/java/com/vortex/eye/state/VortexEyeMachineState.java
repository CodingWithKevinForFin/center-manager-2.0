package com.vortex.eye.state;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OneToOne;
import com.f1.vortexcommon.msg.VortexEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.vortex.agent.VortexEntityMap;

public class VortexEyeMachineState {

	final private VortexEntityMap entitiesByType = new VortexEntityMap();

	final private static Logger log = Logger.getLogger(VortexEyeMachineState.class.getName());

	final private String machineUid;
	final private VortexAgentMachine machine;
	final private VortexEyeState eyeState;
	final private long miid;
	private VortexEyeAgentState agentState;
	private long origMiid;

	protected VortexEyeMachineState(VortexEyeState eyeState, VortexAgentMachine machine) {
		this.eyeState = eyeState;
		if (machine.getMachineUid() == null)
			throw new NullPointerException("machineUid required: " + machine);
		if (machine.getId() <= 0)
			throw new IllegalArgumentException("invalid machineId: " + machine);
		if (machine.getMachineInstanceId() != machine.getId())
			throw new IllegalArgumentException("machineId / id mismatch: " + machine);
		this.machine = machine;
		this.miid = machine.getMachineInstanceId();
		this.machineUid = machine.getMachineUid();
	}

	public long getMiid() {
		return miid;
	}

	public String getPuid() {
		return machine.getAgentProcessUid();
		//return processUid;
	}

	public String getRemoteHost() {
		return machine.getHostName();
	}

	public String getMuid() {
		return this.machineUid;
	}

	//orig miid can change
	public long getOrigMiid() {
		return origMiid;
	}

	public void setOrigMiid(long origMiid) {
		if (agentState != null)
			throw new IllegalStateException("orig miid can not change while member of an agent!");
		this.origMiid = origMiid;
	}

	/////////////////////
	//Machine information
	/////////////////////
	private Map<String, VortexAgentEntity> entities = new HashMap<String, VortexAgentEntity>();

	public Set<String> getEntityKeys() {
		return entities.keySet();
	}

	public VortexEntity getEntityByKey(String key) {
		return entities.get(key);
	}

	public void clearAgentIdMapping() {
		agentIdToEyeId.clear();
	}

	public VortexAgentEntity getEntityByOrigId(long id) {
		VortexAgentEntity r = agentIdToEyeId.getValue(id);
		if (r == null) {
			if (id == origMiid)
				return machine;
			else
				throw new RuntimeException("unknown orig id [" + id + "] for " + getRemoteHost());
		}
		return r;
	}
	private OneToOne<Long, VortexAgentEntity> agentIdToEyeId = new OneToOne<Long, VortexAgentEntity>();
	//private String processUid;
	public void addEntityNoThrow(String key, Long fromAgentId, VortexAgentEntity entity) {
		try {
			addEntity(key, fromAgentId, entity);
		} catch (Exception e) {
			LH.warning(log, "Could not add Enity with agentId ", fromAgentId, e);
		}
	}
	public void addEntity(String key, Long fromAgentId, VortexAgentEntity entity) {
		if (entity instanceof VortexAgentMachine)
			throw new IllegalArgumentException("can not add machine as entity: " + entity);
		if (fromAgentId != null)
			agentIdToEyeId.put(fromAgentId, entity);
		try {
			CH.putOrThrow(entities, key, entity);
		} catch (RuntimeException e) {
			if (fromAgentId != null)
				agentIdToEyeId.removeByKey(fromAgentId);
			throw e;
		}
		entitiesByType.add(entity);
	}
	public VortexAgentEntity removeEntity(String key) {
		VortexAgentEntity entity = CH.removeOrThrow(entities, key);
		agentIdToEyeId.removeByValue(entity);
		entitiesByType.remove(entity);
		return entity;
	}
	public Collection<VortexAgentEntity> getEntities() {
		return entities.values();
	}
	public Map<String, VortexAgentEntity> getEntitiesMap() {
		return entities;
	}

	//public void setProcessUid(String processUid) {
	//this.processUid = processUid;
	//}

	public void mapAgentIdToEntity(long id, VortexAgentEntity entity) {
		if (id == this.origMiid) {
			if (entity == machine)
				return;
			else
				throw new RuntimeException("bad mapping: " + id + ", " + entity);
		} else if (entity == machine) {
			this.origMiid = id;
		} else if (entity instanceof VortexAgentMachine)
			throw new RuntimeException("Can not bind orig id to machine: " + id + " ==> " + entity);
		else
			this.agentIdToEyeId.put(id, entity);
	}

	public VortexAgentMachine getMachine() {
		return machine;
	}

	public VortexEntityMap getEntitiesByType() {
		return entitiesByType;
	}

	public VortexEyeState getEyeState() {
		return eyeState;
	}
	public VortexEyeAgentState getAgentState() {
		return agentState;
	}

	public void setAgentState(VortexEyeAgentState agentState) {
		if (this.agentState == agentState)
			return;
		this.agentState = agentState;
		eyeState.onMachineStateAgentChanged(this);
	}

}
