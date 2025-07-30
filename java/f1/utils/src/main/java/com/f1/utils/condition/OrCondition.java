package com.f1.utils.condition;

import com.f1.utils.SH;

public class OrCondition<K> implements Condition<K> {

	final private Condition<K> inner[];

	public OrCondition(Condition<K>[] inner) {
		this.inner = inner;
	}

	@Override
	public Boolean get(K key) {
		for (int i = 0; i < inner.length; i++)
			if (inner[i].get(key))
				return Boolean.TRUE;
		return Boolean.FALSE;
	}

	@Override
	public String toString() {
		return "(" + SH.join(" || ", (Object[]) inner) + ")";
	}
}
