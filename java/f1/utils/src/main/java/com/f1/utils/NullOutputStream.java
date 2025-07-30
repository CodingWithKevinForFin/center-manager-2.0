package com.f1.utils;

import java.io.OutputStream;

public class NullOutputStream extends OutputStream {

	public static final OutputStream INSTANCE = new NullOutputStream();

	@Override
	public void write(int b) {
	}

	@Override
	public void write(byte[] b, int off, int len) {
		OH.assertLe(len + off, b.length);
	}

	@Override
	public void write(byte[] b) {
	}

}
