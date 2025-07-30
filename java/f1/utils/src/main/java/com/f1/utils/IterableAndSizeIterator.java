/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.Iterator;

import com.f1.base.IterableAndSize;

/**
 * will create an iterator that will iterate over the elements of multiple iterable collections in the order that they are supplied to the constructor
 * 
 * @param <T>
 */
public class IterableAndSizeIterator<T> implements IterableAndSize<T> {

	static public <T> IterableAndSize<T> create(IterableAndSize<T>... i) {
		if (i.length == 0)
			return EmptyIterable.INSTANCE;
		if (i.length == 1)
			return i[0];
		else
			return new ForArray<T>(i);
	}

	@Override
	public int size() {
		int r = 0;
		for (IterableAndSize<? extends T> i : this.iterable)
			r += i.size();
		return r;
	}
	static public <T> IterableAndSize<T> create(Iterable<IterableAndSize<T>> i) {
		return new IterableAndSizeIterator<T>(i);
	}

	private Iterable<? extends IterableAndSize<? extends T>> iterable;

	public IterableAndSizeIterator(Iterable<? extends IterableAndSize<? extends T>> iterable) {
		this.iterable = iterable;
	}

	@Override
	public Iterator<T> iterator() {
		if (this.iterable instanceof IterableAndSize) {
			int size = ((IterableAndSize) this.iterable).size();
			if (size == 0)
				return EmptyIterator.INSTANCE;
			if (size == 1) {
				IterableAndSize<? extends T> t = this.iterable.iterator().next();
				return (Iterator<T>) t.iterator();
			}
		}
		return new It(this.iterable.iterator());
	}

	public static class ForArray<T> implements IterableAndSize<T> {
		final private IterableAndSize<? extends T>[] iterators;

		public ForArray(IterableAndSize<? extends T>[] iterators) {
			this.iterators = iterators;
		}

		@Override
		public Iterator<T> iterator() {
			return new ArrayIt(this.iterators);
		}

		@Override
		public int size() {
			int r = 0;
			for (IterableAndSize<? extends T> i : this.iterators)
				r += i.size();
			return r;
		}
	}

	private static class ArrayIt<T> implements Iterator<T> {
		private Iterator<? extends T> currentIterator;
		final private IterableAndSize<? extends T>[] iterators;
		private int iteratorsPos = 0;

		public ArrayIt(IterableAndSize<? extends T>[] iterators) {
			this.iterators = iterators;
			this.iteratorsPos = 0;
			walkIterator();
		}

		@Override
		public T next() {
			T r = currentIterator.next();
			if (!currentIterator.hasNext())
				walkIterator();
			return r;
		}

		private void walkIterator() {
			while (iteratorsPos < iterators.length)
				if ((currentIterator = iterators[iteratorsPos++].iterator()).hasNext())
					return;
			currentIterator = null;
		}
		@Override
		public boolean hasNext() {
			return currentIterator != null;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("can not remove");
		}

	}

	private static class It<T> implements Iterator<T> {

		private Iterator<? extends Iterable<? extends T>> iterator;
		private Iterator<? extends T> currentIterator;

		public It(Iterator<? extends Iterable<? extends T>> iterator) {
			this.iterator = iterator;
			walkIterator();
		}
		@Override
		public T next() {
			T r = currentIterator.next();
			if (!currentIterator.hasNext())
				walkIterator();
			return r;
		}

		private void walkIterator() {
			while (iterator.hasNext()) {
				currentIterator = iterator.next().iterator();
				if (currentIterator.hasNext())
					return;
			}
			currentIterator = null;

		}

		@Override
		public boolean hasNext() {
			return currentIterator != null;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("can not remove");
		}
	}

}
