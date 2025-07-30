package com.f1.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.base.IntIterator;
import com.f1.utils.mutable.Mutable;
import com.f1.utils.mutable.Mutable.Int;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.IntKeyMap.Node;

public class IntValueMap<T> {

	final private Map<T, Mutable.Int> counts = new HashMap<T, Mutable.Int>();

	/**
	 * increments value associated key by supplied value amount. If key didn't exist, creates ad defaults to zero (then increments by value)
	 * 
	 * @param key
	 * @param value
	 * @return the value after adding
	 */
	public int add(T key, int value) {
		Int i = counts.get(key);
		if (i == null)
			counts.put(key, i = new Mutable.Int(value));
		else
			i.value += value;
		return i.value;
	}
	public int getValue(T data) {
		Int r = counts.get(data);
		return r == null ? 0 : r.value;
	}
	public void setValue(T data, int value) {
		Int r = counts.get(data);
		if (r == null)
			counts.put(data, r = new Int(value));
		else
			r.value = value;
	}

	public void clear() {
		this.counts.clear();
	}

	public Set<T> getKeys() {
		return this.counts.keySet();
	}

	@Override
	public String toString() {
		return counts.toString();
	}

	public List<T> getKeysSortedByCount() {
		return getKeysSortedByCount(null, true);
	}
	public List<T> getKeysSortedByCount(boolean minFirst) {
		return getKeysSortedByCount(null, minFirst);
	}
	public List<T> getKeysSortedByCount(Comparator<? super T> keyComparator) {
		return this.getKeysSortedByCount(keyComparator, true);
	}
	public List<T> getKeysSortedByCount(Comparator<? super T> keyComparator, boolean minFirst) {
		IntKeyMap<List<T>> byCount = new IntKeyMap<List<T>>();
		for (Entry<T, Int> i : counts.entrySet()) {
			Node<List<T>> node = byCount.getNodeOrCreate(i.getValue().value);
			List<T> list = node.getValue();
			if (list == null)
				node.setValue(list = new ArrayList<T>());
			list.add(i.getKey());
		}
		int[] nums = new int[byCount.size()];
		int j = 0;
		for (IntIterator it = byCount.keyIterator(); it.hasNext();)
			nums[j++] = it.nextInt();
		Arrays.sort(nums);
		if (!minFirst)
			AH.reverse(nums);
		final List<T> r = new ArrayList<T>(counts.size());
		for (int num : nums) {
			List<T> l = byCount.get(num);
			if (keyComparator != null)
				Collections.sort(l, keyComparator);
			r.addAll(l);
		}
		return r;
	}
	public int size() {
		return this.counts.size();
	}

}
