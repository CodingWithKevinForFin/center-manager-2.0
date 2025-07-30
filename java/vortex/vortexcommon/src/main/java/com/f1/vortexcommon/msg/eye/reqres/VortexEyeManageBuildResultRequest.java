package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexBuildResult;

@VID("F1.VE.MBRQ")
public interface VortexEyeManageBuildResultRequest extends VortexEyeRequest {

	@PID(10)
	public VortexBuildResult getBuildResult();
	public void setBuildResult(VortexBuildResult buildResult);

}
