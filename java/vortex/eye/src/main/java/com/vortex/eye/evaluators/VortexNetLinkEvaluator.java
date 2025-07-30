package com.vortex.eye.evaluators;

import java.util.HashMap;
import java.util.Map;

import com.f1.povo.db.DbRequestMessage;
import com.f1.vortexcommon.msg.agent.VortexAgentNetLink;
import com.f1.vortexcommon.msg.agent.VortexAgentSnapshot;
import com.vortex.agent.VortexAgentUtils;
import com.vortex.eye.VortexEyeUtils;

public class VortexNetLinkEvaluator extends VortexAbstractRevisionEvaluator<VortexAgentNetLink> {

	@Override
	public DbRequestMessage insertToDatabase(VortexAgentNetLink netLink) {
		VortexEyeUtils.assertValid(netLink);
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("id", netLink.getId());
		params.put("active", VortexEyeUtils.isActive(netLink));
		params.put("machine_instance_id", netLink.getMachineInstanceId());
		params.put("now", netLink.getNow());
		params.put("revision", netLink.getRevision());

		params.put("mac", netLink.getMac());
		params.put("broadcast", netLink.getBroadcast());
		params.put("name", netLink.getName());
		params.put("transmission_details", netLink.getTransmissionDetails());
		params.put("mtu", netLink.getMtu());
		params.put("state", netLink.getState());
		return execute("insert_net_link_instance", params);

	}

	@Override
	public DbRequestMessage insertStatsToDatabase(VortexAgentNetLink netLink) {
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("id", netLink.getId());
		params.put("now", netLink.getNow());
		params.put("rx_packets", netLink.getRxPackets());
		params.put("rx_errors", netLink.getRxErrors());
		params.put("rx_dropped", netLink.getRxDropped());
		params.put("rx_overrun", netLink.getRxOverrun());
		params.put("rx_multicast", netLink.getRxMulticast());
		params.put("tx_packets", netLink.getTxPackets());
		params.put("tx_errors", netLink.getTxErrors());
		params.put("tx_dropped", netLink.getTxDropped());
		params.put("tx_carrier", netLink.getTxCarrier());
		params.put("tx_collsns", netLink.getTxCollsns());
		return execute("insert_net_link_stats", params);
	}

	@Override
	public Map<String, VortexAgentNetLink> getFromSnapshot(VortexAgentSnapshot snapshot) {
		return snapshot.getNetLinks();
	}

	@Override
	public Class<VortexAgentNetLink> getAgentType() {
		return VortexAgentNetLink.class;
	}

	@Override
	public byte getPidType(byte pid) {
		switch (pid) {
			case VortexAgentNetLink.PID_BROADCAST:
			case VortexAgentNetLink.PID_MTU:
			case VortexAgentNetLink.PID_TRANSMISSION_DETAILS:
			case VortexAgentNetLink.PID_MAC:
			case VortexAgentNetLink.PID_STATE:
				return FUNDAMENTAL_PID;
			default:
				return STATS_PID;
		}
	}
	@Override
	String getKey(VortexAgentNetLink value) {
		return VortexAgentUtils.getKey(value);
	}

}
