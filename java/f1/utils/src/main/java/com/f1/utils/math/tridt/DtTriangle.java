package com.f1.utils.math.tridt;

public class DtTriangle {
	DtPoint a, b, c;
	DtTriangle abnext, bcnext, canext;
	DtCircle circum;
	int _mc = 0;

	boolean _mark = false; // tag - for bfs algorithms
	public boolean halfplane;

	/** constructs a triangle form 3 point - store it in counterclockwise order. */
	public DtTriangle(DtPoint A, DtPoint B, DtPoint C) {
		a = A;
		halfplane = false;
		int res = C.pointLineTest(A, B);
		if ((res <= DtPoint.LEFT) || (res == DtPoint.INFRONTOFA) || (res == DtPoint.BEHINDB)) {
			b = B;
			c = C;
		} else {
			b = C;
			c = B;
		}
		circumcircle();
	}

	public DtTriangle(DtPoint A, DtPoint B) {
		a = A;
		b = B;
		c = null;
		halfplane = true;
	}

	public boolean isHalfplane() {
		return halfplane;
	}
	public DtPoint getPoint1() {
		return a;
	}
	public DtPoint getPoint2() {
		return b;
	}
	public DtPoint getPoint3() {
		return c;
	}
	public DtTriangle getAdjacent12() {
		return this.abnext;
	}
	public DtTriangle getAdjacent23() {
		return this.bcnext;
	}
	public DtTriangle getAdjacent31() {
		return this.canext;
	}

	public DtRect getBoundingBox() {
		DtPoint lowerLeft, upperRight;
		lowerLeft = new DtPoint(Math.min(a.getX(), Math.min(b.getX(), c.getX())), Math.min(a.getY(), Math.min(b.getY(), c.getY())));
		upperRight = new DtPoint(Math.max(a.getX(), Math.max(b.getX(), c.getX())), Math.max(a.getY(), Math.max(b.getY(), c.getY())));
		return new DtRect(lowerLeft, upperRight);
	}

	void switchAdjacent(DtTriangle old, DtTriangle nuw) {
		if (abnext == old)
			abnext = nuw;
		else if (bcnext == old)
			bcnext = nuw;
		else if (canext == old)
			canext = nuw;
		else
			throw new IllegalArgumentException("not adjacent to this triangle: " + old);
	}

	DtTriangle getAdjacentClockwiseTo(DtPoint p) {
		if (a.equals(p))
			return canext;
		if (b.equals(p))
			return abnext;
		if (c.equals(p))
			return bcnext;
		else
			throw new IllegalArgumentException("not a point of this triangle: " + p);
	}

	DtTriangle nextNeighbor(DtPoint p, DtTriangle prevTriangle) {
		DtTriangle r = null;

		if (r.isHalfplane() || r.equals(prevTriangle)) {
			if (a.equals(p))
				r = abnext;
			else if (b.equals(p))
				r = bcnext;
			else if (c.equals(p))
				r = canext;
		} else {
			if (a.equals(p))
				r = canext;
			else if (b.equals(p))
				r = abnext;
			else if (c.equals(p))
				r = bcnext;
			else
				throw new IllegalStateException();
		}

		return r;
	}

	DtCircle circumcircle() {
		double u = ((a.x - b.x) * (a.x + b.x) + (a.y - b.y) * (a.y + b.y)) / 2d;
		double v = ((b.x - c.x) * (b.x + c.x) + (b.y - c.y) * (b.y + c.y)) / 2d;
		double den = (a.x - b.x) * (b.y - c.y) - (b.x - c.x) * (a.y - b.y);
		if (den == 0) //degenerate case
			circum = new DtCircle(a, Double.POSITIVE_INFINITY);
		else {
			DtPoint cen = new DtPoint((u * (b.y - c.y) - v * (a.y - b.y)) / den, (v * (a.x - b.x) - u * (b.x - c.x)) / den);
			circum = new DtCircle(cen, cen.getDistanceXy(a));
		}
		return circum;
	}

	boolean circumcircle_contains(DtPoint p) {
		return circum.getRadius() > circum.getCenter().getDistanceXy(p);
	}

	public String toString() {
		if (c == null)
			return "half_triangle[" + a.toString() + b.toString() + "]";
		return "triangle[" + a.toString() + b.toString() + c.toString() + "]";
	}

	public boolean contains(DtPoint p, boolean includeBorder) {
		if (this.isHalfplane() || p == null)
			return false;
		else if (isCorner(p))
			return true;

		int ab = p.pointLineTest(a, b);
		int bc = p.pointLineTest(b, c);
		int ca = p.pointLineTest(c, a);

		if ((ab == DtPoint.LEFT && bc == DtPoint.LEFT && ca == DtPoint.LEFT) || (ab == DtPoint.RIGHT && bc == DtPoint.RIGHT && ca == DtPoint.RIGHT))
			return true;
		return includeBorder && (ab == DtPoint.ONSEGMENT || bc == DtPoint.ONSEGMENT || ca == DtPoint.ONSEGMENT);
	}

	/**
	 * Checks if the given point is a corner of this triangle.
	 * 
	 * @param p
	 *            The given point.
	 * @return True iff the given point is a corner of this triangle.
	 * 
	 *         By Eyal Roth & Doron Ganel.
	 */
	public boolean isCorner(DtPoint p) {
		return (p.x == a.x & p.y == a.y) | (p.x == b.x & p.y == b.y) | (p.x == c.x & p.y == c.y);
	}

	//Doron
	public boolean fallInsideCircumcircle(DtPoint[] arrayPoints) {
		boolean isInside = false;
		DtPoint p1 = this.getPoint1();
		DtPoint p2 = this.getPoint2();
		DtPoint p3 = this.getPoint3();
		int i = 0;
		while (!isInside && i < arrayPoints.length) {
			DtPoint p = arrayPoints[i];
			if (!p.equals(p1) && !p.equals(p2) && !p.equals(p3)) {
				isInside = this.circumcircle_contains(p);
			}
			i++;
		}

		return isInside;
	}

	public double getZvalueAt(double x, double y) {
		if (isHalfplane())
			throw new RuntimeException("not a triangle");
		if (x == a.x & y == a.y)
			return a.z;
		else if (x == b.x & y == b.y)
			return b.z;
		else if (x == c.x & y == c.y)
			return c.z;

		double xr = 0, x0 = x, x1 = a.x, x2 = b.x, x3 = c.x;
		double yr = 0, y0 = y, y1 = a.y, y2 = b.y, y3 = c.y;
		double zr = 0, m01 = 0, k01 = 0, m23 = 0, k23 = 0;

		// 0 - regular, 1-horizontal , 2-vertical.
		int flag01 = 0;
		if (x0 != x1) {
			m01 = (y0 - y1) / (x0 - x1);
			k01 = y0 - m01 * x0;
			if (m01 == 0)
				flag01 = 1;
		} else { // 2-vertical.
			flag01 = 2;//x01 = x0
		}
		int flag23 = 0;
		if (x2 != x3) {
			m23 = (y2 - y3) / (x2 - x3);
			k23 = y2 - m23 * x2;
			if (m23 == 0)
				flag23 = 1;
		} else { // 2-vertical.
			flag23 = 2;//x01 = x0
		}

		if (flag01 == 2) {
			xr = x0;
			yr = m23 * xr + k23;
		} else {
			if (flag23 == 2) {
				xr = x2;
				yr = m01 * xr + k01;
			} else { // regular case 
				xr = (k23 - k01) / (m01 - m23);
				yr = m01 * xr + k01;

			}
		}
		double r = 0;
		if (flag23 == 2) {
			r = (y2 - yr) / (y2 - y3);
		} else {
			r = (x2 - xr) / (x2 - x3);
		}
		zr = b.z + (c.z - b.z) * r;
		if (flag01 == 2) {
			r = (y1 - y0) / (y1 - yr);
		} else {
			r = (x1 - x0) / (x1 - xr);
		}
		return a.z + (zr - a.z) * r;
	}

	public double getZvalueAt(DtPoint p) {
		return getZvalueAt(p.getX(), p.getY());
	}

}
