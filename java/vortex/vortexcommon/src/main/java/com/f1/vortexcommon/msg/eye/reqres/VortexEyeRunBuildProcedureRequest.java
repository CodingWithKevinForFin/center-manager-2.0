package com.f1.vortexcommon.msg.eye.reqres;

import java.util.Map;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.RBPQ")
public interface VortexEyeRunBuildProcedureRequest extends VortexEyeRequest {

	@PID(10)
	void setBuildProcedureId(long buildProcedureId);
	long getBuildProcedureId();

	@PID(11)
	void setBuildProcedureVariables(Map<String, String> emptyMap);
	Map<String, String> getBuildProcedureVariables();

	@PID(12)
	void setInvokedBy(String invokedBy);
	String getInvokedBy();

	@PID(13)
	void setMetadata(Map<String, String> generateMetadata);
	public Map<String, String> getMetadata();

}
