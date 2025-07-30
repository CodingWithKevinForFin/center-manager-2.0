package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexEyeScheduledTask;

@VID("F1.VE.MSTR")
public interface VortexEyeManageScheduledTaskResponse extends VortexEyeResponse {

	@PID(10)
	public VortexEyeScheduledTask getScheduledTask();
	public void setScheduledTask(VortexEyeScheduledTask scheduledTask);

}
