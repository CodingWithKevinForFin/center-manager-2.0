package com.f1.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UTFDataFormatException;

public class FastRandomAccessFile implements Closeable {

	private class SHUTDOWN_HOOK extends Thread {
		public void run() {
			try {
				if (!isClosed) {
					inner.getChannel().force(true);
					IOH.close(inner);
				}
			} catch (Exception e) {
				System.err.print("Error on shutdown for FastRandomAccessFile: " + IOH.getFullPath(file));
				e.printStackTrace(System.err);
			}
		};
	}

	private long filePosition = -1;//This is the physical file position, as the underlining RAF knows it. Note, to get the logical position you may need to add offsets from the read/write buffers (see getPosition)
	final private boolean isReadonly;
	final private File file;
	final private Thread shutdown;
	private RandomAccessFile inner;
	private boolean isClosed = true;
	final private FastBufferedInputStream in;
	final private FastBufferedOutputStream out;
	private Input input;
	private Output output;
	private String mode;
	final private int blockSize;
	private long filePointerAtClose = -1L;
	private long length = -1;
	final private FastRandomAccessFileListener listener;

	public FastRandomAccessFile(File file, String mode, int blockSize, boolean addShutdownHook, FastRandomAccessFileListener listener) throws IOException {
		this.file = file;
		this.mode = mode;
		this.blockSize = blockSize;
		this.isReadonly = mode.equals("r");
		this.listener = listener;
		if (!isReadonly && addShutdownHook)
			this.shutdown = new SHUTDOWN_HOOK();
		else
			this.shutdown = null;
		this.in = new FastBufferedInputStream(null, blockSize) {
			@Override
			protected void incrementInnerRead(long r) {
				super.incrementInnerRead(r);
				filePosition += r;
				if (length != -1 && filePosition > length)
					length = filePosition;
			}
			@Override
			public void skipBytesFully(int bytes) throws IOException {
				FastRandomAccessFile.this.skipBytesFully(bytes);
			}
		};
		this.out = new FastBufferedOutputStream(null, blockSize) {
			@Override
			protected void incrementInnerWrote(int len) {
				super.incrementInnerWrote(len);
				filePosition += len;
				if (length != -1 && filePosition > length)
					length = filePosition;
			}
		};
		open();
		this.input = new Input();
		this.output = new Output();
	}

	public FastDataInput getInput() {
		return this.input;
	}
	public FastDataOutput getOutput() {
		return this.output;
	}

	public void setLength(long l) throws IOException {
		ensureOpen();
		if (lastOp == WRITE) {
			this.out.flushIfBuffered();
			lastOp = NONE;
		} else if (lastOp == READ) {
			this.in.resetBuffer();
			lastOp = NONE;
		}
		this.inner.setLength(l);
		if (l > filePosition)
			filePosition = l;
		this.length = l;
	}

	//	private void flushBoth() throws IOException {
	//		if (lastOp == WRITE) {
	//			this.out.flushIfBuffered();
	//			lastOp = NONE;
	//		} else if (lastOp == READ) {
	//			this.in.resetBuffer();
	//			lastOp = NONE;
	//		}
	//	}
	public void seek(long n) throws IOException {
		ensureOpen();
		if (n == getPosition())
			return;
		if (this.lastOp == WRITE) {
			this.out.flushIfBuffered();
			lastOp = NONE;
		} else if (this.lastOp == READ) {
			int bufferedBytes = this.in.count;
			long pos = n - (this.filePosition - bufferedBytes);
			if (pos >= 0 && pos <= bufferedBytes) {
				this.in.pos = (int) pos;
				return;
			}
			this.in.resetBuffer();
			lastOp = NONE;
		}
		this.inner.seek(n);
		this.filePosition = n;
	}
	private void ensureOpen() throws IOException {
		if (isClosed)
			open();
		if (listener != null)
			listener.onUsed(this);
	}

	public void skipBytesFully(int t) throws IOException {
		seek(getPosition() + t);
	}

	@Override
	public void close() throws IOException {
		if (isClosed)
			return;
		this.out.flush();
		filePointerAtClose = this.inner.getFilePointer();
		this.filePosition = -1;
		this.length = -1;
		this.inner.close();
		if (shutdown != null)
			Runtime.getRuntime().removeShutdownHook(this.shutdown);
		this.isClosed = true;
		if (this.listener != null)
			this.listener.onClosed(this);
	}

	private byte READ = 1;
	private byte WRITE = 2;
	private byte NONE = 0;
	private byte lastOp = NONE;

	private void onRead() throws IOException {
		ensureOpen();
		if (lastOp != READ) {
			if (lastOp == WRITE)
				this.out.flushIfBuffered();
			lastOp = READ;
		}
	}
	private void onWrite() throws IOException {
		ensureOpen();
		if (lastOp != WRITE) {
			if (lastOp == READ) {
				int n = this.in.availableBuffer();
				if (n > 0) {
					this.filePosition -= n;
					this.inner.seek(this.filePosition);
				}
				this.in.resetBuffer();
			}
			lastOp = WRITE;
		}

	}

	public void open() throws IOException {
		OH.assertTrue(isClosed);
		this.length = -1;
		this.inner = new RandomAccessFile(file, mode);
		if (this.filePointerAtClose != -1) {
			this.inner.seek(this.filePointerAtClose);
			this.filePosition = this.filePointerAtClose;
		} else
			this.filePosition = 0;
		this.isClosed = false;
		if (shutdown != null)
			Runtime.getRuntime().addShutdownHook(this.shutdown);
		final FileDescriptor fd = this.inner.getFD();
		this.in.reset(new FileInputStream(fd));
		this.out.reset(new FileOutputStream(fd));
		this.lastOp = NONE;
		if (this.listener != null)
			this.listener.onOpened(this);
	}

	private class Input extends InputStream implements FastDataInput {

		@Override
		public int read(byte[] b) throws IOException {
			onRead();
			return in.read(b);
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			onRead();
			return in.read(b, off, len);
		}
		@Override
		public void readFully(byte[] b) throws IOException {
			onRead();
			in.readFully(b);
		}

		@Override
		public void readFully(byte[] b, int off, int len) throws IOException {
			onRead();
			in.readFully(b, off, len);

		}

		@Override
		public int skipBytes(int n) throws IOException {
			onRead();
			return in.skipBytes(n);
		}

		@Override
		public boolean readBoolean() throws IOException {
			onRead();
			return in.readBoolean();
		}

		@Override
		public byte readByte() throws IOException {
			onRead();
			return in.readByte();
		}

		@Override
		public int readUnsignedByte() throws IOException {
			onRead();
			return in.readUnsignedByte();
		}

		@Override
		public short readShort() throws IOException {
			onRead();
			return in.readShort();
		}

		@Override
		public int readUnsignedShort() throws IOException {
			onRead();
			return in.readUnsignedShort();
		}

		@Override
		public char readChar() throws IOException {
			onRead();
			return in.readChar();
		}

		@Override
		public int readInt() throws IOException {
			onRead();
			return in.readInt();
		}

		@Override
		public long readLong() throws IOException {
			onRead();
			return in.readLong();
		}

		@Override
		public float readFloat() throws IOException {
			onRead();
			return in.readFloat();
		}

		@Override
		public double readDouble() throws IOException {
			onRead();
			return in.readDouble();
		}

		@Override
		public String readLine() throws IOException {
			onRead();
			return in.readLine();
		}

		@Override
		public String readUTF() throws IOException {
			onRead();
			return in.readUTF();
		}

		@Override
		public short[] readFully(short[] b, int off, int len) throws IOException {
			onRead();
			return in.readFully(b, off, len);

		}

		@Override
		public int[] readFully(int[] b, int off, int len) throws IOException {
			onRead();
			return in.readFully(b, off, len);
		}

		@Override
		public long[] readFully(long[] b, int off, int len) throws IOException {
			onRead();
			return in.readFully(b, off, len);
		}

		@Override
		public float[] readFully(float[] b, int off, int len) throws IOException {
			onRead();
			return in.readFully(b, off, len);
		}

		@Override
		public double[] readFully(double[] b, int off, int len) throws IOException {
			onRead();
			return in.readFully(b, off, len);
		}

		@Override
		public char[] readFully(char[] b, int off, int len) throws IOException {
			onRead();
			return in.readFully(b, off, len);
		}

		@Override
		public boolean[] readFully(boolean[] b, int off, int len) throws IOException {
			onRead();
			return in.readFully(b, off, len);
		}

		@Override
		public int readInt3() throws IOException {
			onRead();
			return in.readInt3();
		}

		@Override
		public long readLong5() throws IOException {
			onRead();
			return in.readLong5();
		}

		@Override
		public long readLong6() throws IOException {
			onRead();
			return in.readLong6();
		}

		@Override
		public long readLong7() throws IOException {
			onRead();
			return in.readLong7();
		}

		@Override
		public int read() throws IOException {
			onRead();
			return in.read();
		}

		@Override
		public int available() throws IOException {
			return FastRandomAccessFile.this.available();
		}

		@Override
		public void skipUTF() throws IOException {
			onRead();
			in.skipUTF();
		}

		@Override
		public void skipBytesFully(int bytes) throws IOException {
			onRead();
			in.skipBytesFully(bytes);
		}

	}

	private class Output extends OutputStream implements FastDataOutput {

		@Override
		public void write(byte[] b) throws IOException {
			onWrite();
			out.write(b);
		}
		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			onWrite();
			out.write(b, off, len);
		}
		@Override
		public void writeBoolean(boolean v) throws IOException {
			onWrite();
			out.writeBoolean(v);
		}

		@Override
		public void writeByte(int v) throws IOException {
			onWrite();
			out.writeByte(v);
		}

		@Override
		public void writeShort(int v) throws IOException {
			onWrite();
			out.writeShort(v);
		}

		@Override
		public void writeChar(int v) throws IOException {
			onWrite();
			out.writeChar(v);
		}

		@Override
		public void writeInt(int v) throws IOException {
			onWrite();
			out.writeInt(v);
		}

		@Override
		public void writeLong(long v) throws IOException {
			onWrite();
			out.writeLong(v);
		}

		@Override
		public void writeFloat(float v) throws IOException {
			onWrite();
			out.writeFloat(v);
		}

		@Override
		public void writeDouble(double v) throws IOException {
			onWrite();
			out.writeDouble(v);
		}

		@Override
		public void writeBytes(String s) throws IOException {
			onWrite();
			out.writeBytes(s);
		}

		@Override
		public void writeChars(String s) throws IOException {
			onWrite();
			out.writeChars(s);
		}

		@Override
		public void writeUTF(String s) throws IOException {
			onWrite();
			out.writeUTF(s);
		}

		@Override
		public void write(short[] b, int off, int len) throws IOException {
			onWrite();
			out.write(b, off, len);
		}

		@Override
		public void write(int[] b, int off, int len) throws IOException {
			onWrite();
			out.write(b, off, len);
		}

		@Override
		public void write(long[] b, int off, int len) throws IOException {
			onWrite();
			out.write(b, off, len);
		}

		@Override
		public void write(float[] b, int off, int len) throws IOException {
			onWrite();
			out.write(b, off, len);
		}

		@Override
		public void write(double[] b, int off, int len) throws IOException {
			onWrite();
			out.write(b, off, len);
		}

		@Override
		public void write(char[] b, int off, int len) throws IOException {
			onWrite();
			out.write(b, off, len);
		}

		@Override
		public void write(boolean[] b, int off, int len) throws IOException {
			onWrite();
			out.write(b, off, len);
		}

		@Override
		public void writeUTFSupportLarge(CharSequence str) throws UTFDataFormatException, IOException {
			onWrite();
			out.writeUTFSupportLarge(str);
		}

		@Override
		public void writeInt3(int i) throws IOException {
			onWrite();
			out.writeInt3(i);
		}

		@Override
		public void writeLong5(long i) throws IOException {
			onWrite();
			out.writeLong5(i);
		}

		@Override
		public void writeLong6(long i) throws IOException {
			onWrite();
			out.writeLong6(i);
		}

		@Override
		public void writeLong7(long i) throws IOException {
			onWrite();
			out.writeLong7(i);
		}

		@Override
		public void write(int b) throws IOException {
			onWrite();
			out.write(b);
		}

		@Override
		public void flush() throws IOException {
			out.flushIfBuffered();
		}

	}

	public long getPhysicalPosition() throws IOException {
		ensureOpen();
		return this.inner.getFilePointer();
	}
	public int available() throws IOException {
		return (int) (length() - this.getPosition());
	}

	public long getPhysicalLength() throws IOException {
		ensureOpen();
		return this.inner.length();
	}

	//If data has been cached then this will reflect that.
	public long getPosition() throws IOException {
		ensureOpen();
		if (this.lastOp == WRITE)
			return this.filePosition + this.out.bufferedBytes();
		if (this.lastOp == READ)
			return this.filePosition - this.in.availableBuffer();
		return this.filePosition;
	}
	//If data has been cached but not yet written then this will reflect that.
	public long length() throws IOException {
		ensureOpen();
		if (this.length == -1)
			this.length = this.inner.length();

		if (this.lastOp == WRITE)
			return Math.max(this.filePosition + this.out.bufferedBytes(), this.length);
		return this.length;
	}

	@Override
	public String toString() {
		return "FastRandomAccessFile: " + IOH.getFullPath(this.file);
	}

	public File getFile() {
		return this.file;
	}

}
