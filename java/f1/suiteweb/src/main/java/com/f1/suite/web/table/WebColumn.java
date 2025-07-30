/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table;

import com.f1.base.Row;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.tree.impl.FastWebColumn;

public interface WebColumn extends FastWebColumn {

	public String getColumnId();

	public WebCellFormatter getCellFormatter();

	public int getWidth();

	public WebColumn setWidth(int width);

	public boolean getIsClickable();

	public boolean getIsOneClick(); // one click to trigger onCellClicked callback

	public boolean isFixedWidth(); // one click to trigger onCellClicked callback
	public String[] getTableColumns();

	public int[] getTableColumnLocations();

	void setTableColumnLocations(int[] columnLocations);

	public Object getData(Row row);

	public String getColumnCssClass();

	public String getHeaderStyle();

	//provide a mechanism for special instructions to be sent to browser
	public String getJsFormatterType();

	public FastWebTable getTable();

	public boolean hasHover();
}
