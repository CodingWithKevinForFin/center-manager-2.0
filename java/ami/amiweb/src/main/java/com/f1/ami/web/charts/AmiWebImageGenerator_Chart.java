package com.f1.ami.web.charts;

import static com.f1.ami.web.charts.AmiWebChartGraphicsWrapper.LINE_TYPE_DIRECT;
import static com.f1.ami.web.charts.AmiWebChartGraphicsWrapper.LINE_TYPE_HORZ;
import static com.f1.ami.web.charts.AmiWebChartGraphicsWrapper.LINE_TYPE_VERT;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.utils.LH;

public class AmiWebImageGenerator_Chart extends AmiWebImageGenerator {

	private static final int DEFAULT_FONT_SIZE = 9;
	public static final byte ALIGN_TOP = -1;
	public static final byte ALIGN_LEFT = -1;
	public static final byte ALIGN_MIDDLE = 0;
	public static final byte ALIGN_BOTTOM = 1;
	public static final byte ALIGN_RIGHT = 1;
	private static final Logger log = LH.get();

	private Color vGridColor;
	private Color hGridColor;
	private Color vMidGridColor;
	private Color hMidGridColor;
	private Color vMajorGridColor;
	private Color hMajorGridColor;
	private Color borderColor;
	private double[] vMajorGrid;
	private double[] hMajorGrid;
	private double[] vGrid;
	private double[] hGrid;

	private int vGridSize;
	private int hGridSize;
	private int vMidGridSize;
	private int hMidGridSize;
	private int vMajorGridSize;
	private int hMajorGridSize;

	private List<Map<String, Object>> seriesList = new ArrayList<Map<String, Object>>();
	private int itemsCount;
	private int opacity;

	public void setOpacity(int opacity) {
		this.opacity = opacity;
	}

	@Override
	public int getOpacity() {
		return this.opacity;
	}
	public double[] getVMajorGrid() {
		return vMajorGrid;
	}

	public void setVMajorGrid(double[] vMajorGrid) {
		this.vMajorGrid = vMajorGrid;
	}

	public double[] getHMajorGrid() {
		return hMajorGrid;
	}

	public void setHMajorGrid(double[] hMajorGrid) {
		this.hMajorGrid = hMajorGrid;
	}

	public double[] getvGrid() {
		return vGrid;
	}

	public void setVGrid(double[] vGrid) {
		this.vGrid = vGrid;
	}

	public double[] getHGrid() {
		return hGrid;
	}

	public void setHGrid(double[] hGrid) {
		this.hGrid = hGrid;
	}

	public Color getVGridColor() {
		return vGridColor;
	}

	public void setVGridColor(Color vGridColor) {
		this.vGridColor = vGridColor;
	}

	public Color getHGridColor() {
		return hGridColor;
	}

	public void setHGridColor(Color hGridColor) {
		this.hGridColor = hGridColor;
	}

	public Color getVMidGridColor() {
		return vMidGridColor;
	}

	public void setVMidGridColor(Color vMidGridColor) {
		this.vMidGridColor = vMidGridColor;
	}

	public Color getHMidGridColor() {
		return hMidGridColor;
	}

	public void setHMidGridColor(Color hMidGridColor) {
		this.hMidGridColor = hMidGridColor;
	}

	public Color getVMajorGridColor() {
		return vMajorGridColor;
	}

	public void setVMajorGridColor(Color vMajorGridColor) {
		this.vMajorGridColor = vMajorGridColor;
	}

	public Color getHMajorGridColor() {
		return hMajorGridColor;
	}

	public void setHMajorGridColor(Color hMajorGridColor) {
		this.hMajorGridColor = hMajorGridColor;
	}

	public int getVGridSize() {
		return vGridSize;
	}

	public void setVGridSize(int vGridSize) {
		this.vGridSize = vGridSize;
	}

	public int getHGridSize() {
		return hGridSize;
	}

	public void setHGridSize(int hGridSize) {
		this.hGridSize = hGridSize;
	}

	public int getVMidGridSize() {
		return vMidGridSize;
	}

	public void setVMidGridSize(int vMidGridSize) {
		this.vMidGridSize = vMidGridSize;
	}

	public int getHMidGridSize() {
		return hMidGridSize;
	}

	public void setHMidGridSize(int hMidGridSize) {
		this.hMidGridSize = hMidGridSize;
	}

	public int getVMajorGridSize() {
		return vMajorGridSize;
	}

	public void setVMajorGridSize(int vMajorGridSize) {
		this.vMajorGridSize = vMajorGridSize;
	}

	public int getHMajorGridSize() {
		return hMajorGridSize;
	}

	public void setHMajorGridSize(int hMajorGridSize) {
		this.hMajorGridSize = hMajorGridSize;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	@Override
	public boolean hasGrid() {
		if (this.vMajorGridColor != null && this.vMajorGridSize > 0 && this.vMajorGrid.length > 0)
			return true;
		if (this.vMidGridColor != null && this.vMidGridSize > 0 && this.vGrid.length > 0)
			return true;
		if (this.vGridColor != null && this.vGridSize > 0 && this.vGrid.length > 0)
			return true;
		if (this.hMajorGridColor != null && this.hMajorGridSize > 0 && this.hMajorGrid.length > 0)
			return true;
		if (this.hMidGridColor != null && this.hMajorGridSize > 0 && this.hGrid.length > 0)
			return true;
		if (this.hGridColor != null && this.hGridSize > 0 && this.hGrid.length > 0)
			return true;
		return false;
	}
	@Override
	public void drawGrid(AmiWebChartGraphicsWrapper gw) {
		try {
			int width = gw.getWidth();
			int height = gw.getHeight();

			if (this.vMajorGridColor != null && this.vMajorGridSize > 0) {
				for (int j = 0; j < this.vMajorGrid.length; j++) {
					double t = this.vMajorGrid[j];
					gw.drawLine(0, t, width, t, this.vMajorGridSize, this.vMajorGridColor, LINE_TYPE_DIRECT);
				}
			}
			if (this.vMidGridColor != null && this.vMidGridSize > 0) {
				double last = Double.NaN;
				for (int i = 0; i < this.vGrid.length; i++) {
					double val = this.vGrid[i];
					if (last == last) {
						double t = (val + last) / 2;
						gw.drawLine(0, t, width, t, this.vMidGridSize, this.vMidGridColor, LINE_TYPE_DIRECT);
					}
					last = val;
				}
			}
			if (this.vGridColor != null && this.vGridSize > 0) {
				for (int i = 0; i < this.vGrid.length; i++) {
					double t = this.vGrid[i];
					gw.drawLine(0, t, width, t, this.vGridSize, this.vGridColor, LINE_TYPE_DIRECT);
				}
			}

			if (this.hMajorGridColor != null && this.hMajorGridSize > 0) {
				for (int j = 0; j < this.hMajorGrid.length; j++) {
					double t = this.hMajorGrid[j];
					gw.drawLine(t, 0, t, height, this.hMajorGridSize, this.hMajorGridColor, LINE_TYPE_DIRECT);
				}
			}
			if (this.hMidGridColor != null && this.hMajorGridSize > 0) {
				double last = Double.NaN;
				for (int i = 0; i < this.hGrid.length; i++) {
					double val = this.hGrid[i];
					if (last == last) {
						double t = (val + last) / 2;
						gw.drawLine(t, 0, t, height, this.hMidGridSize, this.hMidGridColor, LINE_TYPE_DIRECT);
					}
					last = val;
				}
			}
			if (this.hGridColor != null && this.hGridSize > 0) {
				for (int i = 0; i < this.hGrid.length; i++) {
					double t = this.hGrid[i];
					gw.drawLine(t, 0, t, height, this.hGridSize, this.hGridColor, LINE_TYPE_DIRECT);
				}
			}
		} catch (Exception e) {
			LH.warning(log, "Error rendering grid: ", e);
			return;
		}
	}
	@Override
	public void draw(AmiWebChartGraphicsWrapper gw, int start, int end) {
		try {
			AmiWebChartLine l1 = new AmiWebChartLine();

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
				byte lineType = (Byte) series.get("lineType");
				AmiWebChartShape[] shapes = (AmiWebChartShape[]) series.get("shapes");
				double[] xPos = (double[]) series.get("xPos");
				double[] yPos = (double[]) series.get("yPos");
				double[] x2Pos = (double[]) series.get("x2Pos");
				double[] y2Pos = (double[]) series.get("y2Pos");
				double[] lineSize = (double[]) series.get("lineSize");
				Color[] lineColor = (Color[]) series.get("lineColor");
				double[] dashSize = (double[]) series.get("lineDash");
				double[] dash2Size = (double[]) series.get("line2Dash");
				double[] line2Size = (double[]) series.get("line2Size");
				Color[] line2Color = (Color[]) series.get("line2Color");
				double[] fillBorderSize = (double[]) series.get("fillBorderSize");
				Color[] fillBorderColor = (Color[]) series.get("fillBorderColor");
				Color[] fillColor = (Color[]) series.get("fillColor");

				boolean hasXY = isNumber(xPos) && isNumber(yPos);
				boolean hasXY2 = isNumber(x2Pos) && isNumber(y2Pos);
				boolean hasLine = hasXY && isPositive(lineSize);
				boolean hasLine2 = hasXY2 && isPositive(line2Size);
				boolean hasFillBorder = isPositive(fillBorderSize) && isColor(fillBorderColor) && hasXY && hasXY2;
				boolean hasFill = isColor(fillColor) && hasXY && hasXY2;
				if (hasFill) {
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
							Color t = deref(fillColor, i);
							if (t != null && oldX2 == oldX2 && oldY2 == oldY2) {
								switch (lineType) {
									case LINE_TYPE_HORZ:
										gw.fillPolygon(oldX, oldY, x, oldY, x, y, x2, y2, x2, oldY2, oldX2, oldY2, t);
										break;
									case LINE_TYPE_VERT:
										gw.fillPolygon(oldX, oldY, oldX, y, x, y, x2, y2, oldX2, y2, oldX2, oldY2, t);
										break;
									case AmiWebChartGraphicsWrapper.LINE_TYPE_HORZ_CUBIC_BEZIER: {
										gw.fillCurvedHorzCubicPolygon(oldX, oldY, x, y, x2, y2, oldX2, oldY2, t);
										break;
									}
									case AmiWebChartGraphicsWrapper.LINE_TYPE_VERT_CUBIC_BEZIER: {
										gw.fillCurvedVertCubicPolygon(oldX, oldY, x, y, x2, y2, oldX2, oldY2, t);
										break;
									}
									case AmiWebChartGraphicsWrapper.LINE_TYPE_HORZ_QUAD_BEZIER: {
										gw.fillCurvedHorzQuadPolygon(oldX, oldY, x, y, x2, y2, oldX2, oldY2, t);
										break;
									}
									case AmiWebChartGraphicsWrapper.LINE_TYPE_VERT_QUAD_BEZIER: {
										gw.fillCurvedVertQuadPolygon(oldX, oldY, x, y, x2, y2, oldX2, oldY2, t);
										break;
									}
									default:
										gw.fillPolygon(oldX, oldY, x, y, x2, y2, oldX2, oldY2, t);
										break;

								}
							}

						} else
							first = false;
						oldX = x;
						oldY = y;
						oldX2 = x2;
						oldY2 = y2;
					}
				}
				if (hasFillBorder) {
					l1.reset(lineType);
					for (int i = start; i < lenForLines; i++) {
						if (gw.isAborted())
							return;
						double x = (deref(xPos, i));
						double y = (deref(yPos, i));
						double x2 = (deref(x2Pos, i));
						double y2 = (deref(y2Pos, i));
						int t = rd(deref(fillBorderSize, i));
						Color t2 = deref(fillBorderColor, i);
						if (t > 0 && t2 != null && x == x && y == y && x2 == x2 && y2 == y2)
							gw.drawLine(x, y, x2, y2, t, t2, LINE_TYPE_DIRECT);
					}
				}
				if (hasLine) {
					l1.reset(lineType);
					for (int i = start; i < lenForLines; i++) {
						if (gw.isAborted())
							return;
						double x = (deref(xPos, i));
						double y = (deref(yPos, i));
						if (x == x && y == y)
							l1.add(x, y, rd(deref(lineSize, i)), deref(lineColor, i), rd(deref(dashSize, i)));
					}
					gw.drawPolygon(l1);
				}
				if (hasLine2) {
					l1.reset(lineType);
					for (int i = start; i < lenForLines; i++) {
						if (gw.isAborted())
							return;
						double x2 = (deref(x2Pos, i));
						double y2 = (deref(y2Pos, i));
						if (x2 == x2 && y2 == y2)
							l1.add(x2, y2, rd(deref(line2Size, i)), deref(line2Color, i), rd(deref(dash2Size, i)));
					}
					gw.drawPolygon(l1);
				}
				for (int i = start; i < len; i++) {
					if (gw.isAborted())
						return;
					AmiWebChartShape shape = shapes[i];
					if (!shape.isValid())
						continue;
					shape.draw(gw);
				}
				start = 0;
				end -= len;
			}

		} catch (Exception e) {
			LH.warning(log, "Error rendering image: ", e);
			return;
		}
	}

	private boolean isNumber(double[] array) {
		if (array == null || array.length == 0)
			return false;
		for (double d : array)
			if (d == d)
				return true;
		return false;
	}
	private boolean isPositive(double[] array) {
		if (array == null || array.length == 0)
			return false;
		for (double d : array)
			if (d > 0.0d)
				return true;
		return false;
	}
	static public boolean isColor(Color[] array) {
		if (array == null || array.length == 0)
			return false;
		for (Color d : array)
			if (d != null)
				return true;
		return false;
	}

	public void addSeries(Map<String, Object> series) {
		this.seriesList.add(series);
		itemsCount += (Integer) series.get("size");
	}

	static public double deref(double[] a, int i) {
		return a == null ? Double.NaN : a.length == 1 ? a[0] : a[i];
	}
	static public String deref(String[] a, int i) {
		return a == null ? null : a.length == 1 ? a[0] : a[i];
	}
	static public Color deref(Color[] a, int i) {
		return a == null || a.length == 0 ? null : a.length == 1 ? a[0] : a[i];
	}
	static public boolean deref(boolean[] a, int i) {
		return a == null ? false : a.length == 1 ? a[0] : a[i];
	}
	public static int rd(double d) {
		if (d != d)
			return Integer.MIN_VALUE;
		return (int) (d + .5d);
	}

	@Override
	public int getRenderingItems() {
		return itemsCount;
	}

	@Override
	public boolean hasText() {
		for (Map<String, Object> series : this.seriesList)
			if (series.get("desc") != null && series.get("descFontFam") != null)
				return true;
		return false;
	}
	@Override
	public void drawText(AmiWebChartGraphicsWrapper gw) {
		for (Map<String, Object> series : this.seriesList) {
			Color[] descColor = (Color[]) series.get("descColor");
			double[] descSz = (double[]) series.get("descSz");
			String descFontFam = (String) series.get("descFontFam");
			String descFontStyle = (String) series.get("descFontStyle");
			String[] descPos = (String[]) series.get("descPos");
			AmiWebChartShape[] shapes = (AmiWebChartShape[]) series.get("shapes");
			String[] desc = (String[]) series.get("desc");
			int len = (Integer) series.get("size");
			for (int i = 0; i < len; i++) {
				if (gw.isAborted())
					return;
				AmiWebChartShape_XY shape = (AmiWebChartShape_XY) shapes[i];
				String description = deref(desc, i);
				if (description != null) {
					final int _l, _t, _r, _b;
					AmiWebChartZoomMetrics zoom = gw.getZoom();
					if (shape.isValid()) {
						_l = AmiWebChartUtils.rd(shape.getL(zoom));
						_t = AmiWebChartUtils.rd(shape.getT(zoom));
						_r = AmiWebChartUtils.rd(shape.getR(zoom));
						_b = AmiWebChartUtils.rd(shape.getB(zoom));
					} else if (shape.hasXY()) {
						_l = _r = AmiWebChartUtils.rd(zoom.scaleX(shape.getX()));
						_t = _b = AmiWebChartUtils.rd(zoom.scaleY(shape.getY()));
					} else
						continue;
					String pos = deref(descPos, i);
					int fontSize = descSz == null ? DEFAULT_FONT_SIZE : rd(deref(descSz, i));
					Color descriptionColor = descColor == null ? shape.getFillColor() : deref(descColor, i);
					if (descriptionColor == null)
						continue;
					if (pos == null)
						pos = "top";
					if (pos == null || "center".equals(pos)) {
						gw.drawTextIfSpaceDirect(description, (_r + _l) / 2, (_b + _t) / 2, 0, ALIGN_MIDDLE, ALIGN_MIDDLE, descriptionColor, fontSize, descFontFam, descFontStyle,
								true, 2);
					} else if (pos.equals("top")) {
						gw.drawTextIfSpaceDirect(description, (_r + _l) / 2, _t, 0, ALIGN_MIDDLE, ALIGN_BOTTOM, descriptionColor, fontSize, descFontFam, descFontStyle, true, 2);
					} else if (pos.equals("bottom")) {
						gw.drawTextIfSpaceDirect(description, (_r + _l) / 2, _b, 0, ALIGN_MIDDLE, ALIGN_TOP, descriptionColor, fontSize, descFontFam, descFontStyle, true, 2);
					} else if (pos.equals("left")) {
						gw.drawTextIfSpaceDirect(description, _l - 2, (_t + _b) / 2, 0, ALIGN_RIGHT, ALIGN_MIDDLE, descriptionColor, fontSize, descFontFam, descFontStyle, true, 2);
					} else if (pos.equals("right")) {
						gw.drawTextIfSpaceDirect(description, _r + 2, (_t + _b) / 2, 0, ALIGN_LEFT, ALIGN_MIDDLE, descriptionColor, fontSize, descFontFam, descFontStyle, true, 2);
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
	public void copySeries(AmiWebImageGenerator_Chart current) {
		this.seriesList.clear();
		this.seriesList.addAll(current.seriesList);
		this.itemsCount = current.itemsCount;
	}

}
