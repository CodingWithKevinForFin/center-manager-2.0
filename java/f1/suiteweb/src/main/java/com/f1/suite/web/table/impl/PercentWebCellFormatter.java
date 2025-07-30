/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table.impl;

import com.f1.suite.web.table.WebColumn;
import com.f1.utils.Formatter;
import com.f1.utils.MH;

public class PercentWebCellFormatter extends NumberWebCellFormatter {

	protected WebColumn column;
	protected int columnLoc;
	protected Formatter numberFormatter;
	private double minValue;
	private double maxValue;

	public PercentWebCellFormatter(Formatter numberFormatter) {
		this(numberFormatter, 0, 1);
	}
	public PercentWebCellFormatter(Formatter numberFormatter, double minValue, double maxValue) {
		super(numberFormatter);
		setCssClass("cell_percent");
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	private Double getPercent(Number r) {
		if (r == null)
			return null;
		double d = r.doubleValue();
		return (d - minValue) / (maxValue - minValue);
	}

	@Override
	public void formatCellToHtml(Object data, StringBuilder sb, StringBuilder cellStyle) {
		formatCellToHtml(data, sb, cellStyle, "#AAFFAA");
	}

	public void formatCellToHtml(Object data, StringBuilder sb, StringBuilder cellStyle, String color) {
		if (color == null)
			color = "gray";
		if (data instanceof Number) {
			Double pct = getPercent((Number) data);
			if (MH.isNumber(pct)) {
				double pct2 = MH.between(pct, 0d, 1d);
				sb.append("<div style='background:").append(color).append(";width:").append(pct2 * 100).append("%;height:100%;top:0px'></div>");
				sb.append("<div style='display:flex;justify-content:center;align-items:center;width:100%;height:100%;top:0px'>");
				super.formatCellToHtml(data, sb, cellStyle);
				sb.append("</div>");
			} else {
				sb.append("N/A");
				cellStyle.append("style.color=gray|");
			}
		} else {
			sb.append("N/A");
			cellStyle.append("style.color=gray|");
		}

	}
}
