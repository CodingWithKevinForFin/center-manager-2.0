package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexExpectation;

@VID("F1.VE.MEQ")
public interface VortexEyeManageExpectationRequest extends VortexEyeRequest {

	@PID(1)
	public VortexExpectation getExpectation();
	public void setExpectation(VortexExpectation rule);

}
