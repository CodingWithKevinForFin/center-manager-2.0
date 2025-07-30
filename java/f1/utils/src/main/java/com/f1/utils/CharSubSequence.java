/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import com.f1.base.Clearable;

public final class CharSubSequence implements CharSequence, Clearable {

	private CharSequence data;
	private int start;
	private int end, hash;
	private String toString = null;

	public CharSubSequence() {
		clear();
	}

	public CharSubSequence(CharSequence data) {
		this(data, 0, data.length());

	}

	public CharSubSequence(CharSequence data, int start, int end) {
		reset(data, start, end);

	}

	public CharSubSequence(String string) {
		this.data = string;
		this.start = 0;
		this.end = string.length();
		this.toString = null;
		this.hash = 0;
	}

	public CharSubSequence reset(CharSequence data) {
		return this.reset(data, 0, data.length());

	}

	public CharSubSequence reset(CharSequence data, int start, int end) {
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
		if (index >= end)
			throw new StringIndexOutOfBoundsException(index + " > " + end);
		return data.charAt(start + index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return new CharSubSequence(data, this.start + start, this.start + end);
	}

	@Override
	public void clear() {
		this.data = "";
		this.start = end = 0;
		this.toString = "";
		this.hash = 0;
	}

	@Override
	public String toString() {
		if (toString == null) {

			toString = new String(toCharArray());
		}
		return toString;
	}

	private char[] toCharArray() {
		final char[] r = new char[end - start];
		for (int i = start; i < end; i++)
			r[i - start] = data.charAt(i);
		return r;

	}
	public CharSequence getData() {
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
		return anObject != null && anObject.getClass() == CharSubSequence.class && equals((CharSubSequence) anObject);
	}

	public boolean equals(CharSubSequence anObject) {
		if (anObject == null)
			return false;
		if (this == anObject)
			return true;
		CharSubSequence other = anObject;
		int s = other.start;
		if (end - start != other.end - s)
			return false;
		CharSequence v1 = data;
		CharSequence v2 = other.data;
		for (int i = end - 1; i >= start; i--)
			if (v1.charAt(i) != v2.charAt(s + i))
				return false;
		return true;
	}

	@Override
	public int hashCode() {
		if (hash != 0 || end == start)
			return 0;
		int h = 0;
		for (int i = start; i < end; i++)
			h = 31 * h + data.charAt(i);
		return hash = h;
	}

	public int indexOf(char c, int fromIndex) {
		for (int i = start + fromIndex; i < end; i++)
			if (data.charAt(i) == c)
				return i - start;
		return -1;
	}

	private int toDataOffset(int offset) {
		return start + offset;
	}

}
