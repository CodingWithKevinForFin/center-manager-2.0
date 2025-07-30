package com.f1.utils.concurrent;

public class IdentityCompactSet<V> extends CompactSet<V> {

	@Override
	protected int hash(Object o) {
		return System.identityHashCode(o);
	}
	@Override
	protected boolean eq(Object a, Object b) {
		return a == b;
	}
}
