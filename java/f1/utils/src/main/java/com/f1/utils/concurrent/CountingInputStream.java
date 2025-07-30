package com.f1.utils.concurrent;

import java.io.IOException;
import java.io.InputStream;

public class CountingInputStream extends InputStream {

	private InputStream inner;
	private long count = 0;
	private long markedCount = 0;

	public CountingInputStream(InputStream inner) {
		this.inner = inner;
	}

	@Override
	public int read() throws IOException {
		int r = inner.read();
		if (r != -1)
			count += r;
		return r;
	}

	@Override
	public int read(byte[] b) throws IOException {
		int r = inner.read(b);
		if (r != -1)
			count += r;
		return r;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int r = inner.read(b, off, len);
		if (r != -1)
			count += r;
		return r;
	}

	@Override
	public long skip(long n) throws IOException {
		long r = inner.skip(n);
		count += r;
		return r;
	}

	@Override
	public int available() throws IOException {
		return inner.available();
	}

	@Override
	public void close() throws IOException {
		inner.close();
	}

	public InputStream getInner() {
		return inner;
	}

	public void setInner(InputStream inner) {
		this.inner = inner;
	}

	@Override
	public void mark(int readlimit) {
		markedCount = count;
		this.inner.mark(readlimit);
	}

	@Override
	public boolean markSupported() {
		return inner.markSupported();
	}

	@Override
	public void reset() throws IOException {
		inner.reset();
		count = markedCount;
	}

	public long getCount() {
		return count;
	}

}
