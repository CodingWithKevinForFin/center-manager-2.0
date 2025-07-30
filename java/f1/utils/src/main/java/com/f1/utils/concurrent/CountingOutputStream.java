package com.f1.utils.concurrent;

import java.io.IOException;
import java.io.OutputStream;

public class CountingOutputStream extends OutputStream {

	private OutputStream inner;
	private long count = 0;
	public CountingOutputStream(OutputStream inner) {
		this.inner = inner;
	}

	public void write(int b) throws IOException {
		inner.write(b);
		count++;
	}

	public void write(byte b[]) throws IOException {
		inner.write(b, 0, b.length);
		count += b.length;
	}

	public void write(byte b[], int off, int len) throws IOException {
		inner.write(b, off, len);
		count += len;
	}

	public void flush() throws IOException {
		inner.flush();
	}

	public void close() throws IOException {
		inner.close();
	}

	public long getCount() {
		return count;
	}

}
