package com.vortex.eye.evaluators;

import java.util.HashMap;
import java.util.Map;

import com.f1.povo.db.DbRequestMessage;
import com.f1.vortexcommon.msg.agent.VortexAgentFileSystem;
import com.f1.vortexcommon.msg.agent.VortexAgentSnapshot;
import com.vortex.agent.VortexAgentUtils;
import com.vortex.eye.VortexEyeUtils;

public class VortexFileSystemEvaluator extends VortexAbstractRevisionEvaluator<VortexAgentFileSystem> {

	@Override
	public byte getPidType(byte pid) {
		switch (pid) {
			case VortexAgentFileSystem.PID_NAME:
			case VortexAgentFileSystem.PID_TYPE:
				return FUNDAMENTAL_PID;
			case VortexAgentFileSystem.PID_TOTAL_SPACE:
			case VortexAgentFileSystem.PID_FREE_SPACE:
			case VortexAgentFileSystem.PID_USABLE_SPACE:
				return STATS_PID;
			default:
				return IGNORE_PID;
		}
	}

	@Override
	public DbRequestMessage insertToDatabase(VortexAgentFileSystem fileSystem) {
		VortexEyeUtils.assertValid(fileSystem);
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("id", fileSystem.getId());
		params.put("active", VortexEyeUtils.isActive(fileSystem));
		params.put("machine_instance_id", fileSystem.getMachineInstanceId());
		params.put("now", fileSystem.getNow());
		params.put("revision", fileSystem.getRevision());
		params.put("name", fileSystem.getName());
		params.put("fs_type", fileSystem.getType());
		return execute("insert_file_system_instance", params);
	}

	@Override
	public DbRequestMessage insertStatsToDatabase(VortexAgentFileSystem fileSystem) {
		final Map<Object, Object> params = new HashMap<Object, Object>();
		VortexEyeUtils.assertValid(fileSystem);
		params.put("id", fileSystem.getId());
		params.put("revision", fileSystem.getRevision());
		params.put("now", fileSystem.getNow());
		params.put("total_space", fileSystem.getTotalSpace());
		params.put("usable_space", fileSystem.getUsableSpace());
		params.put("free_space", fileSystem.getFreeSpace());
		return execute("insert_file_system_stats", params);
	}

	@Override
	public Map<String, VortexAgentFileSystem> getFromSnapshot(VortexAgentSnapshot snapshot) {
		return snapshot.getFileSystems();
	}

	@Override
	public Class<VortexAgentFileSystem> getAgentType() {
		return VortexAgentFileSystem.class;
	}

	@Override
	String getKey(VortexAgentFileSystem value) {
		return VortexAgentUtils.getKey(value);
	}

}
