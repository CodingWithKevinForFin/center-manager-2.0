/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table.impl;

import java.util.List;
import java.util.Map;

import com.f1.utils.CH;
import com.f1.utils.OH;

public class WebColumnManager<K, V> {

	private List<V> values;
	private Map<K, Integer> valueIndexes;
	private boolean hasChanged;

	public void insert(K key, V value, int location) {
		OH.assertBetween(location, 0, values.size());
		CH.putOrThrow(valueIndexes, key, location);
		values.add(location, value);
		hasChanged = true;
	}

	public void add(K key, V value) {
		insert(key, value, values.size());
	}

	public void remove(K key) {
		int location = CH.removeOrThrow(valueIndexes, key);
		values.remove(location);
		hasChanged = true;
	}

	public V getValueAt(int i) {
		return values.get(i);
	}

	public V getValue(K key) {
		return getValueAt(getIndex(key));
	}
	public int getSize() {
		return values.size();
	}

	public int getIndex(K key) {
		return CH.getOrThrow(valueIndexes, key);
	}

	public boolean getHasChanged() {
		return hasChanged;
	}

	public void resetHasChanged() {
		this.hasChanged = false;
	}

}
