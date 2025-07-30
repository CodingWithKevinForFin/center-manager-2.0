package com.f1.ami.web;

import com.f1.suite.web.table.impl.BasicWebCellFormatter;

public class AmiWebParamsWebCellFormatter extends BasicWebCellFormatter {

	@Override
	public void formatCellToHtml(Object o, StringBuilder sb, StringBuilder cellStyle) {
		sb.append("deprecated");
	}

}
