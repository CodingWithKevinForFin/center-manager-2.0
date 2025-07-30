package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.base.Table;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

import spreadsheet.AmiWebSpreadSheetWorksheet;

public class AmiWebScriptMemberMethods_SpreadSheetWorksheet extends AmiWebScriptBaseMemberMethods<AmiWebSpreadSheetWorksheet> {
	private AmiWebScriptMemberMethods_SpreadSheetWorksheet() {
		super();
		addMethod(INIT);
		addMethod(SET_TITLE);
		addMethod(GET_TITLE);
		addMethod(GET_TABLE);
		addMethod(ADD_COL_WITH_FORMULA);
	}

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetWorksheet> INIT = new AmiAbstractMemberMethod<AmiWebSpreadSheetWorksheet>(AmiWebSpreadSheetWorksheet.class, null,
			AmiWebSpreadSheetWorksheet.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetWorksheet targetObject, Object[] params, DerivedCellCalculator caller) {
			return new AmiWebSpreadSheetWorksheet();
		}

		@Override
		protected String getHelp() {
			return "Creates a builder for generating a Spreadsheet";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetWorksheet> GET_TITLE = new AmiAbstractMemberMethod<AmiWebSpreadSheetWorksheet>(AmiWebSpreadSheetWorksheet.class,
			"getTitle", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetWorksheet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTitle();
		}

		@Override
		protected String getHelp() {
			return "Gets the name of the underlying sheet";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetWorksheet> SET_TITLE = new AmiAbstractMemberMethod<AmiWebSpreadSheetWorksheet>(AmiWebSpreadSheetWorksheet.class,
			"setTitle", String.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetWorksheet targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.setTitle((String) params[0]);
			return null;
		}

		protected String[] buildParamNames() {
			return new String[] { "title" };
		}

		@Override
		protected String getHelp() {
			return "Sets the name of the underlying sheet";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetWorksheet> GET_TABLE = new AmiAbstractMemberMethod<AmiWebSpreadSheetWorksheet>(AmiWebSpreadSheetWorksheet.class,
			"getTable", Table.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetWorksheet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTable();
		}

		@Override
		protected String getHelp() {
			return "Gets the underlying table object";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetWorksheet> ADD_COL_WITH_FORMULA = new AmiAbstractMemberMethod<AmiWebSpreadSheetWorksheet>(
			AmiWebSpreadSheetWorksheet.class, "addColWithFormula", Boolean.class, Integer.class, String.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetWorksheet targetObject, Object[] params, DerivedCellCalculator caller) {
			int pos = (Integer) params[0];
			String colName = (String) params[1];
			String formula = (String) params[2];
			return targetObject.createColWithFormula(pos, colName, formula);
		}

		protected String[] buildParamNames() {
			return new String[] { "position", "colName", "formula" };
		}

		@Override
		protected String getHelp() {
			return "Creates a new formula based column - use `exact column name` to automatically replace with row values (For example: addColWithFormula(2,\"col3\",\"`col1` * `col2`\"); col3 -> A#*B#)";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	@Override
	public String getVarTypeName() {
		return "SpreadSheetWorksheet";
	}
	@Override
	public String getVarTypeDescription() {
		return "Builder for generating a Spreadsheet Worksheet";
	}
	@Override
	public Class<AmiWebSpreadSheetWorksheet> getVarType() {
		return AmiWebSpreadSheetWorksheet.class;
	}
	@Override
	public Class<AmiWebSpreadSheetWorksheet> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_SpreadSheetWorksheet INSTANCE = new AmiWebScriptMemberMethods_SpreadSheetWorksheet();
}
