package com.f1.utils.math.tridt;

import com.f1.utils.MH;
import com.f1.utils.OH;

public class DtPoint implements Comparable<DtPoint> {

	final double x, y, z;
	final private int color;

	public DtPoint(double x, double y, double z, int color) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.color = color;
	}
	public DtPoint(double x, double y, double z) {
		this(x, y, z, 0);
	}

	@Override
	public int hashCode() {
		return (int) (color + x * 31 + y * 31 * 31 + z * 31 * 31 * 31);
	}

	public DtPoint(double x, double y) {
		this(x, y, 0, 0);
	}

	public int getColor() {
		return color;
	}

	public double getX() {
		return x;
	};

	public double getY() {
		return y;
	};

	public double getZ() {
		return z;
	};

	double getDistanceXy(DtPoint p) {
		return MH.sq(p.x - x) + MH.sq(p.y - y);
	}

	boolean isLess(DtPoint p) {
		return (x < p.x) || ((x == p.x) && (y < p.y));
	}

	boolean isGreater(DtPoint p) {
		return (x > p.x) || ((x == p.x) && (y > p.y));
	}

	public boolean equals(DtPoint p) {
		return (x == p.x) && (y == p.y);
	}

	public String toString() {
		return " [" + x + "," + y + "," + z + "]";
	}

	public double distance(DtPoint p) {
		double temp = MH.sq(p.getX() - x) + MH.sq(p.getY() - y);
		return Math.sqrt(temp);
	}

	public double distance3D(DtPoint p) {
		double temp = MH.sq(p.getX() - x) + MH.sq(p.getY() - y) + MH.sq(p.getZ() - z);
		return Math.sqrt(temp);
	}

	public final static int ONSEGMENT = 0;
	public final static int LEFT = 1;
	public final static int RIGHT = 2;
	public final static int INFRONTOFA = 3;
	public final static int BEHINDB = 4;
	public final static int ERROR = 5;

	public int pointLineTest(DtPoint a, DtPoint b) {

		double dx = b.x - a.x;
		double dy = b.y - a.y;
		double res = dy * (x - a.x) - dx * (y - a.y);

		// a, b, and this are not collinear
		if (res < 0)
			return LEFT;
		if (res > 0)
			return RIGHT;

		// a, b, and this are collinear
		if (dx > 0) {
			if (x < a.x)
				return INFRONTOFA;
			if (b.x < x)
				return BEHINDB;
			return ONSEGMENT;
		}
		if (dx < 0) {
			if (x > a.x)
				return INFRONTOFA;
			if (b.x > x)
				return BEHINDB;
			return ONSEGMENT;
		}
		if (dy > 0) {
			if (y < a.y)
				return INFRONTOFA;
			if (b.y < y)
				return BEHINDB;
			return ONSEGMENT;
		}
		if (dy < 0) {
			if (y > a.y)
				return INFRONTOFA;
			if (b.y > y)
				return BEHINDB;
			return ONSEGMENT;
		}
		return ERROR;
	}

	boolean areCollinear(DtPoint a, DtPoint b) {
		return (b.y - a.y) * (x - a.x) - (b.x - a.x) * (y - a.y) == 0;
	}

	DtPoint circumcenter(DtPoint a, DtPoint b) {
		double u = ((a.x - b.x) * (a.x + b.x) + (a.y - b.y) * (a.y + b.y)) / 2d;
		double v = ((b.x - x) * (b.x + x) + (b.y - y) * (b.y + y)) / 2d;
		double den = (a.x - b.x) * (b.y - y) - (b.x - x) * (a.y - b.y);
		return new DtPoint((u * (b.y - y) - v * (a.y - b.y)) / den, (v * (a.x - b.x) - u * (b.x - x)) / den);
	}

	@Override
	public int compareTo(DtPoint p) {
		final int r = OH.compare(x, p.x);
		return r == 0 ? OH.compare(y, p.y) : r;
	}

	public boolean equals(Object ob) {
		if (ob == null || ob.getClass() != DtPoint.class)
			return false;
		return compareTo((DtPoint) ob) == 0;
	}
}
