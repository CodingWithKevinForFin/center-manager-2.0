package com.f1.suite.web.table.impl;

import com.f1.utils.casters.Caster_Boolean;

public class CheckboxWebCellFormatter extends BasicWebCellFormatter {

	@Override
	public void formatCellToHtml(Object value, StringBuilder sb, StringBuilder cellStyle) {
		if (value instanceof Boolean) {
			boolean cellVal = Caster_Boolean.PRIMITIVE.cast(value);
			sb.append(cellVal);
		} else {
			sb.append("N/A");
			cellStyle.append("style.color=gray|");
		}
	}
}
