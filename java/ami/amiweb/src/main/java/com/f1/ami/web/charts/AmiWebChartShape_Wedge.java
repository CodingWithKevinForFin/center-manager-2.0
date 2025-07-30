package com.f1.ami.web.charts;

import java.awt.Color;

import com.f1.utils.MH;
import com.f1.utils.OH;

public class AmiWebChartShape_Wedge extends AmiWebChartShape {

	private final double a1, a2, r1, r2, a, r, aw, rh, centerX, centerY;
	public AmiWebChartShape_Wedge(int layerPos, int groupNum, int rowNum, boolean isSelectable, int borderSize, Color borderColor, Color fillColor, double centerX, double centerY,
			double r1, double a1, double r2, double a2, double r, double a, double rh, double aw) {
		super(layerPos, groupNum, rowNum, isSelectable, SHAPE_WEDGE, borderSize, borderColor, fillColor);
		if (centerX == centerX && centerY == centerY && ((a == a && aw == aw) || (a1 == a1 && a2 == a2)) && ((r == r && rh == rh) || (r1 == r1 && r2 == r2))) {
			if (a1 < a2) {
				this.a1 = a1;
				this.a2 = a2;
			} else {
				this.a1 = a2;
				this.a2 = a1;
			}
			if (r1 < r2) {
				this.r1 = r1;
				this.r2 = r2;
			} else {
				this.r1 = r2;
				this.r2 = r1;
			}
			this.centerX = centerX;
			this.centerY = centerY;
			this.a = a;
			this.r = r;
			this.aw = Math.abs(AmiWebChartUtils.rd(aw));
			this.rh = Math.abs(AmiWebChartUtils.rd(rh));
		} else {
			super.setInvalid();
			this.centerX = this.centerY = this.a1 = this.a2 = this.r1 = this.r2 = this.a = this.r = this.aw = this.rh = Double.NaN;
		}
	}

	public double getR1() {
		return r1 == r1 ? r1 : (r - rh / 2);
	}
	public double getR2() {
		return r2 == r2 ? r2 : (r + rh / 2);
	}
	public double getA1() {
		return a1 == a1 ? a1 : (a - aw / 2);
	}
	public double getA2() {
		return a2 == a2 ? a2 : (a + aw / 2);
	}

	@Override
	public void draw(AmiWebChartGraphicsWrapper gw, Color fillColor, int borderSize, Color borderColor) {
		if (isValid()) {
			gw.fillWedge(centerX, centerY, getR1(), getA1(), getR2(), getA2(), fillColor);
			gw.drawWedge(centerX, centerY, getR1(), getA1(), getR2(), getA2(), borderSize, borderColor);

		}
	}

	@Override
	double getDistance(AmiWebChartZoomMetrics zm, int x2, int y2) {
		double a1 = AmiWebChartGraphicsWrapper.normalizeAngle(getA1() * Math.PI / 180);
		double a2 = AmiWebChartGraphicsWrapper.normalizeAngle(getA2() * Math.PI / 180);
		double r1 = getR1();
		double r2 = getR2();
		if (intersects(r1, a1, r2, a2, zm, x2, y2))
			return 0;
		double r = MH.avg(r1, r2);
		double a = MH.avg(a1, a2);
		double x = zm.scaleX(centerX + AmiWebChartGraphicsWrapper.radiansToX(r, a));
		double y = zm.scaleY(centerY + AmiWebChartGraphicsWrapper.radiansToY(r, a));
		return Math.sqrt(MH.sq(MH.diff(x, x2)) + MH.sq(MH.diff(y, y2)));
	}

	@Override
	boolean intersects(AmiWebChartZoomMetrics zm, int x, int y, int w, int h) {
		int x2 = x + w;
		int y2 = y + h;
		double a1 = AmiWebChartGraphicsWrapper.normalizeAngle(getA1() * Math.PI / 180);
		double a2 = AmiWebChartGraphicsWrapper.normalizeAngle(getA2() * Math.PI / 180);
		double r1 = getR1();
		double r2 = getR2();
		if (a1 > a2) {
			double tmp = a1;
			a1 = a2;
			a2 = tmp;
		}
		double p1x = zm.scaleX(centerX + AmiWebChartGraphicsWrapper.radiansToX(r1, a1));
		double p1y = zm.scaleY(centerY + AmiWebChartGraphicsWrapper.radiansToY(r1, a1));
		double p2x = zm.scaleX(centerX + AmiWebChartGraphicsWrapper.radiansToX(r2, a1));
		double p2y = zm.scaleY(centerY + AmiWebChartGraphicsWrapper.radiansToY(r2, a1));
		double p3x = zm.scaleX(centerX + AmiWebChartGraphicsWrapper.radiansToX(r2, a2));
		double p3y = zm.scaleY(centerY + AmiWebChartGraphicsWrapper.radiansToY(r2, a2));
		double p4x = zm.scaleX(centerX + AmiWebChartGraphicsWrapper.radiansToX(r1, a2));
		double p4y = zm.scaleY(centerY + AmiWebChartGraphicsWrapper.radiansToY(r1, a2));
		if (linesIntersect(p1x, p1y, p2x, p2y, x, y, x, y2))
			return true;
		if (linesIntersect(p1x, p1y, p2x, p2y, x, y, x2, y))
			return true;
		if (linesIntersect(p1x, p1y, p2x, p2y, x2, y2, x, y2))
			return true;
		if (linesIntersect(p1x, p1y, p2x, p2y, x2, y2, x2, y))
			return true;
		if (linesIntersect(p3x, p3y, p4x, p4y, x, y, x, y2))
			return true;
		if (linesIntersect(p3x, p3y, p4x, p4y, x, y, x2, y))
			return true;
		if (linesIntersect(p3x, p3y, p4x, p4y, x2, y2, x, y2))
			return true;
		if (linesIntersect(p3x, p3y, p4x, p4y, x2, y2, x2, y))
			return true;
		if (intersects(r1, a1, r2, a2, zm, x, y))
			return true;
		if (intersects(r1, a1, r2, a2, zm, x2, y))
			return true;
		if (intersects(r1, a1, r2, a2, zm, x, y2))
			return true;
		if (intersects(r1, a1, r2, a2, zm, x2, y2))
			return true;

		if (intersects(r1, a1, r2, a2, zm, x + w / 2, y))
			return true;
		if (intersects(r1, a1, r2, a2, zm, x + w / 2, y2))
			return true;
		if (intersects(r1, a1, r2, a2, zm, x, y2 + h / 2))
			return true;
		if (intersects(r1, a1, r2, a2, zm, x2, y2 + h / 2))
			return true;
		return false;
	}

	private boolean intersects(double r1, double a1, double r2, double a2, AmiWebChartZoomMetrics zm, int x, int y) {
		double x2 = zm.unscaleX(x) - centerX;
		double y2 = zm.unscaleY(y) - centerY;
		double angle = AmiWebChartGraphicsWrapper.normalizeAngle(AmiWebChartGraphicsWrapper.toRadians(x2, y2));
		double radius = AmiWebChartGraphicsWrapper.toRadius(x2, y2);
		return (OH.isBetween(radius, r1, r2) && OH.isBetween(angle, a1, a2));
	}

	boolean linesIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
		final double rise2 = y4 - y3;
		final double run2 = x4 - x3;
		final double run1 = x2 - x1;
		final double rise1 = y2 - y1;
		final double offsetY = y1 - y3;
		final double offsetX = x1 - x3;
		final double p1 = run1 * offsetY - rise1 * offsetX;
		final double p2 = run2 * offsetY - rise2 * offsetX;
		final double d = rise2 * run1 - run2 * rise1;
		return OH.isBetween(p2 / d, 0, 1) && OH.isBetween(p1 / d, 0, 1);
	}
}
