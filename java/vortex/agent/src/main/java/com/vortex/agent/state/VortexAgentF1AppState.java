package com.vortex.agent.state;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.bootstrap.F1Constants;
import com.f1.bootstrap.appmonitor.AppMonitorUtils;
import com.f1.povo.f1app.F1AppEntity;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.povo.f1app.F1AppProperty;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.structs.LongKeyMap;

public class VortexAgentF1AppState {
	private static final Logger log = Logger.getLogger(VortexAgentF1AppState.class.getName());

	final private F1AppInstance f1AppInstance;
	final private LongKeyMap<F1AppEntity> entitiesByOrigId = new LongKeyMap<F1AppEntity>();
	final private VortexAgentState state;
	final private long f1AppInstanceOrigId;
	private long currentSeqNum;

	private String diid;
	private Map<String, F1AppProperty> properties = new HashMap<String, F1AppProperty>();

	public VortexAgentF1AppState(VortexAgentState state, long origId, F1AppInstance action, long now) {
		this.state = state;
		f1AppInstance = action;
		this.f1AppInstanceOrigId = origId;
		f1AppInstance.setAgentConnectTime(now);
		f1AppInstance.setAgentDisconnectTime(0);
		f1AppInstance.setAgentProcessUid(EH.getProcessUid());
		f1AppInstance.setAgentMachineUid(state.getMachineUid());
		//f1AppInstance.setAgentLatency((int) (now - this.f1AppInstance.getNowMs()));
	}
	public void addEntity(long origId, F1AppEntity entity) {
		if (entity instanceof F1AppInstance)
			throw new IllegalArgumentException("F1AppInstance can not be added as child entity");
		if (entity instanceof F1AppProperty) {
			F1AppProperty prop = (F1AppProperty) entity;
			if (prop.getPosition() == 0)
				properties.put(prop.getKey(), prop);
		}
		entitiesByOrigId.put(origId, entity);
		origIdById.put(entity.getId(), origId);
	}
	public F1AppInstance getF1AppInstance() {
		return f1AppInstance;
	}
	public long getSnapshotOrigId() {
		return f1AppInstanceOrigId;
	}

	public F1AppEntity getByOrigId(long id) {
		F1AppEntity r = entitiesByOrigId.get(id);
		if (r == null) {
			if (id == f1AppInstanceOrigId)
				return f1AppInstance;
			else
				throw new RuntimeException("orig id [" + id + "] not found for f1 app: " + this);
		}
		return r;
	}

	public String toString() {
		return AppMonitorUtils.describe(f1AppInstance);
	}

	public LongKeyMap<F1AppEntity> getEntities() {
		return this.entitiesByOrigId;
	}
	public F1AppEntity removeByOrigId(long l) {
		F1AppEntity r = entitiesByOrigId.removeOrThrow(l);
		if (r instanceof F1AppProperty) {
			F1AppProperty prop = (F1AppProperty) r;
			if (prop.getPosition() == 0)
				CH.removeOrThrow(properties, prop.getKey());
		}
		origIdById.removeOrThrow(r.getId());
		return r;
	}
	public VortexAgentState getState() {
		return state;
	}
	public long getCurrentSeqNum() {
		return currentSeqNum;
	}
	public void setCurrentSeqNum(long currentSeqNum) {
		this.currentSeqNum = currentSeqNum;
	}
	public F1AppProperty getProperty(String key) {
		return properties.get(key);
	}
	final private LongKeyMap<Long> origIdById = new LongKeyMap<Long>();

	public Long getOrigIdById(long id) {
		Long r = origIdById.get(id);
		if (r == null) {
			if (id == this.f1AppInstance.getId())
				return this.f1AppInstanceOrigId;
			else
				return null;
		}
		return r;
	}
	public String getDiid() {
		final F1AppProperty diidProp = getProperty(F1Constants.PROPERTY_DEPLOYMENT_INSTANCE_ID);
		return diidProp == null ? null : diidProp.getValue();
	}
	public String getPuid() {
		return this.f1AppInstance.getProcessUid();
	}

}
