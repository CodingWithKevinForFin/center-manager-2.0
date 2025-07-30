package com.f1.utils;

import java.io.IOException;
import java.io.Writer;

public class FastBufferedWriter extends Writer {

	@Override
	public void write(char[] cbuf) throws IOException {
		write(cbuf, 0, cbuf.length);
	}

	@Override
	public void write(String str) throws IOException {
		write(str, 0, str.length());
	}

	@Override
	public Writer append(char x0) throws IOException {
		write(x0);
		return this;
	}

	@Override
	public Writer append(CharSequence csq, int start, int end) throws IOException {
		CharSequence cs = (csq == null ? "null" : csq);
		write(cs.subSequence(start, end).toString());
		return this;

	}

	@Override
	public Writer append(CharSequence csq) throws IOException {
		if (csq == null)
			write("null");
		else
			write(csq.toString());
		return this;

	}

	private Writer out;

	private char cb[];
	private int nChars, nextChar;

	private static final int defaultCharBufferSize = 8192;

	private String lineSeparator;

	public FastBufferedWriter(Writer out) {
		this(out, defaultCharBufferSize);
	}

	@SuppressWarnings("restriction")
	public FastBufferedWriter(Writer out, int sz) {
		super(out);
		if (sz <= 0)
			throw new IllegalArgumentException("Buffer size <= 0");
		this.out = out;
		cb = new char[sz];
		nChars = sz;
		nextChar = 0;

		lineSeparator = java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));
	}

	private void ensureOpen() throws IOException {
		if (out == null)
			throw new IOException("Stream closed");
	}

	void flushBuffer() throws IOException {
		ensureOpen();
		if (nextChar == 0)
			return;
		out.write(cb, 0, nextChar);
		nextChar = 0;
	}

	public void write(int c) throws IOException {
		ensureOpen();
		if (nextChar >= nChars)
			flushBuffer();
		cb[nextChar++] = (char) c;
	}

	private int min(int a, int b) {
		if (a < b)
			return a;
		return b;
	}

	public void write(char cbuf[], int off, int len) throws IOException {
		ensureOpen();
		if ((off < 0) || (off > cbuf.length) || (len < 0) || ((off + len) > cbuf.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}

		if (len >= nChars) {
			flushBuffer();
			out.write(cbuf, off, len);
			return;
		}

		int b = off, t = off + len;
		while (b < t) {
			int d = min(nChars - nextChar, t - b);
			System.arraycopy(cbuf, b, cb, nextChar, d);
			b += d;
			nextChar += d;
			if (nextChar >= nChars)
				flushBuffer();
		}
	}

	public void write(String s, int off, int len) throws IOException {
		ensureOpen();

		int b = off, t = off + len;
		while (b < t) {
			int d = min(nChars - nextChar, t - b);
			s.getChars(b, b + d, cb, nextChar);
			b += d;
			nextChar += d;
			if (nextChar >= nChars)
				flushBuffer();
		}
	}

	public void newLine() throws IOException {
		write(lineSeparator);
	}

	public void flush() throws IOException {
		flushBuffer();
		out.flush();
	}

	public void close() throws IOException {
		if (out == null) {
			return;
		}
		try {
			flushBuffer();
		} finally {
			out.close();
			out = null;
			cb = null;
		}
	}
}
