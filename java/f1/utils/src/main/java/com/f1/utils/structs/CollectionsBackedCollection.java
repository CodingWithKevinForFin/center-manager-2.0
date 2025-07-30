package com.f1.utils.structs;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

import com.f1.utils.IterableIterator;

public class CollectionsBackedCollection<V> implements Collection<V> {

	private Collection<V>[] collections;

	public CollectionsBackedCollection(Collection<V>[] collections) {
		this.collections = collections;
	}
	@Override
	public int size() {
		int r = 0;
		for (Collection<V> collection : collections)
			r += collection.size();
		return r;
	}

	@Override
	public boolean isEmpty() {
		for (Collection<V> collection : collections)
			if (!collection.isEmpty())
				return false;
		return true;
	}

	@Override
	public boolean contains(Object o) {
		for (Collection<V> collection : collections)
			if (collection.contains(o))
				return true;
		return false;
	}

	@Override
	public Iterator<V> iterator() {
		return IterableIterator.create(collections).iterator();
	}

	@Override
	public Object[] toArray() {
		Object[] r = new Object[size()];
		int pos = 0;
		for (Collection<V> set : collections)
			for (V e : set)
				r[pos++] = e;
		return r;
	}

	@Override
	public <T> T[] toArray(T[] r) {
		int size = size();
		if (r.length < size)
			r = (T[]) Array.newInstance(r.getClass().getComponentType(), size);
		int pos = 0;
		for (Collection<V> set : collections)
			for (V e : set)
				r[pos++] = (T) e;
		return r;
	}

	@Override
	public boolean add(V e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c)
			if (!contains(o))
				return false;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends V> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}
	public int getCollectionsCount() {
		return this.collections.length;
	}
	public Collection<V> getCollection(int i) {
		return this.collections[i];
	}

	public void replaceCollection(int i, Collection<V> nuw) {
		this.collections[i] = nuw;
	}

}
