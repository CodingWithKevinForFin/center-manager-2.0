/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

public class ByteHelper {

	static public void writeByte(byte v, byte buf[], int pos) {
		buf[pos] = v;
	}
	static public void writeBoolean(boolean v, byte buf[], int pos) {
		buf[pos] = v ? (byte) 1 : (byte) 0;
	}
	static public void writeShort(int v, byte buf[], int pos) {
		buf[pos] = (byte) ((v >>> 8));
		buf[pos + 1] = (byte) ((v >>> 0));
	}

	static public void writeChar(int v, byte buf[], int pos) {
		buf[pos] = (byte) ((v >>> 8));
		buf[pos + 1] = (byte) ((v >>> 0));
	}

	static public void writeInt3(int v, byte buf[], int pos) {
		buf[pos] = (byte) ((v >>> 16));
		buf[pos + 1] = (byte) ((v >>> 8));
		buf[pos + 2] = (byte) ((v >>> 0));
	}

	static public void writeUnsignedShort(int v, byte buf[], int pos) {
		buf[pos] = (byte) ((v >>> 8));
		buf[pos + 1] = (byte) ((v >>> 0));
	}
	static public void writeInt(int v, byte buf[], int pos) {
		buf[pos] = (byte) ((v >>> 24));
		buf[pos + 1] = (byte) ((v >>> 16));
		buf[pos + 2] = (byte) ((v >>> 8));
		buf[pos + 3] = (byte) ((v >>> 0));
	}

	static public void writeLong(long v, byte buf[], int pos) {
		buf[pos] = (byte) (v >>> 56);
		buf[pos + 1] = (byte) (v >>> 48);
		buf[pos + 2] = (byte) (v >>> 40);
		buf[pos + 3] = (byte) (v >>> 32);
		buf[pos + 4] = (byte) (v >>> 24);
		buf[pos + 5] = (byte) (v >>> 16);
		buf[pos + 6] = (byte) (v >>> 8);
		buf[pos + 7] = (byte) (v >>> 0);
	}
	static public void writeLong5(long v, byte buf[], int pos) {
		buf[pos] = (byte) (v >>> 32);
		buf[pos + 1] = (byte) (v >>> 24);
		buf[pos + 2] = (byte) (v >>> 16);
		buf[pos + 3] = (byte) (v >>> 8);
		buf[pos + 4] = (byte) (v >>> 0);
	}
	static public void writeLong6(long v, byte buf[], int pos) {
		buf[pos] = (byte) (v >>> 40);
		buf[pos + 1] = (byte) (v >>> 32);
		buf[pos + 2] = (byte) (v >>> 24);
		buf[pos + 3] = (byte) (v >>> 16);
		buf[pos + 4] = (byte) (v >>> 8);
		buf[pos + 5] = (byte) (v >>> 0);
	}
	static public void writeLong7(long v, byte buf[], int pos) {
		buf[pos] = (byte) (v >>> 48);
		buf[pos + 1] = (byte) (v >>> 40);
		buf[pos + 2] = (byte) (v >>> 32);
		buf[pos + 3] = (byte) (v >>> 24);
		buf[pos + 4] = (byte) (v >>> 16);
		buf[pos + 5] = (byte) (v >>> 8);
		buf[pos + 6] = (byte) (v >>> 0);
	}

	static public void writeFloat(float v, byte buf[], int pos) {
		writeInt(Float.floatToIntBits(v), buf, pos);
	}

	static public void writeDouble(double v, byte buf[], int pos) {
		writeLong(Double.doubleToLongBits(v), buf, pos);
	}
	static public void writeFloatBits(int v, byte buf[], int pos) {
		writeInt(v, buf, pos);
	}

	static public void writeDoubleBits(long v, byte buf[], int pos) {
		writeLong(v, buf, pos);
	}

	static public boolean readBoolean(byte[] buf, int pos) {
		return (buf[pos] & 0xff) != 0;
	}

	static public byte readByte(byte[] buf, int pos) {
		return (byte) (buf[pos] & 0xff);
	}

	static public int readUnsignedByte(byte[] buf, int pos) {
		return (buf[pos] & 0xff);
	}

	static public short readShort(byte[] buf, int pos) {
		return (short) (((buf[pos] & 0xff) << 8) + ((buf[pos + 1] & 0xff) << 0));
	}

	static public int readUnsignedShort(byte[] buf, int pos) {
		return (((buf[pos] & 0xff) << 8) + ((buf[pos + 1] & 0xff) << 0));
	}

	static public char readChar(byte[] buf, int pos) {
		return (char) (((buf[pos] & 0xff) << 8) + ((buf[pos + 1] & 0xff) << 0));
	}

	static public int readInt(byte[] buf, int pos) {
		return ((buf[pos] & 0xff) << 24) + ((buf[pos + 1] & 0xff) << 16) + ((buf[pos + 2] & 0xff) << 8) + ((buf[pos + 3] & 0xff) << 0);
	}

	static public long readLong(byte[] buf, int pos) {
		return (((long) buf[pos] << 56) + ((long) (buf[pos + 1] & 255) << 48) + ((long) (buf[pos + 2] & 255) << 40) + ((long) (buf[pos + 3] & 255) << 32)
				+ ((long) (buf[pos + 4] & 255) << 24) + ((buf[pos + 5] & 255) << 16) + ((buf[pos + 6] & 255) << 8) + ((buf[pos + 7] & 255) << 0));
	}

	static public float readFloat(byte[] buf, int pos) {
		return Float.intBitsToFloat(readInt(buf, pos));
	}

	static public double readDouble(byte[] buf, int pos) {
		return Double.longBitsToDouble(readLong(buf, pos));
	}
	static public int readFloatBits(byte[] buf, int pos) {
		return readInt(buf, pos);
	}

	static public long readDoubleBits(byte[] buf, int pos) {
		return readLong(buf, pos);
	}

	public static int readInt3(byte[] buf, int pos) {
		int r = ((buf[pos + 0] & 0xff) << 16) + ((buf[pos + 1] & 0xff) << 8) + ((buf[pos + 2] & 0xff) << 0);
		return r > MH.MAX_VALUE3 ? MH.MIN_VALUE3 * 2 + r : r;
	}
	public static long readLong5(byte[] buf, int pos) {
		long r = (((long) (buf[pos + 0] & 255) << 32) + ((long) (buf[pos + 1] & 255) << 24) + ((buf[pos + 2] & 255) << 16) + ((buf[pos + 3] & 255) << 8) + ((buf[pos + 4] & 255) << 0));
		return r > MH.MAX_VALUE5 ? MH.MIN_VALUE5 * 2 + r : r;
	}
	public static long readLong6(byte[] buf, int pos) {
		long r = (((long) (buf[pos + 0] & 255) << 40) + ((long) (buf[pos + 1] & 255) << 32) + ((long) (buf[pos + 2] & 255) << 24) + ((buf[pos + 3] & 255) << 16)
				+ ((buf[pos + 4] & 255) << 8) + ((buf[pos + 5] & 255) << 0));
		return r > MH.MAX_VALUE6 ? MH.MIN_VALUE6 * 2 + r : r;
	}
	public static long readLong7(byte[] buf, int pos) {
		long r = (((long) (buf[pos + 0] & 255) << 48) + ((long) (buf[pos + 1] & 255) << 40) + ((long) (buf[pos + 2] & 255) << 32) + ((long) (buf[pos + 3] & 255) << 24)
				+ ((buf[pos + 4] & 255) << 16) + ((buf[pos + 5] & 255) << 8) + ((buf[pos + 6] & 255) << 0));
		return r > MH.MAX_VALUE7 ? MH.MIN_VALUE7 * 2 + r : r;
	}
	public static int writeBytes(byte[] data, byte[] r, int pos) {
		System.arraycopy(data, 0, r, pos, data.length);
		return pos + data.length;
	}
	public static byte[] asBytes(long v) {
		byte[] r = new byte[8];
		writeLong(v, r, 0);
		return r;
	}
	public static byte[] asBytes(int v) {
		byte[] r = new byte[4];
		writeInt(v, r, 0);
		return r;
	}
	public static byte[] asBytes(char v) {
		byte[] r = new byte[2];
		writeChar(v, r, 0);
		return r;
	}
	public static byte[] asBytes(short v) {
		byte[] r = new byte[4];
		writeShort(v, r, 0);
		return r;
	}
	public static byte[] asBytes(byte v) {
		return new byte[] { v };
	}
	public static byte[] asBytes(float v) {
		byte[] r = new byte[4];
		writeFloat(v, r, 0);
		return r;
	}
	public static byte[] asBytes(double v) {
		byte[] r = new byte[8];
		writeDouble(v, r, 0);
		return r;
	}
	public static byte[] asBytes(boolean v) {
		return new byte[] { (byte) (v ? 1 : 0) };
	}

	/**
	 * 
	 * equivalent to calling {@link #asBytes(data)}[pos] but avoids temporary array
	 * 
	 * @param data
	 * @param pos
	 *            0 = most significant bit, 7 = least significant
	 * @return
	 */
	public static byte getByteAt(long data, int pos) {
		return (byte) ((data >> ((Long.SIZE - 1 - pos) << 3)) & 0xff);
	}

	public static short getShortAt(long data, int pos) {
		switch (pos) {
			case 0:
				return (short) ((0xffff000000000000L & data) >> 48);
			case 1:
				return (short) ((0x0000ffff00000000L & data) >> 32);
			case 2:
				return (short) ((0x00000000ffff0000L & data) >> 16);
			case 3:
				return (short) ((0x000000000000ffffL & data));
			default:
				throw new RuntimeException("bad position: " + pos);
		}
	}
	public static int getUnsignedShortAt(long data, int pos) {
		switch (pos) {
			case 0:
				return (int) ((0xffff000000000000L & data) >> 48);
			case 1:
				return (int) ((0x0000ffff00000000L & data) >> 32);
			case 2:
				return (int) ((0x00000000ffff0000L & data) >> 16);
			case 3:
				return (int) ((0x000000000000ffffL & data));
			default:
				throw new RuntimeException("bad position: " + pos);
		}
	}
	/**
	 * 
	 * @param data
	 * @param pos
	 *            0 = most significant bit, 7 = least significant
	 * @param value
	 * @return
	 */
	public static long setByteAt(long data, int pos, byte value) {
		pos = (Long.SIZE - 1 - pos) << 3;
		return MH.clearBits(data, 0xffL << pos) | (((long) value & 0xff) << pos);
	}
	/**
	 * 
	 * equivalent to calling {@link #asBytes(data)}[pos] but avoids temporary array
	 * 
	 * @param data
	 * @param pos
	 *            0 = most significant bit, 7 = least significant
	 * @return
	 */
	public static byte getByteAt(int data, int pos) {
		return (byte) ((data >> ((Integer.SIZE - 1 - pos) << 3)) & 0xff);
	}

	/**
	 * 
	 * @param data
	 * @param pos
	 *            0 = most significant bit, 7 = least significant
	 * @param value
	 * @return
	 */
	public static int setByteAt(int data, int pos, byte value) {
		pos = (Integer.SIZE - 1 - pos) << 3;
		return MH.clearBits(data, 0xff << pos) | (((int) value & 0xff) << pos);
	}
	/**
	 * 
	 * equivalent to calling {@link #asBytes(data)}[pos] but avoids temporary array
	 * 
	 * @param data
	 * @param pos
	 *            0 = most significant bit, 7 = least significant
	 * @return
	 */
	public static byte getByteAt(short data, int pos) {
		return (byte) ((data >> ((Short.SIZE - 1 - pos) << 3)) & 0xff);
	}

	/**
	 * 
	 * @param data
	 * @param pos
	 *            0 = most significant bit, 7 = least significant
	 * @param value
	 * @return
	 */
	public static short setByteAt(short data, int pos, byte value) {
		pos = (Short.SIZE - 1 - pos) << 3;
		return (short) (MH.clearBits(data, (short) (0xff << pos)) | (((short) value & 0xff) << pos));
	}
	public static byte[] asBytes(Number number) {
		if (number instanceof Byte)
			return asBytes(number.byteValue());
		else if (number instanceof Short)
			return asBytes(number.shortValue());
		else if (number instanceof Integer)
			return asBytes(number.intValue());
		else if (number instanceof Long)
			return asBytes(number.longValue());
		else if (number instanceof Float)
			return asBytes(number.floatValue());
		else if (number instanceof Double)
			return asBytes(number.doubleValue());
		else if (number instanceof Byte)
			return asBytes(number.byteValue());
		return null;
	}
}
