/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table;

import com.f1.base.Row;
import com.f1.suite.web.fastwebcolumns.FastWebColumnsListener;
import com.f1.suite.web.table.fast.FastWebTable;

public interface WebContextMenuListener extends FastWebColumnsListener {

	public void onContextMenu(WebTable table, String action);
	public void onCellClicked(WebTable table, Row row, WebColumn col);
	public void onCellMousedown(WebTable table, Row row, WebColumn col);
	public void onSelectedChanged(FastWebTable fastWebTable);
	public void onNoSelectedChanged(FastWebTable fastWebTable);
	public void onScroll(int viewTop, int viewPortHeight, long contentWidth, long contentHeight);
}
