package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexDeployment;

@VID("F1.VE.MDQ")
public interface VortexEyeManageDeploymentRequest extends VortexEyeRequest {

	@PID(10)
	public VortexDeployment getDeployment();
	public void setDeployment(VortexDeployment deployment);

}
