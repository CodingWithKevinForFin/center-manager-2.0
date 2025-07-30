/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

/**
 * a buffer for storing chars, and flushing them to another writer in one shot. may be reset for resuse.
 */
public class BufferedWriter extends Writer implements AppendableBuffer {

	private static final int MAX_GROTH_SIZE = 1024 * 1024 * 10;
	private static final int GROW = 2;
	private char[] chars;
	private int size = 0;
	private int capacity;

	public BufferedWriter(int capacity) {
		if (capacity < 32)
			capacity = 32;
		this.chars = new char[this.capacity = capacity];
	}

	public BufferedWriter append(String str) throws IOException {
		if (str == null)
			str = "null";
		final int len = str.length();
		ensureSize(len + size);
		str.getChars(0, len, chars, size);
		size += len;
		return this;
	}

	@Override
	public BufferedWriter append(CharSequence csq) throws IOException {
		return (csq == null || csq.getClass() == String.class) ? append((String) csq) : append(csq, 0, csq.length());
	}

	@Override
	public BufferedWriter append(char c) throws IOException {
		if (size == capacity)
			ensureSize(size + 1);
		chars[size++] = c;
		return this;
	}

	@Override
	public BufferedWriter append(CharSequence csq, int start, int end) throws IOException {
		ensureSize(end + size - start);
		for (int i = start; i < end; i++)
			chars[size++] = csq.charAt(i);
		return this;
	}

	/**
	 * reset the buffer to zero, please note memory is not freed.
	 */
	public void clear() {
		size = 0;
	}
	public void clear(int maxCapacity) {
		size = 0;
		if (capacity > maxCapacity)
			this.chars = new char[this.capacity = maxCapacity];
	}

	/**
	 * @return number of chars in buffer.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * flush data to out. please note buffer is not reset. set {@link #clear()}
	 * 
	 * @param out
	 *            sink
	 * @throws IOException
	 *             if writing to out fails
	 */
	public void writeTo(Writer out) throws IOException {
		out.write(chars, 0, size);
	}

	/**
	 * return contents of this buffer.
	 */
	@Override
	public String toString() {
		return new String(chars, 0, size);
	}

	@Override
	public void close() throws IOException {
		flush();
		chars = null;
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void write(final char[] cbuf, final int off, final int len) throws IOException {
		ensureSize(size += len);
		System.arraycopy(cbuf, off, chars, size - len, len);
	}

	final protected void ensureSize(int size) {
		if (size > capacity) {
			chars = Arrays.copyOf(chars, capacity = (size > MAX_GROTH_SIZE ? (size + MAX_GROTH_SIZE) : (GROW * size)));
		}
	}
	public char[] getInner() {
		return chars;
	}

	@Override
	public int length() {
		return this.size;
	}

	@Override
	public char charAt(int index) {
		return chars[index];
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return new String(chars, start, end - start);
	}

}
