package com.f1.codegen.helpers;

import java.util.BitSet;

import com.f1.base.ByteIterator;
import com.f1.base.ValuedSchema;

public class PartialMessagePidIterator implements ByteIterator {

	final private BitSet existing;
	final private byte[] pids;
	public PartialMessagePidIterator(BitSet existing, ValuedSchema schema) {
		this.existing = existing;
		pids = schema.askPids();
		current = existing.nextSetBit(0);
	}

	private int current;

	@Override
	public boolean hasNext() {
		return current != -1;
	}

	@Override
	public Byte next() {
		return nextByte();
	}

	@Override
	public byte nextByte() {
		byte r = pids[current];
		current = existing.nextSetBit(current + 1);
		return r;
	}
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
