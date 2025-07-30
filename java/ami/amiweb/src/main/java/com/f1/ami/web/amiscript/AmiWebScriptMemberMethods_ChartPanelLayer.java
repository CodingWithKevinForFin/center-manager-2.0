package com.f1.ami.web.amiscript;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.amiscript.AmiService;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.charts.AmiWebChartAxisPortlet;
import com.f1.ami.web.charts.AmiWebChartFormula;
import com.f1.ami.web.charts.AmiWebChartGridPortlet;
import com.f1.ami.web.charts.AmiWebChartPlotPortlet;
import com.f1.ami.web.charts.AmiWebChartRenderingLayer;
import com.f1.ami.web.charts.AmiWebChartSeries;
import com.f1.ami.web.charts.AmiWebChartSeries_Graph;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.base.Table;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_ChartPanelLayer extends AmiWebScriptBaseMemberMethods<AmiWebChartRenderingLayer> {

	private AmiWebScriptMemberMethods_ChartPanelLayer() {
		super();
		//		this.methods = new AmiWebChartSeries_Graph(service, null, null, null);

		addMethod(GET_CHART, "chart");
		addMethod(GET_DATAMODEL, "datamodels");
		addMethod(GET_PLOT, "plot");
		addMethod(GET_X_AXIS, "xAxis");
		addMethod(GET_Y_AXIS, "yAxis");
		addMethod(GET_Z_POSITION, "zPosition");
		addMethod(GET_SELECTED, "selected");
		addMethod(GET_FORMULA);
		addMethod(GET_STYLE2);
		addMethod(GET_STYLE);
		addMethod(SET_FORMULA);
		addMethod(RESET_FORMULAS);
	}

	@Override
	public String getVarTypeName() {
		return "ChartPanelLayer";
	}

	@Override
	public String getVarTypeDescription() {
		return "Panel for Chart Layer";
	}

	@Override
	public Class<AmiWebChartRenderingLayer> getVarType() {
		return AmiWebChartRenderingLayer.class;
	}

	@Override
	public Class<AmiWebChartRenderingLayer> getVarDefaultImpl() {
		return null;
	}

	private static final AmiAbstractMemberMethod<AmiWebChartRenderingLayer> GET_CHART = new AmiAbstractMemberMethod<AmiWebChartRenderingLayer>(AmiWebChartRenderingLayer.class,
			"getChart", AmiWebChartGridPortlet.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartRenderingLayer targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getChart();
		}

		@Override
		protected String getHelp() {
			return "Returns the chart panel this layer belongs to.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebChartRenderingLayer> GET_DATAMODEL = new AmiAbstractMemberMethod<AmiWebChartRenderingLayer>(AmiWebChartRenderingLayer.class,
			"getDatamodel", AmiWebDm.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartRenderingLayer targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getDm();
		}

		@Override
		protected String getHelp() {
			return "Returns the underlying datamodel used by the layer.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebChartRenderingLayer> GET_PLOT = new AmiAbstractMemberMethod<AmiWebChartRenderingLayer>(AmiWebChartRenderingLayer.class,
			"getPlot", AmiWebChartPlotPortlet.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartRenderingLayer targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getPlot();
		}

		@Override
		protected String getHelp() {
			return "Returns the plot this layer belongs to.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebChartRenderingLayer> GET_X_AXIS = new AmiAbstractMemberMethod<AmiWebChartRenderingLayer>(AmiWebChartRenderingLayer.class,
			"getXAxis", AmiWebChartAxisPortlet.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartRenderingLayer targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getXAxis();
		}

		@Override
		protected String getHelp() {
			return "Returns the x-axis of the layer.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebChartRenderingLayer> GET_Y_AXIS = new AmiAbstractMemberMethod<AmiWebChartRenderingLayer>(AmiWebChartRenderingLayer.class,
			"getYAxis", AmiWebChartAxisPortlet.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartRenderingLayer targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getYAxis();
		}

		@Override
		protected String getHelp() {
			return "Returns the y-axis of the layer.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebChartRenderingLayer> GET_Z_POSITION = new AmiAbstractMemberMethod<AmiWebChartRenderingLayer>(AmiWebChartRenderingLayer.class,
			"getZPosition", Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartRenderingLayer targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getzPosition();
		}

		@Override
		protected String getHelp() {
			return "Returns the z-position of the layer.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebChartRenderingLayer> GET_FORMULA = new AmiAbstractMemberMethod<AmiWebChartRenderingLayer>(AmiWebChartRenderingLayer.class,
			"getFormula", String.class, false, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartRenderingLayer targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiWebChartSeries series = targetObject.getSeries();
			String name = (String) params[0];
			AmiWebChartFormula f = series.getFormula(name);
			return f == null ? null : f.getValue();
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "fieldname" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Field Name in the layer" };
		}

		@Override
		protected String getHelp() {
			return "Returns the formula for the given field.";
		}

		@Override
		public java.util.Map<String, String> getAutocompleteOptions(AmiService service) {
			return autoComplete(service, false);
		};
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static Map<String, String> autoComplete(AmiService service, boolean open) {
		Map<String, String> autoComplete = new TreeMap<String, String>();
		//TODO: getForumulasCount and getFormula should be static as well
		AmiWebChartSeries_Graph methods = new AmiWebChartSeries_Graph((AmiWebService) service, null, null, null);
		for (int n = 0; n < methods.getFormulasCount(); n++) {

			AmiWebChartFormula formula = methods.getFormulaAt(n);
			autoComplete.put('"' + formula.getName() + (open ? "\"," : "\")"), formula.getFullLabel());
		}
		return autoComplete;
	};

	private static final AmiAbstractMemberMethod<AmiWebChartRenderingLayer> SET_FORMULA = new AmiAbstractMemberMethod<AmiWebChartRenderingLayer>(AmiWebChartRenderingLayer.class,
			"setFormula", String.class, false, String.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartRenderingLayer targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiWebChartSeries series = targetObject.getSeries();
			String name = (String) params[0];
			String formula = (String) params[1];
			AmiWebChartFormula f = series.getFormula(name);
			if (f == null)
				return false;
			try {
				f.setValueOverride(formula);
				series.buildData(targetObject.getDataModelSchema(), sf);
				targetObject.getChart().flagConfigStale();
				//				targetObject.flagDataStale();
			} catch (Exception e) {
				warning(sf, "Could not  apply '" + formula + "' formula", CH.m("formula", formula, "field", name), e);
				return false;
			}
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "fieldname", "value" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Field Name in the layer. See description for a list of fields.", "value" };
		}

		@Override
		protected String getHelp() {
			return "Sets the formula for the given field. For field name, use xLbl for X Groupings, yLbl for Y Groupings, name for Group By, and where for Where clause.";
		}
		@Override
		public java.util.Map<String, String> getAutocompleteOptions(AmiService service) {
			return autoComplete(service, true);
		};
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebChartRenderingLayer> RESET_FORMULAS = new AmiAbstractMemberMethod<AmiWebChartRenderingLayer>(AmiWebChartRenderingLayer.class,
			"resetFormulas", Boolean.class, false) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartRenderingLayer targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.resetOverrides();
			return true;
		}

		@Override
		protected String getHelp() {
			return "Resets the formulas to the default (as set in the heatmap's Setting panel).";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebChartRenderingLayer> GET_STYLE2 = new AmiAbstractMemberMethod<AmiWebChartRenderingLayer>(AmiWebChartRenderingLayer.class,
			"getStyle", String.class, false, String.class, Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartRenderingLayer targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];
			Number position = (Number) params[1];
			if (SH.isnt(name))
				return null;
			if (position == null)
				return null;
			short code = AmiWebStyleConsts.GET(name);
			if (code == AmiWebStyleConsts.MISSING_CODE)
				return null;
			AmiWebChartGridPortlet chart = targetObject.getChart();
			Object r = chart.getStylePeer().resolveValue(chart.getStyleType(), code);
			if (r instanceof List)
				r = CH.getAtMod((List) r, position.intValue());
			return AmiUtils.s(r);
		}
		@Override
		protected String getHelp() {
			return "Returns the value associated with the supplied style key in this panel's style as a string. Returns Null if key not found.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "styleKey", "index" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "the key associated with this portlets style", "If the style is a list, which element in the list to return" };
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebChartRenderingLayer> GET_STYLE = new AmiAbstractMemberMethod<AmiWebChartRenderingLayer>(AmiWebChartRenderingLayer.class,
			"getStyle", String.class, false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartRenderingLayer targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];
			if (SH.isnt(name))
				return null;
			short code = AmiWebStyleConsts.GET(name);
			if (code == AmiWebStyleConsts.MISSING_CODE)
				return null;
			AmiWebChartGridPortlet chart = targetObject.getChart();
			Object r = chart.getStylePeer().resolveValue(targetObject.getStyleType(), code);
			if (r instanceof List)
				r = CH.getAtMod((List) r, 0);
			return AmiUtils.s(r);
		}
		@Override
		protected String getHelp() {
			return "Returns the value for a particular style given the style code, which can be obtained by clicking on the style name. Returns Null if key not found.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "styleKey" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "the key associated with this portlet's style" };
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebChartRenderingLayer> GET_SELECTED = new AmiAbstractMemberMethod<AmiWebChartRenderingLayer>(AmiWebChartRenderingLayer.class,
			"getSelected", Table.class, false) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartRenderingLayer targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSelectedRows();
		}

		@Override
		protected String getHelp() {
			return "get the rows from underlying data that are represented by selected items in this layer";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};

	public static final AmiWebScriptMemberMethods_ChartPanelLayer INSTANCE = new AmiWebScriptMemberMethods_ChartPanelLayer();
}
