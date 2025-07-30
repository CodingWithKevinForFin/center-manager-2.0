package com.f1.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a map which is one to many, meaning for one key there may be any
 * number of values. Unlike a multimap (or regular map) the value may only exist
 * once. All operations are constant time... It's note worthy to mention that it
 * is also constant time to look up a key based on a value.
 * 
 * @param <K>
 * @param <V>
 */
public class ManyToMany<K, V> {

	private Map<K, Set<V>> k2v = new HashMap<K, Set<V>>();
	private Map<V, Set<K>> v2k = new HashMap<V, Set<K>>();

	public void put(K key, V val) {
		if (key == null)
			throw new NullPointerException("key");
		if (val == null)
			throw new NullPointerException("value");
		Set<K> keys = v2k.get(val);
		Set<V> vals = k2v.get(key);
		if (keys == null)
			v2k.put(val, keys = new HashSet<K>());
		if (vals == null)
			k2v.put(key, vals = new HashSet<V>());
		keys.add(key);
		vals.add(val);
	}

	public boolean removeValue(K key, V val) {
		if (key == null)
			throw new NullPointerException("key");
		if (val == null)
			throw new NullPointerException("value");
		Set<K> keys = v2k.get(val);
		boolean r1 = false;
		boolean r2 = false;
		if (keys != null) {
			r1 = keys.remove(key);
			if (keys.size() == 0)
				v2k.remove(val);
		}

		Set<V> vals = k2v.get(key);
		if (vals != null) {
			r2 = vals.remove(val);
			if (vals.size() == 0)
				k2v.remove(key);
		}
		OH.assertEq(r1, r2);// integrety check.. could be removed.
		return r1;
	}

	public Set<V> getValues(K key) {
		return k2v.get(key);
	}

	public Set<K> getKeys(V value) {
		return v2k.get(value);
	}

	public Iterable<K> getKeys() {
		return k2v.keySet();
	}

	public Iterable<V> getValues() {
		return v2k.keySet();
	}

	public Map<K, Set<V>> getKeysToValues() {
		return k2v;
	}

	public Map<V, Set<K>> getValuesToKeys() {
		return v2k;
	}

}
