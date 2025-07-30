package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexEyeAuditTrailRule;

@VID("F1.VE.MATRR")
public interface VortexEyeManageAuditTrailRuleResponse extends VortexEyeResponse {

	byte PID_RULE = 1;

	@PID(PID_RULE)
	public VortexEyeAuditTrailRule getRule();
	public void setRule(VortexEyeAuditTrailRule rule);
}
