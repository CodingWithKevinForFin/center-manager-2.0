package com.vortex.eye.state;

import java.util.logging.Logger;

import com.f1.bootstrap.appmonitor.AppMonitorUtils;
import com.f1.povo.f1app.F1AppEntity;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.utils.structs.LongKeyMap;

public class VortexEyeF1AppState {
	private static final Logger log = Logger.getLogger(VortexEyeF1AppState.class.getName());
	final private F1AppInstance f1AppInstance;
	final private LongKeyMap<F1AppEntity> entitiesByOrigId = new LongKeyMap<F1AppEntity>();
	final private LongKeyMap<Long> origIdById = new LongKeyMap<Long>();
	private VortexEyeAgentState agentState;
	final private String processUid;
	private VortexEyeState eyeState;
	private long origId;
	final private long id;

	protected VortexEyeF1AppState(VortexEyeState eyeState, F1AppInstance action) {
		this.id = action.getId();
		this.eyeState = eyeState;
		f1AppInstance = action;
		//f1AppInstance.setId(eyeState.createNextId());
		processUid = action.getProcessUid();
		//f1AppInstance.setAgentDisconnectTime(0);
		//f1AppInstance.setAgentProcessUid(EH.getProcessUid());
		//f1AppInstance.setAgentLatency((int) (now - this.f1AppInstance.getNowMs()));
	}

	public void setOrigId(long origId) {
		if (agentState != null)
			throw new IllegalStateException("orig miid can not change while member of an agent!");
		this.origId = origId;
	}
	public String getPuid() {
		return processUid;
	}

	public void addEntity(long origId, F1AppEntity entity) {
		entitiesByOrigId.putOrThrow(origId, entity);
		origIdById.putOrThrow(entity.getId(), origId);
		entity.setF1AppInstanceId(id);
	}

	public F1AppInstance getF1AppInstance() {
		return f1AppInstance;
	}

	public long getOrigId() {
		return origId;
	}

	public F1AppEntity getByOrigId(long id) {
		F1AppEntity r = entitiesByOrigId.get(id);
		if (r == null) {
			if (id == origId)
				return f1AppInstance;
			else
				throw new RuntimeException("orig id [" + id + "] not found for f1 app: " + this);
		}
		return r;
	}
	public Long getOrigIdById(long id) {
		Long r = origIdById.get(id);
		if (r == null) {
			if (id == this.id)
				return origId;
			else
				return null;
		}
		return r;
	}
	public F1AppEntity removeByOrigId(long id) {
		F1AppEntity r = entitiesByOrigId.removeOrThrow(id);
		origIdById.removeOrThrow(r.getId());
		return r;
	}

	public String toString() {
		return AppMonitorUtils.describe(f1AppInstance);
	}

	public LongKeyMap<F1AppEntity> getEntitiesByOrigId() {
		return this.entitiesByOrigId;
	}
	public LongKeyMap<F1AppEntity> getEntities() {
		return this.entitiesByOrigId;
	}
	public void setAgentState(VortexEyeAgentState agentState) {
		if (this.agentState == agentState)
			return;
		this.agentState = agentState;
		eyeState.onF1AppStateAgentChanged(this);
	}

	public VortexEyeAgentState getAgentState() {
		return agentState;
	}

	public VortexEyeState getEyeState() {
		return eyeState;
	}

	public long getId() {
		return id;
	}

}
