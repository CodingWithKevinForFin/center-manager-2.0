/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table;

public interface WebTableListener {

	public void onColumnsArranged(WebTable fastWebTable);
	public void onColumnsSized(WebTable fastWebTable);
	public void onFilterChanging(WebTable fastWebTable);
}
