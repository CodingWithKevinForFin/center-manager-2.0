package com.f1.utils.encrypt;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.f1.utils.ByteHelper;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class ScryptUtils {

	public static final char DELIM = '$';

	public static byte[] scrypt(byte[] passwd, byte[] salt, int cpuCost, int memCost, int parrallelParam, int keyLength) throws GeneralSecurityException {
		if (cpuCost < 2 || (cpuCost & (cpuCost - 1)) != 0)
			throw new IllegalArgumentException("cpu cost must be a power of 2 greater than 1: " + cpuCost);
		else if (cpuCost > Integer.MAX_VALUE / 128 / memCost)
			throw new IllegalArgumentException("Cpu cost too large: " + cpuCost);
		else if (memCost > Integer.MAX_VALUE / 128 / parrallelParam)
			throw new IllegalArgumentException("Mem cost too large: " + memCost);

		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(passwd, "HmacSHA256"));

		final byte[] r = new byte[keyLength];
		final byte[] B = new byte[128 * memCost * parrallelParam];
		final byte[] XY = new byte[256 * memCost];
		final byte[] V = new byte[128 * memCost * cpuCost];

		pbkdf2(mac, salt, 1, B, parrallelParam * 128 * memCost);
		for (int i = 0; i < parrallelParam; i++)
			smix(B, i * 128 * memCost, memCost, cpuCost, V, XY);
		pbkdf2(mac, B, 1, r, keyLength);
		return r;
	}

	private static void smix(byte[] B, int Bi, int r, int N, byte[] V, byte[] XY) {
		int Xi = 0;
		int Yi = 128 * r;
		int i;

		System.arraycopy(B, Bi, XY, Xi, 128 * r);
		for (i = 0; i < N; i++) {
			System.arraycopy(XY, Xi, V, i * (128 * r), 128 * r);
			blockmix_salsa8(XY, Xi, Yi, r);
		}
		for (i = 0; i < N; i++) {
			int integrify = ByteHelper.readInt(XY, Xi + (2 * r - 1) * 64);
			final int j = integrify & (N - 1);
			blockxor(V, j * (128 * r), XY, Xi, 128 * r);
			blockmix_salsa8(XY, Xi, Yi, r);
		}
		System.arraycopy(XY, Xi, B, Bi, 128 * r);
	}

	private static void blockmix_salsa8(byte[] BY, int Bi, int Yi, int r) {
		byte[] X = new byte[64];

		System.arraycopy(BY, Bi + (2 * r - 1) * 64, X, 0, 64);

		for (int i = 0; i < 2 * r; i++) {
			blockxor(BY, i * 64, X, 0, 64);
			salsa20_8(X);
			System.arraycopy(X, 0, BY, Yi + (i * 64), 64);
		}

		for (int i = 0; i < r; i++)
			System.arraycopy(BY, Yi + (i * 2) * 64, BY, Bi + (i * 64), 64);

		for (int i = 0; i < r; i++)
			System.arraycopy(BY, Yi + (i * 2 + 1) * 64, BY, Bi + (i + r) * 64, 64);
	}

	private static int role(int a, int b) {
		return (a << b) | (a >>> (32 - b));
	}

	private static void salsa20_8(byte[] B) {
		int[] buf32 = new int[16];

		for (int i = 0; i < 16; i++)
			buf32[i] = ByteHelper.readInt(B, i * 4);

		final int x[] = Arrays.copyOf(buf32, 16);
		for (int i = 0; i < 4; i++) {
			x[4] ^= role(x[0] + x[12], 7);
			x[8] ^= role(x[4] + x[0], 9);
			x[12] ^= role(x[8] + x[4], 13);
			x[0] ^= role(x[12] + x[8], 18);
			x[9] ^= role(x[5] + x[1], 7);
			x[13] ^= role(x[9] + x[5], 9);
			x[1] ^= role(x[13] + x[9], 13);
			x[5] ^= role(x[1] + x[13], 18);
			x[14] ^= role(x[10] + x[6], 7);
			x[2] ^= role(x[14] + x[10], 9);
			x[6] ^= role(x[2] + x[14], 13);
			x[10] ^= role(x[6] + x[2], 18);
			x[3] ^= role(x[15] + x[11], 7);
			x[7] ^= role(x[3] + x[15], 9);
			x[11] ^= role(x[7] + x[3], 13);
			x[15] ^= role(x[11] + x[7], 18);
			x[1] ^= role(x[0] + x[3], 7);
			x[2] ^= role(x[1] + x[0], 9);
			x[3] ^= role(x[2] + x[1], 13);
			x[0] ^= role(x[3] + x[2], 18);
			x[6] ^= role(x[5] + x[4], 7);
			x[7] ^= role(x[6] + x[5], 9);
			x[4] ^= role(x[7] + x[6], 13);
			x[5] ^= role(x[4] + x[7], 18);
			x[11] ^= role(x[10] + x[9], 7);
			x[8] ^= role(x[11] + x[10], 9);
			x[9] ^= role(x[8] + x[11], 13);
			x[10] ^= role(x[9] + x[8], 18);
			x[12] ^= role(x[15] + x[14], 7);
			x[13] ^= role(x[12] + x[15], 9);
			x[14] ^= role(x[13] + x[12], 13);
			x[15] ^= role(x[14] + x[13], 18);
		}

		for (int i = 0; i < 16; i++)
			ByteHelper.writeInt(buf32[i] + x[i], B, i * 4);
	}

	private static void blockxor(byte[] source, int SourceOffset, byte[] dest, int destOffset, int len) {
		for (int i = 0; i < len; i++) {
			dest[destOffset + i] ^= source[SourceOffset + i];
		}
	}

	private static int integerify(byte[] B, int Bi, int r) {
		return ByteHelper.readInt(B, Bi + (2 * r - 1) * 64);
	}

	public static void pbkdf2(Mac mac, byte[] salt, int count, byte[] derivedKeySink, int derivedKeyLen) throws GeneralSecurityException {
		final int hLen = mac.getMacLength();

		if (derivedKeyLen > (Math.pow(2, 32) - 1) * hLen)
			throw new GeneralSecurityException("key too long");

		final byte[] U = new byte[hLen];

		final int last = (int) Math.ceil((double) derivedKeyLen / hLen);
		final int lastPos = derivedKeyLen - (last - 1) * hLen;

		final byte[] block1 = Arrays.copyOf(salt, salt.length + 4);

		for (int i = 1; i <= last; i++) {
			ByteHelper.writeInt(i, block1, salt.length);
			mac.update(block1);
			mac.doFinal(U, 0);
			final byte[] T = Arrays.copyOf(U, hLen);
			for (int j = 1; j < count; j++) {
				mac.update(U);
				mac.doFinal(U, 0);
				for (int k = 0; k < hLen; k++)
					T[k] ^= U[k];
			}
			System.arraycopy(T, 0, derivedKeySink, (i - 1) * hLen, (i == last ? lastPos : hLen));
		}
	}

	public static String scrypt(String passwd, int cpuCost, int memCost, int parralelParam) throws GeneralSecurityException {
		OH.assertBetween(cpuCost, 1, Short.MAX_VALUE, "cpuCost");
		OH.assertBetween(memCost, 1, Short.MAX_VALUE, "memCost");
		OH.assertBetween(parralelParam, 1, Short.MAX_VALUE, "parallelParam");
		int i = 1;
		while (i < cpuCost)
			i <<= 1;
		cpuCost = i;
		byte[] salt = new byte[16];
		SecureRandom.getInstance("SHA1PRNG").nextBytes(salt);

		byte[] derived = ScryptUtils.scrypt(passwd.getBytes(), salt, cpuCost, memCost, parralelParam, 32);

		StringBuilder sb = new StringBuilder((salt.length + derived.length) * 2);
		long param = ((long) MH.indexOfOnlyBitSet(cpuCost) << 32L) | (memCost << 16L) | (parralelParam);
		SH.toString(param, 62, sb);
		sb.append(DELIM);
		EncoderUtils.encode64(salt, sb);
		sb.append(DELIM);
		EncoderUtils.encode64(derived, sb);
		return sb.toString();
	}

	public static boolean check(String password, String hashed) throws GeneralSecurityException {
		final String[] parts = SH.split(DELIM, hashed);
		if (parts.length != 3)
			throw new IllegalArgumentException("Invalid hashed value");

		long params = SH.parseLong(parts[0], 62);
		byte[] salt = EncoderUtils.decode64(parts[1]);
		byte[] derived0 = EncoderUtils.decode64(parts[2]);

		int cpuCost = (int) Math.pow(2, (params >> 32L) & 0xffff);
		int memCost = (int) params >> 16 & 0xffff;
		int parallelParam = (int) params & 0xffff;

		byte[] derived1 = ScryptUtils.scrypt(password.getBytes(), salt, cpuCost, memCost, parallelParam, 32);

		return Arrays.equals(derived0, derived1);
	}

}