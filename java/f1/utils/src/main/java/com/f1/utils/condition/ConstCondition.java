package com.f1.utils.condition;

import com.f1.utils.OH;

public class ConstCondition<K> implements Condition<K> {
	public static final Condition TRUE = new ConstCondition(true);
	public static final Condition FALSE = new ConstCondition(false);
	final public Boolean r;

	public ConstCondition(Boolean r) {
		OH.assertNotNull(r);
		this.r = r;
	}

	@Override
	public Boolean get(K key_) {
		return r;
	}

	@Override
	public String toString() {
		return r.toString();
	}

}
