/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class FastByteArrayInputStream extends InputStream {

	public FastByteArrayInputStream reset(byte[] buf) {
		this.buf = buf;
		this.pos = 0;
		this.count = buf.length;
		return this;
	}

	public FastByteArrayInputStream reset(byte[] buf, int start, int length) {
		this.buf = buf;
		this.pos = start;
		this.count = length + start;
		return this;
	}

	protected byte buf[];

	protected int pos;

	private int mark = 0;

	protected int count;

	public FastByteArrayInputStream(byte buf[]) {
		this.buf = buf;
		this.pos = 0;
		this.count = buf.length;
	}

	public FastByteArrayInputStream(byte buf[], int offset, int length) {
		this.buf = buf;
		this.pos = offset;
		this.count = Math.min(offset + length, buf.length);
		this.mark = offset;
	}

	public int read() {
		return (pos < count) ? (buf[pos++] & 0xff) : -1;
	}

	public int read(byte b[], int off, int len) {
		if (b == null) {
			throw new NullPointerException();
		} else if (off < 0 || len < 0 || len > b.length - off) {
			throw new IndexOutOfBoundsException();
		}
		if (pos >= count) {
			return -1;
		}
		if (pos + len > count) {
			len = count - pos;
		}
		if (len <= 0) {
			return 0;
		}
		System.arraycopy(buf, pos, b, off, len);
		pos += len;
		return len;
	}

	@Override
	public long skip(long n) {
		if (pos + n > count) {
			n = count - pos;
		}
		if (n < 0) {
			return 0;
		}
		pos += n;
		return n;
	}

	@Override
	public int available() {
		return count - pos;
	}

	public void ensureAvailable(int size) throws EOFException {
		if (count - pos < size)
			throw new EOFException("available less than requested: " + (count - pos) + " < " + size + " at " + pos);
	}

	@Override
	public boolean markSupported() {
		return true;
	}

	@Override
	public void mark(int readAheadLimit) {
		mark = pos;
	}

	@Override
	public void reset() {
		pos = mark;
	}

	@Override
	public void close() throws IOException {
	}

	public String toString() {
		return getClass().getSimpleName() + ": [ " + pos + " / " + count + " ]";
	}

	public int indexOf(byte[] find) {
		return AH.indexOf(buf, find, pos, this.count);
	}

	public byte[] readUntil(byte[] find) {
		int i = indexOf(find);
		if (i == -1)
			return null;
		byte[] r = new byte[i - pos];
		System.arraycopy(buf, pos, r, 0, i - pos);
		pos = i;
		return r;
	}

}
