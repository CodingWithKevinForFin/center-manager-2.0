package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexDeploymentSet;

@VID("F1.VE.MDSQ")
public interface VortexEyeManageDeploymentSetRequest extends VortexEyeRequest {

	@PID(10)
	public VortexDeploymentSet getDeploymentSet();
	public void setDeploymentSet(VortexDeploymentSet deployment);

	//@PID(11)
	//public void setDeployments(List<VortexDeployment> deployments);
	//public List<VortexDeployment> getDeployments();

	//@PID(12)
	//public void setBackups(List<VortexEyeBackup> deployments);
	//public List<VortexEyeBackup> getBackups();

}
