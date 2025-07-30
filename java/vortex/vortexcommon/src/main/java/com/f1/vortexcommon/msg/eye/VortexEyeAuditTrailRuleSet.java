package com.f1.vortexcommon.msg.eye;

import java.util.List;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.ATRS")
public interface VortexEyeAuditTrailRuleSet extends Message {

	byte PID_RULES = 1;

	@PID(PID_RULES)
	public List<VortexEyeAuditTrailRule> getRules();
	public void setRules(List<VortexEyeAuditTrailRule> rules);

	@PID(2)
	public boolean getIsSnapshot();
	public void setIsSnapshot(boolean isSnapshot);
}
