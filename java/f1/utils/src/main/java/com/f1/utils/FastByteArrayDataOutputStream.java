/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UTFDataFormatException;

public class FastByteArrayDataOutputStream extends FastByteArrayOutputStream implements FastDataOutput {

	@Override
	public void writeBoolean(boolean v) {
		ensureCanStore(1);
		buf[count++] = v ? (byte) 1 : (byte) 0;
	}

	@Override
	public void writeByte(int v) {
		ensureCanStore(1);
		write(v);
	}

	@Override
	public void writeShort(int v) {
		ensureCanStore(2);
		buf[count++] = (byte) ((v >>> 8));
		buf[count++] = (byte) ((v >>> 0));
	}

	public void writeUnsignedShort(int v) throws IOException {
		ensureCanStore(2);
		buf[count++] = (byte) ((v >>> 8) & 0xFF);
		buf[count++] = (byte) ((v >>> 0) & 0xFF);
	}

	@Override
	public void writeChar(int v) {
		ensureCanStore(2);
		buf[count++] = (byte) ((v >>> 8));
		buf[count++] = (byte) ((v >>> 0));
	}

	public void writeChars(char[] s) {
		ensureCanStore(s.length * 2);
		for (int v : s) {
			buf[count++] = (byte) (v >>> 8);
			buf[count++] = (byte) (v >>> 0);
		}
	}

	@Override
	public void writeInt(int v) {
		ensureCanStore(4);
		buf[count++] = (byte) ((v >>> 24));
		buf[count++] = (byte) ((v >>> 16));
		buf[count++] = (byte) ((v >>> 8));
		buf[count++] = (byte) ((v >>> 0));
	}

	@Override
	public void writeInt3(int i) {
		ensureCanStore(3);
		ByteHelper.writeInt3(i, buf, count);
		count += 3;
	}

	@Override
	public void writeLong5(long i) {
		ensureCanStore(5);
		ByteHelper.writeLong5(i, buf, count);
		count += 5;
	}

	@Override
	public void writeLong6(long i) {
		ensureCanStore(6);
		ByteHelper.writeLong6(i, buf, count);
		count += 6;
	}

	@Override
	public void writeLong7(long i) {
		ensureCanStore(7);
		ByteHelper.writeLong7(i, buf, count);
		count += 7;
	}

	@Override
	public void writeLong(long v) {
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

	@Override
	public void writeFloat(float v) {
		writeInt(Float.floatToIntBits(v));
	}

	@Override
	public void writeDouble(double v) {
		writeLong(Double.doubleToLongBits(v));
	}

	public void writeBytes(byte v[]) {
		ensureCanStore(v.length);
		for (byte b : v)
			buf[count++] = b;
	}
	@Override
	public void writeBytes(String s) {
		int len = s.length();
		ensureCanStore(len);
		for (int i = 0; i < len; i++)
			buf[count++] = (byte) s.charAt(i);
	}

	public void writeBytes(CharSequence s) {
		int len = s.length();
		ensureCanStore(len);
		for (int i = 0; i < len; i++)
			buf[count++] = (byte) s.charAt(i);
	}
	public void writeBytes(char[] s) {
		int len = s.length;
		ensureCanStore(len);
		for (int i = 0; i < len; i++)
			buf[count++] = (byte) s[i];
	}

	@Override
	public void writeChars(String s) {
		writeChars((CharSequence) s);
	}
	public void writeChars(CharSequence s) {
		int len = s.length();
		ensureCanStore(len * 2);
		for (int i = 0; i < len; i++) {
			int v = s.charAt(i);
			buf[count++] = (byte) (v >>> 8);
			buf[count++] = (byte) (v >>> 0);
		}

	}

	@Override
	public void writeUTF(String str) throws UTFDataFormatException {
		try {
			writeUTF2(str, this, false);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	public void writeUTF(CharSequence str) throws UTFDataFormatException {
		try {
			writeUTF2(str, this, false);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	/**
	 * Passing in a string with length at most java's native 65536 limit will be compatible with java's readUTF. For large strings you must use the 3forge readUTF(...). For large
	 * strings, the java's readUTF will throw a malformatted UTF exception
	 * 
	 * @param str
	 * @throws UTFDataFormatException
	 */
	@Override
	public void writeUTFSupportLarge(CharSequence str) throws UTFDataFormatException {
		try {
			writeUTF2(str, this, true);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	@Override
	public final int size() {
		return count;
	}

	private static byte UTF_NULL_HEADER[] = new byte[3];
	static {
		UTF_NULL_HEADER[0] = (byte) 0;
		UTF_NULL_HEADER[1] = (byte) 1;
		UTF_NULL_HEADER[2] = (byte) -1;//this is a malformatted UTF, so acts as a header to let the reader know this is a null string
	}

	private void writeUTF2(CharSequence str, OutputStream out, boolean supportLarge) throws IOException {
		if (str == null && supportLarge) {
			out.write(UTF_NULL_HEADER);
			return;
		}
		int strlen = str.length();
		int utflen = strlen;
		int c, count = this.count;

		for (int i = 0; i < strlen; i++) {
			c = str.charAt(i);
			if (c < 0x0001 || c > 0x007F)
				utflen += c > 0x07FF ? 2 : 1;
		}

		int finalLength;
		final byte[] bytearr;
		if (utflen > 65535) {
			if (!supportLarge)
				throw new UTFDataFormatException("encoded string too long: " + utflen + " bytes exceeds 65535 bytes");
			finalLength = utflen + 7;
			ensureCanStore(finalLength);
			bytearr = this.buf;
			bytearr[count++] = (byte) 0;
			bytearr[count++] = (byte) 1;
			bytearr[count++] = (byte) 128;//this is a malformatted UTF, so acts as a header to let the reader know this is a large string
			bytearr[count++] = (byte) ((utflen >>> 24));
			bytearr[count++] = (byte) ((utflen >>> 16));
			bytearr[count++] = (byte) ((utflen >>> 8));
			bytearr[count++] = (byte) ((utflen >>> 0));
		} else {
			finalLength = utflen + 2;
			ensureCanStore(finalLength);
			bytearr = this.buf;
			bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
			bytearr[count++] = (byte) ((utflen >>> 0) & 0xFF);
		}
		if (strlen == utflen) {
			for (int i = 0; i < strlen; i++)
				bytearr[count++] = (byte) str.charAt(i);
		} else {
			for (int i = 0; i < strlen; i++) {
				c = str.charAt(i);
				if ((c >= 0x0001) && (c <= 0x007F)) {
					bytearr[count++] = (byte) c;
				} else if (c > 0x07FF) {
					bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
					bytearr[count++] = (byte) (0x80 | ((c >> 6) & 0x3F));
					bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
				} else {
					bytearr[count++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
					bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
				}
			}
		}
		this.count = count;
	}
	public static int getUTFLength(CharSequence str, boolean supportLarge) {
		if (str == null && supportLarge) {
			return UTF_NULL_HEADER.length;
		}
		int strlen = str.length();
		int utflen = strlen;
		int c;

		for (int i = 0; i < strlen; i++) {
			c = str.charAt(i);
			if (c < 0x0001 || c > 0x007F)
				utflen += c > 0x07FF ? 2 : 1;
		}

		if (utflen > 65535) {
			if (!supportLarge)
				throw new RuntimeException("encoded string too long: " + utflen + " bytes exceeds 65535 bytes");
			return utflen + 7;
		}
		return utflen + 2;
	}
	public static int writeUTF(CharSequence str, OutputStream out, boolean supportLarge) throws IOException {
		if (str == null && supportLarge) {
			out.write(UTF_NULL_HEADER);
			return UTF_NULL_HEADER.length;
		}
		int strlen = str.length();
		int utflen = strlen;
		int c, count = 0;

		for (int i = 0; i < strlen; i++) {
			c = str.charAt(i);
			if (c < 0x0001 || c > 0x007F)
				utflen += c > 0x07FF ? 2 : 1;
		}

		final byte[] bytearr;
		int finalLength;
		if (utflen > 65535) {
			if (!supportLarge)
				throw new UTFDataFormatException("encoded string too long: " + utflen + " bytes exceeds 65535 bytes");
			finalLength = utflen + 7;
			bytearr = new byte[finalLength];
			bytearr[count++] = (byte) 0;
			bytearr[count++] = (byte) 1;
			bytearr[count++] = (byte) 128;//this is a malformatted UTF, so acts as a header to let the reader know this is a large string
			bytearr[count++] = (byte) ((utflen >>> 24));
			bytearr[count++] = (byte) ((utflen >>> 16));
			bytearr[count++] = (byte) ((utflen >>> 8));
			bytearr[count++] = (byte) ((utflen >>> 0));
		} else {
			finalLength = utflen + 2;
			bytearr = new byte[finalLength];
			bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
			bytearr[count++] = (byte) ((utflen >>> 0) & 0xFF);
		}

		if (strlen == utflen) {
			for (int i = 0; i < strlen; i++)
				bytearr[count++] = (byte) str.charAt(i);
		} else {
			for (int i = 0; i < strlen; i++) {
				c = str.charAt(i);
				if ((c >= 0x0001) && (c <= 0x007F)) {
					bytearr[count++] = (byte) c;

				} else if (c > 0x07FF) {
					bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
					bytearr[count++] = (byte) (0x80 | ((c >> 6) & 0x3F));
					bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
				} else {
					bytearr[count++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
					bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
				}
			}
		}
		out.write(bytearr, 0, finalLength);
		return finalLength;
	}

	public void repeat(byte i, int count) {
		ensureCanStore(count);
		buf[count++] = i;
	}

	public void allocate(int count) {
		ensureCanStore(count);
		this.count += count;
	}

	@Override
	public void write(short[] b, int off, int len) {
		ensureCanStore(2 * len);
		int count = this.count;
		for (len += off; off < len; off++) {
			short v = b[off];
			buf[count++] = (byte) ((v >>> 8));
			buf[count++] = (byte) ((v >>> 0));
		}
		this.count = count;
	}
	@Override
	public void write(int[] b, int off, int len) {
		ensureCanStore(4 * len);
		int count = this.count;
		for (len += off; off < len; off++) {
			int v = b[off];
			buf[count++] = (byte) ((v >>> 24));
			buf[count++] = (byte) ((v >>> 16));
			buf[count++] = (byte) ((v >>> 8));
			buf[count++] = (byte) ((v >>> 0));
		}
		this.count = count;
	}

	@Override
	public void write(long[] b, int off, int len) {
		ensureCanStore(8 * len);
		int count = this.count;
		for (len += off; off < len; off++) {
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
	public void write(float[] b, int off, int len) {
		ensureCanStore(4 * len);
		int count = this.count;
		for (len += off; off < len; off++) {
			int v = Float.floatToIntBits(b[off]);
			buf[count++] = (byte) ((v >>> 24));
			buf[count++] = (byte) ((v >>> 16));
			buf[count++] = (byte) ((v >>> 8));
			buf[count++] = (byte) ((v >>> 0));
		}
		this.count = count;
	}

	@Override
	public void write(double[] b, int off, int len) {
		ensureCanStore(8 * len);
		int count = this.count;
		for (len += off; off < len; off++) {
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
	public void write(char[] b, int off, int len) {
		ensureCanStore(2 * len);
		int count = this.count;
		for (len += off; off < len; off++) {
			char v = b[off];
			buf[count++] = (byte) ((v >>> 8));
			buf[count++] = (byte) ((v >>> 0));
		}
		this.count = count;
	}

	@Override
	public void write(boolean[] b, int off, int len) {
		ensureCanStore(len);
		int count = this.count;
		for (len += off; off < len; off++)
			buf[count++] = b[off] ? (byte) 1 : (byte) 0;
		this.count = count;
	}

	public static void main(String a[]) throws IOException {
		byte b[] = new byte[3];
		b[0] = 0;
		b[1] = 1;
		b[2] = (byte) 128;
		System.out.println(b[2]);
		String s = new DataInputStream(new ByteArrayInputStream(b)).readUTF();
		System.out.println(s);
	}

}
