package com.f1.povo.f1app.audit;

import java.util.List;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.FATRS")
public interface F1AppAuditTrailRuleSet extends Message {

	byte PID_RULES = 1;

	@PID(PID_RULES)
	public List<F1AppAuditTrailRule> getRules();
	public void setRules(List<F1AppAuditTrailRule> rules);

	@PID(2)
	public boolean getIsSnapshot();
	public void setIsSnapshot(boolean isSnapshot);
}
