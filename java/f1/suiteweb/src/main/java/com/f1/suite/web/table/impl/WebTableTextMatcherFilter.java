/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table.impl;

import com.f1.base.Row;
import com.f1.suite.web.table.WebCellFormatter;
import com.f1.suite.web.table.WebColumn;
import com.f1.utils.LocalToolkit;
import com.f1.utils.TextMatcher;
import com.f1.utils.structs.table.RowFilter;

public class WebTableTextMatcherFilter implements RowFilter {

	private WebColumn column;
	private WebCellFormatter formatter;
	private TextMatcher matcher;

	public WebTableTextMatcherFilter(WebColumn column, TextMatcher matcher) {
		this.column = column;
		this.formatter = column.getCellFormatter();
		this.matcher = matcher;
	}

	@Override
	public boolean shouldKeep(Row row, LocalToolkit tk) {
		if (formatter.isString()) {
			Object data = column.getData(row);
			return data != null && matcher.matches(data.toString());
		} else {
			StringBuilder sb = tk.borrowStringBuilder();
			try {
				formatter.formatCellToText(column.getData(row), sb);
				return matcher.matches(sb.toString());
			} finally {
				tk.returnStringBuilder(sb);
			}
		}
	}

}
