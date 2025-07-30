package com.f1.suite.web.table.impl;

import com.f1.utils.SH;

public class MemoryWebCellFormatter extends BasicWebCellFormatter {

	public MemoryWebCellFormatter() {
		super.setCssClass("cell_number");
	}

	@Override
	public void formatCellToHtml(Object o, StringBuilder sb, StringBuilder cellStyle) {
		if (o == null)
			sb.append(nullValue);
		else
			sb.append(SH.formatMemory((Long) o));
	}

}
