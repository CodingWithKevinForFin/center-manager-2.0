package com.f1.console.impl;

import java.io.IOException;
import java.io.OutputStream;

import com.f1.utils.SH;

public class TelnetOutputStream extends OutputStream {

	private static final byte[] CHAR_LFCR = new byte[] { SH.CHAR_LF, SH.CHAR_CR };
	private OutputStream inner;
	private boolean inLinefeed;

	public TelnetOutputStream(OutputStream out) {
		this.inner = out;
	}

	@Override
	synchronized public void write(int b) throws IOException {
		if (inLinefeed) {
			inLinefeed = false;
			if (b == SH.CHAR_CR)
				return;
		}
		if ((byte) b == SH.CHAR_LF) {
			inner.write(CHAR_LFCR);
			inLinefeed = true;
		} else
			inner.write(b);

	}

	@Override
	public void flush() throws IOException {
		inner.flush();
	}

}
