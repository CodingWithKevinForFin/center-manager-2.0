package com.vortex.eye.evaluators;

import java.util.Map;

import com.f1.base.ValuedParam;
import com.f1.container.ContainerTools;
import com.f1.povo.db.DbRequestMessage;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentSnapshot;
import com.vortex.agent.VortexAgentUtils;
import com.vortex.eye.VortexEyeUtils;

public abstract class VortexAbstractRevisionEvaluator<T extends VortexAgentEntity> {
	final static public byte DIFF_FUNDAMENTALS_CHANGED = 1;
	final static public byte DIFF_ONLY_STATS_CHANGED = 2;
	final static public byte DIFF_NO_CHANGE = 3;

	final static public byte FUNDAMENTAL_PID = 1;
	final static public byte STATS_PID = 2;
	final static public byte IGNORE_PID = 3;
	private ContainerTools tools;
	private byte agentTypeId;

	public VortexAbstractRevisionEvaluator() {
		this.agentTypeId = VortexAgentUtils.getEntityType(getAgentType());
	}

	public void setTools(ContainerTools tools) {
		this.tools = tools;
	}

	public ContainerTools getTools() {
		return tools;
	}

	public byte diff(T update, T current) {
		boolean statsChanged = false;
		for (ValuedParam<T> param : update.askExistingValuedParams()) {
			switch (param.getPid()) {
				case VortexAgentEntity.PID_ID:
				case VortexAgentEntity.PID_MACHINE_INSTANCE_ID:
				case VortexAgentEntity.PID_NOW:
				case VortexAgentEntity.PID_REVISION:
					continue;
				default:
					byte pidType = getPidType(param.getPid());
					if (pidType == IGNORE_PID || param.areEqual(update, current))
						continue;
					else if (pidType == FUNDAMENTAL_PID) {
						return DIFF_FUNDAMENTALS_CHANGED;
					} else
						statsChanged = true;
			}
		}
		return statsChanged ? DIFF_ONLY_STATS_CHANGED : DIFF_NO_CHANGE;
	}
	public String getIdFountainName() {
		return VortexEyeUtils.FOUNTAIN_ID;
	}

	public void removeFromSnapshot(VortexAgentSnapshot existing, String key, T value) {
		getFromSnapshot(existing).remove(key);
	}
	public void addToSnapshot(VortexAgentSnapshot existing, String key, T value) {
		getFromSnapshot(existing).put(key, value);
	}
	protected DbRequestMessage execute(String sql, Map<Object, Object> params) {
		DbRequestMessage r = getTools().nw(DbRequestMessage.class);
		r.setParams(params);
		r.setId(sql);
		return r;
	}

	abstract public byte getPidType(byte pid);
	abstract public DbRequestMessage insertToDatabase(T value);
	abstract public DbRequestMessage insertStatsToDatabase(T value);
	abstract public Map<String, T> getFromSnapshot(VortexAgentSnapshot snapshot);
	abstract public Class<T> getAgentType();
	abstract String getKey(T value);

	public byte getAgentTypeId() {
		return agentTypeId;
	}
}
