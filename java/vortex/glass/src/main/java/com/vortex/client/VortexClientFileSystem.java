package com.vortex.client;

import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentFileSystem;

public class VortexClientFileSystem extends VortexClientMachineEntity<VortexAgentFileSystem> {

	public VortexClientFileSystem(VortexAgentFileSystem data) {
		super(VortexAgentEntity.TYPE_FILE_SYSTEM, data);
	}

}
