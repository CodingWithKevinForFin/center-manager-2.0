package com.f1.utils.structs;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.f1.utils.CH;
import com.f1.utils.OH;

public class MapByKeyComparator<K> implements Comparator<Map<K, ? extends Object>> {

	private final K key;

	public MapByKeyComparator(K key) {
		this.key = key;
	}

	@Override
	public int compare(Map<K, ? extends Object> o1, Map<K, ? extends Object> o2) {
		if (o1 == o2)
			return 0;
		if (o1 == null)
			return -1;
		if (o2 == null)
			return 1;
		return OH.compare((Comparable) o1.get(key), (Comparable) o2.get(key));
	}

	public static <K> void sort(List<Map<K, Object>> values, K key) {
		if (CH.isEmpty(values))
			return;
		MapByKeyComparator<K> comp = new MapByKeyComparator<K>(key);
		Collections.sort(values, comp);
	}

}
