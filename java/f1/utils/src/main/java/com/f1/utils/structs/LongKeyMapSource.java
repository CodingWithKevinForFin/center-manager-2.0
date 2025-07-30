package com.f1.utils.structs;

import java.util.Map;

import com.f1.base.IterableAndSize;
import com.f1.base.LongIterator;
import com.f1.base.ToStringable;
import com.f1.utils.structs.LongKeyMap.Node;

public interface LongKeyMapSource<V> extends IterableAndSize<LongKeyMap.Node<V>>, ToStringable {

	Node<V> getNode(long key);

	V get(long key);

	int size();

	LongIterator keyIterator();

	Iterable<V> values();

	Map<Long, V> fill(Map<Long, V> sink);

	boolean containsKey(long key);

	V getOrThrow(long key);

}
