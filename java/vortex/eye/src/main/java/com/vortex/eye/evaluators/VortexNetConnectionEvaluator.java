package com.vortex.eye.evaluators;

import java.util.HashMap;
import java.util.Map;

import com.f1.povo.db.DbRequestMessage;
import com.f1.vortexcommon.msg.agent.VortexAgentNetConnection;
import com.f1.vortexcommon.msg.agent.VortexAgentSnapshot;
import com.vortex.agent.VortexAgentUtils;
import com.vortex.eye.VortexEyeUtils;

public class VortexNetConnectionEvaluator extends VortexAbstractRevisionEvaluator<VortexAgentNetConnection> {

	@Override
	public DbRequestMessage insertToDatabase(VortexAgentNetConnection netConnection) {
		VortexEyeUtils.assertValid(netConnection);
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("id", netConnection.getId());
		params.put("active", VortexEyeUtils.isActive(netConnection));
		params.put("machine_instance_id", netConnection.getMachineInstanceId());
		params.put("now", netConnection.getNow());
		params.put("revision", netConnection.getRevision());

		params.put("foreign_host", netConnection.getForeignHost());
		params.put("foreign_port", netConnection.getForeignPort());
		params.put("local_host", netConnection.getLocalHost());
		params.put("local_port", netConnection.getLocalPort());
		params.put("local_pid", netConnection.getLocalPid());
		params.put("local_appname", netConnection.getLocalAppName());
		params.put("state", netConnection.getState());
		return execute("insert_net_connection_instance", params);
	}

	@Override
	public DbRequestMessage insertStatsToDatabase(VortexAgentNetConnection value) {
		return null;
	}

	@Override
	public Map<String, VortexAgentNetConnection> getFromSnapshot(VortexAgentSnapshot snapshot) {
		return snapshot.getConnections();
	}

	@Override
	public Class<VortexAgentNetConnection> getAgentType() {
		return VortexAgentNetConnection.class;
	}

	@Override
	public byte getPidType(byte pid) {
		return FUNDAMENTAL_PID;
	}
	@Override
	String getKey(VortexAgentNetConnection value) {
		return VortexAgentUtils.getKey(value);
	}
}
