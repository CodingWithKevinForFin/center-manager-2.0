package com.f1.utils.concurrent;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MapBackedSet<K> implements Set<K>, Serializable {

	final private Map<K, K> inner;

	static public <K> MapBackedSet<K> wrap(Map<K, K> inner) {
		return new MapBackedSet<K>(inner);
	}

	static public <K> MapBackedSet<K> wrap(Map<K, K> inner, Iterable<K> values) {
		MapBackedSet<K> r = wrap(inner);
		for (K value : values)
			r.add(value);
		return r;
	}

	public MapBackedSet(Map<K, K> inner) {
		this.inner = inner;
	}
	public MapBackedSet(Map<K, K> inner, Collection<K> values) {
		this.inner = inner;
		for (K value : values)
			this.inner.put(value, value);
	}

	public Map<K, ?> getInner() {
		return inner;
	}

	@Override
	public int size() {
		return inner.size();
	}

	@Override
	public boolean isEmpty() {
		return inner.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return inner.containsKey(o);
	}

	@Override
	public Iterator<K> iterator() {
		return inner.keySet().iterator();
	}

	@Override
	public Object[] toArray() {
		return inner.keySet().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return inner.keySet().toArray(a);
	}

	@Override
	public boolean add(K e) {
		if (inner.containsKey(e))
			return false;
		inner.put(e, e);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		if (!inner.containsKey(o))
			return false;
		inner.remove(o);
		return true;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return inner.keySet().containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends K> c) {
		boolean r = false;
		for (K i : c)
			r |= add(i);
		return r;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		Collection<K> toRemove = new HashSet<K>(inner.keySet());
		toRemove.removeAll(c);
		return removeAll(toRemove);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean r = false;
		for (Object i : c)
			r |= remove(i);
		return r;
	}

	@Override
	public void clear() {
		inner.clear();
	}

	@Override
	public String toString() {
		return inner == null ? "<no inner map>" : inner.toString();
	}

}
