/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class MapWrapper<K, V> implements Map<K, V>, Serializable {
	private Map<K, V> map;

	public MapWrapper(Map<K, V> map) {
		setInnerMap(map);
	}
	public MapWrapper() {

	}

	@Override
	public String toString() {
		return getInnerMap() == null ? "null" : getInnerMap().toString();
	}

	@Override
	public boolean containsKey(Object key) {
		return getInnerMap().containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return getInnerMap().containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return getInnerMap().entrySet();
	}

	@Override
	public V get(Object key) {
		return getInnerMap().get(key);
	}

	@Override
	public boolean isEmpty() {
		return getInnerMap().isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return getInnerMap().keySet();
	}

	@Override
	public V put(K key, V value) {
		V r = getInnerMap().put(key, value);
		onChange();
		return r;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		getInnerMap().putAll(m);
		onChange();
	}

	@Override
	public V remove(Object key) {
		V r = getInnerMap().remove(key);
		onChange();
		return r;
	}

	@Override
	public void clear() {
		getInnerMap().clear();
		onChange();
	}

	@Override
	public int size() {
		return getInnerMap().size();
	}

	@Override
	public Collection<V> values() {
		return getInnerMap().values();
	}

	public void setInnerMap(Map<K, V> map) {
		this.map = map;
		onChange();
	}

	public Map<K, V> getInnerMap() {
		return map;
	}

	protected void onChange() {
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o.getClass() == getClass())
			return OH.eq(getInnerMap(), ((MapWrapper) o).getInnerMap());
		return false;
	}

	@Override
	public int hashCode() {
		return getInnerMap().hashCode();
	}

}
