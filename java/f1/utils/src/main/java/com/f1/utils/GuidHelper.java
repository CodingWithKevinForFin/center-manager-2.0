package com.f1.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

public class GuidHelper {

	static private int[] REMAININGS = new int[128];
	final private static double LOG2 = Math.log(2);
	static {
		for (int i = 2; i < REMAININGS.length; i++)
			REMAININGS[i] = (int) MH.round((LOG2 * 128 / Math.log(i)), MH.ROUND_CEILING);
	}
	final static private byte[] localHost = EH.getLocalHost().getBytes();

	final private byte data[];
	final private Random random = new Random();
	final private MessageDigest md5;

	final private FastByteArrayDataInputStream dis;
	final private StringBuilder sb = new StringBuilder(100);

	final private int timeOffset;
	final private int randomOffset;

	public GuidHelper() {
		this.dis = new FastByteArrayDataInputStream(OH.EMPTY_BYTE_ARRAY);
		this.timeOffset = localHost.length;
		this.randomOffset = localHost.length + 8;
		this.data = Arrays.copyOf(localHost, localHost.length + 16); // time + random
		try {
			md5 = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private String getRandomGUID() {
		return getRandomGUID(16);
	}
	private String getRandomGUID(int base) {
		sb.setLength(0);
		getRandomGUID(base, sb);
		return sb.toString();
	}
	public void getRandomGUID(int base, StringBuilder sb) {

		try {

			int buf = 0;
			long bitsBuffered = 0;
			int bitsPerBase = MH.getDigitsCount(base - 1, 2);
			int mask = (1 << bitsPerBase) - 1;
			int remaining = REMAININGS[base];
			for (;;) {
				if (dis.available() == 0) {
					ByteHelper.writeLong(EH.currentTimeMillis() ^ System.nanoTime(), data, timeOffset);
					ByteHelper.writeLong(random.nextLong(), data, randomOffset);
					md5.update(data);
					dis.reset(md5.digest());
				}
				buf = buf | (MH.toUnsignedInt(dis.readByte()) << bitsBuffered);
				bitsBuffered += 8;
				while (bitsBuffered >= bitsPerBase) {
					int num = buf & mask;
					if (num < base) {
						sb.append(SH.getDigitToChar(num));
						remaining--;
						if (remaining <= 0)
							return;
					}
					buf >>= bitsPerBase;
					bitsBuffered -= bitsPerBase;
				}
			}

		} catch (Exception e) {
			throw OH.toRuntime(e);
		}

	}

	static private ThreadLocal<GuidHelper> threadLocal = new ThreadLocal<GuidHelper>() {
		@Override
		protected GuidHelper initialValue() {
			return new GuidHelper();
		}
	};

	public static String getGuid() {
		return threadLocal.get().getRandomGUID();
	}
	public static Random getThreadlocalRandom() {
		return threadLocal.get().getRandom();
	}

	public Random getRandom() {
		return random;
	}

	public static String getGuid(int base) {
		return threadLocal.get().getRandomGUID(base);
	}
	public static void getGuid(int base, StringBuilder sink) {
		threadLocal.get().getRandomGUID(base, sink);
	}

	public static void main(String a[]) {
		System.out.println(getGuid(2));
		System.out.println(getGuid(16));
		System.out.println(getGuid(62));
		System.out.println(getGuid(84));
	}
}
