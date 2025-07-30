package com.f1.ami.web.charts;

import static com.f1.ami.web.charts.AmiWebChartGraphicsWrapper.ALIGN_BOTTOM;
import static com.f1.ami.web.charts.AmiWebChartGraphicsWrapper.ALIGN_LEFT;
import static com.f1.ami.web.charts.AmiWebChartGraphicsWrapper.ALIGN_MIDDLE;
import static com.f1.ami.web.charts.AmiWebChartGraphicsWrapper.ALIGN_RIGHT;
import static com.f1.ami.web.charts.AmiWebChartGraphicsWrapper.ALIGN_TOP;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.utils.LH;

public class AmiWebImageGenerator_RadialChart extends AmiWebImageGenerator {

	public final static byte LINE_TYPE_DIRECT = 0;
	public final static byte LINE_TYPE_HORZ = 1;
	public final static byte LINE_TYPE_VERT = 2;
	public static final double PI2 = Math.PI * 2;
	public static final double PI_180 = Math.PI / 180;
	private static final Logger log = LH.get();

	private Color borderColor;
	private Color circleColor;
	private Color spokesColor;
	private double borderSize;
	private double circleSize;
	private double spokesSize;
	private int spokesCount;
	private int innerPaddingPx;
	private int outerPaddingPx;

	private Color fontColor;
	private int fontSize;
	private double sAngle;
	private double lAngle;
	private double eAngle;
	private int circlesCount;
	private double rMin;
	private double rMax;
	private double centerX;
	private double centerY;
	private List<String> rLabels;
	private List<String> aLabels;
	private List<Map<String, Object>> seriesList = new ArrayList();
	private int it;
	private int itemsCount;
	private int opacity;

	public void setOpacity(int opacity) {
		this.opacity = opacity;
	}

	@Override
	public int getOpacity() {
		return this.opacity;
	}
	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	public Color getCircleColor() {
		return circleColor;
	}

	public void setCircleColor(Color circleColor) {
		this.circleColor = circleColor;
	}

	public Color getSpokesColor() {
		return spokesColor;
	}

	public void setSpokesColor(Color spokesColor) {
		this.spokesColor = spokesColor;
	}

	public double getBorderSize() {
		return borderSize;
	}

	public void setBorderSize(double borderSize) {
		this.borderSize = borderSize;
	}

	public double getCircleSize() {
		return circleSize;
	}

	public void setCircleSize(double circleSize) {
		this.circleSize = circleSize;
	}

	public double getSpokesSize() {
		return spokesSize;
	}

	public void setSpokesSize(double spokesSize) {
		this.spokesSize = spokesSize;
	}

	public int getSpokesCount() {
		return spokesCount;
	}

	public void setSpokesCount(int spokesCount) {
		this.spokesCount = spokesCount;
	}

	public int getInnerPaddingPx() {
		return innerPaddingPx;
	}

	public void setInnerPaddingPx(int innerPaddingPx) {
		this.innerPaddingPx = innerPaddingPx;
	}

	public int getOuterPaddingPx() {
		return outerPaddingPx;
	}

	public void setOuterPaddingPx(int outerPaddingPx) {
		this.outerPaddingPx = outerPaddingPx;
	}

	public Color getFontColor() {
		return fontColor;
	}

	public void setFontColor(Color fontColor) {
		this.fontColor = fontColor;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public double getsAngle() {
		return sAngle;
	}

	public void setsAngle(double sAngle) {
		this.sAngle = sAngle;
	}

	public double getlAngle() {
		return lAngle;
	}

	public void setlAngle(double lAngle) {
		this.lAngle = lAngle;
	}

	public double geteAngle() {
		return eAngle;
	}

	public void seteAngle(double eAngle) {
		this.eAngle = eAngle;
	}

	public int getCirclesCount() {
		return circlesCount;
	}

	public void setCirclesCount(int circlesCount) {
		this.circlesCount = circlesCount;
	}

	public void setRMax(double rmax) {
		this.rMax = rmax;
	}

	public double getRMin() {
		return this.rMin;
	}
	public double getRMax() {
		return this.rMax;
	}

	public void setRMin(double rmin) {
		this.rMin = rmin;
	}

	public void setRLabels(List<String> rLabels) {
		this.rLabels = rLabels;
	}

	public List<String> getRLabels() {
		return this.rLabels;
	}
	public void setALabels(List<String> aLabels) {
		this.aLabels = aLabels;
	}

	public List<String> getALabels() {
		return this.aLabels;
	}
	public String getDescFontStyle() {
		return this.seriesList.isEmpty() ? "normal" : (String) this.seriesList.get(0).get("descFontStyle");
	}
	public String getDescFontFamily() {
		return this.seriesList.isEmpty() ? "arial" : (String) this.seriesList.get(0).get("descFontFam");
	}
	@Override
	public boolean hasGrid() {
		if (this.borderSize > 0 && this.borderColor != null)
			return true;
		if (this.spokesCount > 0 && this.spokesSize > 0 && this.spokesColor != null)
			return true;
		if (this.circlesCount > 0 && this.circleSize > 0 && this.circleColor != null)
			return true;
		return false;
	}
	@Override
	public void drawGrid(AmiWebChartGraphicsWrapper gw) {
		try {
			double outer = Math.min(gw.getWidth(), gw.getHeight()) / 2 - this.outerPaddingPx;
			if (this.borderSize > 0 && this.borderColor != null) {
				gw.drawArc(centerX, centerY, (int) this.innerPaddingPx, (int) sAngle, (int) this.innerPaddingPx, (int) eAngle, this.borderSize, this.borderColor);
				gw.drawArc(centerX, centerY, (int) outer, (int) sAngle, (int) outer, (int) eAngle, this.borderSize, this.borderColor);
			}
			double degrees = (this.eAngle - this.sAngle) / this.spokesCount;
			if (this.spokesCount > 0 && this.spokesSize > 0 && this.spokesColor != null) {
				for (int i = 0; i < this.spokesCount + 1; i++) {
					int d = (int) (this.sAngle + i * degrees);
					gw.drawArc(centerX, centerY, (int) this.innerPaddingPx, d, (int) outer, d, this.spokesSize, this.spokesColor);
					if (this.fontColor != null) {
						int rx = (int) AmiWebChartGraphicsWrapper.radiansToX(outer, d * Math.PI / 180);
						int ry = (int) AmiWebChartGraphicsWrapper.radiansToY(outer, d * Math.PI / 180);
						int angle = d;
						if (i != this.spokesCount || this.sAngle != 0 || this.eAngle != 360) {
							gw.drawText(this.aLabels.get(i), rx + centerX, ry + centerY, -angle, AmiWebChartGraphicsWrapper.ALIGN_LEFT, AmiWebChartGraphicsWrapper.ALIGN_MIDDLE,
									this.fontColor, this.fontSize, getDescFontFamily(), getDescFontStyle(), false, 1);
						}
					}
				}
			}
			if (this.circlesCount > 0 && this.circleSize > 0 && this.circleColor != null) {
				double spacing = (outer - this.innerPaddingPx) / this.circlesCount;
				for (int i = 1; i < this.circlesCount; i++) {
					int t = (int) ((i * spacing) + this.innerPaddingPx);
					if (this.circleSize > 0) {
						gw.drawArc(centerX, centerY, t, (int) sAngle, t, (int) eAngle, this.circleSize, this.circleColor);
					}
					int rx = (int) AmiWebChartGraphicsWrapper.radiansToX(t, this.lAngle * Math.PI / 180);
					int ry = (int) AmiWebChartGraphicsWrapper.radiansToY(t, this.lAngle * Math.PI / 180);
					if (this.fontColor != null) {
						if (this.rLabels != null) {
							double angle = -this.lAngle;
							gw.drawText(this.rLabels.get(i), rx + centerX, ry + centerY, (int) angle, (byte) 0,
									(byte) (-270 <= angle && angle < -90 ? AmiWebChartGraphicsWrapper.ALIGN_BOTTOM : AmiWebChartGraphicsWrapper.ALIGN_TOP), this.fontColor,
									this.fontSize, getDescFontFamily(), getDescFontStyle(), false, 0);
						}
					}
				}
			}
		} catch (Exception e) {
			LH.warning(log, "Error rendering grid", e);
		}
	}

	@Override
	public void draw(AmiWebChartGraphicsWrapper gw, int start, int end) {

		try {
			for (Map<String, Object> series : this.seriesList) {
				if (end <= 0)
					break;
				int len = (Integer) series.get("size");
				if (len <= start) {
					start -= len;
					end -= len;
					continue;
				}
				int lenForLines = len;
				if (len > end) {
					len = end;
					lenForLines = end + 1;
				}
				byte lineType = LINE_TYPE_DIRECT;
				double[] xPos = (double[]) series.get("xPos");
				double[] yPos = (double[]) series.get("yPos");
				double[] x2Pos = (double[]) series.get("x2Pos");
				double[] y2Pos = (double[]) series.get("y2Pos");
				double[] lineSize = (double[]) series.get("lineSize");
				Color[] lineColor = (Color[]) series.get("lineColor");
				double[] line2Size = (double[]) series.get("line2Size");
				Color[] line2Color = (Color[]) series.get("line2Color");
				double[] fillBorderSize = (double[]) series.get("fillBorderSize");
				Color[] fillBorderColor = (Color[]) series.get("fillBorderColor");
				Color[] fillColor = (Color[]) series.get("fillColor");
				if (lineSize != null && lineColor != null && xPos != null & yPos != null && x2Pos != null & y2Pos != null) {
					double oldX = Double.NaN, oldY = Double.NaN, oldX2 = Double.NaN, oldY2 = Double.NaN;
					boolean first = true;
					for (int i = start; i < lenForLines; i++) {
						if (gw.isAborted())
							return;
						double x = (deref(xPos, i));
						double y = (deref(yPos, i));
						double x2 = (deref(x2Pos, i));
						double y2 = (deref(y2Pos, i));
						if (x != x || y != y)
							continue;
						if (!first) {

							double p1x = oldX;
							double p1y = oldY;
							double p3x = x2;
							double p3y = y2;

							Color t = deref(fillColor, i);
							if (t != null && oldX2 == oldX2 && oldY2 == oldY2) {
								gw.fillArc(centerX, centerY, oldY, oldX, y, x, y2, x2, oldY2, oldX2, t);
							}

						} else
							first = false;
						oldX = x;
						oldY = y;
						oldX2 = x2;
						oldY2 = y2;
					}
				}
				if (lineSize != null) {
					double oldX = Double.NaN, oldY = Double.NaN, oldX2 = Double.NaN, oldY2 = Double.NaN;
					boolean first = true;
					for (int i = start; i < lenForLines; i++) {
						if (gw.isAborted())
							return;
						double x = (deref(xPos, i));
						double y = (deref(yPos, i));
						double x2 = (deref(x2Pos, i));
						double y2 = (deref(y2Pos, i));
						if (x != x || y != y)
							continue;
						if (!first) {
							int t = rd(deref(lineSize, i));
							Color t2 = deref(lineColor, i);
							if (t > 0 && t2 != null)
								gw.drawArc(centerX, centerY, oldY, oldX, y, x, t, t2);

							t = rd(deref(line2Size, i));
							t2 = deref(line2Color, i);
							if (t > 0 && t2 != null && x2 == x2 && y2 == y2)
								gw.drawArc(centerX, centerY, oldY2, oldX2, y2, x2, t, t2);
						} else {
							first = false;
						}
						int t = rd(deref(fillBorderSize, i));
						Color t2 = deref(fillBorderColor, i);
						if (t > 0 && t2 != null && x2 == x2 && y2 == y2) {
							double p2x = (centerX + AmiWebChartUtils.radiansToX(y, x * AmiWebChartUtils.PI_180));
							double p2y = (centerY + AmiWebChartUtils.radiansToY(y, x * AmiWebChartUtils.PI_180));
							double p3x = (centerX + AmiWebChartUtils.radiansToX(y2, x2 * AmiWebChartUtils.PI_180));
							double p3y = (centerY + AmiWebChartUtils.radiansToY(y2, x2 * AmiWebChartUtils.PI_180));
							gw.drawLine(p2x, p2y, p3x, p3y, t, t2, LINE_TYPE_DIRECT);
						}
						oldX = x;
						oldY = y;
						oldX2 = x2;
						oldY2 = y2;
					}
				}
				AmiWebChartShape shapes[] = (AmiWebChartShape[]) series.get("shapes");
				for (int i = start; i < len; i++) {
					AmiWebChartShape shape = shapes[i];
					if (gw.isAborted())
						return;
					shape.draw(gw);
				}
				start = 0;
				end -= len;
			}
		} catch (Exception e) {
			LH.warning(log, "Error rendering", e);
		}
	}
	public void addSeries(Map<String, Object> series) {
		this.seriesList.add(series);
		itemsCount += (Integer) series.get("size");
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
	public static int rd(double d) {
		if (d != d)
			return Integer.MIN_VALUE;
		return (int) (d + .5d);
	}
	public static byte angleToAlignH(double a, boolean isFlip) {
		while (a > PI2)
			a -= PI2;
		while (a < 0)
			a += PI2;
		if (a < Math.PI / 2)
			return isFlip ? ALIGN_RIGHT : ALIGN_LEFT;
		else if (a < Math.PI * 1.5)
			return isFlip ? ALIGN_LEFT : ALIGN_RIGHT;
		else
			return isFlip ? ALIGN_RIGHT : ALIGN_LEFT;
	}
	public static byte angleToAlignV(double a, boolean isFlip) {
		while (a > PI2)
			a -= PI2;
		while (a < 0)
			a += PI2;
		if (a < Math.PI)
			return isFlip ? ALIGN_BOTTOM : ALIGN_TOP;
		else
			return isFlip ? ALIGN_TOP : ALIGN_BOTTOM;
	}

	public double getCenterX() {
		return centerX;
	}

	public void setCenterX(double centerX) {
		this.centerX = centerX;
	}

	public double getCenterY() {
		return centerY;
	}

	public void setCenterY(double centerY) {
		this.centerY = centerY;
	}

	@Override
	public int getRenderingItems() {
		return this.itemsCount;
	}

	@Override
	public void drawText(AmiWebChartGraphicsWrapper gw) {
		for (Map<String, Object> series : this.seriesList) {
			int len = (Integer) series.get("size");
			AmiWebChartShape[] shapes = (AmiWebChartShape[]) series.get("shapes");
			for (int i = 0; i < len; i++) {
				AmiWebChartShape shape = shapes[i];
				if (gw.isAborted())
					return;
				if (!shape.isValid())
					continue;
				String[] desc = (String[]) series.get("desc");
				Color[] descColor = (Color[]) series.get("descColor");
				double[] descSz = (double[]) series.get("descSz");
				String descFontFam = (String) series.get("descFontFam");
				String descFontStyle = (String) series.get("descFontStyle");
				String[] descPos = (String[]) series.get("descPos");
				String description = deref(desc, i);
				if (description != null) {
					if (shape instanceof AmiWebChartShape_Wedge) {
						AmiWebChartShape_Wedge wedge = (AmiWebChartShape_Wedge) shape;
						double __a1 = wedge.getA1();
						double __a2 = wedge.getA2();
						double __r1 = wedge.getR1();
						double __r2 = wedge.getR2();
						double size = deref(descSz, i);
						double midA = (__a1 + __a2) / 2 * PI_180;
						double ty = AmiWebChartUtils.radiansToY(__r2, midA);
						double tx = AmiWebChartUtils.radiansToX(__r2, midA);
						ty = gw.getZoom().scaleY(centerY + ty);
						tx = gw.getZoom().scaleX(centerX + tx);
						Color descriptionColor = deref(descColor, i);
						gw.drawTextIfSpaceDirect(description, (int) (tx), (int) (ty), 0, angleToAlignH(midA, false), angleToAlignV(midA, true), descriptionColor, (int) size,
								descFontFam, descFontStyle, true, 2);
					} else {
						AmiWebChartShape_XY shapexy = (AmiWebChartShape_XY) shape;
						String pos = deref(descPos, i);
						if (pos == null)
							pos = "top";
						final int _l = AmiWebChartUtils.rd(shapexy.getL(gw.getZoom()));
						final int _t = AmiWebChartUtils.rd(shapexy.getT(gw.getZoom()));
						final int _r = AmiWebChartUtils.rd(shapexy.getR(gw.getZoom()));
						final int _b = AmiWebChartUtils.rd(shapexy.getB(gw.getZoom()));
						int fontSize = rd(deref(descSz, i));
						Color descriptionColor = deref(descColor, i);
						if (pos == null || "center".equals(pos)) {
							gw.drawTextIfSpaceDirect(description, (_r + _l) / 2, (_b + _t) / 2, 0, ALIGN_MIDDLE, ALIGN_MIDDLE, descriptionColor, fontSize, descFontFam,
									descFontStyle, true, 2);
						} else if (pos.equals("top")) {
							gw.drawTextIfSpaceDirect(description, (_r + _l) / 2, _t, 0, ALIGN_MIDDLE, ALIGN_BOTTOM, descriptionColor, fontSize, descFontFam, descFontStyle, true,
									2);
						} else if (pos.equals("bottom")) {
							gw.drawTextIfSpaceDirect(description, (_r + _l) / 2, _b, 0, ALIGN_MIDDLE, ALIGN_TOP, descriptionColor, fontSize, descFontFam, descFontStyle, true, 2);
						} else if (pos.equals("left")) {
							gw.drawTextIfSpaceDirect(description, _l - 2, (_t + _b) / 2, 0, ALIGN_RIGHT, ALIGN_MIDDLE, descriptionColor, fontSize, descFontFam, descFontStyle, true,
									2);
						} else if (pos.equals("right")) {
							gw.drawTextIfSpaceDirect(description, _r + 2, (_t + _b) / 2, 0, ALIGN_LEFT, ALIGN_MIDDLE, descriptionColor, fontSize, descFontFam, descFontStyle, true,
									2);
						} else if (pos.equals("topleft")) {
							gw.drawTextIfSpaceDirect(description, _l - 2, _t, 0, ALIGN_RIGHT, ALIGN_BOTTOM, descriptionColor, fontSize, descFontFam, descFontStyle, true, 2);
						} else if (pos.equals("topright")) {
							gw.drawTextIfSpaceDirect(description, _r + 2, _t, 0, ALIGN_LEFT, ALIGN_BOTTOM, descriptionColor, fontSize, descFontFam, descFontStyle, true, 2);
						} else if (pos.equals("bottomleft")) {
							gw.drawTextIfSpaceDirect(description, _l - 2, _b, 0, ALIGN_RIGHT, ALIGN_TOP, descriptionColor, fontSize, descFontFam, descFontStyle, true, 2);
						} else if (pos.equals("bottomright")) {
							gw.drawTextIfSpaceDirect(description, _r + 2, _b, 0, ALIGN_LEFT, ALIGN_TOP, descriptionColor, fontSize, descFontFam, descFontStyle, true, 2);
						}
					}
				}
			}
		}
	}
	@Override
	public boolean hasText() {
		for (Map<String, Object> series : this.seriesList)
			if (series.get("desc") != null && series.get("descColor") != null && series.get("descSz") != null && series.get("descFontFam") != null && series.get("descPos") != null)
				return true;
		return false;
	}

}
