package com.f1.utils.encrypt;

import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class EncoderUtils {

	private static final char ENCODE16_MAP[] = "0123456789abcdef".toCharArray();
	private static final char ENCODE64_MAP_URL_SAFE[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".toCharArray();
	private static final char ENCODE64_MAP[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
	private static final int[] DECODE64_MAP = new int[128];
	private static final int[] DECODE16_MAP = new int[128];

	static {
		AH.fill(DECODE64_MAP, -1);
		AH.fill(DECODE16_MAP, -1);
		for (int i = 0; i < ENCODE64_MAP.length; i++)
			DECODE64_MAP[ENCODE64_MAP[i]] = DECODE64_MAP[ENCODE64_MAP_URL_SAFE[i]] = i;
		DECODE64_MAP['='] = 0;

		int i = 0;
		for (char c : ENCODE16_MAP)
			DECODE16_MAP[Character.toLowerCase(c)] = DECODE16_MAP[Character.toUpperCase(c)] = i++;

	}

	public static byte[] decodeCert(String src) {
		String certData = SH.trim(src.toString());
		certData = SH.stripPrefix(certData, "-----BEGIN CERTIFICATE-----", false);
		certData = SH.stripSuffix(certData, "-----END CERTIFICATE-----", false);
		return decode64IgnoreWhitespace(certData);
	}
	public static byte[] decode64IgnoreWhitespace(CharSequence src) {
		int i = 0, len = src.length();

		while (i < len) {
			char c = src.charAt(i++);
			if (c <= ' ') {
				StringBuilder sb = new StringBuilder(src.length() - 1);
				sb.append(src, 0, i - 1);
				while (i < len) {
					c = src.charAt(i++);
					if (c > ' ')
						sb.append(c);
				}
				return decode64(sb);
			}
		}
		return decode64(src);
	}
	public static byte[] decode64(CharSequence src) {
		return decode64(src, 0, src.length());
	}
	public static byte[] decode64(CharSequence src, int start, int end) {
		final int len = end - start;
		if (len == 0)
			return OH.EMPTY_BYTE_ARRAY;
		final int padCount;
		switch (len & 3) {
			case 0:
				padCount = (src.charAt(end - 1) == '=' ? (src.charAt(end - 2) == '=' ? 2 : 1) : 0);
				break;
			case 1:
				padCount = -1;
				break;
			default:
				padCount = 0;
				break;
		}
		int bytes = (int) (((len * 6L) >> 3) - padCount);
		final int blocks = (bytes / 3) * 3;

		final byte[] dst = new byte[bytes];
		int si = start, di = 0;

		while (di < blocks) {
			final int n = DECODE64_MAP[src.charAt(si++)] << 18 | DECODE64_MAP[src.charAt(si++)] << 12 | DECODE64_MAP[src.charAt(si++)] << 6 | DECODE64_MAP[src.charAt(si++)];
			dst[di++] = (byte) (n >> 16);
			dst[di++] = (byte) (n >> 8);
			dst[di++] = (byte) n;
		}

		if (di < bytes) {
			int n = 0;
			switch (len - si) {
				case 4:
					n |= DECODE64_MAP[src.charAt(si + 3)];
				case 3:
					n |= DECODE64_MAP[src.charAt(si + 2)] << 6;
				case 2:
					n |= DECODE64_MAP[src.charAt(si + 1)] << 12;
				case 1:
					n |= DECODE64_MAP[src.charAt(si)] << 18;
			}
			for (int r = 16; di < bytes; r -= 8)
				dst[di++] = (byte) (n >> r);
		}

		return dst;
	}
	public static String encode64UrlSafe(byte[] src) {
		return encode64(src, 0, src.length, true, new StringBuilder()).toString();
	}
	public static String encode64(byte[] src) {
		return encode64(src, 0, src.length, false, new StringBuilder()).toString();
	}
	public static StringBuilder encode64UrlSafe(byte[] src, StringBuilder sink) {
		return encode64(src, 0, src.length, true, sink);
	}
	public static StringBuilder encode64(byte[] src, StringBuilder sink) {
		return encode64(src, 0, src.length, false, sink);
	}
	public static StringBuilder encode64(byte[] src, int start, int end, boolean urlSafe, StringBuilder sink) {
		char[] charSet = urlSafe ? ENCODE64_MAP_URL_SAFE : ENCODE64_MAP;
		final int len = end - start;
		if (len == 0)
			return sink;

		int blocks = (len / 3) * 3;
		int tail = len - blocks;

		int si = start;

		while (si < blocks) {
			int n = (src[si++] & 0xff) << 16 | (src[si++] & 0xff) << 8 | (src[si++] & 0xff);
			sink.append(charSet[(n >>> 18) & 0x3f]);
			sink.append(charSet[(n >>> 12) & 0x3f]);
			sink.append(charSet[(n >>> 6) & 0x3f]);
			sink.append(charSet[n & 0x3f]);
		}
		if (tail > 0) {
			int n = (src[si] & 0xff) << 10;
			if (tail == 2)
				n |= (src[++si] & 0xff) << 2;

			sink.append(charSet[(n >>> 12) & 0x3f]);
			sink.append(charSet[(n >>> 6) & 0x3f]);
			if (tail == 2)
				sink.append(charSet[n & 0x3f]);

			if (tail == 1)
				sink.append('=');
			sink.append('=');
		}
		return sink;
	}

	public static StringBuilder encode16(byte[] data, StringBuilder sink) {
		SH.ensureExtraCapacity(sink, data.length << 1);
		for (byte b : data)
			sink.append(ENCODE16_MAP[(0xf0 & b) >>> 4]).append(ENCODE16_MAP[(0x0f & b)]);
		return sink;
	}

	public static char[] encode16(byte[] data) {
		char[] r = new char[data.length << 1];
		int i = 0;
		for (byte b : data) {
			r[i++] = ENCODE16_MAP[(0xf0 & b) >>> 4];
			r[i++] = ENCODE16_MAP[(0x0f & b)];
		}
		return r;
	}

	public static byte[] decode16(CharSequence src) {
		return decode16(src, 0, src.length());
	}
	public static byte[] decode16(CharSequence src, int start, int end) {
		int len = end - start;
		if ((len & 1) == 1)
			throw new IllegalArgumentException("length invalid, must be multiple of 2: " + len);
		if (len == 0)
			return OH.EMPTY_BYTE_ARRAY;
		byte[] r = new byte[len >> 1];
		for (int in = start, out = 0; in < end; out++) {
			try {
				final int n = DECODE16_MAP[src.charAt(in++)];
				if (n == -1)
					throw new IllegalArgumentException("bad char");
				final int m = DECODE16_MAP[src.charAt(in++)];
				if (m == -1)
					throw new IllegalArgumentException("bad char");
				r[out] = (byte) (n << 4 | m);
			} catch (Exception e) {
				throw new IllegalArgumentException("bad char at " + (--in) + ": " + src.charAt(in), e);
			}
		}
		return r;
	}
	public static byte[] decode16(char[] src) {
		return decode16(src, 0, src.length);
	}
	public static byte[] decode16(char[] src, int start, int end) {
		int len = end - start;
		if ((len & 1) == 1)
			throw new IllegalArgumentException("length invalid, must be multiple of 2: " + len);
		if (len == 0)
			return OH.EMPTY_BYTE_ARRAY;
		byte[] r = new byte[len >> 1];
		for (int in = 0, out = 0; in < len; out++) {
			try {
				final int n = DECODE16_MAP[src[in++]];
				if (n == -1)
					throw new IllegalArgumentException("bad char");
				final int m = DECODE16_MAP[src[in++]];
				if (m == -1)
					throw new IllegalArgumentException("bad char");
				r[out] = (byte) (n << 4 | m);
			} catch (Exception e) {
				throw new IllegalArgumentException("bad char at " + (--in) + ": " + src[in], e);
			}
		}
		return r;
	}

	//reads 11 bytes
	public static long decodeLong64(char[] data, int start) {
		long r = 0;
		for (int end = start + 11; start < end; start++)
			r = (r << 6) | DECODE64_MAP[data[start]];
		return r;
	}

	//reads 11 bytes
	public static long decodeLong64(byte[] data, int start) {
		long r = 0;
		for (int end = start + 11; start < end; start++)
			r = (r << 6) | DECODE64_MAP[data[start]];
		return r;
	}

	//reads 11 bytes
	public static long decodeLong64(CharSequence data, int start) {
		long r = 0;
		for (int end = start + 11; start < end; start++)
			r = (r << 6) | DECODE64_MAP[data.charAt(start)];
		return r;
	}

	//writes 11 bytes
	public static void encodeLong64(long data, StringBuilder sink) {
		for (int i = 10; i >= 0; i--) {
			long n = (data >> (i * 6)) & 0x3F;
			sink.append(ENCODE64_MAP_URL_SAFE[(int) n]);
		}
	}

	//reads 6 bytes
	public static int decodeInt64(char[] data, int start) {
		int r = 0;
		for (int end = start + 6; start < end; start++)
			r = (r << 6) | DECODE64_MAP[data[start]];
		return r;
	}
	//reads 6 bytes
	public static int decodeInt64(byte[] data, int start) {
		int r = 0;
		for (int end = start + 6; start < end; start++)
			r = (r << 6) | DECODE64_MAP[data[start]];
		return r;
	}
	//reads 6 bytes
	public static int decodeInt64(CharSequence data, int start) {
		int r = 0;
		for (int end = start + 6; start < end; start++)
			r = (r << 6) | DECODE64_MAP[data.charAt(start)];
		return r;
	}

	//writes 6 bytes
	public static void encodeInt64(int data, StringBuilder sink) {
		for (int i = 5; i >= 0; i--) {
			long n = (data >> (i * 6)) & 0x3F;
			sink.append(ENCODE64_MAP_URL_SAFE[(int) n]);
		}
	}

}
