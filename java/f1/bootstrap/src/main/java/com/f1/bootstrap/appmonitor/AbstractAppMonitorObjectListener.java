package com.f1.bootstrap.appmonitor;

import java.lang.ref.WeakReference;

import com.f1.povo.f1app.F1AppEntity;
import com.f1.povo.f1app.audit.F1AppAuditTrailEvent;
import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.concurrent.FastQueue;

public abstract class AbstractAppMonitorObjectListener<T extends F1AppEntity, O> implements AppMonitorObjectListener<T, O> {

	final private WeakReference<O> ref;
	final private AppMonitorState state;
	volatile private boolean hasChanged;
	private T agentObject;
	private final Class<?> objectClazz;

	public AbstractAppMonitorObjectListener(AppMonitorState state, O object) {
		this.state = state;
		ref = object == null ? null : new WeakReference<O>(object);
		objectClazz = OH.getClass(object);
	}

	@Override
	final public AppMonitorState getState() {
		return state;
	}

	@Override
	final public T getAgentObject() {
		return agentObject;
	}

	@Override
	final public void setAgentObject(T o) {
		O object = getObject();
		try {
			agentObject = o;
			if (object != null) {
				populate(object, agentObject);
			}
			agentObject.setAuditRulesCount(getAuditRulesCount());
		} catch (Exception e) {
			throw new RuntimeException("Error for object: " + object, e);
		}
	}

	//only called by the single monitor thread.
	abstract protected void populate(O source, T sink);

	@Override
	final public boolean resetHasChanged() {
		boolean r = hasChanged;
		hasChanged = false;
		return r;
	}

	@Override
	public O getObject() {
		return ref == null ? null : ref.get();
	}

	final protected void flagChanged() {
		hasChanged = true;
	}

	@Override
	public boolean updateAgentObject() {
		O object = getObject();
		if (object == null)
			return false;
		populate(object, agentObject);
		agentObject.setAuditRulesCount(getAuditRulesCount());
		return true;
	}

	private byte getAuditRulesCount() {
		AppMonitorAuditRule[] t = auditRules;
		if (t == null)
			return 0;
		return (byte) t.length;
	}

	//null if no auditRules
	private volatile AppMonitorAuditRule[] auditRules;

	//only called by the single monitor thread.
	@Override
	public void addAuditRule(AppMonitorAuditRule<?> rule) {
		if (auditRules == null)
			auditRules = new AppMonitorAuditRule[] { rule };
		else {
			long ruleId = rule.getId();
			for (int i = 0; i < auditRules.length; i++) {
				if (auditRules[i].getId() == ruleId)
					auditRules[i] = rule;
			}
			auditRules = AH.insert(auditRules, 0, rule);
		}
		hasChanged = true;
	}

	public boolean getIsAudited() {
		return auditRules != null;
	}

	//only called by the single monitor thread.
	public boolean removeAuditRule(AppMonitorAuditRule<?> rule) {
		if (auditRules != null) {
			long ruleId = rule.getId();
			for (int i = 0; i < auditRules.length; i++) {
				if (auditRules[i].getId() == ruleId) {
					auditRules = auditRules.length == 1 ? null : AH.remove(auditRules, i);
					hasChanged = true;
					return true;
				}
			}
		}
		return false;
	}

	private FastQueue<F1AppAuditTrailEvent> queue = new FastQueue<F1AppAuditTrailEvent>();

	protected void onAuditEvent(F1AppAuditTrailEvent ae) {
		queue.put(ae);
		hasChanged = true;
	}

	@Override
	public F1AppAuditTrailEvent popAuditTrailEvent() {
		return queue.get();
	}

	//returns null if no rules, never empty array
	public AppMonitorAuditRule[] getAuditRuleIdsOrNull() {
		return auditRules;
	}

	@Override
	public Class<?> getObjectClass() {
		return objectClazz;
	}

}
