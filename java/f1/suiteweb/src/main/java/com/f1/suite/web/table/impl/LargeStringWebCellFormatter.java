package com.f1.suite.web.table.impl;

import com.f1.base.LockedException;
import com.f1.utils.SH;

public class LargeStringWebCellFormatter extends BasicWebCellFormatter {

	private int maxLength = 100;

	@Override
	public String formatCellToHtml(Object o) {
		return SH.ddd(super.formatCellToHtml(o), maxLength);
	}
	@Override
	public void formatCellToHtml(Object o, StringBuilder sb, StringBuilder style) {
		int len = sb.length();
		super.formatCellToHtml(o, sb, style);
		if (sb.length() > len + maxLength) {
			sb.setLength(len + maxLength - 3);
			sb.append("...");
		}
	}
	public int getMaxLength() {
		return maxLength;
	}
	public LargeStringWebCellFormatter setMaxLength(int maxLength) {
		LockedException.assertNotLocked(this);
		this.maxLength = maxLength;
		return this;
	}
}
