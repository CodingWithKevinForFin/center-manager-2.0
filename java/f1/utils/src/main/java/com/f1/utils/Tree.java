package com.f1.utils;

import java.util.Map;

public interface Tree<K, V> {

	public Map<K, V> getLeafs();

	public Map<K, Tree<K, V>> getTrees();

}
