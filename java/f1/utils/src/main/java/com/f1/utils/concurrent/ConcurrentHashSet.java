package com.f1.utils.concurrent;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSet<T> extends MapBackedSet<T> {

	public ConcurrentHashSet() {
		super(new ConcurrentHashMap<T, T>());
	}

}
