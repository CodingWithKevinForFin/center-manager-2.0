/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.logging.Logger;

public class FastByteArrayOutputStream extends OutputStream implements Buffer {

	private static final Logger log = Logger.getLogger(FastByteArrayOutputStream.class.getName());

	protected byte buf[];

	protected int count;

	public FastByteArrayOutputStream() {
		this(32);
	}

	public FastByteArrayOutputStream(int size) {
		if (size < 0)
			throw new IllegalArgumentException("Negative initial size: " + size);
		buf = new byte[size];
	}

	public void write(int b) {
		int newcount = count + 1;
		ensureCanStore(1);
		buf[count] = (byte) b;
		count = newcount;
	}

	public void write(byte b[], int off, int len) {
		if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0))
			throw new IndexOutOfBoundsException();
		if (len == 0)
			return;
		int newcount = count + len;
		ensureCanStore(len);
		System.arraycopy(b, off, buf, count, len);
		count = newcount;
	}
	public void write(byte b[], int off) {
		if ((off < 0) || (off > b.length))
			throw new IndexOutOfBoundsException();
		int len = b.length - off;
		if (len == 0)
			return;
		int newcount = count + len;
		ensureCanStore(len);
		System.arraycopy(b, off, buf, count, len);
		count = newcount;
	}

	protected void ensureCanStore(int len) {
		if (count + len > buf.length) {
			try {
				int newSize = count + len;
				if (newSize >= 1000000000)
					newSize += 100000000;
				else
					newSize = newSize << 1;
				if (newSize < 0) {
					newSize = Integer.MAX_VALUE;
					long l = ((long) count) + len;
					if (newSize < l)
						throw new RuntimeException("Exceeded max java array size: " + l);
				}

				buf = Arrays.copyOf(buf, newSize);
			} catch (OutOfMemoryError e) {
				final OutOfMemoryError e2 = new OutOfMemoryError("trying to allocate " + (len + count) + " byte(s)");
				e2.initCause(e);
				throw e2;
			}
		}
	}

	public void writeTo(OutputStream out) throws IOException {
		out.write(buf, 0, count);
	}

	public void reset() {
		count = 0;
	}

	public void reset(int maxBufferSize) {
		count = 0;
		if (buf.length > maxBufferSize)
			buf = new byte[maxBufferSize];
	}

	public byte[] toByteArray() {
		return count == 0 ? OH.EMPTY_BYTE_ARRAY : Arrays.copyOf(buf, count);
	}

	public void toByteArray(byte[] data, int offset, int length) {
		System.arraycopy(buf, 0, data, offset, length);
	}

	public int size() {
		return count;
	}

	public String toString() {
		return count == 0 ? "" : new String(buf, 0, count);
	}

	public void close() throws IOException {
	}

	@Override
	public int getCapacity() {
		return buf.length;
	}

	@Override
	public void setCapacity(int size) {
		buf = Arrays.copyOf(buf, size);
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public void flush() {
	}

	public void write(byte b[]) {
		write(b, 0, b.length);
	}

	public byte[] getBuffer() {
		return buf;
	}

	public void ensureCapacity(int size) {
		if (size > buf.length) {
			if (count == 0)
				buf = new byte[size];
			else
				buf = Arrays.copyOf(buf, size);
		}
	}

	public void incrementCount(int i) {
		count += i;
	}
}
