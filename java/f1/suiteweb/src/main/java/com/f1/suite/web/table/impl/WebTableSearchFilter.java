/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table.impl;

import com.f1.base.Row;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebTable;
import com.f1.utils.AH;
import com.f1.utils.LocalToolkit;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.structs.table.RowFilter;

public class WebTableSearchFilter implements RowFilter {

	private WebTable webTable;
	private WebColumn[] columns;
	private TextMatcher search;

	public WebTableSearchFilter(WebTable webTable) {
		this.webTable = webTable;
		this.search = SH.m(webTable.getSearch());
		calcColumns();
	}

	public void calcColumns() {
		this.columns = new WebColumn[webTable.getVisibleColumnsCount()];
		for (int i = 0; i < webTable.getVisibleColumnsCount(); i++) {
			WebColumn column = webTable.getVisibleColumn(i);
			columns[i] = column;

		}
	}

	@Override
	public boolean shouldKeep(Row row, LocalToolkit tk) {
		StringBuilder buf = tk.borrowStringBuilder();
		try {
			buf.append(SH.CHAR_TAB);
			for (WebColumn c : columns)
				c.getCellFormatter().formatCellForSearch(c.getData(row), buf).append(SH.CHAR_TAB);
			boolean r = search.matches(buf);
			return r;

		} finally {
			tk.returnStringBuilder(buf);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != WebTableSearchFilter.class)
			return false;
		WebTableSearchFilter other = (WebTableSearchFilter) obj;
		return webTable == other.webTable && AH.eq(columns, other.columns) && OH.eq(search, other.search);
	}

}
