/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.Iterator;

/**
 * will create an iterator that will iterate over the elements of multiple iterable collections in the order that they are supplied to the constructor
 * 
 * @param <T>
 */
public class IterableIterator<T> implements Iterable<T> {

	static public <T> Iterable<T> create(Iterable<? extends T>... i) {
		if (i.length == 0)
			return EmptyIterable.INSTANCE;
		if (i.length == 1)
			return (Iterable<T>) i[0];
		else
			return new ForArray<T>(i);
	}
	static public <T> Iterable<T> create(Iterable<? extends Iterable<? extends T>> i) {
		return new IterableIterator<T>(i);
	}

	private Iterable<? extends Iterable<? extends T>> iterable;

	private IterableIterator(Iterable<? extends Iterable<? extends T>> iterable) {
		this.iterable = iterable;
	}

	@Override
	public Iterator<T> iterator() {
		return new It(this.iterable.iterator());
	}

	public static class ForArray<T> implements Iterable<T> {
		final private Iterable<? extends T>[] iterators;

		private ForArray(Iterable<? extends T>[] iterators) {
			this.iterators = iterators;
		}

		@Override
		public Iterator<T> iterator() {
			return new ArrayIt(this.iterators);
		}
	}

	static private class ArrayIt<T> implements Iterator<T> {
		private Iterator<? extends T> currentIterator;
		final private Iterable<? extends T>[] iterators;
		private int iteratorsPos = 0;

		public ArrayIt(Iterable<? extends T>[] iterators) {
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

	static private class It<T> implements Iterator<T> {

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
