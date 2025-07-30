package com.vortex.eye.evaluators;

import java.util.HashMap;
import java.util.Map;

import com.f1.povo.db.DbRequestMessage;
import com.f1.vortexcommon.msg.agent.VortexAgentNetAddress;
import com.f1.vortexcommon.msg.agent.VortexAgentSnapshot;
import com.vortex.agent.VortexAgentUtils;
import com.vortex.eye.VortexEyeUtils;

public class VortexNetAddressEvaluator extends VortexAbstractRevisionEvaluator<VortexAgentNetAddress> {

	@Override
	public DbRequestMessage insertToDatabase(VortexAgentNetAddress netAddress) {
		VortexEyeUtils.assertValid(netAddress);
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("id", netAddress.getId());
		params.put("active", VortexEyeUtils.isActive(netAddress));
		params.put("now", netAddress.getNow());
		params.put("revision", netAddress.getRevision());
		params.put("machine_instance_id", netAddress.getMachineInstanceId());
		params.put("broadcast", netAddress.getBroadcast());
		params.put("link_name", netAddress.getLinkName());
		params.put("address", netAddress.getAddress());
		params.put("broadcast", netAddress.getBroadcast());
		params.put("address_type", netAddress.getType());
		params.put("scope", netAddress.getScope());
		return execute("insert_net_address_instance", params);
	}

	@Override
	public DbRequestMessage insertStatsToDatabase(VortexAgentNetAddress value) {
		return null;
	}

	@Override
	public Map<String, VortexAgentNetAddress> getFromSnapshot(VortexAgentSnapshot snapshot) {
		return snapshot.getNetAddresses();
	}

	@Override
	public Class<VortexAgentNetAddress> getAgentType() {
		return VortexAgentNetAddress.class;
	}

	@Override
	public byte getPidType(byte pid) {
		return FUNDAMENTAL_PID;
	}
	@Override
	String getKey(VortexAgentNetAddress value) {
		return VortexAgentUtils.getKey(value);
	}
}
