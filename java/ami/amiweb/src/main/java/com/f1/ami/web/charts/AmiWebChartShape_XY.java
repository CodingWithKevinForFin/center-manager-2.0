package com.f1.ami.web.charts;

import java.awt.Color;

import com.f1.utils.MH;

public class AmiWebChartShape_XY extends AmiWebChartShape {

	public static final char SHAPE_CIRCLE = 'c';
	public static final char SHAPE_SQUARE = 's';
	public static final char SHAPE_TRIANGLE = 't';
	public static final char SHAPE_DIAMOND = 'd';
	public static final char SHAPE_PENTAGON = 'p';
	public static final char SHAPE_HEXAGON = 'n';
	public static final char SHAPE_CROSS = 'x';
	public static final char SHAPE_TICK = 'k';
	public static final char SHAPE_HBAR = 'h';
	public static final char SHAPE_VBAR = 'v';
	public static final char SHAPE_INVALID = 0;
	private final double l;
	private final double r;
	private final double t;
	private final double b;
	private final double x;
	private final double y;
	private final double w;
	private final double h;
	private boolean snapLeft;
	private boolean snapTop;

	public AmiWebChartShape_XY(int layerPos, int groupNum, int rowNum, boolean isSelectable, double l, double t, double r, double b, double x, double y, double w, double h,
			char shape, int borderSize, Color borderColor, Color fillColor, boolean snapLeft, boolean snapTop) {
		super(layerPos, groupNum, rowNum, isSelectable, shape, borderSize, borderColor, fillColor);
		this.snapLeft = snapLeft;
		this.snapTop = snapTop;
		if (((x == x && w == w) || (l == l && r == r) || shape == SHAPE_HBAR) && ((y == y && h == h) || (t == t && b == b) || shape == SHAPE_VBAR)
				&& (borderColor != null || fillColor != null) && shape != SHAPE_INVALID) {
			if (r < l) {
				this.l = r;
				this.r = l;
			} else {
				this.l = l;
				this.r = r;
			}
			if (b < t) {
				this.t = b;
				this.b = t;
			} else {
				this.t = t;
				this.b = b;
			}
			this.x = x;
			this.y = y;
			this.w = Math.abs(w);
			this.h = Math.abs(h);
		} else {
			super.setInvalid();
			this.x = x;
			this.y = y;
			this.l = this.t = this.r = this.b = Double.NaN;
			this.w = this.h = Integer.MIN_VALUE;
		}
	}
	public double getL() {
		return l;
	}
	public double getR() {
		return r;
	}
	public double getT() {
		return t;
	}
	public double getB() {
		return b;
	}
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public double getW() {
		return w;
	}
	public double getH() {
		return h;
	}
	public double getL(AmiWebChartZoomMetrics zm) {
		ensureValid();
		return (l == l) ? zm.scaleX(l) : zm.scaleX(x - zm.unscaleW(w) / 2);
	}
	public double getT(AmiWebChartZoomMetrics zm) {
		ensureValid();
		return (t == t) ? zm.scaleY(t) : zm.scaleY(y - zm.unscaleH(h) / 2);
	}
	public double getR(AmiWebChartZoomMetrics zm) {
		ensureValid();
		return (r == r) ? zm.scaleX(r) : zm.scaleX(x + zm.unscaleW(w) / 2);
	}
	public double getB(AmiWebChartZoomMetrics zm) {
		ensureValid();
		return (b == b) ? zm.scaleY(b) : zm.scaleY(y + zm.unscaleH(h) / 2);
	}
	public double getW(AmiWebChartZoomMetrics zm) {
		ensureValid();
		if (l != l)
			return r != r ? w : zm.scaleW(r - x + zm.unscaleW(w) / 2);
		return r != r ? zm.scaleW(x - l + zm.unscaleW(w) / 2) : zm.scaleW(r - l);
	}
	public double getH(AmiWebChartZoomMetrics zm) {
		ensureValid();
		if (t != t)
			return b != b ? h : zm.scaleH(b - y + zm.unscaleH(h) / 2);
		return b != b ? zm.scaleH(y - t + zm.unscaleH(h) / 2) : zm.scaleH(b - t);
	}
	public double getMidX(AmiWebChartZoomMetrics zm) {
		if (l != l)
			return r != r ? zm.scaleX(x) : zm.scaleX(MH.avg(x - zm.unscaleW(w) / 2, r));
		return r != r ? zm.scaleX(MH.avg(l, x + zm.unscaleW(w) / 2)) : zm.scaleX(MH.avg(l, r));
	}
	public double getMidY(AmiWebChartZoomMetrics zm) {
		if (t != t)
			return b != b ? zm.scaleY(y) : zm.scaleY(MH.avg(y - zm.unscaleH(h) / 2, b));
		return b != b ? zm.scaleY(MH.avg(t, y + zm.unscaleH(h) / 2)) : zm.scaleY(MH.avg(t, b));
	}

	private void ensureValid() {
		if (!isValid())
			throw new IllegalStateException("Not valid shape");
	}
	public static char parseShape(String shape) {
		if (shape != null && shape.length() > 0) {
			switch (shape.charAt(0)) {
				case 'c':
					if ("circle".equals(shape))
						return SHAPE_CIRCLE;
					else if ("cross".equals(shape))
						return SHAPE_CROSS;
					break;
				case 'd':
					if ("diamond".equals(shape))
						return SHAPE_DIAMOND;
					break;

				case 'p':
					if ("pentagon".equals(shape))
						return SHAPE_PENTAGON;
					break;
				case 's':
					if ("square".equals(shape))
						return SHAPE_SQUARE;
					break;
				case 't':
					if ("triangle".equals(shape))
						return SHAPE_TRIANGLE;
					else if ("tick".equals(shape))
						return SHAPE_TICK;
					break;
				case 'h':
					if ("hbar".equals(shape))
						return SHAPE_HBAR;
					else if ("hexagon".equals(shape)) {
						return SHAPE_HEXAGON;
					}
					break;
				case 'v':
					if ("vbar".equals(shape))
						return SHAPE_VBAR;
					break;
			}
		}
		return SHAPE_INVALID;
	}

	public static String formatShape(char shape) {
		switch (shape) {
			case SHAPE_CIRCLE:
				return "circle";
			case SHAPE_SQUARE:
				return "square";
			case SHAPE_TRIANGLE:
				return "triangle";
			case SHAPE_DIAMOND:
				return "diamond";
			case SHAPE_PENTAGON:
				return "pentagon";
			case SHAPE_HEXAGON:
				return "hexagon";
			case SHAPE_CROSS:
				return "cross";
			case SHAPE_TICK:
				return "tick";
			case SHAPE_HBAR:
				return "hbar";
			case SHAPE_VBAR:
				return "vbar";
			default:
				return null;
		}
	}
	public void draw(AmiWebChartGraphicsWrapper gw, Color fillColor, int borderSize, Color borderColor) {
		AmiWebChartZoomMetrics zm = gw.getZoom();
		if (getShape() != SHAPE_INVALID) {
			switch (getShape()) {
				case SHAPE_CIRCLE: {
					double _w = getW(zm);
					if (_w == 0d)
						break;
					double _h = getH(zm);
					if (_h == 0d)
						break;
					int w = rdnz(_w);
					int h = rdnz(_h);
					int l = AmiWebChartUtils.rd(snapLeft ? getL(zm) : (getR(zm) - w));
					int t = AmiWebChartUtils.rd(snapTop ? getT(zm) : (getB(zm) - h));
					if (fillColor != null)
						gw.fillOvalDirect(l, t, w, h, fillColor);
					if (borderSize > 0 && borderColor != null)
						gw.drawOvalDirect(l, t, w, h, borderSize, borderColor);
					break;
				}
				case SHAPE_TRIANGLE: {
					double _w = getW(zm);
					if (_w == 0d)
						break;
					double _h = getH(zm);
					if (_h == 0d)
						break;
					int w = rdnz(_w);
					int h = rdnz(_h);
					int l = AmiWebChartUtils.rd(snapLeft ? getL(zm) : (getR(zm) - w));
					int t = AmiWebChartUtils.rd(snapTop ? getT(zm) : (getB(zm) - h));
					if (fillColor != null)
						gw.fillTriangleDirect(l, t, w, h, fillColor);
					if (borderSize > 0 && borderColor != null)
						gw.drawTriangleDirect(l, t, w, h, borderSize, borderColor);
					break;
				}
				case SHAPE_SQUARE: {
					double _w = getW(zm);
					if (_w == 0d)
						break;
					double _h = getH(zm);
					if (_h == 0d)
						break;
					int w = rdnz(_w);
					int h = rdnz(_h);
					int l = AmiWebChartUtils.rd(snapLeft ? getL(zm) : (getR(zm) - w));
					int t = AmiWebChartUtils.rd(snapTop ? getT(zm) : (getB(zm) - h));

					if (fillColor != null)
						gw.fillSquareDirect(l, t, w, h, fillColor);
					if (borderSize > 0 && borderColor != null)
						gw.drawSquareDirect(l, t, w, h, borderSize, borderColor);
					break;
				}
				case SHAPE_DIAMOND: {
					double _w = getW(zm);
					if (_w == 0d)
						break;
					double _h = getH(zm);
					if (_h == 0d)
						break;
					int w = rdnz(_w);
					int h = rdnz(_h);
					int l = AmiWebChartUtils.rd(snapLeft ? getL(zm) : (getR(zm) - w));
					int t = AmiWebChartUtils.rd(snapTop ? getT(zm) : (getB(zm) - h));
					if (fillColor != null)
						gw.fillDiamondDirect(l, t, w, h, fillColor);
					if (borderSize > 0 && borderColor != null)
						gw.drawDiamondDirect(l, t, w, h, borderSize, borderColor);
					break;
				}
				case SHAPE_PENTAGON: {
					double _w = getW(zm);
					if (_w == 0d)
						break;
					double _h = getH(zm);
					if (_h == 0d)
						break;
					int w = rdnz(_w);
					int h = rdnz(_h);
					int l = AmiWebChartUtils.rd(snapLeft ? getL(zm) : (getR(zm) - w));
					int t = AmiWebChartUtils.rd(snapTop ? getT(zm) : (getB(zm) - h));
					if (fillColor != null)
						gw.fillPentagonDirect(l, t, w, h, fillColor);
					if (borderSize > 0 && borderColor != null)
						gw.drawPentagonDirect(l, t, w, h, borderSize, borderColor);
					break;
				}
				case SHAPE_HEXAGON: {
					double _w = getW(zm);
					if (_w == 0d)
						break;
					double _h = getH(zm);
					if (_h == 0d)
						break;
					int w = rdnz(_w);
					int h = rdnz(_h);
					int l = AmiWebChartUtils.rd(snapLeft ? getL(zm) : (getR(zm) - w));
					int t = AmiWebChartUtils.rd(snapTop ? getT(zm) : (getB(zm) - h));
					if (fillColor != null)
						gw.fillHexagonDirect(l, t, w, h, fillColor);
					if (borderSize > 0 && borderColor != null)
						gw.drawHexagonDirect(l, t, w, h, borderSize, borderColor);
					break;
				}
				case SHAPE_CROSS: {
					double _w = getW(zm);
					if (_w == 0d)
						break;
					double _h = getH(zm);
					if (_h == 0d)
						break;
					int w = rdnz(_w);
					int h = rdnz(_h);
					int l = AmiWebChartUtils.rd(snapLeft ? getL(zm) : (getR(zm) - w));
					int t = AmiWebChartUtils.rd(snapTop ? getT(zm) : (getB(zm) - h));
					if (fillColor != null)
						gw.fillCrossDirect(l, t, w, h, fillColor);
					if (borderSize > 0 && borderColor != null)
						gw.drawCrossDirect(l, t, w, h, borderSize, fillColor);
					break;
				}
				case SHAPE_TICK: {
					double _w = getW(zm);
					if (_w == 0d)
						break;
					double _h = getH(zm);
					if (_h == 0d)
						break;
					int w = rdnz(_w);
					int h = rdnz(_h);
					int l = AmiWebChartUtils.rd(snapLeft ? getL(zm) : (getR(zm) - w));
					int t = AmiWebChartUtils.rd(snapTop ? getT(zm) : (getB(zm) - h));
					if (fillColor != null)
						gw.fillTickDirect(l, t, w, h, fillColor);
					if (borderSize > 0 && borderColor != null)
						gw.drawTickDirect(l, t, w, h, borderSize, fillColor);
					break;
				}
				case SHAPE_HBAR: {
					double _h = getH(zm);
					if (_h == 0d)
						break;
					int h = rdnz(_h);
					int t = AmiWebChartUtils.rd(snapTop ? getT(zm) : (getB(zm) - h));
					int w = zm.getWidth();
					if (fillColor != null)
						gw.fillSquareDirect(0, t, w, h, fillColor);
					if (borderSize > 0 && borderColor != null)
						gw.drawSquareDirect(0, t, w, h, borderSize, borderColor);
					break;
				}
				case SHAPE_VBAR:
					double _w = getW(zm);
					if (_w == 0d)
						break;
					int w = rdnz(_w);
					int l = AmiWebChartUtils.rd(snapLeft ? getL(zm) : (getR(zm) - w));
					int h = zm.getHeight();
					if (fillColor != null)
						gw.fillSquareDirect(l, 0, w, h, fillColor);
					if (borderSize > 0 && borderColor != null)
						gw.drawSquareDirect(l, 0, w, h, borderSize, borderColor);
					break;
			}
		}
	}
	public double getDistance(AmiWebChartZoomMetrics zm, int x2, int y2) {
		double x = getMidX(zm);
		double y = getMidY(zm);
		return Math.sqrt(MH.sq(MH.diff(x, x2)) + MH.sq(MH.diff(y, y2)));
	}
	public double getDistance(double x2, double y2) {
		double x = l == l && r == r ? MH.avg(l, r) : this.x;
		double y = t == t && b == b ? MH.avg(t, b) : this.y;
		return Math.sqrt(MH.sq(MH.diff(x, x2)) + MH.sq(MH.diff(y, y2)));
	}

	public boolean intersects(AmiWebChartZoomMetrics zm, int x2, int y2, int w2, int h2) {
		return getL(zm) <= x2 + w2 && x2 <= getR(zm) && getT(zm) <= y2 + h2 && y2 <= getB(zm);
	}
	public boolean hasXY() {
		return x == x && y == y;
	}

	public static int rdnz(double d) {
		if (d < 0)
			return Math.min(-1, (int) (d - .5d));
		else
			return Math.max(+1, (int) (d + .5d));
	}
}
