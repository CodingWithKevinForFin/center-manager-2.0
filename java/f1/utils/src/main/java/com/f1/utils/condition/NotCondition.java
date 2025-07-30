package com.f1.utils.condition;

public class NotCondition<K> implements Condition<K> {

	final private Condition<K> inner;

	public NotCondition(Condition<K> inner) {
		this.inner = inner;
	}

	@Override
	public Boolean get(K key) {
		return Boolean.FALSE.equals(inner.get(key));
	}

	@Override
	public String toString() {
		return "!(" + inner + ")";
	}
}
