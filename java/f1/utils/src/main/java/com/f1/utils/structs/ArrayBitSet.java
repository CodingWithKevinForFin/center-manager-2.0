package com.f1.utils.structs;

import java.util.Arrays;
import java.util.BitSet;
import com.f1.utils.AH;

public class ArrayBitSet extends BitSet {

	@Override
	public void flip(int i) {
		set(i, !get(i));
	}

	@Override
	public void flip(int fromIndex, int toIndex) {
		for (int i = fromIndex; i < toIndex; i++)
			flip(i);
	}

	@Override
	public void set(int bitIndex, boolean value) {
		bits[bitIndex] = value;
	}

	@Override
	public void set(int fromIndex, int toIndex) {
		for (int i = fromIndex; i < toIndex; i++)
			set(i);
	}

	@Override
	public void set(int fromIndex, int toIndex, boolean value) {
		for (int i = fromIndex; i < toIndex; i++)
			set(i, value);
	}

	@Override
	public void clear(int bitIndex) {
		bits[bitIndex] = false;
	}

	@Override
	public void clear(int fromIndex, int toIndex) {
		for (int i = fromIndex; i < toIndex; i++)
			clear(i);
	}

	@Override
	public void clear() {
		for (int i = 0; i < bits.length; i++)
			clear(i);
	}

	@Override
	public boolean get(int bitIndex) {
		return bitIndex >= bits.length ? false : bits[bitIndex];
	}

	@Override
	public BitSet get(int fromIndex, int toIndex) {
		final boolean[] dest = new boolean[toIndex - fromIndex];
		System.arraycopy(bits, fromIndex, dest, 0, dest.length);
		return new ArrayBitSet(bits);
	}

	@Override
	public int nextSetBit(int fromIndex) {
		for (int i = fromIndex; i < bits.length; i++)
			if (bits[i])
				return i;
		return -1;
	}

	@Override
	public int nextClearBit(int fromIndex) {
		for (int i = fromIndex; i < bits.length; i++)
			if (!bits[i])
				return i;
		return -1;
	}

	@Override
	public int length() {
		for (int i = bits.length - 1;; i--)
			if (i < 0 || bits[i])
				return i + 1;
	}

	@Override
	public boolean isEmpty() {
		return length() > 0;
	}

	@Override
	public boolean intersects(BitSet set) {
		for (int i = 0; i < bits.length; i++)
			if (bits[i] && set.get(i))
				return true;
		return false;
	}

	@Override
	public int cardinality() {
		int cnt = 0;
		for (int i = 0; i < bits.length; i++)
			if (bits[i])
				cnt++;
		return cnt;
	}

	@Override
	public void and(BitSet set) {
		int l = Math.min(bits.length, set.size());
		for (int i = 0; i < l; i++)
			bits[i] &= set.get(i);
	}

	@Override
	public void or(BitSet set) {
		int l = Math.min(bits.length, set.size());
		for (int i = 0; i < l; i++)
			bits[i] |= set.get(i);
	}

	@Override
	public void xor(BitSet set) {
		int l = Math.min(bits.length, set.size());
		for (int i = 0; i < l; i++)
			bits[i] ^= set.get(i);
	}

	@Override
	public void andNot(BitSet set) {
		int l = Math.min(bits.length, set.size());
		for (int i = 0; i < l; i++)
			if (set.get(i))
				bits[i] = false;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(bits);
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException("did you mean .length()?");
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != ArrayBitSet.class)
			return false;
		return Arrays.equals(bits, ((ArrayBitSet) obj).bits);
	}

	@Override
	public String toString() {
		return Arrays.toString(bits);
	}

	private boolean[] bits;

	public ArrayBitSet(int size) {
		super(0);
		bits = new boolean[size];
	}

	public ArrayBitSet(boolean[] bits) {
		this.bits = bits;
	}

	public ArrayBitSet(int[] bitsSet) {
		this(bitsSet.length == 0 ? 0 : AH.max(bitsSet));
		for (int i : bitsSet)
			set(i);
	}

	@Override
	public void set(int bit) {
		if (bit >= bits.length)
			bits = Arrays.copyOf(bits, bit + 1);
		bits[bit] = true;
	}

}
