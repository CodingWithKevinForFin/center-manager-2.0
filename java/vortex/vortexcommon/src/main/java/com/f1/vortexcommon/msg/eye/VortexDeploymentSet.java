package com.f1.vortexcommon.msg.eye;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.VortexMetadatable;

@VID("F1.VO.DS")
public interface VortexDeploymentSet extends VortexEyeEntity, VortexMetadatable {

	@PID(10)
	public String getName();
	public void setName(String name);

	@PID(11)
	public String getProperties();
	public void setProperties(String properties);

	public VortexDeploymentSet clone();

}
