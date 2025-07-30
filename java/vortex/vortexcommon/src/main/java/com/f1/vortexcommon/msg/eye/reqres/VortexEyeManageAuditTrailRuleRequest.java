package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexEyeAuditTrailRule;

@VID("F1.VE.MATRQ")
public interface VortexEyeManageAuditTrailRuleRequest extends VortexEyeRequest {

	byte PID_RULE = 1;

	@PID(PID_RULE)
	public VortexEyeAuditTrailRule getRule();
	public void setRule(VortexEyeAuditTrailRule rule);

}
