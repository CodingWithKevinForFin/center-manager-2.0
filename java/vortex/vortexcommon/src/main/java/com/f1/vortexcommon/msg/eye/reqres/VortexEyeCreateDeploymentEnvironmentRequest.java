package com.f1.vortexcommon.msg.eye.reqres;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.VortexDeploymentSet;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;

@VID("F1.VE.CDEQ")
public interface VortexEyeCreateDeploymentEnvironmentRequest extends VortexEyeRequest {

	@PID(10)
	public List<VortexDeploymentSet> getDeploymentSets();
	public void setDeploymentSets(List<VortexDeploymentSet> deployment);

	@PID(11)
	public void setDeployments(List<VortexDeployment> deployments);
	public List<VortexDeployment> getDeployments();

	@PID(12)
	public void setBackups(List<VortexEyeBackup> deployments);
	public List<VortexEyeBackup> getBackups();

}
