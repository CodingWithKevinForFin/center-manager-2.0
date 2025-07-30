/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import com.f1.utils.OH;

public class FastFileOutputStream extends OutputStream {

	private volatile FastMemMap currentMap;

	public FastFileOutputStream(File file, int size, boolean reset) {
		currentMap = new FastAsyncMemMap();
		currentMap.map(file, 0, size, reset);
	}

	@Override
	public void write(int b) throws IOException {
		currentMap.write(currentMap.reserveBytes(1), (byte) b);
	}

	@Override
	public void write(byte b[], int off, int len) throws IOException {
		currentMap.write(currentMap.reserveBytes(len), b, off, len);
	}

	@Override
	public void write(byte b[]) throws IOException {
		currentMap.write(currentMap.reserveBytes(b.length), b, 0, b.length);
	}

	public long writeAndGetPosition(int b) throws IOException {
		int r;
		currentMap.write(r = currentMap.reserveBytes(1), (byte) b);
		return r;
	}

	public long writeAndGetPosition(byte b[], int off, int len) throws IOException {
		int r;
		currentMap.write(r = currentMap.reserveBytes(len), b, off, len);
		return r;
	}

	public long writeAndGetPosition(byte b[]) throws IOException {
		int r;
		currentMap.write(r = currentMap.reserveBytes(b.length), b, 0, b.length);
		return r;
	}

	public long write(char c[], int off, int len) throws IOException {
		int r;
		currentMap.write(r = currentMap.reserveBytes(len), c, off, len);
		return r;
	}

	@Override
	public void close() throws IOException {
		currentMap.unmap();
		currentMap = null;
	}

	public void readAt(long position, byte[] buf, int off, int len) {
		OH.assertBetween(position + len, 0, length());
		currentMap.read(position, buf, off, len);
	}

	public int length() {
		return currentMap.length();
	}

	public void skip(long l) throws IOException {
		currentMap.reserveBytes((int) l);
	}

}
