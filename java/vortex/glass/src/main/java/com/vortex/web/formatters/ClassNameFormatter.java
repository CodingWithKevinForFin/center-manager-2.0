package com.vortex.web.formatters;

import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.utils.SH;

public class ClassNameFormatter extends BasicWebCellFormatter {

	@Override
	public void formatCellToHtml(Object value, StringBuilder sb, StringBuilder cellStyle) {
		String cn = SH.s(value);
		if (cn == null)
			super.formatCellToHtml(null, sb, cellStyle);
		else {
			int idx = cn.lastIndexOf('.');
			if (idx == -1) {
				super.formatCellToHtml(cn, sb, cellStyle);
			} else {
				sb.append(cn, idx + 1, cn.length());
				sb.append("<span style='color:#AAAAAA'> (");
				sb.append(cn, 0, idx);
				sb.append(")</span>");
			}
		}
	}
}
