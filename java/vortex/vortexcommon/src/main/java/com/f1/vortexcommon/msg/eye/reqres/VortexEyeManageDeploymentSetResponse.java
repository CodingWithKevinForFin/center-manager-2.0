package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexDeploymentSet;

@VID("F1.VE.MDSR")
public interface VortexEyeManageDeploymentSetResponse extends VortexEyeResponse {

	@PID(10)
	public VortexDeploymentSet getDeploymentSet();
	public void setDeploymentSet(VortexDeploymentSet deployment);

}
