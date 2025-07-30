package com.f1.utils.mapping;

import java.util.Iterator;

import com.f1.base.Getter;
import com.f1.base.Mapping;
import com.f1.base.MappingEntry;
import com.f1.utils.structs.Tuple2;

public class MappingWrapper<K, V1, V> implements Mapping<K, V> {

	private Mapping<K, V1> inner;
	private Entries entries;
	private Getter<V1, V> valGetter;

	public MappingWrapper(Mapping<K, V1> inner, Getter<V1, V> valueGetter) {
		this.inner = inner;
		this.valGetter = valueGetter;
	}

	protected V mapValue(V1 value) {
		return valGetter.get(value);
	}

	@Override
	public V get(K key) {
		return mapValue(inner.get(key));
	}

	@Override
	public int size() {
		return inner.size();
	}

	@Override
	public Iterator<K> iterator() {
		return inner.iterator();
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
			return new EntryIterator(inner.entries().iterator());
		}

	}

	public class EntryIterator implements Iterator<MappingEntry<K, V>> {

		private Tuple2<K, V> entry = new Tuple2<K, V>();
		private Iterator<MappingEntry<K, V1>> inner;

		public EntryIterator(Iterator<MappingEntry<K, V1>> iterator) {
			this.inner = iterator;
		}

		@Override
		public boolean hasNext() {
			return inner.hasNext();
		}

		@Override
		public MappingEntry<K, V> next() {
			MappingEntry<K, V1> i = inner.next();
			entry.setAB(i.getKey(), mapValue(i.getValue()));
			return entry;
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
