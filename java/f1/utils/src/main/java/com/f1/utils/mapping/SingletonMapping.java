package com.f1.utils.mapping;

import java.util.Iterator;

import com.f1.base.Mapping;
import com.f1.base.MappingEntry;
import com.f1.utils.OH;
import com.f1.utils.SingletonIterable;
import com.f1.utils.SingletonIterator;

public class SingletonMapping<K, V> implements Mapping<K, V>, MappingEntry<K, V> {

	private final K key;
	private final V value;

	public SingletonMapping(K key, V value) {
		this.key = key;
		this.value = value;
	}
	@Override
	public V get(K key) {
		return containsKey(key) ? value : null;
	}
	@Override
	public Iterator<K> iterator() {
		return new SingletonIterator<K>(key);
	}
	@Override
	public int size() {
		return 1;
	}
	@Override
	public boolean containsKey(K key) {
		return OH.eq(this.key, key);
	}
	@Override
	public Iterable<MappingEntry<K, V>> entries() {
		return new SingletonIterable<MappingEntry<K, V>>(this);
	}
	@Override
	public K getKey() {
		return key;
	}
	@Override
	public V getValue() {
		return value;
	}
	public static <K, V> SingletonMapping<K, V> instance(K k, V v) {
		return new SingletonMapping<K, V>(k, v);
	}

}
