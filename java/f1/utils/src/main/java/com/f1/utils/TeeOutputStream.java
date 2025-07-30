package com.f1.utils;

import java.io.IOException;
import java.io.OutputStream;

public class TeeOutputStream extends FastFilterOutputStream {

	private OutputStream tee;

	public TeeOutputStream(OutputStream o1, OutputStream o2) {
		super(o1);
		setTee(o2);
	}

	@Override
	public void write(int b) throws IOException {
		getInner().write(b);
		tee.write(b);
	}

	public OutputStream setTee(OutputStream tee) {
		OutputStream old = this.tee;
		this.tee = tee;
		return old;
	}

	public OutputStream getTee() {
		return this.tee;
	}

	@Override
	public void write(byte[] b) throws IOException {
		getInner().write(b);
		tee.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		getInner().write(b, off, len);
		tee.write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		getInner().flush();
		tee.flush();
	}

	@Override
	public void close() throws IOException {
		getInner().close();
		tee.close();
	}

}
