package com.f1.utils.math.tridt;

import com.f1.utils.OH;

public class DtRect {
	public static final DtRect NULL = new DtRect();

	private final double minx;
	private final double maxx;
	private final double miny;
	private final double maxy;

	private DtRect() {
		minx = 0;
		maxx = -1;
		miny = 0;
		maxy = -1;
	}

	public DtRect(double x1, double x2, double y1, double y2) {
		if (x1 <= x2) {
			minx = x1;
			maxx = x2;
		} else {
			minx = x2;
			maxx = x1;
		}
		if (y1 <= y2) {
			miny = y1;
			maxy = y2;
		} else {
			miny = y2;
			maxy = y1;
		}
	}

	public DtRect(DtPoint lowerLeft, DtPoint upperRight) {
		this(lowerLeft.x, upperRight.x, lowerLeft.y, upperRight.y);
	}

	public boolean isNull() {
		return maxx < minx;
	}

	public boolean contains(DtRect other) {
		return !(isNull() || other.isNull()) && other.minx >= minx && other.maxy <= maxx && other.miny >= miny && other.maxy <= maxy;
	}
	public boolean contains(DtPoint point) {
		return OH.isBetween(point.x, minx, maxx) && OH.isBetween(point.y, miny, maxy);
	}

	public DtRect unionWith(DtRect other) {
		if (other.isNull())
			return this;
		else if (isNull())
			return other;
		else
			return new DtRect(Math.min(minx, other.minx), Math.max(maxx, other.maxx), Math.min(miny, other.miny), Math.max(maxy, other.maxy));
	}

	public double getMinX() {
		return minx;
	}

	public double getMinY() {
		return miny;
	}

	public double getMaxX() {
		return maxx;
	}

	public double getMaxY() {
		return maxy;
	}

	public double getWidth() {
		return maxx - minx;
	}

	public double getHeight() {
		return maxy - miny;
	}

	public DtPoint getMinPoint() {
		return new DtPoint(minx, miny);
	}

	public DtPoint getMaxPoint() {
		return new DtPoint(maxx, maxy);
	}

	public DtRect unionWith(DtPoint p) {
		if (isNull())
			return new DtRect(p, p);
		else if (contains(p))
			return this;
		else
			return new DtRect(Math.min(minx, p.getX()), Math.max(maxx, p.getX()), Math.min(miny, p.getY()), Math.max(maxy, p.getY()));
	}
}