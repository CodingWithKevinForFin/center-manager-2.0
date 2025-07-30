package com.f1.utils.encrypt;

import java.io.IOException;
import java.io.InputStream;

import javax.crypto.Cipher;

import com.f1.utils.ByteHelper;
import com.f1.utils.FastFilterInputStream;
import com.f1.utils.IOH;

public class FastCipherInputStream extends FastFilterInputStream {
	private byte[] oBuf;
	private int oStart = 0;
	private int oEnd = 0;
	final private Cipher cipher;
	final private int blockSize;

	public FastCipherInputStream(InputStream in, Cipher cipher) {
		super(in);
		this.blockSize = cipher.getBlockSize();
		this.cipher = cipher;
	}

	public int read() throws IOException {
		if (oStart >= oEnd) {
			int i = 0;
			while (i == 0)
				i = getMoreData();
			if (i == -1)
				return -1;
		}
		return ((int) oBuf[oStart++] & 0xff);
	}

	public int read(byte b[], int off, int len) throws IOException {
		if (oStart >= oEnd) {
			int i = getMoreData();
			if (i == 0)
				return 0;
			if (i == -1)
				return -1;
		}
		if (len <= 0) {
			return 0;
		}
		int available = oEnd - oStart;
		if (len < available)
			available = len;
		if (b != null) {
			System.arraycopy(oBuf, oStart, b, off, available);
		}
		oStart = oStart + available;
		return available;
	}

	public long skip(long n) throws IOException {
		int available = oEnd - oStart;
		if (n > available) {
			n = available;
		}
		if (n < 0) {
			return 0;
		}
		oStart += n;
		return n;
	}

	public int available() throws IOException {
		return (oEnd - oStart);
	}

	public void close() throws IOException {
		in.close();
	}

	public void mark(int readlimit) {
		throw new UnsupportedOperationException();
	}

	public void reset() throws IOException {
		throw new UnsupportedOperationException();
	}

	public boolean markSupported() {
		return false;
	}

	private byte[] buf = new byte[512];

	private int getMoreData() throws IOException {
		while (oEnd == oStart) {
			int count = in.read(buf, 0, 1);
			if (count < 1)
				return count;
			byte code = buf[0];
			boolean isFirst;
			if (code < 0) {
				isFirst = true;
				code = (byte) -code;
			} else
				isFirst = false;
			int size;
			if (code == FastCipherOutputStream.LARGE_BLOCK) {
				IOH.readData(in, buf, 0, 4);
				size = ByteHelper.readInt(buf, 0);
			} else
				size = code - 1;
			size *= blockSize;
			if (this.buf.length < size)
				this.buf = new byte[size];
			IOH.readData(in, this.buf, 0, size);
			if (isFirst) {
				try {
					this.oBuf = cipher.doFinal(this.buf, 0, size);//just reset
				} catch (Throwable e) {
				}
			} else {
				this.oBuf = cipher.update(this.buf, 0, size);
			}
			this.oStart = 0;
			this.oEnd = this.oBuf == null ? 0 : this.oBuf.length;
		}
		return oEnd - oStart;

	}
}
