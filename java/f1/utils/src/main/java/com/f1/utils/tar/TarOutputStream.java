package com.f1.utils.tar;

import java.io.IOException;
import java.io.OutputStream;

import com.f1.utils.FastByteArrayOutputStream;

public class TarOutputStream extends OutputStream {
	private TarEntry currentEntry;
	private long bytesWrittenTotal = 0;
	final private OutputStream inner;

	FastByteArrayOutputStream buf = new FastByteArrayOutputStream();
	public TarOutputStream(OutputStream out) {
		this.inner = out;
		bytesWrittenTotal = 0;
	}

	@Override
	public void close() throws IOException {
		closeEntry();
		inner.write(PADDING, 0, TarHelper.EOF_BLOCK);
		inner.close();
		buf = null;
	}

	@Override
	public void write(int b) throws IOException {
		buf.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		buf.write(b, off, len);
	}
	@Override
	public void write(byte[] b) throws IOException {
		buf.write(b);
	}

	public void putNextEntry(TarEntry entry) throws IOException {
		closeEntry();
		currentEntry = entry;
	}

	public void closeEntry() throws IOException {
		if (currentEntry != null) {
			final byte[] header = new byte[TarHelper.HEADER_BLOCK];
			int offset = 0;
			offset = TarHelper.getNameBytes(currentEntry.getName(), header, offset, TarHelper.NAMELEN);
			offset = TarHelper.getOctalBytes(currentEntry.getMode(), header, offset, TarHelper.MODELEN);
			offset = TarHelper.getOctalBytes(currentEntry.getUserId(), header, offset, TarHelper.UIDLEN);
			offset = TarHelper.getOctalBytes(currentEntry.getGroupId(), header, offset, TarHelper.GIDLEN);
			long size = buf.size();
			offset = TarHelper.getLongOctalBytes(size, header, offset, TarHelper.SIZELEN);
			offset = TarHelper.getLongOctalBytes(currentEntry.getModTime() / 1000, header, offset, TarHelper.MODTIMELEN);
			int csOffset = offset;
			for (int c = 0; c < TarHelper.CHKSUMLEN; ++c)
				header[offset++] = (byte) ' ';
			header[offset++] = currentEntry.getLinkFlag();
			offset = TarHelper.getNameBytes(currentEntry.getLinkName(), header, offset, TarHelper.NAMELEN);
			offset = TarHelper.getNameBytes(currentEntry.getMagic(), header, offset, TarHelper.MAGICLEN);
			offset = TarHelper.getNameBytes(currentEntry.getUserName(), header, offset, TarHelper.UNAMELEN);
			offset = TarHelper.getNameBytes(currentEntry.getGroupName(), header, offset, TarHelper.GNAMELEN);
			offset = TarHelper.getOctalBytes(currentEntry.getDevMajor(), header, offset, TarHelper.DEVLEN);
			offset = TarHelper.getOctalBytes(currentEntry.getDevMinor(), header, offset, TarHelper.DEVLEN);
			for (; offset < header.length;)
				header[offset++] = 0;
			final long checkSum = TarHelper.computeCheckSum(header);
			TarHelper.getCheckSumOctalBytes(checkSum, header, csOffset, TarHelper.CHKSUMLEN);
			inner.write(header);
			buf.writeTo(inner);
			bytesWrittenTotal += header.length + buf.size();
			int extra = (int) (bytesWrittenTotal % TarHelper.DATA_BLOCK);
			if (extra > 0) {
				inner.write(PADDING, 0, TarHelper.DATA_BLOCK - extra);
				bytesWrittenTotal += TarHelper.DATA_BLOCK - extra;
			}
			buf.reset();
			currentEntry = null;
		}
	}

	private static final byte PADDING[] = new byte[TarHelper.EOF_BLOCK];
}
