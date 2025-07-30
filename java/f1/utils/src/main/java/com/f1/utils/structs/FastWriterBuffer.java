/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import com.f1.utils.Buffer;

public class FastWriterBuffer extends Writer implements Buffer {

	public static final int DEFAULT_SIZE = 32;
	private char[] buffer;
	private int count;

	public FastWriterBuffer() {
		this(DEFAULT_SIZE);

	}

	public FastWriterBuffer(int size) {
		this.buffer = new char[size];
	}

	protected void ensureCanStore(int len) {
		if (count + len > buffer.length)
			buffer = Arrays.copyOf(buffer, Math.max(buffer.length << 1, count + len));
	}

	@Override
	public void write(char[] b, int off, int len) throws IOException {
		ensureCanStore(len);

		if (off < 0 || len < 0 || off + len > b.length)
			throw new IndexOutOfBoundsException();
		if (len == 0)
			return;
		System.arraycopy(b, off, buffer, count, len);
		count += len;
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() throws IOException {
		buffer = null;
	}

	public String toString() {
		return new String(buffer, 0, count);
	}

	public void writeTo(StringBuilder sb) {
		sb.append(buffer, 0, count);
	}

	@Override
	public int getCapacity() {
		return buffer.length;
	}

	@Override
	public void setCapacity(int size) {
		buffer = Arrays.copyOf(buffer, size);
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public void reset() {
		this.count = 0;
	}

}
