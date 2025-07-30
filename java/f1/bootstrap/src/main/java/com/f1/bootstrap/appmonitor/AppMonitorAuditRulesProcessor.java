package com.f1.bootstrap.appmonitor;

import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.povo.f1app.audit.F1AppAuditTrailRule;
import com.f1.povo.f1app.audit.F1AppAuditTrailRuleSet;

public class AppMonitorAuditRulesProcessor extends BasicProcessor<F1AppAuditTrailRuleSet, AppMonitorState> {

	public AppMonitorAuditRulesProcessor() {
		super(F1AppAuditTrailRuleSet.class, AppMonitorState.class);
	}

	@Override
	public void processAction(F1AppAuditTrailRuleSet action, AppMonitorState state, ThreadScope threadScope) throws Exception {
		for (F1AppAuditTrailRule rule : action.getRules()) {
			rule.lock();
			if (rule.getRevision() == 65535) {
				AppMonitorAuditRule<?> existing = state.removeAuditTrailRule(rule.getId());
				if (existing == null)
					continue;
				for (AppMonitorObjectListener<?, ?> listener : state.getAuditedListeners()) {
					listener.removeAuditRule(existing);
				}
				//TODO: remove
			} else {
				AppMonitorAuditRule<?> existing = state.getAuditTrailRule(rule.getId());
				AppMonitorAuditRule<?> appMonitorRule = toAppMonitorRule(rule);
				if (existing != null) {
					state.addAuditTrailRule(appMonitorRule);
					//TODO: update
				} else {
					state.addAuditTrailRule(appMonitorRule);
					getApplicants(appMonitorRule, state);
				}
			}
		}
	}

	public static AppMonitorAuditRule<?> toAppMonitorRule(F1AppAuditTrailRule rule) {
		switch (rule.getRuleType()) {
			case F1AppAuditTrailRule.EVENT_TYPE_LOG:
				return new AppMonitorLoggerRule(rule);
			case F1AppAuditTrailRule.EVENT_TYPE_SQL:
				return new AppMonitorSqlRule(rule);
			case F1AppAuditTrailRule.EVENT_TYPE_MSG:
				return new AppMonitorMsgRule(rule);
			case F1AppAuditTrailRule.EVENT_TYPE_F1:
				return new AppMonitorF1EventRule(rule);
			default:
				throw new RuntimeException("unknown rule type: " + rule.getRuleType());
		}

	}

	public static void getApplicants(AppMonitorAuditRule<?> appMonitorRule, AppMonitorState state) {
		for (AppMonitorObjectListener i : state.getListeners(appMonitorRule.getListenerType())) {
			if (((AppMonitorAuditRule) appMonitorRule).isAuditable(i))
				i.addAuditRule(appMonitorRule);
		}
	}

}
