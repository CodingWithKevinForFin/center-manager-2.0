package com.f1.console.impl;

import java.io.IOException;
import java.io.Writer;
import com.f1.utils.SH;

public class PrefixWriter extends Writer {

	private Writer inner;
	private String prefix;

	public PrefixWriter(Writer inner, String prefix) {
		this.inner = inner;
		this.prefix = prefix;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		for (int i = 0; i < len; i++)
			write(cbuf[i + off]);
	}

	private boolean inLinefeed = true;

	public boolean isInLineFeed() {
		return inLinefeed;
	}

	@Override
	public void write(int b) throws IOException {
		if (inLinefeed) {
			inner.write(prefix);
			inLinefeed = false;
		}
		inner.write(b);
		if ((byte) b == SH.CHAR_LF)
			inLinefeed = true;
	}

	@Override
	public void flush() throws IOException {
		inner.flush();
	}

	@Override
	public void close() throws IOException {
		inner.close();
	}

	public void setInLinefeed(boolean inLineFeed) {
		this.inLinefeed = inLineFeed;
	}

}
