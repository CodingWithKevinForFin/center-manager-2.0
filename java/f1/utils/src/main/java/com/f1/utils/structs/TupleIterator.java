package com.f1.utils.structs;

import java.util.Iterator;

public class TupleIterator implements Iterator<Object> {

	private final Tuple tuple;
	private int next;

	public TupleIterator(Tuple tuple) {
		next = 0;
		this.tuple = tuple;
	}

	@Override
	public boolean hasNext() {
		return next < tuple.getSize();
	}

	@Override
	public Object next() {
		return tuple.getAt(next++);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
