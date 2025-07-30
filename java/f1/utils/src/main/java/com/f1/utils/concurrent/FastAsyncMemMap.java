/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent;

import java.io.EOFException;
import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.concurrent.atomic.AtomicInteger;

import com.f1.utils.ByteHelper;
import com.f1.utils.IOH;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class FastAsyncMemMap implements FastMemMap {
	private static final Unsafe unsafe = UnsafeHelper.unsafe;

	private static final Field privateField;
	static {
		try {
			privateField = Buffer.class.getDeclaredField("address");
			privateField.setAccessible(true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	private int size = -1;
	private final AtomicInteger nextPosition = new AtomicInteger();
	volatile private MappedByteBuffer buffer;
	volatile private long address;
	private File file;

	@Override
	public void map(File file, long position, int size, boolean reset) {
		try {
			RandomAccessFile myFile = new RandomAccessFile(file, "rw");
			this.buffer = myFile.getChannel().map(MapMode.READ_WRITE, position, size);
			if (myFile != null)
				myFile.close();
			this.address = getAddress(buffer);
			this.file = file;
			this.size = size;
			int i = 0;
			if (reset) {
				for (; i + 7 < size; i += 8)
					unsafe.putLong(toAddress(i), 0);
				for (; i < size; i++)
					unsafe.putByte(toAddress(i), (byte) 0);
			} else {
				for (; i + 7 < size; i += 8)
					unsafe.getLong(toAddress(i));
				for (; i < size; i++)
					unsafe.getByte(toAddress(i));
			}

		} catch (Exception e) {
			throw new RuntimeException("mapping: " + IOH.getFullPath(file), e);
		}
	}

	private static long getAddress(Buffer buffer) {
		try {
			return privateField.getLong(buffer);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void unmap() {
		try {
			buffer.force();
			((sun.nio.ch.DirectBuffer) buffer).cleaner().clean();
		} catch (Exception e) {
			throw new RuntimeException("umapping: " + IOH.getFullPath(file), e);
		}
	}

	@Override
	public int reserveBytes(int bytes) throws EOFException {
		return nextPosition.getAndAdd(bytes);
	}

	private long toAddress(long i) {
		return (i << 0) + address;
	}

	@Override
	public void write(int position, byte[] data, int offset, int length) {
		for (int i = 0; i < length; i++)
			unsafe.putByte(toAddress(position + i), data[i + offset]);
	}

	@Override
	public void write(int position, byte data) {
		unsafe.putByte(toAddress(position), data);
	}

	@Override
	public void write(int position, char[] c, int offset, int length) {
		for (int i = 0; i < length; i++)
			unsafe.putByte(toAddress(position + i), (byte) c[i + offset]);
	}

	@Override
	public void writeInt(int position, int data) {
		byte[] buf = new byte[4];
		ByteHelper.writeInt(data, buf, 0);
		write(position, buf, 0, 4);
	}

	@Override
	public void read(long position, byte[] buf, int off, int len) {
		for (int i = 0; i < len; i++)
			buf[i + off] = unsafe.getByte(toAddress(position + i));

	}

	@Override
	public int length() {
		return size;
	}
}
