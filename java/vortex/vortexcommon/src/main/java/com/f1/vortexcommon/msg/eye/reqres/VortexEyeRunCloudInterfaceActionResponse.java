package com.f1.vortexcommon.msg.eye.reqres;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.RCIAR")
public interface VortexEyeRunCloudInterfaceActionResponse extends VortexEyeResponse {

	@PID(10)
	public List<String> getValues();
	public void setValues(List<String> values);

}
