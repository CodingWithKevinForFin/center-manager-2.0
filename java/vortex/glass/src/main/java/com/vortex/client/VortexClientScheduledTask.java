package com.vortex.client;

import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeScheduledTask;

public class VortexClientScheduledTask extends VortexClientEntity<VortexEyeScheduledTask> {

	public VortexClientScheduledTask(VortexEyeScheduledTask data) {
		super(VortexAgentEntity.TYPE_SCHEDULED_TASK, data);
		update(data);
	}

}
