package com.vortex.client;

import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentNetLink;

public class VortexClientNetLink extends VortexClientMachineEntity<VortexAgentNetLink> {

	public VortexClientNetLink(VortexAgentNetLink data) {
		super(VortexAgentEntity.TYPE_NET_LINK, data);
	}

}
