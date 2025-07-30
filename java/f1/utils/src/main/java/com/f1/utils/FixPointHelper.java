/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

public class FixPointHelper {
	public static final int BITS_FOR_PRECISION = 4;
	public static final int TOTAL_BITS = 64;
	private static final int BITS_FOR_SIGN = 1;
	private static final int MAX_PRECISION = (1 << BITS_FOR_PRECISION) - 1;
	private static final int BITS_FOR_VALUE = TOTAL_BITS - BITS_FOR_PRECISION - BITS_FOR_SIGN;
	static final long TENS[];
	private static final long[] MIN_VALUE;
	private static final long[] MAX_VALUE;

	static {
		TENS = new long[MAX_PRECISION + 1];
		MIN_VALUE = new long[MAX_PRECISION + 1];
		MAX_VALUE = new long[MAX_PRECISION + 1];
		long j = 1;
		for (int i = 0; i < TENS.length; i++, j *= 10) {
			TENS[i] = j;
			MAX_VALUE[i] = ((1L << BITS_FOR_VALUE) - 1) / TENS[i];
			MIN_VALUE[i] = -MAX_VALUE[i];
		}
		j = 1;
	}

	static public int getPrecision(long bytes) {
		return (int) (Math.abs(bytes) >> (BITS_FOR_VALUE));
	}

	static public long getRawValue(long bytes) {
		return (Math.abs(bytes) << BITS_FOR_PRECISION + BITS_FOR_SIGN) >> (BITS_FOR_PRECISION + BITS_FOR_SIGN);
	}

	static public long getWholeValue(int precision, long rawValue) {
		return rawValue / TENS[precision];
	}

	static public long getDecimalValue(int precision, long rawValue) {
		return rawValue % TENS[precision];
	}

	static public long toBytes(int precision, double value) {
		long ten = TENS[precision];
		OH.assertBetween(precision, 0, MAX_PRECISION);
		OH.assertBetween((long) value, MIN_VALUE[precision], MAX_VALUE[precision]);
		if (value < 0d)
			return -(((long) precision << BITS_FOR_VALUE) + (long) (.5 - (value * ten)));
		else
			return ((long) precision << BITS_FOR_VALUE) + (long) (.5 + (value * ten));
	}

	static public long toBytes(int precision, long whole, long decimal) {
		long ten = TENS[precision];
		OH.assertBetween(precision, 0, MAX_PRECISION);
		OH.assertBetween(decimal, 0, ten);
		OH.assertBetween(whole, MIN_VALUE[precision], MAX_VALUE[precision]);
		if (whole < 0)
			return -(((long) precision << BITS_FOR_VALUE) - whole * ten + decimal);
		else
			return ((long) precision << BITS_FOR_VALUE) + whole * ten + decimal;
	}

	static public long toBytes(int precision, long rawValue) {
		OH.assertBetween(precision, 0, MAX_PRECISION);
		OH.assertBetween(rawValue, MIN_VALUE[precision], MAX_VALUE[precision]);
		if (rawValue < 0)
			return -(((long) precision << BITS_FOR_VALUE) - rawValue);
		else
			return ((long) precision << BITS_FOR_VALUE) + rawValue;
	}

	public static long toBytes(String text) {
		try {
			int i = text.indexOf('.');
			if (i == -1)
				return toBytes(0, Long.parseLong(text), 0);
			return toBytes(text.length() - i - 1, Long.parseLong(text.substring(0, i)), Long.parseLong(text.substring(i + 1)));
		} catch (Exception e) {
			NumberFormatException r = new NumberFormatException(text);
			r.initCause(e);
			throw r;
		}
	}

	public static long toBytes(int precision, String text) {
		try {
			int i = text.indexOf('.');
			if (i == -1)
				return toBytes(precision, Long.parseLong(text), 0);
			long decimal = Long.parseLong(text.substring(i + 1));
			int diff = text.length() - i - 1 - precision;
			if (diff > 0)
				decimal /= TENS[diff];
			else if (diff < 0)
				decimal *= TENS[-diff];
			return toBytes(precision, Long.parseLong(text.substring(0, i)), decimal);
		} catch (Exception e) {
			NumberFormatException r = new NumberFormatException(text);
			r.initCause(e);
			throw r;
		}
	}

	static public String toString(long bytes) {
		StringBuilder sb = new StringBuilder();
		toString(bytes, sb);
		return sb.toString();

	}

	static public double bytesToDouble(long bytes) {
		int p = getPrecision(bytes);
		long rawValue = getRawValue(bytes);
		return (double) (bytes < 0 ? -rawValue : rawValue) / TENS[p];
	}

	static public void toString(long bytes, StringBuilder sb) {
		int p = getPrecision(bytes);
		long rawValue = getRawValue(bytes);
		long w = getWholeValue(p, rawValue);
		long d = getDecimalValue(p, rawValue);
		if (bytes < 0)
			sb.append('-');
		sb.append(w);
		if (p > 0) {
			sb.append(".");
			String ds = SH.toString(d);
			SH.repeat('0', p - ds.length(), sb);
			sb.append(ds);
		}
	}

	public static long minValue(int precision) {
		return MIN_VALUE[precision];
	}

	public static long maxValue(int precision) {
		return MAX_VALUE[precision];
	}

	static public int getMaxPrecision(FixPoint left, FixPoint right) {
		return Math.max(left.getPrecision(), right.getPrecision());
	}

	public static FixPoint add(FixPoint left, FixPoint right) {
		int diff = left.getPrecision() - right.getPrecision();
		if (diff == 0)
			return BasicFixPoint.nuw(toBytes(left.getPrecision(), getRawValue(left.getBytes()) + getRawValue(right.getBytes())));
		else if (diff > 0) {
			return BasicFixPoint.nuw(toBytes(left.getPrecision(), getRawValue(left.getBytes()) + TENS[diff] * getRawValue(right.getBytes())));
		} else {
			return BasicFixPoint.nuw(toBytes(right.getPrecision(), TENS[-diff] * getRawValue(left.getBytes()) + getRawValue(right.getBytes())));
		}
	}

	public static FixPoint subtract(FixPoint left, FixPoint right) {
		int diff = left.getPrecision() - right.getPrecision();
		if (diff == 0)
			return BasicFixPoint.nuw(toBytes(left.getPrecision(), getRawValue(left.getBytes()) - getRawValue(right.getBytes())));
		else if (diff > 0) {
			return BasicFixPoint.nuw(toBytes(left.getPrecision(), getRawValue(left.getBytes()) - TENS[diff] * getRawValue(right.getBytes())));
		} else {
			return BasicFixPoint.nuw(toBytes(right.getPrecision(), TENS[-diff] * getRawValue(left.getBytes()) - getRawValue(right.getBytes())));
		}
	}

	public static FixPoint multiply(FixPoint left, FixPoint right) {
		int diff = left.getPrecision() - right.getPrecision();
		int precision = getMaxPrecision(left, right);
		return BasicFixPoint.nuw(toBytes(precision, getRawValue(left.getBytes()) * TENS[Math.abs(diff)] * getRawValue(right.getBytes()) / TENS[precision]));
	}

	public static FixPoint divide(FixPoint left, FixPoint right) {
		int diff = left.getPrecision() - right.getPrecision();
		if (diff == 0)
			return BasicFixPoint.nuw(toBytes(left.getPrecision(), TENS[left.getPrecision()] * getRawValue(left.getBytes()) / getRawValue(right.getBytes())));
		else if (diff > 0) {
			return BasicFixPoint.nuw(toBytes(left.getPrecision(), TENS[right.getPrecision()] * getRawValue(left.getBytes()) / getRawValue(right.getBytes())));
		} else {
			return BasicFixPoint.nuw(toBytes(right.getPrecision(), TENS[right.getPrecision() - diff] * getRawValue(left.getBytes()) / getRawValue(right.getBytes())));
		}
	}
}
