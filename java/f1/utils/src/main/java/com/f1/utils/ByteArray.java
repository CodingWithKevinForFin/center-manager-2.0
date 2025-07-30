/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.Arrays;

import com.f1.base.ToStringable;

public final class ByteArray implements CharSequence, ToStringable {

	private byte[] data;
	private int start;
	private int end;
	private int hash;
	private String toString = null;

	public ByteArray() {
		clear();
	}

	public ByteArray(byte[] data) {
		this(data, 0, data.length);

	}

	public ByteArray(byte[] data, int start, int end) {
		reset(data, start, end);

	}

	public ByteArray(String string) {
		this(string.getBytes());
	}

	public ByteArray reset(byte[] data) {
		return reset(data, 0, data.length);
	}

	public ByteArray resetNoCheck(byte[] data, int start, int end) {
		this.data = data;
		this.start = start;
		this.end = end;
		this.toString = null;
		this.hash = 0;
		return this;
	}

	public ByteArray reset(byte[] data, int start, int end) {
		if (data == null)
			throw new NullPointerException("data");
		if (start > end || start < 0 || end > data.length)
			throw new IndexOutOfBoundsException("data: " + data.length + " byte(s), start: " + start + ", end: " + end);
		this.data = data;
		this.start = start;
		this.end = end;
		this.toString = null;
		this.hash = 0;
		return this;
	}

	@Override
	public int length() {
		return end - start;
	}

	@Override
	public char charAt(int index) {
		return (char) data[start + index];
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return new ByteArray(data, this.start + start, this.start + end);
	}

	public void clear() {
		this.start = end = 0;
		this.toString = "";
		this.hash = 0;
	}

	@Override
	public String toString() {
		if (toString == null)
			toString = "ByteArray[" + data.length + "]";
		return toString;
	}

	public byte[] getData() {
		return data;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null || anObject.getClass() != ByteArray.class)
			return false;
		ByteArray other = (ByteArray) anObject;
		int s1 = start;
		int s2 = other.start;
		int len = end - s1;
		if (len != other.end - s2)
			return false;
		byte v1[] = data, v2[] = other.data;
		if (len == 0)
			return true;
		else if (v1[s1] != v2[s2])
			return false;
		while (--len > 0)
			if (v1[s1 + len] != v2[s2 + len])
				return false;
		return true;
	}

	@Override
	public int hashCode() {
		if (hash != 0)
			return hash;
		if (end == start)
			return 0;
		int h = data[start];
		for (int i = start + 1; i < end; i++)
			h = 31 * h + data[i];
		if (h == 0)
			h++;
		return hash = h;
	}

	public int indexOf(char c, int fromIndex) {
		for (int i = start + fromIndex; i < end; i++)
			if (data[c] == c)
				return i - start;
		return -1;
	}

	public boolean startsWith(ByteArray prefix, int prefixOffset) {
		if (prefix.length() - prefixOffset > end - start)
			return false;
		byte[] data2 = prefix.data;
		for (int i = start, j = prefix.start + prefixOffset, e = prefix.end; j < e; i++, j++)
			if (data[i] != data2[j])
				return false;
		return true;
	}

	public boolean endsWith(ByteArray prefix, int prefixOffset) {
		if (prefix.length() - prefixOffset > end - start)
			return false;
		byte[] data2 = prefix.data;
		for (int i = end - prefix.length() + prefixOffset, j = prefix.start + prefixOffset, e = prefix.end; j < e; i++, j++)
			if (data[i] != data2[j])
				return false;
		return true;
	}

	public void subarray(int start, int end, ByteArray sink) {
		sink.reset(data, toDataOffset(start), toDataOffset(end));
	}

	private int toDataOffset(int offset) {
		return start + offset;
	}

	public ByteArray cloneToClipped() {
		return new ByteArray(Arrays.copyOfRange(data, start, end));
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.ensureCapacity(sink.length() + end - start);
		for (int i = start; i < end; i++)
			sink.append((char) data[i]);
		return sink;
	}

}
