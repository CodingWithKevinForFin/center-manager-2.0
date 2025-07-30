/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table.fast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.base.Column;
import com.f1.base.Getter;
import com.f1.base.Row;
import com.f1.base.TableList;
import com.f1.base.TableListener;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.FastTablePortletListener;
import com.f1.suite.web.table.WebCellFormatter;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.WebTableColumnContextMenuFactory;
import com.f1.suite.web.table.WebTableColumnContextMenuListener;
import com.f1.suite.web.table.WebTableListener;
import com.f1.suite.web.table.WebTablePage;
import com.f1.suite.web.table.impl.BasicWebColumn;
import com.f1.suite.web.table.impl.WebTableFilteredInFilter;
import com.f1.suite.web.table.impl.WebTableRowComparator;
import com.f1.suite.web.table.impl.WebTableSearchFilter;
import com.f1.suite.web.tree.impl.FastWebColumn;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.Formatter;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.WebPoint;
import com.f1.utils.assist.RootAssister;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.impl.IdentityHasher;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.IndexedList;
import com.f1.utils.structs.table.ChainedRowFilter;
import com.f1.utils.structs.table.RowFilter;
import com.f1.utils.structs.table.SmartTable;

public class FastWebTable implements WebTable, TableListener, FastWebColumns {

	private static final Logger log = LH.get();

	public static final int CHANGED_NAME = 1 << 0;
	public static final int CHANGED_CLIPZONE = 1 << 1;
	public static final int CHANGED_GRAYBARS = 1 << 3;
	public static final int CHANGED_TABLE_CSS = 1 << 4; // not checked
	public static final int CHANGED_ROW_CSS = 1 << 5;
	public static final int CHANGED_ALL_ROWS = 1 << 6;
	public static final int CHANGED_ROWS = 1 << 7;
	public static final int CHANGED_CELLS = 1 << 8;
	public static final int CHANGED_ALL = 1 << 9;
	public static final int CHANGED_COLUMNS = 1 << 10;
	public static final int CHANGED_SELECT = 1 << 11;
	public static final int CHANGED_SCROLL = 1 << 12;
	public static final int KEEP_SORT = 2;
	public static final int ADD = 4;
	public static final int ASCEND = 1;
	public static final String JS_TABLE_NAME_PREFIX = "";
	public static final String CALLBACK_CLIPZONE = "clipzone";
	public static final String CALLBACK_PAGE = "page";
	public static final String CALLBACK_COLUMNS = "columns";
	public static final String CALLBACK_USER_SELECT = "userSelect";
	public static final String CALLBACK_USER_NAVIGATE = "userNavigate";
	public static final String CALLBACK_EXPORT = "export";
	public static final String CALLBACK_SEARCH = "search";
	public static final String CALLBACK_FILTER = "filter";
	public static final String CALLBACK_SHOWMENU = "showMenu";
	public static final String CALLBACK_SHOWHEADERMENU = "showHeaderMenu";
	public static final String CALLBACK_MENUITEM = "menuitem";
	public static final String CALLBACK_HEADERMENUITEM = "headerMenuitem";
	public static final String CALLBACK_COLUMN_WIDTH = "columnWidth";
	public static final String CALLBACK_CELL_CLICKED = "cellClicked";
	public static final String CALLBACK_ROWS_COPIED = "copyRows";
	public static final String CALLBACK_DBL_CLICK = "userDblclick";
	public static final String CALLBACK_MOVE_COLUMN = "moveColumn";
	public static final String CALLBACK_COLUMN_FILTER = "columnFilter";
	public static final String CALLBACK_GET_COLUMN_FILTER_OPTIONS = "getColumnFilterOptions";
	private static final int MENU_BAR_HEIGHT = 20;

	private int[][] columnDependencies2VisibleColumns;
	private boolean[] columnsWithDependencies;
	private boolean columnDependenciesOutdated = true;

	private String webTableId;
	private Map<String, WebColumn> columns = new HashMap<String, WebColumn>();
	private IndexedList<String, WebColumn> visibleColumns = new BasicIndexedList<String, WebColumn>();
	private IndexedList<String, WebColumn> hiddenColumns = new BasicIndexedList<String, WebColumn>();
	private int paddingTop;
	private int paddingBottom;
	private int paddingLeft;
	private int paddingRight;
	private boolean isVisible = true;
	private String name;
	private boolean GrayBarsSupported;
	private int visibleColumnsLimit = -1;

	private FastTablePortletListener fastTablePortletListeners[];
	private int changes = 0;
	private String tableCssClass;
	private Getter<String, Row> rowCssClassGetter;
	private WebContextMenuFactory contextMenuFactory;
	private SmartTable smartTable;
	private RowFilter filter;
	private String jsTableName;
	final private LinkedHashMap<String, Boolean> sortedColumnIds = new LinkedHashMap<String, Boolean>();
	private Comparator<Row> rowComparator;
	private String search;
	final private Map<String, WebTableFilteredInFilter> filteredIn = new HashMap<String, WebTableFilteredInFilter>();
	final private List<WebContextMenuListener> menuListeners = new ArrayList<WebContextMenuListener>();
	private List<WebTableColumnContextMenuListener> columnMenuListeners = new ArrayList<WebTableColumnContextMenuListener>();
	final private List<WebTableListener> webTableListeners = new ArrayList<WebTableListener>();
	private JsFunction jsFunction;
	private RowFilter externalFilter;
	private Row activeRow = null;
	private String selectedRowsText = "";
	final private List<Row> selectedRows = new ArrayList<Row>();
	final private Set<Row> selectedRowsSet = new HasherSet<Row>(IdentityHasher.INSTANCE);
	final private Formatter textFormatter;

	private String rowDelimiter = "\n";
	private String columnDelimiter = "|";
	private String inlineDelimiter = "=";
	private String inlineEnclosed = "\"";
	private String headerOptions = "hot";

	private int rowTxColorColumnPos = -1;
	private int rowBgColorColumnPos = -1;

	private WebTableColumnContextMenuFactory columnContextMenuFactory;

	public String formatText(String text) {
		return textFormatter.format(text);
	}

	public Formatter getFormatter() {
		return textFormatter;
	}

	public int getPendingChangesMask() {
		return changes;
	}

	private void changed(int mask) {
		if ((changes & mask) == mask)
			return;
		changes |= mask;
		if (this.parentPortlet != null)
			this.parentPortlet.onChange(this);
		//		fireOnChange();

		//		//TODO: this should not only happen on the first change
		//		if (MH.anyBits(mask, CHANGED_ALL_ROWS | CHANGED_ALL | CHANGED_ROWS))
		//			for (WebContextMenuListener menuListener : menuListeners)
		//				menuListener.onVisibleRowsChanged(this);
	}

	//	public void fireOnChange() {
	//		for (int i = 0; i < webTableListeners.size(); i++)
	//			webTableListeners.get(i).onChange(this);
	//	}

	public FastWebTable(SmartTable smartTable, Formatter textFormatter) {
		this.smartTable = smartTable;
		this.textFormatter = textFormatter;
		smartTable.addTableListener(this);
		changed(CHANGED_ALL);

	}

	@Override
	public String getWebTableId() {
		return webTableId;
	}

	@Override
	public void setWebTableId(String webTableId) {
		this.jsTableName = JS_TABLE_NAME_PREFIX + webTableId;
		this.jsFunction = new JsFunction(getJsTableName());
		this.webTableId = webTableId;
	}

	private void addColumn(WebColumn column) {
		int locations[] = new int[column.getTableColumns().length];
		int i = 0;
		for (String o : column.getTableColumns())
			locations[i++] = this.smartTable.getColumn(o).getLocation();
		column.setTableColumnLocations(locations);
		CH.putOrThrow(columns, column.getColumnId(), column);
		updateFilter(false);//TODO: this is required because there may be a sort / filter on it.  We really should have an updateColumn(...) method 

		updateSort();

	}

	public void updateSort() {
		if (this.sortedColumnIds.isEmpty())
			return;
		//		if (this.rowComparator == null) {
		List<WebColumn> columns = new ArrayList<WebColumn>();
		List<Boolean> ascendings = new ArrayList<Boolean>();
		for (Map.Entry<String, Boolean> e : sortedColumnIds.entrySet()) {
			columns.add(this.columns.get(e.getKey()));
			ascendings.add(e.getValue());
		}
		rowComparator = new WebTableRowComparator(columns, ascendings);
		//		} 

		//For some reason if you don't create a new comparator, when rebuilding the sort doesn't work correctly

		smartTable.sortRows(rowComparator, getTable().getKeepSorting());
		changed(CHANGED_ALL_ROWS);
	}

	private void updateColumnLocations() {
		for (WebColumn column : this.columns.values()) {
			int locations[] = new int[column.getTableColumns().length];
			int i = 0;
			for (String o : column.getTableColumns())
				locations[i++] = this.smartTable.getColumn(o).getLocation();
			column.setTableColumnLocations(locations);
		}
		this.rowTxColorColumnPos = getColumnLocation(TXCOL);
		this.rowBgColorColumnPos = getColumnLocation(BGCOL);
	}
	private int getColumnLocation(String colId) {
		Column col = this.smartTable.getColumnsMap().get(colId);
		return col == null ? -1 : col.getLocation();
	}

	@Override
	public void addVisibleColumn(WebColumn column) {
		addColumn(column);
		visibleColumns.add(column.getColumnId(), column);
		columnDependenciesOutdated = true;
		changed(CHANGED_COLUMNS);
	}

	@Override
	public void addVisibleColumn(WebColumn column, int columnLocation) {
		addColumn(column);
		visibleColumns.add(column.getColumnId(), column, columnLocation);
		columnDependenciesOutdated = true;
		changed(CHANGED_COLUMNS);
	}

	@Override
	public void addHiddenColumn(WebColumn column) {
		addColumn(column);
		hiddenColumns.add(column.getColumnId(), column);
		changed(CHANGED_COLUMNS);
	}
	@Override
	public void updateColumn(WebColumn column) {
		String columnId = column.getColumnId();
		updateColumn(column, columnId);
	}
	@Override
	public void updateColumn(WebColumn column, String oldColumnId) {
		String columnId = column.getColumnId();
		if (visibleColumns.containsKey(oldColumnId)) {
			visibleColumns.update(oldColumnId, columnId, column);
			columnDependenciesOutdated = true;
			changed(CHANGED_COLUMNS);
		} else
			hiddenColumns.update(oldColumnId, columnId, column);
		CH.removeOrThrow(columns, oldColumnId);
		addColumn(column);
		final WebTableFilteredInFilter f = this.getFiltererdIn(oldColumnId);
		if (f != null)
			f.setColumn(column);
	}

	@Override
	public WebColumn removeColumn(String columnId) {
		int colPos = getColumnPosition(columnId);
		if (this.filteredIn.containsKey(columnId))
			setFilteredIn(columnId, (Set) null);
		WebColumn r = CH.removeOrThrow(columns, columnId);
		if (visibleColumns.containsKey(columnId)) {
			visibleColumns.remove(columnId);
			columnDependenciesOutdated = true;
			if (isColumnPositionPinned(colPos))
				setPinnedColumnsCount(this.pinnedColumnsCount - 1);
			changed(CHANGED_COLUMNS);
		} else
			hiddenColumns.remove(columnId);
		return r;
	}

	private void ensureColumnClipIsValid() {
		int lastCol = this.getVisibleColumnsCount() - 1;
		this.clipZoneRight = Math.min(this.clipZoneRight, lastCol);
		this.clipZoneLeft = Math.min(this.clipZoneLeft, lastCol);
		this.clipZoneRightPin = Math.min(this.clipZoneRightPin, lastCol);
		this.clipZoneLeftPin = Math.min(this.clipZoneLeftPin, lastCol);
		this.pendingClipZoneRight = Math.min(this.pendingClipZoneRight, lastCol);
		this.pendingClipZoneLeft = Math.min(this.pendingClipZoneLeft, lastCol);
		this.pendingClipZoneRightPin = Math.min(this.pendingClipZoneRightPin, lastCol);
		this.pendingClipZoneLeftPin = Math.min(this.pendingClipZoneLeftPin, lastCol);
	}

	@Override
	public void showColumn(String columnId, int columnLocation) {
		// remove from hidden
		WebColumn column = hiddenColumns.removeNoThrow(columnId);
		if (column == null)
			// remove from visible
			column = visibleColumns.removeNoThrow(columnId);
		if (column == null)
			throw new NoSuchElementException("column not found: " + columnId);
		if (isColumnPositionPinned(getColumnPosition(columnId)))
			setPinnedColumnsCount(this.pinnedColumnsCount - 1);
		// add it back to visible and set it at the specified position
		visibleColumns.add(column.getColumnId(), column, columnLocation);
		if (columnLocation < this.pinnedColumnsCount)
			setPinnedColumnsCount(this.pinnedColumnsCount + 1);
		columnDependenciesOutdated = true;
		changed(CHANGED_COLUMNS);
	}

	@Override
	public void showColumn(String columnId) {
		WebColumn column = hiddenColumns.removeNoThrow(columnId);
		if (column == null) {
			column = visibleColumns.get(columnId);
			if (column == null)
				throw new NoSuchElementException("column not found: " + columnId);
			else
				return;//already shown
		}

		visibleColumns.add(column.getColumnId(), column);
		columnDependenciesOutdated = true;
		changed(CHANGED_COLUMNS);
	}

	@Override
	public void hideColumn(String columnId) {
		// check if this column is part of the pinned columns
		// true == it is part of the pinned column, false otherwise
		boolean needsToHide = isColumnPositionPinned(getColumnPosition(columnId));
		// remove a column from visibility then return it
		WebColumn column = visibleColumns.removeNoThrow(columnId);
		if (column == null) {
			column = hiddenColumns.get(columnId);
			if (column == null)
				throw new NoSuchElementException("column not found: " + columnId);
			else
				return;//already hidden
		}
		// count it as hidden
		hiddenColumns.add(column.getColumnId(), column);
		columnDependenciesOutdated = true;
		// 
		if (needsToHide)
			setPinnedColumnsCount(this.pinnedColumnsCount - 1);
		if (this.pinnedColumnsCount == this.getVisibleColumnsCount()) {
			// this means remove pinning
			setPinnedColumnsCount(0);
		}

		changed(CHANGED_COLUMNS);
	}

	@Override
	public WebColumn getHiddenColumn(int columnLocation) {
		return hiddenColumns.getAt(columnLocation);
	}

	@Override
	public WebColumn getVisibleColumn(int columnLocation) {
		if (columnLocation == -1)
			return null;
		return visibleColumns.getAt(columnLocation);
	}

	@Override
	public int getVisibleColumnsCount() {
		return visibleColumns.getSize();
	}

	@Override
	public int getHiddenColumnsCount() {
		return hiddenColumns.getSize();
	}

	private int[] getColumnDependencies(int column) {
		ensureDependenciesUpToDate();
		return this.columnDependencies2VisibleColumns[column];

	}
	private boolean[] getColumnsWithDependencies() {
		ensureDependenciesUpToDate();
		return this.columnsWithDependencies;
	}

	private void ensureDependenciesUpToDate() {
		if (columnDependenciesOutdated) {
			int columnsCount = smartTable.getColumnsCount();
			if (AH.length(this.columnDependencies2VisibleColumns) != columnsCount) {
				this.columnDependencies2VisibleColumns = new int[columnsCount][];
				this.columnsWithDependencies = new boolean[columnsCount];
			} else
				AH.fill(this.columnsWithDependencies, false);
			AH.fill(this.columnDependencies2VisibleColumns, OH.EMPTY_INT_ARRAY);
			for (int i = 0, l = visibleColumns.getSize(); i < l; i++) {
				for (int col : visibleColumns.getAt(i).getTableColumnLocations()) {
					Column column = smartTable.getColumnAt(col);
					int pos = column.getLocation();
					if (isColumnVisible(i))
						this.columnsWithDependencies[pos] = true;
					if (AH.indexOf(i, this.columnDependencies2VisibleColumns) == -1)
						this.columnDependencies2VisibleColumns[pos] = AH.append(this.columnDependencies2VisibleColumns[pos], i);
				}
			}
			if (this.rowBgColorColumnPos != -1)
				this.columnsWithDependencies[this.rowBgColorColumnPos] = true;
			if (this.rowTxColorColumnPos != -1)
				this.columnsWithDependencies[this.rowTxColorColumnPos] = true;
			this.columnDependenciesOutdated = false;
		}
	}

	//this is what the front end has in it's cache
	private int clipZoneTop = -1;
	private int clipZoneBottom = -1;
	private int clipZoneLeft = -1;
	private int clipZoneRight = -1;
	private int clipZoneLeftPin = -1;
	private int clipZoneRightPin = -1;

	//This is what the front end is viewing
	private int pendingClipZoneTop = -1;
	private int pendingClipZoneBottom = -1;
	private int pendingClipZoneLeft = -1;
	private int pendingClipZoneRight = -1;
	private int pendingClipZoneLeftPin = -1;
	private int pendingClipZoneRightPin = -1;

	@Override
	public void setClipZone(int top, int bottom, int left, int right, int leftPin, int rightPin) {
		if (left != -1 && right != -1) {
			if (left < 0)
				left = 0;
			if (right >= getVisibleColumnsCount())
				right = getVisibleColumnsCount() - 1;
		}
		if (leftPin != -1 && rightPin != -1) {
			if (leftPin < 0)
				leftPin = 0;
			if (rightPin >= getVisibleColumnsCount())
				rightPin = getVisibleColumnsCount() - 1;
			if (leftPin > rightPin)
				throw new IllegalArgumentException("leftPin > rightPin: " + leftPin + ", " + rightPin);
		}
		if (top == -1 || bottom == -1) {
			top = -1;
			bottom = -1;
		}
		this.pendingClipZoneTop = top;
		this.pendingClipZoneBottom = bottom;
		this.pendingClipZoneLeft = left;
		this.pendingClipZoneRight = right;
		this.pendingClipZoneLeftPin = leftPin;
		this.pendingClipZoneRightPin = rightPin;

		//This 'squeezes' the clipzone based on the pending clipzone we receive from the browser because the moment the visible zone is changed in the browser,
		//we need to consider any region outside of it as stale. For example lets say we get  2-5 followed by 3-6 followed by 1-4... The only nonstale clipzone is now 3-4
		clipZoneLeft = Math.max(clipZoneLeft, pendingClipZoneLeft);
		clipZoneRight = Math.min(clipZoneRight, pendingClipZoneRight);
		clipZoneTop = Math.max(clipZoneTop, pendingClipZoneTop);
		clipZoneBottom = Math.min(clipZoneBottom, pendingClipZoneBottom);

		if (this.clipZoneTop != -1 && this.clipZoneBottom != -1) {
			this.clipZoneTop = Math.max(this.clipZoneTop, top);
			this.clipZoneBottom = Math.min(this.clipZoneBottom, bottom);
		}

		this.smartTable.setListenForCellsRowRange(-1, -1);
		changed(CHANGED_CLIPZONE);
		if (this.clipZoneLeft != this.pendingClipZoneLeft || this.clipZoneRight != this.pendingClipZoneRight || this.clipZoneLeftPin != this.pendingClipZoneLeftPin
				|| this.clipZoneRightPin != this.pendingClipZoneRightPin)
			columnDependenciesOutdated = true;
	}

	@Override
	public int getClipZoneTop() {
		return this.clipZoneTop;
	}

	@Override
	public int getClipZoneBottom() {
		return this.clipZoneBottom;
	}

	@Override
	public String getJsTableName() {
		return jsTableName;
	}

	@Override
	public boolean getIsVisible() {
		return isVisible;
	}

	@Override
	public void setIsVisible(boolean isVisible) {
		if (this.isVisible == isVisible)
			return;
		if (!isVisible) {
			this.userSelectSeqnum = 0;
			this.userScrollSeqnum = 0;
			this.clipZoneBottom = -1;
			this.clipZoneTop = -1;
			this.clipZoneLeft = -1;
			this.clipZoneRight = -1;
			this.clipZoneLeftPin = -1;
			this.clipZoneRightPin = -1;
			this.columnDependenciesOutdated = true;
		}
		this.isVisible = isVisible;
		changed(CHANGED_ALL);
	}

	@Override
	public int getPaddingTop() {
		return paddingTop;
	}

	@Override
	public int getPaddingBottom() {
		return paddingBottom;
	}

	@Override
	public int getPaddingLeft() {
		return paddingLeft;
	}

	@Override
	public int getPaddingRight() {
		return paddingRight;
	}

	@Override
	public void setPadding(Integer top, Integer right, Integer bottom, Integer left) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setWidth(int width) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getWidth() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setHeight(int height) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getHeight() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setName(String name) {
		if (OH.eq(name, this.name))
			return;
		this.name = name;
		changed(CHANGED_NAME);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setGrayBarsSupported(boolean grayBarsSupported) {
		if (this.GrayBarsSupported == grayBarsSupported)
			return;
		this.GrayBarsSupported = grayBarsSupported;
		changed(CHANGED_GRAYBARS);
	}

	@Override
	public boolean getGrayBarsSupported() {
		return GrayBarsSupported;
	}

	@Override
	public void setTableCssClass(String tableCssClass) {
		if (OH.eq(tableCssClass, tableCssClass))
			return;
		this.tableCssClass = tableCssClass;
		changed(CHANGED_TABLE_CSS);
	}

	@Override
	public String getTableCssClass() {
		return tableCssClass;
	}

	@Override
	public void setRowCssClassGetter(Getter<String, Row> rowStyleGetter) {
		if (this.rowCssClassGetter == rowStyleGetter)
			return;
		this.rowCssClassGetter = rowStyleGetter;
		changed(CHANGED_ROW_CSS);
	}

	@Override
	public Getter<String, Row> getRowCssClassGetter() {
		return rowCssClassGetter;
	}

	@Override
	public void setPageSize(int pageSize) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getPageSize() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getCurrentPage() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCurrentPage(int currentPage) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getPageStart() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getPageEnd() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getPagesCount() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<WebTablePage> getPageEntries() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getHasPages() {
		return false;
	}

	@Override
	public SmartTable getTable() {
		return smartTable;
	}

	@Override
	public StringBuilder getValueAsText(Row row, int column, StringBuilder sb) {
		WebColumn webColumn = getVisibleColumn(column);
		webColumn.getCellFormatter().formatCellToText(webColumn.getData(row), sb);
		return sb;
	}

	@Override
	public Object getValueAsExcel(Row row, int column) {
		return null;
	}

	@Override
	public Object getValue(Row row, int column) {
		return row.getAt(column);
	}

	@Override
	public void getValueAsHtml(Row row, int column, StringBuilder sb, StringBuilder cellStyleSink) {
		WebColumn webColumn = getVisibleColumn(column);
		webColumn.getCellFormatter().formatCellToHtml(webColumn.getData(row), sb, cellStyleSink);
	}

	@Override
	public boolean createJs(StringBuilder js) {
		int len = js.length();
		if (this.changes != 0) {
			fireChangesToListeners(changes);
		}
		if (isVisible) {
			byte status = smartTable.ensureUpToDateAndGetStatus();
			boolean init = false;
			if (MH.anyBits(this.changes, CHANGED_ALL | CHANGED_COLUMNS) || MH.anyBits(status, SmartTable.STATUS_SCHEMA_CHANGED)) {
				ensureColumnClipIsValid();
				createJsClearData(js);
				createJsTableSize(js);
				createJsTableScroll(js);
				createJsInitColumns(js);
				createJsSetTableData(js, true, true, status);
				handleFixedColumns(js);
				init = true;
			} else if (MH.anyBits(status, SmartTable.STATUS_ROWS_CHANGED)) {
				if (this.clipZoneBottom == -1 || smartTable.getMinRowChanged() <= this.pendingClipZoneBottom) {
					createJsClearData(js);
					createJsTableSize(js);
					createJsTableScroll(js);
					createJsSetTableData(js, true, false, status);
				} else {
					createJsTableSize(js);
					createJsTableScroll(js);
					processClipzoneAndCellChanges(js, status);
				}
			} else if (MH.anyBits(changes, CHANGED_SELECT)) {
				createJsSetTableData(js, true, false, status);
			} else {
				if (MH.anyBits(changes, CHANGED_SCROLL))
					createJsTableScroll(js);
				processClipzoneAndCellChanges(js, status);
			}
			if (init == false && MH.anyBits(status, SmartTable.STATUS_SORT_CHANGED | SmartTable.STATUS_FILTER_CHANGED)) {
				createJsSortColumns(js, MH.anyBits(status, SmartTable.STATUS_SORT_CHANGED), MH.anyBits(status, SmartTable.STATUS_FILTER_CHANGED));
			}
			smartTable.resetStatus();
			smartTable.setListenForCellsRowRange(this.clipZoneTop, this.clipZoneBottom);
			smartTable.setListenForCellsColumns(this.getColumnsWithDependencies());
			if (snapToRowNumber >= 0) {
				jsFunction.reset(js, "ensureRowVisibleWithAlign").addParam(snapToRowNumber).addParam(snapToRowTopAlign).end();
				snapToRowNumber = -1;
			}
			if (moveToRow >= 0) {
				if (moveToRow >= getRowsCount())
					moveToRow = getRowsCount() - 1;
				if (moveToRow >= 0) {
					jsFunction.reset(js, "ensureRowVisible").addParam(moveToRow).end();
				}
			}
			moveToRow = -1;
		}
		changes = 0;
		return js.length() > len;
	}

	private void handleFixedColumns(StringBuilder js) {
		jsFunction.reset(js, "fixColumns");
		List<Map<Object, Object>> columns = new ArrayList<Map<Object, Object>>();
		for (int i = 0; i < this.visibleColumns.getSize(); i++) {
			WebColumn c = visibleColumns.getAt(i);
			columns.add(CH.m("id", c.getColumnId(), "fix", c.isFixedWidth()));
		}
		jsFunction.addParam(RootAssister.INSTANCE.toJson(columns)).end();
	}

	private void processClipzoneAndCellChanges(StringBuilder js, byte status) {
		if (MH.anyBits(changes, CHANGED_CLIPZONE)) {
			this.createJsSetTableData(js, false, false, status);
			fireOnScroll();
		}
		if (MH.anyBits(status, SmartTable.STATUS_CELLS_CHANGED) && pendingClipZoneTop != -1 && pendingClipZoneBottom != -1) {
			int[] changedCellRowNums = smartTable.getCellsChanged(pendingClipZoneTop, pendingClipZoneBottom);
			TableList rows = this.getTable().getRows();
			for (int rowNum : changedCellRowNums) {
				if (rowNum == -1)//end of list
					break;
				Row row = rows.get(rowNum);
				BitSet bitset = smartTable.getCellsChangedAtRow(rowNum);
				createJsSetData_Start(js, row);
				for (int col = bitset.nextSetBit(0); col >= 0; col = bitset.nextSetBit(col + 1)) {
					if (col == this.rowBgColorColumnPos)
						createJsSetData_AppendRowBgColor(js, row);
					if (col == this.rowTxColorColumnPos)
						createJsSetData_AppendRowTxColor(js, row);
					int[] cols = getColumnDependencies(col);
					for (int visibleCol : cols) {
						if (isColumnVisible(visibleCol))
							createJsSetData_AppendCell(js, row, visibleCol);
					}
				}
				createJsSetData_End(js, row);
			}
		}
	}

	private void fireChangesToListeners(int change) {
		if (this.fastTablePortletListeners == null)
			return;
		for (FastTablePortletListener i : this.fastTablePortletListeners)
			i.onFastTablePortletChanged(change);
	}

	public void addListener(FastTablePortletListener listener) {
		if (this.fastTablePortletListeners == null)
			this.fastTablePortletListeners = new FastTablePortletListener[] { listener };
		else
			AH.append(this.fastTablePortletListeners, listener);
	}

	public void removeListener(FastTablePortletListener listener) {
		AH.remove(this.fastTablePortletListeners, AH.indexOf(listener, this.fastTablePortletListeners));
		if (this.fastTablePortletListeners.length == 0)
			this.fastTablePortletListeners = null;
	}

	private boolean isColumnVisible(int col) {
		if (this.pendingClipZoneLeft != -1 && OH.isBetween(col, this.pendingClipZoneLeft, this.pendingClipZoneRight))
			return true;
		if (this.pendingClipZoneRightPin != -1 && OH.isBetween(col, this.pendingClipZoneLeftPin, this.pendingClipZoneRightPin))
			return true;
		return false;
	}
	private void updateSelects() {
		if (SH.isnt(selectedRowsText) && activeRow == null) {
			if (selectedRows.size() == 0)
				for (WebContextMenuListener menuListener : menuListeners)
					menuListener.onNoSelectedChanged(this);
			return;
		}
		if (activeRow != null && activeRow.getLocation() == -1)
			activeRow = null;
		if (selectedRows.size() > 0) {
			int newLocations[] = new int[selectedRows.size()];
			int i = 0;
			for (int j = 0; j < selectedRows.size(); j++) {
				Row row = selectedRows.get(j);
				int loc = row.getLocation();
				if (loc != -1)
					newLocations[i++] = loc;
				else
					selectedRows.set(j, null);
			}
			if (i != newLocations.length) {//selected elements were removed
				this.needsFireSelectionChanged = true;
				newLocations = Arrays.copyOf(newLocations, i);
				ArrayList<Row> t = new ArrayList<Row>(selectedRows);
				selectedRows.clear();
				selectedRowsSet.clear();
				for (Row r : t)
					if (r != null)
						selectedRows.add(r);
			}
			if (newLocations.length == 0) {
				selectedRowsText = "";
			} else {
				StringBuilder sb = new StringBuilder();
				Arrays.sort(newLocations);
				int start = newLocations[0];
				int end = start;
				for (i = 1; i < newLocations.length; i++) {
					int t = newLocations[i];
					if (t == end + 1) {
						end++;
					} else {
						if (sb.length() > 0)
							sb.append(',');
						sb.append(start);
						if (start != end)
							sb.append('-').append(end);

						start = end = t;
					}
				}
				if (sb.length() > 0)
					sb.append(',');
				sb.append(start);
				if (start != end)
					sb.append('-').append(end);
				selectedRowsText = sb.toString();
			}
		}
	}

	private boolean needsFireSelectionChanged = false;

	public void fireOnSelectedChanged() {
		if (needsFireSelectionChanged) {
			needsFireSelectionChanged = false;
			for (WebContextMenuListener menuListener : menuListeners)
				menuListener.onSelectedChanged(this);
		}
	}
	private void createJsInitSelection(StringBuilder js) {
		jsFunction.reset(js, "setActiveRow");
		jsFunction.addParam(activeRow == null ? -1 : activeRow.getLocation());
		jsFunction.addParam(userSelectSeqnum);
		jsFunction.end();
		jsFunction.reset(js, "setSelectedRows");
		jsFunction.addParamQuoted(selectedRowsText);
		jsFunction.addParam(userSelectSeqnum);
		jsFunction.end();
	}
	public void fireOnScroll() {
		for (WebContextMenuListener menuListener : menuListeners)
			menuListener.onScroll(this.getTableTop(), this.getTableHeight(), this.getContentWidth(), this.getContentHeight());
	}
	private void createJsInitColumns(StringBuilder js) {
		jsFunction.reset(js, "initColumns");
		jsFunction.addParam(this.getPinnedColumnsCount());
		List<Map<Object, Object>> columns = new ArrayList<Map<Object, Object>>();
		boolean isFiltered;
		for (int i = 0, l = visibleColumns.getSize(); i < l; i++) {
			WebColumn column = visibleColumns.getAt(i);
			Boolean ascending = CH.getOr(sortedColumnIds, column.getColumnId(), null);
			String sort = "";
			if (ascending != null)
				sort = String.valueOf((ascending ? FastWebTable.ASCEND : 0) + (smartTable.getKeepSorting() ? FastWebTable.KEEP_SORT : 0));
			WebTableFilteredInFilter f = filteredIn.get(column.getColumnId());
			isFiltered = f != null;
			String filterText = null;
			if (f != null && f.isSimple())
				filterText = f.getSimpleValue();
			columns.add(CH.m("name", textFormatter.format(column.getColumnName()), "cssClass", column.getColumnCssClass(), "sort", sort, "id", column.getColumnId(), "visible",
					true, "filter", isFiltered, "width", column.getWidth(), "clickable", column.getIsClickable(), "headerStyle", column.getHeaderStyle(), "jsFormatterType",
					column.getJsFormatterType(), "filterText", filterText, "oneClick", column.getIsOneClick(), "hids", this.htmlIdSelectorForColumns.get(column.getColumnId()),
					"fix", column.isFixedWidth(), "hasHover", column.hasHover()));
		}
		for (int i = 0, l = hiddenColumns.getSize(); i < l; i++) {
			WebColumn column = hiddenColumns.getAt(i);
			Boolean ascending = CH.getOr(sortedColumnIds, column.getColumnId(), null);
			String sort = "";
			if (ascending != null)
				sort = String.valueOf((ascending ? FastWebTable.ASCEND : 0) + (smartTable.getKeepSorting() ? FastWebTable.KEEP_SORT : 0));
			isFiltered = filteredIn.containsKey(column.getColumnId());
			columns.add(CH.m("name", textFormatter.format(column.getColumnName()), "cssClass", column.getColumnCssClass(), "sort", sort, "id", column.getColumnId(), "visible",
					false, "filter", isFiltered, "width", column.getWidth(), "clickable", column.getIsClickable(), "headerStyle", column.getHeaderStyle(), "jsFormatterType",
					column.getJsFormatterType(), "oneClick", column.getIsOneClick(), "hids", this.htmlIdSelectorForColumns.get(column.getColumnId()), "fixedWidth",
					column.isFixedWidth()));
		}
		jsFunction.addParam(RootAssister.INSTANCE.toJson(columns));
		jsFunction.end();
	}
	private void createJsSortColumns(StringBuilder js, boolean sortChanged, boolean filterChanged) {
		jsFunction.reset(js, "updateSortAndFilter");
		List<Map<Object, Object>> columns = new ArrayList<Map<Object, Object>>();
		boolean isFiltered;
		// loop once to handle both sort and filter
		for (int i = 0, l = visibleColumns.getSize(); i < l; i++) {
			WebColumn column = visibleColumns.getAt(i);
			HashMap<Object, Object> colJson = new HashMap<Object, Object>();
			colJson.put("i", i);
			if (sortChanged) {
				Boolean ascending = CH.getOr(sortedColumnIds, column.getColumnId(), null);
				String sort = ascending == null ? "" : (String.valueOf((ascending ? FastWebTable.ASCEND : 0) + (smartTable.getKeepSorting() ? FastWebTable.KEEP_SORT : 0)));
				colJson.put("s", sort);
			}
			if (filterChanged) {
				WebTableFilteredInFilter f = filteredIn.get(column.getColumnId());
				isFiltered = f != null;
				String filterText = null;
				if (f != null && f.isSimple())
					filterText = f.getSimpleValue();
				colJson.put("f", isFiltered); // do we really need two vars to do filtering?
				colJson.put("ft", filterText);
			}
			columns.add(colJson);
		}
		jsFunction.addParam(RootAssister.INSTANCE.toJson(columns));
		jsFunction.end();
	}

	private void createJsClearData(StringBuilder js) {
		jsFunction.reset(js, "clearData");
		jsFunction.end();
	}

	private void createJsTableSize(StringBuilder js) {
		jsFunction.reset(js, "setColsRowsCount");
		jsFunction.addParam(getVisibleColumnsCount());
		jsFunction.addParam(getRowsCount());
		jsFunction.end();
		int totalWidth = 0;
		for (int i = 0, l = visibleColumns.getSize(); i < l; i++) {
			WebColumn column = visibleColumns.getAt(i);
			totalWidth += column.getWidth();
		}
		int rows = this.getRowsCount();
		jsFunction.reset(js, "setTotalWidthHeight");
		jsFunction.addParam(totalWidth);
		jsFunction.addParam(rows);
		jsFunction.end();
	}

	private void createJsTableScroll(StringBuilder js) {
		jsFunction.reset(js, "setScroll");
		jsFunction.addParam(this.tableLeft);
		jsFunction.addParam(this.tableTop);
		jsFunction.addParam(this.userScrollSeqnum);
		jsFunction.end();
	}

	private StringBuilder sbTmp = new StringBuilder();
	private StringBuilder sb2Tmp = new StringBuilder();

	private void createJsSetTableData(StringBuilder js, boolean needsDataReset, boolean forceFireChangedSelect, byte status) {
		if (forceFireChangedSelect || MH.anyBits(status, SmartTable.STATUS_ROWS_CHANGED)) {
			updateSelects();
			createJsInitSelection(js);
		} else if (MH.anyBits(this.changes, CHANGED_SELECT)) {
			createJsInitSelection(js);
		}

		if (pendingClipZoneTop == -1 || pendingClipZoneBottom == -1 || smartTable.getRows().isEmpty())
			return;
		if (!needsDataReset) {
			if (this.clipZoneTop == -1 || this.clipZoneBottom == -1)//screen refresh
				needsDataReset = true;
			if (this.clipZoneLeft == -1 && this.clipZoneRight == -1 && this.clipZoneLeftPin == -1 && this.clipZoneRightPin == -1)//screen refresh
				needsDataReset = true;
			else if (pendingClipZoneBottom != -1 && pendingClipZoneBottom <= this.clipZoneTop)//We've scrolled up entire page
				needsDataReset = true;
			else if (pendingClipZoneTop != -1 && pendingClipZoneTop >= this.clipZoneBottom)//We've scrolled down entire page
				needsDataReset = true;
		}

		final int rowsCount = getRowsCount();
		int bottom = Math.min(pendingClipZoneBottom + 1, rowsCount);

		if (needsDataReset) {
			createJsSetFullRowData(js, pendingClipZoneTop, bottom);
		} else {
			int top = Math.min(this.clipZoneTop, rowsCount);
			if (top != pendingClipZoneTop)
				createJsSetFullRowData(js, pendingClipZoneTop, top);
			if (clipZoneBottom + 1 != bottom)
				createJsSetFullRowData(js, clipZoneBottom + 1, bottom);
			boolean scrollLeft = this.pendingClipZoneLeft != -1 && this.clipZoneLeft != -1 && this.pendingClipZoneLeft < this.clipZoneLeft;
			boolean scrollRight = this.pendingClipZoneLeft != -1 && this.clipZoneRight != -1 && this.pendingClipZoneRight > this.clipZoneRight;
			boolean scrollPinRight = this.pendingClipZoneLeftPin != -1 && this.clipZoneRightPin != -1 && this.pendingClipZoneRightPin > this.clipZoneRightPin;
			for (int i = pendingClipZoneTop; i < bottom; i++) {
				Row row = getRow(i);
				createJsSetData_Start(js, row);
				if (scrollLeft)
					createJsSetData_AppendCells(js, row, this.pendingClipZoneLeft, this.clipZoneLeft - 1);
				if (scrollRight)
					createJsSetData_AppendCells(js, row, this.clipZoneRight + 1, this.pendingClipZoneRight);
				if (scrollPinRight)
					createJsSetData_AppendCells(js, row, this.clipZoneRightPin + 1, this.pendingClipZoneRightPin);
				createJsSetData_End(js, row);
			}
		}
		this.clipZoneTop = this.pendingClipZoneTop;
		this.clipZoneBottom = bottom - 1;
		this.clipZoneLeft = this.pendingClipZoneLeft;
		this.clipZoneRight = this.pendingClipZoneRight;
		this.clipZoneLeftPin = this.pendingClipZoneLeftPin;
		this.clipZoneRightPin = this.pendingClipZoneRightPin;
	}
	private void createJsSetFullRowData(StringBuilder js, int start, int end) {
		for (int i = start; i < end; i++) {
			Row row = getRow(i);
			createJsSetFullRowData(js, row);
		}
	}

	private void createJsSetFullRowData(StringBuilder js, Row row) {
		createJsSetData_Start(js, row);
		if (this.rowBgColorColumnPos != -1)
			createJsSetData_AppendRowBgColor(js, row);
		if (this.rowTxColorColumnPos != -1)
			createJsSetData_AppendRowTxColor(js, row);
		if (this.pendingClipZoneLeftPin != -1) {
			if (pendingClipZoneRightPin + 1 == this.pendingClipZoneLeft) {
				createJsSetData_AppendCells(js, row, this.pendingClipZoneLeftPin, this.pendingClipZoneRight);
			} else {
				createJsSetData_AppendCells(js, row, this.pendingClipZoneLeftPin, this.pendingClipZoneRightPin);
				if (this.pendingClipZoneLeft != -1)
					createJsSetData_AppendCells(js, row, this.pendingClipZoneLeft, this.pendingClipZoneRight);
			}
		} else if (this.pendingClipZoneLeft != -1)
			createJsSetData_AppendCells(js, row, this.pendingClipZoneLeft, this.pendingClipZoneRight);
		createJsSetData_End(js, row);
	}

	private int setDataJsMark;
	private int setDataJsStart;

	private void createJsSetData_Start(StringBuilder js, Row row) {
		this.setDataJsMark = js.length();
		jsFunction.reset(js, "srd");
		jsFunction.addParam(row.getLocation());
		jsFunction.addParam(row.getUid());
		this.setDataJsStart = js.length();
	}
	private void createJsSetData_AppendRowTxColor(StringBuilder js, Row row) {
		jsFunction.addParam(-1).addParamQuoted(row.getAt(rowTxColorColumnPos));
	}
	private void createJsSetData_AppendRowBgColor(StringBuilder js, Row row) {
		jsFunction.addParam(-2).addParamQuoted((String) row.getAt(rowBgColorColumnPos));
	}
	private void createJsSetData_AppendCells(StringBuilder js, Row row, int visibleColumnPositionStart, int visibleColumnPositionEnd) {
		int count = visibleColumnPositionEnd - visibleColumnPositionStart + 1;
		if (count == 0)
			return;
		else if (count == 1)
			createJsSetData_AppendCell(js, row, visibleColumnPositionStart);
		else if (count == 2) {
			createJsSetData_AppendCell(js, row, visibleColumnPositionStart);
			createJsSetData_AppendCell(js, row, visibleColumnPositionEnd);
		} else {
			jsFunction.addParam(-3);
			jsFunction.addParam(visibleColumnPositionStart);
			jsFunction.addParam(count);
			for (int x = visibleColumnPositionStart; x <= visibleColumnPositionEnd; x++) {
				sbTmp.setLength(0);
				sb2Tmp.setLength(0);
				WebColumn column = visibleColumns.getAt(x);
				try {
					column.getCellFormatter().formatCellToHtml(column.getData(row), sbTmp, sb2Tmp);
					jsFunction.addParamQuoted(sbTmp);
					jsFunction.addParamQuoted(sb2Tmp);
				} catch (Exception e) {
					jsFunction.addParamQuoted("error");
					jsFunction.addParamQuoted("");
					LH.warning(log, "error formatting column ", column.getColumnId(), " for row ", row, e);
				}
			}
		}
	}
	private void createJsSetData_AppendCell(StringBuilder js, Row row, int visibleColumnPosition) {
		jsFunction.addParam(visibleColumnPosition);
		sbTmp.setLength(0);
		sb2Tmp.setLength(0);
		WebColumn column = visibleColumns.getAt(visibleColumnPosition);
		try {
			column.getCellFormatter().formatCellToHtml(column.getData(row), sbTmp, sb2Tmp);
			jsFunction.addParamQuoted(sbTmp);
			jsFunction.addParamQuoted(sb2Tmp);
		} catch (Exception e) {
			jsFunction.addParamQuoted("error");
			jsFunction.addParamQuoted("");
			LH.warning(log, "error formatting column ", column.getColumnId(), " for row ", row, e);
		}
	}
	private void createJsSetData_End(StringBuilder js, Row row) {
		if (this.setDataJsStart == js.length())
			js.setLength(this.setDataJsMark);
		else
			jsFunction.end();
	}

	private void debugState() {
		LH.warning(log, "=== Critical error, Table State ===  cz:", this.getClipZoneTop(), " - ", this.getClipZoneBottom(), " changes:", this.changes);
		LH.warning(log, " filtered: ", this.filter);
		this.smartTable.debugState();

	}
	@Override
	public void refresh() {
		changed(CHANGED_ALL);
	}

	public void clearSort() {
		if (this.sortedColumnIds.isEmpty())
			return;
		this.sortedColumnIds.clear();
		smartTable.sortRows(null, false);
		rowComparator = null;
		changed(CHANGED_ALL_ROWS);
	}
	@Override
	public void sortRows(String columnId, boolean ascending, boolean keepSorting, boolean add) {
		this.pausedRowComparator = null;
		if (add)
			keepSorting = smartTable.getKeepSorting();
		if (!add)
			this.sortedColumnIds.clear();
		if (columnId == null) {
			smartTable.sortRows(null, keepSorting);
			rowComparator = null;
		} else {
			WebColumn column = columns.get(columnId);
			sortedColumnIds.remove(column.getColumnId());
			sortedColumnIds.put(column.getColumnId(), ascending);
			List<WebColumn> columns = new ArrayList<WebColumn>();
			List<Boolean> ascendings = new ArrayList<Boolean>();
			for (Map.Entry<String, Boolean> e : sortedColumnIds.entrySet()) {
				columns.add(this.columns.get(e.getKey()));
				ascendings.add(e.getValue());
			}
			rowComparator = new WebTableRowComparator(columns, ascendings);
			smartTable.sortRows(rowComparator, keepSorting);
		}
		changed(CHANGED_ALL_ROWS);

	}

	private Comparator<Row> pausedRowComparator = null;

	private int moveToRow = -1;
	private int pinnedColumnsCount = 0;

	private boolean scrollToBottomOnAppend;

	public void pauseSort(boolean pause) {
		if (!isKeepSorting() && pause)
			throw new IllegalStateException();
		if (isPausedSort() == pause)
			throw new IllegalStateException();
		if (pause) {
			this.pausedRowComparator = rowComparator;
			this.rowComparator = null;
			smartTable.sortRows(null, false);
		} else {
			this.rowComparator = pausedRowComparator;
			this.pausedRowComparator = null;
			smartTable.sortRows(rowComparator, true);
		}
	}
	public boolean isPausedSort() {
		return this.pausedRowComparator != null;
	}

	@Override
	public Iterable<Map.Entry<String, Boolean>> getSortedColumns() {
		return this.sortedColumnIds.entrySet();
	}

	@Override
	public Set<String> getSortedColumnIds() {
		return this.sortedColumnIds.keySet();
	}
	@Override
	public boolean isSorting() {
		return sortedColumnIds.size() > 0;
	}
	@Override
	public boolean isKeepSorting() {
		return isSorting() && smartTable.getKeepSorting();
	}

	@Override
	public void setSearch(String expression) {
		if (OH.eq(expression, search))
			return;
		this.search = expression;
		updateFilter(false);
	}

	@Override
	public String getSearch() {
		return search;
	}

	@Override
	public int getRowsCount() {
		return smartTable.getSize();
	}

	/**
	 * This method intends to remove multiple filters at once for Table, should NOT call this method to modify multiple user filter values.
	 * 
	 * @param columnIds
	 *            Takes a set of column Ids (String) to remove
	 * @param filter
	 *            Set as null
	 * 
	 */
	public void clearFilters(Set<String> toRemove) {
		filteredIn.keySet().removeAll(toRemove);
		updateFilter(false);
	}

	@Override
	public void setFilteredIn(String columnId, WebTableFilteredInFilter filter) {
		if (filter == null || filter.isEmpty())
			filteredIn.remove(columnId);
		else
			filteredIn.put(columnId, filter);
		updateFilter(false);
	}
	public void setFilteredIn(String columnId, Set<String> filterIn) {
		this.setFilteredIn(columnId, filterIn, true, false, false, null, false, null, false);
	}
	public void setFilteredIn(String columnId, Set<String> filterIn, boolean keep, boolean includeNull, boolean isPattern, String min, String max) {
		this.setFilteredIn(columnId, filterIn, keep, includeNull, isPattern, min, true, max, true);
	}
	public void setFilteredIn(String columnId, Set<String> filterIn, boolean keep, boolean includeNull, boolean isPattern, String min, boolean minInclusive, String max,
			boolean maxInclusive) {
		WebTableFilteredInFilter f = new WebTableFilteredInFilter(getColumn(columnId));
		f.setValues(filterIn, isPattern).setMin(minInclusive, min).setMax(maxInclusive, max).setIncludeNull(includeNull).setKeep(keep);
		setFilteredIn(columnId, f);
	}

	private void updateFilter(boolean force) {
		final RowFilter old = smartTable.getTableFilter();
		List<RowFilter> filters = new ArrayList<RowFilter>();
		if (this.externalFilter != null)
			filters.add(externalFilter);
		for (Entry<String, WebTableFilteredInFilter> e : filteredIn.entrySet())
			filters.add(e.getValue());
		if (SH.is(search))
			filters.add(new WebTableSearchFilter(this));
		else {
		}
		final RowFilter f;
		if (filters.size() == 0)
			f = (null);
		else if (filters.size() == 1)
			f = filters.get(0);
		else
			f = (new ChainedRowFilter(false, filters));
		if (OH.ne(old, f) || force) {
			smartTable.setTableFilter(f);
			changed(CHANGED_ALL_ROWS);
			fireOnFilterChanging();
		}

	}

	@Override
	public WebTableFilteredInFilter getFiltererdIn(String columnId) {
		return filteredIn.get(columnId);
	}

	@Override
	public Set<String> getFilteredInColumns() {
		return filteredIn.keySet();
	}

	@Override
	public WebColumn getColumn(String columnId) {
		return CH.getOrThrow(columns, columnId);
	}

	public WebColumn findColumnByName(String columnId) {
		for (WebColumn i : this.columns.values())
			if (OH.eq(columnId, i.getColumnName()))
				return i;
		return null;
	}

	@Override
	public Row getRow(int i) {
		return smartTable.getRows().get(i);
	}

	@Override
	public Iterable<Row> getFilteredRows() {
		return smartTable.getFiltered();
	}

	@Override
	public WebContextMenuFactory getMenuFactory() {
		return contextMenuFactory;
	}

	@Override
	public void setMenuFactory(WebContextMenuFactory factory) {
		this.contextMenuFactory = factory;
	}

	@Override
	public List<Row> getRowsByUid(Collection<Integer> rowIds) {
		List<Row> r = new ArrayList<Row>(rowIds.size());
		for (Row row : smartTable.getRows())
			if (rowIds.contains(row.getUid()))
				r.add(row);
		for (Row row : smartTable.getFiltered())
			if (rowIds.contains(row.getUid()))
				r.add(row);
		return r;
	}

	@Override
	public void addMenuListener(WebContextMenuListener listener) {
		if (menuListeners.contains(listener))
			LH.warning(log, "Listener already exists: ", listener);
		else
			menuListeners.add(listener);
	}

	@Override
	public void removeMenuListener(WebContextMenuListener listener) {
		menuListeners.remove(listener);
	}

	@Override
	public List<WebContextMenuListener> getMenuListeners() {
		return menuListeners;
	}

	@Override
	public void addWebTableListener(WebTableListener webTableListener) {
		if (webTableListeners.contains(webTableListener))
			LH.warning(log, "Listener already exists: ", webTableListener);
		else
			webTableListeners.add(webTableListener);
	}

	@Override
	public void removeWebTableListener(WebTableListener webTableListener) {
		webTableListeners.remove(webTableListener);

	}

	@Override
	public void onCell(Row row, int cell, Object oldValue, Object newValue) {
		changed(CHANGED_CELLS);
	}

	@Override
	public void onColumnAdded(Column nuw) {
		changed(CHANGED_COLUMNS);

	}
	public void onColumnChanged(WebColumn nuw) {
		changed(CHANGED_COLUMNS);

	}

	@Override
	public void onColumnRemoved(Column old) {
		changed(CHANGED_COLUMNS);
		updateColumnLocations();
	}

	@Override
	public void onColumnChanged(Column old, Column nuw) {
		changed(CHANGED_COLUMNS);

	}

	@Override
	public void onRowAdded(Row add) {
		changed(CHANGED_ROWS);
		if (scrollToBottomOnAppend && add.getLocation() == getRowsCount() - 1)
			setTableTop(Integer.MAX_VALUE);

	}

	@Override
	public void onRowRemoved(Row removed, int location) {
		changed(CHANGED_ROWS);
	}

	@Override
	public String getCallback() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCallback(String callback, Map<String, String> params) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String> getCallbackParams() {
		throw new UnsupportedOperationException();
	}

	public void setActiveRowNoFire(int activeRow) {
		if (activeRow < 0 || activeRow >= getRowsCount()) {
			this.activeRow = null;
		} else {
			this.activeRow = getRow(activeRow);
		}
	}
	public void setSelectedRows(int[] rows) {
		if (AH.isEmpty(rows)) {
			setActiveRowNoFire(-1);
			setSelectedRowsNoFire("");
		} else {
			setActiveRowNoFire(rows[0]);
			setSelectedRowsNoFire(SH.join(",", rows));
		}
		changed(CHANGED_SELECT);
	}
	public void setSelectedRows(String selectedRowsText) {
		setActiveRowNoFire(-1);
		//		setSelectedRowsNoFire("");
		setSelectedRowsNoFire(selectedRowsText);
		changed(CHANGED_SELECT);
	}

	public void setSelectedRowsNoFire(String selectedRowsText) {
		if (OH.eq(selectedRowsText, this.selectedRowsText))
			return;
		this.selectedRowsText = selectedRowsText;
		buildSelectedRows();
		for (WebContextMenuListener menuListener : menuListeners)
			menuListener.onSelectedChanged(this);
	}

	private void buildSelectedRows() {
		selectedRows.clear();
		selectedRowsSet.clear();
		String[] parts = SH.split(',', selectedRowsText);
		int ranges[] = new int[parts.length * 2];
		int i = 0;
		for (String s : parts) {
			int start, end;
			if (s.indexOf('-') == -1) {
				start = end = Integer.parseInt(s);
			} else {
				start = Integer.parseInt(SH.beforeFirst(s, '-'));
				end = Integer.parseInt(SH.afterFirst(s, '-'));
			}
			ranges[i++] = start;
			ranges[i++] = end;
		}
		int rowsCount = getRowsCount();
		if (i == ranges.length) {//made it to the end
			for (i = 0; i < ranges.length; i += 2) {
				int loc = ranges[i], end = ranges[i + 1];
				while (loc <= end && loc < rowsCount)
					selectedRows.add(getRow(loc++));
			}
		}
	}

	@Override
	public List<Row> getSelectedRows() {
		if (!hasSelectedRows())
			return Collections.EMPTY_LIST;
		if (selectedRows.size() == 0)
			buildSelectedRows();
		return Collections.unmodifiableList(selectedRows);
	}

	public Row getActiveRow() {
		return this.activeRow;
	}

	@Override
	public boolean hasSelectedRows() {
		return selectedRowsText.length() > 0;
	}

	@Override
	public BasicWebColumn addColumn(boolean visible, String title, String columnIdOrArrayOfIds, WebCellFormatter formatter) {
		final String columnId = ensureUniqueColumnId(columnIdOrArrayOfIds.toString());
		BasicWebColumn r = new BasicWebColumn(this, SH.toString(columnId), title, formatter, new String[] { columnIdOrArrayOfIds });
		addColumn(r);
		if (visible) {
			visibleColumns.add(r.getColumnId(), r);
			columnDependenciesOutdated = true;
			changed(CHANGED_COLUMNS);
		} else
			hiddenColumns.add(r.getColumnId(), r);
		return r;
	}
	private String ensureUniqueColumnId(String columnId) {
		String r = columnId;
		int i = 0;
		while (columns.containsKey(r))
			r = columnId + (++i);
		return r;
	}

	@Override
	public BasicWebColumn addColumnAt(boolean visible, String title, String columnIdOrArrayOfIds, WebCellFormatter formatter, int location) {
		BasicWebColumn r = new BasicWebColumn(this, SH.toString(this.columns.size()), title, formatter, new String[] { columnIdOrArrayOfIds });
		addColumn(r);
		if (visible) {
			visibleColumns.add(r.getColumnId(), r, location);
			columnDependenciesOutdated = true;
			changed(CHANGED_COLUMNS);
		} else
			hiddenColumns.add(r.getColumnId(), r);
		if (visible && location < this.pinnedColumnsCount)
			setPinnedColumnsCount(this.pinnedColumnsCount + 1);
		return r;
	}

	@Override
	public RowFilter getExternalFilter() {
		return externalFilter;
	}

	@Override
	public void setExternalFilter(RowFilter externalFilter) {
		if (externalFilter == null && this.externalFilter == null)
			return;
		this.externalFilter = externalFilter;
		getTable().ensureUpToDateAndGetStatus();
		updateFilter(true);
		changed(CHANGED_ALL_ROWS);
	}

	public void setExternalFilterIndex(String columnId, Set<Object> values) {
		if (columnId == null && this.smartTable.getExternalFilterIndexColumnId() == null)
			return;
		this.smartTable.setExternalFilterIndex(columnId, values);
		changed(CHANGED_ALL_ROWS);
	}

	public Set<String> getColumnIds() {
		return columns.keySet();
	}
	public WebColumn getColumnNoThrow(String key) {
		return columns.get(key);
	}

	public void clear() {
		getTable().clear();
		this.selectedRows.clear();
		this.selectedRowsText = "";
	}

	public int getChanges() {
		return changes;
	}

	@Override
	public void addColumnMenuListener(WebTableColumnContextMenuListener listener) {
		if (columnMenuListeners.contains(listener))
			LH.warning(log, "Menu Listener already exists: ", listener);
		else
			columnMenuListeners.add(listener);
	}

	@Override
	public void removeColumnMenuListener(WebTableColumnContextMenuListener listener) {
		columnMenuListeners.remove(listener);
	}
	@Override
	public WebTableColumnContextMenuFactory getColumnMenuFactory() {
		return this.columnContextMenuFactory;
	}
	@Override
	public void setColumnMenuFactory(WebTableColumnContextMenuFactory m) {
		this.columnContextMenuFactory = m;
	}

	@Override
	public List<WebTableColumnContextMenuListener> getColumnMenuListeners() {
		return columnMenuListeners;
	}

	@Override
	public int getColumnPosition(String columnId) {
		return this.visibleColumns.getPositionNoThrow(columnId);
	}

	public String getRowDelimiter() {
		return rowDelimiter;
	}

	public void setRowDelimiter(String rowDelimiter) {
		this.rowDelimiter = rowDelimiter;
	}

	public String getColumnDelimiter() {
		return columnDelimiter;
	}

	public void setColumnDelimiter(String columnDelimiter) {
		this.columnDelimiter = columnDelimiter;
	}

	public String getInlineDelimiter() {
		return inlineDelimiter;
	}

	public void setInlineDelimiter(String inlineDelimiter) {
		this.inlineDelimiter = inlineDelimiter;
	}

	public String getInlineEnclosed() {
		return inlineEnclosed;
	}

	public void setInlineEnclosed(String inlineEnclosed) {
		this.inlineEnclosed = inlineEnclosed;
	}

	public String getHeaderOptions() {
		return headerOptions;
	}

	public void setHeaderOptions(String headerOptions) {
		this.headerOptions = headerOptions;
	}

	public List<Row> getRows() {
		List<Row> rows = new ArrayList<Row>();
		for (int i = 0; i < getRowsCount(); i++) {
			rows.add(i, getRow(i));
		}
		return rows;
	}

	public void userNavigate(int row, int direction) {
		int max = getRowsCount() - 1;
		if (row < 0 || row > max)
			return;
		// gets all the sorted columns
		Set<String> sorted = getSortedColumnIds();
		WebColumn col;
		// if there is no sorted column, use the first visible column
		// otherwise use the first sorted column
		if (CH.isEmpty(sorted))
			col = getVisibleColumn(0);
		else
			col = getColumn(CH.first(sorted));
		WebCellFormatter cf = col.getCellFormatter();
		// obtain value from the target column x row
		Comparable origValue = cf.getOrdinalValue(col.getData(getRow(row)));
		int newRow = row;
		// -1 previous, 1 forward
		if (direction < 0) {
			// wrap around
			if (newRow == 0)
				newRow = max;
			else
				while (newRow > 0) {
					//goes backward and tries to find the first value that is different from current value
					Comparable value = cf.getOrdinalValue(col.getData(getRow(--newRow)));
					if (OH.ne(value, origValue))
						break;
				}
		} else if (direction > 0) {
			if (newRow == max)
				newRow = 0;
			else
				while (newRow < max) {
					Comparable value = cf.getOrdinalValue(col.getData(getRow(++newRow)));
					if (OH.ne(value, origValue))
						break;
				}
		}
		if (newRow != row) {
			setSelectedRows(new int[] { newRow });
			ensureRowVisible(newRow);
		}
	}
	public void ensureRowVisible(int row) {
		this.moveToRow = row;
	}

	public void userNavigate(int row, String text, boolean isNext) {

		int max = getRowsCount() - 1;
		if (row < 0 || row > max)
			return;
		Set<String> sorted = getSortedColumnIds();
		WebColumn col;
		if (CH.isEmpty(sorted))
			col = getVisibleColumn(0);
		else
			col = getColumn(CH.first(getSortedColumnIds()));
		WebCellFormatter cf = col.getCellFormatter();
		int newRow = -1;
		if (isNext && max > 0) {
			for (int i = row + 1; i != row; i++) {
				if (i == max + 1) {
					i = -1;
					continue;
				}
				String value = cf.formatCellToText(col.getData(getRow(i)));
				if (value != null && SH.startsWithIgnoreCase(value, text, 0)) {
					newRow = i;
					break;
				}
			}
		} else {
			for (int i = 0; i <= max; i++) {
				String value = cf.formatCellToText(col.getData(getRow(i)));
				if (value != null && SH.startsWithIgnoreCase(value, text, 0)) {
					newRow = i;
					break;
				}
			}
		}
		if (newRow != row && newRow != -1) {
			setSelectedRows(new int[] { newRow });
			ensureRowVisible(newRow);
		}
	}

	public int getPinnedColumnsCount() {
		return this.pinnedColumnsCount;
	}

	public void setPinnedColumnsCount(int count) {
		if (count >= getVisibleColumnsCount())
			count = 0;
		if (count == this.pinnedColumnsCount)
			return;

		if (count == 0) {
			this.pendingClipZoneLeftPin = -1;
			this.pendingClipZoneRightPin = -1;
		} else {
			this.pendingClipZoneLeftPin = 0;
			this.pendingClipZoneRightPin = count - 1;
		}
		this.pinnedColumnsCount = count;
		changed(CHANGED_COLUMNS);
	}

	// 0-indexed
	private boolean isColumnPositionPinned(int pos) {
		return pos != -1 ? pos >= 0 && pos < this.pinnedColumnsCount : false;
	}
	public void autoSizeColumn(WebColumn webColumn, PortletMetrics metrics, int fontSize, int minWidth, int maxWidth, int headerFontSize) {
		final StringBuilder htmlSink = new StringBuilder();
		final StringBuilder styleSink = new StringBuilder();
		final TableList rows = getTable().getRows();
		int titleWidth = metrics.getHtmlWidth(webColumn.getColumnName(), webColumn.getHeaderStyle(), headerFontSize);
		int width = Math.max(minWidth, titleWidth);
		for (int i = 0, l = Math.min(10000, rows.size()); i < l && width < maxWidth; i++) {
			webColumn.getCellFormatter().formatCellToHtml(webColumn.getData(rows.get(i)), htmlSink, styleSink);
			SH.clear(htmlSink);
			webColumn.getCellFormatter().formatCellToText(webColumn.getData(rows.get(i)), htmlSink);
			width = Math.max(width, metrics.getWidth(htmlSink, styleSink, fontSize));
			SH.clear(htmlSink);
			SH.clear(styleSink);
		}
		width += 10; // not sure what this is for
		if (this.sortedColumnIds.containsKey(webColumn.getColumnId())) {
			// account for sort icon
			width += 11;
		}
		if (webColumn.getWidth() != width) {
			webColumn.setWidth(width);
			fireOnColumnsSized();
			changed(CHANGED_COLUMNS);
		}
	}

	@Override
	public int getColumnPosition(Object columnId) {
		return this.getColumnPosition((String) columnId);
	}

	public void fireOnUserDblclick(String action, Map<String, String> properties) {
		for (int i = 0; i < menuListeners.size(); i++)
			menuListeners.get(i).onUserDblclick(this, action, properties);
	}

	private static final String BGCOL = "!!BGCOL";
	private static final String TXCOL = "!!TXCOL";

	public Column getRowTxColorColumn() {
		return this.rowTxColorColumnPos == -1 ? null : this.smartTable.getColumnAt(this.rowTxColorColumnPos);
	}
	public Column getRowBackgroundColorColumn() {
		return this.rowBgColorColumnPos == -1 ? null : this.smartTable.getColumnAt(this.rowBgColorColumnPos);
	}
	public void setRowTxColorFormula(String col) {
		this.rowTxColorColumnPos = col == null ? -1 : this.smartTable.getColumn(col).getLocation();
		changed(CHANGED_ROW_CSS);
		this.columnDependenciesOutdated = true;
	}

	public void setRowBgColorFormula(String col) {
		this.rowBgColorColumnPos = col == null ? -1 : this.smartTable.getColumn(col).getLocation();
		changed(CHANGED_ROW_CSS);
		this.columnDependenciesOutdated = true;
	}

	private int tableLeft = 0;
	private int tableTop = 0;
	private int tableHeight = 0;
	private int userSelectSeqnum;
	private int userScrollSeqnum;
	private long contentWidth;
	private long contentHeight;

	private FastTablePortlet parentPortlet;

	public void setTableLeft(int left) {
		this.tableLeft = left;
	}
	public int getTableLeft() {
		return this.tableLeft;
	}
	public void setTableTop(int top) {
		this.tableTop = top;
	}
	public int getTableTop() {
		return this.tableTop;
	}
	public void setTableHeight(int height) {
		this.tableHeight = height;
	}
	public int getTableHeight() {
		return this.tableHeight;
	}
	public void setContentWidth(long width) {
		this.contentWidth = width;
	}
	public long getContentWidth() {
		return this.contentWidth;
	}
	public void setContentHeight(long height) {
		this.contentHeight = height;
	}
	public long getContentHeight() {
		return this.contentHeight;
	}
	public void scrollLeftColumn(int scroll) {
		// Get pinned columns width;
		int pcc = this.pinnedColumnsCount;
		int pcw = 0;
		for (int i = 0; i < pcc - 1; i++) {
			pcw += this.visibleColumns.getAt(i).getWidth();
		}
		int point = scroll + pcw;
		//Add pinned width to left;
		//Get column at point
		int colAtPoint = -1;
		int colleft = pcw;
		int colright = pcw;

		for (int i = pcc; i < this.getVisibleColumnsCount(); i++) {
			colleft = colright;
			colright = colright + this.visibleColumns.getAt(i).getWidth();

			if (colleft < point && point < colright) {
				colAtPoint = i;
				break;
			} else if (colleft == point) {
				colAtPoint = i - 1;
				break;
			}
		}

		//if column not found set to first column;
		if (colAtPoint == -1)
			colAtPoint = 0;

		//Get previous column
		//Snap to that column
		WebColumn col = this.visibleColumns.getAt(colAtPoint);
		this.snapToColumn(col.getColumnId());

	}
	public void scrollRightColumn(int scroll) {
		// Get pinned columns width;
		int pcc = this.pinnedColumnsCount;
		int pcw = 0;
		for (int i = 0; i < pcc - 1; i++) {
			pcw += this.visibleColumns.getAt(i).getWidth();
		}
		int point = scroll + pcw;
		//Add pinned width to left;
		//Get column at point
		int colAtPoint = -1;
		int colleft = pcw;
		int colright = pcw;

		for (int i = pcc; i < this.getVisibleColumnsCount(); i++) {
			colleft = colright;
			colright = colright + this.visibleColumns.getAt(i).getWidth();

			if (colleft < point && point < colright) {
				colAtPoint = i + 1;
				break;
			} else if (colleft == point) {
				colAtPoint = i + 1;
				break;
			}
		}

		//if column not found set to last column;
		if (colAtPoint == -1)
			colAtPoint = this.getVisibleColumnsCount() - 1;
		if (colAtPoint == this.getVisibleColumnsCount()) { // on last column and right arrow pressed
			this.setTableLeft(colleft + this.visibleColumns.getAt(colAtPoint - 1).getWidth()); // snap to the end of last column
			changed(CHANGED_SCROLL);
			return;
		}

		//Get previous column
		//Snap to that column
		WebColumn col = this.visibleColumns.getAt(colAtPoint);
		this.snapToColumn(col.getColumnId());
	}
	@Override
	public void snapToColumn(Object columnId) {
		WebColumn column = this.columns.get(columnId);
		if (this.visibleColumns.containsKey(column.getColumnId())) {
			String id = column.getColumnId();
			int colLoc = this.visibleColumns.getPosition(id);
			if (colLoc < this.pinnedColumnsCount)
				return;

			if (this.clipZoneRightPin != -1 && colLoc <= this.clipZoneRightPin)
				return;

			int left = 0;
			for (int i = 0; i < colLoc; i++) {
				left += this.visibleColumns.getAt(i).getWidth();
			}

			if (this.clipZoneRightPin != -1)
				for (int i = 0; i < this.clipZoneRightPin + 1; i++) {
					left -= this.visibleColumns.getAt(i).getWidth();
				}
			final WebPoint maxScroll = getMaxScroll();
			this.setTableLeft(OH.min(left, maxScroll.getX()));
			changed(CHANGED_SCROLL);
		}
	}

	private int snapToRowNumber = -1;
	private byte snapToRowTopAlign = 0; // 0 - top, 1 - bottom
	public static byte SNAP_ALIGN_TOP = 0;
	public static byte SNAP_ALIGN_BOTTOM = 1;

	public void snapToRowWithAlign(int rowPos, byte align) {
		this.snapToRowNumber = rowPos;
		this.snapToRowTopAlign = align;
		changed(CHANGED_SCROLL);
	}

	public void snapToRow(int rowPos) {
		this.setTableTop(rowPos);
		changed(CHANGED_SCROLL);
	}

	public void setUserSelectSeqnum(int userSeqnum) {
		this.userSelectSeqnum = userSeqnum;
	}
	public void setUserScrollSeqnum(int userSeqnum) {
		this.userScrollSeqnum = userSeqnum;
	}

	@Override
	public FastWebColumn getFastWebColumn(Object columnId) {
		return this.columns.get(columnId);
	}

	public boolean getScrollToBottomOnAppend() {
		return scrollToBottomOnAppend;
	}

	public void setScrollToBottomOnAppend(boolean scrollToBottomOnAppend) {
		this.scrollToBottomOnAppend = scrollToBottomOnAppend;
	}

	@Override
	public void fireOnColumnsArranged() {
		for (int i = 0; i < webTableListeners.size(); i++)
			webTableListeners.get(i).onColumnsArranged(this);
		updateFilter(false);
	}
	@Override
	public void fireOnColumnsSized() {
		for (int i = 0; i < webTableListeners.size(); i++)
			webTableListeners.get(i).onColumnsSized(this);
	}
	@Override
	public void fireOnFilterChanging() {
		for (int i = 0; i < webTableListeners.size(); i++)
			webTableListeners.get(i).onFilterChanging(this);
	}

	public void setFilteredExpression(String columnId, String val) {
		if (SH.is(val)) {
			WebTableFilteredInFilter f = new WebTableFilteredInFilter(getColumn(columnId));
			f.setFilteredExpression(val);
			setFilteredIn(columnId, f);
		} else if (filteredIn.remove(columnId) != null)
			updateFilter(false);
	}

	public boolean isSelected(Row row) {
		if (!hasSelectedRows())
			return false;
		if (selectedRowsSet.isEmpty()) {
			if (selectedRows.size() == 0)
				buildSelectedRows();
			this.selectedRowsSet.addAll(this.selectedRows);
		}
		return selectedRowsSet.contains(row);
	}

	public void setParentPortlet(FastTablePortlet pp) {
		this.parentPortlet = pp;
	}
	@Override
	public int getVisibleColumnsLimit() {
		return this.visibleColumnsLimit;
	}
	@Override
	public void setVisibleColumnsLimit(int columnsLimit) {
		if (columnsLimit != visibleColumnsLimit)
			if (columnsLimit < -1)
				this.visibleColumnsLimit = -1;
			else
				this.visibleColumnsLimit = columnsLimit;
	}

	private Map<String, String> htmlIdSelectorForColumns = new HashMap<String, String>();

	public void setHtmlIdSelectorForColumn(String columnId, String his) {
		String htmlIdSelector = this.htmlIdSelectorForColumns.get(columnId);
		if (OH.eq(htmlIdSelector, his))
			return;
		this.htmlIdSelectorForColumns.put(columnId, his);
		//Used in createJsInitColumns 
		changed(CHANGED_COLUMNS);
	}
	// considering adding this to WebTable
	public boolean isPrimarySort(String colId) {
		if (!isSorting())
			return false;
		Set<String> sortedCols = getSortedColumnIds();
		for (String s : sortedCols) {
			if (OH.eq(s, colId))
				return true;
			break;
		}
		return false;
	}
	public boolean autoFitVisibleColumns(int totalWidth) {
		int currentColumnsWidth = this.getAllColumnsWidth(true);
		totalWidth -= this.getFixedColumnsWidth();

		int visibleColumnsCount = this.getVisibleColumnsCount();
		double residual = 0, ratio, curWidth, rawWidth;
		boolean changed = false;
		int newWidth;
		// fit each column with ratio
		for (int i = 0; i < visibleColumnsCount; i++) {
			WebColumn col = this.getVisibleColumn(i);
			if (col.isFixedWidth()) {
				// ignore fixed width columns
				continue;
			}
			curWidth = col.getWidth();
			// keep current ratio
			ratio = curWidth * 1.0 / currentColumnsWidth;
			rawWidth = totalWidth * ratio;
			newWidth = (int) rawWidth;
			// gather decimal residual
			residual += (rawWidth - MH.round(rawWidth, MH.ROUND_DOWN));
			if (newWidth == curWidth)
				continue;
			if (i != visibleColumnsCount - 1) {
				col.setWidth(newWidth);
			} else {
				// give residual to last column
				col.setWidth(newWidth + (int) residual);
			}
			fireOnColumnsSized();
			changed = true;
		}
		if (changed)
			changed(CHANGED_COLUMNS);
		return true;
	}

	public int getAllColumnsWidth(boolean skipFixedCol) {
		int w = 0;
		for (int i = 0; i < getVisibleColumnsCount(); i++) {
			if (skipFixedCol && getVisibleColumn(i).isFixedWidth())
				continue;
			w += getVisibleColumn(i).getWidth();
		}
		return w;
	}

	public int getFixedColumnsWidth() {
		int w = 0;
		for (int i = 0; i < getVisibleColumnsCount(); i++) {
			if (getVisibleColumn(i).isFixedWidth())
				w += getVisibleColumn(i).getWidth();
		}
		return w;

	}

	public void setScrollPosition(WebPoint pos, boolean hasVertScollBar, boolean hasHorizScrollBar) {
		boolean changed = false;
		final WebPoint maxScroll = getMaxScroll();
		if (hasHorizScrollBar && pos.getX() >= 0 && pos.getX() != getTableLeft()) {
			setTableLeft(OH.min(pos.getX(), maxScroll.getX()));
			changed = true;
		} else
			LH.info(log, "skipping horizontal scroll (", "has scrollbar: ", hasHorizScrollBar, " x position: ", pos.getX(), " table left offset: ", getTableLeft());

		if (hasVertScollBar && pos.getY() >= 0 && pos.getY() != getTableTop()) {
			setTableTop(OH.min(pos.getY(), maxScroll.getY()));
			changed = true;
		} else
			LH.info(log, "skipping vertical scroll (", "has scrollbar: ", hasVertScollBar, " y position: ", pos.getY(), " table top offset: ", getTableTop());

		if (changed)
			changed(CHANGED_CLIPZONE | CHANGED_SCROLL);
	}

	public void flagColumnFixedWidth() {
		changed(CHANGED_COLUMNS);
	}

	public WebPoint getScrollPosition() {
		return new WebPoint(getTableLeft(), getTableTop());
	}

	public int getClipZoneRight() {
		return this.clipZoneRight;
	}

	public int getClipZoneLeft() {
		return this.clipZoneLeft;
	}

	private WebPoint getMaxScroll() {
		int scrollbarWidth = Caster_Integer.INSTANCE.cast(this.parentPortlet.getOption(FastTablePortlet.OPTION_SCROLL_BAR_WIDTH, "0"));
		if (this.parentPortlet.hasHorizontalScrollBar()) {
			scrollbarWidth += FastTablePortlet.DEFAULT_SCROLLBAR_WIDTH;
		}
		final int maxLeft = this.getAllColumnsWidth(false) - this.parentPortlet.getWidth() + scrollbarWidth;

		int maxTop = 0;
		if (this.parentPortlet.hasVerticalScrollBar()) {
			maxTop = (int) this.getContentHeight() - this.parentPortlet.getHeight();
			if (this.parentPortlet.hasHorizontalScrollBar())
				maxTop += scrollbarWidth;
			Boolean quickFilterHidden = Caster_Boolean.INSTANCE.cast(this.parentPortlet.getOption(FastTablePortlet.OPTION_QUICK_COLUMN_FILTER_HIDDEN, "false"));
			if (!quickFilterHidden) {
				final int quickFilterHeight = Caster_Integer.INSTANCE.cast(this.parentPortlet.getOption(FastTablePortlet.OPTION_QUICK_COLUMN_FILTER_HEIGHT, "0"));
				maxTop += quickFilterHeight;
			}
			Boolean menuBarHidden = Caster_Boolean.INSTANCE.cast(this.parentPortlet.getOption(FastTablePortlet.OPTION_QUICK_COLUMN_FILTER_HIDDEN, "false"));
			if (!menuBarHidden)
				maxTop += MENU_BAR_HEIGHT;
			final int headerHeight = Caster_Integer.INSTANCE.cast(this.parentPortlet.getOption(FastTablePortlet.OPTION_HEADER_ROW_HEIGHT, "0"));
			maxTop += headerHeight;
		}

		return new WebPoint(maxLeft, maxTop);
	}

}
