package com.f1.ami.web.amiscript;

import java.util.List;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebTreemapPortlet;
import com.f1.base.Bytes;
import com.f1.base.Table;
import com.f1.utils.CH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_HeatmapPanel extends AmiWebScriptBaseMemberMethods<AmiWebTreemapPortlet> {

	private AmiWebScriptMemberMethods_HeatmapPanel() {
		super();
		addMethod(SET_TOP_LEVEL_GROUPING_FORMULA);
		addMethod(GET_TOP_LEVEL_GROUPING_FORMULA, "topLevelGroupingFormula");
		addMethod(SET_GROUP_BY_FORMULA);
		addMethod(GET_GROUP_BY_FORMULA, "groupByFormula");
		addMethod(GET_SIZE_FORMULA, "sizeFormula");
		addMethod(SET_SIZE_FORMULA);
		addMethod(GET_HEAT_FORMULA, "heatFormula");
		addMethod(SET_HEAT_FORMULA);
		addMethod(GET_TOOLTIP_FORMULA, "tooltipFormula");
		addMethod(GET_SELECTED_ROWS);
		addMethod(GET_SELECTED, "selected");
		addMethod(SET_TOOLTIP_FORMULA);
		addMethod(GET_STICKYNESS, "stickyness");
		addMethod(GET_RATIO, "ratio");
		addMethod(SET_RATIO);
		addMethod(SET_STICKYNESS);
		//		addMethod(resetFormulas);
		//add toPng
		addMethod(TO_PNG);
		addMethod(SELECT_NODE);
		addMethod(HIGHLIGHT_NODE);
		addMethod(IS_SELECTED);
		addMethod(IS_HIGHLIGHTED);
		registerCallbackDefinition(AmiWebTreemapPortlet.CALLBACK_DEF_ONSELECTED);
	}

	@Override
	public String getVarTypeName() {
		return "HeatmapPanel";
	}

	@Override
	public String getVarTypeDescription() {
		return "Panel for Heatmap";
	}

	@Override
	public Class<AmiWebTreemapPortlet> getVarType() {
		return AmiWebTreemapPortlet.class;
	}

	@Override
	public Class<AmiWebTreemapPortlet> getVarDefaultImpl() {
		return null;
	}

	public static final AmiAbstractMemberMethod<AmiWebTreemapPortlet> GET_TOP_LEVEL_GROUPING_FORMULA = new AmiAbstractMemberMethod<AmiWebTreemapPortlet>(AmiWebTreemapPortlet.class,
			"getTopLevelGroupingFormula", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreemapPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getGroupingFormula(true);
		}

		@Override
		protected String getHelp() {
			return "Returns the formula used in the Top Level Grouping field.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public static final AmiAbstractMemberMethod<AmiWebTreemapPortlet> GET_GROUP_BY_FORMULA = new AmiAbstractMemberMethod<AmiWebTreemapPortlet>(AmiWebTreemapPortlet.class,
			"getGroupByFormula", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreemapPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getLabelFormula(true);
		}

		@Override
		protected String getHelp() {
			return "Returns the formula used in the Group By Field.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebTreemapPortlet> GET_SIZE_FORMULA = new AmiAbstractMemberMethod<AmiWebTreemapPortlet>(AmiWebTreemapPortlet.class,
			"getSizeFormula", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreemapPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSizeFormula(true);
		}

		@Override
		protected String getHelp() {
			return "Returns the formula used in the Size field.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	public static final AmiAbstractMemberMethod<AmiWebTreemapPortlet> GET_HEAT_FORMULA = new AmiAbstractMemberMethod<AmiWebTreemapPortlet>(AmiWebTreemapPortlet.class,
			"getHeatFormula", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreemapPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHeatFormula(true);
		}

		@Override
		protected String getHelp() {
			return "Returns the formula used in the Heat field.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebTreemapPortlet> GET_TOOLTIP_FORMULA = new AmiAbstractMemberMethod<AmiWebTreemapPortlet>(AmiWebTreemapPortlet.class,
			"getTooltipFormula", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreemapPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTooltipFormula(true);
		}

		@Override
		protected String getHelp() {
			return "Returns the formula used in the Tooltip field.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	//	public final AmiAbstractMemberMethod<AmiWebTreemapPortlet> getDatamodel = new AmiAbstractMemberMethod<AmiWebTreemapPortlet>(AmiWebTreemapPortlet.class, "getDatamodel",
	//			AmiWebDm.class) {
	//
	//		@Override
	//		public Object invokeMethod2(StackFrame sf,Map <String,Object> execInstance, AmiWebTreemapPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
	//			if (targetObject instanceof AmiWebTreemapStaticPortlet) {
	//				AmiWebDm dm = ((AmiWebTreemapStaticPortlet) targetObject).getDm().getDm();
	//				return service.getDmManager().getDmByAliasDotName(dm.getAliasDotName());
	//			} else
	//				return null;
	//		}
	//
	//		@Override
	//		protected String getHelp() {
	//			return "Returns the underlying datamodel for the panel. Null if panel is driven by realtime data.";
	//		}
	//
	//	};
	public static final AmiAbstractMemberMethod<AmiWebTreemapPortlet> SET_TOP_LEVEL_GROUPING_FORMULA = new AmiAbstractMemberMethod<AmiWebTreemapPortlet>(AmiWebTreemapPortlet.class,
			"setTopLevelGroupingFormula", Boolean.class, false, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreemapPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String formula = (String) params[0];
			try {
				targetObject.setGroupingFormulaOverride(formula);
			} catch (Exception e) {
				warning(sf, "Could not apply Grouping Formula", CH.m("formula", formula), e);
				return false;
			}
			return true;
		}

		protected String[] buildParamNames() {
			return new String[] { "formula" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "formula, ex: Region" };
		}

		@Override
		protected String getHelp() {
			return "Sets the formula used in the Top Level Grouping field. Returns true on success.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebTreemapPortlet> SET_GROUP_BY_FORMULA = new AmiAbstractMemberMethod<AmiWebTreemapPortlet>(AmiWebTreemapPortlet.class,
			"setGroupByFormula", Boolean.class, false, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreemapPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String formula = (String) params[0];
			try {
				targetObject.setLabelFormulaOverride(formula);
			} catch (Exception e) {
				warning(sf, "Could not apply Formula", CH.m("formula", formula), e);
				return false;
			}
			return true;
		}

		protected String[] buildParamNames() {
			return new String[] { "formula" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "formula, ex: Region" };
		}

		@Override
		protected String getHelp() {
			return "Sets the formula used in the Group By field. Returns true on success.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebTreemapPortlet> SET_SIZE_FORMULA = new AmiAbstractMemberMethod<AmiWebTreemapPortlet>(AmiWebTreemapPortlet.class,
			"setSizeFormula", Boolean.class, false, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreemapPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String formula = (String) params[0];
			try {
				targetObject.setSizeFormulaOverride(formula);
			} catch (Exception e) {
				warning(sf, "Could not apply Formula", CH.m("formula", formula), e);
				return false;
			}
			return true;
		}

		protected String[] buildParamNames() {
			return new String[] { "formula" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "formula, ex: sum(quantity)" };
		}

		@Override
		protected String getHelp() {
			return "Sets the formula used in the Size field. Returns true on success.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	public static final AmiAbstractMemberMethod<AmiWebTreemapPortlet> SET_HEAT_FORMULA = new AmiAbstractMemberMethod<AmiWebTreemapPortlet>(AmiWebTreemapPortlet.class,
			"setHeatFormula", Boolean.class, false, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreemapPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String formula = (String) params[0];
			try {
				targetObject.setHeatFormulaOverride(formula);
			} catch (Exception e) {
				warning(sf, "Could not apply Formula", CH.m("formula", formula), e);
				return false;
			}
			return true;
		}

		protected String[] buildParamNames() {
			return new String[] { "formula" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "formula, ex: sum(quantity)" };
		}

		@Override
		protected String getHelp() {
			return "Sets the formula used in heat field. Returns true upon success";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebTreemapPortlet> SET_TOOLTIP_FORMULA = new AmiAbstractMemberMethod<AmiWebTreemapPortlet>(AmiWebTreemapPortlet.class,
			"setTooltipFormula", Boolean.class, false, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreemapPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String formula = (String) params[0];
			try {
				targetObject.setTooltipFormulaOverride(formula);
			} catch (Exception e) {
				warning(sf, "Could not apply Formula", CH.m("formula", formula), e);
				return false;
			}
			return true;
		}

		protected String[] buildParamNames() {
			return new String[] { "formula" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "formula, ex: sum(quantity)" };
		}

		@Override
		protected String getHelp() {
			return "Sets the formula used in the Tooltip field. Returns true on success.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	//	public final AmiAbstractMemberMethod<AmiWebTreemapPortlet> resetFormulas = new AmiAbstractMemberMethod<AmiWebTreemapPortlet>(AmiWebTreemapPortlet.class, "resetFormulas",
	//			Boolean.class, false) {
	//
	//		@Override
	//		public Object invokeMethod2(StackFrame sf,Map <String,Object> execInstance, AmiWebTreemapPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
	//			StringBuilder errorsSink = new StringBuilder();
	//			if (targetObject.resetOverrides())
	//				targetObject.applyFormulas(errorsSink);
	//			if (errorsSink.length() > 0) {
	//				warning(execInstance, "Could not apply Formula", CH.m("Error", errorsSink), null);
	//				return false;
	//			}
	//			return true;
	//		}
	//
	//		@Override
	//		protected String getHelp() {
	//			return "Resets the formulas to the default (as set in the heatmap's Setting panel)";
	//		}
	//
	//		@Override
	//		public boolean isReadOnly() {
	//			return false;
	//		}
	//	};

	public static final AmiAbstractMemberMethod<AmiWebTreemapPortlet> GET_SELECTED = new AmiAbstractMemberMethod<AmiWebTreemapPortlet>(AmiWebTreemapPortlet.class, "getSelectedRow",
			Table.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreemapPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				Table r = targetObject.getSelectableRows(AmiWebPortlet.SELECTED);
				return r;
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String getHelp() {
			return "Returns the selected rows of this heatmap. Columns are TopGrouping, SubGrouping, Heat and Size.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebTreemapPortlet> GET_SELECTED_ROWS = new AmiAbstractMemberMethod<AmiWebTreemapPortlet>(AmiWebTreemapPortlet.class,
			"getSelectedUnderlyingRows", Table.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreemapPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				Table r = targetObject.getUnderlyingSelectableRows(AmiWebPortlet.SELECTED);
				return r;
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String getHelp() {
			return "Returns the underlying rows of the selected nodes.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public static final AmiAbstractMemberMethod<AmiWebTreemapPortlet> GET_STICKYNESS = new AmiAbstractMemberMethod<AmiWebTreemapPortlet>(AmiWebTreemapPortlet.class, "getStickyness",
			Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreemapPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return targetObject.getStickyness(true);
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String getHelp() {
			return "Returns the stickyness";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public static final AmiAbstractMemberMethod<AmiWebTreemapPortlet> GET_RATIO = new AmiAbstractMemberMethod<AmiWebTreemapPortlet>(AmiWebTreemapPortlet.class, "getRatio",
			Double.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreemapPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return targetObject.getRatio(true);
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String getHelp() {
			return "Returns the ratio";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebTreemapPortlet> SET_RATIO = new AmiAbstractMemberMethod<AmiWebTreemapPortlet>(AmiWebTreemapPortlet.class, "setRatio",
			Boolean.class, false, Double.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreemapPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Double ratio = (Double) params[0];
			targetObject.setRatio(ratio, true);
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "ratio" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "ratio" };
		}
		@Override
		protected String getHelp() {
			return "Sets the ratio of the heatmap with the given ratio.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	public static final AmiAbstractMemberMethod<AmiWebTreemapPortlet> SET_STICKYNESS = new AmiAbstractMemberMethod<AmiWebTreemapPortlet>(AmiWebTreemapPortlet.class, "setStickyness",
			Boolean.class, false, Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreemapPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer stickyness = (Integer) params[0];
			targetObject.setStickyness(stickyness, true);
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "stickyness" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "stickyness" };
		}
		@Override
		protected String getHelp() {
			return "Sets the stickyness of the heatmap with the given stickyness.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	//To do, add toPng()
	public static final AmiAbstractMemberMethod<AmiWebTreemapPortlet> TO_PNG = new AmiAbstractMemberMethod<AmiWebTreemapPortlet>(AmiWebTreemapPortlet.class, "toPng", Bytes.class,
			false, Number.class, Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreemapPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Number width = (Number) params[0];
			Number height = (Number) params[1];

			if (width == null)
				width = targetObject.getWidth();
			if (height == null)
				height = targetObject.getHeight();

			byte b[] = null;

			b = targetObject.treemapPortletToPng(width.intValue(), height.intValue());

			return b == null ? null : new Bytes(b);
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "widthPx", "heightPx" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "width of PNG image in px", "height of PNG image in px" };
		}

		@Override
		protected String getHelp() {
			return "Converts a Heatmap panel to PNG";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public static final AmiAbstractMemberMethod<AmiWebTreemapPortlet> SELECT_NODE = new AmiAbstractMemberMethod<AmiWebTreemapPortlet>(AmiWebTreemapPortlet.class, "selectNode", Boolean.class,
			false, List.class, Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreemapPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			List<String> paths = (List<String>)params[0];
			boolean select = (boolean)params[1];
			if (paths.isEmpty()) {
				return false;
			}
			return targetObject.selectNodeByGroupPath(paths, select);
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "list of path", "shouldSelect" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "The list of paths that lead to the node, starts from the top level grouping. For example, if your heatmap is grouped by Continent, then Country, then City, to select a node named Paris, pass in a list of string where the first element is Europe, second is France, last is Paris.", "If true, selects, otherwise deselects. Note that onSelected callback will fire only if selection status changed as a result." };
		}

		@Override
		protected String getHelp() {
			return "Selects the specific node given a list of grouping path. Returns true if selection has been changed as a result, false if grouping path is valid or selection did not change.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	public static final AmiAbstractMemberMethod<AmiWebTreemapPortlet> HIGHLIGHT_NODE = new AmiAbstractMemberMethod<AmiWebTreemapPortlet>(AmiWebTreemapPortlet.class, "highlightNode", Boolean.class,
			false, List.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreemapPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			List<String> paths = (List<String>)params[0];
			String borderColor = (String)params[1];
			if (paths == null)
				return false;
			return targetObject.setNodeBorderColorByGroupPath(paths,borderColor);
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "list of paths", "color" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "The list of grouping paths that lead to the node, starts from the top level grouping. For example, if your heatmap is grouped by Continent, then Country, then City, to change the border color of a node named Paris, pass in a list of string where the first element is Europe, second is France, last is Paris. Note that if top level grouping is not set in the heatmap formula, then user should use the default top level grouping name set by AMI, which is \"1.0\"", "A valid hex color, e.g. #ff0000, or null to reset to default" };
		}

		@Override
		protected String getHelp() {
			return "Highlights or resets the border of a specific node given a list of grouping path and a hex color. Returns true if border color is changed as a result, false if grouping path is invalid or color is the same as before. This will not override the selected node border color and will NOT be saved in the layout. Pass in null for color to reset border color to default.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebTreemapPortlet> IS_SELECTED = new AmiAbstractMemberMethod<AmiWebTreemapPortlet>(AmiWebTreemapPortlet.class, "isSelected", Boolean.class,
			false, List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreemapPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			List<String> paths = (List<String>)params[0];
			if (paths.isEmpty()) {
				return false;
			}
			return targetObject.isNodeSelected(paths);
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "list of paths"};
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "The list of paths that lead to the node, starts from the top level grouping. For example, if your heatmap is grouped by Continent, then Country, then City, to check the status of a node named Paris, pass in a list of string where the first element is Europe, second is France, last is Paris."};
		}

		@Override
		protected String getHelp() {
			return "Returns true if a node is selected, false if path is invalid or node is not selected.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebTreemapPortlet> IS_HIGHLIGHTED = new AmiAbstractMemberMethod<AmiWebTreemapPortlet>(AmiWebTreemapPortlet.class, "isHighlighted", Boolean.class,
			false, List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreemapPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			List<String> paths = (List<String>)params[0];
			if (paths.isEmpty()) {
				return false;
			}
			return targetObject.isNodeHighlighted(paths);
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "list of paths"};
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "The list of paths that lead to the node, starts from the top level grouping. For example, if your heatmap is grouped by Continent, then Country, then City, to check the status of a node named Paris, pass in a list of string where the first element is Europe, second is France, last is Paris."};
		}

		@Override
		protected String getHelp() {
			return "Returns true if a node is highlighted, false if path is invalid or node is not highlighted.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public final static AmiWebScriptMemberMethods_HeatmapPanel INSTANCE = new AmiWebScriptMemberMethods_HeatmapPanel();

}
