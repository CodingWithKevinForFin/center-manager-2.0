package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexDeployment;

@VID("F1.VE.RBPR")
public interface VortexEyeRunBuildProcedureResponse extends VortexEyeResponse {

	@PID(13)
	public VortexDeployment getResult();
	public void setResult(VortexDeployment result);

}
