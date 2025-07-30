package com.f1.utils;

import com.f1.utils.concurrent.HasherSet;

public class InternPool<T> {

	private HasherSet<T> pool = new HasherSet<T>();

	public InternPool() {
	}

	public T intern(T value) {
		return pool.addIfAbsent(value);
	}
	public InternPool<T> internArray(T[] values) {
		for (int i = 0; i < values.length; i++)
			values[i] = intern(values[i]);
		return this;
	}

	public T getIfExists(T value) {
		return pool.get(value);
	}

	public int getPoolSize() {
		return pool.size();
	}

	public void clear() {
		pool.clear();
	}

	public Iterable<T> getPooled() {
		return pool;
	}
}
