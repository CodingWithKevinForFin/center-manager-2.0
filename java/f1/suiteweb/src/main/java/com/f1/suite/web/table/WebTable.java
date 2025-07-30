/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.base.Getter;
import com.f1.base.Row;
import com.f1.suite.web.table.impl.WebTableFilteredInFilter;
import com.f1.utils.structs.table.RowFilter;
import com.f1.utils.structs.table.SmartTable;

public interface WebTable {

	// Identification
	public String getWebTableId();
	public void setWebTableId(String webTableId);

	// Columns
	public WebColumn addColumn(boolean visible, String title, String columnId, WebCellFormatter formatter);
	public WebColumn addColumnAt(boolean visible, String title, String columnId, WebCellFormatter formatter, int location);

	public void addVisibleColumn(WebColumn column, int columnLocation);
	public void addVisibleColumn(WebColumn column);
	public void addHiddenColumn(WebColumn column);
	public WebColumn removeColumn(String columnId);

	public void showColumn(String columnId, int columnLocation);
	public void showColumn(String columnId);
	public void hideColumn(String columnId);
	public void updateColumn(WebColumn column, String origColumnId);
	public void updateColumn(WebColumn column);

	public WebColumn getHiddenColumn(int hiddenColumnIndex);
	public WebColumn getVisibleColumn(int columnLocation);
	public WebColumn getColumn(String columnId);

	public int getVisibleColumnsCount();
	public int getHiddenColumnsCount();

	// Front end Communication
	public void setClipZone(int top, int bottom, int left, int right, int leftPin, int rightPin);
	public int getClipZoneTop();
	public int getClipZoneBottom();

	// java-script
	public String getJsTableName();

	public boolean createJs(StringBuilder js);

	// Location

	public boolean getIsVisible();
	public void setIsVisible(boolean isVisible);

	public int getPaddingTop();
	public int getPaddingBottom();
	public int getPaddingLeft();
	public int getPaddingRight();

	public void setPadding(Integer top, Integer right, Integer bottom, Integer left);

	public void setWidth(int width);
	public int getWidth();

	public void setHeight(int height);
	public int getHeight();

	// L&F
	public void setName(String name);
	public String getName();

	public void setGrayBarsSupported(boolean supportGrayBars);
	public boolean getGrayBarsSupported();

	public void setTableCssClass(String tableCssClass);
	public String getTableCssClass();

	public void setRowCssClassGetter(Getter<String, Row> rowStyleGetter);
	public Getter<String, Row> getRowCssClassGetter();

	// Pages
	public void setPageSize(int pageSize);
	public int getPageSize();

	public int getCurrentPage();
	public void setCurrentPage(int currentPage);

	public int getPageStart();
	public int getPageEnd();
	public int getPagesCount();

	public List<WebTablePage> getPageEntries();

	public boolean getHasPages();

	// Filters
	void setFilteredIn(String columnId, WebTableFilteredInFilter filter);
	public WebTableFilteredInFilter getFiltererdIn(String columnId);
	public Set<String> getFilteredInColumns();

	// Data Retrieval
	public SmartTable getTable();

	public StringBuilder getValueAsText(Row row, int column, StringBuilder sb);
	public Object getValueAsExcel(Row row, int column);
	public Object getValue(Row row, int column);
	public void getValueAsHtml(Row row, int column, StringBuilder valueSink, StringBuilder cellStyleSink);

	public void refresh();

	// Search
	public void setSearch(String expression);
	public String getSearch();

	public int getRowsCount();

	public Row getRow(int i);
	public Iterable<Row> getFilteredRows();

	public WebContextMenuFactory getMenuFactory();

	public void setMenuFactory(WebContextMenuFactory factory);

	public List<Row> getRowsByUid(Collection<Integer> rowIds);

	public void addMenuListener(WebContextMenuListener listener);
	public void removeMenuListener(WebContextMenuListener listener);
	public List<WebContextMenuListener> getMenuListeners();

	public void addWebTableListener(WebTableListener webTableListener);
	public void removeWebTableListener(WebTableListener webTableListener);

	public String getCallback();
	public Map<String, String> getCallbackParams();
	public void setCallback(String callback, Map<String, String> params);

	List<Row> getSelectedRows();
	boolean hasSelectedRows();

	public void setExternalFilter(RowFilter externalFilter);
	public RowFilter getExternalFilter();

	/**
	 * @return all columns involved in sorting (in order from highest to lowest priority). The key is the column id, value is true for ascending, false for descending
	 */
	Iterable<Entry<String, Boolean>> getSortedColumns();

	boolean isSorting();
	boolean isKeepSorting();
	void sortRows(String columnId, boolean ascendings, boolean keepSorting, boolean add);
	Set<String> getSortedColumnIds();

	public void addColumnMenuListener(WebTableColumnContextMenuListener listener);
	public void removeColumnMenuListener(WebTableColumnContextMenuListener listener);
	public WebTableColumnContextMenuFactory getColumnMenuFactory();
	public void setColumnMenuFactory(WebTableColumnContextMenuFactory m);
	public List<WebTableColumnContextMenuListener> getColumnMenuListeners();
	public int getColumnPosition(String columnId);
}
