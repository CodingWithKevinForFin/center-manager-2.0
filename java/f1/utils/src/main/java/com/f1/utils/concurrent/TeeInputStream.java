package com.f1.utils.concurrent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.f1.utils.FastFilterInputStream;

public class TeeInputStream extends FastFilterInputStream {

	private static final byte[] SKIP = new byte[1024];;
	private OutputStream tee;
	private boolean shouldClose = true;
	private boolean shouldWriteOnSkip = true;

	public TeeInputStream(InputStream in, OutputStream o2) {
		super(in);
		tee = o2;
	}

	public TeeInputStream(InputStream in, OutputStream o2, boolean shouldClose, boolean shouldWriteOnSkip) {
		this(in, o2);
		this.shouldClose = shouldClose;
		this.shouldWriteOnSkip = shouldWriteOnSkip;
	}

	@Override
	public int read() throws IOException {
		int r = getInner().read();
		tee.write(r);
		return r;
	}

	@Override
	public int read(byte[] b) throws IOException {
		int r = getInner().read(b);
		if (r != -1)
			tee.write(b, 0, r);
		return r;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int r = getInner().read(b, off, len);
		if (r != -1)
			tee.write(b, off, r);
		return r;
	}

	@Override
	public long skip(long n) throws IOException {
		n = getInner().skip(n);
		if (shouldWriteOnSkip) {
			for (; n > SKIP.length; n -= SKIP.length)
				tee.write(SKIP);
			tee.write(SKIP, 0, (int) n);
		}
		return n;
	}

	@Override
	public void close() throws IOException {
		try {
			super.close();
		} finally {
			if (shouldClose)
				tee.close();
		}
	}

}
