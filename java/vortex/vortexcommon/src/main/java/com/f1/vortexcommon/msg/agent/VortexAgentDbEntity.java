package com.f1.vortexcommon.msg.agent;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexEyeEntity;

@VID("F1.VA.DE")
public interface VortexAgentDbEntity extends VortexEyeEntity {

	@PID(1)
	public long getDbServerId();
	public void setDbServerId(long dbServerId);

}
