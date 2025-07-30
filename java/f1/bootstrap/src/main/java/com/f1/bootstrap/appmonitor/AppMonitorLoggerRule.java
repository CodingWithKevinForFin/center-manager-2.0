package com.f1.bootstrap.appmonitor;

import com.f1.povo.f1app.audit.F1AppAuditTrailRule;
import com.f1.speedlogger.SpeedLoggerLevels;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.impl.ConstTextMatcher;

public class AppMonitorLoggerRule implements AppMonitorAuditRule<AppMonitorLoggerListener> {

	public final F1AppAuditTrailRule rule;

	public final int level;
	public final TextMatcher idMask;

	public AppMonitorLoggerRule(F1AppAuditTrailRule rule) {
		this.rule = rule;
		final String lev = this.rule.getRules().get(F1AppAuditTrailRule.RULE_LOGGER_LOG_LEVEL);
		level = lev == null ? SpeedLoggerLevels.ALL : Integer.parseInt(lev);

		final String id = this.rule.getRules().get(F1AppAuditTrailRule.RULE_LOGGER_LOGGER_ID);
		idMask = id == null ? ConstTextMatcher.TRUE : SH.m(id);
	}

	@Override
	public boolean isAuditable(AppMonitorLoggerListener listener) {
		return idMask.matches(listener.getObject().getId());
	}

	@Override
	public Class<AppMonitorLoggerListener> getListenerType() {
		return AppMonitorLoggerListener.class;
	}

	@Override
	public long getId() {
		return rule.getId();
	}

}
