package com.vortex.client;

import java.util.HashMap;
import java.util.Map;

import com.f1.povo.f1app.audit.F1AppAuditTrailRule;
import com.f1.speedlogger.impl.SpeedLoggerUtils;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeAuditTrailRule;

public class VortexClientAuditTrailRule extends VortexClientEntity<VortexEyeAuditTrailRule> {

	private Map<Short, String> rulesMap;
	private String rules;

	public VortexClientAuditTrailRule(VortexEyeAuditTrailRule data) {
		super(VortexAgentEntity.TYPE_AUDIT_EVENT_RULE, data);
		update(data);
	}

	public String getRules() {
		return rules;
	}
	public Map<Short, String> getRulesMap() {
		return rulesMap;
	}

	@Override
	public void update(VortexEyeAuditTrailRule data) {
		super.update(data);
		StringBuilder sb = new StringBuilder();
		rulesMap = new HashMap<Short, String>();
		for (Map.Entry<Short, String> e : data.getRules().entrySet()) {
			final String key, value;
			switch (e.getKey().shortValue()) {
				case F1AppAuditTrailRule.RULE_LOGGER_LOG_LEVEL:
					key = "Log Event";
					value = SpeedLoggerUtils.getFullLevelAsString(Integer.parseInt(e.getValue()));
					break;
				case F1AppAuditTrailRule.RULE_LOGGER_LOGGER_ID:
					key = "Logger Id";
					value = e.getValue();
					break;
				case F1AppAuditTrailRule.RULE_PROCESS_APPNAME_MASK:
					key = "Process App";
					value = e.getValue();
					break;
				case F1AppAuditTrailRule.RULE_PROCESS_HOSTMACHINE_MASK:
					key = "Process Host";
					value = e.getValue();
					break;
				case F1AppAuditTrailRule.RULE_PROCESS_USER_MASK:
					key = "Process User";
					value = e.getValue();
					break;
				default:
					key = SH.toString(e.getKey());
					value = e.getValue();
					break;
			}
			rulesMap.put(e.getKey(), value);
			if (sb.length() > 0)
				sb.append(", ");
			sb.append(key).append(':').append(value);
		}
		rules = sb.toString();
	}

}
