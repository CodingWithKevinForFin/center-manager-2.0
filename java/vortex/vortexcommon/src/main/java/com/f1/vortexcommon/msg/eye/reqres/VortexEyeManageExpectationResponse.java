package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexExpectation;

@VID("F1.VE.MER")
public interface VortexEyeManageExpectationResponse extends VortexEyeResponse {

	byte PID_EXPECTATION = 1;

	@PID(PID_EXPECTATION)
	public VortexExpectation getExpectation();
	public void setExpectation(VortexExpectation expectation);

}
