/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

final public class BasicFixPoint extends FixPoint {

	public final static BasicFixPoint ZERO = new BasicFixPoint("0");

	private long bytes;

	public BasicFixPoint(long bytes) {
		this.bytes = bytes;
	}

	public BasicFixPoint(int precision, long whole, long decimal) {
		this.bytes = FixPointHelper.toBytes(precision, whole, decimal);
	}

	public BasicFixPoint(int precision, double value) {
		this.bytes = FixPointHelper.toBytes(precision, value);
	}

	public BasicFixPoint(String text) {
		this.bytes = FixPointHelper.toBytes(text);
	}

	public BasicFixPoint(int precision, String text) {
		this.bytes = FixPointHelper.toBytes(precision, text);
	}

	@Override
	public int getPrecision() {
		return FixPointHelper.getPrecision(bytes);
	}

	@Override
	public long getMinValue() {
		return FixPointHelper.minValue(getPrecision());
	}

	@Override
	public long getMaxValue() {
		return FixPointHelper.maxValue(getPrecision());
	}

	@Override
	public long getBytes() {
		return bytes;
	}

	@Override
	public int intValue() {
		return (int) FixPointHelper.getWholeValue(getPrecision(), FixPointHelper.getRawValue(bytes));
	}

	@Override
	public long longValue() {
		return FixPointHelper.getWholeValue(getPrecision(), FixPointHelper.getRawValue(bytes));
	}

	@Override
	public float floatValue() {
		return (float) FixPointHelper.bytesToDouble(bytes);
	}

	@Override
	public double doubleValue() {
		return FixPointHelper.bytesToDouble(bytes);
	}

	@Override
	public String toString() {
		return FixPointHelper.toString(bytes);
	}

	public static FixPoint nuw(long bytes) {
		return new BasicFixPoint(bytes);
	}

	public static FixPoint nuw(int precision, long whole, long decimal) {
		return new BasicFixPoint(precision, whole, decimal);
	}

	public static FixPoint nuw(int precision, double value) {
		return new BasicFixPoint(precision, value);
	}

	public static FixPoint nuw(String text) {
		return new BasicFixPoint(text);
	}

	public static FixPoint nuw(int precision, String text) {
		return new BasicFixPoint(precision, text);
	}

	@Override
	public long getDecimal() {
		return FixPointHelper.getDecimalValue(getPrecision(), FixPointHelper.getRawValue(bytes));
	}

	@Override
	public int compareTo(FixPoint o) {
		long l = longValue(), r = o.longValue();
		if (l < r)
			return -1;
		if (l > r)
			return 1;
		l = getDecimal();
		r = o.getDecimal();
		int diff = getPrecision() - o.getPrecision();
		if (diff > 0)
			r *= FixPointHelper.TENS[diff];
		if (diff < 0)
			l *= FixPointHelper.TENS[-diff];
		return l < r ? -1 : l > r ? 1 : 0;
	}

	@Override
	public boolean equals(Object o) {
		return o != null && o.getClass() == BasicFixPoint.class && compareTo((FixPoint) o) == 0;
	}

	@Override
	public int hashCode() {
		return (int) this.bytes;
	}

	@Override
	public FixPoint abs() {
		throw new ToDoException();
	}

}
