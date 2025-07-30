package com.f1.ami.web.charts;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.f1.utils.MH;
import com.f1.utils.math.BigDecimalMath;

public class AmiWebChartUtils {

	public static final double PI2 = Math.PI * 2;
	public static final double PI_180 = Math.PI / 180;
	private static final int[] HUMAN_FACTORS = new int[] { 10, 20, 25, 50, 100 };
	public static double calcUnitSize(final double bucketsCount, final double delta) {
		if (bucketsCount <= 0)
			return 1;//TODO: is this right?
		double t = delta / bucketsCount;
		if (MH.isntNumber(t) || t == 0)
			return 1;
		double r = unitSize(t);
		while (r * bucketsCount < delta) {
			r = unitSize(r * 1.1);
		}
		return r;
	}
	private static double unitSize(final double delta) {
		double digits = 1;
		double t = Math.abs(delta);
		while (t > 100) {
			digits *= 10;
			t /= 10;
		}
		while (t < 10) {
			digits /= 10;
			t *= 10;
		}
		//return digits;
		for (int i : HUMAN_FACTORS)
			if (i >= t) {
				digits *= i;
				break;
			}
		return digits;
	}
	public static Number roundDown(Number _n, Number _unit) {
		BigDecimal n = BigDecimalMath.INSTANCE.cast(_n);
		BigDecimal unit = BigDecimalMath.INSTANCE.cast(_unit);
		BigDecimal r = n.divide(unit, 0, n.compareTo(BigDecimal.ZERO) < 0 ? RoundingMode.UP : RoundingMode.DOWN).multiply(unit);
		return r;
	}
	public static Number roundUp(Number _n, Number _unit) {
		BigDecimal n = BigDecimalMath.INSTANCE.cast(_n);
		BigDecimal unit = BigDecimalMath.INSTANCE.cast(_unit);
		BigDecimal r = n.divide(unit, 0, n.compareTo(BigDecimal.ZERO) < 0 ? RoundingMode.DOWN : RoundingMode.UP).multiply(unit);
		return r;
	}

	static public double toTens(int mag) {
		return mag > 0 ? MH.toTheTenth(mag) : (1d / MH.toTheTenth(-mag - 1));
	}
	public static double calcMinorUnitSize(double t) {
		return t / 5;
	}

	private static final int SECOND = 1000;
	private static final int MINUTE = 60 * SECOND;
	private static final int HOUR = 60 * MINUTE;
	private static final int DAY = 24 * HOUR;
	private static final int[] HUMAN_MILLIS_FACTORS = new int[] { 15 * SECOND, 30 * SECOND, MINUTE, 2 * MINUTE, 5 * MINUTE, 10 * MINUTE, 15 * MINUTE, 30 * MINUTE, HOUR, 2 * HOUR,
			4 * HOUR, 6 * HOUR, 12 * HOUR, DAY, 2 * DAY, 5 * DAY, 10 * DAY };
	public static double unitSizeMilliseconds(long buckets, long millis) {
		if (millis / buckets < 10000)//less than 10 seconds per bucket?
			return unitSize((double) millis);
		for (int i : HUMAN_MILLIS_FACTORS)
			if (millis <= i * buckets)
				return i;
		return 86400000;
	}

	static public double deref(double[] a, int i) {
		return a == null || a.length == 0 ? Double.NaN : a.length == 1 ? a[0] : a[i];
	}
	static public String deref(String[] a, int i) {
		return a == null || a.length == 0 ? null : a.length == 1 ? a[0] : a[i];
	}
	static public Color deref(Color[] a, int i) {
		return a == null || a.length == 0 ? null : a.length == 1 ? a[0] : a[i];
	}
	static public boolean deref(boolean[] a, int i) {
		return a == null || a.length == 0 ? false : a.length == 1 ? a[0] : a[i];
	}

	static public double radiansToX(double r, double a) {
		return r * Math.cos(a);
	}

	static public double radiansToY(double r, double a) {
		return -r * Math.sin(a);
	}

	public static int rd(double d) {
		if (d != d)
			return Integer.MIN_VALUE;
		else if (d > 0)
			return (int) (d + .5d);
		else if (d < 0)
			return (int) (d - .5d);
		return 0;
	}

}
