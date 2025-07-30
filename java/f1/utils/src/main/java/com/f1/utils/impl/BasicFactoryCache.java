package com.f1.utils.impl;

import java.util.concurrent.ConcurrentMap;
import com.f1.utils.CopyOnWriteHashMap;
import com.f1.base.Factory;
import com.f1.base.Getter;

public class BasicFactoryCache<K, V> implements Getter<K, V> {
	ConcurrentMap<K, V> cache = new CopyOnWriteHashMap<K, V>();
	private Factory<K, ? extends V> factory;
	private Object SEMEPHORE = new Object();

	public BasicFactoryCache(Factory<K, ? extends V> factory) {
		this.factory = factory;
	}

	@Override
	public V get(K key) {
		V r = cache.get(key);
		if (r == null) {
			synchronized (SEMEPHORE) {
				r = cache.get(key);
				if (r == null) {
					r = factory.get(key);
				}
				cache.put(key, r);
			}
		}
		return r;
	}

}
