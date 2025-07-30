/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.f1.base.Factory;

public class CopyOnWriteHashMap<K, V> extends MapWrapper<K, V> implements ConcurrentMap<K, V> {

	volatile private Map<K, V> map;
	private Factory<Map<K, V>, Map<K, V>> factory;

	public CopyOnWriteHashMap() {
		super(Collections.EMPTY_MAP);
	}

	public CopyOnWriteHashMap(Factory<Map<K, V>, Map<K, V>> factory) {
		super(factory.get(null));
		this.factory = factory;
	}

	private Map<K, V> copyMap() {
		if (factory != null)
			return factory.get(getInnerMap());
		else
			return new HashMap<K, V>(getInnerMap());
	}

	@Override
	synchronized public void clear() {
		setInnerMap(Collections.EMPTY_MAP);
	}

	@Override
	public void setInnerMap(Map<K, V> map) {
		this.map = Collections.unmodifiableMap(map);
	}

	@Override
	synchronized public V putIfAbsent(K arg0, V arg1) {
		return !getInnerMap().containsKey(arg0) ? put(arg0, arg1) : getInnerMap().get(arg0);
	}

	@Override
	synchronized public boolean remove(Object arg0, Object arg1) {
		V existing = getInnerMap().get(arg0);
		if ((existing == null && !getInnerMap().containsKey(arg0)) || OH.ne(existing, arg1))
			return false;
		remove(arg0);
		return true;
	}

	@Override
	synchronized public V replace(K arg0, V arg1) {
		return getInnerMap().containsKey(arg0) ? put(arg0, arg1) : null;
	}

	@Override
	synchronized public boolean replace(K arg0, V arg1, V arg2) {
		V existing = getInnerMap().get(arg0);
		if ((existing == null && !getInnerMap().containsKey(arg0)) || OH.ne(existing, arg1))
			return false;
		put(arg0, arg2);
		return true;
	}

	@Override
	synchronized public V put(K key, V value) {
		Map<K, V> copy = copyMap();
		V r = copy.put(key, value);
		setInnerMap(copy);
		onChange();
		return r;
	}

	@Override
	synchronized public void putAll(Map<? extends K, ? extends V> m) {
		Map<K, V> copy = copyMap();
		copy.putAll(m);
		setInnerMap(copy);
		onChange();
	}

	@Override
	synchronized public V remove(Object key) {
		Map<K, V> copy = copyMap();
		V r = copy.remove(key);
		setInnerMap(copy);
		onChange();
		return r;
	}

	@Override
	public Map<K, V> getInnerMap() {
		return map;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || o.getClass() != CopyOnWriteHashMap.class)
			return false;
		return OH.eq(map, ((CopyOnWriteHashMap) o).map);
	}

	@Override
	public int hashCode() {
		return map == null ? 0 : map.hashCode();
	};
}
