/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListWrapper<T> implements List<T> {

	private class WrapperIterator implements Iterator<T> {
		int pos = 0;
		int lastReturn = -1;

		public boolean hasNext() {
			return pos != size();
		}

		public T next() {
			int i = pos;
			T next = get(i);
			lastReturn = i;
			pos = i + 1;
			return next;
		}

		public void remove() {
			if (lastReturn < 0)
				throw new IllegalStateException();
			ListWrapper.this.remove(lastReturn);
			if (lastReturn < pos)
				pos--;
			lastReturn = -1;
		}

	}

	public class ListWrapperIterator extends WrapperIterator implements ListIterator<T> {

		public ListWrapperIterator(int i) {
			this.pos = i;
		}

		public boolean hasPrevious() {
			return pos != 0;
		}

		public T previous() {
			int i = pos - 1;
			T previous = get(i);
			lastReturn = pos = i;
			return previous;
		}

		public int nextIndex() {
			return pos;
		}

		public int previousIndex() {
			return pos - 1;
		}

		public void set(T e) {
			if (lastReturn < 0)
				throw new IllegalStateException();
			ListWrapper.this.set(lastReturn, e);
		}

		public void add(T e) {
			int i = pos;
			ListWrapper.this.add(i, e);
			lastReturn = -1;
			pos = i + 1;
		}

	}

	final private List<T> inner;

	public ListWrapper(List<T> inner) {
		this.inner = inner;
	}

	public List<T> getInner() {
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
		return inner.contains(o);
	}

	@Override
	public Iterator<T> iterator() {
		return new WrapperIterator();
	}

	@Override
	public Object[] toArray() {
		return inner.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return inner.toArray(a);
	}

	@Override
	public boolean add(T e) {
		return inner.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return inner.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return inner.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return inner.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return inner.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return inner.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return inner.retainAll(c);
	}

	@Override
	public void clear() {
		inner.clear();

	}

	@Override
	public T get(int index) {
		return inner.get(index);
	}

	@Override
	public T set(int index, T element) {
		return inner.set(index, element);
	}

	@Override
	public void add(int index, T element) {
		inner.add(index, element);
	}

	@Override
	public T remove(int index) {
		return inner.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return inner.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return inner.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return new ListWrapperIterator(0);//inner.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return new ListWrapperIterator(index);
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return inner.subList(fromIndex, toIndex);
	}

	@Override
	public boolean equals(Object o) {
		return inner.equals(o);
	}

	@Override
	public int hashCode() {
		return inner.hashCode();
	}
}
