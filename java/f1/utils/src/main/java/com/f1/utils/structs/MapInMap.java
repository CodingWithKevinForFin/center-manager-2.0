package com.f1.utils.structs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import com.f1.base.Lockable;
import com.f1.utils.DetailedException;
import com.f1.utils.MapWrapper;
import com.f1.utils.OH;
import com.f1.utils.concurrent.MapFactory;

public class MapInMap<K1, K2, V> extends MapWrapper<K1, Map<K2, V>> implements Lockable {
	public static final MapFactory FACTORY_HASHMAP = new MapFactory() {
		@Override
		public Map newMap() {
			return new HashMap();
		}

		@Override
		public Map newMap(Map m) {
			return new HashMap(m);
		}
	};
	public static final MapFactory FACTORY_LINKED_HASHMAP = new MapFactory() {
		@Override
		public Map newMap() {
			return new LinkedHashMap();
		}

		@Override
		public Map newMap(Map m) {
			return new LinkedHashMap(m);
		}
	};
	public static final MapFactory FACTORY_TREEMAP = new MapFactory() {

		@Override
		public Map newMap() {
			return new TreeMap();
		}

		@Override
		public Map newMap(Map m) {
			return new TreeMap(m);
		}
	};

	private boolean locked = false;
	private boolean removeEmptyPolicy = true;
	private MapFactory<K2, V> mapFactory = FACTORY_HASHMAP;

	public MapInMap() {
		super(new HashMap<K1, Map<K2, V>>());
	}
	public MapInMap(Map<K1, Map<K2, V>> toCopy) {
		super(new HashMap<K1, Map<K2, V>>());
		for (java.util.Map.Entry<K1, Map<K2, V>> e : toCopy.entrySet())
			putAllMulti(e.getKey(), newMap(e.getValue()));
	}
	public V putMulti(K1 k1, K2 k2, V value) {
		Map<K2, V> m = get(k1);
		if (m == null)
			put(k1, m = newMap());
		return m.put(k2, value);
	}
	public V putMultiOrThrow(K1 k1, K2 k2, V value) {
		Map<K2, V> m = get(k1);
		if (m == null)
			put(k1, m = newMap());
		else if (m.containsKey(k2) && m.get(k2) != value)
			throw new DetailedException("key already exists in map and associated with different value").set("key1", k1).set("key2", k2).set("supplied value", value)
					.set("existing value", m.get(k2));
		return m.put(k2, value);
	}
	public Map<K2, V> getOrCreate(K1 k1) {
		Map<K2, V> m = get(k1);
		if (m == null)
			put(k1, m = newMap());
		return m;
	}

	private Map<K2, V> newMap() {
		return mapFactory.newMap();
	}
	private Map<K2, V> newMap(Map<K2, V> m) {
		return mapFactory.newMap(m);
	}

	public void putAllMulti(K1 k1, Map<K2, V> val) {
		Map<K2, V> m = get(k1);
		if (m == null)
			put(k1, m = newMap());
		m.putAll(val);
	}
	public void putAllMulti(Map<K1, Map<K2, V>> val) {
		for (Entry<K1, Map<K2, V>> e : val.entrySet())
			putAllMulti(e.getKey(), e.getValue());
	}

	// Returns the first occurrence, or null if none.
	public Iterable<java.util.Map.Entry<K2, V>> getMulti(K1 k1) {
		final Map<K2, V> m = get(k1);
		if (m == null)
			return Collections.EMPTY_SET;
		return m.entrySet();
	}

	public V getMulti(K1 k1, K2 k2) {
		final Map<K2, V> m = get(k1);
		return m == null ? null : m.get(k2);
	}

	public V removeMulti(K1 k1, K2 k2) {
		Map<K2, V> m = get(k1);
		if (m == null)
			return null;
		V r = m.remove(k2);
		if (removeEmptyPolicy && m.size() == 0)
			remove(k1);
		return r;
	}
	public boolean removeMulti(K1 k1, K2 k2, V expecting) {
		Map<K2, V> m = get(k1);
		if (m == null || OH.ne(m.get(k2), expecting))
			return false;
		m.remove(k2);
		if (removeEmptyPolicy && m.size() == 0)
			remove(k1);
		return true;
	}

	//NOTE: the tuple3 is reused!!!
	public Iterable<Tuple3<K1, K2, V>> entrySetMulti() {
		return new MapInMapIterable();
	}
	public Iterable<V> valuesMulti() {
		return new MapInMapValuesIterable();
	}

	private class MapInMapIterable implements Iterable<Tuple3<K1, K2, V>> {

		@Override
		public Iterator<Tuple3<K1, K2, V>> iterator() {
			return new MapInMapIterator();
		}
	}

	private class MapInMapValuesIterable implements Iterable<V> {

		@Override
		public Iterator<V> iterator() {
			return new MapInMapValuesIterator();
		}
	}

	private class MapInMapValuesIterator implements Iterator<V> {

		private MapInMapIterator inner = new MapInMapIterator();

		@Override
		public boolean hasNext() {
			return inner.hasNext();
		}
		@Override
		public V next() {
			return inner.next().getC();
		}
		@Override
		public void remove() {
			inner.remove();
		}

	}

	private class MapInMapIterator implements Iterator<Tuple3<K1, K2, V>> {
		private Tuple3<K1, K2, V> tmp = new Tuple3<K1, K2, V>();

		//Iterator over the outer map
		private Iterator<Entry<K1, Map<K2, V>>> iterator;

		//iterator for the inner map
		private Iterator<Entry<K2, V>> currentIterator;

		//Current entry of the outer map which is being iterated over
		private Entry<K1, Map<K2, V>> entry;

		private MapInMapIterator() {
			this.iterator = entrySet().iterator();
			walkIterator();
		}

		@Override
		public Tuple3<K1, K2, V> next() {
			final Entry<K2, V> e = currentIterator.next();
			tmp.setABC(entry.getKey(), e.getKey(), e.getValue());
			Tuple3<K1, K2, V> r = tmp;
			if (!currentIterator.hasNext())
				walkIterator();
			return r;
		}

		private void walkIterator() {
			while (iterator.hasNext()) {
				entry = iterator.next();
				currentIterator = entry.getValue().entrySet().iterator();
				if (currentIterator.hasNext()) {
					return;
				}
			}
			currentIterator = null;
			tmp = null;
		}

		@Override
		public boolean hasNext() {
			return currentIterator != null;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	public void lock() {
		if (locked)
			return;
		for (java.util.Map.Entry<K1, Map<K2, V>> e : this.entrySet())
			e.setValue(Collections.unmodifiableMap(e.getValue()));
		setInnerMap(Collections.unmodifiableMap(getInnerMap()));
		this.locked = true;
	}
	@Override
	public boolean isLocked() {
		return locked;
	}
	public boolean containsKey(K1 k1, K2 k2) {
		Map<K2, V> t = get(k1);
		return t != null && t.containsKey(k2);
	}
	public boolean isRemoveEmptyPolicy() {
		return removeEmptyPolicy;
	}
	public MapInMap<K1, K2, V> setRemoveEmptyPolicy(boolean removeEmptyPolicy) {
		this.removeEmptyPolicy = removeEmptyPolicy;
		return this;
	}
	public MapInMap<K1, K2, V> setMapFactory(MapFactory<K2, V> factory) {
		this.mapFactory = factory;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof MapInMap && OH.eq(getInnerMap(), ((MapInMap) obj).getInnerMap());
	}
}
