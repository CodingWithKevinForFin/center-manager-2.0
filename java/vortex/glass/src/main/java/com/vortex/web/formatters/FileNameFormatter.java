package com.vortex.web.formatters;

import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.utils.SH;

public class FileNameFormatter extends BasicWebCellFormatter {

	@Override
	public void formatCellToHtml(Object value, StringBuilder sb, StringBuilder cellStyle) {
		String file = SH.s(value);
		if (file == null)
			super.formatCellToHtml("", sb, cellStyle);
		else {
			int idx = SH.indexOfLast(file, file.length(), SH.SLASHES);
			if (idx == -1) {
				super.formatCellToHtml(file, sb, cellStyle);
			} else {
				sb.append(file, idx + 1, file.length());
				sb.append("<span style='color:#AAAAAA'> (");
				sb.append(file, 0, idx);
				sb.append(")</span>");
			}
		}
	}
}
