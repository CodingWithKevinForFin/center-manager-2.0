package com.f1.utils.concurrent;

import java.io.IOException;
import java.io.OutputStream;

import com.f1.utils.FastFilterOutputStream;

public class TeeOutputStream extends FastFilterOutputStream {

	private OutputStream tee;

	public TeeOutputStream(OutputStream o1, OutputStream o2) {
		super(o1);
		tee = o2;
	}

	@Override
	public void write(int b) throws IOException {
		super.write(b);
		tee.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		super.write(b);
		tee.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		super.write(b, off, len);
		tee.write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		super.flush();
		tee.flush();
	}

	@Override
	public void close() throws IOException {
		super.close();
		tee.close();
	}

}
