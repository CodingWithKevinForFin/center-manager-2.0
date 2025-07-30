package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.RSTQ")
public interface VortexEyeRunScheduledTaskRequest extends VortexEyeRequest {

	@PID(10)
	void setScheduledTaskId(long deploymentId);
	long getScheduledTaskId();

}
