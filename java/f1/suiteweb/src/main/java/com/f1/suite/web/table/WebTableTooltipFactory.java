package com.f1.suite.web.table;

import com.f1.base.Row;

public interface WebTableTooltipFactory {
	
	public String createTooltip(WebColumn col, Row row);
	public boolean isColumnTooltipSet(WebColumn col);
}
