package com.f1.utils.impl;

import java.util.NavigableMap;
import java.util.TreeMap;
import com.f1.utils.Distribution;
import com.f1.utils.OH;

public class BasicDistribution<T extends Comparable<?>> implements Distribution<T> {

	private NavigableMap<T, Long> counts = new TreeMap<T, Long>();

	@Override
	public Long add(T key) {
		Long count = counts.get(key);
		if (count == null)
			count = 1L;
		else
			count++;
		counts.put(key, count);
		return count;
	}

	@Override
	public void addAll(Iterable<T> keys) {
		for (T key : keys)
			add(key);

	}

	@Override
	public Long getCount(T key) {
		return OH.noNull(counts.get(key), 0L);
	}

	@Override
	public NavigableMap<T, Long> getCounts() {
		return counts;
	}

}
