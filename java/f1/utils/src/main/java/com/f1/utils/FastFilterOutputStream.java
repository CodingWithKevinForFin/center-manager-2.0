package com.f1.utils;

import java.io.IOException;
import java.io.OutputStream;

public class FastFilterOutputStream extends OutputStream {

	public FastFilterOutputStream(OutputStream out) {
		this.out = out;
	}

	protected void setInner(OutputStream out) {
		this.out = out;
	}

	public OutputStream getInner() {
		return this.out;
	}

	public void write(int b) throws IOException {
		out.write(b);
	}

	public void write(byte b[]) throws IOException {
		write(b, 0, b.length);
	}

	public void write(byte b[], int off, int len) throws IOException {
		if ((off | len | b.length - (len + off) | off + len) < 0)
			throw new IndexOutOfBoundsException();
		for (int i = 0; i < len; i++)
			write(b[off + i]);

	}

	public void flush() throws IOException {
		out.flush();
	}

	public void close() throws IOException {
		if (out == null)
			return;
		try {
			flush();
		} catch (IOException ignored) {
		}
		out.close();
	}

	protected OutputStream out;
}
