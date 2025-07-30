package com.f1.base;

import java.util.Map;

/**
 * 
 * Similar to {@link Map.Entry} but for {@link Mapping}
 */
public interface MappingEntry<K, V> {

	/**
	 * 
	 * @return the key for this entry
	 */
	public K getKey();

	/**
	 * @return the value for this entry.
	 */
	public V getValue();

}
