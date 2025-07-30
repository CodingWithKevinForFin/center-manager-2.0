package com.f1.utils.structs;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class SetsBackedSet<V> implements Set<V> {

	private Set<V>[] sets;
	private boolean suppressDups;

	public SetsBackedSet(boolean suppressDups, Set<V>... sets) {
		this.sets = sets;
		this.suppressDups = suppressDups;
	}

	@Override
	public int size() {
		int r = 0;
		if (suppressDups)
			for (V k : this)
				r++;
		else
			for (Set<V> set : sets)
				r += set.size();
		return r;
	}
	@Override
	public boolean isEmpty() {
		for (Set<V> set : sets)
			if (!set.isEmpty())
				return false;
		return true;
	}

	@Override
	public boolean contains(Object o) {
		for (Set<V> set : sets)
			if (set.contains(o))
				return true;
		return false;
	}

	@Override
	public Iterator<V> iterator() {
		return new Iter();
	}
	@Override
	public Object[] toArray() {
		Object[] r = new Object[size()];
		int pos = 0;
		for (V e : this)
			r[pos++] = e;
		return r;
	}

	@Override
	public <T> T[] toArray(T[] r) {
		int size = size();
		if (r.length < size)
			r = (T[]) Array.newInstance(r.getClass().getComponentType(), size);
		int pos = 0;
		for (V e : this)
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
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	public int getSetsCount() {
		return this.sets.length;
	}
	public Set<V> getSet(int i) {
		return this.sets[i];
	}

	public void replaceSet(int i, Set<V> nuw) {
		this.sets[i] = nuw;
	}

	public class Iter implements Iterator<V> {
		private Iterator<V> currentIterator;
		private int iteratorsPos = 0;
		private V next;
		private boolean hasNext;

		public Iter() {
			if (sets.length > 0) {
				currentIterator = sets[0].iterator();
				walkIterator();
				hasNext = true;
			}
		}

		@Override
		public V next() {
			if (next == null)
				throw new NoSuchElementException();
			V r = next;
			walkIterator();
			return r;
		}

		private void walkIterator() {
			for (;;) {
				while (currentIterator.hasNext()) {
					next = currentIterator.next();
					if (!alreadyHit(next))
						return;
				}
				iteratorsPos++;
				if (iteratorsPos == sets.length)
					break;
				currentIterator = sets[iteratorsPos].iterator();
			}
			currentIterator = null;
			next = null;
			hasNext = false;
		}
		private boolean alreadyHit(V k) {
			if (suppressDups)
				for (int i = 0; i < iteratorsPos; i++)
					if (sets[i].contains(k))
						return true;
			return false;
		}

		@Override
		public boolean hasNext() {
			return hasNext;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("can not remove");
		}

	}

}
