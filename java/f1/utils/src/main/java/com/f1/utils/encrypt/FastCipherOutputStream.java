package com.f1.utils.encrypt;

import java.io.IOException;
import java.io.OutputStream;

import javax.crypto.Cipher;

import com.f1.utils.ByteHelper;
import com.f1.utils.FastFilterOutputStream;
import com.f1.utils.OH;

public class FastCipherOutputStream extends FastFilterOutputStream {
	public final static byte LARGE_BLOCK = 127; //> 127 bytes
	private Cipher cipher;
	private byte[] byteBuf = new byte[1];
	private byte[] intBuf = new byte[4];
	// the buffer holding data ready to be written out
	// stream status
	private int blockSize;
	private static final byte[] PADDING = new byte[2048];
	private boolean needsFlush;

	public FastCipherOutputStream(OutputStream out, Cipher c) throws IOException {
		super(out);
		this.blockSize = c.getBlockSize();
		OH.assertLe(this.blockSize, PADDING.length);
		this.cipher = c;
	}
	public void write(int b) throws IOException {
		byteBuf[0] = (byte) b;
		byte[] update = cipher.update(byteBuf, 0, 1);
		if (update != null && update.length > 0)
			writeBuf(update, false);
		needsFlush = true;
	}
	public void write(byte b[], int off, int len) throws IOException {
		if (len > 0) {
			byte[] update = cipher.update(b, off, len);
			if (update != null && update.length > 0)
				writeBuf(update, false);
			needsFlush = true;
		}
	}
	public void flush() throws IOException {
		if (!needsFlush)
			return;
		needsFlush = false;
		try {
			byte[] data = cipher.doFinal();
			writeBuf(data, true);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	public void close() throws IOException {
		flush();
		out.close();
	}
	private void writeBuf(byte[] b, boolean doFinal) throws IOException {
		OH.assertEq(b.length % blockSize, 0);
		int size = b.length / blockSize;
		if (size < 125) {
			out.write(doFinal ? (-1 - size) : (1 + size));
		} else {
			out.write(doFinal ? -LARGE_BLOCK : LARGE_BLOCK);
			ByteHelper.writeInt(size, intBuf, 0);
			out.write(intBuf);
		}
		out.write(b);
	};
}