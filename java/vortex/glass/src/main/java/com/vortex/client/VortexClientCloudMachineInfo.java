package com.vortex.client;

import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortextEyeCloudMachineInfo;

public class VortexClientCloudMachineInfo extends VortexClientEntity<VortextEyeCloudMachineInfo> {

	public VortexClientCloudMachineInfo(VortextEyeCloudMachineInfo data) {
		super(VortexAgentEntity.TYPE_CLOUD_MACHINE_INFO, data);
		update(data);
	}

	public void update(VortextEyeCloudMachineInfo data) {
		super.update(data);
	}
}
