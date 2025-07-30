package com.f1.utils.structs;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

public class MapEntryKeyComparator<K, V> implements Comparator<Map.Entry<K, V>> {

	final private Comparator<K> inner;

	public MapEntryKeyComparator() {
		inner = (Comparator<K>) ComparableComparator.INSTANCE;
	}
	public MapEntryKeyComparator(Comparator<K> inner) {
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
		return inner.compare(o1.getKey(), o2.getKey());
	}

}
