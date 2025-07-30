package com.f1.utils;

import java.util.NavigableMap;

public interface Distribution<T extends Comparable<?>> {

	public Long add(T type);

	public void addAll(Iterable<T> values);

	public Long getCount(T key);

	public NavigableMap<T, Long> getCounts();
}
