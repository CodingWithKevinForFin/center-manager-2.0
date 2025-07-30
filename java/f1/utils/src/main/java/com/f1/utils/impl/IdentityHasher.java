package com.f1.utils.impl;

import com.f1.utils.Hasher;

public class IdentityHasher implements Hasher {

	public static final Hasher INSTANCE = new IdentityHasher();

	protected IdentityHasher() {
	}

	@Override
	public int hashcode(Object o) {
		return o == null ? 0 : System.identityHashCode(o);
	}

	@Override
	public boolean areEqual(Object l, Object r) {
		return l == r;
	}

}
