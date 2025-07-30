package com.vortex.eye.evaluators;

import java.util.HashMap;
import java.util.Map;

import com.f1.povo.db.DbRequestMessage;
import com.f1.vortexcommon.msg.agent.VortexAgentProcess;
import com.f1.vortexcommon.msg.agent.VortexAgentSnapshot;
import com.vortex.agent.VortexAgentUtils;
import com.vortex.eye.VortexEyeUtils;

public class VortexProcessEvaluator extends VortexAbstractRevisionEvaluator<VortexAgentProcess> {

	@Override
	public byte getPidType(byte pid) {
		switch (pid) {
			case VortexAgentProcess.PID_COMMAND:
			case VortexAgentProcess.PID_PARENT_PID:
			case VortexAgentProcess.PID_USER:
				return FUNDAMENTAL_PID;
			default:
				return STATS_PID;

		}
	}

	@Override
	public DbRequestMessage insertToDatabase(VortexAgentProcess process) {
		VortexEyeUtils.assertValid(process);
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("id", process.getId());
		params.put("active", VortexEyeUtils.isActive(process));
		params.put("machine_instance_id", process.getMachineInstanceId());
		params.put("now", process.getNow());
		params.put("revision", process.getRevision());

		params.put("user_name", process.getUser());
		params.put("command", process.getCommand());
		params.put("start_time", process.getStartTime());
		params.put("end_time", process.getEndTime());
		params.put("parent_pid", process.getParentPid());
		params.put("pid", process.getPid());
		return execute("insert_process_instance", params);
	}

	@Override
	public DbRequestMessage insertStatsToDatabase(VortexAgentProcess process) {
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("now", process.getNow());
		params.put("id", process.getId());
		params.put("memory", process.getMemory());
		params.put("cpu_percent", process.getCpuPercent());
		return execute("insert_process_stats", params);
	}

	@Override
	public Map<String, VortexAgentProcess> getFromSnapshot(VortexAgentSnapshot snapshot) {
		return snapshot.getProcesses();
	}

	@Override
	public Class<VortexAgentProcess> getAgentType() {
		return VortexAgentProcess.class;
	}

	@Override
	String getKey(VortexAgentProcess value) {
		return VortexAgentUtils.getKey(value);
	}

}
