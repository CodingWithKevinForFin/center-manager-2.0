package com.f1.utils.tar;

public class TarHelper {

	/*
	 * Stream
	 */
	public static final int EOF_BLOCK = 1024;
	public static final int DATA_BLOCK = 512;
	public static final int HEADER_BLOCK = 512;

	/*
	 * Header
	 */
	public static final int NAMELEN = 100;
	public static final int MODELEN = 8;
	public static final int UIDLEN = 8;
	public static final int GIDLEN = 8;
	public static final int SIZELEN = 12;
	public static final int MODTIMELEN = 12;
	public static final int CHKSUMLEN = 8;
	public static final byte LF_OLDNORM = 0;

	/*
	 * File Types
	 */
	public static final byte LF_NORMAL = (byte) '0';
	public static final byte LF_LINK = (byte) '1';
	public static final byte LF_SYMLINK = (byte) '2';
	public static final byte LF_CHR = (byte) '3';
	public static final byte LF_BLK = (byte) '4';
	public static final byte LF_DIR = (byte) '5';
	public static final byte LF_FIFO = (byte) '6';
	public static final byte LF_CONFIG = (byte) '7';

	/*
	 * Ustar header
	 */

	public static final int MAGICLEN = 8;
	/**
	 * The magic tag representing a POSIX tar archive.
	 */
	public static final String TMAGIC = "ustar";

	/**
	 * The magic tag representing a GNU tar archive.
	 */
	public static final String GNU_TMAGIC = "ustar  ";

	public static final int UNAMELEN = 32;
	public static final int GNAMELEN = 32;
	public static final int DEVLEN = 8;
	public static final int MODE_TUREAD = 0400;
	public static final int MODE_TUWRITE = 0200;
	public static final int MODE_TUEXEC = 0100;
	public static final String MAGIC = "ustar";
	public static final String LONG_LINK_FILE_NAME = "././@LongLink";

	/**
	 * Parse an octal string from a header buffer. This is used for the file permission mode value.
	 * 
	 * @param header
	 *            The header buffer from which to parse.
	 * @param offset
	 *            The offset into the buffer from which to parse.
	 * @param length
	 *            The number of header bytes to parse.
	 * 
	 * @return The long value of the octal string.
	 */
	public static long parseOctal(byte[] header, int offset, int length) {
		long result = 0;
		boolean stillPadding = true;
		int end = offset + length;
		for (int i = offset; i < end; ++i) {
			if (header[i] == 0)
				break;
			if (header[i] == (byte) ' ' || header[i] == '0') {
				if (stillPadding)
					continue;
				if (header[i] == (byte) ' ')
					break;
			}
			stillPadding = false;
			result = (result << 3) + (header[i] - '0');
		}
		return result;
	}

	/**
	 * Parse an octal integer from a header buffer.
	 * 
	 * @param value
	 * @param buf
	 *            The header buffer from which to parse.
	 * @param offset
	 *            The offset into the buffer from which to parse.
	 * @param length
	 *            The number of header bytes to parse.
	 * 
	 * @return The integer value of the octal bytes.
	 */
	public static int getOctalBytes(long value, byte[] buf, int offset, int length) {
		int idx = length - 1;
		buf[offset + idx] = 0;
		--idx;
		buf[offset + idx] = (byte) ' ';
		--idx;
		if (value == 0) {
			buf[offset + idx] = (byte) '0';
			--idx;
		} else {
			for (long val = value; idx >= 0 && val > 0; --idx) {
				buf[offset + idx] = (byte) ((byte) '0' + (byte) (val & 7));
				val = val >> 3;
			}
		}
		for (; idx >= 0; --idx) {
			buf[offset + idx] = (byte) ' ';
		}
		return offset + length;
	}

	/**
	 * Parse the checksum octal integer from a header buffer.
	 * 
	 * @param value
	 * @param buf
	 *            The header buffer from which to parse.
	 * @param offset
	 *            The offset into the buffer from which to parse.
	 * @param length
	 *            The number of header bytes to parse.
	 * @return The integer value of the entry's checksum.
	 */
	public static int getCheckSumOctalBytes(long value, byte[] buf, int offset, int length) {
		getOctalBytes(value, buf, offset, length);
		buf[offset + length - 1] = (byte) ' ';
		buf[offset + length - 2] = 0;
		return offset + length;
	}

	/**
	 * Parse an octal long integer from a header buffer.
	 * 
	 * @param value
	 * @param buf
	 *            The header buffer from which to parse.
	 * @param offset
	 *            The offset into the buffer from which to parse.
	 * @param length
	 *            The number of header bytes to parse.
	 * 
	 * @return The long value of the octal bytes.
	 */
	public static int getLongOctalBytes(long value, byte[] buf, int offset, int length) {
		byte[] temp = new byte[length + 1];
		getOctalBytes(value, temp, 0, length + 1);
		System.arraycopy(temp, 0, buf, offset, length);
		return offset + length;
	}

	public static int getNameBytes(String name, byte[] buf, int offset, int length) {
		int i;

		for (i = 0; i < length && i < name.length(); ++i) {
			buf[offset + i] = (byte) name.charAt(i);
		}

		for (; i < length; ++i) {
			buf[offset + i] = 0;
		}

		return offset + length;
	}
	public static String parseName(byte[] header, int offset, int length) {
		StringBuilder result = new StringBuilder(length);
		for (int i = offset, end = offset + length; i < end && header[i] != 0; ++i)
			result.append((char) header[i]);
		return result.toString();
	}

	public static long computeCheckSum(byte[] buf) {
		long sum = 0;
		for (int i = 0; i < buf.length; ++i)
			sum += 0xff & buf[i];
		return sum;
	}

}
