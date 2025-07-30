package com.f1.codegen.helpers;

import java.util.BitSet;
import java.util.Iterator;

import com.f1.base.ValuedParam;
import com.f1.base.ValuedSchema;

public class PartialMessageNameIterator implements Iterator<ValuedParam> {

	final private BitSet existing;
	final private ValuedParam[] params;
	private int current;

	public PartialMessageNameIterator(BitSet existing, ValuedSchema schema) {
		this.existing = existing;
		params = schema.askValuedParams();
		current = existing.nextSetBit(0);
	}

	@Override
	public boolean hasNext() {
		return current != -1;
	}

	@Override
	public ValuedParam next() {
		ValuedParam r = params[current];
		current = existing.nextSetBit(current + 1);
		return r;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
