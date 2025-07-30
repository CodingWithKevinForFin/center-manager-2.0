/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.base.IterableAndSize;
import com.f1.base.ToStringable;
import com.f1.utils.CH;
import com.f1.utils.Hasher;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.structs.MapEntryValuesIterator.IterableCollection;

public class BasicIndexedList<K, V> implements IndexedList<K, V>, ToStringable {

	final private List<Map.Entry<K, V>> entries;
	final private HasherMap<K, Integer> valueIndexes;
	private IterableCollection<V> values = null;
	private boolean hasChanged = false;
	private ValueList valueList = null;
	private KeyValuesMap keyValuesMap = null;
	private KeySet keyset = null;
	private EntrySet entrySet = null;

	public BasicIndexedList() {
		entries = new ArrayList<Map.Entry<K, V>>();
		valueIndexes = new HasherMap<K, Integer>();
	}
	public BasicIndexedList(Hasher<K> hasher) {
		entries = new ArrayList<Map.Entry<K, V>>();
		valueIndexes = new HasherMap<K, Integer>(hasher);
	}
	public BasicIndexedList(BasicIndexedList<K, V> other) {
		entries = new ArrayList<Map.Entry<K, V>>(other.entries.size());
		for (int i = 0; i < other.entries.size(); i++)
			entries.add(new MapEntry(other.entries.get(i)));
		valueIndexes = new HasherMap<K, Integer>(other.valueIndexes.getHasher(), other.valueIndexes);
	}

	@Override
	public void add(K key, V value, int location) {
		OH.assertBetween(location, 0, entries.size());
		CH.putOrThrow(valueIndexes, key, location);
		entries.add(location, new MapEntry<K, V>(key, value));
		rebuildStartAt(location + 1);
		hasChanged = true;
	}

	@Override
	public void add(K key, V value) {
		add(key, value, entries.size());
	}

	@Override
	public V remove(K key) {
		int location = CH.removeOrThrow(valueIndexes, key);
		V r = entries.remove(location).getValue();
		rebuildStartAt(location);
		hasChanged = true;
		return r;
	}
	@Override
	public V removeNoThrow(K key) {
		Integer location = valueIndexes.remove(key);
		if (location == null)
			return null;
		V r = entries.remove((int) location).getValue();
		rebuildStartAt(location);
		hasChanged = true;
		return r;
	}

	@Override
	public V getAt(int i) {
		return entries.get(i).getValue();
	}
	@Override
	public V update(K key, V value) {
		Integer loc = CH.getOrThrow(this.valueIndexes, key);
		return entries.get(loc).setValue(value);
	}
	@Override
	public V update(K key, K newKey, V value) {
		if (OH.eq(key, newKey))
			return update(key, value);
		if (valueIndexes.containsKey(newKey))
			throw new RuntimeException("NewKey already exists: " + newKey);
		Integer loc = CH.removeOrThrow(this.valueIndexes, key);
		this.valueIndexes.put(newKey, loc);
		return entries.get(loc).setValue(value);
	}

	@Override
	public V get(K key) {
		return getAt(getPosition(key));
	}
	@Override
	public V getNoThrow(K key) {
		final int position = getPositionNoThrow(key);
		return position == -1 ? null : getAt(position);
	}

	@Override
	public int getSize() {
		return entries.size();
	}

	@Override
	public int getPosition(K key) {
		return CH.getOrThrow(valueIndexes, key);
	}

	@Override
	public boolean getHasChanged() {
		return hasChanged;
	}

	@Override
	public void resetHasChanged() {
		this.hasChanged = false;
	}

	@Override
	public V removeAt(int i) {
		Entry<K, V> e = entries.remove(i);
		CH.removeOrThrow(valueIndexes, e.getKey());
		rebuildStartAt(i);
		hasChanged = true;
		return e.getValue();
	}

	private void rebuildStartAt(int location) {
		for (int i = location, l = getSize(); i < l; i++) {
			valueIndexes.put(entries.get(i).getKey(), i);
		}
	}

	@Override
	public boolean containsKey(K key) {
		return valueIndexes.containsKey(key);
	}

	@Override
	public String toString() {
		return SH.s(entries).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		return SH.s(entries, sb);
	}

	@Override
	public Iterator<Entry<K, V>> iterator() {
		return entries.iterator();
	}

	@Override
	public Set<K> keySet() {
		if (keyset == null)
			keyset = new KeySet();
		return keyset;
	}
	@Override
	public Iterator<K> keys() {
		return new KeyIterator();
	}

	@Override
	public IterableAndSize<V> values() {
		if (values == null)
			values = new MapEntryValuesIterator.IterableCollection<V>(entries);
		return values;
	}
	@Override
	public void sortByKeys(Comparator<K> comparator) {
		Collections.sort(this.entries, new MapEntryKeyComparator<K, V>(comparator));
		hasChanged = true;
		rebuildStartAt(0);
	}

	@Override
	public void sortByValues(Comparator<V> comparator) {
		Collections.sort(this.entries, new MapEntryValueComparator<K, V>(comparator));
		hasChanged = true;
		rebuildStartAt(0);
	}

	@Override
	public void clear() {
		if (this.entries.size() == 0)
			return;
		this.entries.clear();
		this.valueIndexes.clear();
		this.hasChanged = true;
	}

	@Override
	public int getPositionNoThrow(K key) {
		Integer r = valueIndexes.get(key);
		return r == null ? -1 : r.intValue();
	}

	@Override
	public Entry<K, V> getEntryAt(int i) {
		return entries.get(i);
	}
	@Override
	public K getKeyAt(int i) {
		return entries.get(i).getKey();
	}

	private class KeySet implements Set<K> {

		@Override
		public int size() {
			return BasicIndexedList.this.getSize();
		}

		@Override
		public boolean isEmpty() {
			return BasicIndexedList.this.getSize() == 0;
		}

		@Override
		public boolean contains(Object o) {
			return BasicIndexedList.this.valueIndexes.containsKey(o);
		}

		@Override
		public Iterator<K> iterator() {
			return new KeyIterator();
		}

		@Override
		public Object[] toArray() {
			Object[] r = new Object[entries.size()];
			for (int i = 0; i < r.length; i++)
				r[i] = entries.get(i).getKey();
			return r;
		}

		@Override
		public <T> T[] toArray(T[] r) {
			if (r.length < entries.size())
				r = (T[]) Array.newInstance(r.getClass().getComponentType(), r.length);
			for (int i = 0; i < r.length; i++)
				r[i] = (T) entries.get(i).getKey();
			return r;
		}

		@Override
		public boolean add(K e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object i : c)
				if (!BasicIndexedList.this.valueIndexes.containsKey(c))
					return false;
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends K> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String toString() {
			return SH.join(",", this);
		}

	}

	public class KeyIterator implements Iterator<K> {

		private Iterator<Entry<K, V>> inner;

		KeyIterator() {
			this.inner = entries.iterator();
		}
		@Override
		public boolean hasNext() {
			return inner.hasNext();
		}

		@Override
		public K next() {
			return inner.next().getKey();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public void addAll(BasicIndexedList<K, V> types) {
		for (Entry<K, V> i : types.entries)
			this.add(i.getKey(), i.getValue());
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BasicIndexedList))
			return false;
		BasicIndexedList o = (BasicIndexedList) obj;
		if (o.getSize() != getSize())
			return false;
		for (int i = 0; i < getSize(); i++)
			if (OH.ne(getKeyAt(i), o.getKeyAt(i)) || OH.ne(getAt(i), o.getAt(i)))
				return false;
		return true;
	}

	@Override
	public List<V> valueList() {
		if (this.valueList == null)
			this.valueList = new ValueList();
		return this.valueList;

	}

	@Override
	public Map<K, V> map() {
		if (this.keyValuesMap == null)
			this.keyValuesMap = new KeyValuesMap();
		return keyValuesMap;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		if (this.entrySet == null)
			this.entrySet = new EntrySet();
		return this.entrySet;
	}

	private class KeyValuesMap implements Map<K, V> {

		@Override
		public int size() {
			return valueIndexes.size();
		}

		@Override
		public boolean isEmpty() {
			return valueIndexes.isEmpty();
		}

		@Override
		public boolean containsKey(Object key) {
			return valueIndexes.containsKey(key);
		}

		@Override
		public boolean containsValue(Object value) {
			return values().contains(value);
		}

		@Override
		public V get(Object key) {
			Integer i = valueIndexes.get(key);
			return i == null ? null : entries.get(i).getValue();
		}

		@Override
		public V put(K key, V value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public V remove(Object key) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void putAll(Map<? extends K, ? extends V> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<K> keySet() {
			return BasicIndexedList.this.keySet();
		}

		@Override
		public Collection<V> values() {
			return values();
		}

		@Override
		public Set<Entry<K, V>> entrySet() {
			return entrySet();
		}

	}

	private class ValueList extends AbstractList<V> {

		@Override
		public V get(int index) {
			return entries.get(index).getValue();
		}

		@Override
		public int size() {
			return entries.size();
		}

		@Override
		public boolean add(V e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public V set(int index, V element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Iterator<V> iterator() {
			return values().iterator();
		}

		@Override
		public boolean contains(Object o) {
			for (int i = 0, n = entries.size(); i < n; i++)
				if (OH.eq(o, get(i)))
					return true;
			return false;
		}

	}

	private class EntrySet implements Set<Entry<K, V>> {

		@Override
		public int size() {
			return entries.size();
		}

		@Override
		public boolean isEmpty() {
			return entries.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return entries.contains(o);
		}

		@Override
		public Iterator<Entry<K, V>> iterator() {
			return entries.iterator();
		}

		@Override
		public Object[] toArray() {
			return entries.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return entries.toArray(a);
		}

		@Override
		public boolean add(Entry<K, V> e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return entries.containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends Entry<K, V>> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

	}

}
