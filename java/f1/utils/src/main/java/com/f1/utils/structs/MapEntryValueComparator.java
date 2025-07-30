package com.f1.utils.structs;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

public class MapEntryValueComparator<K, V> implements Comparator<Map.Entry<K, V>> {

	final private Comparator<V> inner;

	public MapEntryValueComparator() {
		inner = (Comparator<V>) ComparableComparator.INSTANCE;
	}
	public MapEntryValueComparator(Comparator<V> inner) {
		this.inner = inner;
	}

	@Override
	public int compare(Entry<K, V> o1, Entry<K, V> o2) {
		if (o1 == o2)
			return 0;
		else if (o1 == null)
			return -1;
		else if (o2 == null)
			return 1;
		return inner.compare(o1.getValue(), o2.getValue());
	}

}
