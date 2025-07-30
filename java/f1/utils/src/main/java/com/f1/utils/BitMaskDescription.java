package com.f1.utils;

import java.util.Iterator;
import java.util.List;

import com.f1.base.IterableAndSize;
import com.f1.base.Lockable;
import com.f1.base.LockedException;
import com.f1.base.LongIterator;
import com.f1.utils.formatter.AbstractFormatter;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongKeyMap.Node;

public class BitMaskDescription extends AbstractFormatter implements Iterable<Long>, Lockable {

	private static final char DEFAULT_DELIM = ',';
	private String[] descriptions;
	private char delim;
	private LongKeyMap<String> types = new LongKeyMap<String>();
	private boolean locked;

	public BitMaskDescription(String name, char delim, int bits) {
		OH.assertBetween(bits, 0, 64);
		descriptions = new String[bits];
		this.delim = delim;
	}

	public BitMaskDescription(String name) {
		this(name, DEFAULT_DELIM, Long.SIZE);
	}

	public BitMaskDescription(BitMaskDescription src) {
		descriptions = src.descriptions.clone();
	}

	public byte defineByte(byte bitmask, String name) {
		return (byte) define((int) bitmask, name);
	}

	public short defineShort(short bitmask, String name) {
		return (short) define((int) bitmask, name);
	}

	public int define(final int bitmask, final String name) {
		define((long) bitmask, name);
		return bitmask;
	}
	public long define(final long bitmask, final String name) {
		LockedException.assertNotLocked(this);
		int location = MH.indexOfOnlyBitSet(bitmask);
		if (location >= descriptions.length)
			throw new IndexOutOfBoundsException("bitmask too large for bit depth of " + descriptions.length + ": " + SH.toHex(bitmask));
		if (location == -1)
			throw new IllegalArgumentException("should only have one bit set: " + SH.toHex(bitmask));
		if (descriptions[location] != null)
			throw new RuntimeException("duplicate description at " + SH.toHex(bitmask) + ": " + descriptions[location] + "," + name);
		descriptions[location] = name;
		types.put(bitmask, name);
		return bitmask;
	}

	public String toString(int mask) {
		return toString(mask, new StringBuilder()).toString();
	}

	public StringBuilder toString(long mask, StringBuilder sb) {
		int start = -1;
		boolean first = true;
		for (;;) {
			start = MH.indexOfBitSet(mask, start + 1);
			if (start == -1)
				break;
			String desc = descriptions[start];
			if (first)
				first = false;
			else
				sb.append(delim);
			sb.append(desc != null ? desc : SH.toHex(1 << start));
		}
		return sb;
	}

	public String toDetailedString(long mask) {
		return toString(mask, new StringBuilder()).toString();
	}
	public List<String> getFlags(long mask, List<String> sink) {
		int start = -1;
		for (;;) {
			start = MH.indexOfBitSet(mask, start + 1);
			if (start == -1)
				break;
			String desc = descriptions[start];
			if (desc == null)
				desc = "BIT_" + SH.toString(start);
			sink.add(desc);
		}
		return sink;
	}

	public long getMask(String description) {
		for (Node<String> e : this.types)
			if (e.getValue().equals(description))
				return e.getLongKey();
		return -1;
	}

	public StringBuilder toDetailedString(long mask, StringBuilder sb) {
		int start = -1;
		boolean first = true;
		for (;;) {
			start = MH.indexOfBitSet(mask, start + 1);
			if (start == -1)
				break;
			String desc = descriptions[start];
			if (first)
				first = false;
			else
				sb.append(delim);
			sb.append('(').append(1L << start).append(')');
			sb.append(desc != null ? desc : SH.toHex(1 << start));
		}
		return sb;
	}

	public LongIterator getBitmasks() {
		return types.keyIterator();
	}

	@Override
	public Iterator<Long> iterator() {
		return getBitmasks();
	}

	@Override
	public boolean canParse(String text) {
		return false;
	}

	@Override
	public Object parse(String text) {
		return null;
	}

	@Override
	public void format(Object value, StringBuilder sb) {
		if (value == null)
			sb.append("null");
		else
			toString(((Number) value).intValue(), sb);
	}

	@Override
	public Formatter clone() {
		return new BitMaskDescription(this);
	}

	@Override
	public void lock() {
		this.locked = true;
	}

	@Override
	public boolean isLocked() {
		return locked;
	}

	public IterableAndSize<String> getDescriptions() {
		return this.types.values();
	}

}

