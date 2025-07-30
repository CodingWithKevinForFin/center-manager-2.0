package com.f1.suite.web.portal.impl;

import com.f1.base.Row;
import com.f1.base.Table;

public interface FastTableEditListener {

	public void onTableEditComplete(Table origTable, Table editedTable, FastTablePortlet fastTablePortlet, StringBuilder errorSink);
	public void onTableEditAbort(FastTablePortlet fastTablePortlet);
	public void onEditCell(int x, int y, String v);
	public Object getEditOptions(WebColumnEditConfig cfg, Row row);

}
