/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent;

import java.io.EOFException;
import java.io.File;

public interface FastMemMap {

	public void map(File file, long position, int size, boolean reset);

	public void unmap();

	public int reserveBytes(int numBytes) throws EOFException;

	public void write(int pos2Write, byte[] data, int offset, int length);

	public void write(int addr2Write, char[] data, int offset, int length);

	public void write(int pos2Write, byte data);

	public void writeInt(int position, int data);

	public void read(long position, byte[] buf, int off, int len);

	public int length();

}
