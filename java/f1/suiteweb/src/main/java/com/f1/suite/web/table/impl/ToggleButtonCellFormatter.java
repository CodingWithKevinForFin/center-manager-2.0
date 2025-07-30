/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table.impl;

import java.util.Comparator;

import com.f1.utils.structs.ComparableComparator;

public class ToggleButtonCellFormatter extends BasicWebCellFormatter {

	final private String trueImageText;
	final private String falseImageText;
	final private String trueAltText;
	final private String falseAltText;

	public ToggleButtonCellFormatter(String trueImage, String falseImage, String falseAltText, String trueAltText) {
		this.trueAltText = trueAltText;
		this.falseAltText = falseAltText;
		this.trueImageText = "className=" + trueImage;
		this.falseImageText = "className=" + falseImage;
		setDefaultWidth(35);
		setCssClass("cell_button");
		setDefaultClickable(true);
	}
	protected boolean isTrue(Object data) {
		Boolean r = (Boolean) data;
		return r.booleanValue();
	}

	@Override
	public StringBuilder formatCellToText(Object row, StringBuilder sb) {
		if (row == null)
			return sb;
		return sb.append(isTrue(row) ? trueAltText : falseAltText);
	}
	@Override
	public String formatCellToText(Object row) {
		if (row == null)
			return "";
		return isTrue(row) ? trueAltText : falseAltText;
	}

	@Override
	public void formatCellToHtml(Object row, StringBuilder sb, StringBuilder cellStyle) {
		if (row != null)
			cellStyle.append(isTrue(row) ? trueImageText : falseImageText);
	}

	@Override
	public StringBuilder formatCellToExcel(Object row, StringBuilder sb) {
		return formatCellToText(row, sb);
	}

	@Override
	public String formatCellToHtml(Object row) {
		if (row == null)
			return "";
		return isTrue(row) ? trueAltText : falseAltText;
	}

	@Override
	public Comparator getComparator() {
		return ComparableComparator.INSTANCE;
	}

}
