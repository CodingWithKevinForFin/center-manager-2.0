package com.vortex.client;

import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentNetAddress;

public class VortexClientNetAddress extends VortexClientMachineEntity<VortexAgentNetAddress> {

	final private boolean isLoopback;

	public VortexClientNetAddress(VortexAgentNetAddress data) {
		super(VortexAgentEntity.TYPE_NET_ADDRESS, data);
		this.isLoopback = VortexClientManager.isLoopback(data.getAddress());
	}

	public boolean isLoopback() {
		return isLoopback;
	}

}
