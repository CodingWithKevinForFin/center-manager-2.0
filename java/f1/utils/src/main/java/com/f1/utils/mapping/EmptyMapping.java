package com.f1.utils.mapping;

import java.util.Iterator;

import com.f1.base.Mapping;
import com.f1.base.MappingEntry;
import com.f1.utils.EmptyIterable;
import com.f1.utils.EmptyIterator;

public class EmptyMapping<K, V> implements Mapping<K, V> {

	public static final EmptyMapping INSTANCE = new EmptyMapping();
	public static final <K, V> EmptyMapping<K, V> instance() {
		return INSTANCE;
	}

	private EmptyMapping() {
	};
	@Override
	public V get(K key) {
		return null;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Iterator<K> iterator() {
		return EmptyIterator.INSTANCE;
	}
	@Override
	public boolean containsKey(K key) {
		return false;
	}

	@Override
	public Iterable<MappingEntry<K, V>> entries() {
		return EmptyIterable.INSTANCE;
	}

	public static <K, V> Mapping<K, V> noNull(Mapping<K, V> i) {
		return i == null ? INSTANCE : i;
	}

}
