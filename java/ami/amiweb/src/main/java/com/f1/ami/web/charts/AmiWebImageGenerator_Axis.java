package com.f1.ami.web.charts;

import java.awt.Color;
import java.util.List;
import java.util.logging.Logger;

import com.f1.utils.ColorHelper;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class AmiWebImageGenerator_Axis extends AmiWebImageGenerator {

	private static final int DEFAULT_FONT_SIZE = 9;
	private static final int DEFAULT_LINE_SIZE = 1;
	private static final int DEFAULT_BORDER_SIZE = 1;
	private static final int NO_BORDER = 0;
	private static final int INTER_AXIS_BORDER_SIZE = 1;

	public static final byte ALIGN_TOP = -1;
	public static final byte ALIGN_LEFT = -1;
	public static final byte ALIGN_MIDDLE = 0;
	public static final byte ALIGN_BOTTOM = 1;
	public static final byte ALIGN_RIGHT = 1;
	private static final Logger log = LH.get();

	private static final int RIGHT_ANGLE = 90;

	private AmiWebChartAxisPortlet axis;
	private AmiWebChartAxisFormatter formatter;

	private double sx, sy;
	private double scalingFactor;
	private boolean printBg;

	public AmiWebImageGenerator_Axis(AmiWebChartAxisPortlet axis, int xOffset, int yOffset, double scalingFactor, boolean printBg) {
		this.axis = axis;
		this.formatter = axis.getAxisFormatter();
		this.scalingFactor = scalingFactor;

		this.sx = xOffset;
		this.sy = yOffset;
		this.printBg = printBg;
	}

	@Override
	public void draw(AmiWebChartGraphicsWrapper wrapper, int itemsStart, int itemsEnd) {
		// actual rect for the axis
		double w = axis.getWidth() + sx;
		double h = axis.getHeight() + sy;

		int borderSize = sx + sy > 0 ? INTER_AXIS_BORDER_SIZE : NO_BORDER;

		if (this.printBg) {
			Color fillColor = ColorHelper.parseColor(axis.getBgColor());
			wrapper.fillSquare(sx + borderSize, sy + borderSize, w - borderSize, h - borderSize, fillColor);
		}

		drawNumbers(wrapper);
		drawLabels(wrapper);
		drawBorders(wrapper, w, h, borderSize, ColorHelper.parseColor(axis.getLineColor()));

	}

	private void drawBorders(AmiWebChartGraphicsWrapper wrapper, double w, double h, int borderSize, Color borderColor) {
		// left
		if (!OH.eq(axis.getAxisOrientation(), 'R'))
			wrapper.drawLine(sx, sy, sx, sy + h, borderSize, borderColor, AmiWebChartGraphicsWrapper.LINE_TYPE_DIRECT);

		// top
		if (!OH.eq(axis.getAxisOrientation(), 'B'))
			wrapper.drawLine(sx, sy, sx + w, sy, borderSize, borderColor, AmiWebChartGraphicsWrapper.LINE_TYPE_DIRECT);

		// right
		if (!OH.eq(axis.getAxisOrientation(), 'L'))
			wrapper.drawLine(sx + w, sy, sx + w, sy + h, borderSize, borderColor, AmiWebChartGraphicsWrapper.LINE_TYPE_DIRECT);

		// bottom
		if (!OH.eq(axis.getAxisOrientation(), 'T'))
			wrapper.drawLine(sx, sy + h, sx + w, sy + h, borderSize, borderColor, AmiWebChartGraphicsWrapper.LINE_TYPE_DIRECT);

	}

	private void drawNumbers(AmiWebChartGraphicsWrapper wrapper) {
		double minValue = axis.getMinValue();
		double maxValue = axis.getMaxValue();

		double majorUnit = axis.getMajorUnit();
		double minorUnit = axis.getMinorUnit();

		if (minValue + minorUnit > maxValue)
			return;

		Color numberColor = Color.BLACK;
		if (axis.getNumberFontColor() != null) {
			numberColor = ColorHelper.parseColor(axis.getNumberFontColor());
		}
		Color lineColor = numberColor;
		if (axis.getLineColor() != null) {
			lineColor = ColorHelper.parseColor(axis.getLineColor());
		}

		int rotate = getRotation(axis.getNumberRotate());
		byte[] aligns = getAlignments(axis.getLabelRotate(), axis.getAxisOrientation());

		// more accurate minorUnit
		minorUnit = AmiWebChartUtils.calcMinorUnitSize(majorUnit);

		double gap = 0;
		double startingPoint = 0;

		// calculating the intervals for major and minor ticks
		if (axis.isVertical()) {
			gap = (double) (axis.getHeight() - axis.getGroupPadding());
			startingPoint = axis.getHeight() - axis.getGroupPadding() - (axis.getStartPadding() * scalingFactor);
		} else {
			gap = (double) (axis.getWidth() - axis.getGroupPadding());
			startingPoint = axis.getGroupPadding() + (axis.getStartPadding() * scalingFactor);
		}

		double actualGap = gap - (axis.getEndPadding() + axis.getStartPadding()) * scalingFactor;

		double minorUnitDiff = (minorUnit / (maxValue - minValue) * actualGap);
		// using minorUnit ratio to calculate diff since it's more accurate
		double majorUnitDiff = (majorUnit / minorUnit) * minorUnitDiff;

		// scaled values
		int fontSize = (int) (axis.getNumberFontSize() * scalingFactor);
		int majorTickSize = (int) (axis.getMajorUnitTickSize() * scalingFactor);
		int minorTickSize = (int) (axis.getMinorUnitTickSize() * scalingFactor);

		int endPadding = (int) (axis.getEndPadding() * scalingFactor);

		for (double i = minValue; i <= maxValue; i += majorUnit) {

			// numbers and major ticks

			if (axis.isVertical()) {
				int x1 = 0, x2 = 0, numberOffset = 0;
				if (OH.eq(axis.getAxisOrientation(), 'R')) {
					// numbering on the right side
					x1 = axis.getWidth();
					x2 = x1 - majorTickSize;
					numberOffset = -endPadding;
				} else if (OH.eq(axis.getAxisOrientation(), 'L')) {
					// numbering on the left side
					x1 = 0;
					x2 = majorTickSize;
					numberOffset = endPadding;
				}
				String num = this.formatter.format(i);
				if (num != null && startingPoint != 0 && startingPoint < gap) {
					wrapper.drawLine(sx + x1, sy + startingPoint, sx + x2, sy + startingPoint, DEFAULT_LINE_SIZE, lineColor, AmiWebChartGraphicsWrapper.LINE_TYPE_DIRECT);
					wrapper.drawText(num, sx + (x2 + numberOffset), sy + startingPoint, rotate, aligns[0], aligns[1], numberColor, fontSize, axis.getNumberFontFamily(),
							axis.getFontStyle(), true, 2);
				}
				startingPoint -= majorUnitDiff;

			} else {
				int y1 = 0, y2 = 0, numberOffset = 0;
				if (OH.eq(axis.getAxisOrientation(), 'T')) {
					// numbering on the top side
					y1 = 0;
					y2 = majorTickSize;
					numberOffset = endPadding;
				} else if (OH.eq(axis.getAxisOrientation(), 'B')) {
					// numbering on the bottom side
					y1 = axis.getHeight();
					y2 = y1 - majorTickSize;
					numberOffset = -endPadding;
				}
				String num = this.formatter.format(i);
				if (num != null && startingPoint != 0 && startingPoint < gap) {
					wrapper.drawLine(sx + startingPoint, sy + y1, sx + startingPoint, sy + y2, DEFAULT_LINE_SIZE, lineColor, AmiWebChartGraphicsWrapper.LINE_TYPE_DIRECT);
					wrapper.drawText(num, sx + startingPoint, sy + (y2 + numberOffset), rotate, aligns[0], aligns[1], numberColor, fontSize, axis.getNumberFontFamily(),
							axis.getFontStyle(), false, 2);
				}
				startingPoint += majorUnitDiff;
			}

			// minor ticks
			if (minorUnitDiff > 0) {
				double start = startingPoint + (axis.isVertical() ? majorUnitDiff : -majorUnitDiff);
				double limit = i + majorUnit;
				for (double j = i; j <= limit && j <= maxValue; j += minorUnit) {
					if (axis.isVertical()) {
						int x1 = 0, x2 = 0;
						if (OH.eq(axis.getAxisOrientation(), 'R')) {
							x1 = axis.getWidth();
							x2 = x1 - minorTickSize;
						} else if (OH.eq(axis.getAxisOrientation(), 'L')) {
							x1 = 0;
							x2 = minorTickSize;
						}
						wrapper.drawLine(sx + x1, sy + start, sx + x2, sy + start, DEFAULT_LINE_SIZE, lineColor, AmiWebChartGraphicsWrapper.LINE_TYPE_DIRECT);
						start -= minorUnitDiff;
					} else {
						int y1 = 0, y2 = 0;
						if (OH.eq(axis.getAxisOrientation(), 'T')) {
							y1 = 0;
							y2 = minorTickSize;
						} else if (OH.eq(axis.getAxisOrientation(), 'B')) {
							y1 = axis.getHeight();
							y2 = y1 - minorTickSize;
						}
						wrapper.drawLine(sx + start, sy + y1, sx + start, sy + y2, DEFAULT_LINE_SIZE, lineColor, AmiWebChartGraphicsWrapper.LINE_TYPE_DIRECT);
						start += minorUnitDiff;

					}
				}
			}
		}
	}

	private void drawLabels(AmiWebChartGraphicsWrapper wrapper) {
		double[] d = axis.getLabelTicks();
		List<String> series = axis.getSeries();

		Color labelColor = Color.BLACK;
		if (axis.getLabelFontColor() != null) {
			labelColor = ColorHelper.parseColor(axis.getLabelFontColor());
		}
		Color lineColor = labelColor;
		if (axis.getLineColor() != null) {
			lineColor = ColorHelper.parseColor(axis.getLineColor());
		}

		int rotate = getRotation(axis.getLabelRotate());
		byte[] aligns = getAlignments(axis.getLabelRotate(), axis.getAxisOrientation());

		// scaled values
		int fontSize = (int) (axis.getLabelFontSize() * scalingFactor);
		int tickSize = (int) (axis.getLabelTickSize() * scalingFactor);
		int padding = (int) (axis.getLabelPadding() * scalingFactor);

		if (series.size() > 0) {
			for (int i = 0; i < d.length; ++i) {
				if (series.get(i) == null)
					break;

				if (axis.isVertical()) {
					int y = (int) (axis.getStartPadding() * scalingFactor + d[i]);
					int x1 = 0, x2 = 0, labelOffset = 0;
					if (OH.eq(axis.getAxisOrientation(), 'R')) {
						// labels on the right side
						x1 = axis.getWidth();
						x2 = x1 - tickSize;
						labelOffset = -padding;
					} else if (OH.eq(axis.getAxisOrientation(), 'L')) {
						// labels on the left side
						x1 = 0;
						x2 = tickSize;
						labelOffset = padding;
					}

					wrapper.drawLine(sx + x1, sy + y, sx + x2, sy + y, DEFAULT_LINE_SIZE, lineColor, AmiWebChartGraphicsWrapper.LINE_TYPE_DIRECT);
					wrapper.drawText(series.get(i), sx + (x2 + labelOffset), sy + y, rotate, aligns[0], aligns[1], labelColor, fontSize, axis.getLabelFontFamily(),
							axis.getFontStyle(), true, 2);
				} else {
					int x = (int) (axis.getStartPadding() * scalingFactor + d[i]);

					int y1 = 0, y2 = 0, labelOffset = 0;
					if (OH.eq(axis.getAxisOrientation(), 'T')) {
						// labels on the top side
						y1 = 0;
						y2 = tickSize;
						labelOffset = axis.getLabelPadding();
					} else if (OH.eq(axis.getAxisOrientation(), 'B')) {
						// labels on the bottom side
						y1 = axis.getHeight();
						y2 = y1 - tickSize;
						labelOffset = -padding;
					}
					wrapper.drawLine(sx + x, sy + y1, sx + x, sy + y2, DEFAULT_LINE_SIZE, lineColor, AmiWebChartGraphicsWrapper.LINE_TYPE_DIRECT);
					wrapper.drawText(series.get(i), sx + x, sy + (y2 + labelOffset), rotate, aligns[0], aligns[1], labelColor, fontSize, axis.getLabelFontFamily(),
							axis.getFontStyle(), true, 2);
				}
			}
		}
	}

	@Override
	public void drawGrid(AmiWebChartGraphicsWrapper wrapper) {
		// Nothing to draw
	}

	@Override
	public void drawText(AmiWebChartGraphicsWrapper wrapper) {

		Color fontColor = Color.BLACK;
		if (axis.getTitleColor() != null) {
			fontColor = ColorHelper.parseColor(axis.getTitleColor());
		}

		int rotate = getRotation(axis.getTitleRotate());
		byte[] aligns = getAlignments(axis.getLabelRotate(), axis.getAxisOrientation());

		// scaled font size
		int fontSize = (int) (axis.getTitleSize() * scalingFactor);
		// half font size for padding
		double extraPadding = axis.getTitleSize() / 2f;
		double pd = axis.getTitlePadding();
		if (axis.getLabelTicks().length == 0) {
			pd += axis.getNumberPadding() + axis.getMajorUnitTickSize();
		} else {
			pd += axis.getLabelPadding() + axis.getLabelTickSize();
		}

		if (axis.isVertical()) {
			// Y axis
			//- pd + extraPadding
			double x = axis.getRequiredSpace() + extraPadding;
			double y = axis.getHeight() / 2;
			if (OH.eq(axis.getAxisOrientation(), 'R')) {
				x = axis.getWidth() / scalingFactor - x;
			} else if (OH.eq(axis.getAxisOrientation(), 'L')) {
				x += axis.getMajorUnitTickSize();
			}

			if (x * scalingFactor < axis.getWidth() && x > 0)
				wrapper.drawText(axis.getTitle(), sx + x * scalingFactor, sy + y, rotate, aligns[0], aligns[1], fontColor, fontSize, axis.getTitleFontFamily(), axis.getFontStyle(),
						false, 2);
		} else {
			// X axis
			double y = axis.getRequiredSpace() + extraPadding;
			double endTitlePosition = y + fontSize / 2;
			if (OH.eq(axis.getAxisOrientation(), 'B')) {
				y = axis.getHeight() / scalingFactor - y;
				endTitlePosition = y - fontSize / 2;
			}

			double x = axis.getWidth() / 2;

			if (y * scalingFactor < axis.getHeight() && y > 0)
				wrapper.drawText(axis.getTitle(), sx + x, sy + y * scalingFactor, rotate, aligns[0], aligns[1], fontColor, fontSize, axis.getTitleFontFamily(), axis.getFontStyle(),
						true, 2);
		}

	}

	@Override
	public int getRenderingItems() {
		return axis.getSeries().size();
	}

	@Override
	public boolean hasGrid() {
		return false;
	}

	@Override
	public boolean hasText() {
		return true;
	}

	@Override
	int getOpacity() {
		return 100;
	}

	// updates angle of rotation after considering orientation of the axis
	private int getRotation(int rotate) {
		char o = axis.getAxisOrientation();
		boolean isFlipped = OH.eq(o, 'B') || OH.eq(o, 'R');

		if (isFlipped)
			rotate += 180;
		if (!axis.isVertical())
			rotate += 90;

		rotate = rotate % 360;

		if (rotate > RIGHT_ANGLE && rotate < 270) {
			rotate = (rotate + 180) % 360;
		}

		return rotate;
	}

	// returns the alignments after considering orientation of the axis
	private byte[] getAlignments(int rotate, char orientation) {
		byte[] aligns = new byte[2];

		aligns[1] = ALIGN_MIDDLE;
		if (OH.eq(orientation, 'R')) {
			aligns[0] = ALIGN_RIGHT;
		} else {
			aligns[0] = ALIGN_LEFT;
		}

		if (rotate == RIGHT_ANGLE || rotate == -RIGHT_ANGLE) {
			byte temp = aligns[0];
			aligns[0] = aligns[1];
			aligns[1] = temp;
		}

		return aligns;
	}

}