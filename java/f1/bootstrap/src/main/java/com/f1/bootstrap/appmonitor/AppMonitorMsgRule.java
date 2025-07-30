package com.f1.bootstrap.appmonitor;

import com.f1.povo.f1app.audit.F1AppAuditTrailRule;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.impl.ConstTextMatcher;

public class AppMonitorMsgRule implements AppMonitorAuditRule<AppMonitorMsgTopicListener> {

	public final F1AppAuditTrailRule rule;
	final private TextMatcher fieldMask;
	final private TextMatcher topicMask;
	final private TextMatcher classMask;

	public AppMonitorMsgRule(F1AppAuditTrailRule rule) {
		this.rule = rule;
		fieldMask = toMask(F1AppAuditTrailRule.RULE_MSG_FIELDS_MASK);
		topicMask = toMask(F1AppAuditTrailRule.RULE_MSG_TOPIC_MASK);
		classMask = toMask(F1AppAuditTrailRule.RULE_MSG_CLASS_MASK);
	}

	private TextMatcher toMask(short field) {
		final String text = this.rule.getRules().get(field);
		return text == null ? ConstTextMatcher.TRUE : SH.m(text);
	}

	@Override
	public boolean isAuditable(AppMonitorMsgTopicListener listener) {
		String name = listener.getConnection().getClass().getName();
		String topicName = listener.getObject().getFullTopicName();
		boolean r = classMask.matches(name) && topicMask.matches(topicName);
		return r;
	}
	public TextMatcher getFieldMask() {
		return fieldMask;
	}

	@Override
	public Class<AppMonitorMsgTopicListener> getListenerType() {
		return AppMonitorMsgTopicListener.class;
	}

	@Override
	public long getId() {
		return rule.getId();
	}

}
