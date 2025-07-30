package com.f1.utils.mapping;

import java.util.Iterator;

import com.f1.base.Mapping;
import com.f1.base.MappingEntry;
import com.f1.base.ToStringable;

public class MappingIterable<K, V> implements Iterable<MappingEntry<K, V>> {

	public class MappingIterator implements Iterator<MappingEntry<K, V>>, MappingEntry<K, V>, ToStringable {

		final private Iterator<K> innerIterator;
		private V value;
		private K key;

		public MappingIterator(Iterator<K> iterator) {
			innerIterator = iterator;
		}

		@Override
		public boolean hasNext() {
			return innerIterator.hasNext();
		}

		@Override
		public MappingEntry<K, V> next() {
			this.key = innerIterator.next();
			this.value = inner.get(key);
			return this;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public K getKey() {
			return this.key;
		}

		@Override
		public V getValue() {
			return this.value;
		}

		@Override
		public String toString() {
			return toString(new StringBuilder()).toString();
		}

		@Override
		public StringBuilder toString(StringBuilder sink) {
			return sink.append(this.key).append("=").append(this.value);
		}

	}

	final private Mapping<K, V> inner;

	public MappingIterable(Mapping<K, V> inner) {
		this.inner = inner;
	}

	@Override
	public Iterator<MappingEntry<K, V>> iterator() {
		return new MappingIterator(inner.iterator());
	}

}
