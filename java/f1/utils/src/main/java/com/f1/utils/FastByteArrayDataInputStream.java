/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.UTFDataFormatException;

public class FastByteArrayDataInputStream extends FastByteArrayInputStream implements FastDataInput {

	final private byte bytearr[] = new byte[127];
	final private char chararr[] = new char[127];

	public FastByteArrayDataInputStream(byte[] buf, int offset, int length) {
		super(buf, offset, length);
	}

	public FastByteArrayDataInputStream(byte[] buf) {
		super(buf);
	}

	@Override
	public void readFully(byte[] b) throws IOException {
		readFully(b, 0, b.length);

	}
	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		ensureAvailable(len);
		System.arraycopy(buf, pos, b, off, len);
		pos += len;
	}
	@Override
	public void skipBytesFully(int bytes) throws IOException {
		ensureAvailable(bytes);
		pos += bytes;
	}

	@Override
	public int skipBytes(int n) throws IOException {
		if (n + pos > count)
			n = count - pos;
		super.pos += n;
		return n;
	}

	@Override
	public boolean readBoolean() throws IOException {
		ensureAvailable(1);
		return (buf[pos++] & 0xff) != 0;
	}

	@Override
	public byte readByte() throws IOException {
		ensureAvailable(1);
		return (byte) (buf[pos++] & 0xff);
	}

	@Override
	public int readUnsignedByte() throws IOException {
		ensureAvailable(1);
		return (buf[pos++] & 0xff);
	}

	@Override
	public short readShort() throws IOException {
		ensureAvailable(2);
		return (short) (((buf[pos++] & 0xff) << 8) + ((buf[pos++] & 0xff) << 0));
	}

	@Override
	public int readUnsignedShort() throws IOException {
		ensureAvailable(2);
		return (((buf[pos++] & 0xff) << 8) + ((buf[pos++] & 0xff) << 0));
	}

	@Override
	public char readChar() throws IOException {
		ensureAvailable(2);
		return (char) (((buf[pos++] & 0xff) << 8) + ((buf[pos++] & 0xff) << 0));
	}
	public int readInt3() throws EOFException {
		ensureAvailable(3);
		pos += 3;
		return ByteHelper.readInt3(buf, pos - 3);
	}

	@Override
	public int readInt() throws IOException {
		ensureAvailable(4);
		return ((buf[pos++] & 0xff) << 24) + ((buf[pos++] & 0xff) << 16) + ((buf[pos++] & 0xff) << 8) + ((buf[pos++] & 0xff) << 0);
	}
	public long readUnsignedInt() throws EOFException {
		ensureAvailable(4);
		return ((buf[pos++] & 0xffL) << 24) + ((buf[pos++] & 0xffL) << 16) + ((buf[pos++] & 0xffL) << 8) + ((buf[pos++] & 0xffL) << 0);
	}

	@Override
	public long readLong() throws IOException {
		ensureAvailable(8);
		return (((long) buf[pos++] << 56) + ((long) (buf[pos++] & 255) << 48) + ((long) (buf[pos++] & 255) << 40) + ((long) (buf[pos++] & 255) << 32)
				+ ((long) (buf[pos++] & 255) << 24) + ((buf[pos++] & 255) << 16) + ((buf[pos++] & 255) << 8) + ((buf[pos++] & 255) << 0));
	}
	public long readLong5() throws EOFException {
		ensureAvailable(5);
		pos += 5;
		return ByteHelper.readLong5(buf, pos - 5);
	}
	public long readLong6() throws EOFException {
		ensureAvailable(6);
		pos += 6;
		return ByteHelper.readLong6(buf, pos - 6);
	}
	public long readLong7() throws EOFException {
		ensureAvailable(7);
		pos += 7;
		return ByteHelper.readLong7(buf, pos - 7);
	}

	@Override
	public float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}

	@Override
	public double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}

	private char lineBuffer[];

	@Override
	public String readLine() throws IOException {
		InputStream in = this;
		char buf[] = lineBuffer;

		if (buf == null) {
			buf = lineBuffer = new char[128];
		}

		int room = buf.length;
		int offset = 0;
		int c;

		loop: while (true) {
			switch (c = read()) {
				case -1:
				case '\n':
					break loop;

				case '\r':
					int c2 = in.read();
					if ((c2 != '\n') && (c2 != -1)) {
						in = new PushbackInputStream(in);
						((PushbackInputStream) in).unread(c2);
					}
					break loop;

				default:
					if (--room < 0) {
						buf = new char[offset + 128];
						room = buf.length - offset - 1;
						System.arraycopy(lineBuffer, 0, buf, 0, offset);
						lineBuffer = buf;
					}
					buf[offset++] = (char) c;
					break;
			}
		}
		if ((c == -1) && (offset == 0)) {
			return null;
		}
		return String.copyValueOf(buf, 0, offset);
	}

	@Override
	public String readUTF() throws IOException {
		int utflen = readUnsignedShort();
		if (utflen == 0)
			return "";
		byte first = readByte();
		if (utflen == 1) {
			if (first == (byte) 128) {//this is a large string
				utflen = readInt();
				if (utflen <= 65535)
					throw new UTFDataFormatException("malformed input: Large header, expecting length above 65535: " + utflen);
				first = readByte();
			} else if (first == -1)
				return null;
			else
				return SH.toString((char) first);

		}

		this.ensureAvailable(utflen - 1);
		final byte[] bytearr = this.buf;
		final char[] chararr;
		if (utflen <= this.chararr.length) {
			chararr = this.chararr;
		} else
			chararr = new char[utflen];

		int c, char2, char3;
		int count = this.pos - 1;
		int chararr_count = 0;
		final int end = utflen + count;

		while (count < end) {
			c = (int) bytearr[count] & 0xff;
			switch (c >> 4) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
					count++;
					chararr[chararr_count++] = (char) c;
					break;
				case 12:
				case 13:
					count += 2;
					if (count > end)
						throw new UTFDataFormatException("malformed input: partial character at end");
					char2 = (int) bytearr[count - 1];
					if ((char2 & 0xC0) != 0x80)
						throw new UTFDataFormatException("malformed input around byte " + count);
					chararr[chararr_count++] = (char) (((c & 0x1F) << 6) | (char2 & 0x3F));
					break;
				case 14:
					count += 3;
					if (count > end)
						throw new UTFDataFormatException("malformed input: partial character at end");
					char2 = (int) bytearr[count - 2];
					char3 = (int) bytearr[count - 1];
					if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
						throw new UTFDataFormatException("malformed input around byte " + (count - 1));
					chararr[chararr_count++] = (char) (((c & 0x0F) << 12) | ((char2 & 0x3F) << 6) | ((char3 & 0x3F) << 0));
					break;
				default:
					throw new UTFDataFormatException("malformed input around byte " + count);
			}
		}
		this.pos = count;
		// The number of chars produced may be less than utflen
		return new String(chararr, 0, chararr_count);
	}
	public final static void skipUTF(FastDataInput in) throws IOException {
		int utflen = in.readUnsignedShort();
		if (utflen == 0)
			return;
		byte first = in.readByte();
		if (utflen == 1) {
			if (first == (byte) 128) {//this is a large string
				utflen = in.readInt();
				if (utflen <= 65535)
					throw new UTFDataFormatException("malformed input: Large header, expecting length above 65535: " + utflen);
				first = in.readByte();
			} else if (first == -1)
				return;
			else
				return;

		}
		in.skipBytesFully(utflen - 1);
	}
	public int getPosition() {
		return pos;
	}
	public final static String readUTF(DataInput in) throws IOException {
		int utflen = in.readUnsignedShort();
		if (utflen == 0)
			return "";
		byte first = in.readByte();
		if (utflen == 1) {
			if (first == (byte) 128) {//this is a large string
				utflen = in.readInt();
				if (utflen <= 65535)
					throw new UTFDataFormatException("malformed input: Large header, expecting length above 65535: " + utflen);
				first = in.readByte();
			} else if (first == -1)
				return null;
			else
				return SH.toString((char) first);

		}

		final byte[] bytearr;
		final char[] chararr;
		if (in instanceof FastByteArrayDataInputStream && utflen <= 65536) {
			FastByteArrayDataInputStream dis = (FastByteArrayDataInputStream) in;
			if (dis.bytearr.length < utflen) {
				final int bufsize = utflen;
				bytearr = new byte[bufsize];
				chararr = new char[bufsize];
			} else {
				chararr = dis.chararr;
				bytearr = dis.bytearr;
			}
		} else if (in instanceof FastBufferedInputStream && utflen <= 65536) {
			FastBufferedInputStream dis = (FastBufferedInputStream) in;
			if (dis.bytearr.length < utflen) {
				final int bufsize = utflen;
				bytearr = new byte[bufsize];
				chararr = new char[bufsize];
			} else {
				chararr = dis.chararr;
				bytearr = dis.bytearr;
			}
		} else {
			bytearr = new byte[utflen];
			chararr = new char[utflen];
		}

		int c, char2, char3;
		int count = 0;
		int chararr_count = 0;
		bytearr[0] = first;

		in.readFully(bytearr, 1, utflen - 1);

		while (count < utflen) {
			c = (int) bytearr[count] & 0xff;
			if (c > 127)
				break;
			count++;
			chararr[chararr_count++] = (char) c;
		}

		while (count < utflen) {
			c = (int) bytearr[count] & 0xff;
			switch (c >> 4) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
					count++;
					chararr[chararr_count++] = (char) c;
					break;
				case 12:
				case 13:
					count += 2;
					if (count > utflen)
						throw new UTFDataFormatException("malformed input: partial character at end");
					char2 = (int) bytearr[count - 1];
					if ((char2 & 0xC0) != 0x80)
						throw new UTFDataFormatException("malformed input around byte " + count);
					chararr[chararr_count++] = (char) (((c & 0x1F) << 6) | (char2 & 0x3F));
					break;
				case 14:
					count += 3;
					if (count > utflen)
						throw new UTFDataFormatException("malformed input: partial character at end");
					char2 = (int) bytearr[count - 2];
					char3 = (int) bytearr[count - 1];
					if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
						throw new UTFDataFormatException("malformed input around byte " + (count - 1));
					chararr[chararr_count++] = (char) (((c & 0x0F) << 12) | ((char2 & 0x3F) << 6) | ((char3 & 0x3F) << 0));
					break;
				default:
					throw new UTFDataFormatException("malformed input around byte " + count);
			}
		}
		// The number of chars produced may be less than utflen
		return new String(chararr, 0, chararr_count);
	}

	@Override
	public short[] readFully(short[] b, int off, int len) throws IOException {
		ensureAvailable(len * 2);
		int pos = this.pos;
		for (len += off; off < len; off++)
			b[off] = (short) (((buf[pos++] & 0xff) << 8) + ((buf[pos++] & 0xff) << 0));
		this.pos = pos;
		return b;
	}

	@Override
	public int[] readFully(int[] b, int off, int len) throws IOException {
		ensureAvailable(len * 2);
		int pos = this.pos;
		for (len += off; off < len; off++)
			b[off] = ((buf[pos++] & 0xff) << 24) + ((buf[pos++] & 0xff) << 16) + ((buf[pos++] & 0xff) << 8) + ((buf[pos++] & 0xff) << 0);
		this.pos = pos;
		return b;
	}

	@Override
	public long[] readFully(long[] b, int off, int len) throws IOException {
		ensureAvailable(len * 8);
		int pos = this.pos;
		for (len += off; off < len; off++)
			b[off] = (((long) buf[pos++] << 56) + ((long) (buf[pos++] & 255) << 48) + ((long) (buf[pos++] & 255) << 40) + ((long) (buf[pos++] & 255) << 32)
					+ ((long) (buf[pos++] & 255) << 24) + ((buf[pos++] & 255) << 16) + ((buf[pos++] & 255) << 8) + ((buf[pos++] & 255) << 0));
		this.pos = pos;
		return b;
	}

	@Override
	public float[] readFully(float[] b, int off, int len) throws IOException {
		ensureAvailable(len * 2);
		int pos = this.pos;
		for (len += off; off < len; off++)
			b[off] = Float.intBitsToFloat(((buf[pos++] & 0xff) << 24) + ((buf[pos++] & 0xff) << 16) + ((buf[pos++] & 0xff) << 8) + ((buf[pos++] & 0xff) << 0));
		this.pos = pos;
		return b;
	}

	@Override
	public double[] readFully(double[] b, int off, int len) throws IOException {
		ensureAvailable(len * 8);
		int pos = this.pos;
		for (len += off; off < len; off++)
			b[off] = Double.longBitsToDouble((((long) buf[pos++] << 56) + ((long) (buf[pos++] & 255) << 48) + ((long) (buf[pos++] & 255) << 40) + ((long) (buf[pos++] & 255) << 32)
					+ ((long) (buf[pos++] & 255) << 24) + ((buf[pos++] & 255) << 16) + ((buf[pos++] & 255) << 8) + ((buf[pos++] & 255) << 0)));
		this.pos = pos;
		return b;
	}

	@Override
	public char[] readFully(char[] b, int off, int len) throws IOException {
		ensureAvailable(len * 2);
		int pos = this.pos;
		for (len += off; off < len; off++)
			b[off] = (char) (((buf[pos++] & 0xff) << 8) + ((buf[pos++] & 0xff) << 0));
		this.pos = pos;
		return b;
	}

	@Override
	public boolean[] readFully(boolean[] b, int off, int len) throws IOException {
		ensureAvailable(len);
		int pos = this.pos;
		for (len += off; off < len; off++)
			b[off] = (buf[pos++] & 0xff) != 0;
		this.pos = pos;
		return b;
	}

	@Override
	public void skipUTF() throws IOException {
		skipUTF(this);
	}

}
