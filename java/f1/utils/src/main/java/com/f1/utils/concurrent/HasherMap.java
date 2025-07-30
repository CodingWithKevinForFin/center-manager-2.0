package com.f1.utils.concurrent;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.f1.base.IterableAndSize;
import com.f1.base.Mapping;
import com.f1.base.MappingEntry;
import com.f1.base.ToStringable;
import com.f1.utils.Hasher;
import com.f1.utils.ToDoException;
import com.f1.utils.impl.BasicHasher;
import com.f1.utils.structs.ComparableComparator;

public class HasherMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Mapping<K, V>, Cloneable, ToStringable {

	static final int DEFAULT_INITIAL_CAPACITY = 16;
	static final int MAXIMUM_CAPACITY = 1 << 30;
	static final float DEFAULT_LOAD_FACTOR = 0.75f;
	public static final MapFactory FACTORY = new MapFactory() {

		@Override
		public Map newMap() {
			return new HasherMap();
		}

		@Override
		public Map newMap(Map m) {
			return new HasherMap(m);
		}
	};
	Entry[] table;
	int size;
	int threshold;
	final float loadFactor;
	int modCount;
	private List<K> keysSorted;

	public HasherMap(int initialCapacity, float loadFactor) {
		this(BasicHasher.INSTANCE, initialCapacity, loadFactor);
	}

	public HasherMap(Hasher hasher, int initialCapacity, float loadFactor) {
		this.hasher = hasher;
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
		if (initialCapacity > MAXIMUM_CAPACITY)
			initialCapacity = MAXIMUM_CAPACITY;
		if (loadFactor <= 0 || Float.isNaN(loadFactor))
			throw new IllegalArgumentException("Illegal load factor: " + loadFactor);

		// Find a power of 2 >= initialCapacity
		int capacity = 1;
		while (capacity < initialCapacity)
			capacity <<= 1;

		this.loadFactor = loadFactor;
		threshold = (int) (capacity * loadFactor);
		table = new Entry[capacity];
	}

	public HasherMap(int initialCapacity) {
		this(BasicHasher.INSTANCE, initialCapacity);
	}

	public HasherMap(Hasher hasher, int initialCapacity) {
		this(hasher, initialCapacity, DEFAULT_LOAD_FACTOR);
	}

	public HasherMap(Mapping<K, V> values) {
		this(BasicHasher.INSTANCE, values.size(), DEFAULT_LOAD_FACTOR);
		for (K i : values)
			put(i, values.get(i));
	}
	public HasherMap() {
		this(BasicHasher.INSTANCE);
	}

	public HasherMap(Hasher hasher) {
		this.hasher = hasher;
		this.loadFactor = DEFAULT_LOAD_FACTOR;
		threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
		table = new Entry[DEFAULT_INITIAL_CAPACITY];
	}

	public HasherMap(Map<? extends K, ? extends V> m) {
		this(BasicHasher.INSTANCE, m);
	}
	public HasherMap(HasherMap<K, V> m) {
		this(BasicHasher.INSTANCE, m);
	}

	public HasherMap(Hasher hasher, Map<? extends K, ? extends V> m) {
		this(hasher, Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1, DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
		putAllForCreate(m);
	}

	static int hash(int h) {
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	static int indexFor(int h, int length) {
		return h & (length - 1);
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public V get(Object key) {
		if (key == null)
			return getForNullKey();
		int hash = hash(hasher.hashcode(key));
		for (Entry<K, V> e = table[indexFor(hash, table.length)]; e != null; e = e.next) {
			if (e.hash == hash && hasher.areEqual(key, e.key))
				return e.value;
		}
		return null;
	}

	private V getForNullKey() {
		for (Entry<K, V> e = table[0]; e != null; e = e.next) {
			if (e.key == null)
				return e.value;
		}
		return null;
	}

	public boolean containsKey(Object key) {
		return getEntry(key) != null;
	}

	public final Entry<K, V> getEntry(Object key) {
		int hash = (key == null) ? 0 : hash(hasher.hashcode(key));
		for (Entry<K, V> e = table[indexFor(hash, table.length)]; e != null; e = e.next) {
			if (e.hash == hash && hasher.areEqual(key, e.key))
				return e;
		}
		return null;
	}

	public Entry<K, V> getOrCreateEntry(K key) {
		if (key == null)
			return getOrCreateForNullKey();
		int hash = hash(hasher.hashcode(key));
		int i = indexFor(hash, table.length);
		for (Entry<K, V> e = table[i]; e != null; e = e.next) {
			Object k;
			if (e.hash == hash && hasher.areEqual(key, e.key)) {
				return e;
			}
		}

		modCount++;
		return addEntry(hash, key, null, i);
	}

	public V put(K key, V value) {
		if (key == null)
			return putForNullKey(value);
		int hash = hash(hasher.hashcode(key));
		int i = indexFor(hash, table.length);
		for (Entry<K, V> e = table[i]; e != null; e = e.next) {
			Object k;
			if (e.hash == hash && hasher.areEqual(key, e.key)) {
				V oldValue = e.value;
				e.value = value;
				return oldValue;
			}
		}

		modCount++;
		addEntry(hash, key, value, i);
		return null;
	}

	public Entry<K, V> putAndReturn(K key, V value) {
		if (key == null)
			return putForNullKeyAndReturn(value);
		int hash = hash(hasher.hashcode(key));
		int i = indexFor(hash, table.length);
		for (Entry<K, V> e = table[i]; e != null; e = e.next) {
			Object k;
			if (e.hash == hash && hasher.areEqual(key, e.key)) {
				V oldValue = e.value;
				e.value = value;
				return e;
			}
		}

		modCount++;
		return addEntry(hash, key, value, i);
	}

	protected Entry<K, V> put(Entry<K, V> entry) {
		K key = entry.key;
		V value = entry.value;
		if (key == null)
			throw new ToDoException("null key not support at this time");
		int hash = hash(hasher.hashcode(key));
		int i = indexFor(hash, table.length);
		for (Entry<K, V> e = table[i]; e != null; e = e.next) {
			Object k;
			if (e.hash == hash && hasher.areEqual(key, e.key)) {
				V oldValue = e.value;
				e.value = value;
				return e;
			}
		}

		modCount++;
		addEntry(entry, i);
		return null;
	}

	private V putForNullKey(V value) {
		for (Entry<K, V> e = table[0]; e != null; e = e.next) {
			if (e.key == null) {
				V oldValue = e.value;
				e.value = value;
				return oldValue;
			}
		}
		modCount++;
		addEntry(0, null, value, 0);
		return null;
	}

	private Entry<K, V> getOrCreateForNullKey() {
		for (Entry<K, V> e = table[0]; e != null; e = e.next) {
			if (e.key == null) {
				V oldValue = e.value;
				return e;
			}
		}
		modCount++;
		return addEntry(0, null, null, 0);
	}

	private Entry<K, V> putForNullKeyAndReturn(V value) {
		for (Entry<K, V> e = table[0]; e != null; e = e.next) {
			if (e.key == null) {
				V oldValue = e.value;
				e.value = value;
				return e;
			}
		}
		modCount++;
		return addEntry(0, null, value, 0);
	}

	private void putForCreate(K key, V value) {
		int hash = (key == null) ? 0 : hash(hasher.hashcode(key));
		int i = indexFor(hash, table.length);

		for (Entry<K, V> e = table[i]; e != null; e = e.next) {
			Object k;
			if (e.hash == hash && hasher.areEqual(key, e.key)) {
				e.value = value;
				return;
			}
		}

		createEntry(hash, key, value, i);
	}

	private void putAllForCreate(Map<? extends K, ? extends V> m) {
		for (Iterator<? extends Map.Entry<? extends K, ? extends V>> i = m.entrySet().iterator(); i.hasNext();) {
			Map.Entry<? extends K, ? extends V> e = i.next();
			putForCreate(e.getKey(), e.getValue());
		}
	}

	void resize(int newCapacity) {
		Entry[] oldTable = table;
		int oldCapacity = oldTable.length;
		if (oldCapacity == MAXIMUM_CAPACITY) {
			threshold = Integer.MAX_VALUE;
			return;
		}

		Entry[] newTable = new Entry[newCapacity];
		transfer(newTable);
		table = newTable;
		threshold = (int) (newCapacity * loadFactor);
	}

	void transfer(Entry[] newTable) {
		Entry[] src = table;
		int newCapacity = newTable.length;
		for (int j = 0; j < src.length; j++) {
			Entry<K, V> e = src[j];
			if (e != null) {
				src[j] = null;
				do {
					Entry<K, V> next = e.next;
					int i = indexFor(e.hash, newCapacity);
					e.next = newTable[i];
					newTable[i] = e;
					e = next;
				} while (e != null);
			}
		}
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		int numKeysToBeAdded = m.size();
		if (numKeysToBeAdded == 0)
			return;

		if (numKeysToBeAdded > threshold) {
			int targetCapacity = (int) (numKeysToBeAdded / loadFactor + 1);
			if (targetCapacity > MAXIMUM_CAPACITY)
				targetCapacity = MAXIMUM_CAPACITY;
			int newCapacity = table.length;
			while (newCapacity < targetCapacity)
				newCapacity <<= 1;
			if (newCapacity > table.length)
				resize(newCapacity);
		}

		for (Iterator<? extends Map.Entry<? extends K, ? extends V>> i = m.entrySet().iterator(); i.hasNext();) {
			Map.Entry<? extends K, ? extends V> e = i.next();
			put(e.getKey(), e.getValue());
		}
	}

	public V remove(Object key) {
		Entry<K, V> e = removeEntryForKey(key);
		return (e == null ? null : e.value);
	}

	public Entry<K, V> removeEntry(Object key) {
		Entry<K, V> e = removeEntryForKey(key);
		return (e == null ? null : e);
	}

	final Entry<K, V> removeEntryForKey(Object key) {
		int hash = (key == null) ? 0 : hash(hasher.hashcode(key));
		int i = indexFor(hash, table.length);
		Entry<K, V> prev = table[i];
		Entry<K, V> e = prev;

		while (e != null) {
			Entry<K, V> next = e.next;
			Object k;
			if (e.hash == hash && ((k = e.key) == key || (key != null && hasher.areEqual(key, k)))) {
				modCount++;
				size--;
				if (prev == e)
					table[i] = next;
				else
					prev.next = next;
				onKeysChanged();
				return e;
			}
			prev = e;
			e = next;
		}

		return e;
	}
	final Entry<K, V> removeMapping(Object o) {
		if (!(o instanceof Map.Entry))
			return null;

		Map.Entry<K, V> entry = (Map.Entry<K, V>) o;
		Object key = entry.getKey();
		int hash = (key == null) ? 0 : hash(hasher.hashcode(key));
		int i = indexFor(hash, table.length);
		Entry<K, V> prev = table[i];
		Entry<K, V> e = prev;

		while (e != null) {
			Entry<K, V> next = e.next;
			if (e.hash == hash && e.equals(entry)) {
				modCount++;
				size--;
				if (prev == e)
					table[i] = next;
				else
					prev.next = next;
				onKeysChanged();
				return e;
			}
			prev = e;
			e = next;
		}

		return e;
	}

	public void clear() {
		if (size == 0)
			return;
		modCount++;
		Entry[] tab = table;
		for (int i = 0; i < tab.length; i++)
			tab[i] = null;
		size = 0;
		onKeysChanged();
	}

	public boolean containsValue(Object value) {
		if (value == null)
			return containsNullValue();

		Entry[] tab = table;
		for (int i = 0; i < tab.length; i++)
			for (Entry e = tab[i]; e != null; e = e.next)
				if (value.equals(e.value))
					return true;
		return false;
	}

	private boolean containsNullValue() {
		Entry[] tab = table;
		for (int i = 0; i < tab.length; i++)
			for (Entry e = tab[i]; e != null; e = e.next)
				if (e.value == null)
					return true;
		return false;
	}

	@Override
	public HasherMap<K, V> clone() {
		try {
			HasherMap<K, V> result = (HasherMap<K, V>) super.clone();
			result.entrySet = null;
			result.modCount = 0;
			result.values = null;
			Entry<K, V>[] tbl = result.table.clone();
			for (int i = 0; i < tbl.length; i++) {
				Entry<K, V> t = tbl[i];
				if (t != null) {
					tbl[i] = t = t.clone();
					while (t.next != null)
						t = t.next = t.next.clone();
				}
			}
			result.table = tbl;
			return result;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public static class Entry<K, V> implements Map.Entry<K, V>, MappingEntry<K, V>, Cloneable {
		final K key;
		V value;
		Entry<K, V> next;
		final int hash;

		Entry(int h, K k, V v, Entry<K, V> n) {
			value = v;
			next = n;
			key = k;
			hash = h;
		}

		public final K getKey() {
			return key;
		}

		public final V getValue() {
			return value;
		}

		public final V setValue(V newValue) {
			V oldValue = value;
			value = newValue;
			return oldValue;
		}

		public final boolean equals(Object o) {
			if (!(o instanceof Map.Entry))
				return false;
			Map.Entry e = (Map.Entry) o;
			Object k1 = getKey();
			Object k2 = e.getKey();
			if (k1 == k2 || (k1 != null && k1.equals(k2))) {
				Object v1 = getValue();
				Object v2 = e.getValue();
				if (v1 == v2 || (v1 != null && v1.equals(v2)))
					return true;
			}
			return false;
		}

		public final int hashCode() {
			return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
		}

		public final String toString() {
			return getKey() + "=" + getValue();
		}

		@Override
		public Entry<K, V> clone() {
			try {
				return (Entry<K, V>) super.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}

	}

	Entry<K, V> addEntry(int hash, K key, V value, int bucketIndex) {
		Entry<K, V> e = table[bucketIndex];
		Entry<K, V> r = table[bucketIndex] = newEntry(hash, key, value, e);
		if (size++ >= threshold)
			resize(2 * table.length);
		onKeysChanged();
		return r;
	}

	private void onKeysChanged() {
		if (keysSorted != null)
			keysSorted.clear();
	}

	public List<K> getKeysSorted() {
		if (keysSorted == null)
			keysSorted = new ArrayList<K>(size);
		if (size > 0 && keysSorted.isEmpty()) {
			keysSorted.addAll(this.keySet());
			Collections.sort((List) keysSorted, (Comparator) ComparableComparator.INSTANCE);
		}

		return keysSorted;
	}
	void addEntry(Entry<K, V> entry, int bucketIndex) {
		entry.next = table[bucketIndex];
		table[bucketIndex] = entry;
		if (size++ >= threshold)
			resize(2 * table.length);
		onKeysChanged();
	}

	protected HasherMap.Entry<K, V> newEntry(int hash, K key, V value, Entry<K, V> next) {
		return new Entry<K, V>(hash, key, value, next);
	}

	void createEntry(int hash, K key, V value, int bucketIndex) {
		Entry<K, V> e = table[bucketIndex];
		table[bucketIndex] = newEntry(hash, key, value, e);
		size++;
		onKeysChanged();
	}

	private abstract class HashIterator<E> implements Iterator<E> {
		Entry<K, V> next, current;
		int expectedModCount, index;

		HashIterator() {
			expectedModCount = modCount;
			if (size > 0) { // advance to first entry
				Entry[] t = table;
				while (index < t.length && (next = t[index++]) == null)
					;
			}
		}

		public final boolean hasNext() {
			return next != null;
		}

		final Entry<K, V> nextEntry() {
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
			Entry<K, V> e = next;
			if (e == null)
				throw new NoSuchElementException();

			if ((next = e.next) == null) {
				Entry[] t = table;
				while (index < t.length && (next = t[index++]) == null)
					;
			}
			current = e;
			return e;
		}

		public void remove() {
			if (current == null)
				throw new IllegalStateException();
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
			Object k = current.key;
			current = null;
			HasherMap.this.removeEntryForKey(k);
			expectedModCount = modCount;
		}

	}

	private final class ValueIterator extends HashIterator<V> {
		public V next() {
			return nextEntry().value;
		}
	}

	private final class KeyIterator extends HashIterator<K> {
		public K next() {
			return nextEntry().getKey();
		}
	}

	private final class EntryIterator extends HashIterator<Entry<K, V>> {
		public Entry<K, V> next() {
			return nextEntry();
		}
	}

	Iterator<K> newKeyIterator() {
		return new KeyIterator();
	}

	Iterator<V> newValueIterator() {
		return new ValueIterator();
	}

	Iterator<Entry<K, V>> newEntryIterator() {
		return new EntryIterator();
	}

	private Set<Entry<K, V>> entrySet = null;

	private Set<K> keySet;

	private Values<V> values;

	private Hasher hasher = BasicHasher.INSTANCE;

	public Set<K> keySet() {
		Set<K> ks = keySet;
		return (ks != null ? ks : (keySet = new KeySet()));
	}

	private final class KeySet extends AbstractSet<K> {
		public Iterator<K> iterator() {
			return newKeyIterator();
		}

		public int size() {
			return size;
		}

		public boolean contains(Object o) {
			return containsKey(o);
		}

		public boolean remove(Object o) {
			return HasherMap.this.removeEntryForKey(o) != null;
		}

		public void clear() {
			HasherMap.this.clear();
		}
	}

	public Values<V> values() {
		Values<V> vs = values;
		return (vs != null ? vs : (values = new Values()));
	}

	public final class Values<T> extends AbstractCollection<V> implements IterableAndSize<V> {
		public Iterator<V> iterator() {
			return newValueIterator();
		}

		public int size() {
			return size;
		}

		public boolean contains(Object o) {
			return containsValue(o);
		}

		public void clear() {
			HasherMap.this.clear();
		}
	}

	public Set<Map.Entry<K, V>> entrySet() {
		return (Set) entrySet0();
	}

	private Set<Entry<K, V>> entrySet0() {
		Set<Entry<K, V>> es = entrySet;
		return es != null ? es : (entrySet = new EntrySet());
	}

	private final class EntrySet extends AbstractSet<Entry<K, V>> {
		public Iterator<Entry<K, V>> iterator() {
			return newEntryIterator();
		}

		public boolean contains(Object o) {
			if (!(o instanceof Map.Entry))
				return false;
			Map.Entry<K, V> e = (Map.Entry<K, V>) o;
			Entry<K, V> candidate = getEntry(e.getKey());
			return candidate != null && candidate.equals(e);
		}

		public boolean remove(Object o) {
			return removeMapping(o) != null;
		}

		public int size() {
			return size;
		}

		public void clear() {
			HasherMap.this.clear();
		}
	}

	int capacity() {
		return table.length;
	}

	float loadFactor() {
		return loadFactor;
	}

	public void setHasher(Hasher hasher) {
		if (!isEmpty())
			throw new RuntimeException("can not set hasher with element in map");
		this.hasher = hasher;

	}

	@Override
	public Iterator<K> iterator() {
		return keySet().iterator();
	}

	public void putAll(Mapping<K, V> values) {
		for (MappingEntry<K, V> e : values.entries())
			put(e.getKey(), e.getValue());
	}

	@Override
	public Iterable<MappingEntry<K, V>> entries() {
		return (Iterable) this.entrySet();
	}
	@Override
	public StringBuilder toString(StringBuilder sb) {
		if (isEmpty()) {
			return sb.append("{}");
		}
		Iterator<Map.Entry<K, V>> i = entrySet().iterator();
		sb.append('{');
		for (;;) {
			Map.Entry<K, V> e = i.next();
			K key = e.getKey();
			V value = e.getValue();
			sb.append(key == this ? "(this Map)" : key);
			sb.append('=');
			sb.append(value == this ? "(this Map)" : value);
			if (!i.hasNext())
				return sb.append('}');
			sb.append(',').append(' ');
		}
	}

	public Hasher<K> getHasher() {
		return this.hasher;
	}

	public boolean isNull(K key) {
		if (key == null) {
			for (Entry<K, V> e = table[0]; e != null; e = e.next) {
				if (e.key == null)
					return e.value == null;
			}
			return false;
		}
		int hash = hash(hasher.hashcode(key));
		for (Entry<K, V> e = table[indexFor(hash, table.length)]; e != null; e = e.next) {
			if (e.hash == hash && hasher.areEqual(key, e.key))
				return e.value == null;
		}
		return false;
	}
}
