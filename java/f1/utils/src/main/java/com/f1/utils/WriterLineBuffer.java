package com.f1.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

public abstract class WriterLineBuffer extends Writer {

	private static final Logger log = LH.get();

	private StringBuilder buffer = new StringBuilder();
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		while (off < len) {
			char c = cbuf[off++];
			if (c == '\n') {
				try {
					onEol(buffer);
				} catch (Exception e) {
					LH.warning(log, "error writting buffer ", e);
				}
				buffer.setLength(0);
			} else if (c != '\r')
				buffer.append(c);
		}
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
		if (buffer.length() > 0) {
			try {
				onEol(buffer);
			} catch (Exception e) {
				LH.warning(log, "error writting buffer ", e);
			}
			buffer.setLength(0);
		}
		this.buffer = null;
	}

	public abstract void onEol(CharSequence data);

}
