/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SingletonIterable<I> implements Iterable<I> {

	public static <S> SingletonIterable<S> toIterable(S singleValue) {
		return new SingletonIterable<S>(singleValue);
	}

	private I value;

	public SingletonIterable(I value) {
		this.value = value;
	}

	@Override
	public Iterator<I> iterator() {
		return new It();
	}

	public class It implements Iterator<I> {

		private boolean called = false;

		@Override
		public boolean hasNext() {
			return !called;
		}

		@Override
		public I next() {
			if (called)
				throw new NoSuchElementException();
			called = true;
			return value;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
}
