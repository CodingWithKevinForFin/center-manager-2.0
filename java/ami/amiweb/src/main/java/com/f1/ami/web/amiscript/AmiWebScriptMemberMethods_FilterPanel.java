package com.f1.ami.web.amiscript;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.filter.AmiWebFilterLink;
import com.f1.ami.web.filter.AmiWebFilterPortlet;
import com.f1.base.Row;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FilterPanel extends AmiWebScriptBaseMemberMethods<AmiWebFilterPortlet> {

	private AmiWebScriptMemberMethods_FilterPanel() {
		super();

		addMethod(IS_CLEAR_ON_REQUERY, "clearOnRequery");
		addMethod(IS_AUTO_APPLY_FILTER, "autoApplyFilter");
		addMethod(GET_FILTER_TYPE, "filterType");
		addMethod(GET_LINKED_DATAMODELS, "linkedDatamodels");
		addMethod(GET_FORMULA_FOR_LINKED_DATAMODELS);
		addMethod(GET_DATAMODEL, "datamodel");
		addMethod(GET_MAXIMUM_CHECKBOXES, "maximumCheckboxes");
		addMethod(GET_SELECTED_ROWS, "selectedRows");
		addMethod(GET_FORMAT_EXPRESSION);
		addMethod(GET_COLOR_EXPRESSION);
		addMethod(GET_SORT_EXPRESSION);
		addMethod(SET_FORMAT_EXPRESSION);
		addMethod(SET_COLOR_EXPRESSION);
		addMethod(SET_SORT_EXPRESSION);
		addMethod(SET_MAXIMUM_OPTIONS);
		addMethod(GET_MAXIMUM_OPTIONS, "maximumOptions");
		addMethod(SET_FILTER_TYPE);
		addMethod(GET_TITLE, "filterTitle");
	}

	@Override
	public String getVarTypeName() {
		return "FilterPanel";
	}

	@Override
	public String getVarTypeDescription() {
		return "Panel for Filters";
	}

	@Override
	public Class<AmiWebFilterPortlet> getVarType() {
		return AmiWebFilterPortlet.class;
	}

	@Override
	public Class<AmiWebFilterPortlet> getVarDefaultImpl() {
		return null;
	}

	private static final AmiAbstractMemberMethod<AmiWebFilterPortlet> IS_CLEAR_ON_REQUERY = new AmiAbstractMemberMethod<AmiWebFilterPortlet>(AmiWebFilterPortlet.class,
			"isClearOnRequery", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFilterPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getClearOnRequery();
		}

		@Override
		protected String getHelp() {
			return "Returns the boolean status of the ClearOnRequery flag. If set to true, the filter and its result set will be cleared on requery, vice versa.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebFilterPortlet> IS_AUTO_APPLY_FILTER = new AmiAbstractMemberMethod<AmiWebFilterPortlet>(AmiWebFilterPortlet.class,
			"isAutoApplyFilter", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFilterPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getIsApplyToSourceTable();
		}

		@Override
		protected String getHelp() {
			return "Indicates whether filter will be applied eveytime there is a change on the input (i.e. checkboxes, dropdown etc.)";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebFilterPortlet> GET_FILTER_TYPE = new AmiAbstractMemberMethod<AmiWebFilterPortlet>(AmiWebFilterPortlet.class, "getFilterType",
			String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFilterPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			switch (targetObject.getDisplayType(true)) {
				case AmiWebFilterPortlet.DISPLAY_TYPE_CHECKBOXES:
					return "checkboxes";
				case AmiWebFilterPortlet.DISPLAY_TYPE_SELECT_SINGLE:
					return "dropdown";
				case AmiWebFilterPortlet.DISPLAY_TYPE_RANGE:
					return "range";
				case AmiWebFilterPortlet.DISPLAY_TYPE_SEARCH:
					return "search";
				case AmiWebFilterPortlet.DISPLAY_TYPE_RANGE_SLIDER:
					return "range_slider";
				case AmiWebFilterPortlet.DISPLAY_TYPE_MULTICHECKBOX:
					return "multi_checkbox";
				case AmiWebFilterPortlet.DISPLAY_TYPE_RADIOS:
					return "radios";
				default:
					return null;
			}
		}

		@Override
		protected String getHelp() {
			return "Returns the type of filter to be used. Expected return values: checkboxes|dropdown|range|range_slider|search";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebFilterPortlet> GET_DATAMODEL = new AmiAbstractMemberMethod<AmiWebFilterPortlet>(AmiWebFilterPortlet.class, "getDatamodel",
			AmiWebDm.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFilterPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getDm().getDm();
		}

		@Override
		protected String getHelp() {
			return "Returns the underlyting datamodel used by the filter.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebFilterPortlet> GET_LINKED_DATAMODELS = new AmiAbstractMemberMethod<AmiWebFilterPortlet>(AmiWebFilterPortlet.class,
			"getLinkedDatamodels", Set.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFilterPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			IntKeyMap<AmiWebFilterLink> links = targetObject.getLinks();
			Set<String> linkedDatamodels = new HashSet<String>();
			for (int key : links.getKeys()) {
				AmiWebFilterLink filterLink = links.get(key);
				linkedDatamodels.add(filterLink.getTargetDmAliasDotName());
			}
			return linkedDatamodels;
		}

		@Override
		protected String getHelp() {
			return "Returns a set of linked datamodels used by the filter. Returns an empty collection if no linked datamodels are present.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebFilterPortlet> GET_FORMULA_FOR_LINKED_DATAMODELS = new AmiAbstractMemberMethod<AmiWebFilterPortlet>(
			AmiWebFilterPortlet.class, "getFormulaForLinkedDatamodel", String.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFilterPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String datamodelId = Caster_String.INSTANCE.cast(params[0]);
			IntKeyMap<AmiWebFilterLink> links = targetObject.getLinks();
			if (SH.is(datamodelId)) {
				for (int key : links.getKeys()) {
					AmiWebFilterLink filterLink = links.get(key);
					if (datamodelId.equals(filterLink.getTargetDmAliasDotName()))
						return filterLink.getFormula();
				}
			}
			return null;
		}

		@Override
		protected String getHelp() {
			return "Returns the formula used by the linked datamodel specified by the datamodelId. Null if datamodelId does not exist.";
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "datamodelId" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "id for the linked datamodel, ex: Country" };
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebFilterPortlet> GET_MAXIMUM_CHECKBOXES = new AmiAbstractMemberMethod<AmiWebFilterPortlet>(AmiWebFilterPortlet.class,
			"getMaximumCheckboxes", Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFilterPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject.getDisplayType(true) == AmiWebFilterPortlet.DISPLAY_TYPE_CHECKBOXES
					|| targetObject.getDisplayType(true) == AmiWebFilterPortlet.DISPLAY_TYPE_MULTICHECKBOX
					|| targetObject.getDisplayType(true) == AmiWebFilterPortlet.DISPLAY_TYPE_RADIOS)
				return targetObject.getMaxOptions(true);
			return null;
		}

		@Override
		protected String getHelp() {
			return "Returns the maximum number of checkboxes the filter panel can have. Null if filter does not use checkbox(es).";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebFilterPortlet> GET_SELECTED_ROWS = new AmiAbstractMemberMethod<AmiWebFilterPortlet>(AmiWebFilterPortlet.class,
			"getSelectedRows", List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFilterPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			HasherSet<Row> l = targetObject.getSelectedRows();
			return targetObject.getSelectedRows() != null ? CH.l(targetObject.getSelectedRows()) : CH.emptyList(Row.class);
		}

		@Override
		protected String getHelp() {
			return "Returns a list of selected rows resulting from the filter. Empty list if nothing is selected.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebFilterPortlet> GET_FORMAT_EXPRESSION = new AmiAbstractMemberMethod<AmiWebFilterPortlet>(AmiWebFilterPortlet.class,
			"getFormatExpression", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFilterPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getFormatExpression(true);
		}

		@Override
		protected String getHelp() {
			return "Returns the format expression";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebFilterPortlet> GET_COLOR_EXPRESSION = new AmiAbstractMemberMethod<AmiWebFilterPortlet>(AmiWebFilterPortlet.class,
			"getColorExpression", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFilterPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getColorExpression(true);
		}

		@Override
		protected String getHelp() {
			return "Returns the color expression";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebFilterPortlet> GET_SORT_EXPRESSION = new AmiAbstractMemberMethod<AmiWebFilterPortlet>(AmiWebFilterPortlet.class,
			"getSortExpression", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFilterPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSortExpression(true);
		}

		@Override
		protected String getHelp() {
			return "Returns the sort expression";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebFilterPortlet> SET_FORMAT_EXPRESSION = new AmiAbstractMemberMethod<AmiWebFilterPortlet>(AmiWebFilterPortlet.class,
			"setFormatExpression", Boolean.class, false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFilterPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String formula = (String) params[0];
			targetObject.setFormatExpressionOverride(formula);
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "formatExpression" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "format expression" };
		}
		@Override
		protected String getHelp() {
			return "Sets the format expression";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebFilterPortlet> SET_COLOR_EXPRESSION = new AmiAbstractMemberMethod<AmiWebFilterPortlet>(AmiWebFilterPortlet.class,
			"setColorExpression", Boolean.class, false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFilterPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String formula = (String) params[0];
			targetObject.setColorExpressionOverride(SH.doubleQuote(formula));
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "formula" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "color expression in format of (\"\\\"formula\\\"\")" };
		}
		@Override
		protected String getHelp() {
			return "Sets the color expression of the filter with the given string.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebFilterPortlet> SET_SORT_EXPRESSION = new AmiAbstractMemberMethod<AmiWebFilterPortlet>(AmiWebFilterPortlet.class,
			"setSortExpression", Boolean.class, false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFilterPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String formula = (String) params[0];
			targetObject.setSortExpressionOverride(formula);
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "sortExpression" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "sort expression" };
		}
		@Override
		protected String getHelp() {
			return "Sets the sort expression of the filter panel with the given string.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebFilterPortlet> SET_MAXIMUM_OPTIONS = new AmiAbstractMemberMethod<AmiWebFilterPortlet>(AmiWebFilterPortlet.class,
			"setMaximumOptions", Boolean.class, false, Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFilterPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer max = (Integer) params[0];

			if (max == null) {
				return false;
			}

			if (targetObject.getDisplayType(true) == AmiWebFilterPortlet.DISPLAY_TYPE_CHECKBOXES
					|| targetObject.getDisplayType(true) == AmiWebFilterPortlet.DISPLAY_TYPE_MULTICHECKBOX
					|| targetObject.getDisplayType(true) == AmiWebFilterPortlet.DISPLAY_TYPE_RADIOS) {
				targetObject.setMaxOptions(max, true);
				return true;
			}
			return false;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "max" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "maximum options" };
		}
		@Override
		protected String getHelp() {
			return "Sets the maximum number of checkboxes or radios. This only applies for checkboxes, multi_checkboxes, and radios.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebFilterPortlet> SET_FILTER_TYPE = new AmiAbstractMemberMethod<AmiWebFilterPortlet>(AmiWebFilterPortlet.class, "setFilterType",
			Boolean.class, false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFilterPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String type = (String) params[0];
			if (type == null) {
				return false;
			}
			switch (type) {
				case "checkboxes":
					targetObject.setDisplayType((byte) 1, true);
					return true;
				case "dropdown":
					targetObject.setDisplayType((byte) 2, true);
					return true;
				case "range":
					targetObject.setDisplayType((byte) 3, true);
					return true;
				case "search":
					targetObject.setDisplayType((byte) 4, true);
					return true;
				case "slider":
					targetObject.setDisplayType((byte) 5, true);
					return true;
				case "multicheckbox":
					targetObject.setDisplayType((byte) 6, true);
					return true;
				case "radios":
					targetObject.setDisplayType((byte) 7, true);
					return true;
				default:
					targetObject.setDisplayType((byte) 1, true);
					return true;
			}
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "type" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "display type. Accepted input: checkboxes, radios, multicheckbox, slider, search, range, dropdown. Default is checkboxes." };
		}
		@Override
		protected String getHelp() {
			return "Sets the display type of the filter panel with the given value";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebFilterPortlet> GET_MAXIMUM_OPTIONS = new AmiAbstractMemberMethod<AmiWebFilterPortlet>(AmiWebFilterPortlet.class,
			"getMaximumOptions", Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFilterPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject.getDisplayType(true) == AmiWebFilterPortlet.DISPLAY_TYPE_CHECKBOXES
					|| targetObject.getDisplayType(true) == AmiWebFilterPortlet.DISPLAY_TYPE_MULTICHECKBOX
					|| targetObject.getDisplayType(true) == AmiWebFilterPortlet.DISPLAY_TYPE_RADIOS)
				return targetObject.getMaxOptions(true);
			return null;
		}

		@Override
		protected String getHelp() {
			return "Returns the maximum number of options the filter panel can have. Null if filter does not use checkbox(es) or radios.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebFilterPortlet> GET_TITLE = new AmiAbstractMemberMethod<AmiWebFilterPortlet>(AmiWebFilterPortlet.class, "getTitle",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFilterPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getAmiTitle(true);
		}
		@Override
		protected String getHelp() {
			return "Returns the name of the filter panel.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final static AmiWebScriptMemberMethods_FilterPanel INSTANCE = new AmiWebScriptMemberMethods_FilterPanel();
}