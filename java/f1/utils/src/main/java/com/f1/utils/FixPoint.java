/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

public abstract class FixPoint extends Number implements Comparable<FixPoint> {

	public abstract int getPrecision();

	public abstract long getMinValue();

	public abstract long getMaxValue();

	public abstract long getBytes();

	public abstract long getDecimal();

	public abstract FixPoint abs();
}
