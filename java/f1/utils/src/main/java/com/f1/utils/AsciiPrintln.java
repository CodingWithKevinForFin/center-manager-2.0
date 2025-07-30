package com.f1.utils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;

public class AsciiPrintln implements Println {

	public static final byte OPTION_UNPRINTABLE_SKIP = 0;
	public static final byte OPTION_UNPRINTABLE_MARKER = 1;
	public static final byte OPTION_UNPRINTABLE_CODE = 2;
	public static final byte OPTION_UNPRINTABLE_PRINT = 3;
	private OutputStream inner;

	private byte unprintableMode = 0;
	private boolean error;

	public AsciiPrintln(OutputStream out) {
		this.inner = out;
	}
	@Override
	public void close() throws IOException {
		inner.close();
	}

	@Override
	public Println append(CharSequence csq) {
		return append(csq, 0, csq.length());
	}

	@Override
	public Println println(CharSequence sb) {
		return append(sb, 0, sb.length()).println();
	}

	@Override
	public Println print(CharSequence sb) {
		return append(sb, 0, sb.length());
	}

	@Override
	public Println println(Object sb) {
		String value = String.valueOf(sb);
		return append(value, 0, value.length()).println();
	}

	@Override
	public Println print(Object sb) {
		String value = String.valueOf(sb);
		return append(value, 0, value.length());
	}

	@Override
	public Println println() {
		append('\n');
		return this;
	}

	@Override
	public void flush() {
		try {
			inner.flush();
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	@Override
	public Println append(CharSequence csq, int start, int end) {
		while (start < end)
			append(csq.charAt(start++));
		return this;
	}

	private boolean wasCr;

	//  \r\n is okay  
	@Override
	public Println append(char c) {
		try {
			if (unprintableMode == OPTION_UNPRINTABLE_PRINT) {
				inner.write((int) c);
				return this;
			}
			if (wasCr) {
				wasCr = false;
				if (c == '\n')
					inner.write('\r');
				else
					handleBadChar('\r');
			}
			if (c == '\r') {
				wasCr = true;
			} else if ((c >= 0x20 && c < 0x7f) || c == '\n' || c == '\t')
				inner.write((int) c);
			else
				handleBadChar(c);
		} catch (InterruptedIOException x) {
			Thread.currentThread().interrupt();
		} catch (IOException x) {
			this.error = true;
		} finally {
		}
		return this;
	}
	private void handleBadChar(char c) throws IOException {
		if (unprintableMode != OPTION_UNPRINTABLE_SKIP) {
			inner.write(SH.CHAR_UPSIDEDOWN_QUESTIONMARK);
			if (unprintableMode == OPTION_UNPRINTABLE_CODE) {
				inner.write(tohex((c & 0xf000) >> 12));
				inner.write(tohex((c & 0x0f00) >> 8));
				inner.write(tohex((c & 0x00f0) >> 4));
				inner.write(tohex((c & 0x000f) >> 0));
			}
		}
	}
	private byte tohex(int i) {
		return (byte) (i < 10 ? ('0' + i) : ('a' + i - 10));
	}
	public byte getUnprintableMode() {
		return unprintableMode;
	}
	public void setUnprintableMode(byte unprintableMode) {
		this.unprintableMode = unprintableMode;
	}
	public boolean isError() {
		return error;
	}
	@Override
	public void println(String sb) {
		append(sb, 0, sb.length()).println();
	}

	@Override
	public void print(String sb) {
		append(sb, 0, sb.length());
	}
}
