package com.f1.utils.concurrent;

import java.util.Collection;
import java.util.IdentityHashMap;

public class IdentityHashSet<K> extends MapBackedSet<K> {

	public IdentityHashSet() {
		super(new IdentityHashMap<K, K>());
	}
	public IdentityHashSet(Collection<K> values) {
		super(new IdentityHashMap<K, K>(), values);
	}

}
