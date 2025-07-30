package com.f1.utils.mapping;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.f1.base.Mapping;
import com.f1.base.MappingEntry;
import com.f1.utils.AH;
import com.f1.utils.EmptyIterator;

public class MappingsBackedMapping<K, V> implements Mapping<K, V> {

	private final Mapping<K, V>[] getters;
	private Entries entries;
	private boolean suppressDups;

	public MappingsBackedMapping(boolean suppressDups, Mapping<K, V>... getters) {
		AH.assertNotNull(getters);
		this.suppressDups = suppressDups;
		this.getters = getters;
	}

	@Override
	public V get(K key) {
		V r;
		for (int i = 0; i < getters.length; i++) {
			final Mapping<K, V> getter = getters[i];
			if (getter.containsKey(key))
				return getter.get(key);
		}
		return null;
	}

	@Override
	public Iterator<K> iterator() {
		return new Iter();
	}

	@Override
	public int size() {
		int r = 0;
		if (suppressDups)
			for (K k : this)
				r++;
		else
			for (int i = 0; i < getters.length; i++)
				r += getters[i].size();
		return r;
	}

	@Override
	public boolean containsKey(K key) {
		for (int i = 0; i < getters.length; i++)
			if (getters[i].containsKey(key))
				return true;
		return false;
	}

	@Override
	public Iterable<MappingEntry<K, V>> entries() {
		if (entries == null)
			entries = new Entries();
		return entries;
	}

	private class Entries implements Iterable<MappingEntry<K, V>> {

		@Override
		public Iterator<MappingEntry<K, V>> iterator() {
			if (getters.length == 0)
				return EmptyIterator.INSTANCE;
			else if (getters.length == 1)
				return getters[0].entries().iterator();
			else
				return new EntriesIterator(getters);
		}

	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		boolean isFirst = true;
		for (MappingEntry<K, V> i : this.entries()) {
			if (isFirst)
				isFirst = false;
			else
				sb.append(", ");
			sb.append(i.getKey()).append("=").append(i.getValue());
		}
		sb.append("}");
		return sb.toString();
	}

	public class EntriesIterator implements Iterator<MappingEntry<K, V>> {
		private Iterator<MappingEntry<K, V>> currentIterator;
		private int iteratorsPos = 0;
		private MappingEntry<K, V> next;

		public EntriesIterator(Mapping<K, V>[] getters) {
			if (getters.length > 0) {
				currentIterator = getters[0].entries().iterator();
				walkIterator();
			}
		}

		@Override
		public MappingEntry<K, V> next() {
			if (next == null)
				throw new NoSuchElementException();
			MappingEntry<K, V> r = next;
			walkIterator();
			return r;
		}

		private void walkIterator() {
			for (;;) {
				while (currentIterator.hasNext()) {
					next = currentIterator.next();
					if (!alreadyHit(next.getKey()))
						return;
				}
				iteratorsPos++;
				if (iteratorsPos == getters.length)
					break;
				currentIterator = getters[iteratorsPos].entries().iterator();
			}
			currentIterator = null;
			next = null;
		}
		private boolean alreadyHit(K k) {
			if (suppressDups)
				for (int i = 0; i < iteratorsPos; i++)
					if (getters[i].containsKey(k))
						return true;
			return false;
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("can not remove");
		}

	}

	public class Iter implements Iterator<K> {
		private Iterator<K> currentIterator;
		private int iteratorsPos = 0;
		private K next;
		private boolean hasNext;

		public Iter() {
			if (getters.length > 0) {
				currentIterator = getters[0].iterator();
				hasNext = true;
				walkIterator();
			}
		}

		@Override
		public K next() {
			if (next == null)
				throw new NoSuchElementException();
			K r = next;
			walkIterator();
			return r;
		}

		private void walkIterator() {
			for (;;) {
				while (currentIterator.hasNext()) {
					next = currentIterator.next();
					if (!alreadyHit(next))
						return;
				}
				iteratorsPos++;
				if (iteratorsPos == getters.length)
					break;
				currentIterator = getters[iteratorsPos].iterator();
			}
			currentIterator = null;
			next = null;
			hasNext = false;
		}
		private boolean alreadyHit(K k) {
			if (suppressDups)
				for (int i = 0; i < iteratorsPos; i++)
					if (getters[i].containsKey(k))
						return true;
			return false;
		}

		@Override
		public boolean hasNext() {
			return hasNext;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("can not remove");
		}

	}

}
