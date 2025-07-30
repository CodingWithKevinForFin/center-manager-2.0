/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import com.f1.base.StringBuildable;

public class BasicStringBuildable implements StringBuildable {
	final private StringBuilder inner;

	public BasicStringBuildable() {
		this(new StringBuilder());
	}

	public BasicStringBuildable(StringBuilder inner) {
		this.inner = inner;
	}

	public int length() {
		return inner.length();
	}

	public int capacity() {
		return inner.capacity();
	}

	public int hashCode() {
		return inner.hashCode();
	}

	public void ensureCapacity(int minimumCapacity) {
		inner.ensureCapacity(minimumCapacity);
	}

	public void trimToSize() {
		inner.trimToSize();
	}

	public void setLength(int newLength) {
		inner.setLength(newLength);
	}

	public BasicStringBuildable append(Object obj) {
		inner.append(obj);
		return this;
	}

	public BasicStringBuildable append(String str) {
		inner.append(str);
		return this;
	}

	public BasicStringBuildable append(StringBuffer sb) {
		inner.append(sb);
		return this;
	}

	public char charAt(int index) {
		return inner.charAt(index);
	}

	public BasicStringBuildable append(CharSequence s) {
		inner.append(s);
		return this;
	}

	public int codePointAt(int index) {
		return inner.codePointAt(index);
	}

	public BasicStringBuildable append(CharSequence s, int start, int end) {
		inner.append(s, start, end);
		return this;
	}

	public BasicStringBuildable append(char[] str) {
		inner.append(str);
		return this;
	}

	public BasicStringBuildable append(char[] str, int offset, int len) {
		inner.append(str, offset, len);
		return this;
	}

	public BasicStringBuildable append(boolean b) {
		inner.append(b);
		return this;
	}

	public BasicStringBuildable append(char c) {
		inner.append(c);
		return this;
	}

	public BasicStringBuildable append(int i) {
		inner.append(i);
		return this;
	}

	public int codePointBefore(int index) {
		return inner.codePointBefore(index);
	}

	public BasicStringBuildable append(long lng) {
		inner.append(lng);
		return this;
	}

	public BasicStringBuildable append(float f) {
		inner.append(f);
		return this;
	}

	public BasicStringBuildable append(double d) {
		inner.append(d);
		return this;
	}

	public BasicStringBuildable appendCodePoint(int codePoint) {
		inner.appendCodePoint(codePoint);
		return this;
	}

	public BasicStringBuildable delete(int start, int end) {
		inner.delete(start, end);
		return this;
	}

	public BasicStringBuildable deleteCharAt(int index) {
		inner.deleteCharAt(index);
		return this;
	}

	public BasicStringBuildable replace(int start, int end, String str) {
		inner.replace(start, end, str);
		return this;
	}

	public int codePointCount(int beginIndex, int endIndex) {
		return inner.codePointCount(beginIndex, endIndex);
	}

	public BasicStringBuildable insert(int index, char[] str, int offset, int len) {
		inner.insert(index, str, offset, len);
		return this;
	}

	public BasicStringBuildable insert(int offset, Object obj) {
		inner.insert(offset, obj);
		return this;
	}

	public BasicStringBuildable insert(int offset, String str) {
		inner.insert(offset, str);
		return this;
	}

	public BasicStringBuildable insert(int offset, char[] str) {
		inner.insert(offset, str);
		return this;
	}

	public BasicStringBuildable insert(int dstOffset, CharSequence s) {
		inner.insert(dstOffset, s);
		return this;
	}

	public int offsetByCodePoints(int index, int codePointOffset) {
		return inner.offsetByCodePoints(index, codePointOffset);
	}

	public BasicStringBuildable insert(int dstOffset, CharSequence s, int start, int end) {
		inner.insert(dstOffset, s, start, end);
		return this;
	}

	public BasicStringBuildable insert(int offset, boolean b) {
		inner.insert(offset, b);
		return this;
	}

	public BasicStringBuildable insert(int offset, char c) {
		inner.insert(offset, c);
		return this;
	}

	public BasicStringBuildable insert(int offset, int i) {
		inner.insert(offset, i);
		return this;
	}

	public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
		inner.getChars(srcBegin, srcEnd, dst, dstBegin);
	}

	public BasicStringBuildable insert(int offset, long l) {
		inner.insert(offset, l);
		return this;
	}

	public BasicStringBuildable insert(int offset, float f) {
		inner.insert(offset, f);
		return this;
	}

	public BasicStringBuildable insert(int offset, double d) {
		inner.insert(offset, d);
		return this;
	}

	public int indexOf(String str) {
		return inner.indexOf(str);
	}

	public int indexOf(String str, int fromIndex) {
		return inner.indexOf(str, fromIndex);
	}

	public int lastIndexOf(String str) {
		return inner.lastIndexOf(str);
	}

	public int lastIndexOf(String str, int fromIndex) {
		return inner.lastIndexOf(str, fromIndex);
	}

	public BasicStringBuildable reverse() {
		inner.reverse();
		return this;
	}

	public String toString() {
		return inner.toString();
	}

	public void setCharAt(int index, char ch) {
		inner.setCharAt(index, ch);
	}

	public String substring(int start) {
		return inner.substring(start);
	}

	public CharSequence subSequence(int start, int end) {
		return inner.subSequence(start, end);
	}

	public String substring(int start, int end) {
		return inner.substring(start, end);
	}

	@Override
	public void write(int b) {
		append(b);
	}

	@Override
	public void write(byte[] bytes) {
		write(bytes, 0, bytes.length);
	}

	@Override
	public void write(byte[] b, int off, int len) {
		len += off;
		for (int i = off; i < len; i++)
			append(b[i]);

	}

	@Override
	public void writeBoolean(boolean v) {
		append(v);
	}

	@Override
	public void writeByte(int v) {
		append(v);

	}

	@Override
	public void writeShort(int v) {
		append(v);

	}

	@Override
	public void writeChar(int v) {
		append(v);

	}

	@Override
	public void writeInt(int v) {
		append(v);

	}

	@Override
	public void writeLong(long v) {
		append(v);

	}

	@Override
	public void writeFloat(float v) {
		append(v);

	}

	@Override
	public void writeDouble(double v) {
		append(v);

	}

	@Override
	public void writeBytes(String s) {
		append(s);

	}

	@Override
	public void writeChars(String s) {
		append(s);

	}

	@Override
	public void writeUTF(String s) {
		append(s);

	}

	@Override
	public void appendNewLine() {
		append(SH.NEWLINE);
	}

	@Override
	public StringBuilder getInner() {
		return inner;
	}

}
