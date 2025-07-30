package com.vortex.web.formatters;

import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.utils.SH;

public class HostNameFormatter extends BasicWebCellFormatter {

	@Override
	public void formatCellToHtml(Object value, StringBuilder sb, StringBuilder cellStyle) {
		if (value == null)
			return;
		String host = SH.s(value);
		if (host == null) {
			super.formatCellToHtml(null, sb, cellStyle);
		} else {
			int idx = host.indexOf('.');
			if (idx == -1) {
				super.formatCellToHtml(host, sb, cellStyle);
			} else {
				sb.append(host, 0, idx);
				sb.append("<span style='color:#AAAAAA'> (");
				sb.append(host, idx + 1, host.length());
				sb.append(")</span>");
			}
		}
	}
}
