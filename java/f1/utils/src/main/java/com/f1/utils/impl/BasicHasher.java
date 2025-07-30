package com.f1.utils.impl;

import com.f1.utils.Hasher;
import com.f1.utils.OH;

public class BasicHasher implements Hasher {

	public static final BasicHasher INSTANCE = new BasicHasher();

	protected BasicHasher() {
	}

	@Override
	public int hashcode(Object o) {
		return OH.hashCode(o);
	}

	@Override
	public boolean areEqual(Object l, Object r) {
		return OH.eq(l, r);
	}

}
