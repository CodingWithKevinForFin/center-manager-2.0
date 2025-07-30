package com.f1.ami.web.amiscript;

import java.util.LinkedHashSet;
import java.util.Set;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.suite.web.table.impl.WebTableFilteredInFilter;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_TablePanelColumnFilter extends AmiWebScriptBaseMemberMethods<WebTableFilteredInFilter> {

	private AmiWebScriptMemberMethods_TablePanelColumnFilter() {
		super();
		addMethod(GET_VALUES, "values");
		addMethod(GET_IS_PATTERN, "isPattern");
		addMethod(GET_IS_SHOW, "isShow");
		addMethod(GET_IS_INCLUDE_NULL, "includeNull");
		addMethod(GET_IS_MAX_VALUE_INCLUSIVE, "getIsMaxValueInclusive");
		addMethod(GET_IS_MIN_VALUE_INCLUSIVE, "getIsMinValueInclusive");
		addMethod(GET_INPUT_TEXT, "inputText");
		addMethod(GET_MAX_VALUE, "maxValue");
		addMethod(GET_MIN_VALUE, "minValue");
	}

	private static final AmiAbstractMemberMethod<WebTableFilteredInFilter> GET_VALUES = new AmiAbstractMemberMethod<WebTableFilteredInFilter>(WebTableFilteredInFilter.class,
			"getValues", Set.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, WebTableFilteredInFilter targetObject, Object[] params, DerivedCellCalculator caller) {
			Set<String> t = targetObject.getValues();
			if (t == null)
				return null;
			else
				return new LinkedHashSet<String>(t);
		}
		@Override
		protected String getHelp() {
			return "Returns a set of values filtered on, or null if no values are set.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<WebTableFilteredInFilter> GET_IS_PATTERN = new AmiAbstractMemberMethod<WebTableFilteredInFilter>(WebTableFilteredInFilter.class,
			"getIsPattern", Boolean.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, WebTableFilteredInFilter targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getIsPattern();
		}
		@Override
		protected String getHelp() {
			return "Returns true if the filter is a pattern match. False otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<WebTableFilteredInFilter> GET_IS_SHOW = new AmiAbstractMemberMethod<WebTableFilteredInFilter>(WebTableFilteredInFilter.class,
			"getIsShow", Boolean.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, WebTableFilteredInFilter targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getKeep();
		}
		@Override
		protected String getHelp() {
			return "Returns true if the filter showing (vs hiding) matches, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<WebTableFilteredInFilter> GET_IS_INCLUDE_NULL = new AmiAbstractMemberMethod<WebTableFilteredInFilter>(
			WebTableFilteredInFilter.class, "getIsIncludeNull", Boolean.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, WebTableFilteredInFilter targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getIncludeNull();
		}
		@Override
		protected String getHelp() {
			return "Returns true if the filter is including null.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<WebTableFilteredInFilter> GET_IS_MAX_VALUE_INCLUSIVE = new AmiAbstractMemberMethod<WebTableFilteredInFilter>(
			WebTableFilteredInFilter.class, "getIsMaxValueInclusive", Boolean.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, WebTableFilteredInFilter targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMaxInclusive();
		}
		@Override
		protected String getHelp() {
			return "Returns true if the filter is including the max value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<WebTableFilteredInFilter> GET_IS_MIN_VALUE_INCLUSIVE = new AmiAbstractMemberMethod<WebTableFilteredInFilter>(
			WebTableFilteredInFilter.class, "getIsMinValueInclusive", Boolean.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, WebTableFilteredInFilter targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMinInclusive();
		}
		@Override
		protected String getHelp() {
			return "Returns true if the filter is including the min value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<WebTableFilteredInFilter> GET_INPUT_TEXT = new AmiAbstractMemberMethod<WebTableFilteredInFilter>(WebTableFilteredInFilter.class,
			"getInputText", String.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, WebTableFilteredInFilter targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject == null)
				return null;
			return targetObject.isSimple() ? targetObject.getSimpleValue() : null;
		}
		@Override
		protected String getHelp() {
			return "Returns the input text for the column filter.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<WebTableFilteredInFilter> GET_MAX_VALUE = new AmiAbstractMemberMethod<WebTableFilteredInFilter>(WebTableFilteredInFilter.class,
			"getMaxValue", String.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, WebTableFilteredInFilter targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMax();
		}
		@Override
		protected String getHelp() {
			return "Returns the max value to filter on, or null if none is set.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<WebTableFilteredInFilter> GET_MIN_VALUE = new AmiAbstractMemberMethod<WebTableFilteredInFilter>(WebTableFilteredInFilter.class,
			"getMinValue", String.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, WebTableFilteredInFilter targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMin();
		}
		@Override
		protected String getHelp() {
			return "Returns the min value to filter on, or null if none is set.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "TablePanelColumnFilter";
	}

	@Override
	public String getVarTypeDescription() {
		return "The filter for a table panel";
	}

	@Override
	public Class<WebTableFilteredInFilter> getVarType() {
		return WebTableFilteredInFilter.class;
	}

	@Override
	public Class<WebTableFilteredInFilter> getVarDefaultImpl() {
		return WebTableFilteredInFilter.class;
	}

	public final static AmiWebScriptMemberMethods_TablePanelColumnFilter INSTANCE = new AmiWebScriptMemberMethods_TablePanelColumnFilter();
}
