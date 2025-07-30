package com.f1.console.impl;

import java.io.IOException;
import java.io.InputStream;

import com.f1.utils.SH;

public class TelnetInputStream extends InputStream {
	private final InputStream inner;
	private boolean inLineFeed;

	public TelnetInputStream(InputStream inner) {
		if (inner == null)
			throw new NullPointerException();
		this.inner = inner;
	}

	@Override
	public int read(byte[] b, int start, int end) throws IOException {
		if (inLineFeed) {
			byte c = (byte) inner.read();
			if (c != SH.CHAR_CR)
				throw new RuntimeException("expecting CR not " + c);
			inLineFeed = false;
		}
		int r = inner.read(b, start, end);
		end = r + start;
		int i = start, o = start;
		while (i < end) {
			if (b[i] == SH.CHAR_LF) {
				i++;
				if (i == end) {
					inLineFeed = true;
					return r;
				}
				if (b[i] != SH.CHAR_CR)
					throw new RuntimeException("expecting CR: " + b[i]);
				r--;
			}
			o++;
			i++;
			b[o] = b[i];
		}
		return r;
	}

	@Override
	public int read() throws IOException {
		return inner.read();
	}

	@Override
	public void close() throws IOException {
		inner.close();
	}

	@Override
	public int available() throws IOException {
		return inner.available();
	}

}
