/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.base.IterableAndSize;

public interface IndexedList<K, V> extends Iterable<Map.Entry<K, V>> {

	public void add(K key, V value, int location);

	public void add(K key, V value);

	public V update(K key, V value);
	public V update(K key, K newKey, V value);

	public V remove(K key);

	public V get(K key);

	public V getAt(int i);

	public V removeAt(int i);

	public int getPosition(K key);

	public int getPositionNoThrow(K key);

	public boolean getHasChanged();

	public void resetHasChanged();

	public int getSize();

	public boolean containsKey(K key);

	V removeNoThrow(K key);

	public Set<K> keySet();
	public List<V> valueList();
	public Set<Entry<K, V>> entrySet();
	public Map<K, V> map();

	public IterableAndSize<V> values();

	void sortByKeys(Comparator<K> comparator);

	void sortByValues(Comparator<V> comparator);

	public void clear();

	public V getNoThrow(K key);

	public K getKeyAt(int i);

	public Entry<K, V> getEntryAt(int i);

	public Iterator<K> keys();
}
