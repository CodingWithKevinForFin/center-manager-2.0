package com.f1.utils.condition;

import com.f1.utils.SH;

public class AndCondition<K> implements Condition<K> {

	final private Condition<K> inner[];

	public AndCondition(Condition<K>[] inner) {
		this.inner = inner;
	}

	@Override
	public Boolean get(K key) {
		for (int i = 0; i < inner.length; i++)
			if (!inner[i].get(key))
				return Boolean.FALSE;
		return Boolean.TRUE;
	}

	@Override
	public String toString() {
		return "(" + SH.join(" && ", (Object[]) inner) + ")";
	}
}
