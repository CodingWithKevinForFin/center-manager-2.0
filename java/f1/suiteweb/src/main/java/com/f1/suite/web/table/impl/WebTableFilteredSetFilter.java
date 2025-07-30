/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table.impl;

import java.util.Set;

import com.f1.base.Row;
import com.f1.suite.web.table.WebColumn;
import com.f1.utils.LocalToolkit;
import com.f1.utils.OH;
import com.f1.utils.structs.table.RowFilter;

public class WebTableFilteredSetFilter implements RowFilter {

	final private WebColumn column;
	final private Set<?> values;

	public WebTableFilteredSetFilter(WebColumn column, Set<?> values) {
		if (column == null)
			throw new NullPointerException("column");
		if (values == null)
			throw new NullPointerException("values");
		this.column = column;
		this.values = values;
	}

	@Override
	public boolean shouldKeep(Row row, LocalToolkit tk) {
		return values.contains(column.getData(row));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != WebTableFilteredSetFilter.class)
			return false;
		WebTableFilteredSetFilter other = (WebTableFilteredSetFilter) obj;
		return OH.eq(column, other.column) && OH.eq(values, other.values);
	}

}
