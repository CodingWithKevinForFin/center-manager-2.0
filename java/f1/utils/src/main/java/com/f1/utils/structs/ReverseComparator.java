package com.f1.utils.structs;

import java.util.Comparator;

public class ReverseComparator<T extends Comparable<T>> implements Comparator<T> {

	public static final ReverseComparator<?> INSTANCE = new ReverseComparator(ComparableComparator.INSTANCE);

	final private Comparator<T> inner;

	public ReverseComparator(Comparator<T> inner_) {
		super();
		inner = inner_;
	}

	@Override
	public int compare(T l, T r) {
		return -inner.compare(l, r);
	}

	static public <C extends Comparable<C>> ReverseComparator<C> instance(Class<C> type) {
		return (ReverseComparator<C>) INSTANCE;
	}

}

