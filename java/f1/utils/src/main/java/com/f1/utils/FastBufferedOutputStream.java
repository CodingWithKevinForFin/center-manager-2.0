/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UTFDataFormatException;

public class FastBufferedOutputStream extends FastFilterOutputStream implements FastDataOutput {
	protected byte buf[];

	protected int count;

	private long outCount;

	private void flushBuffer() throws IOException {
		if (count > 0) {
			out.write(buf, 0, count);
			incrementInnerWrote(count);
			count = 0;
		}
	}

	public void reset(OutputStream out) {
		count = 0;
		outCount = 0;
		setInner(out);
	}

	public FastBufferedOutputStream(OutputStream out) {
		this(out, 8192);
	}

	public FastBufferedOutputStream(OutputStream out, int size) {
		super(out);
		if (size < 128) {
			size = 128;
		}
		buf = new byte[size];
	}

	@Override
	public void write(int b) throws IOException {
		if (count >= buf.length) {
			flushBuffer();
		}
		buf[count++] = (byte) b;
	}

	@Override
	public void write(byte b[], int off, int len) throws IOException {
		if (len >= buf.length) {
			flushBuffer();
			out.write(b, off, len);
			incrementInnerWrote(len);
			return;
		}
		if (len > buf.length - count) {
			flushBuffer();
		}
		System.arraycopy(b, off, buf, count, len);
		count += len;
	}
	protected void incrementInnerWrote(int len) {
		outCount += len;
	}

	public long getTotalBytesWritten() {
		return outCount + count;
	}

	@Override
	public void flush() throws IOException {
		flushBuffer();
		out.flush();
	}
	private void ensureCanStore(int size) throws IOException {
		if (count + size > buf.length) {
			flushBuffer();
		}
	}
	public void writeBoolean(boolean v) throws IOException {
		writeByte(v ? 1 : 0);
	}

	public void writeByte(int v) throws IOException {
		ensureCanStore(1);
		write(v);
	}
	public void writeUnsignedByte(int v) throws IOException {
		ensureCanStore(1);
		write(v & 0xff);
	}

	public void writeShort(int v) throws IOException {
		ensureCanStore(2);
		buf[count++] = (byte) ((v >>> 8));
		buf[count++] = (byte) ((v >>> 0));
	}
	public void writeUnsignedShort(int v) throws IOException {
		ensureCanStore(2);
		buf[count++] = (byte) ((v >>> 8) & 0xFF);
		buf[count++] = (byte) ((v >>> 0) & 0xFF);
	}

	public void writeChar(int v) throws IOException {
		ensureCanStore(2);
		buf[count++] = (byte) ((v >>> 8));
		buf[count++] = (byte) ((v >>> 0));
	}

	public void writeInt(int v) throws IOException {
		ensureCanStore(4);
		buf[count++] = (byte) ((v >>> 24));
		buf[count++] = (byte) ((v >>> 16));
		buf[count++] = (byte) ((v >>> 8));
		buf[count++] = (byte) ((v >>> 0));
	}

	@Override
	public void writeInt3(int i) throws IOException {
		ensureCanStore(3);
		ByteHelper.writeInt3(i, buf, count);
		count += 3;
	}

	@Override
	public void writeLong5(long i) throws IOException {
		ensureCanStore(5);
		ByteHelper.writeLong5(i, buf, count);
		count += 5;
	}

	@Override
	public void writeLong6(long i) throws IOException {
		ensureCanStore(6);
		ByteHelper.writeLong6(i, buf, count);
		count += 6;
	}

	@Override
	public void writeLong7(long i) throws IOException {
		ensureCanStore(7);
		ByteHelper.writeLong7(i, buf, count);
		count += 7;
	}

	public void writeLong(long v) throws IOException {
		ensureCanStore(8);
		buf[count++] = (byte) (v >>> 56);
		buf[count++] = (byte) (v >>> 48);
		buf[count++] = (byte) (v >>> 40);
		buf[count++] = (byte) (v >>> 32);
		buf[count++] = (byte) (v >>> 24);
		buf[count++] = (byte) (v >>> 16);
		buf[count++] = (byte) (v >>> 8);
		buf[count++] = (byte) (v >>> 0);
	}

	public void writeFloat(float v) throws IOException {
		writeInt(Float.floatToIntBits(v));
	}

	public void writeDouble(double v) throws IOException {
		writeLong(Double.doubleToLongBits(v));
	}

	public void writeBytes(CharSequence s) throws IOException {
		int len = s.length();
		for (int i = 0; i < len; i++)
			write(s.charAt(i));
	}
	@Override
	public void writeBytes(String s) throws IOException {
		int len = s.length();
		for (int i = 0; i < len; i++)
			write(s.charAt(i));
	}

	@Override
	public void writeChars(String s) throws IOException {
		int len = s.length();
		for (int i = 0; i < len; i++) {
			writeChar(s.charAt(i));
		}

	}

	public void writeChars(CharSequence s) throws IOException {
		int len = s.length();
		for (int i = 0; i < len; i++)
			writeChar(s.charAt(i));

	}

	@Override
	public void writeUTF(String str) throws UTFDataFormatException, IOException {
		FastByteArrayDataOutputStream.writeUTF(str, this, false);
	}
	public void writeUTF(CharSequence str) throws UTFDataFormatException, IOException {
		FastByteArrayDataOutputStream.writeUTF(str, this, false);
	}
	/**
	 * Passing in a string with length at most java's native 65536 limit will be compatible with java's readUTF. For large strings you must use the 3forge readUTF(...). For large
	 * strings, the java's readUTF will throw a malformatted UTF exception
	 * 
	 * @param str
	 * @throws UTFDataFormatException
	 */
	public void writeUTFSupportLarge(CharSequence str) throws UTFDataFormatException, IOException {
		FastByteArrayDataOutputStream.writeUTF(str, this, true);
	}

	@Override
	public void write(short[] b, int off, int len) throws IOException {
		int count = this.count;
		int bufLen = this.buf.length;
		for (len += off; off < len; off++) {
			if (count + 2 > bufLen) {
				this.count = count;
				flushBuffer();
				count = this.count;
			}
			short v = b[off];
			buf[count++] = (byte) ((v >>> 8));
			buf[count++] = (byte) ((v >>> 0));
		}
		this.count = count;
	}
	@Override
	public void write(int[] b, int off, int len) throws IOException {
		int count = this.count;
		int bufLen = this.buf.length;
		for (len += off; off < len; off++) {
			if (count + 4 > bufLen) {
				this.count = count;
				flushBuffer();
				count = this.count;
			}
			int v = b[off];
			buf[count++] = (byte) ((v >>> 24));
			buf[count++] = (byte) ((v >>> 16));
			buf[count++] = (byte) ((v >>> 8));
			buf[count++] = (byte) ((v >>> 0));
		}
		this.count = count;
	}

	@Override
	public void write(long[] b, int off, int len) throws IOException {
		int count = this.count;
		int bufLen = this.buf.length;
		for (len += off; off < len; off++) {
			if (count + 8 > bufLen) {
				this.count = count;
				flushBuffer();
				count = this.count;
			}
			long v = b[off];
			buf[count++] = (byte) (v >>> 56);
			buf[count++] = (byte) (v >>> 48);
			buf[count++] = (byte) (v >>> 40);
			buf[count++] = (byte) (v >>> 32);
			buf[count++] = (byte) (v >>> 24);
			buf[count++] = (byte) (v >>> 16);
			buf[count++] = (byte) (v >>> 8);
			buf[count++] = (byte) (v >>> 0);
		}
		this.count = count;
	}

	@Override
	public void write(float[] b, int off, int len) throws IOException {
		int count = this.count;
		int bufLen = this.buf.length;
		for (len += off; off < len; off++) {
			if (count + 4 > bufLen) {
				this.count = count;
				flushBuffer();
				count = this.count;
			}
			int v = Float.floatToIntBits(b[off]);
			buf[count++] = (byte) ((v >>> 24));
			buf[count++] = (byte) ((v >>> 16));
			buf[count++] = (byte) ((v >>> 8));
			buf[count++] = (byte) ((v >>> 0));
		}
		this.count = count;
	}

	@Override
	public void write(double[] b, int off, int len) throws IOException {
		int count = this.count;
		int bufLen = this.buf.length;
		for (len += off; off < len; off++) {
			if (count + 8 > bufLen) {
				this.count = count;
				flushBuffer();
				count = this.count;
			}
			long v = Double.doubleToLongBits(b[off]);
			buf[count++] = (byte) (v >>> 56);
			buf[count++] = (byte) (v >>> 48);
			buf[count++] = (byte) (v >>> 40);
			buf[count++] = (byte) (v >>> 32);
			buf[count++] = (byte) (v >>> 24);
			buf[count++] = (byte) (v >>> 16);
			buf[count++] = (byte) (v >>> 8);
			buf[count++] = (byte) (v >>> 0);
		}
		this.count = count;
	}
	@Override
	public void write(char[] b, int off, int len) throws IOException {
		int count = this.count;
		int bufLen = this.buf.length;
		for (len += off; off < len; off++) {
			if (count + 2 > bufLen) {
				this.count = count;
				flushBuffer();
				count = this.count;
			}
			char v = b[off];
			buf[count++] = (byte) ((v >>> 8));
			buf[count++] = (byte) ((v >>> 0));
		}
		this.count = count;
	}

	@Override
	public void write(boolean[] b, int off, int len) throws IOException {
		ensureCanStore(len);
		int count = this.count;
		for (len += off; off < len; off++)
			buf[count++] = b[off] ? (byte) 1 : (byte) 0;
		this.count = count;
	}

	public void flushIfBuffered() throws IOException {
		if (count > 0)
			this.flush();
	}
	public int bufferedBytes() {
		return this.count;
	}

	public void unbuffer(int bytes) {
		this.count -= bytes;
	}
}
