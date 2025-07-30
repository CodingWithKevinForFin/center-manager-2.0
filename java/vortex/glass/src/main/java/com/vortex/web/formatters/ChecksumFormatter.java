package com.vortex.web.formatters;

import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.utils.MH;
import com.f1.utils.SH;

public class ChecksumFormatter extends BasicWebCellFormatter {

	@Override
	public void formatCellToHtml(Object value, StringBuilder sb, StringBuilder cellStyle) {
		if (value == null) {
			super.formatCellToHtml(null, sb, cellStyle);
		} else {
			Long checksum = (Long) value;
			if (checksum != 0L) {
				SH.toString(MH.abs(checksum), 62, sb);
				//SH.rightAlign('0', text, 11, false, sb);
			} else
				super.formatCellToHtml(null, sb, cellStyle);
		}
	}

	@Override
	public String formatCellToText(Object o) {
		if (o == null)
			return "";
		return SH.toString(MH.abs((Long) o), 62);
	}

}
