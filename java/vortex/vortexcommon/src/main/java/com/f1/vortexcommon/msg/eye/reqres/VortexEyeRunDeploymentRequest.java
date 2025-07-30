package com.f1.vortexcommon.msg.eye.reqres;

import java.util.Map;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.RDQ")
public interface VortexEyeRunDeploymentRequest extends VortexEyeRequest {

	byte TYPE_DEPLOY = 1;
	byte TYPE_VERIFY = 2;
	byte TYPE_DEPLOY_CONFIG = 3;

	@PID(10)
	void setDeploymentId(long deploymentId);
	long getDeploymentId();

	@PID(11)
	void setDeploymentVariables(Map<String, String> emptyMap);
	Map<String, String> getDeploymentVariables();

	@PID(13)
	void setBuildResultId(long parseLong);
	public long getBuildResultId();

	@PID(14)
	public byte getType();
	void setType(byte type);

}
