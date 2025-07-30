/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table.impl;

import java.util.Map;

import com.f1.suite.web.table.WebCellFormatter;

public class MapEntryWebCellFormatter extends BasicWebCellFormatter implements WebCellFormatter {

	private final Object key;

	public MapEntryWebCellFormatter(Object key) {
		this.key = key;
	}

	@Override
	public StringBuilder formatCellToText(Object o, StringBuilder sb) {
		final Map<?, ?> map = (Map<?, ?>) o;
		final Object value = map == null ? null : map.get(this.key);
		return super.formatCellToText(value, sb);
	}
	@Override
	public String formatCellToText(Object o) {
		final Map<?, ?> map = (Map<?, ?>) o;
		final Object value = map == null ? null : map.get(this.key);
		return super.formatCellToText(value);
	}

}
