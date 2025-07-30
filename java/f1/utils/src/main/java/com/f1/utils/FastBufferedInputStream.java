/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class FastBufferedInputStream extends FastFilterInputStream implements FastDataInput {
	final protected byte bytearr[] = new byte[127];
	final protected char chararr[] = new char[127];
	private static int defaultBufferSize = 8192;

	protected volatile byte buf[];

	private static final AtomicReferenceFieldUpdater<FastBufferedInputStream, byte[]> bufUpdater = AtomicReferenceFieldUpdater.newUpdater(FastBufferedInputStream.class,
			byte[].class, "buf");

	protected int count;

	protected int pos;

	protected int markpos = -1;

	protected int marklimit;

	private long innerRead = 0;

	public void reset(InputStream in) {
		super.setInner(in);
		count = pos = marklimit = 0;
		this.innerRead = 0;
	}
	public void resetBuffer() {
		count = pos = marklimit = 0;
	}
	private InputStream getInIfOpen() throws IOException {
		InputStream input = in;
		if (input == null)
			throw new IOException("Stream closed");
		return input;
	}

	private byte[] getBufIfOpen() throws IOException {
		byte[] buffer = buf;
		if (buffer == null)
			throw new IOException("Stream closed");
		return buffer;
	}

	public FastBufferedInputStream(InputStream in) {
		this(in, defaultBufferSize);
	}

	public FastBufferedInputStream(InputStream in, int size) {
		super(in);
		if (size <= 0)
			throw new IllegalArgumentException("Buffer size negative: " + size);
		buf = new byte[size];
	}

	private void fill() throws IOException {
		byte[] buffer = getBufIfOpen();
		if (markpos < 0)
			pos = 0;
		else if (pos >= buffer.length)
			if (markpos > 0) {
				int sz = pos - markpos;
				System.arraycopy(buffer, markpos, buffer, 0, sz);
				pos = sz;
				markpos = 0;
			} else if (buffer.length >= marklimit) {
				markpos = -1;
				pos = 0;
			} else {
				int nsz = pos * 2;
				if (nsz > marklimit)
					nsz = marklimit;
				byte nbuf[] = new byte[nsz];
				System.arraycopy(buffer, 0, nbuf, 0, pos);
				if (!bufUpdater.compareAndSet(this, buffer, nbuf)) {
					throw new IOException("Stream already closed");
				}
				buffer = nbuf;
			}
		count = pos;
		final int n = getInIfOpen().read(buffer, pos, buffer.length - pos);
		if (n > 0) {
			incrementInnerRead(n);
			count = n + pos;
		}
	}

	public int read() throws IOException {
		if (pos >= count) {
			fill();
			if (pos >= count)
				return -1;
		}
		return getBufIfOpen()[pos++] & 0xff;
	}

	public byte readByte() throws IOException {
		if (pos >= count) {
			fill();
			if (pos >= count)
				throw new EOFException();
		}
		return buf[pos++];
	}
	public int readUByte() throws IOException {
		return readByte() & 0xff;
	}

	private int read1(byte[] b, int off, int len) throws IOException {
		int avail = count - pos;
		if (avail <= 0) {
			if (len >= getBufIfOpen().length && markpos < 0) {
				int r = getInIfOpen().read(b, off, len);
				if (r > 0)
					incrementInnerRead(r);
				return r;
			}
			fill();
			avail = count - pos;
			if (avail <= 0)
				return -1;
		}
		int cnt = (avail < len) ? avail : len;
		System.arraycopy(getBufIfOpen(), pos, b, off, cnt);
		pos += cnt;
		return cnt;
	}

	@Override
	public int read(byte b[], int off, int len) throws IOException {
		getBufIfOpen();
		if ((off | len | (off + len) | (b.length - (off + len))) < 0) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return 0;
		}

		int n = 0;
		for (;;) {
			int nread = read1(b, off + n, len - n);
			if (nread <= 0)
				return (n == 0) ? nread : n;
			n += nread;
			if (n >= len)
				return n;
			InputStream input = in;
			if (input != null && input.available() <= 0)
				return n;
		}
	}

	@Override
	public void skipBytesFully(int bytes) throws IOException {
		getBufIfOpen();
		if (bytes > 0)
			IOH.skip(in, bytes);
	}
	@Override
	public long skip(long n) throws IOException {
		getBufIfOpen();
		if (n <= 0) {
			return 0;
		}
		long avail = count - pos;

		if (avail <= 0) {
			if (markpos < 0) {
				long r = getInIfOpen().skip(n);
				incrementInnerRead(r);
				return r;
			}
			fill();
			avail = count - pos;
			if (avail <= 0)
				return 0;
		}

		long skipped = (avail < n) ? avail : n;
		pos += skipped;
		return skipped;
	}

	protected void incrementInnerRead(long r) {
		innerRead += r;
	}
	@Override
	public int available() throws IOException {
		return getInIfOpen().available() + (count - pos);
	}
	public int availableBuffer() {
		return (count - pos);
	}

	public long getBytesConsumed() {
		return innerRead - count + pos;
	}

	@Override
	public void mark(int readlimit) {
		marklimit = readlimit;
		markpos = pos;
	}

	@Override
	public void reset() throws IOException {
		getBufIfOpen(); // Cause exception if closed
		if (markpos < 0)
			throw new IOException("Resetting to invalid mark");
		pos = markpos;
	}

	public boolean markSupported() {
		return true;
	}

	@Override
	public void close() throws IOException {
		byte[] buffer;
		while ((buffer = buf) != null) {
			if (bufUpdater.compareAndSet(this, buffer, null)) {
				InputStream input = in;
				in = null;
				if (input != null)
					input.close();
				return;
			}
			// Else retry in case a new buf was CASed in fill()
		}
	}
	@Override
	public void readFully(byte[] b) throws IOException {
		readFully(b, 0, b.length);

	}
	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		IOH.readData(this, b, off, len);
	}

	@Override
	public int skipBytes(int n) throws IOException {
		return (int) this.skip(n);
	}

	@Override
	public boolean readBoolean() throws IOException {
		return readUByte() != 0;
	}

	@Override
	public int readUnsignedByte() throws IOException {
		return readUByte();
	}

	@Override
	public short readShort() throws IOException {
		return (short) ((readUByte() << 8) + (readUByte() << 0));
	}

	@Override
	public int readUnsignedShort() throws IOException {
		return ((readUByte() << 8) + (readUByte() << 0));
	}
	public long readUnsignedInt() throws IOException {
		return MH.toUnsignedInt(readInt());
	}

	@Override
	public char readChar() throws IOException {
		return (char) ((readUByte() << 8) + (readUByte() << 0));
	}

	@Override
	public int readInt() throws IOException {
		return (readUByte() << 24) + (readUByte() << 16) + (readUByte() << 8) + (readUByte() << 0);
	}

	@Override
	public long readLong() throws IOException {
		return (((long) readByte() << 56) + ((long) readUByte() << 48) + ((long) readUByte() << 40) + ((long) readUByte() << 32) + ((long) readUByte() << 24) + (readUByte() << 16)
				+ (readUByte() << 8) + (readUByte() << 0));
	}

	@Override
	public float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}

	@Override
	public double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}
	@Override
	public String readUTF() throws IOException {
		return FastByteArrayDataInputStream.readUTF(this);
	}

	@Override
	public String readLine() throws IOException {
		throw new UnsupportedOperationException();
	}
	@Override
	public short[] readFully(short[] b, int off, int len) throws IOException {
		for (int i = 0; i < len; i++)
			b[off + i] = readShort();
		return b;
	}
	@Override
	public int[] readFully(int[] b, int off, int len) throws IOException {
		for (int i = 0; i < len; i++)
			b[off + i] = readInt();
		return b;
	}
	@Override
	public long[] readFully(long[] b, int off, int len) throws IOException {
		for (int i = 0; i < len; i++)
			b[off + i] = readLong();
		return b;
	}
	@Override
	public float[] readFully(float[] b, int off, int len) throws IOException {
		for (int i = 0; i < len; i++)
			b[off + i] = readFloat();
		return b;
	}
	@Override
	public double[] readFully(double[] b, int off, int len) throws IOException {
		for (int i = 0; i < len; i++)
			b[off + i] = readDouble();
		return b;
	}
	@Override
	public char[] readFully(char[] b, int off, int len) throws IOException {
		for (int i = 0; i < len; i++)
			b[off + i] = readChar();
		return b;
	}
	@Override
	public boolean[] readFully(boolean[] b, int off, int len) throws IOException {
		for (int i = 0; i < len; i++)
			b[off + i] = readBoolean();
		return b;
	}
	@Override
	public int readInt3() throws IOException {
		int r = (readUByte() << 16) + (readUByte() << 8) + (readUByte() << 0);
		return r > MH.MAX_VALUE3 ? MH.MIN_VALUE3 * 2 + r : r;
	}
	@Override
	public long readLong5() throws IOException {
		long r = ((long) readUByte() << 32) + ((long) readUByte() << 24) + (readUByte() << 16) + (readUByte() << 8) + (readUByte() << 0);
		return r > MH.MAX_VALUE5 ? MH.MIN_VALUE5 * 2 + r : r;
	}
	@Override
	public long readLong6() throws IOException {
		long r = ((long) readUByte() << 40) + ((long) readUByte() << 32) + ((long) readUByte() << 24) + (readUByte() << 16) + (readUByte() << 8) + (readUByte() << 0);
		return r > MH.MAX_VALUE6 ? MH.MIN_VALUE6 * 2 + r : r;
	}
	@Override
	public long readLong7() throws IOException {
		long r = ((long) readUByte() << 48) + ((long) readUByte() << 40) + ((long) readUByte() << 32) + ((long) readUByte() << 24) + (readUByte() << 16) + (readUByte() << 8)
				+ (readUByte() << 0);
		return r > MH.MAX_VALUE7 ? MH.MIN_VALUE7 * 2 + r : r;
	}
	@Override
	public void skipUTF() throws IOException {
		FastByteArrayDataInputStream.skipUTF(this);
	}
}
