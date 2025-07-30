package com.f1.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class FastOutputStreamWriter extends Writer {

	static final private char[] lineSeparator = java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("line.separator")).toCharArray();
	private final FastStreamEncoder se;

	public FastOutputStreamWriter(OutputStream out, String charsetName) throws UnsupportedEncodingException {
		super(out);
		if (charsetName == null)
			throw new NullPointerException("charsetName");
		se = FastStreamEncoder.forOutputStreamWriter(out, charsetName);
	}

	public FastOutputStreamWriter(OutputStream out) {
		super(out);
		try {
			se = FastStreamEncoder.forOutputStreamWriter(out, (String) null);
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
		}
	}

	public FastOutputStreamWriter(OutputStream out, Charset cs) {
		super(out);
		if (cs == null)
			throw new NullPointerException("charset");
		se = FastStreamEncoder.forOutputStreamWriter(out, cs);
	}

	public FastOutputStreamWriter(OutputStream out, CharsetEncoder enc) {
		super(out);
		if (enc == null)
			throw new NullPointerException("charset encoder");
		se = FastStreamEncoder.forOutputStreamWriter(out, enc);
	}

	public String getEncoding() {
		return se.getEncoding();
	}

	void flushBuffer() throws IOException {
		se.flushBuffer();
	}

	public void write(int c) throws IOException {
		se.write(c);
	}

	public void write(char cbuf[], int off, int len) throws IOException {
		se.write(cbuf, off, len);
	}

	public void write(String str, int off, int len) throws IOException {
		se.write(str, off, len);
	}
	public void write(CharSequence str, int off, int len) throws IOException {
		se.write(str, off, len);
	}

	public void flush() throws IOException {
		se.flush();
	}

	public void close() throws IOException {
		se.close();
	}

	public void newLine() throws IOException {
		write(lineSeparator, 0, lineSeparator.length);
	}

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
}
