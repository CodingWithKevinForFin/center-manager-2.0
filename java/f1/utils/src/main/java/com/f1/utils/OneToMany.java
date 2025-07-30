package com.f1.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a map which is one to many, meaning for one key there may be any number of values. Unlike a multimap (or regular map) the value may only exist once. All operations
 * are constant time... It's note worthy to mention that it is also constant time to look up a key based on a value.
 * 
 * @param <K>
 * @param <V>
 */
public class OneToMany<K, V> {

	private Map<K, Set<V>> k2v = new HashMap<K, Set<V>>();
	private Map<V, K> v2k = new HashMap<V, K>();

	public void put(K key, V value) {
		K existingKey = getKey(value);
		if (key.equals(existingKey)) {
			return;// nothing has changed
		} else if (existingKey != null) {
			if (!k2v.get(existingKey).remove(value))
				throw new IllegalStateException();
			v2k.put(value, key);
		} else {
			v2k.put(value, key);
		}
		Set<V> set = k2v.get(key);
		if (set == null)
			set = new HashSet<V>();
		set.add(value);
		k2v.put(key, set);
	}

	public K removeValue(V value) {
		K key = v2k.remove(value);
		if (key != null)
			if (!k2v.get(key).remove(value))
				throw new IllegalStateException();
		return key;
	}

	public Set<V> getValues(K key) {
		return k2v.get(key);
	}

	public K getKey(V value) {
		return v2k.get(value);
	}

	public Iterable<K> getKeys() {
		return k2v.keySet();
	}

}
