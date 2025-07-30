package com.f1.utils.structs;

import java.util.Comparator;

import com.f1.utils.OH;

public class ComparableComparator<T extends Comparable<T>> implements Comparator<T> {
	public static final ComparableComparator<?> INSTANCE = new ComparableComparator();

	@Override
	public int compare(T l, T r) {
		return OH.compare(l, r);
	}

	public static <T> Comparator<T> instance(Class<T> class1) {
		return (Comparator<T>) INSTANCE;
	}

}
