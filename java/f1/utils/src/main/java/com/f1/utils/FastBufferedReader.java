package com.f1.utils;

import java.io.IOException;
import java.io.Reader;

/**
 * Non synchronized version of BufferedReader, also avoids string creating on each new readline by using a sink
 */
public class FastBufferedReader extends Reader {
	public static final int DEFAULT_BUFFER_SIZE = 8192;
	public static final int DEFAULT_LINE_SIZE = 80;

	private static final int INVALIDATED = -2;
	private static final int UNMARKED = -1;

	private Reader inner;
	private char buffer[];
	private int nChars, nextChar;

	private int markedChar = UNMARKED;
	private int readAheadLimit = 0;
	private boolean skipLF = false;
	private boolean markedSkipLF = false;

	public FastBufferedReader(Reader in, int bufferSize) {
		super(in);
		if (bufferSize <= 0)
			throw new IndexOutOfBoundsException("Buffer size <= 0: " + bufferSize);
		this.inner = in;
		buffer = new char[bufferSize];
	}

	public FastBufferedReader(Reader in) {
		this(in, DEFAULT_BUFFER_SIZE);
	}

	private void assertOpen() throws IOException {
		if (inner == null)
			throw new IOException("Stream closed");
	}

	private void fill() throws IOException {
		int dst;
		if (markedChar <= UNMARKED) {
			dst = 0;
		} else {
			int delta = nextChar - markedChar;
			if (delta >= readAheadLimit) {
				markedChar = INVALIDATED;
				readAheadLimit = 0;
				dst = 0;
			} else {
				if (readAheadLimit <= buffer.length) {
					System.arraycopy(buffer, markedChar, buffer, 0, delta);
					markedChar = 0;
					dst = delta;
				} else {
					char ncb[] = new char[readAheadLimit];
					System.arraycopy(buffer, markedChar, ncb, 0, delta);
					buffer = ncb;
					markedChar = 0;
					dst = delta;
				}
				nextChar = nChars = delta;
			}
		}

		int n;
		do {
			n = inner.read(buffer, dst, buffer.length - dst);
		} while (n == 0);
		if (n > 0) {
			nChars = dst + n;
			nextChar = dst;
		}
	}

	@Override
	public int read() throws IOException {
		assertOpen();
		for (;;) {
			if (nextChar >= nChars) {
				fill();
				if (nextChar >= nChars)
					return -1;
			}
			if (skipLF) {
				skipLF = false;
				if (buffer[nextChar] == '\n') {
					nextChar++;
					continue;
				}
			}
			return buffer[nextChar++];
		}
	}

	private int readInner(char[] cbuf, int off, int len) throws IOException {
		if (nextChar >= nChars) {
			if (len >= buffer.length && markedChar <= UNMARKED && !skipLF) {
				return inner.read(cbuf, off, len);
			}
			fill();
		}
		if (nextChar >= nChars)
			return -1;
		if (skipLF) {
			skipLF = false;
			if (buffer[nextChar] == '\n') {
				nextChar++;
				if (nextChar >= nChars)
					fill();
				if (nextChar >= nChars)
					return -1;
			}
		}
		int n = Math.min(len, nChars - nextChar);
		System.arraycopy(buffer, nextChar, cbuf, off, n);
		nextChar += n;
		return n;
	}

	@Override
	public int read(char cbuf[], int off, int len) throws IOException {
		assertOpen();
		AH.assertStartAndLength(cbuf.length, off, len);
		if (len == 0)
			return 0;
		int n = readInner(cbuf, off, len);
		if (n > 0)
			while ((n < len) && inner.ready()) {
				int n1 = readInner(cbuf, off + n, len - n);
				if (n1 <= 0)
					break;
				n += n1;
			}
		return n;
	}

	private boolean readLine(boolean ignoreLF, StringBuilder s) throws IOException {
		int startChar;

		assertOpen();
		boolean omitLF = ignoreLF || skipLF;

		for (;;) {

			if (nextChar >= nChars)
				fill();
			if (nextChar >= nChars) {
				if (s != null && s.length() > 0)
					return true;
				else
					return false;
			}
			boolean eol = false;
			char c = 0;
			int i;

			if (omitLF && (buffer[nextChar] == '\n'))
				nextChar++;
			skipLF = false;
			omitLF = false;

			charLoop: for (i = nextChar; i < nChars; i++) {
				c = buffer[i];
				if ((c == '\n') || (c == '\r')) {
					eol = true;
					break charLoop;
				}
			}

			startChar = nextChar;
			nextChar = i;

			if (eol) {
				s.append(buffer, startChar, i - startChar);
				nextChar++;
				if (c == '\r') {
					skipLF = true;
				}
				return true;
			}

			s.append(buffer, startChar, i - startChar);
		}
	}

	final StringBuilder sb = new StringBuilder();

	//false = EOF
	public boolean readLine(StringBuilder sink) throws IOException {
		return readLine(false, sink);
	}
	public String readLine() throws IOException {
		sb.setLength(0);
		if (readLine(false, sb))
			return SH.toStringAndClear(sb);
		sb.setLength(0);
		return null;
	}
	@Override
	public long skip(long n) throws IOException {
		if (n < 0L) {
			throw new IllegalArgumentException("skip value is negative");
		}
		assertOpen();
		long r = n;
		while (r > 0) {
			if (nextChar >= nChars)
				fill();
			if (nextChar >= nChars)
				break;
			if (skipLF) {
				skipLF = false;
				if (buffer[nextChar] == '\n') {
					nextChar++;
				}
			}
			long d = nChars - nextChar;
			if (r <= d) {
				nextChar += r;
				r = 0;
				break;
			} else {
				r -= d;
				nextChar = nChars;
			}
		}
		return n - r;
	}

	@Override
	public boolean ready() throws IOException {
		assertOpen();

		if (skipLF) {
			if (nextChar >= nChars && inner.ready()) {
				fill();
			}
			if (nextChar < nChars) {
				if (buffer[nextChar] == '\n')
					nextChar++;
				skipLF = false;
			}
		}
		return (nextChar < nChars) || inner.ready();
	}

	@Override
	public boolean markSupported() {
		return true;
	}

	@Override
	public void mark(int readAheadLimit) throws IOException {
		if (readAheadLimit < 0) {
			throw new IllegalArgumentException("Read-ahead limit < 0");
		}
		assertOpen();
		this.readAheadLimit = readAheadLimit;
		markedChar = nextChar;
		markedSkipLF = skipLF;
	}

	@Override
	public void reset() throws IOException {
		assertOpen();
		if (markedChar < 0)
			throw new IOException(markedChar == INVALIDATED ? "invalid" : "not marked, call mark() first");
		nextChar = markedChar;
		skipLF = markedSkipLF;
	}

	@Override
	public void close() throws IOException {
		if (inner == null)
			return;
		inner.close();
		inner = null;
		buffer = null;
	}

}
