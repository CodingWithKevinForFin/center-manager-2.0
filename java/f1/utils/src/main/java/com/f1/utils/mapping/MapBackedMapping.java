package com.f1.utils.mapping;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.base.Mapping;
import com.f1.base.MappingEntry;
import com.f1.utils.structs.Tuple2;

public class MapBackedMapping<K, V> implements Mapping<K, V> {

	private Map<K, V> inner;
	private Entries entries;

	public MapBackedMapping(Map<K, V> inner) {
		this.inner = inner;
	}

	public Map<K, V> getInner() {
		return inner;
	}

	@Override
	public V get(K key) {
		return inner.get(key);
	}

	@Override
	public int size() {
		return inner.size();
	}

	@Override
	public Iterator<K> iterator() {
		return inner.keySet().iterator();
	}

	@Override
	public boolean containsKey(K key) {
		return inner.containsKey(key);
	}

	@Override
	public Iterable<MappingEntry<K, V>> entries() {
		if (entries == null)
			entries = new Entries();
		return entries;

	}

	public class Entries implements Iterable<MappingEntry<K, V>> {

		@Override
		public Iterator<MappingEntry<K, V>> iterator() {
			return new Iter(inner.entrySet().iterator());
		}

	}

	public class Iter implements Iterator<MappingEntry<K, V>> {

		private Tuple2<K, V> entry = new Tuple2<K, V>();
		private Iterator<Entry<K, V>> inner;

		public Iter(Iterator<Entry<K, V>> iterator) {
			this.inner = iterator;
		}

		@Override
		public boolean hasNext() {
			return inner.hasNext();
		}

		@Override
		public MappingEntry<K, V> next() {
			Entry<K, V> i = inner.next();
			entry.setAB(i.getKey(), i.getValue());
			return entry;
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
