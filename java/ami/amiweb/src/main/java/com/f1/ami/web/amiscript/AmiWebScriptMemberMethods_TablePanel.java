package com.f1.ami.web.amiscript;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebAbstractTablePortlet;
import com.f1.ami.web.AmiWebAggregateObjectTablePortlet;
import com.f1.ami.web.AmiWebCustomColumn;
import com.f1.ami.web.AmiWebDatasourceTablePortlet;
import com.f1.ami.web.AmiWebFormatterManager;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.AmiWebWhereClause.WhereClause;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebColumn;
import com.f1.suite.web.table.impl.WebTableFilteredInFilter;
import com.f1.suite.web.tree.impl.ArrangeColumnsPortlet;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.ToDoException;
import com.f1.utils.WebPoint;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.concurrent.LinkedHasherSet;
import com.f1.utils.structs.table.BasicColumn;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_TablePanel extends AmiWebScriptBaseMemberMethods<AmiWebAbstractTablePortlet> {

	private AmiWebScriptMemberMethods_TablePanel() {
		super();

		addMethod(CLEAR_ROWS);
		addMethod(CLEAR_FILTERS);
		addMethod(GET_COLUMN_BY_ID);
		addMethod(CALL_COMMAND_ON_SELECTED);
		addMethod(GET_SELECTED_ROWS, "selectedRows");
		addMethod(GET_SELECTED_VALUES);
		addMethod(AS_TABLE, "table");
		addMethod(AS_TABLE1);
		addMethod(AS_TABLE2);
		addMethod(AS_TABLE3);
		addMethod(AS_FORMATTED_TABLE, "formattedTable");
		addMethod(AS_FORMATTED_TABLE2);
		addMethod(AS_FORMATTED_TABLE3);
		//		addMethod(this.requery);
		addMethod(SELECT_ROW);
		addMethod(SET_FILTER);
		addMethod(SET_FILTER2);
		addMethod(GET_FILTER);
		addMethod(GET_COLUMNS_WITH_FILTERS, "columnsWithFilters");
		addMethod(GET_COLUMNS_WTIH_SORT, "columnsWithSort");
		addMethod(GET_COLUMN_SORTING);
		addMethod(GET_VISIBLE_COLUMNS, "visibleColumns");
		addMethod(GET_HIDDEN_COLUMNS, "hiddenColumns");
		addMethod(MOVE_COLUMNS_TO);
		addMethod(GET_COLUMN_LOCATION);
		addMethod(SWAP_VISIBLE_COLUMN);
		addMethod(SORT);
		addMethod(RESET_COLUMNS);
		addMethod(SET_DEFAULT_WHERE);
		addMethod(RESET_WHERE);
		addMethod(GET_DEFAULT_WHERE, "defaultWhere");
		addMethod(SET_CURRENT_WHERE);
		addMethod(GET_CURRENT_WHERE, "currentWhere");
		addMethod(GET_COLUMN_FILTER);
		addMethod(SET_TITLE);
		addMethod(GET_TITLE, "tableTitle");
		addMethod(SET_DOWNLOAD_NAME);
		addMethod(GET_DOWNLOAD_NAME, "downloadName");
		addMethod(GET_SEARCH, "search");
		addMethod(SET_SEARCH);
		addMethod(START_EDIT);
		addMethod(IS_EDITING, "isEditing");
		addMethod(CANCEL_EDIT);
		addMethod(GET_COLUMN);
		addMethod(SET_VISIBLE_COLUMNS);
		addMethod(GET_VISIBLE_ROWS_COUNT);
		addMethod(AUTOSIZE_ALL_COLUMNS);
		addMethod(MEETS_WHERE_CLAUSE);
		addMethod(IS_VALID_WHERE_CLAUSE);
		addMethod(SELECT_ROWS_RANGE);
		addMethod(ENSURE_ROW_VISIBLE);
		addMethod(SET_HIDDEN_COLUMNS);
		addMethod(REMOVE_COLUMN);
		addMethod(REMOVE_TRANSIENT_COLUMNS);
		addMethod(ADD_COLUMN);
		addMethod(AUTOFIT_ALL_COLUMNS);
		addMethod(GET_SCROLL_POSITION);
		addMethod(SET_SCROLL_POSITION);
		registerCallbackDefinition(AmiWebAbstractTablePortlet.CALLBACK_DEF_ONCELLCLICKED);
		registerCallbackDefinition(AmiWebAbstractTablePortlet.CALLBACK_DEF_ONSELECTED);
		registerCallbackDefinition(AmiWebAbstractTablePortlet.CALLBACK_DEF_ONCOLUMNSARRANGED);
		registerCallbackDefinition(AmiWebAbstractTablePortlet.CALLBACK_DEF_ONCOLUMNSSIZED);
		registerCallbackDefinition(AmiWebAbstractTablePortlet.CALLBACK_DEF_ONEDIT);
		registerCallbackDefinition(AmiWebAbstractTablePortlet.CALLBACK_DEF_ONFILTERCHANGING);
		registerCallbackDefinition(AmiWebAbstractTablePortlet.CALLBACK_DEF_ONQUICKFILTERTYPING);
		registerCallbackDefinition(AmiWebAbstractTablePortlet.CALLBACK_DEF_ONSCROLL);
		registerCallbackDefinition(AmiWebAbstractTablePortlet.CALLBACK_DEF_ONBEFOREEDIT);
	}

	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> SET_SCROLL_POSITION = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "setScrollPosition", Boolean.class, WebPoint.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			WebPoint p = (WebPoint) params[0];
			targetObject.getTablePortlet().setScrollPosition(p);
			return true;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "Position" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {
					"Position is created by giving x and y position in the Point constuctor (Point p = new Point(100, 200)), with x being the horizontal and y being the vertical scrollbar position in pixels." };
		}
		@Override
		protected String getHelp() {
			return "Moves the vertical and horizontal scrollbar specified by the position. Use negative values (-1 recommended) to ignore scrolling for specific direction(s). Values larger than the upper limit will cause it to scroll all the way to the end.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> GET_SCROLL_POSITION = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "getScrollPosition", WebPoint.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTablePortlet().getScrollPosition();
		}

		@Override
		protected String getHelp() {
			return "Returns a Point object, with x indicating horizontal and y indicating vertical scrollbar position in pixels.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> IS_VALID_WHERE_CLAUSE = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "isValidWhereClause", Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String whereClause = Caster_String.INSTANCE.cast(params[0]);
			StringBuilder errorSink = new StringBuilder();
			targetObject.compileWhereFilter(whereClause, errorSink);
			return errorSink.length() == 0 ? true : false;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "whereClause" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "WHERE clause to be compiled agains this panel." };
		}
		@Override
		protected String getHelp() {
			return "Checks if the WHERE clause will successfully apply on this panel. Returns true if successful, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> MEETS_WHERE_CLAUSE = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "meetsWhereClause", Boolean.class, Map.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Map m = (Map) params[0];
			if (m == null)
				return false;
			return targetObject.meetsWhereFilter(DerivedHelper.toFrame(m));
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "row", };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "An object of type Map." };
		}
		@Override
		protected String getHelp() {
			return "Returns true if the row satisfies the WHERE clause defined by this panel, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> AUTOSIZE_ALL_COLUMNS = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "autosizeAllColumns", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				targetObject.getTablePortlet().autoSizeAllColumns();
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Autosizes all visible columns. Returns true if operation is successful, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> AUTOFIT_ALL_COLUMNS = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "autoFitAllColumns", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return targetObject.getTablePortlet().autoFitVisibleColumns();
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Auto fit all visible column widths until panel space is equally divided among them (In case of indivisibility, residual space is given to the last column). This has effect only if there is unused space in the panel. Returns true if column width has changed as a result, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> IS_EDITING = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(AmiWebAbstractTablePortlet.class,
			"isEditing", Boolean.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject.getEditMode() == AmiWebAbstractTablePortlet.EDIT_OFF)
				return false;
			if (targetObject.getTablePortlet() != null)
				return targetObject.getTablePortlet().isEditing();
			return false;

		}
		@Override
		protected String getHelp() {
			return "Returns if table is in edit mode.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> CANCEL_EDIT = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(AmiWebAbstractTablePortlet.class,
			"cancelEdit", Boolean.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject.getEditMode() == AmiWebAbstractTablePortlet.EDIT_OFF)
				return false;
			if (targetObject.getTablePortlet() != null)
				return targetObject.getTablePortlet().editAborted();
			return false;

		}
		@Override
		protected String getHelp() {
			return "Cancels table edit.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> START_EDIT = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(AmiWebAbstractTablePortlet.class,
			"startEdit", Boolean.class, false, Collection.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject.getEditMode() == AmiWebAbstractTablePortlet.EDIT_OFF)
				return false;
			if (targetObject.getTablePortlet() == null)
				return false;
			if (targetObject.getTablePortlet().isEditing())
				return false;
			Collection rowNumbers = (Collection) params[0];
			if (rowNumbers == null)
				return false;
			FastWebTable fwt = targetObject.getTable();
			ArrayList<Row> rows = new ArrayList<Row>();
			int rc = fwt.getRowsCount();
			for (Object rowNum : rowNumbers) {
				if (targetObject.getEditMode() == AmiWebAbstractTablePortlet.EDIT_SINGLE && rows.size() == 1)
					break;
				Integer r = Caster_Integer.PRIMITIVE.cast(rowNum);
				if (r == null || r >= rc)
					continue;
				rows.add(fwt.getRow(r));
			}
			if (rows.isEmpty())
				return false;
			if (targetObject.isEditingBlockedByOnBeforeEdit())
				return false;

			targetObject.startEditRows(rows);
			return true;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "rows", };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "List of row numbers, 0 indexed" };
		}
		@Override
		protected String getHelp() {
			return "Starts the editing of the table panel if not in edit mode already. Aborts editing if onBeforeEdit callback returns false.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> SET_SEARCH = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(AmiWebAbstractTablePortlet.class,
			"setSearch", Boolean.class, false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String searchVal = (String) params[0];
			if (SH.is(searchVal))
				targetObject.getTablePortlet().getTable().setSearch(SH.trim(searchVal));
			else
				targetObject.getTablePortlet().getTable().setSearch(null);
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "searchValue" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "The value to set in the search box of the table." };
		}
		@Override
		protected String getHelp() {
			return "Sets the value of the search box of the table. Empty or null value clears out the search.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> GET_SEARCH = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(AmiWebAbstractTablePortlet.class,
			"getSearch", String.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTablePortlet().getTable().getSearch();
		}
		@Override
		protected String getHelp() {
			return "Returns the value inside the search box of the table (null if empty).";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> SET_TITLE = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(AmiWebAbstractTablePortlet.class,
			"setTitle", Boolean.class, false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String title = (String) params[0];
			if (title == null) {
				return false;
			}
			targetObject.setAmiTitle(title, true);
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "title" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "title" };
		}
		@Override
		protected String getHelp() {
			return "Sets the title of the table with the given string.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> GET_TITLE = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(AmiWebAbstractTablePortlet.class,
			"getTitle", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTable().getTable().getTitle();
		}
		@Override
		protected String getHelp() {
			return "Returns the name of the table panel.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> SET_DOWNLOAD_NAME = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "setDownloadName", Boolean.class, false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String title = (String) params[0];
			if (SH.isnt(title))
				title = null;
			targetObject.getTablePortlet().setDownloadName(title);
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "name" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "name" };
		}
		@Override
		protected String getHelp() {
			return "Sets the file name of downloaded files (do not include extension) to the given string. If null, then the return value from getTitle() is used (see getTitle)";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> GET_DOWNLOAD_NAME = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "getDownloadName", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTablePortlet().getDownloadName();
		}
		@Override
		protected String getHelp() {
			return "Returns the name of files for download (null indicates title will be used instead).";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> CLEAR_ROWS = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(AmiWebAbstractTablePortlet.class,
			"clearRows", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				if (targetObject instanceof AmiWebDatasourceTablePortlet) {
					targetObject.clearRows();
					return true;
				} else
					return false;
			} catch (Exception e) {
				return false;
			}
		}
		@Override
		protected String getHelp() {
			return "Clears all rows in this table. Returns true on success, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> CLEAR_FILTERS = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "clearFilters", Boolean.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				Set<String> filteredInColumns = targetObject.getTable().getFilteredInColumns();
				targetObject.getTable().clearFilters(filteredInColumns);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		@Override
		protected String getHelp() {
			return "Clears all filters in this table. Returns true on success, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> GET_COLUMN_BY_ID = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "getColumnById", AmiWebCustomColumn.class, false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String id = Caster_String.INSTANCE.cast(params[0]);
				WebColumn col = SH.isnt(id) ? null : targetObject.getTable().getColumn(id);
				return col == null ? null : targetObject.getCustomDisplayColumn(col.getColumnId());
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String getHelp() {
			return "Returns the column given its column id.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "Id" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "The column id" };
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> SELECT_ROW = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(AmiWebAbstractTablePortlet.class,
			"selectRows", Boolean.class, Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Boolean shouldSelect = (Boolean) params[0];
			if (shouldSelect == null)
				return false;
			if (shouldSelect) {
				final int count = targetObject.getTable().getRowsCount();
				final int[] rows = new int[count];
				for (int i = 0; i < count; i++)
					rows[i] = i;
				targetObject.getTable().setSelectedRows(rows);
			} else {
				targetObject.getTable().setSelectedRows(OH.EMPTY_INT_ARRAY);
			}
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "shouldSelect" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "If true selects all otherwise deselects all" };
		}
		@Override
		protected String getHelp() {
			return "Selects or deselects all rows. Returns true on success, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> SET_FILTER = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(AmiWebAbstractTablePortlet.class,
			"setFilter", Boolean.class, true, String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String columnName = (String) params[0];
			WebColumn col = targetObject.findColumnByTitle(columnName);
			if (col == null) {
				warning(sf, "Columns not found for move", CH.m("Missing Columns", columnName), null);
				return false;
			}
			Set<String> filter = new HashSet<String>(params.length);
			for (int i = 1; i < params.length; i++) {
				filter.add((String) params[i]);
			}
			targetObject.getTable().setFilteredIn(col.getColumnId(), filter);
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "columnName", "values" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "name of the column to set a filter for", "values to filter in" };
		}
		@Override
		protected String getHelp() {
			return "DEPRECATED... use the setFilter() under TablePanelColumn. Sets or clears the filter. Clears the filter if you only pass in the column name. Sets the filter if you also pass in values. Returns true on success, false if the column not found.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> SET_FILTER2 = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(AmiWebAbstractTablePortlet.class,
			"setFilter", Boolean.class, false, String.class, Boolean.class, Boolean.class, List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String columnName = (String) params[0];
			boolean keep = Boolean.TRUE.equals((Boolean) params[1]);
			boolean isPattern = Boolean.TRUE.equals((Boolean) params[2]);
			Collection values = (Collection) params[3];
			if (values == null) {
				warning(sf, "filter values cannot be null", CH.m("filter value", values));
				return false;
			}

			Set<String> filter = new HashSet<String>(params.length);
			boolean includeNull = false;
			for (Object o : values) {
				if (o == null)
					includeNull = true;
				else
					filter.add(AmiUtils.s(o));
			}
			WebColumn col = targetObject.findColumnByTitle(columnName);
			if (col == null) {
				warning(sf, "Columns not found for move", CH.m("Missing Columns", columnName));
				return false;
			}
			targetObject.getTable().setFilteredIn((String) col.getColumnId(), filter, keep, includeNull, isPattern, null, null);
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "columnName", "isKeep", "isPattern", "values" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "name of the column to set a filter for", "true=keep those that match,false=hide those that match", "values are patterns",
					"values to filter in" };
		}
		@Override
		protected String getHelp() {
			return "DEPRECATED... use the setFilter() under TablePanelColumn. Sets or clears a filter using the supplied column name and filter values. If value is empty the filter is cleared. Returns true on success, false if the column not found.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> SORT = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(AmiWebAbstractTablePortlet.class,
			"sort", Boolean.class, true, Boolean.class, String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			boolean keepSorting = Boolean.TRUE.equals((Boolean) params[0]);
			String descendingStr = (String) params[1];
			final List<String> missingColumns = new ArrayList<String>();
			final FastWebTable table = targetObject.getTable();
			boolean first = true;
			boolean descending = false;
			for (int i = 2; i < params.length; i++) {
				final WebColumn col = targetObject.findColumnByTitle((String) params[i]);
				if (col != null) {
					int pos = i - 2;
					if (descendingStr != null && descendingStr.length() > pos)
						descending = 'D' == Character.toUpperCase(descendingStr.charAt(pos));
					table.sortRows(col.getColumnId(), !descending, keepSorting, !first);
					first = false;
				} else
					missingColumns.add((String) params[i]);
			}
			if (missingColumns.size() > 0)
				warning(sf, "Columns not found for move", CH.m("Missing Column Names", missingColumns));
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "keepSorting", "AorD", "ColumnNames" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "keepSorting", "AorD", "ColumnNames" };
		}
		@Override
		protected String getHelp() {
			return "Sets the sort.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> GET_FILTER = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(AmiWebAbstractTablePortlet.class,
			"getFilter", List.class, false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String columnName = (String) params[0];
			WebColumn col = targetObject.findColumnByTitle(columnName);
			WebTableFilteredInFilter t = col == null ? null : targetObject.getTable().getFiltererdIn((String) col.getColumnId());
			return t == null ? null : new ArrayList<String>(t.getValues());
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "columnName" };
		}
		@Override
		protected String getHelp() {
			return "Returns a list of the string values that are in a filter for a given column, or null if the column is not filtered or missing.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> GET_COLUMN_FILTER = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "getColumnFilter", WebTableFilteredInFilter.class, false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String columnName = (String) params[0];
			WebColumn col = targetObject.findColumnByTitle(columnName);
			if (col == null)
				return null;
			WebTableFilteredInFilter t = targetObject.getTable().getFiltererdIn((String) col.getColumnId());
			return t;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "columnName" };
		}
		@Override
		protected String getHelp() {
			return "Returns the filter for a given column, or null if the column is not filtered or missing.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> GET_COLUMNS_WITH_FILTERS = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "getColumnsFilter", List.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			FastWebTable table = targetObject.getTable();
			Set<String> t = table.getFilteredInColumns();
			List<String> t2 = new ArrayList<String>(t.size());
			for (String s : t)
				t2.add(table.getColumn(s).getColumnName());
			return t2;
		}
		@Override
		protected String getHelp() {
			return "Return a list of the names of the columns that have filters.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> GET_COLUMNS_WTIH_SORT = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "getColumnsWithSort", List.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			FastWebTable table = targetObject.getTable();
			Set<String> t = table.getSortedColumnIds();
			List<String> t2 = new ArrayList<String>(t.size());
			for (String s : t)
				t2.add(table.getColumn(s).getColumnName());
			return t2;
		}
		@Override
		protected String getHelp() {
			return "Return a list of the names of the columns that have sorts.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> GET_SELECTED_ROWS = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "getSelectedRows", List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				if (targetObject instanceof AmiWebAggregateObjectTablePortlet) {
					Table t = asTable(targetObject, true, false, false, true, true, false, null);
					return new ArrayList(t.getRows());
				} else {
					List<Row> r = targetObject.getTable().getSelectedRows();
					if (r == null) {
						return new ArrayList();
					} else {
						BasicTable newTable = new BasicTable(targetObject.getTable().getTable().getColumns());
						for (int i = 0; i < r.size(); i++) {
							newTable.getRows().addRow(r.get(i).getValuesCloned());
						}
						if (newTable.getColumnsMap().containsKey("D"))
							newTable.removeColumn("D");
						if (newTable.getColumnsMap().containsKey("!params"))
							newTable.removeColumn("!params");
						return new ArrayList(newTable.getRows());
					}
				}
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String getHelp() {
			return "Deprecated, use asTable(\"SELECTED\",....) instead. Returns a list of the rows of this panel selected by the user.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> GET_SELECTED_VALUES = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "getSelectedValues", Table.class, true, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject instanceof AmiWebAggregateObjectTablePortlet) {
				Set<String> columns = new LinkedHasherSet<String>();
				for (Object i : params)
					columns.add(AmiUtils.s(i));
				Table t = asTable(targetObject, true, false, false, true, true, false, columns);
				return new ArrayList(t.getRows());
			} else {
				BasicTable r = new BasicTable();
				FastWebTable ftable = targetObject.getTable();
				SmartTable table = ftable.getTable();

				int pos[] = new int[params.length];
				for (int i = 0; i < pos.length; i++) {
					String id = (String) params[i];
					Column col = table.getColumnsMap().get(id);
					if (col == null)
						throw new RuntimeException("Column not found: " + id);
					r.addColumn(col.getType(), id);
					pos[i] = col.getLocation();
				}
				List<Row> selected = ftable.getSelectedRows();
				for (Row row : selected) {
					Row newRow = r.newEmptyRow();
					for (int i = 0; i < pos.length; i++)
						newRow.putAt(i, row.getAt(pos[i]));
					r.getRows().add(newRow);
				}
				r.setTitle(table.getTitle());
				return r;
			}
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "columnNames" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "names of the columns to include values for" };
		}
		@Override
		protected String getHelp() {
			return "Deprecated, use asTable(\"SELECTED\",....) instead. Returns a table whose rows are those that are selected, columns are those that are specified in the argument.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> AS_TABLE = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(AmiWebAbstractTablePortlet.class,
			"asTable", BasicTable.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return asTable(targetObject, true, true, false, true, false, false, null);
		}

		@Override
		protected String getHelp() {
			return "Copies and returns the values of this panel as a table.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> AS_TABLE1 = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(AmiWebAbstractTablePortlet.class,
			"asTable", BasicTable.class, String.class, Set.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String options = (String) params[0];
			Set cols = (Set) params[1];
			if (SH.isnt(options))
				return asTable(targetObject, true, true, true, true, true, true, cols);
			String[] parts = SH.split(',', options);
			boolean selected = false, unselected = false, filtered = false, visible = false, hidden = false, format = false;
			for (String part : parts) {
				part = SH.trim(part);
				if ("SELECTED".equalsIgnoreCase(part))
					selected = true;
				else if ("UNSELECTED".equalsIgnoreCase(part))
					unselected = true;
				else if ("FILTERED".equalsIgnoreCase(part))
					filtered = true;
				else if ("VISIBLE".equalsIgnoreCase(part))
					visible = true;
				else if ("HIDDEN".equalsIgnoreCase(part))
					hidden = true;
				else if ("FORMAT".equalsIgnoreCase(part))
					format = true;
			}
			if (!selected && !unselected && !filtered)
				selected = unselected = filtered = true;
			if (!visible && !hidden)
				visible = hidden = true;
			return asTable(targetObject, selected, unselected, filtered, visible, hidden, format, cols);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "options", "columns" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "A comma delimited combination of: SELECTED,UNSELECTED,FILTERED,VISIBLE,HIDDEN,FORMAT",
					"Set of column names to include, if null all columns are included" };
		}

		@Override
		protected String getHelp() {
			return "Copies and returns the values of this panel as a table given the options and a list of columns to include.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> AS_TABLE2 = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(AmiWebAbstractTablePortlet.class,
			"asTable", BasicTable.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			boolean selectedRows = Boolean.TRUE.equals(params[0]);
			boolean unselectedRows = Boolean.TRUE.equals(params[1]);
			boolean filteredRows = Boolean.TRUE.equals(params[2]);
			boolean visibleColumns = Boolean.TRUE.equals(params[3]);
			boolean hiddenColumns = Boolean.TRUE.equals(params[4]);
			return asTable(targetObject, selectedRows, unselectedRows, filteredRows, visibleColumns, hiddenColumns, false, null);
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "selectedRows", "unselectedRows", "filteredRows", "visibleColumns", "hiddenColumns" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "include selected Rows", "include unselected Rows", "include filtered Rows", "include Visible Columns", "include Hidden Columns" };
		}

		@Override
		protected String getHelp() {
			return "Copies and returns the values of this panel as a table, with the options to predefine which rows to be selected/unselected/visible/hidden.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> AS_TABLE3 = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(AmiWebAbstractTablePortlet.class,
			"asTable", BasicTable.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Set.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			boolean selectedRows = Boolean.TRUE.equals(params[0]);
			boolean unselectedRows = Boolean.TRUE.equals(params[1]);
			boolean filteredRows = Boolean.TRUE.equals(params[2]);
			boolean visibleColumns = Boolean.TRUE.equals(params[3]);
			boolean hiddenColumns = Boolean.TRUE.equals(params[4]);
			Set<String> includeColumns = (Set) params[5];
			return asTable(targetObject, selectedRows, unselectedRows, filteredRows, visibleColumns, hiddenColumns, false, includeColumns);
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "selectedRows", "unselectedRows", "filteredRows", "visibleColumns", "hiddenColumns", "columns" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Include selected Rows", "include unselected Rows", "include filtered Rows", "include Visible Columns", "Include Hidden Columns",
					"Set of column names to include" };
		}

		@Override
		protected String getHelp() {
			return "Copies and returns the values of this panel as a table, with the options to predefine which rows to be selected/unselected/visible/hidden and define columns to include.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> AS_FORMATTED_TABLE2 = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "asFormattedTable", BasicTable.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			boolean selectedRows = Boolean.TRUE.equals(params[0]);
			boolean unselectedRows = Boolean.TRUE.equals(params[1]);
			boolean filteredRows = Boolean.TRUE.equals(params[2]);
			boolean visibleColumns = Boolean.TRUE.equals(params[3]);
			boolean hiddenColumns = Boolean.TRUE.equals(params[4]);
			return asTable(targetObject, selectedRows, unselectedRows, filteredRows, visibleColumns, hiddenColumns, true, null);
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "selectedRows", "unselectedRows", "filteredRows", "visibleColumns", "hiddenColumns" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Include selected Rows", "include unselected Rows", "include filtered Rows", "include Visible Columns", "Include Hidden Columns" };
		}

		@Override
		protected String getHelp() {
			return "Copies and returns the values of this panel as a table, with cells formatted.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> AS_FORMATTED_TABLE3 = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "asFormattedTable", BasicTable.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Set.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			boolean selectedRows = Boolean.TRUE.equals(params[0]);
			boolean unselectedRows = Boolean.TRUE.equals(params[1]);
			boolean filteredRows = Boolean.TRUE.equals(params[2]);
			boolean visibleColumns = Boolean.TRUE.equals(params[3]);
			boolean hiddenColumns = Boolean.TRUE.equals(params[4]);
			Set<String> includeColumns = (Set) params[5];
			return asTable(targetObject, selectedRows, unselectedRows, filteredRows, visibleColumns, hiddenColumns, true, includeColumns);
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "selectedRows", "unselectedRows", "filteredRows", "visibleColumns", "hiddenColumns", "columns" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Include selected Rows", "include unselected Rows", "include filtered Rows", "include Visible Columns", "Include Hidden Columns",
					"Set of column names to include" };
		}

		@Override
		protected String getHelp() {
			return "Copies and returns the values of this panel as a table, with cells formatted.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> AS_FORMATTED_TABLE = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "asFormattedTable", BasicTable.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return asTable(targetObject, true, true, false, true, false, true, null);
		}

		@Override
		protected String getHelp() {
			return "Copies and returns the values of this panel as a table.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> GET_VISIBLE_COLUMNS = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "getVisibleColumns", List.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			FastWebTable table = targetObject.getTable();
			int count = table.getVisibleColumnsCount();
			List<String> r = new ArrayList<String>(count);
			for (int i = 0; i < count; i++)
				r.add(table.getVisibleColumn(i).getColumnName());
			return r;
		}
		@Override
		protected String getHelp() {
			return "Return a list of the names of the columns that are visible, from left to right.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> GET_HIDDEN_COLUMNS = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "getHiddenColumns", List.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			FastWebTable table = targetObject.getTable();
			int count = table.getHiddenColumnsCount();
			List<String> r = new ArrayList<String>(count);
			for (int i = 0; i < count; i++)
				r.add(table.getHiddenColumn(i).getColumnName());
			return r;
		}
		@Override
		protected String getHelp() {
			return "Return a list of the names of the columns that are hidden.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> MOVE_COLUMNS_TO = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "moveColumnsTo", Object.class, true, Integer.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			int position = (Integer) params[0];
			final List<String> missingColumns = new ArrayList<String>();
			final boolean isHide = position < 0;
			final FastWebTable table = targetObject.getTable();
			for (int i = 1; i < params.length; i++) {
				final WebColumn col = targetObject.findColumnByTitle((String) params[i]);
				if (col != null) {
					if (isHide) {
						if (table.getVisibleColumnsCount() > 0)
							table.hideColumn(col.getColumnId());
						continue;
					}
					position = Math.min(position, table.getVisibleColumnsCount());
					int t = table.getColumnPosition(col.getColumnId());
					if (t != -1 && t < position)
						position--;
					table.showColumn(col.getColumnId(), position);
					position++;
				} else
					missingColumns.add((String) params[i]);
			}
			if (missingColumns.size() > 0)
				warning(sf, "Columns not found for move", CH.m("Missing Column Names", missingColumns), null);
			return null;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "pos", "columnNames" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "zero-indexed left offset Position of the first column, or -1 to hide all columns", "name of the columns to move, from left to right" };
		}
		@Override
		protected String getHelp() {
			return "Moves a list of columns to a particular position in the table, or hide them.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> GET_COLUMN_LOCATION = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "getColumnLocation", Integer.class, false, String.class) {
		@Override
		public Integer invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];
			WebColumn col = targetObject.findColumnByTitle((String) name);
			if (col == null) {
				warning(sf, "Columns not found", CH.m("Missing Column Name", name), null);
				return -2;
			} else {
				return targetObject.getTable().getColumnPosition(col.getColumnId());
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "columnName" };
		}

		@Override
		protected String getHelp() {
			return "Returns the zero-indexed left based position of the column, -1 if hidden, -2 if it doesn't exist.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> GET_COLUMN_SORTING = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "getColumnSorting", String.class, true, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];
			WebColumn col = targetObject.findColumnByTitle((String) name);
			if (col == null) {
				warning(sf, "Columns not found", CH.m("Missing Column Name", name));
				return null;
			} else {
				Iterable<Entry<String, Boolean>> t = targetObject.getTable().getSortedColumns();
				if (t != null) {
					for (Entry<String, Boolean> i : t) {
						if (OH.eq(col.getColumnId(), i.getKey()))
							return i.getValue() ? "D" : "A";
					}
				}
			}
			return null;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "columnName" };
		}

		@Override
		protected String getHelp() {
			return "Returns the sort, returns true=ascending, false=descending, null=no sort or column not found.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> SWAP_VISIBLE_COLUMN = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "swapVisibleColumn", Object.class, false, String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			final String visible = (String) params[0];
			final String hidden = (String) params[1];
			final WebColumn vCol = targetObject.findColumnByTitle((String) visible);
			final WebColumn hCol = targetObject.findColumnByTitle((String) hidden);
			final FastWebTable table = targetObject.getTable();
			if (vCol == null || hCol == null) {
				ArrayList<String> missing = new ArrayList<String>();
				if (vCol == null)
					missing.add(visible);
				if (hCol == null)
					missing.add(hidden);
				warning(sf, "Columns not found for swap", CH.m("Missing Columns", missing));
				return false;
			}
			int visiblePosition = table.getColumnPosition(vCol.getColumnId());
			int hiddenPosition = table.getColumnPosition(hCol.getColumnId());
			if (visiblePosition == -1 || hiddenPosition != -1)
				return false;
			hCol.setWidth(vCol.getWidth());
			table.showColumn(hCol.getColumnId(), visiblePosition);
			table.hideColumn(vCol.getColumnId());
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "visible", "hidden" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "visible column to hide", "hidden column to show" };
		}
		@Override
		protected String getHelp() {
			return "Replaces a visible column with a currently hidden one, effectively hiding the currently visible one. Returns true if the column arrangement changed as a result, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> CALL_COMMAND_ON_SELECTED = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "callCommandOnSelected", Boolean.class, String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String appName = (String) params[0];
			String cmdId = (String) params[1];
			return targetObject.callCommandOnSelected(appName, cmdId);
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "applicationId", "cmdId" };
		}

		@Override
		protected String getHelp() {
			return "Calls the given command on the currently user-selected rows.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	// this one is not ready
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> requery = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(AmiWebAbstractTablePortlet.class,
			"requery", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				if (targetObject instanceof AmiWebDatasourceTablePortlet) {
					throw new ToDoException();
				} else
					return false;
			} catch (Exception e) {
				return false;
			}
		}
		@Override
		protected String getHelp() {
			return "Clear the user's selection";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> RESET_COLUMNS = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "resetColumns", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.applyUserPref(targetObject.getDefaultPref());
			return true;
		}
		@Override
		protected String getHelp() {
			return "Resets all columns to their default locations.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	@Override
	public String getVarTypeName() {
		return "TablePanel";
	}
	@Override
	public String getVarTypeDescription() {
		return "A visualization Panel of type Table (static or realtime)";
	}
	@Override
	public Class<AmiWebAbstractTablePortlet> getVarType() {
		return AmiWebAbstractTablePortlet.class;
	}
	@Override
	public Class<AmiWebAbstractTablePortlet> getVarDefaultImpl() {
		return null;
	}

	private static Table asTable(AmiWebAbstractTablePortlet targetObject, boolean selectedRows, boolean unselectedRows, boolean filteredRows, boolean visibleColumns,
			boolean hiddenColumns, boolean format, Set<String> columnsToInclude) {
		FastWebTable ftable = targetObject.getTable();
		SmartTable table = ftable.getTable();
		table.ensureUpToDateAndGetStatus();
		List<WebColumn> colsW = new ArrayList<WebColumn>();
		if (visibleColumns)
			for (int i = 0; i < ftable.getVisibleColumnsCount(); i++) {
				WebColumn c = ftable.getVisibleColumn(i);
				if (columnsToInclude != null && !columnsToInclude.contains(c.getColumnName()))
					continue;
				if ("Params".equals(c.getColumnName()))
					continue;
				colsW.add(c);
			}
		if (hiddenColumns)
			for (int i = 0; i < ftable.getHiddenColumnsCount(); i++) {
				WebColumn c = ftable.getHiddenColumn(i);
				if (columnsToInclude != null && !columnsToInclude.contains(c.getColumnName()))
					continue;
				if ("Params".equals(c.getColumnName()))
					continue;
				colsW.add(c);
			}
		int colsCount = colsW.size();
		if (colsCount == 0)
			return new BasicTable();
		Column[] colsI = new Column[colsCount];
		Column[] colsO = new Column[colsCount];

		for (int i = 0; i < colsCount; i++) {
			WebColumn c = colsW.get(i);
			int colId = c.getTableColumnLocations()[0];
			colsI[i] = table.getColumnAt(colId);
			colsO[i] = new BasicColumn(format ? String.class : colsI[i].getType(), c.getColumnName());
		}
		BasicTable r = new BasicTable(colsO);
		List<Row> rows;

		if (selectedRows && unselectedRows) {
			rows = ftable.getRows();
		} else if (selectedRows) {
			rows = ftable.getSelectedRows();
		} else if (unselectedRows) {
			List<Row> rowsSel = ftable.getSelectedRows();
			if (rowsSel.isEmpty()) {
				rows = ftable.getRows();
			} else {
				List<Row> rowsAll = ftable.getRows();
				rows = new ArrayList<Row>(rowsAll.size() - rowsSel.size());
				Set<Row> rowsSelSet = new IdentityHashSet<Row>(rowsSel);
				for (Row row : rowsAll)
					if (!rowsSelSet.contains(row))
						rows.add(row);
			}
		} else {
			rows = Collections.EMPTY_LIST;
		}

		if (filteredRows) {
			Iterator<Row> filtered = ftable.getFilteredRows().iterator();
			if (filtered.hasNext()) {
				rows = new ArrayList<Row>(rows);
				while (filtered.hasNext())
					rows.add(filtered.next());
			}
		}
		if (format) {
			for (Row row : rows) {
				Object values[] = new Object[colsO.length];
				for (int i = 0; i < colsCount; i++)
					values[i] = colsW.get(i).getCellFormatter().formatCellToText(row.getAt(colsW.get(i).getTableColumnLocations()[0]));
				r.getRows().addRow(values);
			}
		} else {
			for (Row row : rows) {
				Object values[] = new Object[colsO.length];
				for (int i = 0; i < colsCount; i++)
					values[i] = row.getAt(colsI[i].getLocation());
				r.getRows().addRow(values);
			}
		}
		r.setTitle(table.getTitle());
		return r;
	}

	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> SET_DEFAULT_WHERE = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "setDefaultWhere", Boolean.class, false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String filter = (String) params[0];
			StringBuilder sb = new StringBuilder();
			targetObject.compileWhereFilter(filter, sb);
			if (sb.length() > 0) {
				warning(sf, "Compilation of filter failed ", CH.m("Error", sb.toString(), "Filter Expression", filter));
				return false;
			}
			targetObject.setCurrentRuntimeFilter(filter, false);
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "booleanExpression" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "An expression to filter incoming data on, evaluating to true indicates the data is displayed, false means filtered out" };
		}
		@Override
		protected String getHelp() {
			return "Sets the default WHERE filter that will be applied on login. Returns true on success, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> GET_DEFAULT_WHERE = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "getDefaultWhere", String.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getDefaultWhereFilter();
		}
		@Override
		protected String getHelp() {
			return "Returns the currently applied filter.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> SET_CURRENT_WHERE = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "setCurrentWhere", Boolean.class, false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String filter = (String) params[0];
			com.f1.base.CalcTypes vars = AmiWebUtils.getAvailableVariables(targetObject.getService(), targetObject);
			vars = targetObject.getUnderlyingVarTypes();
			StringBuilder sb = new StringBuilder();
			WhereClause fm = targetObject.compileWhereFilter(filter, sb);
			if (sb.length() > 0) {
				warning(sf, "Compilation of filter failed ", CH.m("Error", sb.toString(), "Filter Expression", filter));
				return false;
			}
			targetObject.setCurrentRuntimeFilter(filter, true);
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "booleanExpression" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "An expression to filter incoming data on, evaluating to true indicates the data is displayed, false means filtered out" };
		}
		@Override
		protected String getHelp() {
			return "Applies a filter on the underlying data for this table panel. If the filter is null or an empty string then no filter is applied and all data is shown. Returns true on success, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> GET_CURRENT_WHERE = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "getCurrentWhere", String.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getCurrentRuntimeFilter();
		}
		@Override
		protected String getHelp() {
			return "Returns the currently applied filter.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> RESET_WHERE = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(AmiWebAbstractTablePortlet.class,
			"resetWhere", Boolean.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.resetWhere();
		}
		@Override
		protected String getHelp() {
			return "Resets the current WHERE filter to the default.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> GET_COLUMN = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(AmiWebAbstractTablePortlet.class,
			"getColumn", AmiWebCustomColumn.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String colName = Caster_String.INSTANCE.cast(params[0]);
			WebColumn col = SH.isnt(colName) ? null : targetObject.findColumnByTitle(colName);
			return col == null ? null : targetObject.getCustomDisplayColumn(col.getColumnId());
		}

		@Override
		protected String getHelp() {
			return "Returns the column with the given column name, which is the Title under Column Header in the Column editor.";
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "columnName" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Name of the column." };
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> SET_VISIBLE_COLUMNS = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "setVisibleColumns", Object.class, List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			List<?> columns = (List) params[0];
			if (columns == null)
				return null;
			List<String> columnIds = new ArrayList<String>();
			for (Object s : columns) {
				if (s != null) {
					WebColumn col = targetObject.findColumnByTitle(AmiUtils.s(s));
					if (col != null)
						columnIds.add(col.getColumnId());
				}
			}
			ArrangeColumnsPortlet.arrange(targetObject.getTable(), columnIds);
			return null;
			//			targetObject.getTable().hideColumn(columnId);
			//
			//			return SH.isnt(colName) ? null : targetObject.findColumnByTitle(colName);
		}

		@Override
		protected String getHelp() {
			return "Specifies a particular list of columns to be visible.";
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "columnName" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "A list containing the name of the columns." };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> GET_VISIBLE_ROWS_COUNT = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "getVisibleRowsCount", Integer.class) {
		@Override
		public Integer invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTable().getRowsCount();
		}

		@Override
		protected String getHelp() {
			return "Returns the number of visible rows.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> SET_HIDDEN_COLUMNS = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "setHiddenColumns", Object.class, List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			List<?> columns = (List) params[0];
			if (columns == null)
				return null;
			List<String> toHide = new ArrayList<String>();

			// get current visible columns
			List<String> visibleColumns = ArrangeColumnsPortlet.getVisibleColumns(targetObject.getTable());
			// convert to set to capitalize on Theta(1) search/remove
			Set<String> visibleSet = new HashSet<>(visibleColumns);
			List<String> toShow = new ArrayList<String>();
			// validate input
			for (Object s : columns) {
				if (s != null) {
					WebColumn col = targetObject.findColumnByTitle(AmiUtils.s(s));
					if (col != null)
						toHide.add(col.getColumnId());
				}
			}
			// remove the to-hide columns from visible columns
			for (String s : toHide) {
				visibleSet.remove(s);
			}
			toShow.addAll(visibleSet);
			ArrangeColumnsPortlet.arrange(targetObject.getTable(), toShow);
			return null;
		}

		@Override
		protected String getHelp() {
			return "Specifies a particular list of columns to be hidden.";
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "columnName" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "A list containing the name of the columns." };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> SELECT_ROWS_RANGE = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "selectRowsRange", Boolean.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String selectedRowsText = Caster_String.INSTANCE.cast(params[0]);
			if (selectedRowsText.contains(",") && selectedRowsText.contains("-"))
				return false;
			targetObject.getTable().setSelectedRows(selectedRowsText);
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "selection" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Comma delimited row indeces OR a range (selectRows(\\\"0,1,3,5\\\") OR selectRows(\\\"5-10\\\"))." };
		}
		@Override
		protected String getHelp() {
			return "Overrides the currently selected rows with the supplied range of rows. If there is no currently selected rows, this method simply sets selected rows. Otherwise this will de-selected any previously selected rows then sets selected rows.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> ENSURE_ROW_VISIBLE = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "ensureRowVisible", Boolean.class, Integer.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer rowToSnap = Caster_Integer.INSTANCE.cast(params[0]);
			String snapDirection = Caster_String.INSTANCE.cast(params[1]);
			byte align = -1;
			if (SH.equalsIgnoreCase("top", snapDirection))
				align = FastWebTable.SNAP_ALIGN_TOP;
			else if (SH.equalsIgnoreCase("bottom", snapDirection))
				align = FastWebTable.SNAP_ALIGN_BOTTOM;
			else {
				warning(sf, "Invalid snap alignment: " + snapDirection + ". Accepted values: TOP / BOTTOM", null);
				return false;
			}
			targetObject.getTable().snapToRowWithAlign(rowToSnap, align);
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "rowNumber", "alignment" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Row number to snap to", "Alignment of snap = \"TOP\" OR \"BOTTOM\"" };
		}
		@Override
		protected String getHelp() {
			return "Snaps the table panel to the row number in the indicated snap alignment.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> REMOVE_COLUMN = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "removeColumn", Boolean.class, false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String id = (String) params[0];
			if (id == null || targetObject.getTable().getColumnIds().contains(id) == false)
				return false;
			if (targetObject.getCustomDisplayColumn(id).isTransient() == false)
				return false;
			targetObject.getTable().removeColumn(id);
			targetObject.getTable().getTable().removeColumn(id);
			targetObject.removeCustomDisplayColumn(id);
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "column id" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "column id" };
		}
		@Override
		protected String getHelp() {
			return "Removes a transient column with the given column id";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> REMOVE_TRANSIENT_COLUMNS = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(
			AmiWebAbstractTablePortlet.class, "removeTransientColumns", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Set<String> transientIds = new HashSet<String>();
			for (String id : targetObject.getCustomDisplayColumnIds()) {
				if (targetObject.getCustomDisplayColumn(id).isTransient() == true) {
					transientIds.add(id);
				}
			}

			for (String colId : transientIds) {
				targetObject.getTable().removeColumn(colId);
				targetObject.getTable().getTable().removeColumn(colId);
				targetObject.removeCustomDisplayColumn(colId);
			}
			return true;
		}

		@Override
		protected String getHelp() {
			return "Removes all transient columns from a table panel";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebAbstractTablePortlet> ADD_COLUMN = new AmiAbstractMemberMethod<AmiWebAbstractTablePortlet>(AmiWebAbstractTablePortlet.class,
			"addColumn", Boolean.class, false, String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAbstractTablePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String id = (String) params[0];
			if (id == null || targetObject.getTable().getColumnIds().contains(id) == true || targetObject.getCustomDisplayColumnIds().contains(id) == true) {
				return false;
			}
			String type = (String) params[1];
			if (!AmiWebUtils.CUSTOM_COL_NAMES.containsValue(type)) {
				return false;
			}
			Byte b = AmiWebUtils.CUSTOM_COL_NAMES.getKey(type);
			AmiWebFormatterManager fm = targetObject.getService().getFormatterManager();
			Column col2 = targetObject.getTable().getTable().addColumn(String.class, id);
			BasicWebColumn col = targetObject.getTable().addColumn(true, (String) col2.getId(), col2.getId(), fm.getBasicFormatter());
			targetObject.getTable().showColumn((String) col.getColumnId());
			targetObject.createCustomCol((String) col2.getId());
			targetObject.getCustomDisplayColumn((String) col2.getId()).setType(b, false);
			return true;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "column id", "column type" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Column ID", "type of column" };
		}
		@Override
		protected String getHelp() {
			return "Creates a transient column with the given column id. Acceptable types are text, numeric, progress, time, date, price, percent, html, json, image, checkbox, Masked, spark, time_sec,time_millis, time_micros, time_nanos, datetime, datetime_sec, datetime_millis, datetime_micros, datetime_nanos";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	public final static AmiWebScriptMemberMethods_TablePanel INSTANCE = new AmiWebScriptMemberMethods_TablePanel();
}
