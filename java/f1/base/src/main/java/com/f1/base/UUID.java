package com.f1.base;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

/**
 * Most:<BR>
 * 0xFFFFFFFF00000000 time_low<BR>
 * 0x00000000FFFF0000 time_mid<BR>
 * 0x000000000000F000 version<BR>
 * 0x0000000000000FFF time_hi<BR>
 * 
 * Least:<BR>
 * 0xC000000000000000 variant<BR>
 * 0x3FFF000000000000 clock_seq<BR>
 * 0x0000FFFFFFFFFFFF node<BR>
 **/
public class UUID implements ToStringable, Comparable<UUID> {

	private static final long UNIX_EPOC_FOR_1582_10_15_IN_HUNDREDS_OF_NANOS = -122192928000000000L;//see calcUuidEpoc()

	public static final UUID EMPTY = new UUID(0, 0);

	final private long mostSig, leastSig;

	public static long unixMillisToUuidEpoc(long time) {
		return (time * 10000L) - UNIX_EPOC_FOR_1582_10_15_IN_HUNDREDS_OF_NANOS;//convert millis -> 100's of nanos
	}
	public static long uuidToUnixMillisEpoc(long time) {
		return (time + UNIX_EPOC_FOR_1582_10_15_IN_HUNDREDS_OF_NANOS) / 10000L;//convert 100's of nanos -> millis
	}
	public static long unixNanosToUuidEpoc(long time) {
		return (time / 100L - UNIX_EPOC_FOR_1582_10_15_IN_HUNDREDS_OF_NANOS);//convert nanos -> 100's of nanos
	}
	public static long uuidToUnixNanosEpoc(long time) {
		return (time + UNIX_EPOC_FOR_1582_10_15_IN_HUNDREDS_OF_NANOS) * 100L;//convert 100's of nanos -> nanos
	}

	public UUID(int version, int variant, DateMillis time, int clockSeq, long node) {
		this(version, variant, unixMillisToUuidEpoc(time.getDate()), clockSeq, node);
	}
	public UUID(int version, int variant, DateNanos time, int clockSeq, long node) {
		this(version, variant, unixNanosToUuidEpoc(time.getTimeNanos()), clockSeq, node);
	}

	//time is 

	/**
	 * 
	 * @param version
	 *            4 bit number (0-15)
	 * @param variant
	 *            2 bit number (0-3)
	 * @param time
	 *            60 bit number (100-nanosecond intervals since midnight 15 October 1582)
	 * @param clockSeq
	 *            14 bit number
	 * @param node
	 *            48 bit number
	 */
	public UUID(int version, int variant, long time, int clockSeq, long node) {
		if ((version & 0xfL) != version)
			throw new RuntimeException("Invalid version: " + version + ", must be betwween 0 and " + 0xfL);
		if ((time & 0xfffffffffffffffL) != time)
			throw new RuntimeException("Invalid time: " + time + ", must be betwween 0 and " + 0xfffffffffffffffL);
		if ((variant & 0x3L) != variant)
			throw new RuntimeException("Invalid variant: " + variant + ", must be betwween 0 and " + 0x3L);
		if ((clockSeq & 0x3fffL) != clockSeq)
			throw new RuntimeException("Invalid clockSeq: " + clockSeq + ", must be betwween 0 and " + 0x3fffL);
		if ((node & 0xffffffffffffL) != node)
			throw new RuntimeException("Invalid node: " + node + ", must be betwween 0 and " + 0xffffffffffffL);

		long timeHigh = time >> 48L;
		long timeLow = time & 0xffffffffL;
		long timeMid = (time & 0xffff00000000L) >> 32L;
		long m = timeLow << 32L;
		m |= timeMid << 16L;
		m |= version << 12L;
		m |= timeHigh;
		long l = (long) variant << 62L;
		//		l |= clockSeq << 48L;
		//		l |= node;
		this.mostSig = m;
		this.leastSig = l;
	}
	public UUID(long mostSig, long leastSig) {
		this.mostSig = mostSig;
		this.leastSig = leastSig;
	}
	public UUID(String name) {
		int start = 0;
		int end = name.indexOf('-', start);
		long mostSigBits = parseHex(name, start, end);

		mostSigBits <<= 16;
		start = end + 1;
		end = name.indexOf('-', start);
		mostSigBits |= parseHex(name, start, end);
		mostSigBits <<= 16;

		start = end + 1;
		end = name.indexOf('-', start);
		mostSigBits |= parseHex(name, start, end);

		start = end + 1;
		end = name.indexOf('-', start);
		long leastSigBits = parseHex(name, start, end);
		leastSigBits <<= 48;

		start = end + 1;
		leastSigBits |= parseHex(name, start, name.length());
		this.mostSig = mostSigBits;
		this.leastSig = leastSigBits;
	}

	public UUID(Random sr) {
		long mostSigBits = sr.nextLong();
		long leastSigBits = sr.nextLong();

		mostSigBits &= 0xffffffffffff0fffL;// clear version
		mostSigBits |= 0x0000000000004000L;//set to version 4;
		leastSigBits &= 0x3fffffffffffffffL; // clear variant
		leastSigBits |= 0x8000000000000000L; // set to IETF variant

		this.mostSig = mostSigBits;
		this.leastSig = leastSigBits;
	}

	public String toString() {
		return toString(new StringBuilder()).toString();
	}
	public String toHex() {
		return toHex(new StringBuilder()).toString();
	}

	public StringBuilder toHex(StringBuilder sink) {
		sink.append("0x");
		sink.append(toByte((mostSig & 0xf000000000000000L) >> 60L));
		sink.append(toByte((mostSig & 0x0f00000000000000L) >> 56L));
		sink.append(toByte((mostSig & 0x00f0000000000000L) >> 52L));
		sink.append(toByte((mostSig & 0x000f000000000000L) >> 48L));
		sink.append(toByte((mostSig & 0x0000f00000000000L) >> 44L));
		sink.append(toByte((mostSig & 0x00000f0000000000L) >> 40L));
		sink.append(toByte((mostSig & 0x000000f000000000L) >> 36L));
		sink.append(toByte((mostSig & 0x0000000f00000000L) >> 32L));
		sink.append(toByte((mostSig & 0x00000000f0000000L) >> 28L));
		sink.append(toByte((mostSig & 0x000000000f000000L) >> 24L));
		sink.append(toByte((mostSig & 0x0000000000f00000L) >> 20L));
		sink.append(toByte((mostSig & 0x00000000000f0000L) >> 16L));
		sink.append(toByte((mostSig & 0x000000000000f000L) >> 12L));
		sink.append(toByte((mostSig & 0x0000000000000f00L) >> 8L));
		sink.append(toByte((mostSig & 0x00000000000000f0L) >> 4L));
		sink.append(toByte((mostSig & 0x000000000000000fL) >> 0L));
		sink.append(toByte((leastSig & 0xf000000000000000L) >> 60L));
		sink.append(toByte((leastSig & 0x0f00000000000000L) >> 56L));
		sink.append(toByte((leastSig & 0x00f0000000000000L) >> 52L));
		sink.append(toByte((leastSig & 0x000f000000000000L) >> 48L));
		sink.append(toByte((leastSig & 0x0000f00000000000L) >> 44L));
		sink.append(toByte((leastSig & 0x00000f0000000000L) >> 40L));
		sink.append(toByte((leastSig & 0x000000f000000000L) >> 36L));
		sink.append(toByte((leastSig & 0x0000000f00000000L) >> 32L));
		sink.append(toByte((leastSig & 0x00000000f0000000L) >> 28L));
		sink.append(toByte((leastSig & 0x000000000f000000L) >> 24L));
		sink.append(toByte((leastSig & 0x0000000000f00000L) >> 20L));
		sink.append(toByte((leastSig & 0x00000000000f0000L) >> 16L));
		sink.append(toByte((leastSig & 0x000000000000f000L) >> 12L));
		sink.append(toByte((leastSig & 0x0000000000000f00L) >> 8L));
		sink.append(toByte((leastSig & 0x00000000000000f0L) >> 4L));
		sink.append(toByte((leastSig & 0x000000000000000fL) >> 0L));
		return sink;
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append(toByte((mostSig & 0xf000000000000000L) >> 60L));
		sink.append(toByte((mostSig & 0x0f00000000000000L) >> 56L));
		sink.append(toByte((mostSig & 0x00f0000000000000L) >> 52L));
		sink.append(toByte((mostSig & 0x000f000000000000L) >> 48L));
		sink.append(toByte((mostSig & 0x0000f00000000000L) >> 44L));
		sink.append(toByte((mostSig & 0x00000f0000000000L) >> 40L));
		sink.append(toByte((mostSig & 0x000000f000000000L) >> 36L));
		sink.append(toByte((mostSig & 0x0000000f00000000L) >> 32L));
		sink.append('-');
		sink.append(toByte((mostSig & 0x00000000f0000000L) >> 28L));
		sink.append(toByte((mostSig & 0x000000000f000000L) >> 24L));
		sink.append(toByte((mostSig & 0x0000000000f00000L) >> 20L));
		sink.append(toByte((mostSig & 0x00000000000f0000L) >> 16L));
		sink.append('-');
		sink.append(toByte((mostSig & 0x000000000000f000L) >> 12L));
		sink.append(toByte((mostSig & 0x0000000000000f00L) >> 8L));
		sink.append(toByte((mostSig & 0x00000000000000f0L) >> 4L));
		sink.append(toByte((mostSig & 0x000000000000000fL) >> 0L));
		sink.append('-');
		sink.append(toByte((leastSig & 0xf000000000000000L) >> 60L));
		sink.append(toByte((leastSig & 0x0f00000000000000L) >> 56L));
		sink.append(toByte((leastSig & 0x00f0000000000000L) >> 52L));
		sink.append(toByte((leastSig & 0x000f000000000000L) >> 48L));
		sink.append('-');
		sink.append(toByte((leastSig & 0x0000f00000000000L) >> 44L));
		sink.append(toByte((leastSig & 0x00000f0000000000L) >> 40L));
		sink.append(toByte((leastSig & 0x000000f000000000L) >> 36L));
		sink.append(toByte((leastSig & 0x0000000f00000000L) >> 32L));
		sink.append(toByte((leastSig & 0x00000000f0000000L) >> 28L));
		sink.append(toByte((leastSig & 0x000000000f000000L) >> 24L));
		sink.append(toByte((leastSig & 0x0000000000f00000L) >> 20L));
		sink.append(toByte((leastSig & 0x00000000000f0000L) >> 16L));
		sink.append(toByte((leastSig & 0x000000000000f000L) >> 12L));
		sink.append(toByte((leastSig & 0x0000000000000f00L) >> 8L));
		sink.append(toByte((leastSig & 0x00000000000000f0L) >> 4L));
		sink.append(toByte((leastSig & 0x000000000000000fL) >> 0L));
		return sink;
	}

	private static char toByte(long n) {
		switch ((int) n & 0xf) {
			case 0x0:
				return '0';
			case 0x1:
				return '1';
			case 0x2:
				return '2';
			case 0x3:
				return '3';
			case 0x4:
				return '4';
			case 0x5:
				return '5';
			case 0x6:
				return '6';
			case 0x7:
				return '7';
			case 0x8:
				return '8';
			case 0x9:
				return '9';
			case 0xa:
				return 'a';
			case 0xb:
				return 'b';
			case 0xc:
				return 'c';
			case 0xd:
				return 'd';
			case 0xe:
				return 'e';
			case 0xf:
				return 'f';
			default:
				throw new RuntimeException("" + n);
		}
	}
	private static long parseChar(char n) {
		switch (n) {
			case '0':
				return 0;
			case '1':
				return 1;
			case '2':
				return 2;
			case '3':
				return 3;
			case '4':
				return 4;
			case '5':
				return 5;
			case '6':
				return 6;
			case '7':
				return 7;
			case '8':
				return 8;
			case '9':
				return 9;
			case 'a':
			case 'A':
				return 10;
			case 'b':
			case 'B':
				return 11;
			case 'c':
			case 'C':
				return 12;
			case 'd':
			case 'D':
				return 13;
			case 'e':
			case 'E':
				return 14;
			case 'f':
			case 'F':
				return 15;
			default:
				throw new RuntimeException("Bad UUID, invalid hex char: " + n);
		}
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null || other.getClass() != UUID.class)
			return false;
		UUID o = (UUID) other;
		return o.mostSig == mostSig && o.leastSig == leastSig;
	}

	private static long parseHex(CharSequence cs, int start, int end) {
		if (end == -1)
			throw new RuntimeException("Bad UUID, Missing dash (-)");
		long r = 0;
		for (int n = start; n < end; n++)
			r = (r << 4) + parseChar(cs.charAt(n));
		return r;
	}

	@Override
	public int compareTo(UUID o) {
		if (mostSig < o.mostSig)
			return -1;
		if (mostSig > o.mostSig)
			return 1;
		if (leastSig < o.leastSig)
			return -1;
		if (leastSig > o.leastSig)
			return 1;
		return 0;
	}
	public int hashCode() {
		long n = mostSig ^ leastSig;
		return ((int) (n >> 32)) ^ (int) n;
	}

	public long getLeastSignificantBits() {
		return this.leastSig;
	}
	public long getMostSignificantBits() {
		return this.mostSig;
	}

	//1=Time-based UUID, 2=DCE security UUID, 3=Name-based UUID, 4=Randomly generated UUID
	public int getVersion() {
		return (int) ((mostSig >> 12) & 0x0f);
	}
	public int getVariant() {
		return (int) (leastSig >>> 62L);
	}

	public long getUnixTimestampMillis() {
		return uuidToUnixMillisEpoc(getTimestamp());
	}
	public long getUnixTimestampNanos() {
		return uuidToUnixNanosEpoc(getTimestamp());
	}
	public long getTimestamp() {
		assertTimeBased();
		return (mostSig & 0x0FFFL) << 48 | ((mostSig >> 16) & 0x0FFFFL) << 32 | mostSig >>> 32;
	}
	public int getClockSequence() {
		assertTimeBased();
		return (int) ((leastSig & 0x3FFF000000000000L) >>> 48);
	}
	public long getNode() {
		assertTimeBased();
		return leastSig & 0x0000FFFFFFFFFFFFL;
	}
	private void assertTimeBased() {
		if (getVersion() != 1)
			throw new UnsupportedOperationException("Not a time-based UUID (version is " + getVersion() + " but must be 1)");
	}
	public static void main(String a[]) throws InterruptedException {
		System.out.println(new UUID("adfadf"));
		long n = 1L << 62l;
		System.out.println(n >>> 62L);

		caclulateUuidEpoc();
		UUID t = new UUID(1, 1, unixMillisToUuidEpoc(System.currentTimeMillis()), 123, 7654321);
		System.out.println("ver:" + t.getVersion());
		System.out.println("var:" + t.getVariant());
		System.out.println("tim:" + new Date(t.getUnixTimestampMillis()));
		System.out.println("seq:" + t.getClockSequence());
		System.out.println("nod:" + t.getNode());

		System.out.println(new UUID(0, 0));
		System.out.println(new UUID(0xffffffffffffffffL, 0xffffffffffffffffL));
		System.out.println(new UUID(0xabcdefabcdefabcdL, 0xabcdefabcdefabcdL));
		//		for (int i = 0; i < 10000; i++)
		//			UUID.fromString("1-2-3-4-5").toString();
		//		StringBuilder sb = new StringBuilder();
		//		for (int i = 0; i < 10000; i++) {
		//			new UID128("1-2-3-4-5").toString(sb);
		//			sb.setLength(0);
		//		}
		//		long start = System.nanoTime();
		//		for (int n = 0; n < 10; n++) {
		//			for (int i = 0; i < 1000000; i++)
		//				UUID.fromString("1-2-3-4-5").toString();
		//			long next = System.nanoTime();
		//			for (int i = 0; i < 1000000; i++) {
		//				new UID128("1-2-3-4-5").toString(sb);
		//				sb.setLength(0);
		//			}
		//			long last = System.nanoTime();
		//			System.out.println(next - start);
		//			System.out.println(last - next);
		//			System.out.println();
		//		}
	}
	private static void caclulateUuidEpoc() {
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("UTC"));
		c.setTimeInMillis(0);
		c.set(1582, 9, 15);
		System.out.println(c.getTimeInMillis() * 10000L);
	}
}
