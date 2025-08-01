package com.f1.utils;

import java.io.IOException;
import java.io.InputStream;

public class FastFilterInputStream extends InputStream {

	protected FastFilterInputStream(InputStream in) {
		this.in = in;
	}

	public int read() throws IOException {
		return in.read();
	}

	public int read(byte b[]) throws IOException {
		return read(b, 0, b.length);
	}

	public int read(byte b[], int off, int len) throws IOException {
		return in.read(b, off, len);
	}

	public long skip(long n) throws IOException {
		return in.skip(n);
	}

	public int available() throws IOException {
		return in.available();
	}

	public void close() throws IOException {
		in.close();
	}

	public void mark(int readlimit) {
		in.mark(readlimit);
	}

	public void reset() throws IOException {
		in.reset();
	}

	public boolean markSupported() {
		return in.markSupported();
	}

	protected volatile InputStream in;

	protected void setInner(InputStream in) {
		this.in = in;
	}

	protected InputStream getInner() {
		return this.in;
	}
}
