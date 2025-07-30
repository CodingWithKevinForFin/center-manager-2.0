/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table.impl;

import java.util.Comparator;

import com.f1.utils.Formatter;
import com.f1.utils.structs.ComparableComparator;

public class NumberWebCellFormatter extends BasicWebCellFormatter {

	final private Formatter formatter;

	public NumberWebCellFormatter(Formatter formatter) {
		this.formatter = formatter;
		super.setCssClass("cell_number");
		super.setHeaderStyle("_fm=right");
		super.setDefaultWidth(80);
	}

	@Override
	public Comparator getComparator() {
		return ComparableComparator.INSTANCE;
	}

	@Override
	public StringBuilder formatCellToText(Object data, StringBuilder sb) {
		if (isNotANumber(data))
			sb.append(nullValue);
		else
			formatter.format(data, sb);
		return sb;
	}

	@Override
	public String formatCellToText(Object data) {
		if (isNotANumber(data))
			return super.formatCellToText(data);
		else
			return formatter.format(data);
	}
	@Override
	public void formatCellToHtml(Object data, StringBuilder sb, StringBuilder style) {
		if (isNotANumber(data))
			sb.append(nullValue);
		else
			formatter.format(data, sb);
	}

	@Override
	public String formatCellToHtml(Object data) {
		if (isNotANumber(data))
			return super.formatCellToText(data);
		else
			return formatter.format(data);
	}

	private boolean isNotANumber(Object data) {
		if (!(data instanceof Number))
			return true;
		else if (data instanceof Double)
			return Double.isNaN(((Number) data).doubleValue());
		else if (data instanceof Float)
			return Float.isNaN(((Number) data).floatValue());
		return false;
	}

	@Override
	public Comparable getOrdinalValue(Object row) {
		final Comparable r = super.getOrdinalValue(row);
		return isNotANumber(r) ? null : r;
	}

	public Formatter getFormatter() {
		return this.formatter;
	}
}
