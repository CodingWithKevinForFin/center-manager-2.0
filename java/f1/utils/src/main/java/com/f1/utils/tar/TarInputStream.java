package com.f1.utils.tar;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.f1.utils.IOH;

public class TarInputStream extends FilterInputStream {

	private TarEntry currentEntry;
	private long bytesReadInCurrentEntry = 0;
	private long bytesReadTotal = 0;

	public TarInputStream(InputStream in) {
		super(in);
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public void mark(int readlimit) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		bytesReadTotal = 0;
		bytesReadInCurrentEntry = 0;
		currentEntry = null;
	}

	@Override
	public int read() throws IOException {
		if (currentEntry != null) {
			long remaining = currentEntry.getSize() - bytesReadInCurrentEntry;
			if (remaining == 0)
				return -1;
			int br = super.read();
			if (br == -1) {
				bytesReadInCurrentEntry++;
				bytesReadTotal++;
			}
			return br;
		} else {
			int br = super.read();
			if (br != -1)
				bytesReadTotal += 1;
			return br;
		}
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (currentEntry != null) {
			long remaining = currentEntry.getSize() - bytesReadInCurrentEntry;
			if (remaining == 0)
				return -1;
			else if (remaining < len)
				len = (int) remaining;
			int br = super.read(b, off, len);
			if (br != -1) {
				bytesReadInCurrentEntry += br;
				bytesReadTotal += br;
			}
			return br;
		} else {
			int br = super.read(b, off, len);
			if (br != -1)
				bytesReadTotal += br;
			return br;
		}
	}

	public TarEntry getNextEntry() throws IOException {
		closeCurrentEntry();
		byte[] header = new byte[TarHelper.HEADER_BLOCK];
		IOH.readData(this, header, 0, header.length);
		boolean eof = true;
		for (byte b : header) {
			if (b != 0) {
				eof = false;
				break;
			}
		}

		if (!eof)
			currentEntry = new TarEntry(header);

		if (currentEntry != null && currentEntry.getMode() == 0 && TarHelper.LONG_LINK_FILE_NAME.equals(currentEntry.getName())) {
			byte[] longName = new byte[(int) currentEntry.getSize()];
			IOH.readData(this, longName, 0, longName.length);
			read(longName);
			closeCurrentEntry();
			IOH.readData(this, header, 0, header.length);
			eof = true;
			for (byte b : header) {
				if (b != 0) {
					eof = false;
					break;
				}
			}
			currentEntry = new TarEntry(header);
			if (longName.length > 0 && longName[longName.length - 1] == 0)
				currentEntry.setName(new String(longName, 0, longName.length - 1));
			else
				currentEntry.setName(new String(longName, 0, longName.length));

		}
		return currentEntry;
	}

	public long skip(long n) throws IOException {
		bytesReadTotal += n;
		return super.skip(n);
	}

	private void closeCurrentEntry() throws IOException {
		if (currentEntry != null) {
			if (currentEntry.getSize() > bytesReadInCurrentEntry)
				skip(currentEntry.getSize() - bytesReadInCurrentEntry);
			final long padding = bytesReadTotal % TarHelper.DATA_BLOCK;
			if (padding > 0)
				skip(TarHelper.DATA_BLOCK - padding);
		}
		currentEntry = null;
		bytesReadInCurrentEntry = 0L;
	}

}
