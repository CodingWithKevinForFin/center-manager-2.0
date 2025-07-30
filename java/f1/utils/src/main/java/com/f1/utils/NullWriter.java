package com.f1.utils;

import java.io.IOException;
import java.io.Writer;

public class NullWriter extends Writer {

	public static final NullWriter INSTANCE = new NullWriter();

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public void write(char[] cbuf) throws IOException {
	}

	@Override
	public void write(int c) throws IOException {
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
	}

	@Override
	public void write(String str) throws IOException {
	}

}
