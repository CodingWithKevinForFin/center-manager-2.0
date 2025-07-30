package com.f1.vortexcommon.msg.eye;

import java.util.Map;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.ATR")
public interface VortexEyeAuditTrailRule extends VortexEyeEntity {

	byte PID_RULE_TYPE = 1;
	byte PID_RULES = 2;
	byte PID_NAME = 3;

	@PID(PID_RULE_TYPE)
	public byte getRuleType();
	public void setRuleType(byte eventType);

	@PID(PID_RULES)
	public Map<Short, String> getRules();
	public void setRules(Map<Short, String> masks);

	@PID(PID_NAME)
	public String getName();
	public void setName(String name);

	public VortexEyeAuditTrailRule clone();

}
