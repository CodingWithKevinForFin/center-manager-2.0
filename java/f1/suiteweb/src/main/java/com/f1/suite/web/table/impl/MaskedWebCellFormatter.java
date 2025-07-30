package com.f1.suite.web.table.impl;

import com.f1.utils.casters.Caster_String;

public class MaskedWebCellFormatter extends BasicWebCellFormatter {
	public static String UNICODE_DOT = "\u2022";

	@Override
	public void formatCellToHtml(Object value, StringBuilder sb, StringBuilder cellStyle) {
		if (value instanceof String) {
			String cellVal = Caster_String.INSTANCE.cast(value);
			for (int i = 0; i < cellVal.length(); i++)
				sb.append(UNICODE_DOT);
		} else {
			sb.append("N/A");
			cellStyle.append("style.color=gray|");
		}
	}
}
