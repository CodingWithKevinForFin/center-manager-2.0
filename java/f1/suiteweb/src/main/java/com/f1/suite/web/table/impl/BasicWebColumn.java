/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table.impl;

import com.f1.base.Row;
import com.f1.suite.web.table.WebCellFormatter;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class BasicWebColumn implements WebColumn {

	private int[] tableColumnLocations;
	private int singleTableColumnLocation = -1;
	private String columnName;
	final private String columnId;
	private WebCellFormatter cellFormatter;
	private boolean isClickable = false;
	private boolean isOneClick = false;
	private boolean isFixedWidth = false;
	private int width;
	private String[] tableColumnIds;
	private String cssClassName;
	private boolean isGrouping;
	private FastWebTable table;
	private String headerStyle;

	public BasicWebColumn(FastWebTable table, String columnId, String columnName, WebCellFormatter cellFormatter, String[] tableColumnIds) {
		this.table = table;
		this.tableColumnIds = tableColumnIds;
		this.columnId = columnId;
		this.width = cellFormatter.getDefaultWidth();
		this.columnName = columnName;
		this.tableColumnLocations = new int[tableColumnIds.length];
		this.cellFormatter = cellFormatter;
		this.isClickable = cellFormatter.getDefaultClickable();
		this.isOneClick = cellFormatter.getDefaultOneClick();
		this.cssClassName = cellFormatter.getDefaultColumnCssClass();
		this.headerStyle = cellFormatter.getDefaultHeaderStyle();
	}

	public BasicWebColumn(FastWebTable table, String columnId, String columnName, int width, WebCellFormatter cellFormatter, String[] tableColumnIds) {
		this.table = table;
		this.tableColumnIds = tableColumnIds;
		this.columnId = columnId;
		this.width = width;
		this.columnName = columnName;
		this.tableColumnLocations = new int[tableColumnIds.length];
		this.cellFormatter = cellFormatter;
		this.isClickable = cellFormatter.getDefaultClickable();
		this.isOneClick = cellFormatter.getDefaultOneClick();
		this.cssClassName = cellFormatter.getDefaultColumnCssClass();
		this.headerStyle = cellFormatter.getDefaultHeaderStyle();
	}

	@Override
	public FastWebTable getTable() {
		return this.table;
	}

	@Override
	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String colName) {
		this.columnName = colName;
	}

	@Override
	public String getColumnId() {
		return columnId;
	}

	@Override
	public WebCellFormatter getCellFormatter() {
		return cellFormatter;
	}

	public void setCellFormatter(WebCellFormatter cellFormatter) {
		this.cellFormatter = cellFormatter;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public BasicWebColumn setWidth(int width) {
		if (width > 0)
			this.width = width;
		return this;
	}

	public boolean getIsClickable() {
		return isClickable;
	}

	public BasicWebColumn setIsClickable(boolean isClickable) {
		this.isClickable = isClickable;
		return this;
	}

	public BasicWebColumn setIsOneClick(boolean isOneClick) {
		this.isOneClick = isOneClick;
		return this;
	}

	@Override
	public int[] getTableColumnLocations() {
		return tableColumnLocations;
	}

	@Override
	public void setTableColumnLocations(int[] columnLocations) {
		this.tableColumnLocations = columnLocations;
		this.singleTableColumnLocation = columnLocations.length == 1 ? columnLocations[0] : -1;
	}

	public BasicWebColumn setCssColumn(String cssClassName) {
		this.cssClassName = cssClassName;
		return this;
	}

	@Override
	public String[] getTableColumns() {
		return tableColumnIds;
	}

	private String jsFormatterType;
	private boolean hasHover;

	@Override
	public Object getData(Row row) {
		if (singleTableColumnLocation != -1)
			return row.getAt(singleTableColumnLocation);
		final int len = this.tableColumnLocations.length;
		if (len == 0)
			return OH.EMPTY_OBJECT_ARRAY;
		final Object[] r = new Object[len];
		for (int i = 0; i < len; i++)
			r[i] = row.getAt(this.tableColumnLocations[i]);
		return r;
	}
	@Override
	public String getColumnCssClass() {
		return this.cssClassName;
	}

	public BasicWebColumn addCssClass(String cssClass) {
		if (SH.isnt(this.cssClassName))
			this.cssClassName = cssClass;
		else
			this.cssClassName += " " + cssClass;
		return this;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + columnId + ": " + columnName + "]";
	}

	@Override
	public String getHeaderStyle() {
		return this.headerStyle;
	}

	public BasicWebColumn setHeaderStyle(String newheaderStyle) {
		this.headerStyle = newheaderStyle;
		return this;
	}

	@Override
	public String getJsFormatterType() {
		return this.jsFormatterType;
	}

	public void setJsFormatterType(String jsFormatterType) {
		this.jsFormatterType = jsFormatterType;
	}

	@Override
	public boolean getIsGrouping() {
		return isGrouping;
	}

	public void setIsGrouping(boolean isGrouping) {
		this.isGrouping = isGrouping;
	}

	@Override
	public boolean getIsOneClick() {
		// TODO Auto-generated method stub
		return this.isOneClick;
	}
	public boolean isFixedWidth() {
		return isFixedWidth;
	}

	public void setFixedWidth(boolean isFixedWidth) {
		this.isFixedWidth = isFixedWidth;
	}
	public void setHasHover(boolean hasHover) {
		this.hasHover = hasHover;
	}

	@Override
	public boolean hasHover() {
		return hasHover;
	}
}
