package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexBuildProcedure;

@VID("F1.VE.MBPQ")
public interface VortexEyeManageBuildProcedureRequest extends VortexEyeRequest {

	@PID(10)
	public VortexBuildProcedure getBuildProcedure();
	public void setBuildProcedure(VortexBuildProcedure buildProcedure);

}
