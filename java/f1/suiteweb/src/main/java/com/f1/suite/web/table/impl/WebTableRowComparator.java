/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table.impl;

import java.util.Comparator;
import java.util.List;

import com.f1.base.Row;
import com.f1.suite.web.table.WebCellFormatter;
import com.f1.suite.web.table.WebColumn;
import com.f1.utils.AH;
import com.f1.utils.OH;

public class WebTableRowComparator implements Comparator<Row> {

	final private WebColumn[] columns;
	final private boolean[] ascendings;

	public WebTableRowComparator(List<WebColumn> columns, List<Boolean> ascendings) {
		this.columns = new WebColumn[columns.size()];
		for (int i = 0; i < this.columns.length; i++)
			this.columns[i] = columns.get(i);
		this.ascendings = AH.toArrayBoolean(ascendings);
	}

	@Override
	public int compare(Row o1, Row o2) {
		for (int i = 0; i < ascendings.length; i++) {
			WebColumn column = columns[i];
			WebCellFormatter formatter = column.getCellFormatter();
			Comparable v1 = formatter.getOrdinalValue(column.getData(o1));
			Comparable v2 = formatter.getOrdinalValue(column.getData(o2));
			if (OH.eq(v1, v2))
				continue;
			if (v1 == null)
				return 1;
			if (v2 == null)
				return -1;
			int r = formatter.getComparator().compare(v1, v2);
			if (r != 0)
				return r > 0 == ascendings[i] ? 1 : -1;
		}
		return 0;
	}
}
