package com.f1.utils;

public class BitHelper {

	public static int indexOfFirstBit(long n) {
		if (n == 0)
			return -1;
		return Long.numberOfTrailingZeros(n);
	}
	public static int indexOfFirstBitBefore(long n, int endExclusive) {
		if (n == 0)
			return -1;
		int r = Long.numberOfTrailingZeros(n);
		return r >= endExclusive ? -1 : r;
	}
	public static int indexOfFirstBitAfter(long n, int startInclusive) {
		if (startInclusive >= 64)
			return -1;
		n >>>= startInclusive;
		if (n == 0)
			return -1;
		int r = Long.numberOfTrailingZeros(n) + startInclusive;
		return r;
	}
	public static int indexOfFirstBitBetween(long n, int startInclusive, int endExclusive) {
		if (startInclusive >= 64)
			return -1;
		n >>>= startInclusive;
		if (n == 0)
			return -1;
		int r = Long.numberOfTrailingZeros(n) + startInclusive;
		return r >= endExclusive ? -1 : r;
	}

}
