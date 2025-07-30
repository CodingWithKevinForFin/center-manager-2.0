package com.f1.utils.condition;

import com.f1.base.Getter;

public interface Condition<K> extends Getter<K, Boolean> {
	/**
	 * returns true if the condition is satisfied
	 */
	@Override
	Boolean get(K key);
}
