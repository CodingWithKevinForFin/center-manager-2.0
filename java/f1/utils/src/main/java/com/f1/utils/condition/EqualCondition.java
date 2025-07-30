package com.f1.utils.condition;

import com.f1.utils.SH;

public class EqualCondition<K> implements Condition<K> {

	final private Condition<K> inner[];

	public EqualCondition(Condition<K>[] inner) {
		if (inner.length == 0)
			throw new IndexOutOfBoundsException("must be at least one condition");
		this.inner = inner;
	}

	@Override
	public Boolean get(K key) {
		Boolean t = inner[0].get(key);
		for (int i = 1; i < inner.length; i++)
			if (t.equals(inner[i].get(key)))
				return Boolean.FALSE;
		return Boolean.TRUE;
	}

	@Override
	public String toString() {
		return "(" + SH.join(" == ", (Object[]) inner) + ")";
	}
}
