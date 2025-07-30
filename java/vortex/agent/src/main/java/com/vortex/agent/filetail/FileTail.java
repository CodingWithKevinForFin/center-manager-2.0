package com.vortex.agent.filetail;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.f1.utils.FastByteArrayOutputStream;
import com.f1.utils.IOH;

public class FileTail implements Closeable {

	public void moveToHead() throws IOException {
		pushback = null;
		skip(in.available());
	}

	final private File file;
	private InputStream in;
	private long position;
	private int readBack;
	private boolean posDup;
	private int headerChecksumBytes;
	private byte[] pushback;

	public FileTail(File file, int readBack, int headerChecksumBytes) {
		this.readBack = readBack;
		this.file = file;
		this.headerChecksumBytes = headerChecksumBytes;
	}

	public String getFileName() {
		return file.getName();
	}

	public File getFile() {
		return file;
	}

	public boolean isFileExists() {
		return file.isFile();
	}

	public boolean isReset() throws IOException {
		if (in == null || !file.isFile())
			return false;
		long length = file.length();
		return length < position || (length > position && in.available() == 0);
	}

	public void close() throws IOException {
		if (!isOpen())
			throw new RuntimeException("already closed");
		position = 0;
		checksumSize = 0;
		checksum = 0;
		posDup = false;
		pushback = null;
		IOH.close(in);
		in = null;
	}

	public boolean isOpen() {
		return in != null;
	}

	public boolean isPosDup() {
		return posDup || !hasChecksum();
	}

	public int getState() throws IOException {
		int r = 0;
		if (isOpen())
			r |= STATE_OPEN;
		if (isFileExists())
			r |= STATE_EXISTS;
		if (isReset())
			r |= STATE_RESET;
		if (isPosDup())
			r |= STATE_POSDUP;
		return r;
	}

	static final public int STATE_OPEN = 1;
	static final public int STATE_RESET = 2;
	static final public int STATE_EXISTS = 4;
	static final public int STATE_POSDUP = 8;

	public boolean open() throws IOException {
		if (isOpen())
			throw new RuntimeException("already open");
		if (file.isFile()) {
			this.in = new FileInputStream(file);
			if (file.length() > 0) {
				int available = in.available();

				posDup = true;
				int startingPosition;
				if (file.length() > readBack)
					startingPosition = available - readBack;
				else
					startingPosition = 0;
				byte[] sink = new byte[Math.min(available, headerChecksumBytes)];
				read(sink, 0, sink.length);
				processChecksum(sink, 0, sink.length);
				if (position > startingPosition) {
					pushback = new byte[(int) (position - startingPosition)];
					System.arraycopy(sink, sink.length - pushback.length, pushback, 0, pushback.length);
				} else
					skip(startingPosition - position);
			}
			return true;
		}
		return false;
	}

	private void processChecksum(byte[] sink, int start, int length) {
		if (checksumSize < headerChecksumBytes) {
			int remaining = Math.min(headerChecksumBytes - checksumSize, length);
			int i = 0;
			for (i = 0; i < remaining; i++)
				checksum = IOH.applyChecksum64(checksum, sink[start + i]);
			checksumSize += i;
			// if (checksumSize == headerChecksumBytes)
			// System.out.println("Checksum complete for: " + getFileName() + ": " + checksum);
		}
	}

	private void skip(long bytes) throws IOException {
		in.skip(bytes);
		position += bytes;
	}

	public long readAvailable(FastByteArrayOutputStream sink) throws IOException {
		int available = in.available();
		long startPosition = getPosition();
		if (pushback != null) {
			sink.write(pushback);
			pushback = null;
		}
		if (available > 0) {
			do {
				sink.ensureCapacity(sink.getCount() + available);
				read(sink.getBuffer(), sink.getCount(), available);
				processChecksum(sink.getBuffer(), sink.getCount(), available);
				sink.incrementCount(available);
				available = in.available();
			} while (available > 0);
		}
		posDup = false;
		return (position - startPosition);
	}

	public long getPosition() {
		return position - (pushback == null ? 0 : pushback.length);
	}

	private void read(byte[] sink, int offset, int bytesCount) throws IOException {
		IOH.readData(in, sink, offset, bytesCount);
		position += bytesCount;
	}

	public long getChecksum() {
		if (!hasChecksum())
			throw new RuntimeException("checksum not available");
		return checksum;
	}

	public boolean hasChecksum() {
		return checksumSize == headerChecksumBytes;
	}

	private long checksum = 0;
	private int checksumSize = 0;

	public String getFilePath() {
		return file.getPath();
	}

}
