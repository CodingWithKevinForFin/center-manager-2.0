package com.f1.utils.structs.table;

import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.utils.LocalToolkit;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;

public class TextMatcherRowFilter implements RowFilter {

	private Column column;
	private TextMatcher matcher;

	public TextMatcherRowFilter(Column column, TextMatcher matcher) {
		this.column = column;
		this.matcher = matcher;
	}

	@Override
	public boolean shouldKeep(Row row, LocalToolkit tk) {
		try {
			final Object value = row.getAt(column.getLocation());
			return value != null && matcher.matches(SH.s(value));
		} catch (Exception e) {
			return false;
		}
	}
}
