package com.f1.suite.web.table.impl;

public class IdWebCellFormatter extends BasicWebCellFormatter {

	private final String prepend;

	public IdWebCellFormatter() {
		this.prepend = "";
	}
	public IdWebCellFormatter(String prepend) {
		this.prepend = prepend;
	}

	@Override
	public StringBuilder formatCellToText(Object o, StringBuilder sb) {
		if (o != null) {
			Long val = (Long) (o instanceof String ? Long.parseLong((String) o) : o);
			if (val <= 0)
				return sb;
			sb.append(prepend).append(val);
		}
		return sb;
	}
	@Override
	public void formatCellToHtml(Object o, StringBuilder sb, StringBuilder style) {
		if (o != null) {
			Long val = (Long) (o instanceof String ? Long.parseLong((String) o) : o);
			if (val <= 0)
				return;
			sb.append(prepend).append(val);
		}
	}

}
