/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

public final class CharArray implements CharSequence {

	private char[] data;
	private int start;
	private int end, hash;
	private String toString = null;

	public CharArray() {
		clear();
	}

	public CharArray(char[] data) {
		this(data, 0, data.length);

	}

	public CharArray(char[] data, int start, int end) {
		reset(data, start, end);

	}

	public CharArray(String string) {
		this(string.toCharArray());
	}

	public void reset(char[] data) {
		this.reset(data, 0, data.length);
	}

	public void reset(char[] data, int start, int end) {
		this.data = data;
		this.start = start;
		this.end = end;
		this.toString = null;
		this.hash = 0;
	}

	@Override
	public int length() {
		return end - start;
	}

	@Override
	public char charAt(int index) {
		return data[start + index];
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return new CharArray(data, this.start + start, this.start + end);
	}

	public void clear() {
		this.data = OH.EMPTY_CHAR_ARRAY;
		this.start = end = 0;
		this.toString = "";
		this.hash = 0;
	}

	@Override
	public String toString() {
		if (toString == null)
			toString = new String(data, start, end - start);
		return toString;
	}

	public char[] getData() {
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
		return anObject != null && anObject.getClass() == CharArray.class && equals((CharArray) anObject);
	}

	public boolean equals(CharArray anObject) {
		if (anObject == null)
			return false;
		if (this == anObject)
			return true;
		CharArray other = anObject;
		int s = other.start;
		if (end - start != other.end - s)
			return false;
		char v1[] = data, v2[] = other.data;
		for (int i = end - 1; i >= start; i--)
			if (v1[i] != v2[s + i])
				return false;
		return true;
	}

	@Override
	public int hashCode() {
		if (hash != 0 || end == start)
			return 0;
		int h = 0;
		for (int i = start; i < end; i++)
			h = 31 * h + data[i];
		return hash = h;
	}

	public int indexOf(char c, int fromIndex) {
		for (int i = start + fromIndex; i < end; i++)
			if (data[c] == c)
				return i - start;
		return -1;
	}

	public boolean startsWith(CharArray prefix, int prefixOffset) {
		if (prefix.length() - prefixOffset > end - start)
			return false;
		char[] data2 = prefix.data;
		for (int i = start, j = prefix.start + prefixOffset, e = prefix.end; j < e; i++, j++)
			if (data[i] != data2[j])
				return false;
		return true;
	}

	public boolean endsWith(CharArray prefix, int prefixOffset) {
		if (prefix.length() - prefixOffset > end - start)
			return false;
		char[] data2 = prefix.data;
		for (int i = end - prefix.length() + prefixOffset, j = prefix.start + prefixOffset, e = prefix.end; j < e; i++, j++)
			if (data[i] != data2[j])
				return false;
		return true;
	}

	private int toDataOffset(int offset) {
		return start + offset;
	}

}
