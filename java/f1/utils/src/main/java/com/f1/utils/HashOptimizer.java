/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.MultiMap;

public class HashOptimizer<K, V> {

	private final Set<Integer> hashcodes = new HashSet<Integer>();
	private final Map<K, V> items = new HashMap<K, V>();
	private final MultiMap<Integer, Map.Entry<K, V>, List<Map.Entry<K, V>>> values = new BasicMultiMap.List<Integer, Map.Entry<K, V>>(new TreeMap());
	private int hash = -1;
	private String variableName;

	public void addElement(K key, V value) {
		CH.putOrThrow(items, key, value);
		hashcodes.add(MH.abs(key.hashCode()));
	}

	public void optimize() {
		int size = hashcodes.size();
		int bestCollisions = size;
		int bestHash = size;
		for (int r = size; r < size * 10 && bestCollisions > 0; r++) {
			boolean exists[] = new boolean[r];
			int collissions = 0;
			for (int i : hashcodes) {
				if (exists[i % r])
					collissions++;
				else
					exists[i % r] = true;
			}
			if (collissions < bestCollisions) {
				bestCollisions = collissions;
				bestHash = r;
			}
		}
		hash = bestHash;
		values.clear();
		for (Map.Entry<K, V> e : items.entrySet())
			values.putMulti(MH.abs(e.getKey().hashCode()) % hash, e);
		if (hash == 0)
			hash = 1;// avoid % 0
	}

	public Iterable<Map.Entry<Integer, List<Map.Entry<K, V>>>> getHashEntries() {
		if (hash == -1)
			optimize();
		return values.entrySet();

	}

	public int getHash() {
		if (hash == -1)
			optimize();
		return hash;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public String getHashExpression() {
		if (getHash() == 0)
			return "1";
		return "Math.abs(" + variableName + ".hashCode()) % " + getHash();
	}

	public boolean getHasEntries() {
		if (hash == -1)
			optimize();
		return hashcodes.size() > 0;
	}

	public Map<K, V> getItems() {
		return items;
	}
}
