package com.f1.ami.web;

import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.SH;

public class AmiWebDddFormatter extends BasicWebCellFormatter {

	final private int max;

	public AmiWebDddFormatter(int max) {
		this.max = max;
	}

	@Override
	public void formatCellToHtml(Object value, StringBuilder sb, StringBuilder cellStyle) {
		if (value instanceof CharSequence) {
			CharSequence cs = (CharSequence) value;
			if (cs.length() > max - 3) {
				WebHelper.escapeHtml(cs, 0, max - 3, true, "\\n", sb);
				sb.append("...");
			} else
				WebHelper.escapeHtml(cs, 0, cs.length(), true, "\\n", sb);
		} else if (value != null)
			formatCellToHtml(SH.s(value), sb, cellStyle);
		else
			sb.append(nullValue);
	}
	@Override
	public String formatCellToHtml(Object value) {
		if (value == null)
			return nullValue;
		StringBuilder sb = new StringBuilder();
		formatCellToHtml(value, sb, null);
		return sb.toString();
	}

}
