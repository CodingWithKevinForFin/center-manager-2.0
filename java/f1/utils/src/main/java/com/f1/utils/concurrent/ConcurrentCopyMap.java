package com.f1.utils.concurrent;

import java.util.Arrays;
import java.util.Map;
import java.util.Stack;

public class ConcurrentCopyMap<K, V> extends HasherMap<K, V> {

	public static final int DEFAULT_ARRAY_SIZE = 8;

	private CCEntry<K, V> entries[] = new CCEntry[DEFAULT_ARRAY_SIZE];
	private int next = 0;
	private Stack<Integer> deleted = new Stack<Integer>();

	public ConcurrentCopyMap() {
		super();
	}

	public ConcurrentCopyMap(Map<K, V> map) {
		super(map);
	}

	public ConcurrentCopyMap(int length) {
		super(length);
	}

	@Override
	public V put(K key, V value) {
		HasherMap.Entry<K, V> entry = getEntry(key);
		if (entry != null)
			return entry.setValue(value);
		CCEntry<K, V> r = (CCEntry<K, V>) putAndReturn(key, value);
		int index;
		if (deleted.isEmpty()) {
			if ((index = next++) == entries.length)
				entries = Arrays.copyOf(entries, entries.length * 2);
		} else
			index = deleted.pop();
		r.arrayLocation = index;
		entries[index] = r;
		return null;
	}

	public void getEntriesCopy(Map<K, V> sink) {
		CCEntry<K, V>[] entries = this.entries;
		for (Entry<K, V> e : entries) {
			if (e != null)
				sink.put(e.key, e.value);
		}

	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
			put(e.getKey(), e.getValue());
	}

	@Override
	public V remove(Object key) {
		CCEntry<K, V> r = (CCEntry<K, V>) removeEntry(key);
		if (r == null)
			return null;
		entries[r.arrayLocation] = null;
		deleted.add(r.arrayLocation);
		return r.value;
	}

	protected HasherMap.Entry<K, V> newEntry(int hash, K key, V value, Entry<K, V> next) {
		return new CCEntry<K, V>(hash, key, value, next);
	}

	public static class CCEntry<K, V> extends HasherMap.Entry<K, V> {
		public int arrayLocation;

		CCEntry(int h, K k, V v, HasherMap.Entry<K, V> n) {
			super(h, k, v, n);
		}
	}

}
