package com.f1.bootstrap.appmonitor;

import com.f1.base.Action;
import com.f1.base.Message;
import com.f1.container.Processor;
import com.f1.container.State;
import com.f1.povo.f1app.audit.F1AppAuditTrailRule;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.impl.ConstTextMatcher;

public class AppMonitorF1EventRule implements AppMonitorAuditRule<AppMonitorProcessorListener> {

	public final F1AppAuditTrailRule rule;
	final private TextMatcher processorMask;
	final private Class<? extends Processor> processorClass;
	final private Class<? extends State> stateClass;
	final private Class<? extends Message> messageClass;

	public AppMonitorF1EventRule(F1AppAuditTrailRule rule) {
		this.rule = rule;
		processorMask = toMask(F1AppAuditTrailRule.RULE_F1_PROCESSOR_MASK);
		processorClass = toClass(F1AppAuditTrailRule.RULE_F1_PROCESSOR_CLASSNAME, Processor.class);
		stateClass = toClass(F1AppAuditTrailRule.RULE_F1_STATE_CLASSNAME, State.class);
		messageClass = toClass(F1AppAuditTrailRule.RULE_F1_MESSAGE_CLASSNAME, Message.class);
	}

	private <T> Class<? extends T> toClass(short field, Class<T> requiredType) {
		final String classname = this.rule.getRules().get(field);
		if (SH.is(classname)) {
			try {
				return Class.forName(classname).asSubclass(requiredType);
			} catch (ClassNotFoundException e) {
			} catch (ClassCastException e) {
			}
		}
		return null;
	}

	private TextMatcher toMask(short field) {
		final String text = this.rule.getRules().get(field);
		return text == null ? ConstTextMatcher.TRUE : SH.m(text);
	}

	@Override
	public boolean isAuditable(AppMonitorProcessorListener listener) {
		final Processor<?, ?> processor = listener.getObject();
		if (!isEitherAssignable(processorClass, processor.getClass()) || !isEitherAssignable(stateClass, processor.getStateType())
				|| !isEitherAssignable(messageClass, processor.getActionType()))
			return false;
		return processorMask.matches(processor.getFullName());
	}

	private boolean isEitherAssignable(Class<?> clazz, Class<?> clazz2) {
		return clazz == null || clazz2 == null || clazz.isAssignableFrom(clazz2) || clazz2.isAssignableFrom(clazz);
	}
	private boolean isAssignable(Class<?> clazz, Class<?> clazz2) {
		return clazz == null || clazz2 == null && clazz.isAssignableFrom(clazz2.getClass());
	}

	@Override
	public Class<AppMonitorProcessorListener> getListenerType() {
		return AppMonitorProcessorListener.class;
	}

	@Override
	public long getId() {
		return rule.getId();
	}

	public boolean isAuditable(Action action, State state) {
		if (stateClass != null && state != null && !stateClass.isAssignableFrom(state.getClass()))
			return false;
		if (messageClass != null && action != null && !messageClass.isAssignableFrom(action.getClass()))
			return false;
		return true;
	}

}
