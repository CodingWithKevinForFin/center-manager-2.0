package com.vortex.eye.evaluators;

import java.util.HashMap;
import java.util.Map;

import com.f1.povo.db.DbRequestMessage;
import com.f1.utils.CH;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.f1.vortexcommon.msg.agent.VortexAgentSnapshot;
import com.vortex.eye.VortexEyeUtils;

public class VortexMachineEvaluator extends VortexAbstractRevisionEvaluator<VortexAgentMachine> {

	@Override
	public byte getPidType(byte pid) {
		switch (pid) {
			case VortexAgentMachine.PID_SYSTEM_START_TIME:
			case VortexAgentMachine.PID_HOST_NAME:
			case VortexAgentMachine.PID_MACHINE_UID:
			case VortexAgentMachine.PID_OS_VERSION:
			case VortexAgentMachine.PID_OS_ARCHITECTURE:
			case VortexAgentMachine.PID_OS_NAME:
			case VortexAgentMachine.PID_CPU_COUNT:
				return FUNDAMENTAL_PID;
			case VortexAgentMachine.PID_SYSTEM_LOAD_AVERAGE:
			case VortexAgentMachine.PID_TOTAL_MEMORY:
			case VortexAgentMachine.PID_USED_MEMORY:
			case VortexAgentMachine.PID_TOTAL_SWAP_MEMORY:
			case VortexAgentMachine.PID_USED_SWAP_MEMORY:
				return STATS_PID;
			default:
				return IGNORE_PID;
		}
	}

	@Override
	public DbRequestMessage insertToDatabase(VortexAgentMachine agent) {
		agent.setMachineInstanceId(agent.getId());
		VortexEyeUtils.assertValid(agent);
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("active", true);
		params.put("id", agent.getId());
		params.put("revision", agent.getRevision());
		params.put("hostname", agent.getHostName());
		params.put("now", agent.getNow());
		params.put("machine_uid", agent.getMachineUid());
		params.put("machine_start_time", agent.getSystemStartTime());
		params.put("os_version", agent.getOsVersion());
		params.put("os_name", agent.getOsName());
		params.put("os_architecture", agent.getOsArchitecture());
		params.put("cpu_count", agent.getCpuCount());
		params.put("metadata", agent.getMetadata() == null ? null : VortexEyeUtils.joinMap(agent.getMetadata()));
		return execute("insert_machine_instance", params);
	}

	@Override
	public DbRequestMessage insertStatsToDatabase(VortexAgentMachine stats) {
		VortexEyeUtils.assertValid(stats);
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("active", true);
		params.put("machine_instance_id", stats.getMachineInstanceId());
		params.put("now", stats.getNow());
		params.put("total_memory", stats.getTotalMemory());
		params.put("used_memory", stats.getUsedMemory());
		params.put("total_swap_memory", stats.getTotalSwapMemory());
		params.put("used_swap_memory", stats.getUsedSwapMemory());
		params.put("system_load_avg", stats.getSystemLoadAverage());
		return execute("insert_machine_instance_stats", params);
	}

	@Override
	public Map<String, VortexAgentMachine> getFromSnapshot(VortexAgentSnapshot snapshot) {
		VortexAgentMachine m = snapshot.getMachine();
		if (m == null)
			return null;
		return CH.m(getKey(m), m);
	}

	@Override
	public Class<VortexAgentMachine> getAgentType() {
		return VortexAgentMachine.class;
	}

	@Override
	String getKey(VortexAgentMachine value) {
		return "MACHINE";
	}

	@Override
	public void removeFromSnapshot(VortexAgentSnapshot sink, String key, VortexAgentMachine value) {
		sink.setMachine(null);
	}
	@Override
	public void addToSnapshot(VortexAgentSnapshot sink, String key, VortexAgentMachine value) {
		sink.setMachine(value);
	}

}
