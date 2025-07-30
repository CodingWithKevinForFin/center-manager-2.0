package com.f1.utils;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * will iterator over a list backwards(starting at the last element)
 * 
 * @param <I>
 */
public class ReverseListIterator<I> implements Iterator<I>, Iterable<I> {

	public ReverseListIterator(List<I> list) {
		it = list.listIterator(list.size());
	}

	final private ListIterator<I> it;

	@Override
	public boolean hasNext() {
		return it.hasPrevious();
	}

	@Override
	public I next() {
		return it.previous();
	}

	@Override
	public void remove() {
		it.remove();
	}

	@Override
	public Iterator<I> iterator() {
		if (gotIterator)
			throw new IllegalStateException();
		gotIterator = true;
		return this;
	}

	private boolean gotIterator = false;

}
