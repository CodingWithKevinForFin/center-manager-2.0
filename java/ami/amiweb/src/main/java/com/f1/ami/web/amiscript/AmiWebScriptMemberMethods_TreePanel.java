package com.f1.ami.web.amiscript;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.tree.AmiWebTreeColumn;
import com.f1.ami.web.tree.AmiWebTreeGroupBy;
import com.f1.ami.web.tree.AmiWebTreePortlet;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.WebTreeNodeFormatter;
import com.f1.suite.web.tree.impl.BasicWebTreeManager;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.IntSet;
import com.f1.utils.structs.table.BasicColumn;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_TreePanel extends AmiWebScriptBaseMemberMethods<AmiWebTreePortlet> {

	private AmiWebScriptMemberMethods_TreePanel() {
		super();

		addMethod(GET_COLUMN);
		addMethod(GET_COLUMN_IDS);
		addMethod(GET_COLUMN_SORTING);
		addMethod(GET_COLUMN_BY_ID);
		addMethod(GET_GROUPING);
		addMethod(HIDE_COLUMN);
		addMethod(HIDE_COLUMNS2);
		addMethod(HIDE_COLUMNS_BY_ID);
		addMethod(GET_VISIBLE_COLUMNS_COUNT);
		addMethod(GET_HIDDEN_COLUMNS_COUNT);
		addMethod(GET_SELECTED_ROWS, "selectedRows");
		addMethod(SELECT_ROWS);
		addMethod(SORT);
		addMethod(SORT2);
		addMethod(CLEAR_SORT);
		addMethod(GET_SEARCH, "search");
		addMethod(SET_SEARCH);
		addMethod(CLEAR_FILTER);
		addMethod(GET_ROWS, "data");
		addMethod(CLEAR_SELECTED_ROWS);
		addMethod(GET_COLUMNS_WITH_SORT);
		addMethod(GET_COLUMN_LOCATION);
		addMethod(MOVE_COLUMNS_TO);
		addMethod(GET_COLUMNS_WITH_FILTERS, "columnsWithFilters");
		addMethod(ADD_GROUPING);
		addMethod(ADD_GROUPING_AT);
		addMethod(REMOVE_GROUPING_AT);
		addMethod(REMOVE_GROUPING);
		addMethod(GET_GROUPINGS);
		addMethod(ADD_COLUMN);
		addMethod(ADD_COLUMN2);
		addMethod(REMOVE_COLUMN);
		addMethod(AS_TABLE, "table");
		addMethod(AS_TABLE_2);
		addMethod(AS_TABLE_3);
		addMethod(AS_TABLE_4);
		addMethod(AS_FORMATTED_TABLE, "formattedTable");
		addMethod(AS_FORMATTED_TABLE2);
		addMethod(AS_FORMATTED_TABLE3);
		addMethod(RESET_WHERE);
		addMethod(GET_DEFAULT_WHERE, "defaultWhere");
		addMethod(SET_CURRENT_WHERE);
		addMethod(GET_CURRENT_WHERE, "currentWhere");
		addMethod(EXPAND_SELECTED_NODES);
		registerCallbackDefinition(AmiWebTreePortlet.CALLBACK_DEF_ONSELECTED);
		registerCallbackDefinition(AmiWebTreePortlet.CALLBACK_DEF_ONCOLUMNSARRANGED);
		registerCallbackDefinition(AmiWebTreePortlet.CALLBACK_DEF_ONCOLUMNSSIZED);
		registerCallbackDefinition(AmiWebTreePortlet.CALLBACK_DEF_ONFILTERCHANGING);
	}

	@Override
	public String getVarTypeName() {
		return "TreePanel";
	}

	@Override
	public String getVarTypeDescription() {
		return "Panel for Tree Visualization.";
	}

	@Override
	public Class<AmiWebTreePortlet> getVarType() {
		return AmiWebTreePortlet.class;
	}

	@Override
	public Class<AmiWebTreePortlet> getVarDefaultImpl() {
		return null;
	}

	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> GET_GROUPINGS = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "getGroupings",
			List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			List<AmiWebTreeGroupBy> groupings = new ArrayList<AmiWebTreeGroupBy>(targetObject.getGroupbyFormulas().getSize());
			groupings.addAll(targetObject.getGroupbyFormulas().valueList());
			return groupings;
		}

		@Override
		protected String getHelp() {
			return "returns a list of TreePanelGrouping objects.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};

	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> GET_COLUMN_IDS = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "getColumnIds",
			Set.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return new LinkedHashSet<String>(targetObject.getColumnAmiIds());
		}

		@Override
		protected String getHelp() {
			return "returns a list of TreePanelColumn ids";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};

	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> ADD_GROUPING = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "addGrouping",
			Boolean.class, String.class, String.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String grouping = Caster_String.INSTANCE.cast(params[0]);
			if (SH.isnt(grouping)) {
				targetObject.getManager().showAlert("grouping cannot be null or empty");
				return false;
			} else if (targetObject.getGroupbyFormulas().keySet().contains(grouping)) {
				targetObject.getManager().showAlert("Grouping already exists: " + grouping);
			}
			String display = Caster_String.INSTANCE.cast(params[1]);
			String parentFormula = Caster_String.INSTANCE.cast(params[2]);
			targetObject.addGroupingColumn(grouping, display, parentFormula, null);
			return true;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "grouping", "display", "parentFormula" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "A valid grouping name; can be a formula as well", "The optional display formula for the row",
					"The optional parent formula for recursive grouping" };
		}
		@Override
		protected String getHelp() {
			return "adds a new grouping column at the innermost level.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};

	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> ADD_GROUPING_AT = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "addGroupingAt",
			Boolean.class, String.class, String.class, String.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String grouping = Caster_String.INSTANCE.cast(params[0]);
			if (SH.isnt(grouping)) {
				targetObject.getManager().showAlert("grouping cannot null or empty");
				return false;
			} else if (targetObject.getGroupbyFormulas().keySet().contains(grouping)) {
				targetObject.getManager().showAlert("Grouping already exists: " + grouping);
			}
			int position = Caster_Integer.PRIMITIVE.cast(params[3]);
			if (OH.isntBetween(position, 0, targetObject.getGroupbyFormulas().getSize())) {
				targetObject.getManager().showAlert("Position out of range: " + position);
				return false;
			}
			String display = Caster_String.INSTANCE.cast(params[1]);
			String parentFormula = Caster_String.INSTANCE.cast(params[2]);
			targetObject.addGroupingColumn(grouping, display, parentFormula, position);
			return true;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "grouping", "display", "parentFormula", "position" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "A valid grouping name; can be a formula as well", "The optional display formula for the row",
					"The optional parent formula for recursive grouping", "Position to add the new grouping column (0 based position)" };
		}
		@Override
		protected String getHelp() {
			return "adds a new grouping column at the given position.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};

	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> REMOVE_GROUPING = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "removeGrouping",
			Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String groupId = Caster_String.INSTANCE.cast(params[0]);
			if (targetObject.getGroupbyFormulas().getSize() == 1) {
				targetObject.getManager().showAlert("Cannot remove grouping <b>" + groupId + "</b>. There must exist at least one grouping in the tree.");
				return false;
			}
			if (!targetObject.getGroupbyFormulas().keySet().contains(groupId)) {
				targetObject.getManager().showAlert("Grouping does not exist: " + groupId);
				return false;
			}
			targetObject.removeGrouping(groupId);
			return true;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "groupingId" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "An existing grouping id" };
		}
		@Override
		protected String getHelp() {
			return "removes an existing grouping by the id. To find the group id use TreePanel:: getGroupings() and TreePanelGrouping::getId().";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> REMOVE_GROUPING_AT = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "removeGroupingAt",
			Boolean.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject.getGroupbyFormulas().getSize() == 1) {
				targetObject.getManager().showAlert("Cannot remove grouping. There must exist at least one grouping in the tree.");
				return false;
			}
			int position = Caster_Integer.INSTANCE.cast(params[0]);
			if (OH.isntBetween(position, 0, targetObject.getGroupbyFormulas().getSize())) {
				targetObject.getManager().showAlert("Position out or range: " + position);
				return false;
			}
			targetObject.removeGroupingAt(position);
			return true;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "position" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Position of the grouping to be removed (0 based position)" };
		}
		@Override
		protected String getHelp() {
			return "removes an existing grouping column by it's index.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};

	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> GET_COLUMN = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "getColumn",
			AmiWebTreeColumn.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String colName = Caster_String.INSTANCE.cast(params[0]);
			FastWebTreeColumn r = SH.isnt(colName) ? null : targetObject.findColumnByName(colName);
			if (r == null)
				return null;
			return targetObject.getColumnFormatter(r.getColumnId());
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "columnName" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Name of the column. Note, this is the column title that the end users sees" };
		}
		@Override
		protected String getHelp() {
			return "Returns the column using the provided column name.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> GET_COLUMN_BY_ID = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "getColumnById",
			AmiWebTreeColumn.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getColumnByAmiId((String) params[0]);
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "columnId" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Id of the column." };
		}
		@Override
		protected String getHelp() {
			return "Returns the column using the provided column id.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> GET_GROUPING = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "getGrouping",
			AmiWebTreeGroupBy.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getGroupBy((String) params[0]);
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "groupingId" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "id of the grouping" };
		}
		@Override
		protected String getHelp() {
			return "Returns the grouping using the provided id.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> HIDE_COLUMN = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "hideColumn",
			Boolean.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			int columnId = Caster_Integer.PRIMITIVE.cast(params[0]);
			if (columnId >= 0 && columnId < targetObject.getTree().getColumnsCount()) {
				targetObject.getTree().hideColumn(columnId);
				return true;
			} else
				return false;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "positionalColumnId" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Positional Column Id (non-zero indexed)" };
		}
		@Override
		protected String getHelp() {
			return "Hides a column specified by positionalColumnId. Returns false on invalid positional column id, true if operation is successful.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> HIDE_COLUMNS2 = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "hideColumn",
			Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String columnTitle = Caster_String.INSTANCE.cast(params[0]);
			int columnId = targetObject.findColumnIdByName(columnTitle);
			if (columnId != -1 && columnId > 0 && columnId <= targetObject.getTree().getColumnsCount()) {
				targetObject.getTree().hideColumn(columnId);
				return true;
			}
			return false;
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
		protected String getHelp() {
			return "Hides a column specified by columnName. Returns false on invalid columnName, true if operation is successful.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> HIDE_COLUMNS_BY_ID = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "hideColumnById",
			Boolean.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String amiColumnId = Caster_String.INSTANCE.cast(params[0]);
			if (amiColumnId == null)
				return false;

			AmiWebTreeColumn columnByAmiId = targetObject.getColumnByAmiId(amiColumnId);
			if (columnByAmiId == null)
				return false;

			Integer columnId = columnByAmiId.getColumnId();
			if (columnId != -1 && columnId > 0 && columnId <= targetObject.getTree().getColumnsCount()) {
				targetObject.getTree().hideColumn(columnId);
				return true;
			}
			return false;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "columnId" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Id of the column." };
		}
		@Override
		protected String getHelp() {
			return "Hides a column specified by the columnId. Returns false on invalid columnId, true if operation is successful.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		};
	};

	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> GET_VISIBLE_COLUMNS_COUNT = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class,
			"getVisibleColumnsCount", Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTree().getVisibleColumnsCount();
		}

		@Override
		protected String getHelp() {
			return "Returns the number of columns visible on the panel.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> GET_HIDDEN_COLUMNS_COUNT = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class,
			"getHiddenColumnsCount", Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTree().getHiddenColumnsCount();
		}

		@Override
		protected String getHelp() {
			return "Returns the number of columns hidden from the panel.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> GET_SELECTED_ROWS = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "getSelectedRows",
			List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return new ArrayList(targetObject.getSelectableRows(null, AmiWebPortlet.SELECTED).getRows());
		}

		@Override
		protected String getHelp() {
			return "Returns a list of selected rows. Empty collection if no selection is made.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};

	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> SELECT_ROWS = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "selectRows",
			Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String selectedRowsText = Caster_String.INSTANCE.cast(params[0]);
			//TODO: needs more robust checking for the parameter.
			if (selectedRowsText.contains(",") && selectedRowsText.contains("-"))
				return false;
			targetObject.getTree().setSelectedRowsNoFire(selectedRowsText, false);
			return true;
		}

		@Override
		protected String getHelp() {
			return "Overrides the currently selected rows with the supplied range of rows. If there is no currently selected rows, this method simply sets selected rows. Otherwise this will de-selected any previously selected rows then sets selected rows.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "selectedRowsText" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Comma delimited row indeces OR a range (selectRows(\"0,1,3,5\") OR selectRows(\"5-10\"))." };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};

	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> CLEAR_SELECTED_ROWS = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class,
			"clearSelectedRows", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.getTree().clearSelected();
			return true;
		}

		@Override
		protected String getHelp() {
			return " De-selects all the selected rows. Always succeeds.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		};
	};

	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> SORT = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "sort", Boolean.class,
			Integer.class, Boolean.class, Boolean.class, Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			int columnId = Caster_Integer.PRIMITIVE.cast(params[0]);
			boolean descending = Caster_Boolean.PRIMITIVE.cast(params[1]);
			boolean keepSorting = Caster_Boolean.PRIMITIVE.cast(params[2]);
			boolean add = Caster_Boolean.PRIMITIVE.cast(params[3]);
			targetObject.getTree().sortRows(columnId, !descending, keepSorting, add);
			return true;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "columnId", "descending", "keepSorting", "add" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Column Id (non-zero indexed)", "true for descending, false for ascending", "Keep Sorting", "Add" };
		}

		@Override
		protected String getHelp() {
			return "Sorts the rows based on the column specified by columnId.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		};
	};

	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> SORT2 = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "sort", Boolean.class,
			String.class, Boolean.class, Boolean.class, Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String columnName = Caster_String.INSTANCE.cast(params[0]);
			int columnId = targetObject.findColumnIdByName(columnName);
			if (columnId != -1) {
				boolean ascending = Caster_Boolean.PRIMITIVE.cast(params[1]);
				boolean keepSorting = Caster_Boolean.PRIMITIVE.cast(params[2]);
				boolean add = Caster_Boolean.PRIMITIVE.cast(params[3]);
				targetObject.getTree().sortRows(columnId, ascending, keepSorting, add);
				return true;
			}
			return false;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "columnName", "ascending", "keepSorting", "add" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "columnName", "true for ascending, false for descending", "Keep Sorting", "Add" };
		}

		@Override
		protected String getHelp() {
			return "Sorts the rows based on the column specified by columnName.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		};
	};

	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> CLEAR_SORT = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "clearSort", Boolean.class,
			String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String columnName = Caster_String.INSTANCE.cast(params[0]);
			int columnId = targetObject.findColumnIdByName(columnName);
			if (columnId != -1) {
				targetObject.getTree().sortRows(columnId, true, false, false);
				return true;
			}
			return false;
		}

		@Override
		protected String getHelp() {
			return "Clears the sort on the specified column name. Returns true on success, false on invalid column name.";
		}

		protected String[] buildParamNames() {
			return new String[] { "columnName" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "columnName" };
		}

		@Override
		public boolean isReadOnly() {
			return false;
		};
	};

	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> GET_SEARCH = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "getSearch",
			String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTree().getSearch();
		}

		@Override
		protected String getHelp() {
			return "Returns the last keyword(s) used to search the tree panel.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};

	//TODO: does not update searchbar in front-end, consult Rob.
	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> SET_SEARCH = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "setSearch", Boolean.class,
			String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String searchText = Caster_String.INSTANCE.cast(params[0]);
			targetObject.getTree().setSearch(searchText);
			return true;
		}

		@Override
		protected String getHelp() {
			return "Searches the Tree Panel with keyword specified by searchText. Empty string or null clears the seach selection. Successful search selects the first matched row and consecutive matches can be found by pressing the enter key.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "searchText" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Text to search for." };
		}

		@Override
		public boolean isReadOnly() {
			return false;
		};
	};

	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> CLEAR_FILTER = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "clearFilter",
			Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String columnName = Caster_String.INSTANCE.cast(params[0]);
			int columnId = targetObject.findColumnIdByName(columnName);
			if (columnId != -1)
				targetObject.getTree().setFilteredIn(columnId, null);
			return false;
		}

		@Override
		protected String getHelp() {
			return "Clears the filter on the column specified by columnName. Returns true if successful, false otherwise.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "columnName" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "column to clear filter on." };
		}

		@Override
		public boolean isReadOnly() {
			return false;
		};
	};

	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> GET_ROWS = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "getRows", List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Table table = targetObject.getSelectableRows(null, AmiWebPortlet.ALL);
			ArrayList<Row> rows = new ArrayList<Row>(table.getRows().size());
			for (int i = 0; i < table.getRows().size(); i++)
				rows.add(table.getRows().get(i));
			return rows;
		}
		@Override
		protected String getHelp() {
			return "Returns a list of all rows from the Panel.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	private static Table asTable(AmiWebTreePortlet targetObject, boolean selectedRows, boolean unselectedRows, boolean filteredRows, boolean visibleColumns, boolean hiddenColumns,
			boolean format, Set<String> columnsToInclude) {
		List<FastWebTreeColumn> colsW = new ArrayList<FastWebTreeColumn>();
		FastWebTree tree = targetObject.getTree();
		if (visibleColumns) {
			FastWebTreeColumn c2 = tree.getTreeColumn();
			if (columnsToInclude == null || columnsToInclude.contains(c2.getColumnName()))
				colsW.add(c2);
			for (int i = 0; i < tree.getVisibleColumnsCount(); i++) {
				FastWebTreeColumn c = tree.getVisibleColumn(i + 1);
				if (columnsToInclude != null && !columnsToInclude.contains(c.getColumnName()))
					continue;
				colsW.add(c);
			}
		}
		if (hiddenColumns)
			for (int i = 0; i < tree.getHiddenColumnsCount(); i++) {
				FastWebTreeColumn c = tree.getHiddenColumn(i);
				if (columnsToInclude != null && !columnsToInclude.contains(c.getColumnName()))
					continue;
				colsW.add(c);
			}
		int colsCount = colsW.size();
		if (colsCount == 0)
			return new BasicTable();
		//		Column[] colsI = new Column[colsCount];
		Column[] colsO = new Column[colsCount];

		for (int i = 0; i < colsCount; i++) {
			FastWebTreeColumn c = colsW.get(i);
			//TODO:  if not formatting, should get column type instead of just using Object
			colsO[i] = new BasicColumn(format ? String.class : Object.class, c.getColumnName());
		}
		BasicTable r = new BasicTable(colsO);
		List<WebTreeNode> rows;

		if (selectedRows && unselectedRows) {
			rows = CH.l(tree.getNodes());
		} else if (selectedRows) {
			rows = tree.getTreeManager().getSelectedNodes();
		} else if (unselectedRows) {
			Iterable<WebTreeNode> rowsSel = tree.getSelected();
			Iterable<WebTreeNode> allRows = tree.getNodes();
			IntSet s = new IntSet();
			for (WebTreeNode i : rowsSel)
				s.add(i.getUid());
			rows = new ArrayList<WebTreeNode>();
			for (WebTreeNode row : allRows)
				if (!s.contains(row.getUid()))
					rows.add(row);
		} else {
			rows = new ArrayList<WebTreeNode>();
		}
		Collections.sort(rows, BasicWebTreeManager.POSITION_SORTER);

		if (filteredRows) {
			Iterator<WebTreeNode> filtered = tree.getFilteredRows().iterator();
			if (filtered.hasNext()) {
				rows = new ArrayList<WebTreeNode>();
				while (filtered.hasNext())
					rows.add(filtered.next());
			}
		}

		WebTreeNodeFormatter formatters[] = new WebTreeNodeFormatter[colsW.size()];
		for (int i = 0; i < colsW.size(); i++)
			formatters[i] = colsW.get(i).getFormatter();
		TableList rows2 = r.getRows();
		if (format) {
			StringBuilder sink = new StringBuilder();
			for (WebTreeNode row : rows) {
				Object values[] = new Object[colsCount];
				for (int i = 0; i < colsCount; i++)
					values[i] = formatters[i].formatToText(formatters[i].getValue(row));
				rows2.addRow(values);
			}
		} else {
			for (WebTreeNode row : rows) {
				Object values[] = new Object[colsCount];
				for (int i = 0; i < colsCount; i++)
					values[i] = formatters[i].getValue(row);
				rows2.addRow(values);
			}
		}
		r.setTitle(targetObject.getAmiTitle(false));
		return r;
	}

	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> AS_TABLE = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "asTable",
			BasicTable.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return asTable(targetObject, true, true, false, true, false, false, null);
		}

		@Override
		protected String getHelp() {
			return "Copies and returns the values of this panel as a Table.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> AS_TABLE_4 = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "asTable",
			BasicTable.class, String.class, Set.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
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
			return "Copies and returns the values in the specified columns of this panel as a Table with options.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> AS_TABLE_2 = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "asTable",
			BasicTable.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
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
			return new String[] { "Include selected Rows", "include unselected Rows", "include filtered Rows", "include Visible Columns", "Include Hidden Columns" };
		}

		@Override
		protected String getHelp() {
			return "Copies and returns the values of this panel as a Table with more options.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> AS_TABLE_3 = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "asTable",
			BasicTable.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Set.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
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
			return "Copies and returns the values in the selected columns of this panel as a Table with more options.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> AS_FORMATTED_TABLE2 = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class,
			"asFormattedTable", BasicTable.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
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
			return "Copies and returns the values of this panel as a Table, with cells formatted.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> AS_FORMATTED_TABLE3 = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class,
			"asFormattedTable", BasicTable.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Set.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
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
			return "Copy and return the values in the specified columns of this panel as a Table, with cells formatted";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> AS_FORMATTED_TABLE = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "asFormattedTable",
			BasicTable.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return asTable(targetObject, true, true, false, true, false, true, null);
		}

		@Override
		protected String getHelp() {
			return "Copies and returns the values of this panel as a Table, with cells formatted.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> GET_DEFAULT_WHERE = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "getDefaultWhere",
			String.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getDefaultWhereFilter();
		}
		@Override
		protected String getHelp() {
			return "Returns the default filter value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> SET_CURRENT_WHERE = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "setCurrentWhere",
			Boolean.class, false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String filter = (String) params[0];
			StringBuilder sb = new StringBuilder();
			targetObject.compileWhereFilter(filter, sb);
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
			return "Applies a filter on the underlying data for this table panel. If the filter is null or an empty string then no filter is applied and all data is shown.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> GET_CURRENT_WHERE = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "getCurrentWhere",
			String.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getCurrentRuntimeFilter();
		}
		@Override
		protected String getHelp() {
			return "Returns the currently applied filter.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> RESET_WHERE = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "resetWhere",
			Boolean.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.resetWhere();
		}
		@Override
		protected String getHelp() {
			return "Resets the current where filter to the default.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> GET_COLUMNS_WITH_SORT = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class,
			"getColumnsWithSort", List.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			FastWebTree tree = targetObject.getTree();
			Set<Integer> t = tree.getSortedColumnIds();
			List<String> t2 = new ArrayList<String>(t.size());
			for (Integer s : t)
				t2.add(tree.getColumn(s).getColumnName());
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

	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> GET_COLUMN_SORTING = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "getColumnSorting",
			String.class, true, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];
			FastWebTreeColumn col = targetObject.findColumnByName((String) name);
			if (col == null) {
				warning(sf, "Columns not found", CH.m("Missing Column Name", name));
				return null;
			} else {
				Iterable<Entry<Integer, Boolean>> t = targetObject.getTree().getSortedColumns();
				if (t != null) {
					for (Entry<Integer, Boolean> i : t) {
						if (OH.eq(col.getColumnId(), i.getKey()))
							return i.getValue() ? "A" : "D";
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

	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> GET_COLUMN_LOCATION = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class,
			"getColumnLocation", Integer.class, false, String.class) {
		@Override
		public Integer invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];
			FastWebTreeColumn col = targetObject.findColumnByName((String) name);
			if (col == null) {
				warning(sf, "Columns not found", CH.m("Missing Column Name", name), null);
				return -2;
			} else {
				return targetObject.getTree().getColumnPosition(col.getColumnId());
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

	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> MOVE_COLUMNS_TO = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "moveColumnsTo",
			Object.class, true, Integer.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			int position = (Integer) params[0];
			final List<String> missingColumns = new ArrayList<String>();
			final boolean isHide = position < 0;
			final FastWebTree tree = targetObject.getTree();
			for (int i = 1; i < params.length; i++) {
				final FastWebTreeColumn col = targetObject.findColumnByName((String) params[i]);
				if (col != null) {
					if (isHide) {
						if (tree.getVisibleColumnsCount() > 0)
							tree.hideColumn(col.getColumnId());
						continue;
					}
					position = Math.min(position, tree.getVisibleColumnsCount());
					int t = tree.getColumnPosition(col.getColumnId());
					if (t != -1 && t < position)
						position--;
					tree.showColumn(col.getColumnId(), position);
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
			return new String[] { "zero-indexed left offset Position of the first column, or -1 to hide all columns",
					"name of the columns to move in a comma delimited manner, from left to right" };
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

	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> GET_COLUMNS_WITH_FILTERS = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class,
			"getColumnsFilter", List.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			FastWebTree tree = targetObject.getTree();
			Set<Integer> t = tree.getFilteredInColumns();
			List<String> t2 = new ArrayList<String>(t.size());
			for (Integer s : t)
				t2.add(tree.getColumn(s).getColumnName());
			return t2;
		}
		@Override
		protected String getHelp() {
			return "Returns a list of filtered columns.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	//TreePanel:: boolean setSelectedTreeNodeState (isSetExpanded(the opposite is collapsed), boolean isRecursive)
	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> EXPAND_SELECTED_NODES = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class,
			"expandSelectedNodes", Boolean.class, false, Boolean.class, Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack cf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			FastWebTree tree = targetObject.getTree();
			boolean isSetExpanded = (Boolean) params[0];
			boolean isRecursive = (Boolean) params[1];
			try {
				if (isSetExpanded) {
					if (isRecursive) {
						((BasicWebTreeManager) tree.getTreeManager()).setSelectedExpanded(true, true);
						return true;
					} else {
						((BasicWebTreeManager) tree.getTreeManager()).setSelectedExpanded(true, false);
						return true;
					}

				} else {
					if (isRecursive) {
						((BasicWebTreeManager) tree.getTreeManager()).setSelectedExpanded(false, true);
						return true;
					} else {
						((BasicWebTreeManager) tree.getTreeManager()).setSelectedExpanded(false, false);
						return true;
					}
				}
			} catch (Exception e) {
				return false;
			}

		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "isSetExpanded", "isRecursive" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "if isSetExpanded is true, all selected tree nodes will be set expended. If false, they will be set collapsed",
					"if isRecursive is set to true, tree nodes will be recursively expanded/collapsed down to its leaf nodes. If false, only its immediate " };
		}
		@Override
		protected String getHelp() {
			return "expand or collapse the selected nodes either recursively or non-recursively ";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> ADD_COLUMN = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "addColumn",
			FastWebTreeColumn.class, String.class, String.class, String.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String id = Caster_String.INSTANCE.cast(params[0]);
			if (id == null || targetObject.getColumnByAmiId(id) != null) {
				return null;
			}
			String type = (String) params[1];
			if (!AmiWebUtils.CUSTOM_COL_NAMES.containsValue(type))
				throw new FlowControlThrow("Unknown type: " + type + " (Valid types: " + SH.join(", ", AmiWebUtils.CUSTOM_COL_NAMES.getValues()) + ")");
			Byte b = AmiWebUtils.CUSTOM_COL_NAMES.getKey(type);
			String formula = (String) params[2];
			Integer position = (Integer) (params[3]);
			int totalCount = targetObject.getTree().getVisibleColumnsCount();
			if (position == null || position > totalCount)
				position = totalCount;
			StringBuilder errorSink = new StringBuilder();
			FastWebTreeColumn col = targetObject.addTransientColumn(-1, id, id, formula, "", "", "", "", b, 0, position, errorSink, "", "");
			if (errorSink.length() != 0) {
				throw new FlowControlThrow("Could not create transient column, error: " + errorSink.toString());
			}
			return targetObject.getColumnFormatter(col.getColumnId());
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "columnId", "columnType", "formula", "position" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Column ID", "Type of column", "Column Formula", "Position of the Column" };
		}
		@Override
		protected String getHelp() {
			return "Creates a tree column using the provided information. Valid column types are: text, numeric, progress, time, date, price, percent, html, json, image, checkbox, Masked, spark, time_sec,time_millis, time_micros, time_nanos, datetime, datetime_sec, datetime_millis, datetime_micros, datetime_nanos";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};

	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> ADD_COLUMN2 = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "addColumn",
			FastWebTreeColumn.class, String.class, String.class, String.class, Integer.class, String.class, String.class, String.class, String.class, Integer.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String id = Caster_String.INSTANCE.cast(params[0]);
			if (id == null || targetObject.getColumnByAmiId(id) != null) {
				return null;
			}
			String type = (String) params[1];
			if (!AmiWebUtils.CUSTOM_COL_NAMES.containsValue(type))
				throw new FlowControlThrow("Unknown type: " + type + " (Valid types: " + SH.join(", ", AmiWebUtils.CUSTOM_COL_NAMES.getValues()) + ")");
			Byte b = AmiWebUtils.CUSTOM_COL_NAMES.getKey(type);
			String formula = (String) params[2];
			Integer position = Caster_Integer.INSTANCE.cast(params[3]);
			String orderBy = Caster_String.INSTANCE.cast(params[4]);
			String style = Caster_String.INSTANCE.cast(params[5]);
			String color = Caster_String.INSTANCE.cast(params[6]);
			String bgColor = Caster_String.INSTANCE.cast(params[7]);
			Integer decimals = Caster_Integer.INSTANCE.cast(params[8]);
			String headerStyle = Caster_String.INSTANCE.cast(params[9]);
			int totalCount = targetObject.getTree().getVisibleColumnsCount();
			if (position == null || position > totalCount)
				position = totalCount;
			StringBuilder errorSink = new StringBuilder();
			FastWebTreeColumn col = targetObject.addTransientColumn(-1, id, id, formula, orderBy, style, color, bgColor, b, decimals, position, errorSink, "", headerStyle);
			if (errorSink.length() != 0) {
				targetObject.getManager().showAlert(errorSink.toString());
				return null;
			}
			return targetObject.getColumnFormatter(col.getColumnId());
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "columnId", "columnType", "formula", "position", "orderBy", "style", "color", "bgColor", "decimals", "headerStyle" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Column ID", "Type of column", "Column Formula", "Position of the Column", "Order By Formula", "Style to be applied", "Color to be applied",
					"Background color to be applied", "Decimal Precision", "Header Style to be applied" };
		}
		@Override
		protected String getHelp() {
			return "Creates a tree column using the provided information. Valid column types are: text, numeric, progress, time, date, price, percent, html, json, image, checkbox, Masked, spark, time_sec,time_millis, time_micros, time_nanos, datetime, datetime_sec, datetime_millis, datetime_micros, datetime_nanos";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};

	private static final AmiAbstractMemberMethod<AmiWebTreePortlet> REMOVE_COLUMN = new AmiAbstractMemberMethod<AmiWebTreePortlet>(AmiWebTreePortlet.class, "removeColumn",
			Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String id = Caster_String.INSTANCE.cast(params[0]);
			AmiWebTreeColumn col = targetObject.getColumnByAmiId(id);
			if (col == null)
				return false;
			targetObject.removeTransientColumn(col.getColumnId());
			return true;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "id" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "ID of the column to be removed" };
		}
		@Override
		protected String getHelp() {
			return "Removes the specified treepanel column";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};

	};

	public final static AmiWebScriptMemberMethods_TreePanel INSTANCE = new AmiWebScriptMemberMethods_TreePanel();

}