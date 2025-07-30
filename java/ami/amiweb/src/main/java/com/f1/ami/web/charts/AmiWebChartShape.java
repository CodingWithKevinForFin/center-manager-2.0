package com.f1.ami.web.charts;

import java.awt.Color;

import com.f1.utils.Hasher;
import com.f1.utils.OH;

public abstract class AmiWebChartShape {

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
	public static final char SHAPE_WEDGE = 'w';
	public static final char SHAPE_INVALID = 0;
	private final int layerPos;
	private final int groupNum;
	private final int rowNum;
	private char shape;
	private final int borderSize;
	private final Color borderColor;
	private final Color fillColor;
	private boolean isSelectable;

	public AmiWebChartShape(int layerPos, int groupNum, int rowNum, boolean isSelectable, char shape, int borderSize, Color borderColor, Color fillColor) {
		this.isSelectable = isSelectable;
		this.layerPos = layerPos;
		this.groupNum = groupNum;
		this.rowNum = rowNum;
		this.shape = shape;
		this.borderSize = borderSize;
		this.borderColor = borderColor;
		this.fillColor = fillColor;
	}
	public int getLayerPos() {
		return layerPos;
	}
	public int getGroupNum() {
		return groupNum;
	}
	public int getRowNum() {
		return rowNum;
	}
	public char getShape() {
		return shape;
	}
	public int getBorderSize() {
		return borderSize;
	}
	public Color getBorderColor() {
		return borderColor;
	}
	public Color getFillColor() {
		return fillColor;
	}

	public boolean isValid() {
		return this.shape != 0;
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
					else if ("hexagon".equals(shape))
						return SHAPE_HEXAGON;
					break;
				case 'v':
					if ("vbar".equals(shape))
						return SHAPE_VBAR;
					break;
				case 'w':
					if ("wedge".equals(shape))
						return SHAPE_WEDGE;
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
			case SHAPE_WEDGE:
				return "wedge";
			default:
				return null;
		}
	}
	public void draw(AmiWebChartGraphicsWrapper gw) {
		draw(gw, this.fillColor, this.borderSize, this.borderColor);
	}
	abstract public void draw(AmiWebChartGraphicsWrapper gw, Color fillColor, int borderSize, Color borderColor);

	abstract double getDistance(AmiWebChartZoomMetrics zm, int x2, int y2);

	abstract boolean intersects(AmiWebChartZoomMetrics zm, int x2, int y2, int w2, int h2);

	public static final ShapeHasher HASHER = new ShapeHasher();

	public static class ShapeHasher implements Hasher<AmiWebChartShape> {

		@Override
		public int hashcode(AmiWebChartShape o) {
			return OH.hashCode(o.groupNum, o.layerPos, o.rowNum);
		}

		@Override
		public boolean areEqual(AmiWebChartShape l, AmiWebChartShape r) {
			return l.groupNum == r.groupNum && l.layerPos == r.layerPos && l.rowNum == r.rowNum;
		}

	}

	public void setInvalid() {
		this.shape = SHAPE_INVALID;
	}
	public boolean isSelectable() {
		return this.isSelectable;
	}
}
