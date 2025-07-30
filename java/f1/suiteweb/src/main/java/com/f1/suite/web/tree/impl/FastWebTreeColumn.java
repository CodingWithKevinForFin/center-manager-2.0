package com.f1.suite.web.tree.impl;

import com.f1.suite.web.tree.WebTreeNodeFormatter;
import com.f1.utils.OH;

public class FastWebTreeColumn implements FastWebColumn {
	public static final int DEFAULT_WIDTH = 100;
	final private Integer columnId;
	private String columnName;
	private WebTreeNodeFormatter formatter;
	private int width;
	private String columnCssClass;
	private FastWebTree tree;
	private String help;
	private String jsFormatterType;
	private boolean isSelectable = true;
	private boolean isGrouping;
	private String headerStyle;

	public FastWebTreeColumn(Integer columnId, WebTreeNodeFormatter formatter, String columnName, String help, boolean isGrouping) {
		this(columnId, formatter, columnName, DEFAULT_WIDTH, null, help, isGrouping);
	}
	public FastWebTreeColumn(Integer columnId, WebTreeNodeFormatter formatter, String columnName, int width, String columnCssClass, String help, boolean isGrouping) {
		OH.assertNotNull(formatter);
		this.columnId = columnId;
		this.formatter = formatter;
		this.columnName = columnName;
		this.width = width;
		this.columnCssClass = columnCssClass;
		this.isGrouping = isGrouping;
		this.setHelp(help);
	}

	protected void setTree(FastWebTree tree) {
		if (this.tree != null)
			throw new IllegalStateException();
		this.tree = tree;
	}

	public Integer getColumnId() {
		return columnId;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		if (OH.eq(this.columnName, columnName))
			return;
		this.columnName = columnName;
		tree.onColumnChanged(this);

	}
	public WebTreeNodeFormatter getFormatter() {
		return formatter;
	}
	public void setFormatter(WebTreeNodeFormatter formatter) {
		this.formatter = formatter;
		tree.onColumnChanged(this);
	}
	public int getWidth() {
		return width;
	}
	public FastWebTreeColumn setWidth(int width) {
		this.width = width;
		return this;
	}
	public String getColumnCssClass() {
		return columnCssClass;
	}
	public void setColumnCssClass(String columnCssClass) {
		this.columnCssClass = columnCssClass;
	}
	public String getHelp() {
		return this.help;
	}
	public void setHelp(String help) {
		this.help = help;
	}
	public String getHeaderStyle() {
		return this.headerStyle;
	}
	public void setHeaderStyle(String headerStyle) {
		this.headerStyle = headerStyle;
	}
	public void setJsFormatterType(String jsFormatterType) {
		this.jsFormatterType = jsFormatterType;
	}
	public String getJsFormatterType() {
		return this.jsFormatterType;
	}
	public boolean isSelectable() {
		return isSelectable;
	}
	public void setSelectable(boolean isSelectable) {
		this.isSelectable = isSelectable;
	}
	@Override
	public boolean getIsGrouping() {
		return this.isGrouping;
	}

}
