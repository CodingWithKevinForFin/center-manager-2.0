package com.f1.suite.web.tree.impl;

import com.f1.suite.web.portal.impl.form.FormPortletMultiSelectField;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.impl.CopyPortlet;

public interface Copyable {

	String getColumnDelimiter();

	String getRowDelimiter();

	String getHeaderOptions();

	String getInlineDelimiter();

	String getInlineEnclosed();

	void setInlineDelimiter(String value);

	void setInlineEnclosed(String value);

	void setRowDelimiter(String value);

	void setColumnDelimiter(String value);

	void setHeaderOptions(String value);

	String getTitle();

	int getVisibleColumnsCount();

	String getColumnName(int i);

	String getColumnId(int i);

	int getHiddenColumnsCount();

	String getHiddenColumnName(int i);

	String getHiddenColumnId(int i);

	WebColumn getColumn(String columnId);

	void populateVisibleColumnsField(FormPortletMultiSelectField<String> visColumnsField);

	void populateHiddenColumnsField(FormPortletMultiSelectField<String> hColumnsField);

	void populateTextArea(CopyPortlet copyPortlet);

	void saveTableCopyOptions(CopyPortlet copyPortlet);

	byte[] prepareDownload(CopyPortlet copyPortlet);
}
