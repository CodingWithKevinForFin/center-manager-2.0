package com.f1.vortexcommon.msg.eye.reqres;

import java.util.List;
import java.util.Map;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.GEIR")
public interface VortexEyeGetEyeInfoResponse extends VortexEyeResponse {

	@PID(1)
	public List<String> getAvailableAgentVersions();
	public void setAvailableAgentVersions(List<String> id);

	//name --> description
	@PID(2)
	public Map<String, String> getAvailableAgentInterfaces();
	public void setAvailableAgentInterfaces(Map<String, String> interfaces);

	//name --> description
	@PID(3)
	public String getAgentDefaultTargetDirectory();
	public void setAgentDefaultTargetDirectory(String interfaces);

}
