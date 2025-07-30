package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexDeployment;

@VID("F1.VE.MDR")
public interface VortexEyeManageDeploymentResponse extends VortexEyeResponse {

	@PID(10)
	public VortexDeployment getDeployment();
	public void setDeployment(VortexDeployment deployment);

	@PID(13)
	public VortexDeployment getDeploymentStatus();
	public void setDeploymentStatus(VortexDeployment deployment);

}
