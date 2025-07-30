package com.f1.ami.web.amiscript;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebCustomColumn;
import com.f1.ami.web.AmiWebUtils;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebColumn;
import com.f1.suite.web.table.impl.WebTableFilteredInFilter;
import com.f1.suite.web.tree.impl.ArrangeColumnsPortlet;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

// TODO: need Rob's suggestion whether to stick to WebColumn as targetObject. Other options: Colmn, ColumnarColumn, AmiWebCustomColumn, FastWebColumn, BasicColumn.
public class AmiWebScriptMemberMethods_TablePanelColumn extends AmiWebScriptBaseMemberMethods<AmiWebCustomColumn> {

	private AmiWebScriptMemberMethods_TablePanelColumn() {
		super();
		addMethod(GET_TITLE, "title");
		addMethod(GET_ID, "id");
		addMethod(IS_CLICKABLE, "clickable");
		addMethod(IS_ONE_CLICK, "oneClick");
		addMethod(GET_TYPE);
		addMethod(SET_FILTER);
		addMethod(SET_FILTER2);
		addMethod(SET_FILTER3);
		addMethod(GET_FILTER);
		addMethod(GET_CSS_CLASS, "cssClass");
		addMethod(GET_WIDTH, "width");
		addMethod(SET_WIDTH);
		addMethod(GET_HEADER_STYLE, "headerStyle");
		addMethod(GET_DISPLAY_FORMULA, "displayFormula");
		addMethod(GET_DESCRIPTION, "description");
		addMethod(AUTO_SIZE);
		addMethod(HIDE_COLUMN);
		addMethod(SHOW_COLUMN);
		addMethod(IS_VISIBLE);
		addMethod(IS_EDITABLE);
		addMethod(SET_TO_EDITABLE);
		addMethod(IS_TRANSIENT);
		addMethod(SET_TITLE);
		addMethod(SET_HEADER_STYLE);
		addMethod(GET_EDIT_TYPE);
		addMethod(SET_CLICKABLE);
		addMethod(SET_WIDTH_FIXED);
		addMethod(IS_WIDTH_FIXED);
	}

	@Override
	public String getVarTypeName() {
		return "TablePanelColumn";
	}

	@Override
	public String getVarTypeDescription() {
		return "Represents a column within a TablePanel object. It can accessed using tabelPanel.getColumn(colName).";
	}

	@Override
	public Class<AmiWebCustomColumn> getVarType() {
		return AmiWebCustomColumn.class;
	}

	@Override
	public Class<AmiWebCustomColumn> getVarDefaultImpl() {
		// TODO Auto-generated method stub
		return null;
	}

	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> AUTO_SIZE = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "autosize",
			Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				targetObject.getTable().getTablePortlet().autoSizeColumn(targetObject.getWebColumn());
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Autosizes this column. Returns true if operation is successful, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> GET_DISPLAY_FORMULA = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class,
			"getDisplayFormula", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getDisplayFormula().getFormula(true);
		}

		@Override
		protected String getHelp() {
			return "Returns the display formula of the column.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> GET_FILTER = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "getFilter",
			WebTableFilteredInFilter.class, false) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			WebTableFilteredInFilter t = targetObject.getTable().getTable().getFiltererdIn(targetObject.getColumnId());
			return t;
		}

		@Override
		protected String getHelp() {
			return "Returns the filter of this column.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> SET_FILTER = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "setFilter",
			Object.class, true, Object.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			if (params.length == 1 && !SH.is((String) params[0])) {
				targetObject.getTable().getTable().setFilteredIn(targetObject.getColumnId(), new HashSet<String>());
				return null;
			}
			List<String> ls = new ArrayList<>();
			for (Object obj : params) {
				if (obj == null) {
					warning(sf, "variable length version of setFilter cannot contain null", CH.m("value", obj));
					return null;
				}
				ls.add((String) obj);
			}
			Set<String> mySet = new HashSet<String>(ls);
			targetObject.getTable().getTable().setFilteredIn(targetObject.getColumnId(), mySet);
			return null;
		}

		@Override
		protected String getHelp() {
			return "Sets the filter for this column.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "values" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {
					"The values to filter out for this column. You may put as many as you need. You may clear the filter with this method with setFilter(null) or setFilter(\"\")" };
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> SET_FILTER2 = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "setFilter",
			Boolean.class, false, Boolean.class, Boolean.class, List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			Boolean isKeep = (Boolean) params[0];
			Boolean isPattern = (Boolean) params[1];
			Set<String> ss = null;
			if (params[2] instanceof List<?>) {
				List<String> sa = (List<String>) params[2];
				ss = new HashSet<String>(sa);
			}

			if (ss != null && ss.contains(null)) {
				ss.remove(null);
				targetObject.getTable().getTable().setFilteredIn(targetObject.getColumnId(), ss, isKeep, true, isPattern, null, null);
			} else if (ss != null) {
				targetObject.getTable().getTable().setFilteredIn(targetObject.getColumnId(), ss, isKeep, false, isPattern, null, null);
			}
			return true;
		}

		@Override
		protected String getHelp() {
			return "Sets/Clears the filter for this column with the given options and the supplied values as a list. Clears the filter if the list is empty. If the list contains null, the method will consider null values when filtering.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "isKeep", "isPattern", "a list of strings" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "true=keep those that match,false=hide those that match", "If true, the values in your list is considered regular expression.",
					"The list of string to filter" };
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> SET_FILTER3 = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "setFilter",
			Boolean.class, false, List.class, Boolean.class, Boolean.class, Boolean.class, String.class, Boolean.class, String.class, Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			Set<String> ss = null;
			if (params[0] instanceof List<?>) {
				List<String> sa = (List<String>) params[0];
				ss = new HashSet<String>(sa);
			} else {
				return false;
			}
			Boolean isKeep = (Boolean) params[1];
			Boolean isPattern = (Boolean) params[2];
			Boolean isIncludeNull = (Boolean) params[3];
			String maxVal = (String) params[4];
			Boolean isMaxValInclu = (Boolean) params[5];
			String minVal = (String) params[6];
			Boolean isMinValInclu = (Boolean) params[7];
			targetObject.getTable().getTable().setFilteredIn(targetObject.getColumnId(), ss, isKeep, isIncludeNull, isPattern, minVal, isMinValInclu, maxVal, isMaxValInclu);

			return true;
		}

		@Override
		protected String getHelp() {
			return "Sets the filter with the supplied list of values and filtering options.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "a list of strings", "isKeep", "isPattern", "isIncludeNull", "maxVal", "isMaxValInclusive", "minVal", "isMinValInclusive" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "The list of strings to filter", "true=keep those that match,false=hide those that match",
					"If true, the values in your list is considered regular expression", "true=includes null, false=excludes null", "the max value to filter as a string",
					"true=can include max value itself, false=excludes max value", "the min value to filter as a string",
					"true=can include min value itself, false=excludes min value" };
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> GET_DESCRIPTION = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "getDescription",
			String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getDescription();
		}

		@Override
		protected String getHelp() {
			return "Returns the description of the column.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> GET_TITLE = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "getTitle",
			String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTitle(true);
		}

		@Override
		protected String getHelp() {
			return "Returns the title of the column.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> GET_ID = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "getId", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getColumnId();
		}

		@Override
		protected String getHelp() {
			return "Returns the column id (i.e. col_1, col_2 etc.)";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> IS_CLICKABLE = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "isClickable",
			Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getWebColumn().getIsClickable();
		}

		@Override
		protected String getHelp() {
			return "Returns true if column is clickable, false otherwise. Note: clickable is usually set to true to make use of the onCellClicked callback.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> IS_ONE_CLICK = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "isOneClick",
			Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getWebColumn().getIsOneClick();
		}

		@Override
		protected String getHelp() {
			return "Returns true if column's <b>One Click</b> option is set to to true, false otherwise. Note: with <b>One Click</b> set to true, the onCellClicked callback will fire by clicking the cell once. With <b>Clickable</b> set to true, user needs two clicks to fire the callback: one to select, one to fire.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> GET_TYPE = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "getType", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			Byte type = targetObject.getType().getValue();
			return AmiWebUtils.CUSTOM_COL_DESCRIPTIONS.get(type);
		}

		@Override
		protected String getHelp() {
			return "Returns the column type.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> GET_CSS_CLASS = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "getCssClass",
			String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHeaderStyle(true);
			//return targetObject.getColumnCssClass();
		}

		@Override
		protected String getHelp() {
			return "Returns the css class associated with this column.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> SET_WIDTH = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "setWidth", Object.class,
			Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			WebColumn c = targetObject.getWebColumn();
			Integer i = (Integer) params[0];
			if (i != null && i >= 0 && i != c.getWidth()) {
				c.setWidth(i);
				c.getTable().onColumnChanged(c);
			}
			return null;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "columnWidth" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Width of the column, must not be negative" };
		}
		@Override
		protected String getHelp() {
			return "Sets the width of the column.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> GET_WIDTH = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "getWidth",
			Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getWebColumn().getWidth();

		}

		@Override
		protected String getHelp() {
			return "Returns the width of the column.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> GET_HEADER_STYLE = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "getHeaderStyle",
			String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHeaderStyle(true);
		}

		@Override
		protected String getHelp() {
			return "Returns the style associated with the column header.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> SHOW_COLUMN = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "showColumn",
			Object.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			FastWebTable table = targetObject.getTable().getTable();
			table.showColumn(targetObject.getColumnId());
			return null;
		}

		@Override
		protected String getHelp() {
			return "Shows the target column at the end of the table.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> HIDE_COLUMN = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "hideColumn",
			Object.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			FastWebTable table = targetObject.getTable().getTable();
			table.hideColumn(targetObject.getColumnId());
			return null;
		}

		@Override
		protected String getHelp() {
			return "Hides the target column from the table.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> IS_VISIBLE = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "isVisible",
			Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			FastWebTable table = targetObject.getTable().getTable();
			List<String> visibleColumns = ArrangeColumnsPortlet.getVisibleColumns(table);
			Set<String> visibleSet = new HashSet<>(visibleColumns);
			return visibleSet.contains(targetObject.getColumnId());
		}

		@Override
		protected String getHelp() {
			return "Returns true if target column is visible in the table, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> IS_TRANSIENT = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "isTransient",
			Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.isTransient();
		}

		@Override
		protected String getHelp() {
			return "Returns true if column is transient, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> IS_EDITABLE = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "isEditable",
			Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject.getEditType(true) != AmiWebCustomColumn.EDIT_DISABLED) {
				return true;
			}
			return false;
		}

		@Override
		protected String getHelp() {
			return "Returns true if the column is editable. Returns false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> GET_EDIT_TYPE = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "getEditType",
			String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			byte editType = targetObject.getEditType(true);
			String type = "Disabled";

			if (OH.eq(editType, AmiWebCustomColumn.EDIT_TEXTFIELD)) {
				type = "Text";
			}
			if (OH.eq(editType, AmiWebCustomColumn.EDIT_READONLY)) {
				type = "Readonly";
			}
			if (OH.eq(editType, AmiWebCustomColumn.EDIT_NUMERIC)) {
				type = "Numeric";
			}
			if (OH.eq(editType, AmiWebCustomColumn.EDIT_CHECKBOX)) {
				type = "Checkbox";
			}
			if (OH.eq(editType, AmiWebCustomColumn.EDIT_MASKED)) {
				type = "Masked";
			}
			if (OH.eq(editType, AmiWebCustomColumn.EDIT_DISABLED)) {
				type = "Disabled";
			}
			return type;
		}

		@Override
		protected String getHelp() {
			return "Returns the edit type of the column.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> SET_TO_EDITABLE = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "setToEditable",
			Boolean.class, String.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			String editId = (String) params[0];
			String editType = (String) params[1];
			for (String c : targetObject.getTable().getCustomDisplayColumnIds()) {
				AmiWebCustomColumn col = targetObject.getTable().getCustomDisplayColumn(c);
				if (col.getColumnId() == targetObject.getColumnId()) {
					continue;
				}
				if (col.getEditType(true) != AmiWebCustomColumn.EDIT_DISABLED && OH.eq(col.getEditId(true), editId)) {
					targetObject.getTable().getManager().showAlert(targetObject.getColumnId() + " has duplicate edit id as " + col.getColumnId());
					return false;
				}
			}
			Byte type = AmiWebCustomColumn.EDIT_DISABLED;
			if (OH.eq(editType, "Text")) {
				type = AmiWebCustomColumn.EDIT_TEXTFIELD;
			}
			if (OH.eq(editType, "Readonly")) {
				type = AmiWebCustomColumn.EDIT_READONLY;
			}
			if (OH.eq(editType, "Numeric")) {
				type = AmiWebCustomColumn.EDIT_NUMERIC;
			}
			if (OH.eq(editType, "Checkbox")) {
				type = AmiWebCustomColumn.EDIT_CHECKBOX;
			}
			if (OH.eq(editType, "Masked")) {
				type = AmiWebCustomColumn.EDIT_MASKED;
			}
			if (OH.eq(editType, "Disabled")) {
				type = AmiWebCustomColumn.EDIT_DISABLED;
			}

			targetObject.setEditType(type, true);
			targetObject.setEditId(editId, true);
			return true;
		};

		@Override
		protected String[] buildParamNames() {
			return new String[] { "editId", "editType" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "edit id", "edit type" };
		}
		@Override
		protected String getHelp() {
			return "Sets the column to editable. This also includes transient columns. Options are Text, Readonly, Numeric, Checkbox, and Masked";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> SET_TITLE = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "setTitle",
			Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			String title = (String) params[0];
			if (title == null) {
				return false;
			}
			WebColumn c = targetObject.getWebColumn();
			targetObject.setTitle(title, true);
			BasicWebColumn webColumn = (BasicWebColumn) targetObject.getTable().getTable().getColumn(targetObject.getColumnId());
			webColumn.setColumnName(title);
			c.getTable().onColumnChanged(c);
			return true;
		};

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
			return "Sets the column title with the given name.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> SET_HEADER_STYLE = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "setHeaderStyle",
			Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			String headerStyle = (String) params[0];
			if (headerStyle == null) {
				return false;
			}
			WebColumn c = targetObject.getWebColumn();
			targetObject.setHeaderStyle(headerStyle, true);
			targetObject.setHeaderStyleExpression(true);
			BasicWebColumn webColumn = (BasicWebColumn) targetObject.getTable().getTable().getColumn(targetObject.getColumnId());
			String headerStyleExpression = targetObject.getHeaderStyleExpression(true);
			webColumn.setHeaderStyle(headerStyleExpression);
			c.getTable().onColumnChanged(c);
			return true;
		};

		@Override
		protected String[] buildParamNames() {
			return new String[] { "headerStyle" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "headerStyle" };
		}
		@Override
		protected String getHelp() {
			return "Sets the header style of the column. Able to set fonts,alignment,decoration,colors e.g:impact,right,italic,#ff0000";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> SET_CLICKABLE = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "setClickable",
			Boolean.class, Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			boolean clickable = (boolean) params[0];
			BasicWebColumn webColumn = (BasicWebColumn) targetObject.getTable().getTable().getColumn(targetObject.getColumnId());
			WebColumn c = targetObject.getWebColumn();

			if (clickable != true && clickable != false) {
				return false;
			}
			if (clickable == true) {
				targetObject.setClickable("m", true);
				webColumn.setIsClickable(true);
			} else {
				targetObject.setClickable(null, true);
				webColumn.setIsClickable(false);
			}
			c.getTable().onColumnChanged(c);
			return true;
		};

		@Override
		protected String[] buildParamNames() {
			return new String[] { "Clickable" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "true,false" };
		}
		@Override
		protected String getHelp() {
			return "Sets the column to clickable if true is passed. If false is passed it is not clickable.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> SET_WIDTH_FIXED = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "setWidthFixed",
			Boolean.class, Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			Boolean fix = (Boolean) params[0];
			if (fix == null)
				return false;
			try {
				if (targetObject.isFixedWidth() != fix) {
					//					BasicWebColumn webColumn = (BasicWebColumn) targetObject.getTable().getTable().getColumn(targetObject.getColumnId());
					targetObject.setIsFixedWidth(fix);
					targetObject.getTable().setFixedColumn(targetObject.getColumnId(), fix);
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		};

		@Override
		protected String[] buildParamNames() {
			return new String[] { "shouldFixWidth" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "true, false" };
		}
		@Override
		protected String getHelp() {
			return "Controls whether a column's width is fixed. A column with fixed width does not respond to autoSizeAllColumns and autoFitAllColumns from column header menu, and prevents the column from being resized by dragging. Returns true if setting has been changed as a result, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebCustomColumn> IS_WIDTH_FIXED = new AmiAbstractMemberMethod<AmiWebCustomColumn>(AmiWebCustomColumn.class, "isWidthFixed",
			Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomColumn targetObject, Object[] params, DerivedCellCalculator caller) {
			//			BasicWebColumn webColumn = (BasicWebColumn) targetObject.getTable().getTable().getColumn(targetObject.getColumnId());
			//			WebColumn c = targetObject.getWebColumn();
			return targetObject.isFixedWidth();
		};

		@Override
		protected String getHelp() {
			return "returns true if this column's width is fixed, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	public final static AmiWebScriptMemberMethods_TablePanelColumn INSTANCE = new AmiWebScriptMemberMethods_TablePanelColumn();
}
