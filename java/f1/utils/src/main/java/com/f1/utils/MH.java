package com.f1.utils;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.math.PrimitiveMathManager;

import sun.misc.DoubleConsts;
import sun.misc.FloatConsts;

/**
 * 
 * Math Helper
 * 
 */

public class MH {
	public static final double TAU = Math.PI * 2;
	private static final long TO_THE_TENTH[];
	private static final double TO_THE_TENTH_DOUBLES[];
	public static final double MAX_LONG_AS_DOUBLE = Long.MAX_VALUE;
	public static final double MIN_LONG_AS_DOUBLE = Long.MIN_VALUE;
	public static final long MAX_UNSIGNED_INT = (long) Integer.MAX_VALUE - Integer.MIN_VALUE;
	public static final int MAX_UNSIGNED_BYTE = Byte.MAX_VALUE - Byte.MIN_VALUE;
	public static final int MAX_UNSIGNED_SHORT = Short.MAX_VALUE - Short.MIN_VALUE;

	public static final int ROUND_HALF_EVEN = BigDecimal.ROUND_HALF_EVEN;
	public static final int ROUND_HALF_UP = BigDecimal.ROUND_HALF_UP;
	public static final int ROUND_HALF_DOWN = BigDecimal.ROUND_HALF_DOWN;
	public static final int ROUND_UP = BigDecimal.ROUND_UP;
	public static final int ROUND_DOWN = BigDecimal.ROUND_DOWN;
	public static final int ROUND_FLOOR = BigDecimal.ROUND_FLOOR;
	public static final int ROUND_CEILING = BigDecimal.ROUND_CEILING;
	public static final double EULER_MASCHERONI_CONSTANT = 0.57721566490153286060;
	public static final double eps0 = Math.ulp(1.0);

	static {
		List<Long> longs = new ArrayList<Long>();
		List<Double> doubles = new ArrayList<Double>();
		long c = 1;
		for (; c < Long.MAX_VALUE / 10L; c *= 10) {
			longs.add(c);
			doubles.add((double) c);
		}
		for (double d = c; d < Double.MAX_VALUE; d *= 10) {
			doubles.add(d);
		}
		TO_THE_TENTH = AH.toArrayLong(longs);
		TO_THE_TENTH_DOUBLES = AH.toArrayDouble(doubles);
	}

	/**
	 * Calculates ten to the power of i
	 * 
	 * @param i
	 * @return
	 */
	public static double toTheTenthDouble(int i) {
		if (i >= TO_THE_TENTH_DOUBLES.length)
			return Double.MAX_VALUE;
		return TO_THE_TENTH_DOUBLES[i];
	}

	/**
	 * Calculates ten to the power of i
	 * 
	 * @param i
	 * @return
	 */
	public static long toTheTenth(int i) {
		return TO_THE_TENTH[i];
	}
	/**
	 * Gets the max power that {@link #toTheTenth(int)} can calculate
	 * 
	 * @return
	 */
	public static int maxToTheTenth() {
		return TO_THE_TENTH.length - 1;
	}

	/** number of bytes in a kilobyte */
	public static final long KILOBYTES = 1024;

	/** number of bytes in a megabyte */
	public static final long MEGABYTES = KILOBYTES * KILOBYTES;

	/** number of bytes in a gigabyte */
	public static final long GIGABYTES = MEGABYTES * KILOBYTES;

	/** number of bytes in a terabyte */
	public static final long TERABYTES = GIGABYTES * KILOBYTES;

	// returns true if any of the bits specified in the mask are true in the
	// flag
	// if mask==0, will always return false
	/**
	 * Checks if any of the bits in the flags are true
	 * 
	 * @param flags
	 * @param mask
	 * @return
	 */
	static public boolean anyBits(int flags, int mask) {
		return (flags & mask) != 0;
	}

	/**
	 * Checks if any of the bits in the flags are true
	 * 
	 * @param flags
	 * @param mask
	 * @return
	 */
	static public boolean anyBits(byte flags, byte mask) {
		return (flags & mask) != 0;
	}

	/**
	 * Checks if any of the bits in the flags are true
	 * 
	 * @param flags
	 * @param mask
	 * @return
	 */
	static public boolean anyBits(long flags, long mask) {
		return (flags & mask) != 0;
	}

	/**
	 * Checks if any of the bits in the flags are true
	 * 
	 * @param flags
	 * @param mask
	 * @return
	 */
	static public boolean areAnyBitsSet(int flags, int mask) {
		return (flags & mask) != 0;
	}

	/**
	 * Checks if any of the bits in the flags are true
	 * 
	 * @param flags
	 * @param mask
	 * @return
	 */
	static public boolean areAnyBitsSet(long flags, long mask) {
		return (flags & mask) != 0;
	}

	/**
	 * Checks if all of the bits in the flags are true
	 * 
	 * @param flags
	 * @param mask
	 * @return
	 */
	// returns true if all of the bits specified in the mask are true in the
	// flag
	static public boolean areAllBitsSet(int flags, int mask) {
		return (flags & mask) == mask;
	}

	/**
	 * Checks if all of the bits in the flags are true
	 * 
	 * @param flags
	 * @param mask
	 * @return
	 */
	static public boolean areAllBitsSet(long flags, long mask) {
		return (flags & mask) == mask;
	}

	/**
	 * Checks if all of the bits in the flags are true
	 * 
	 * @param flags
	 * @param mask
	 * @return
	 */
	static public boolean allBits(int flags, int mask) {
		return (flags & mask) == mask;
	}

	/**
	 * Checks if all of the bits in the flags are true
	 * 
	 * @param flags
	 * @param mask
	 * @return
	 */
	static public boolean allBits(long flags, long mask) {
		return (flags & mask) == mask;
	}

	/**
	 * Returns the position of the bit set.
	 * 
	 * @param i
	 *            , where i has only one bit set
	 * @return
	 */
	static public int indexOfOnlyBitSet(long i) {
		if (i > Integer.MAX_VALUE) {
			return 32 + indexOfOnlyBitSet((int) (i >> 32));
		} else if (i == Long.MIN_VALUE)
			return 63;
		else
			return indexOfOnlyBitSet((int) i);
	}
	/**
	 * Returns the position of the bit set.
	 * 
	 * @param i
	 *            , where i has only one bit set
	 * @return
	 */
	static public int indexOfOnlyBitSet(int i) {
		switch (i) {
			case 1 << 0:
				return 0;
			case 1 << 1:
				return 1;
			case 1 << 2:
				return 2;
			case 1 << 3:
				return 3;
			case 1 << 4:
				return 4;
			case 1 << 5:
				return 5;
			case 1 << 6:
				return 6;
			case 1 << 7:
				return 7;
			case 1 << 8:
				return 8;
			case 1 << 9:
				return 9;
			case 1 << 10:
				return 10;
			case 1 << 11:
				return 11;
			case 1 << 12:
				return 12;
			case 1 << 13:
				return 13;
			case 1 << 14:
				return 14;
			case 1 << 15:
				return 15;
			case 1 << 16:
				return 16;
			case 1 << 17:
				return 17;
			case 1 << 18:
				return 18;
			case 1 << 19:
				return 19;
			case 1 << 20:
				return 20;
			case 1 << 21:
				return 21;
			case 1 << 22:
				return 22;
			case 1 << 23:
				return 23;
			case 1 << 24:
				return 24;
			case 1 << 25:
				return 25;
			case 1 << 26:
				return 26;
			case 1 << 27:
				return 27;
			case 1 << 28:
				return 28;
			case 1 << 29:
				return 29;
			case 1 << 30:
				return 30;
			case 1 << 31:
				return 31;
			default:
				return -1;
		}
	}

	/**
	 * Returns the position of the last bit set.
	 * 
	 * @param i
	 * @return
	 */
	public static int indexOfLastBitSet(int bitmask) {
		if (bitmask == 0)
			return 0;
		return indexOfOnlyBitSet((((bitmask - 1) & bitmask) ^ bitmask));
	}

	// Zero based index of the first bit set on or after the start index (-1 if
	// no bits set)
	/**
	 * Returns the index of the first bit set starting at position "start".
	 * 
	 * @param bitmask
	 * @param start
	 * @return returns -1 if no bits are set
	 */
	public static int indexOfBitSet(long bitmask, int start) {
		OH.assertLt(start, Long.SIZE);
		int i = start;
		for (long mask = (1L << start); i < Long.SIZE; i++, mask <<= 1)
			if (areAnyBitsSet(bitmask, mask))
				return i;
		return -1;
	}
	/**
	 * Returns the index of the first bit set within the range [start, endExclusive).
	 * 
	 * @param bitmask
	 * @param start
	 * @param endExclusive
	 * @return
	 */
	public static int indexOfBitSet(long bitmask, int startInclusive, int endExclusive) {
		return BitHelper.indexOfFirstBitBetween(bitmask, startInclusive, endExclusive);
	}
	public static int indexOfBitSet(long bitmask) {
		return BitHelper.indexOfFirstBit(bitmask);
	}
	public static int indexOfBitSetBefore(long bitmask, int endExclusive) {
		return BitHelper.indexOfFirstBitBefore(bitmask, endExclusive);
	}
	public static int indexOfBitSetAfter(long bitmask, int startInclusive) {
		return BitHelper.indexOfFirstBitAfter(bitmask, startInclusive);
	}

	/**
	 * Returns the index of the first bit set starting at position "start"
	 * 
	 * @param bitmask
	 * @param start
	 * @return
	 */
	public static int indexOfBitSet(int bitmask, int start) {
		return BitHelper.indexOfFirstBitAfter(bitmask, start);
	}
	/**
	 * Returns value mod modulous
	 * 
	 * @param value
	 * @param modulous
	 * @return value, where value = value mod modulous
	 */

	public static int mod(int value, int modulous) {
		return (value %= modulous) < 0 ? modulous + value : value;
	}

	/**
	 * Returns value mod modulous
	 * 
	 * @param value
	 * @param modulous
	 * @return value, where value = value mod modulous
	 */
	public static long mod(long value, long modulous) {
		return (value %= modulous) < 0 ? modulous + value : value;
	}
	/**
	 * Clears the bits set by bitsToClear
	 * 
	 * @param bits
	 * @param bitsToClear
	 * @return
	 */
	public static byte clearBits(byte bits, byte bitsToClear) {
		return (byte) (bits ^ (bits & bitsToClear));
	}
	/**
	 * Clears the bits set by bitsToClear
	 * 
	 * @param bits
	 * @param bitsToClear
	 * @return
	 */
	public static int clearBits(int bits, int bitsToClear) {
		return bits ^ (bits & bitsToClear);
	}
	/**
	 * Clears the bits set by bitsToClear
	 * 
	 * @param bits
	 * @param bitsToClear
	 * @return
	 */
	public static short clearBits(short bits, short bitsToClear) {
		return (short) (bits ^ (bits & bitsToClear));
	}
	/**
	 * Clears the bits set by bitsToClear
	 * 
	 * @param bits
	 * @param bitsToClear
	 * @return
	 */
	public static long clearBits(long bits, long bitsToClear) {
		return bits ^ (bits & bitsToClear);
	}
	/**
	 * Turns bit set by bitsToSet on or off
	 * 
	 * @param bits
	 * @param bitsToSet
	 * @param on
	 * @return
	 */
	public static int setBits(int bits, int bitsToSet, boolean on) {
		return on ? bits | bitsToSet : clearBits(bits, bitsToSet);
	}

	/**
	 * Turns bit set by bitsToSet on or off
	 * 
	 * @param bits
	 * @param bitsToSet
	 * @param on
	 * @return
	 */
	public static short setBits(short bits, short bitsToSet, boolean on) {
		return on ? (short) (bits | bitsToSet) : clearBits(bits, bitsToSet);
	}
	/**
	 * Turns bit set by bitsToSet on or off
	 * 
	 * @param bits
	 * @param bitsToSet
	 * @param on
	 * @return
	 */
	public static byte setBits(byte bits, byte bitsToSet, boolean on) {
		return on ? (byte) (bits | bitsToSet) : clearBits(bits, bitsToSet);
	}

	/**
	 * Turns bit set by bitsToSet on or off
	 * 
	 * @param bits
	 * @param bitsToSet
	 * @param on
	 * @return
	 */
	public static long setBits(long bits, long bitsToSet, boolean on) {
		return on ? bits | bitsToSet : clearBits(bits, bitsToSet);
	}

	/**
	 * Returns the absolute value of the param value. Edge case: abs(MIN_VALUE) returns MAX_VALUE
	 * 
	 * @param value
	 * @return
	 */
	public static long abs(long value) {
		return value == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(value);
	}

	/**
	 * Returns the absolute value of the param value. Edge case: abs(MIN_VALUE) returns MAX_VALUE
	 * 
	 * @param value
	 * @return
	 */
	public static int abs(int value) {
		return value == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(value);
	}
	/**
	 * Returns the absolute value of the param value. Edge case: abs(MIN_VALUE) returns MAX_VALUE
	 * 
	 * @param value
	 * @return
	 */

	public static short abs(short value) {
		return value == Short.MIN_VALUE ? Short.MAX_VALUE : value < 0 ? (short) -value : value;
	}

	/**
	 * Returns the absolute value of the param value. Edge case: abs(MIN_VALUE) returns MAX_VALUE
	 * 
	 * @param value
	 * @return
	 */
	public static byte abs(byte value) {
		return value == Byte.MIN_VALUE ? Byte.MAX_VALUE : value < 0 ? (byte) -value : value;
	}
	/**
	 * Returns the absolute value of the param value. Edge case: abs(MIN_VALUE) returns MAX_VALUE
	 * 
	 * @param value
	 * @return
	 */
	public static float abs(float v) {
		if (Float.isInfinite(v))
			return Float.POSITIVE_INFINITY;
		if (v < 0f)
			return -v;
		return v;
	}
	/**
	 * Returns the absolute value of the param value. Edge case: abs(MIN_VALUE) returns MAX_VALUE
	 * 
	 * @param value
	 * @return
	 */
	public static double abs(double v) {
		if (Double.isInfinite(v))
			return Double.POSITIVE_INFINITY;
		if (v < 0d)
			return -v;
		return v;
	}

	public static double round(double d, int type) {
		if (Double.isNaN(d) || Double.isInfinite(d))
			throw new ArithmeticException("can not round: " + d);
		if (d > MAX_LONG_AS_DOUBLE || d < MIN_LONG_AS_DOUBLE)
			return d;//Number is so big we've already lost precision, it must be rounded already.
		switch (type) {
			case BigDecimal.ROUND_HALF_EVEN:
				return Math.round(d);
			case BigDecimal.ROUND_CEILING:
				return Math.ceil(d);
			case BigDecimal.ROUND_FLOOR:
				return Math.floor(d);
			case BigDecimal.ROUND_UP:
				if (d < 0)
					return -Math.ceil(-d);
				return Math.ceil(d);
			case BigDecimal.ROUND_DOWN:
				if (d < 0)
					return -Math.floor(-d);
				return Math.floor(d);
			default:
				throw new IllegalArgumentException("unknown rounding method: " + type);

		}
	}

	public static long roundBy(long d, int modulus, int type) {
		if (modulus < 1)
			throw new IllegalArgumentException("modulus must be greater than one: " + modulus);
		else if (modulus == 1 || d == 0)
			return d;
		else {
			if (d < 0) {
				switch (type) {
					case BigDecimal.ROUND_HALF_EVEN:
						return ((-d + (modulus / 2)) / modulus) * -modulus;
					case BigDecimal.ROUND_CEILING:
					case BigDecimal.ROUND_DOWN:
						return (d / modulus) * modulus;//DONE
					case BigDecimal.ROUND_FLOOR:
					case BigDecimal.ROUND_UP:
						return ((-d + (modulus - 1)) / modulus) * -modulus;//DONE
					default:
						throw new IllegalArgumentException("unknown rounding method: " + type);
				}
			} else {
				switch (type) {
					case BigDecimal.ROUND_HALF_EVEN:
						return ((d + (modulus / 2)) / modulus) * modulus;
					case BigDecimal.ROUND_CEILING:
					case BigDecimal.ROUND_UP:
						return ((d + (modulus - 1)) / modulus) * modulus;
					case BigDecimal.ROUND_FLOOR:
					case BigDecimal.ROUND_DOWN:
						return (d / modulus) * modulus;
					default:
						throw new IllegalArgumentException("unknown rounding method: " + type);
				}
			}
		}
	}
	public static int roundBy(int d, int modulus, int type) {
		if (modulus < 1)
			throw new IllegalArgumentException("modulus must be greater than one: " + modulus);
		else if (modulus == 1 || d == 0)
			return d;
		else {
			if (d < 0) {
				switch (type) {
					case BigDecimal.ROUND_HALF_EVEN:
						return ((-d + (modulus / 2)) / modulus) * -modulus;
					case BigDecimal.ROUND_CEILING:
					case BigDecimal.ROUND_DOWN:
						return (d / modulus) * modulus;//DONE
					case BigDecimal.ROUND_FLOOR:
					case BigDecimal.ROUND_UP:
						return ((-d + (modulus - 1)) / modulus) * -modulus;//DONE
					default:
						throw new IllegalArgumentException("unknown rounding method: " + type);
				}
			} else {
				switch (type) {
					case BigDecimal.ROUND_HALF_EVEN:
						return ((d + (modulus / 2)) / modulus) * modulus;
					case BigDecimal.ROUND_CEILING:
					case BigDecimal.ROUND_UP:
						return ((d + (modulus - 1)) / modulus) * modulus;
					case BigDecimal.ROUND_FLOOR:
					case BigDecimal.ROUND_DOWN:
						return (d / modulus) * modulus;
					default:
						throw new IllegalArgumentException("unknown rounding method: " + type);
				}
			}
		}
	}
	/* rounds a number taking a round mode and a level of precision
	 * d is the double to round
	 * type is a const defined in MH.java potential modes MH.[ROUND_UP, ROUND_DOWN, ROUND_FLOOR, ROUND_CEILING, ROUND_HALF_EVEN, ROUND_HALF_UP, ROUND_HALF_DOWN
	 */
	public static double round(double d, int type, int precision) {
		if (precision == 0)
			return round(d, type);
		else if (d > MAX_LONG_AS_DOUBLE / 1000 || d < MIN_LONG_AS_DOUBLE / 1000) {
			return new BigDecimal(d).setScale(precision, type).doubleValue();
		} else if (precision < 0)
			return round((d / TO_THE_TENTH[-precision]), type) * TO_THE_TENTH[-precision];
		else
			return round((d * TO_THE_TENTH[precision]), type) / TO_THE_TENTH[precision];
	}

	public static long sum(long[] a) {
		long r = 0;
		int i = a.length;
		for (long n : a)
			r += n;
		return r;
	}

	public static int sum(int[] a) {
		int r = 0;
		for (int n : a)
			r += n;
		return r;
	}

	public static float sum(float[] a) {
		float r = 0;
		int i = a.length;
		for (float n : a)
			r += n;
		return r;
	}

	public static double sum(double[] a) {
		double r = 0;
		for (double n : a)
			r += n;
		return r;
	}
	public static float sumSkipNan(float[] a) {
		float r = 0;
		for (float n : a)
			if (n == n)
				r += n;
		return r;
	}

	public static double sumSkipNan(double[] a) {
		double r = 0;
		for (double n : a)
			if (n == n)
				r += n;
		return r;
	}

	public static short sum(short[] a) {
		short r = 0;
		for (short n : a)
			r += n;
		return r;
	}

	public static byte sum(byte[] a) {
		byte r = 0;
		for (byte n : a)
			r += n;
		return r;
	}

	public static long avg(long i, long j) {
		if (i == 0)
			return j / 2;
		if (i > 0 == j > 0)
			return (i - j) / 2 + j;
		else
			return (i + j) / 2;
	}
	public static double avg(double i, double j) {
		return (i + j) / 2;

	}
	// Uses selection algorithm ~O(n)
	public static Double median(DoubleArrayList numbers) {
		final int size = numbers.size();
		if (size == 0)
			return null;
		if (numbers.isSorted()) {
			if ((size & 1) == 1)
				return numbers.get(size / 2);
			return MH.avg(numbers.get(size / 2), numbers.get(size / 2 - 1));
		}
		if ((size & 1) == 1)
			return select(numbers, 0, size - 1, size / 2);
		return MH.avg(select(numbers, 0, size - 1, size / 2), select(numbers, 0, size - 1, size / 2 - 1));
	}

	// Sorts the array before getting the median ~O(nlog(n))
	public static Double medianUsingSort(DoubleArrayList numbers) {
		final int size = numbers.size();
		if (size == 0)
			return null;
		numbers.sort();
		if ((size & 1) == 1)
			return numbers.get(size / 2);
		return MH.avg(numbers.get(size / 2), numbers.get(size / 2 - 1));
	}

	private static Double select(DoubleArrayList numbers, int left, int right, int k) {
		if (left == right) {
			return numbers.get(left);
		}
		int pivotIndex = (left + right) / 2;
		pivotIndex = partition(numbers, left, right, pivotIndex);

		if (k == pivotIndex)
			return numbers.get(k);
		else if (k < pivotIndex)
			return select(numbers, left, pivotIndex - 1, k);
		else
			return select(numbers, pivotIndex + 1, right, k);
	}

	private static int partition(DoubleArrayList list, int left, int right, int pivotIndex) {
		Double pivotValue = list.get(pivotIndex);

		list.set(pivotIndex, list.get(right));
		list.set(right, pivotValue);

		int storeIndex = left;
		Double storeValue = null;
		for (int i = left; i < right; i++) {
			//			if (nC.compare(list.get(i), pivotValue) == -1) {
			if (list.get(i) < pivotValue) {
				if (storeIndex != i) {
					storeValue = list.get(storeIndex);
					list.set(storeIndex, list.get(i));
					list.set(i, storeValue);
				}
				storeIndex++;
			}
		}
		storeValue = list.get(storeIndex);
		list.set(storeIndex, list.get(right));
		list.set(right, storeValue);
		return storeIndex;
	}

	public static int rand(Random random, int min, int max) {
		return random.nextInt(max - min) + min;
	}
	public static byte[] randomBytes(Random random, int length) {
		byte[] r = new byte[length];
		random.nextBytes(r);
		return r;
	}

	public static double between(double d, double min, double max) {
		if (min < max)
			return d < min ? min : (d < max ? d : max);
		else
			return d < max ? max : (d < min ? d : min);
	}
	public static long between(long d, long min, long max) {
		if (min < max)
			return d < min ? min : (d < max ? d : max);
		else
			return d < max ? max : (d < min ? d : min);
	}
	public static int between(int d, int min, int max) {
		if (min < max)
			return d < min ? min : (d < max ? d : max);
		else
			return d < max ? max : (d < min ? d : min);
	}
	public static int diff(int l, int r) {
		return l > r ? l - r : r - l;
	}

	public static int diff(char l, char r) {
		return l > r ? l - r : r - l;
	}
	public static float diff(float l, float r) {
		return l > r ? l - r : r - l;
	}
	public static double diff(double l, double r) {
		return l > r ? l - r : r - l;
	}
	public static long diff(long l, long r) {
		return l > r ? l - r : r - l;
	}
	public static int diff(short l, short r) {
		return l > r ? l - r : r - l;
	}
	public static int diff(byte l, byte r) {
		return l > r ? l - r : r - l;
	}

	public static boolean isNumber(Number n) {
		return (n instanceof Double || n instanceof Float) ? isNumber(n.doubleValue()) : n != null;
	}
	public static boolean isntNumber(Number n) {
		return (n instanceof Double || n instanceof Float) ? isntNumber(n.doubleValue()) : n == null;
	}
	public static boolean isntNumber(Double n) {
		return n == null || isntNumber(n.doubleValue());
	}
	public static boolean isNumber(Double n) {
		return n != null && isNumber(n.doubleValue());
	}
	public static boolean isntNumber(double n) {
		return Double.isInfinite(n) || Double.isNaN(n);
	}
	public static boolean isNumber(double n) {
		return !isntNumber(n);
	}
	public static double noNan(double n, double insteadOfNan) {
		return isntNumber(n) ? insteadOfNan : n;
	}

	public static double sq(double d) {
		return d * d;
	}
	public static int sq(int d) {
		return d * d;
	}

	public static long minl(long... values) {
		final int len = values.length;
		if (len == 0)
			throw new IllegalArgumentException("min undefined for empty array");
		long r = values[0], t;
		for (int i = 1; i < len; i++)
			if (r > (t = values[i]))
				r = t;
		return r;
	}
	public static long maxl(long... values) {
		final int len = values.length;
		if (len == 0)
			throw new IllegalArgumentException("max undefined for empty array");
		long r = values[0], t;
		for (int i = 1; i < len; i++)
			if (r < (t = values[i]))
				r = t;
		return r;
	}
	public static double mind(double... values) {
		final int len = values.length;
		if (len == 0)
			throw new IllegalArgumentException("min undefined for empty array");
		double r = values[0], t;
		for (int i = 1; i < len; i++)
			if (r > (t = values[i]))
				r = t;
		return r;
	}
	public static double maxd(double... values) {
		final int len = values.length;
		if (len == 0)
			throw new IllegalArgumentException("max undefined for empty array");
		double r = values[0], t;
		for (int i = 1; i < len; i++)
			if (r < (t = values[i]))
				r = t;
		return r;
	}
	public static int mini(int... values) {
		final int len = values.length;
		if (len == 0)
			throw new IllegalArgumentException("min undefined for empty array");
		int r = values[0], t;
		for (int i = 1; i < len; i++)
			if (r > (t = values[i]))
				r = t;
		return r;
	}
	public static int maxi(int... values) {
		final int len = values.length;
		if (len == 0)
			throw new IllegalArgumentException("max undefined for empty array");
		int r = values[0], t;
		for (int i = 1; i < len; i++)
			if (r < (t = values[i]))
				r = t;
		return r;
	}
	public static short maxs(short... values) {
		final int len = values.length;
		if (len == 0)
			throw new IllegalArgumentException("max undefined for empty array");
		short r = values[0], t;
		for (int i = 1; i < len; i++)
			if (r < (t = values[i]))
				r = t;
		return r;
	}
	public static float minf(float... values) {
		final int len = values.length;
		if (len == 0)
			throw new IllegalArgumentException("min undefined for empty array");
		float r = values[0], t;
		for (int i = 1; i < len; i++)
			if (r > (t = values[i]))
				r = t;
		return r;
	}
	public static float maxf(float... values) {
		final int len = values.length;
		if (len == 0)
			throw new IllegalArgumentException("max undefined for empty array");
		float r = values[0], t;
		for (int i = 1; i < len; i++)
			if (r < (t = values[i]))
				r = t;
		return r;
	}
	public static BitSet toBitSet(long value) {
		BitSet bits = new BitSet();
		int index = 0;
		while (value != 0L) {
			if (value % 2L != 0)
				bits.set(index);
			++index;
			value = value >>> 1;
		}
		return bits;
	}

	public static long toLong(BitSet bits) {
		long value = 0L;
		for (int i = 0; i < bits.length(); ++i)
			value += bits.get(i) ? (1L << i) : 0L;
		return value;
	}

	public static final byte LEFT = 1, RIGHT = 2, BOTH = 4;

	public static long commBits(long left, long right, byte setMask) {
		switch (setMask) {
			case 0:
				return 0L;
			case LEFT:
				return left & ~right;
			case RIGHT:
				return right & ~left;
			case LEFT | RIGHT:
				return (left | right) ^ (left & right);
			case BOTH:
				return left & right;
			case BOTH | LEFT:
				return left;
			case BOTH | RIGHT:
				return right;
			case BOTH | LEFT | RIGHT:
				return left | right;
			default:
				throw new IllegalArgumentException("invalid mask: " + setMask);
		}
	}

	public static long commBits(long left, long right, boolean includeLeft, boolean includeRight, boolean includeBoth) {
		return commBits(left, right, (byte) ((includeLeft ? LEFT : 0) | (includeRight ? RIGHT : 0) | (includeBoth ? BOTH : 0)));
	}

	public static int getDigitsCount(long i, int base) {
		if (base < 2)
			throw new IllegalArgumentException("invalid base: " + base);
		int r = 1;
		if (i < 0) {
			r++;
			i = abs(i);
		}
		while ((i /= base) > 0)
			r++;
		return r;
	}

	public static int clip(int val, int min, int max) {
		OH.assertLe(min, max);
		return val < min ? min : (val > max ? max : val);
	}
	public static long clip(long val, long min, long max) {
		OH.assertLe(min, max);
		return val < min ? min : (val > max ? max : val);
	}
	public static double clip(double val, double min, double max) {
		OH.assertLe(min, max);
		return val < min ? min : (val > max ? max : val);
	}
	public static float clip(float val, float min, float max) {
		OH.assertLe(min, max);
		return val < min ? min : (val > max ? max : val);
	}
	public static byte clip(byte val, byte min, byte max) {
		OH.assertLe(min, max);
		return val < min ? min : (val > max ? max : val);
	}
	public static char clip(char val, char min, char max) {
		OH.assertLe(min, max);
		return val < min ? min : (val > max ? max : val);
	}
	public static short clip(short val, short min, short max) {
		OH.assertLe(min, max);
		return val < min ? min : (val > max ? max : val);
	}
	public static <T> Comparable<T> clip(Comparable<T> val, Comparable<T> min, Comparable<T> max) {
		OH.assertLe(min, max);
		if (OH.compare(min, val) >= 0)
			return min;
		if (OH.compare(max, val) <= 0)
			return max;
		else
			return val;
	}
	public static long toUnsignedInt(int i) {
		return i & 0xffffffffL;
	}
	public static int fromUnsignedInt(long i) {
		OH.assertBetween(i, 0, MAX_UNSIGNED_INT);
		return (int) i;
	}
	public static byte fromUnsignedByte(int i) {
		OH.assertBetween(i, 0, MAX_UNSIGNED_BYTE);
		return (byte) i;
	}
	public static int toUnsignedInt(byte b) {
		return b & 0xff;
	}

	final public static byte MAX_VALUE1 = Byte.MAX_VALUE;
	final public static byte MIN_VALUE1 = Byte.MIN_VALUE;
	final public static short MAX_VALUE2 = Short.MAX_VALUE;
	final public static short MIN_VALUE2 = Short.MIN_VALUE;
	final public static int MAX_VALUE3 = Integer.MAX_VALUE >> 8;
	final public static int MIN_VALUE3 = Integer.MIN_VALUE >> 8;
	final public static int MAX_VALUE4 = Integer.MAX_VALUE;
	final public static int MIN_VALUE4 = Integer.MIN_VALUE;
	final public static long MAX_VALUE5 = Long.MAX_VALUE >> 24;
	final public static long MIN_VALUE5 = Long.MIN_VALUE >> 24;
	final public static long MAX_VALUE6 = Long.MAX_VALUE >> 16;
	final public static long MIN_VALUE6 = Long.MIN_VALUE >> 16;
	final public static long MAX_VALUE7 = Long.MAX_VALUE >> 8;
	final public static long MIN_VALUE7 = Long.MIN_VALUE >> 8;
	final public static long MAX_VALUE8 = Long.MAX_VALUE;
	final public static long MIN_VALUE8 = Long.MIN_VALUE;
	public static final Random RANDOM = new Random();
	public static final SecureRandom RANDOM_SECURE = new SecureRandom();

	static public int getByteDepth(long i) {
		if (i > MAX_VALUE4) {
			if (i > MAX_VALUE6)
				return i > MAX_VALUE7 ? 8 : 7;
			else
				return i > MAX_VALUE5 ? 6 : 5;
		} else if (i < MIN_VALUE4) {
			if (i < MIN_VALUE6)
				return i < MIN_VALUE7 ? 8 : 7;
			else
				return i < MIN_VALUE5 ? 6 : 5;
		} else
			return getByteDepth((int) i);
	}
	static public int getByteDepth(int i) {
		if (i > 0) {
			if (i > MAX_VALUE2)
				return i > MAX_VALUE3 ? 4 : 3;
			else
				return i > MAX_VALUE1 ? 2 : 1;
		} else {
			if (i < MIN_VALUE2)
				return i < MIN_VALUE3 ? 4 : 3;
			else
				return i < MIN_VALUE1 ? 2 : 1;
		}
	}
	public static long addNoOverflow(long a, long b) {
		if (a == 0)
			return b;
		else if (b == 0)
			return a;
		else if (a < 0 != b < 0)//pos & neg... cant overflow
			return a + b;
		long r = a + b;
		if (a < 0) {
			if (r >= 0)
				return Long.MIN_VALUE;
		} else {
			if (r <= 0)
				return Long.MAX_VALUE;
		}
		return r;

	}

	static public long getPowerOfTwoUpper(long x) {
		if (x < 0)
			throw new IllegalArgumentException("must not be negative: " + x);
		else if (isPowerOfTwo(x))
			return x;
		x--;
		x |= x >> 1;
		x |= x >> 2;
		x |= x >> 4;
		x |= x >> 8;
		x |= x >> 16;
		x |= x >> 32;
		return x + 1;

	}
	public static boolean isPowerOfTwo(long x) {
		return x > 0 && (x & (x - 1)) == 0;
	}

	public static double pctDiff(double a, double b) {
		if (a == b)
			return a == 0 ? Double.NaN : 0;
		else if (a < 0 || b < 0)
			return Double.NaN;
		else
			return diff(a, b) / avg(a, b);
	}
	public static double pctChange(double a, double b) {
		if (a == b)
			return 0;
		else
			return (a - b) / b;
	}
	public static boolean isBitSetAt(long bits, int position) {
		return (bits & (1L << position)) != 0;
	}
	public static long setBitAt(long bits, int position, boolean on) {
		if (on)
			return bits | (1L << position);
		else
			return bits & ~(1L << position);
	}
	static public double minAvoidNan(double a, double b) {
		return a < b || MH.isntNumber(b) ? a : b;
	}

	static public double maxAvoidNan(double a, double b) {
		return a > b || MH.isntNumber(b) ? a : b;
	}

	// 0, nan, inf  => 0
	// .01 to <.1   =>-3 
	// .01 to <.1   =>-2 
	// .1  to <1    =>-1 
	// 1   to <10   => 1
	// 10  to <100  => 2
	// 100 to <1000 => 3
	public static int getMagnitude(double i) {
		if (isntNumber(i) || i == 0)
			return 0;
		if (i < 0)
			i = -i;
		int r = 0;
		if (i < 1)
			do {
				r--;
				i *= 10;
			} while (i < 1);
		else
			do {
				i /= 10;
				r++;
			} while (i >= 1);
		return r;
	}
	public static double remainder(double a, double b) {
		if (isntNumber(a) || isntNumber(b))
			return Double.NaN;
		if (b == 0)
			return a >= 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
		return a - b * (long) (a / b);
	}

	public static long hash(long h) {
		h ^= (h >>> 43) ^ (h >>> 25);
		h ^= (h >>> 20) ^ (h >>> 12);
		h ^= (h >>> 7) ^ (h >>> 4);
		return h;
	}
	public static int hash(int h) {
		h ^= (h >>> 20) ^ (h >>> 12);
		h ^= (h >>> 7) ^ (h >>> 4);
		return h;
	}

	//consideres Nan=Nan as true
	public static boolean eq(double l, double r) {
		return l == r || (l != l && r != r);
	}

	//consideres Nan=Nan as true
	public static boolean eq(float l, float r) {
		return l == r || (l != l && r != r);
	}

	//consideres Nan=Nan as true
	public static boolean eq(double l, double r, double delta) {
		if (l == r || (l != l && r != r))
			return true;
		return (r - delta < l && r + delta > l);
	}
	public static boolean eq(float l, float r, float delta) {
		if (l == r || (l != l && r != r))
			return true;
		return (r - delta < l && r + delta > l);
	}
	public static boolean le(double l, double r, double delta) {
		return (l <= r + delta);
	}
	public static boolean ge(double l, double r, double delta) {
		return (l >= r - delta);
	}
	public static boolean between(double l, double min, double max, double delta) {
		return ge(l, min, delta) && le(l, max, delta);
	}
	public static boolean le(float l, float r, float delta) {
		return (l <= r + delta);
	}
	public static boolean ge(float l, float r, float delta) {
		return (l >= r - delta);
	}

	public static double stdev(double[] values) {
		double sumSqr = 0;
		double sum = 0;
		for (double v : values) {
			sumSqr += v * v;
			sum += v;
		}
		double avg = sum / values.length;
		return Math.sqrt((sumSqr / values.length) - avg * avg);
	}
	public static double stdev(int[] values) {
		double sumSqr = 0;
		double sum = 0;
		for (int v : values) {
			sumSqr += (double) v * v;
			sum += v;
		}
		double avg = sum / values.length;
		return Math.sqrt((sumSqr / values.length) - avg * avg);
	}

	public static double scale(double val, double inMin, double inMax, double outMin, double outMax) {
		val -= inMin;
		val *= (outMax - outMin) / (inMax - inMin);
		val += outMin;
		return val;
	}
	public static long scale(long val, long inMin, long inMax, long outMin, long outMax) {
		val -= inMin;
		val *= (outMax - outMin) / (inMax - inMin);
		val += outMin;
		return val;
	}

	public static StringBuilder nextGuid(Random rand, StringBuilder sb) {
		//xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
		int i = rand.nextInt();
		writeGuidPart(i & 0x0000ffffL, sb);
		writeGuidPart((i & 0xffff0000L) >> 16, sb);
		sb.append('-');

		i = rand.nextInt();
		writeGuidPart(i & 0x0000ffffL, sb);
		sb.append('-');
		writeGuidPart((i & 0xffff0000L) >> 16, sb);

		i = rand.nextInt();
		sb.append('-');
		writeGuidPart(i & 0x0000ffffL, sb);
		sb.append('-');
		writeGuidPart((i & 0xffff0000L) >> 16, sb);

		i = rand.nextInt();
		writeGuidPart(i & 0x0000ffffL, sb);
		writeGuidPart((i & 0xffff0000L) >> 16, sb);
		return sb;
	}

	private static void writeGuidPart(long n, StringBuilder sb) {
		if (n <= 0x0fff)
			sb.append(n <= 0x00ff ? (n <= 0x000f ? "000" : "00") : "0");
		SH.toString(n & 0xffff, 16, sb);
	}

	public static double digamma(double x) {
		// Implementation of Algorithm AS 103 by Jose Bernardo
		if (x <= 0) {
			return Double.NaN;
		}
		// Use approximation for small argument
		if (x <= 1e-6) {
			return EULER_MASCHERONI_CONSTANT - 1.0 / x + 1.6449340668482264365 * x;
		}
		// Reduce to digamma(x + n)
		double value = 0;
		while (x < 8.5) {
			value = value - 1.0 / x;
			x = x + 1.0;
		}
		// Use Stirling's expansion
		double r = 1.0 / x;
		value = value + Math.log(x) - 0.5 * r;
		r = r * r;
		return value - r * (1.0 / 12.0 - r * (1.0 / 120.0 - r * (1.0 / 252.0 - r * (1.0 / 240.0 - r * (1.0 / 132.0)))));
	}
	public static double lnGamma(double x) {
		// Implementation of Algorithm TOMS291: Logarithm of Gamma Function by Malcolm Pike, David Hill
		if (x <= 0) {
			return Double.NaN;
		}
		double y = x;
		double f, z;
		if (x < 7.0) {
			f = 1.0;
			z = y;

			while (z < 7.0) {
				f *= z;
				z += 1.0;
			}
			y = z;
			f = -Math.log(f);
		} else {
			f = 0.0;
		}

		z = 1.0 / (y * y);
		return f + (y - 0.5) * Math.log(y) - y + 0.918938533204673 + (((-0.000595238095238 * z + 0.000793650793651) * z - 0.002777777777778) * z + 0.083333333333333) / y;
	}
	public static double gaussianPdf(double[] x, double[] mean, double[][] covariance) {
		int D = x.length;
		if (D != mean.length) {
			throw new IllegalArgumentException("Input and mean vectors must be the same length.");
		}
		if (D != covariance.length || D != covariance[0].length) {
			throw new IllegalArgumentException("Covariance matrix must be an (D x D) matrix where D is the length of the input vector.");
		}
		double[] diff = LAH.subtract(x, mean);
		return 1.0 / Math.pow(Math.pow(2 * Math.PI, D) * LAH.determinant(covariance), 0.5) * Math.exp(-0.5 * LAH.inner(LAH.multiply(diff, LAH.inverse(covariance)), diff));
	}
	public static double wishartLnBFunction(double[][] W, double nu) {
		int D = W.length;
		if (D != W[0].length) {
			throw new IllegalArgumentException("W must be a square matrix.");
		}
		double sum = 0;
		for (int i = 1; i <= D; i++) {
			sum += lnGamma((nu + 1 - i) / 2.0);
		}
		return -(nu / 2.0 * Math.log(LAH.determinant(W)) + nu * D / 2.0 * Math.log(2) + D * (D - 1) / 4.0 * Math.log(Math.PI) + sum);
	}
	public static boolean isClose(double x, double y, double tol) {
		return Math.abs(x - y) < tol;
	}
	public static boolean isClose(double x, double y) {
		return isClose(x, y, 10 * eps0);
	}
	public static double[] complexAdd(double[] x, double[] y) {
		assertLengthTwo(x);
		assertLengthTwo(y);
		return new double[] { x[0] + y[0], x[1] + y[1] };
	}
	public static double[] complexMultiply(double[] x, double[] y) {
		assertLengthTwo(x);
		assertLengthTwo(y);
		return new double[] { x[0] * y[0] - x[1] * y[1], x[0] * y[1] + x[1] * y[0] };
	}
	private static void assertLengthTwo(double[] x) {
		if (x.length != 2) {
			throw new IllegalArgumentException("Argument must have length two.");
		}
	}
	public static double[] polar2Cartesian(double r, double theta) {
		return new double[] { r * Math.cos(theta), r * Math.sin(theta) };
	}
	public static int max(int... values) {
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < values.length; i++) {
			if (values[i] > max) {
				max = values[i];
			}
		}
		return max;
	}
	public static int min(int... values) {
		int max = Integer.MAX_VALUE;
		for (int i = 0; i < values.length; i++) {
			if (values[i] < max) {
				max = values[i];
			}
		}
		return max;
	}
	public static byte max(byte a, byte b) {
		return a > b ? a : b;
	}
	public static byte min(byte a, byte b) {
		return a < b ? a : b;
	}
	public static int nextPowerOfTwo(int x) {
		int highestOneBit = Integer.highestOneBit(x);
		if (x == highestOneBit) {
			return x;
		}
		return highestOneBit << 1;
	}

	private static final int ARRAY_SIZE_MIN = 10;
	private static final int ARRAY_SIZE_MAX = Integer.MAX_VALUE - 1000;
	private static final int ARRAY_PADDING_MAX = 1000 * 1000 * 100;
	private static final Logger log = LH.get();

	public static int getArrayGrowth(int currentArraySize, int requiredSize) {
		OH.assertLt(currentArraySize, requiredSize);
		if (currentArraySize >= ARRAY_SIZE_MAX || requiredSize > ARRAY_SIZE_MAX)
			throw new RuntimeException("SIZE OVERFLOW: can't grow array from " + currentArraySize + " to " + requiredSize);
		long r = (currentArraySize * 3L) / 2L + 1;
		r = clip(r, requiredSize, requiredSize + ARRAY_PADDING_MAX);
		r = clip(r, ARRAY_SIZE_MIN, ARRAY_SIZE_MAX);
		if (r >= ARRAY_PADDING_MAX)
			LH.info(log, "Request to grow large array: " + currentArraySize + " to " + r + " (requested=" + requiredSize + ")");
		return (int) r;
	}
	public static long nextLongSigned(Random r, long n) {
		return n < 0 ? -nextLong(r, abs(n)) : nextLong(r, n);
	}
	public static double nextDoubleSigned(Random r, double n) {
		return r.nextDouble() * n;
	}
	public static long nextLong(Random r, long n) {
		if (n <= 0)
			throw new IllegalArgumentException("n must be positive");
		if (n <= Integer.MAX_VALUE)
			return r.nextInt((int) n);
		long bits, val;
		int cnt = 0;
		do {
			bits = r.nextLong();
			while (bits == Long.MIN_VALUE) {
				bits = r.nextLong();
			}
			bits = Math.abs(bits);
			val = bits % n;
		} while (bits - val + (n - 1) < 0);
		if (cnt > 1)
			System.out.println(cnt);
		return val;
	}

	public static void main(String a[]) {
		System.out.println(Long.MAX_VALUE);
		System.out.println((double) Long.MAX_VALUE);
		double n2 = (double) Long.MAX_VALUE;
		n2 = n2 - 1000;
		System.out.println(n2);
		System.out.println((long) n2);
		for (int n = 0; n < 5; n++) {
			Random r = new Random();
			long parts[] = new long[64];
			long limit = Integer.MAX_VALUE;
			limit += 1233923917233123L;
			limit += 1233923917233123L;
			limit = -limit;
			limit = 1;
			BigDecimal bi = BigDecimal.ZERO;
			int k = 10000000;
			long min = 0, max = 0;
			for (int i = 0; i < k; i++) {
				long t = nextLongSigned(r, limit);
				if (i == 0) {
					min = max = t;
				} else {
					min = Math.min(t, min);
					max = Math.max(t, max);
				}
				bi = bi.add(BigDecimal.valueOf(t));
				OH.assertLt(t, limit);
				OH.assertLe(t, 0);
				for (int j = 0; j < 64; j++) {
					if (((t >> j) & 1) == 1)
						parts[j]++;
				}
			}
			System.out.println("");
			System.out.println(bi.divide(BigDecimal.valueOf(k)));
			System.out.println(BigDecimal.valueOf(limit).divide(BigDecimal.valueOf(2d)));
			System.out.println("Min: " + min);
			System.out.println("Max: " + max);
			System.out.println("Exp: " + limit);
		}
	}

	public static Number minAvoidNull(Number a, Number b) {
		if (a == null)
			return b;
		else if (b == null)
			return a;
		PrimitiveMath pm = PrimitiveMathManager.INSTANCE.getNoThrow(a, b);
		if (pm == null)
			return a.doubleValue() < b.doubleValue() ? a : b;
		return pm.cast(pm.compare(a, b) < 0 ? a : b);
	}
	public static Number maxAvoidNull(Number a, Number b) {
		if (a == null)
			return b;
		else if (b == null)
			return a;
		PrimitiveMath pm = PrimitiveMathManager.INSTANCE.getNoThrow(a, b);
		if (pm == null)
			return a.doubleValue() > b.doubleValue() ? a : b;
		return pm.cast(pm.compare(a, b) > 0 ? a : b);
	}
	/**
	 * Returns {@code true} if the argument is a finite floating-point value; returns {@code false} otherwise (for NaN and infinity arguments).
	 *
	 * @param f
	 *            the {@code float} value to be tested
	 * @return {@code true} if the argument is a finite floating-point value, {@code false} otherwise.
	 */
	public static boolean isFinite(float f) {
		return Math.abs(f) <= FloatConsts.MAX_VALUE;
	}

	/**
	 * Returns {@code true} if the argument is a finite floating-point value; returns {@code false} otherwise (for NaN and infinity arguments).
	 *
	 * @param d
	 *            the {@code double} value to be tested
	 * @return {@code true} if the argument is a finite floating-point value, {@code false} otherwise.
	 */
	public static boolean isFinite(double d) {
		return Math.abs(d) <= DoubleConsts.MAX_VALUE;
	}

}
