package com.f1.utils.concurrent;

import java.util.Map;

public interface MapFactory<K, V> {

	public Map<K, V> newMap();
	public Map<K, V> newMap(Map<K, V> m);

}
