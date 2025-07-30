package com.vortex.client;

import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeCloudInterface;

public class VortexClientCloudInterface extends VortexClientEntity<VortexEyeCloudInterface> {

	public VortexClientCloudInterface(VortexEyeCloudInterface data) {
		super(VortexAgentEntity.TYPE_CLOUD_INTERFACE, data);
		update(data);
	}

	public void update(VortexEyeCloudInterface data) {
		super.update(data);
	}

	public String getKey() {
		return VortexClientUtils.decryptToString(getData().getKeyContents());
	}

	public String getPassword() {
		return VortexClientUtils.decryptToString(getData().getPassword());
	}

}
