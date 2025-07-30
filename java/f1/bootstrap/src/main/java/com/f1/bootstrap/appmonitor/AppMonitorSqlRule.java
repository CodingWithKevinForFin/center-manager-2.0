package com.f1.bootstrap.appmonitor;

import com.f1.povo.f1app.audit.F1AppAuditTrailRule;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.impl.ConstTextMatcher;

public class AppMonitorSqlRule implements AppMonitorAuditRule<AppMonitorDatabaseListener> {

	public final F1AppAuditTrailRule rule;

	public final TextMatcher urlMask;
	public final TextMatcher statementMask;

	public AppMonitorSqlRule(F1AppAuditTrailRule rule) {
		this.rule = rule;
		final String st = this.rule.getRules().get(F1AppAuditTrailRule.RULE_SQL_STATEMENT_MASK);
		statementMask = st == null ? ConstTextMatcher.TRUE : SH.m(st);

		final String url = this.rule.getRules().get(F1AppAuditTrailRule.RULE_SQL_DATABASE_URL_MASK);
		urlMask = url == null ? ConstTextMatcher.TRUE : SH.m(url);
	}

	@Override
	public boolean isAuditable(AppMonitorDatabaseListener listener) {
		return urlMask.matches(listener.getObject().getUrl());
	}

	public TextMatcher getStatementMask() {
		return statementMask;
	}

	@Override
	public Class<AppMonitorDatabaseListener> getListenerType() {
		return AppMonitorDatabaseListener.class;
	}

	@Override
	public long getId() {
		return rule.getId();
	}

}
