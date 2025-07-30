package com.f1.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;

public class FastStreamEncoder extends Writer {

	@Override
	public void write(char[] cbuf) throws IOException {
		write(cbuf, 0, cbuf.length);
	}

	@Override
	public void write(String str) throws IOException {
		super.write(str);
	}

	@Override
	public Writer append(char c) throws IOException {
		write(c);
		return this;
	}

	@Override
	public Writer append(CharSequence x0, int x1, int x2) throws IOException {
		return super.append(x0, x1, x2);
	}

	@Override
	public Writer append(CharSequence x0) throws IOException {
		return super.append(x0);
	}

	private static final int DEFAULT_BYTE_BUFFER_SIZE = 8192;

	private volatile boolean isOpen = true;

	private void ensureOpen() throws IOException {
		if (!isOpen)
			throw new IOException("Stream closed");
	}

	public static FastStreamEncoder forOutputStreamWriter(OutputStream out, String charsetName) throws UnsupportedEncodingException {
		String csn = charsetName;
		if (csn == null)
			csn = Charset.defaultCharset().name();
		try {
			if (Charset.isSupported(csn))
				return new FastStreamEncoder(out, Charset.forName(csn));
		} catch (IllegalCharsetNameException x) {
		}
		throw new UnsupportedEncodingException(csn);
	}

	public static FastStreamEncoder forOutputStreamWriter(OutputStream out, Charset cs) {
		return new FastStreamEncoder(out, cs);
	}

	public static FastStreamEncoder forOutputStreamWriter(OutputStream out, CharsetEncoder enc) {
		return new FastStreamEncoder(out, enc);
	}

	public static FastStreamEncoder forEncoder(WritableByteChannel ch, CharsetEncoder enc, int minBufferCap) {
		return new FastStreamEncoder(ch, enc, minBufferCap);
	}

	public String getEncoding() {
		if (isOpen())
			return encodingName();
		return null;
	}

	public void flushBuffer() throws IOException {
		if (isOpen())
			implFlushBuffer();
		else
			throw new IOException("Stream closed");
	}

	private char[] cbuf = new char[1];

	public void write(int c) throws IOException {
		implWrite((char) c);
	}

	public void write(char cbuf[], int off, int len) throws IOException {
		ensureOpen();
		if ((off < 0) || (off > cbuf.length) || (len < 0) || ((off + len) > cbuf.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}
		implWrite(cbuf, off, len);
	}
	public void write(CharSequence cbuf, int off, int len) throws IOException {
		ensureOpen();
		if ((off < 0) || (off > cbuf.length()) || (len < 0) || ((off + len) > cbuf.length()) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}
		implWrite(cbuf, off, len);
	}

	private char[] cb = new char[1024];

	public void write(String s, int off, int len) throws IOException {
		ensureOpen();
		if (cb.length < len)
			cb = new char[len];
		s.getChars(off, off + len, cb, 0);
		write(cb, 0, len);
	}

	public void flush() throws IOException {
		ensureOpen();
		implFlush();
	}

	public void close() throws IOException {
		if (!isOpen)
			return;
		implClose();
		isOpen = false;
	}

	private boolean isOpen() {
		return isOpen;
	}

	private Charset cs;
	private CharsetEncoder encoder;
	private ByteBuffer bb;

	private final OutputStream out;
	private WritableByteChannel ch;

	private boolean haveLeftoverChar = false;
	private char leftoverChar;
	private CharBuffer lcb = null;

	private FastStreamEncoder(OutputStream out, Charset cs) {
		this(out, cs.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE));
	}

	private FastStreamEncoder(OutputStream out, CharsetEncoder enc) {
		this.out = out;
		this.ch = null;
		this.cs = enc.charset();
		this.encoder = enc;
		bb = ByteBuffer.allocate(DEFAULT_BYTE_BUFFER_SIZE);
	}

	private FastStreamEncoder(WritableByteChannel ch, CharsetEncoder enc, int mbc) {
		this.out = null;
		this.ch = ch;
		this.cs = enc.charset();
		this.encoder = enc;
		this.bb = ByteBuffer.allocate(mbc < 0 ? DEFAULT_BYTE_BUFFER_SIZE : mbc);
	}

	private void writeBytes() throws IOException {
		bb.flip();
		int lim = bb.limit();
		int pos = bb.position();
		assert (pos <= lim);
		int rem = (pos <= lim ? lim - pos : 0);

		if (rem > 0) {
			if (ch != null) {
				if (ch.write(bb) != rem)
					assert false : rem;
			} else {
				out.write(bb.array(), bb.arrayOffset() + pos, rem);
			}
		}
		bb.clear();
	}

	private void flushLeftoverChar(CharBuffer cb, boolean endOfInput) throws IOException {
		if (!haveLeftoverChar && !endOfInput)
			return;
		if (lcb == null)
			lcb = CharBuffer.allocate(2);
		else
			lcb.clear();
		if (haveLeftoverChar)
			lcb.put(leftoverChar);
		if ((cb != null) && cb.hasRemaining())
			lcb.put(cb.get());
		lcb.flip();
		while (lcb.hasRemaining() || endOfInput) {
			CoderResult cr = encoder.encode(lcb, bb, endOfInput);
			if (cr.isUnderflow()) {
				if (lcb.hasRemaining()) {
					leftoverChar = lcb.get();
					if (cb != null && cb.hasRemaining())
						flushLeftoverChar(cb, endOfInput);
					return;
				}
				break;
			}
			if (cr.isOverflow()) {
				assert bb.position() > 0;
				writeBytes();
				continue;
			}
			cr.throwException();
		}
		haveLeftoverChar = false;
	}

	private final char buf[] = new char[1];

	void implWrite(char c) throws IOException {
		buf[0] = c;
		write(buf);
	}

	void implWrite(char cbuf[], int off, int len) throws IOException {
		CharBuffer cb = CharBuffer.wrap(cbuf, off, len);

		if (haveLeftoverChar)
			flushLeftoverChar(cb, false);

		while (cb.hasRemaining()) {
			CoderResult cr = encoder.encode(cb, bb, false);
			if (cr.isUnderflow()) {
				assert (cb.remaining() <= 1) : cb.remaining();
				if (cb.remaining() == 1) {
					haveLeftoverChar = true;
					leftoverChar = cb.get();
				}
				break;
			}
			if (cr.isOverflow()) {
				assert bb.position() > 0;
				writeBytes();
				continue;
			}
			cr.throwException();
		}
	}
	void implWrite(CharSequence cbuf, int off, int len) throws IOException {
		CharBuffer cb = CharBuffer.wrap(cbuf, off, len);

		if (haveLeftoverChar)
			flushLeftoverChar(cb, false);

		while (cb.hasRemaining()) {
			CoderResult cr = encoder.encode(cb, bb, false);
			if (cr.isUnderflow()) {
				assert (cb.remaining() <= 1) : cb.remaining();
				if (cb.remaining() == 1) {
					haveLeftoverChar = true;
					leftoverChar = cb.get();
				}
				break;
			}
			if (cr.isOverflow()) {
				assert bb.position() > 0;
				writeBytes();
				continue;
			}
			cr.throwException();
		}
	}

	void implFlushBuffer() throws IOException {
		if (bb.position() > 0)
			writeBytes();
	}

	void implFlush() throws IOException {
		implFlushBuffer();
		if (out != null)
			out.flush();
	}

	void implClose() throws IOException {
		flushLeftoverChar(null, true);
		try {
			for (;;) {
				CoderResult cr = encoder.flush(bb);
				if (cr.isUnderflow())
					break;
				if (cr.isOverflow()) {
					assert bb.position() > 0;
					writeBytes();
					continue;
				}
				cr.throwException();
			}

			if (bb.position() > 0)
				writeBytes();
			if (ch != null)
				ch.close();
			else
				out.close();
		} catch (IOException x) {
			encoder.reset();
			throw x;
		}
	}

	String encodingName() {
		return cs.name();
	}
}
