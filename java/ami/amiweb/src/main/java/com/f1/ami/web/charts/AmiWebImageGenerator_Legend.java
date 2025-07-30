package com.f1.ami.web.charts;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.f1.ami.web.charts.AmiWebChartSeries.Grouping;
import com.f1.utils.ColorHelper;
import com.f1.utils.OH;

public class AmiWebImageGenerator_Legend extends AmiWebImageGenerator {

	private final static int DEFAULT_BORDER_SIZE = 1;
	private final static byte ALIGN_BOTTOM = 1;
	private final static byte ALIGN_RIGHT = 1;
	private final static byte ALIGN_LEFT = -1;
	private final static byte ALIGN_TOP = -1;
	private final static byte ALIGN_MIDDLE = 0;

	private final static int ROW_HEIGHT_PX = 12;
	private final static int LEGEND_PADDING = 2;
	private final static int ROW_PADDING = 14; // 9 + 5
	private final static int FONT_SIZE = 12;
	private final static int SHAPE_SIZE = 7;
	private final static int OFFSET_WIDTH = 7;
	private final static int DASH_SIZE = 3;
	private final static int CHECK_BORDER_SIZE = 1;
	private final static int CHECKBOX_SIZE = 7;

	private double rowHeight;
	private int fontSize;
	private double legendPadding;

	private AmiWebChartRenderingLayer_Legend legend;
	private double scalingFactor;
	private double[] sFArray;
	// position of legend on chart
	private double sx, sy;
	private boolean printBg;

	public AmiWebImageGenerator_Legend(AmiWebChartRenderingLayer_Legend legend, double[] scalingFactor, int plotWidth, int plotHeight, boolean printBg) {
		this.legend = legend;
		this.sFArray = scalingFactor;
		this.scalingFactor = OH.min(scalingFactor[0], scalingFactor[1]);
		this.rowHeight = ROW_HEIGHT_PX * this.scalingFactor;
		this.legendPadding = LEGEND_PADDING * this.scalingFactor;
		this.fontSize = (int) (FONT_SIZE * this.scalingFactor);
		int[] sxy = getVisibleStartingCoordinates(plotWidth, plotHeight);
		this.sx = sxy[0];
		this.sy = sxy[1];
		this.printBg = printBg;
	}

	public AmiWebImageGenerator_Legend(AmiWebChartRenderingLayer_Legend legend, double[] scalingFactor) {
		this(legend, scalingFactor, 0, 0, false);
	}

	@Override
	public void draw(AmiWebChartGraphicsWrapper wrapper, int itemsStart, int itemsEnd) {
		double w = legend.getMaxWidth() * scalingFactor;
		double h = getHeight();

		// at least 1 px
		int borderSize = OH.max((int) (DEFAULT_BORDER_SIZE * scalingFactor), DEFAULT_BORDER_SIZE);

		Color bgColor = legend.parseColor(legend.getBackgroundColor());
		Color borderColor = legend.parseColor(legend.getBorderColor());

		wrapper.drawSquare(sx, sy, w, h, borderSize, borderColor);

		if (this.printBg)
			wrapper.fillSquare(sx + borderSize, sy + borderSize, w - borderSize, h - borderSize, bgColor);
		else
			wrapper.fillSquare(sx + borderSize, sy + borderSize, w - borderSize, h - borderSize, Color.WHITE);

		drawLegend(wrapper);
	}

	// if starting coords are outside plot, scales them down
	public int[] getVisibleStartingCoordinates(int plotWidth, int plotHeight) {

		int[] xy = legend.getXY(legend.getMaxWidth(), legend.getMaxHeight());

		// scaling offset to current plot size
		double sx = (xy[0] * 1f) / legend.getPlot().getWidth() * plotWidth;
		double sy = (xy[1] * 1f) / legend.getPlot().getHeight() * plotHeight;

		double width = legend.getMaxWidth() * scalingFactor;
		double height = getHeight();

		double hPad = legend.getHPadding() * scalingFactor;
		double vPad = legend.getVPadding() * scalingFactor;
		if (width + sx + hPad > plotWidth) {
			sx = plotWidth - width - hPad;
		}

		if (height + sy + vPad > plotHeight) {
			sy = plotHeight - height - vPad;
		}

		return new int[] { AmiWebChartUtils.rd(sx), AmiWebChartUtils.rd(sy) };
	}

	// gets height based on how many series in the legend
	private double getHeight() {
		double h = 0;
		h += legendPadding * 2;
		h += legend.getNameSize() * scalingFactor + legendPadding;
		for (int i : legend.getReferencedSeries()) {
			AmiWebChartSeries series = legend.getChart().getSeries(i);
			if (series != null) {
				h += series.getAllGroupings().getSize() * (rowHeight + legendPadding);
			}
		}
		return OH.max(h, legend.getMaxHeight() * scalingFactor);
	}

	private static final String SHAPE_KEY = "shape";
	private static final String SHAPE_COLOR_KEY = "shapeColor";
	private static final String NAME_KEY = "name";
	private static final String CHECKED_KEY = "checked";
	private static final String LINE_SIZE_KEY = "lineSize";
	private static final String DASH_KEY = "dash";

	private void drawLegend(AmiWebChartGraphicsWrapper wrapper) {
		double y = sy + legendPadding, xOffset = sx + legendPadding;

		int titleFontSize = (int) (legend.getNameSize() * scalingFactor);
		Color titleColor = ColorHelper.parseColor(legend.getNameColor());
		// following javascript logic found in ami_legendchart.js
		byte textAlign = legend.getNamePosition() == 5 ? ALIGN_LEFT : legend.getNamePosition() == 9 ? ALIGN_RIGHT : ALIGN_MIDDLE;

		wrapper.drawText(legend.getName(), xOffset, y, 0, textAlign, ALIGN_TOP, titleColor, titleFontSize, legend.getFontFamily(), "", true, 2);

		y += titleFontSize + legendPadding;

		for (int i : legend.getReferencedSeries()) {
			AmiWebChartSeries series = legend.getChart().getSeries(i);
			if (series == null)
				continue;
			for (Grouping grouping : series.getAllGroupings().values()) {
				Map<String, Object> seriesParams = new HashMap<>();
				int lineSize = OH.noNull(series.getLegendLineSize(grouping), 0);
				seriesParams.put(SHAPE_KEY, series.getLegendShape(grouping));
				seriesParams.put(SHAPE_COLOR_KEY, series.getLegendShapeColor(grouping));
				seriesParams.put(NAME_KEY, series.getLegendName(grouping));
				seriesParams.put(CHECKED_KEY, legend.checkedContainsMulti(series.getId(), grouping.getId()));
				seriesParams.put(LINE_SIZE_KEY, lineSize);
				Integer dashSize = series.getLegendLineDash(grouping);
				boolean isDash = dashSize != null && dashSize > 0;
				seriesParams.put(DASH_KEY, isDash);

				drawLegendRow(wrapper, seriesParams, xOffset, y);
				y += rowHeight + legendPadding;
			}
		}
	}

	private void drawLegendRow(AmiWebChartGraphicsWrapper wrapper, Map<String, Object> params, double x, double y) {
		// draw a single row

		double w = SHAPE_SIZE * scalingFactor;
		double h = w;

		double y_ = y + w / 2;

		Color checkboxColor = ColorHelper.parseColor(legend.getCheckboxColor());
		Color checkboxCheckColor = ColorHelper.parseColor(legend.getCheckboxCheckColor());
		Color checkboxBorderColor = ColorHelper.parseColor(legend.getCheckboxBorderColor());

		double offsetWidth = OFFSET_WIDTH * scalingFactor;

		double cw = CHECKBOX_SIZE * scalingFactor;
		double ch = cw;

		drawCheckbox(wrapper, x + offsetWidth / 2, y_, cw, ch, checkboxBorderColor, checkboxColor, checkboxCheckColor, (boolean) params.get(CHECKED_KEY));
		x += cw + offsetWidth;

		Color color = ColorHelper.parseColor((String) params.get(SHAPE_COLOR_KEY));

		int lineSize = (int) params.get(LINE_SIZE_KEY);
		boolean dashed = (boolean) params.get(DASH_KEY);

		if (lineSize > 0 || dashed)
			drawLine(wrapper, x, y + w, w + 2 * offsetWidth, color, lineSize, dashed);

		x += offsetWidth;

		// drawing the shape
		String shape = (String) params.get(SHAPE_KEY);
		if (shape != null)
			drawShape(wrapper, x, y_, w, h, color, shape);

		double padding = ROW_PADDING * scalingFactor + offsetWidth;

		String name = (String) params.get(NAME_KEY);
		wrapper.drawText(name, x + w + padding, y, 0, ALIGN_LEFT, ALIGN_TOP, color, fontSize, legend.getFontFamily(), "", true, 2);
	}

	private void drawCheckbox(AmiWebChartGraphicsWrapper wrapper, double x, double y, double w, double h, Color borderColor, Color fillColor, Color checkColor, boolean checked) {
		int checkBorderSize = AmiWebChartUtils.rd(CHECK_BORDER_SIZE * scalingFactor);
		wrapper.drawSquare(x, y, w, h, checkBorderSize, borderColor);
		wrapper.fillSquare(x + checkBorderSize, y + checkBorderSize, w - checkBorderSize, h - checkBorderSize, fillColor);

		if (checked) {
			// drawing the check mark
			double x1 = x + checkBorderSize;
			double y1 = y + checkBorderSize + h / 3;

			double x2 = x + checkBorderSize + w / 2;
			double y2 = y + h - checkBorderSize;

			double x3 = x + w - checkBorderSize;
			double y3 = y;

			wrapper.drawLine(x1, y1, x2, y2, checkBorderSize, checkColor, AmiWebChartGraphicsWrapper.LINE_TYPE_DIRECT);
			wrapper.drawLine(x2, y2, x3, y3, checkBorderSize, checkColor, AmiWebChartGraphicsWrapper.LINE_TYPE_DIRECT);
		}
	}

	private void drawLine(AmiWebChartGraphicsWrapper wrapper, double x, double y, double w, Color color, int lineSize, boolean dashed) {
		if (dashed) {
			int dashSize = (int) (DASH_SIZE * scalingFactor);
			wrapper.drawLineDashed(x, y, x + w, y, lineSize, dashSize, color, AmiWebChartGraphicsWrapper.LINE_TYPE_DIRECT);
		} else {
			wrapper.drawLine(x, y, x + w, y, lineSize, color, AmiWebChartGraphicsWrapper.LINE_TYPE_DIRECT);
		}
	}

	private void drawShape(AmiWebChartGraphicsWrapper wrapper, double x, double y, double w, double h, Color color, String shape) {
		char shapeC = AmiWebChartShape.parseShape(shape);

		switch (shapeC) {
			case AmiWebChartShape.SHAPE_CIRCLE:
				wrapper.fillOval(x, y, w, h, color);
				break;
			case AmiWebChartShape.SHAPE_HBAR:
			case AmiWebChartShape.SHAPE_VBAR:
			case AmiWebChartShape.SHAPE_SQUARE:
				wrapper.fillSquare(x, y, w, h, color);
				break;
			case AmiWebChartShape.SHAPE_TRIANGLE:
				wrapper.fillTriangle(x, y, w, h, color);
				break;
			case AmiWebChartShape.SHAPE_DIAMOND:
				wrapper.fillDiamond(x, y, w, h, color);
				break;
			case AmiWebChartShape.SHAPE_PENTAGON:
				wrapper.fillPentagon(x, y, w, h, color);
				break;
			case AmiWebChartShape.SHAPE_HEXAGON:
				wrapper.fillHexagon(x, y, w, h, color);
				break;
			case AmiWebChartShape.SHAPE_CROSS:
				wrapper.fillCross(x, y, w, h, color);
				break;
			case AmiWebChartShape.SHAPE_TICK:
				wrapper.fillTick(x, y, w, h, color);
				break;
			default:
				throw new RuntimeException("Bad shape type while drawing legend: " + shapeC);
		}
	}

	@Override
	public void drawGrid(AmiWebChartGraphicsWrapper wrapper) {
		// Auto-generated method stub
	}

	@Override
	public void drawText(AmiWebChartGraphicsWrapper wrapper) {
		// Auto-generated method stub
	}

	@Override
	public int getRenderingItems() {
		return 1;
	}

	@Override
	public boolean hasGrid() {
		return false;
	}

	@Override
	public boolean hasText() {
		return false;
	}

	@Override
	int getOpacity() {
		return 100;
	}

}
