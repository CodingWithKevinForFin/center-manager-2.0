package com.f1.base;

/**
 * 
 * a read-only version of a map, which a highly reduced interface (easier to implement)
 * 
 * @param <K>
 *            keys of the map
 * @param <V>
 *            values of the map
 */
public interface Mapping<K, V> extends Getter<K, V>, IterableAndSize<K> {

	/**
	 * Does this map contain a key
	 */
	public boolean containsKey(K key);

	/**
	 * @return an iterator of all key-value pairs for this map.
	 * 
	 *         IMPORTANT: for performance reasons the MappingEntry is no longer valid after you've iterated "away" from it. Some implementaions use the same entry as you itereate
	 *         and just update valus.
	 */
	public Iterable<MappingEntry<K, V>> entries();
}
