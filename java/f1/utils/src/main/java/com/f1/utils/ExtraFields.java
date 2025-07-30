/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.Arrays;

@Deprecated
public class ExtraFields {
	final public static int MIN_PAIR_SIZE = 3;// k=v
	final private CharArray inner;
	final private long offsets[];// bytes[0..3]=fixVal [4..5]=start [6..7]=end

	public ExtraFields(CharArray inner, char equals, char delim) {
		this.inner = inner;
		int size = 0;
		char[] data = inner.getData();
		if (inner.length() > 0) {
			size++;
			for (int i = inner.getStart() + MIN_PAIR_SIZE, l = inner.getEnd() - MIN_PAIR_SIZE; i < l; i++)
				if (data[i] == delim) {
					size++;
					i += MIN_PAIR_SIZE;
				}
		}
		offsets = new long[size];
		int keyStart = 0, valStart;
		int index = 0;
		for (int i = inner.getStart(), l = inner.getEnd(); i < l; i++) {
			if (data[i] == equals) {
				valStart = ++i;
				while (i < l && data[i] != delim)
					i++;
				int key = SH.parseInt(inner, keyStart, valStart - 1, 10);
				offsets[index++] = (((long) key) << 32) + (valStart << 16) + i;
				keyStart = ++i;
			}
		}
		Arrays.sort(offsets);
	}

	public boolean getValue(int key, CharArray out) {
		int i = findIndex(key);
		if (i == -1) {
			out.clear();
			return false;
		}
		long v = offsets[i];
		out.reset(inner.getData(), (int) (v >> 16 & Short.MAX_VALUE), (int) (v & Short.MAX_VALUE));
		return true;
	}

	public int getSize() {
		return offsets.length;
	}

	public int getKeyAt(int i) {
		return (int) (offsets[i] >> 32);
	}

	public CharArray getValueAt(int i, CharArray out) {
		if (out == null)
			out = new CharArray();
		long v = offsets[i];
		out.reset(inner.getData(), (int) (v >> 16 & Short.MAX_VALUE), (int) (v & Short.MAX_VALUE));
		return out;
	}

	private int findIndex(int key) {
		int min = 0, max = offsets.length - 1;
		while (min <= max) {
			int pivot = (int) (((long) min + max) / 2);
			int val = (int) (offsets[pivot] >> 32);
			if (val == key)
				return pivot;
			else if (val < key)
				min = pivot + 1;
			else
				max = pivot - 1;
		}
		return -1;
	}

	public static void main(String[] a) {
		ExtraFields ef = new ExtraFields(new CharArray("123=rob;432=dave;1=adi"), '=', ';');
		for (int i = 0; i < ef.getSize(); i++)
			System.out.println(ef.getKeyAt(i) + "  ==> " + ef.getValueAt(i, null));
	}
}
