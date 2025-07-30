package com.f1.ami.web.amiscript;

import java.util.List;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.base.Table;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

import spreadsheet.AmiWebSpreadSheetFlexsheet;

public class AmiWebScriptMemberMethods_SpreadSheetFlexsheet extends AmiWebScriptBaseMemberMethods<AmiWebSpreadSheetFlexsheet> {
	private AmiWebScriptMemberMethods_SpreadSheetFlexsheet() {
		super();
		addMethod(INIT);
		addMethod(SET_TITLE);
		addMethod(GET_TITLE);
		addMethod(SET_VALUE);
		addMethod(SET_VALUE2);
		addMethod(GET_VALUE);
		addMethod(GET_RAW_VALUE);
		addMethod(SET_STYLE);
		addMethod(GET_STYLE);
		addMethod(SET_FORMULA);
		addMethod(GET_FORMULA);
		addMethod(GET_VALUE_NAMED_RANGE);
		addMethod(GET_RAW_VALUE_NAMED_RANGE);
		addMethod(SET_VALUES_NAMED_RANGE);
		addMethod(GET_VALUES);
		addMethod(GET_RAW_VALUES);
		addMethod(GET_VALUES_NAMED_RANGE);
		addMethod(GET_RAW_VALUES_NAMED_RANGE);
		addMethod(CLEAR_CELL);
		addMethod(CLEAR_CELL_NAMED_RANGE);
	}

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet> INIT = new AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet>(AmiWebSpreadSheetFlexsheet.class, null,
			AmiWebSpreadSheetFlexsheet.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetFlexsheet targetObject, Object[] params, DerivedCellCalculator caller) {
			return new AmiWebSpreadSheetFlexsheet();
		}

		@Override
		protected String getHelp() {
			return "Creates an empty Flexsheet";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet> GET_TITLE = new AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet>(AmiWebSpreadSheetFlexsheet.class,
			"getTitle", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetFlexsheet targetObject, Object[] params, DerivedCellCalculator caller) {
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

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet> SET_TITLE = new AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet>(AmiWebSpreadSheetFlexsheet.class,
			"setTitle", String.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetFlexsheet targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.setTitle((String) params[0]);
			return null;
		}

		protected String[] buildParamNames() {
			return new String[] { "title" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Title to be set for the sheet" };
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

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet> SET_VALUE = new AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet>(AmiWebSpreadSheetFlexsheet.class,
			"setValue", Boolean.class, String.class, Object.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetFlexsheet targetObject, Object[] params, DerivedCellCalculator caller) {
			String dimensions = SH.toUpperCase((String) params[0]);
			Object value = (Object) params[1];
			targetObject.setValue(dimensions, value);
			return true;
		}

		protected String[] buildParamNames() {
			return new String[] { "dimensions", "value" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Excel dimensions to be applied (e.g. A2, or A2:C4 for a range)", "Value to be set" };
		}

		@Override
		protected String getHelp() {
			return "Sets the value for the specified range of cells";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet> SET_VALUE2 = new AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet>(AmiWebSpreadSheetFlexsheet.class,
			"setValue", Boolean.class, String.class, Table.class, Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetFlexsheet targetObject, Object[] params, DerivedCellCalculator caller) {
			String dimension = SH.toUpperCase((String) params[0]);
			Table value = (Table) params[1];
			Boolean useHeader = (Boolean) params[2];
			targetObject.setValue(dimension, value, useHeader);
			return true;
		}

		protected String[] buildParamNames() {
			return new String[] { "dimension", "value", "useHeader" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Upper left excel dimension to be used (e.g. A2)", "Table to be set", "Should the table's header be applied" };
		}

		@Override
		protected String getHelp() {
			return "Sets the value for the specified range of cells using a table. Expects the top left position of the table as the input dimension";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet> GET_VALUE = new AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet>(AmiWebSpreadSheetFlexsheet.class,
			"getValue", Object.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetFlexsheet targetObject, Object[] params, DerivedCellCalculator caller) {
			String dimension = SH.toUpperCase((String) params[0]);
			return targetObject.getValue(dimension, false);
		}

		protected String[] buildParamNames() {
			return new String[] { "dimension" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Excel dimensions to be used (e.g. A2, or A2:C4 for a range)" };
		}

		@Override
		protected String getHelp() {
			return "Gets the value of a single cell. Note that this is the underlying excel value not what excel might compute";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet> GET_RAW_VALUE = new AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet>(
			AmiWebSpreadSheetFlexsheet.class, "getRawValue", Object.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetFlexsheet targetObject, Object[] params, DerivedCellCalculator caller) {
			String dimension = SH.toUpperCase((String) params[0]);
			return targetObject.getValue(dimension, true);
		}

		protected String[] buildParamNames() {
			return new String[] { "dimension" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Excel dimensions to be used (e.g. A2, or A2:C4 for a range)" };
		}

		@Override
		protected String getHelp() {
			return "Gets the raw value of a single cell. Note that this is the underlying excel value not what excel might compute";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet> GET_VALUE_NAMED_RANGE = new AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet>(
			AmiWebSpreadSheetFlexsheet.class, "getValueNamedRange", Object.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetFlexsheet targetObject, Object[] params, DerivedCellCalculator caller) {
			String namedRange = (String) params[0];
			return targetObject.getValueNamedRange(namedRange, false);
		}

		protected String[] buildParamNames() {
			return new String[] { "namedRange" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Named excel dimensions to be used" };
		}

		@Override
		protected String getHelp() {
			return "Gets the value of a specified named range";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet> GET_RAW_VALUE_NAMED_RANGE = new AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet>(
			AmiWebSpreadSheetFlexsheet.class, "getRawValueNamedRange", Object.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetFlexsheet targetObject, Object[] params, DerivedCellCalculator caller) {
			String namedRange = (String) params[0];
			return targetObject.getValueNamedRange(namedRange, true);
		}

		protected String[] buildParamNames() {
			return new String[] { "namedRange" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Named excel dimensions to be used" };
		}

		@Override
		protected String getHelp() {
			return "Gets the value of a specified named range";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet> SET_STYLE = new AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet>(AmiWebSpreadSheetFlexsheet.class,
			"setStyle", Boolean.class, String.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetFlexsheet targetObject, Object[] params, DerivedCellCalculator caller) {
			String dimensions = SH.toUpperCase((String) params[0]);
			Integer value = (Integer) params[1];
			return targetObject.setStyle(dimensions, value);
		}

		protected String[] buildParamNames() {
			return new String[] { "dimensions", "id" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Excel dimensions to be applied (e.g. A2, or A2:C4 for a range)", "Style id to be set (use getStyle to retrieve an existing id)" };
		}

		@Override
		protected String getHelp() {
			return "Sets the style id for the specified range of cells";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet> GET_STYLE = new AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet>(AmiWebSpreadSheetFlexsheet.class,
			"getStyle", Integer.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetFlexsheet targetObject, Object[] params, DerivedCellCalculator caller) {
			String dimension = SH.toUpperCase((String) params[0]);
			return targetObject.getStyle(dimension);
		}

		protected String[] buildParamNames() {
			return new String[] { "dimension" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Single excel dimension to be used (e.g. A2)" };
		}

		@Override
		protected String getHelp() {
			return "Gets the style id for a specified range cell, returns null if it does not exist";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet> SET_VALUES_NAMED_RANGE = new AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet>(
			AmiWebSpreadSheetFlexsheet.class, "setValueNamedRange", Boolean.class, String.class, Object.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetFlexsheet targetObject, Object[] params, DerivedCellCalculator caller) {
			String namedRange = (String) params[0];
			Object value = (Object) params[1];
			return targetObject.setValueNamedRange(namedRange, value);
		}

		protected String[] buildParamNames() {
			return new String[] { "namedRange", "value" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Named excel dimensions to be used", "Value to be set" };
		}

		@Override
		protected String getHelp() {
			return "Sets the value for the specified named range of cells";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet> GET_VALUES = new AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet>(AmiWebSpreadSheetFlexsheet.class,
			"getValues", Table.class, String.class, Boolean.class, List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetFlexsheet targetObject, Object[] params, DerivedCellCalculator caller) {
			String dimension = SH.toUpperCase((String) params[0]);
			Boolean hasHeader = (Boolean) params[1];
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) params[2];
			Class<?>[] classArray = null;

			if (list != null && !list.isEmpty()) {
				classArray = new Class<?>[list.size()];
				for (int i = 0; i < list.size(); ++i) {
					String s = SH.toLowerCase(list.get(i));
					if (s.equals("boolean"))
						classArray[i] = Boolean.class;
					else if (s.equals("integer"))
						classArray[i] = Integer.class;
					else if (s.equals("float"))
						classArray[i] = Float.class;
					else if (s.equals("double"))
						classArray[i] = Double.class;
					else if (s.equals("long"))
						classArray[i] = Long.class;
					else
						classArray[i] = String.class;
				}
			}
			return targetObject.getValues(dimension, hasHeader, classArray, false);
		}

		protected String[] buildParamNames() {
			return new String[] { "dimension", "hasHeader", "classes" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Excel dimensions to be retrieved (e.g. A2:C4). Only works for ranges", "Does the range include a table header",
					"List of expected column class types, use null to skip, speeds up parsing by removing type deduction (Supported: \"String\",\"Boolean\",\"Integer\",\"Long\",\"Float\",\"Double\")" };
		}

		@Override
		protected String getHelp() {
			return "Returns the specified range of cells as a table";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet> GET_RAW_VALUES = new AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet>(
			AmiWebSpreadSheetFlexsheet.class, "getRawValues", Table.class, String.class, Boolean.class, List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetFlexsheet targetObject, Object[] params, DerivedCellCalculator caller) {
			String dimension = SH.toUpperCase((String) params[0]);
			Boolean hasHeader = (Boolean) params[1];
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) params[2];
			Class<?>[] classArray = null;

			if (list != null && !list.isEmpty()) {
				classArray = new Class<?>[list.size()];
				for (int i = 0; i < list.size(); ++i) {
					String s = SH.toLowerCase(list.get(i));
					if (s.equals("boolean"))
						classArray[i] = Boolean.class;
					else if (s.equals("integer"))
						classArray[i] = Integer.class;
					else if (s.equals("float"))
						classArray[i] = Float.class;
					else if (s.equals("double"))
						classArray[i] = Double.class;
					else if (s.equals("long"))
						classArray[i] = Long.class;
					else
						classArray[i] = String.class;
				}
			}
			return targetObject.getValues(dimension, hasHeader, classArray, true);
		}

		protected String[] buildParamNames() {
			return new String[] { "dimension", "hasHeader", "classes" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Excel dimensions to be retrieved (e.g. A2:C4). Only works for ranges", "Does the range include a table header",
					"List of expected column class types, use null to skip, speeds up parsing by removing type deduction (Supported: \"String\",\"Boolean\",\"Integer\",\"Long\",\"Float\",\"Double\")" };
		}

		@Override
		protected String getHelp() {
			return "Returns the raw value of the specified range of cells as a table";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet> GET_VALUES_NAMED_RANGE = new AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet>(
			AmiWebSpreadSheetFlexsheet.class, "getValuesNamedRange", Table.class, String.class, Boolean.class, List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetFlexsheet targetObject, Object[] params, DerivedCellCalculator caller) {
			String namedRange = (String) params[0];
			Boolean hasHeader = (Boolean) params[1];
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) params[2];
			Class<?>[] classArray = null;

			if (list != null && !list.isEmpty()) {
				classArray = new Class<?>[list.size()];
				for (int i = 0; i < list.size(); ++i) {
					String s = SH.toLowerCase(list.get(i));
					if (s.equals("boolean"))
						classArray[i] = Boolean.class;
					else if (s.equals("integer"))
						classArray[i] = Integer.class;
					else if (s.equals("float"))
						classArray[i] = Float.class;
					else if (s.equals("double"))
						classArray[i] = Double.class;
					else if (s.equals("long"))
						classArray[i] = Long.class;
					else
						classArray[i] = String.class;
				}
			}
			return targetObject.getValuesNamedRange(namedRange, hasHeader, classArray, false);
		}

		protected String[] buildParamNames() {
			return new String[] { "namedRange", "hasHeader", "classes" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Named excel range to be retrieved", "Does the range include a table header",
					"List of expected column class types, use null to skip, speeds up parsing by removing type deduction (Supported: \"String\",\"Boolean\",\"Integer\",\"Long\",\"Float\",\"Double\")" };
		}

		@Override
		protected String getHelp() {
			return "Returns the specified named range as a table";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet> GET_RAW_VALUES_NAMED_RANGE = new AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet>(
			AmiWebSpreadSheetFlexsheet.class, "getRawValuesNamedRange", Table.class, String.class, Boolean.class, List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetFlexsheet targetObject, Object[] params, DerivedCellCalculator caller) {
			String namedRange = (String) params[0];
			Boolean hasHeader = (Boolean) params[1];
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) params[2];
			Class<?>[] classArray = null;

			if (list != null && !list.isEmpty()) {
				classArray = new Class<?>[list.size()];
				for (int i = 0; i < list.size(); ++i) {
					String s = SH.toLowerCase(list.get(i));
					if (s.equals("boolean"))
						classArray[i] = Boolean.class;
					else if (s.equals("integer"))
						classArray[i] = Integer.class;
					else if (s.equals("float"))
						classArray[i] = Float.class;
					else if (s.equals("double"))
						classArray[i] = Double.class;
					else if (s.equals("long"))
						classArray[i] = Long.class;
					else
						classArray[i] = String.class;
				}
			}
			return targetObject.getValuesNamedRange(namedRange, hasHeader, classArray, true);
		}

		protected String[] buildParamNames() {
			return new String[] { "namedRange", "hasHeader", "classes" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Named excel range to be retrieved", "Does the range include a table header",
					"List of expected column class types, use null to skip, speeds up parsing by removing type deduction (Supported: \"String\",\"Boolean\",\"Integer\",\"Long\",\"Float\",\"Double\")" };
		}

		@Override
		protected String getHelp() {
			return "Returns the raw value of the the specified named range as a table";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet> CLEAR_CELL = new AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet>(AmiWebSpreadSheetFlexsheet.class,
			"clearCell", Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetFlexsheet targetObject, Object[] params, DerivedCellCalculator caller) {
			String dimension = SH.toUpperCase((String) params[0]);
			return targetObject.clearCell(dimension);
		}

		protected String[] buildParamNames() {
			return new String[] { "dimension" };
		}

		@Override
		protected String getHelp() {
			return "Clears the specified range of cells (removes style and value)";
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Excel dimensions to be cleared (e.g. A2, or A2:C4)" };
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet> CLEAR_CELL_NAMED_RANGE = new AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet>(
			AmiWebSpreadSheetFlexsheet.class, "clearCellNamedRange", Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetFlexsheet targetObject, Object[] params, DerivedCellCalculator caller) {
			String namedRange = (String) params[0];
			return targetObject.clearCellNamedRange(namedRange);
		}

		protected String[] buildParamNames() {
			return new String[] { "namedRange" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Excel named range to be cleared" };
		}

		@Override
		protected String getHelp() {
			return "Clears the specified named range of cells";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet> SET_FORMULA = new AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet>(AmiWebSpreadSheetFlexsheet.class,
			"setFormula", Boolean.class, String.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetFlexsheet targetObject, Object[] params, DerivedCellCalculator caller) {
			String dimensions = SH.toUpperCase((String) params[0]);
			String value = (String) params[1];
			return targetObject.setFormula(dimensions, value);
		}

		protected String[] buildParamNames() {
			return new String[] { "dimensions", "id" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Excel dimensions to be applied (e.g. A2, or A2:C4 for a range)", "Formula to be used" };
		}

		@Override
		protected String getHelp() {
			return "Sets the cell formula for the specified range of cells. Note that formulas do not affect the cell's value";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet> GET_FORMULA = new AmiAbstractMemberMethod<AmiWebSpreadSheetFlexsheet>(AmiWebSpreadSheetFlexsheet.class,
			"getFormula", String.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSpreadSheetFlexsheet targetObject, Object[] params, DerivedCellCalculator caller) {
			String dimension = SH.toUpperCase((String) params[0]);
			return targetObject.getFormula(dimension);
		}

		protected String[] buildParamNames() {
			return new String[] { "dimension" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Single excel dimension to be used (e.g. A2)" };
		}

		@Override
		protected String getHelp() {
			return "Gets the current formula for a specified cell, returns null if it does not exist";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "SpreadSheetFlexsheet";
	}
	@Override
	public String getVarTypeDescription() {
		return "Builder for generating a Spreadsheet Worksheet";
	}
	@Override
	public Class<AmiWebSpreadSheetFlexsheet> getVarType() {
		return AmiWebSpreadSheetFlexsheet.class;
	}
	@Override
	public Class<AmiWebSpreadSheetFlexsheet> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_SpreadSheetFlexsheet INSTANCE = new AmiWebScriptMemberMethods_SpreadSheetFlexsheet();
}
